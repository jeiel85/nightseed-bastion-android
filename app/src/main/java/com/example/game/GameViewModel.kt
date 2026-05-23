package com.example.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

enum class GameScreen {
    MAIN_MENU,
    DAY_BUILD,
    DUSK_OMEN,
    NIGHT_BATTLE,
    DAWN_REWARDS,
    VICTORY_SCREEN,
    LOSS_SCREEN
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val db = GameDatabase.getDatabase(application)
    private val repository = GameRepository(db.gameDao())

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val buildingsMapType = Types.newParameterizedType(
        Map::class.java,
        String::class.java,
        BuildingSaveModel::class.java
    )
    private val buildingsAdapter = moshi.adapter<Map<String, BuildingSaveModel>>(buildingsMapType)
    private val stringListAdapter = moshi.adapter<List<String>>(Types.newParameterizedType(List::class.java, String::class.java))

    // UI States
    private val _currentScreen = MutableStateFlow(GameScreen.MAIN_MENU)
    val currentScreen: StateFlow<GameScreen> = _currentScreen.asStateFlow()

    private val _accountState = MutableStateFlow(AccountStateEntity())
    val accountState: StateFlow<AccountStateEntity> = _accountState.asStateFlow()

    // Run-Specific States
    private val _currentNight = MutableStateFlow(1)
    val currentNight: StateFlow<Int> = _currentNight.asStateFlow()

    private val _coreHp = MutableStateFlow(100)
    val coreHp: StateFlow<Int> = _coreHp.asStateFlow()

    private val _coreMaxHp = MutableStateFlow(100)
    val coreMaxHp: StateFlow<Int> = _coreMaxHp.asStateFlow()

    private val _moonshards = MutableStateFlow(60)
    val moonshards: StateFlow<Int> = _moonshards.asStateFlow()

    private val _ember = MutableStateFlow(20)
    val ember: StateFlow<Int> = _ember.asStateFlow()

    private val _hope = MutableStateFlow(50)
    val hope: StateFlow<Int> = _hope.asStateFlow()

    private val _heroAtkBonus = MutableStateFlow(1f)
    val heroAtkBonus: StateFlow<Float> = _heroAtkBonus.asStateFlow()

    private val _towerRangeBonus = MutableStateFlow(0f)
    val towerRangeBonus: StateFlow<Float> = _towerRangeBonus.asStateFlow()

    // Key format: "LANE_POS" e.g. "LEFT_OUTER"
    private val _placedBuildings = MutableStateFlow<Map<String, BuildingInstance>>(emptyMap())
    val placedBuildings: StateFlow<Map<String, BuildingInstance>> = _placedBuildings.asStateFlow()

    private val _activeBargains = MutableStateFlow<List<DuskBargain>>(emptyList())
    val activeBargains: StateFlow<List<DuskBargain>> = _activeBargains.asStateFlow()

    // Dusk Specific choices
    private val _duskBargainOptions = MutableStateFlow<List<DuskBargain>>(emptyList())
    val duskBargainOptions: StateFlow<List<DuskBargain>> = _duskBargainOptions.asStateFlow()

    private val _spentBargainThisDusk = MutableStateFlow(false)
    val spentBargainThisDusk: StateFlow<Boolean> = _spentBargainThisDusk.asStateFlow()

    // Dawn reward options
    private val _dawnRewardOptions = MutableStateFlow<List<DawnRewardOption>>(emptyList())
    val dawnRewardOptions: StateFlow<List<DawnRewardOption>> = _dawnRewardOptions.asStateFlow()

    // Battle Live States (not saved, simulated only during NIGHT_BATTLE)
    private val _battleEnemies = MutableStateFlow<List<EnemyInstance>>(emptyList())
    val battleEnemies: StateFlow<List<EnemyInstance>> = _battleEnemies.asStateFlow()

    private val _battleProjectiles = MutableStateFlow<List<CombatProjectile>>(emptyList())
    val battleProjectiles: StateFlow<List<CombatProjectile>> = _battleProjectiles.asStateFlow()

    private val _battleHeroX = MutableStateFlow(350f)
    val battleHeroX: StateFlow<Float> = _battleHeroX.asStateFlow()

    private val _battleHeroY = MutableStateFlow(750f)
    val battleHeroY: StateFlow<Float> = _battleHeroY.asStateFlow()

    private val _battleHeroHp = MutableStateFlow(100f)
    val battleHeroHp: StateFlow<Float> = _battleHeroHp.asStateFlow()

    private val _heroDashing = MutableStateFlow(false)
    val heroDashing: StateFlow<Boolean> = _heroDashing.asStateFlow()

    private val _lastLanternTriggered = MutableStateFlow(false)
    val lastLanternTriggered: StateFlow<Boolean> = _lastLanternTriggered.asStateFlow()

    private val _lastLanternActive = MutableStateFlow(false)
    val lastLanternActive: StateFlow<Boolean> = _lastLanternActive.asStateFlow()

    private val _wardensMarkTimeLeft = MutableStateFlow(0) // Ticks left for Warden's Mark boost
    val wardensMarkTimeLeft: StateFlow<Int> = _wardensMarkTimeLeft.asStateFlow()

    private val _floatingTexts = MutableStateFlow<List<FloatingText>>(emptyList())
    val floatingTexts: StateFlow<List<FloatingText>> = _floatingTexts.asStateFlow()

    private val _waveSpawnProgressPercent = MutableStateFlow(0f)
    val waveSpawnProgressPercent: StateFlow<Float> = _waveSpawnProgressPercent.asStateFlow()

    private val _nightVictory = MutableStateFlow(false)
    private val _nightLoss = MutableStateFlow(false)

    // Log & summary states
    private val _runsCleared = MutableStateFlow(0)
    private val _runsLosses = MutableStateFlow(0)
    private val _runStatsSummary = MutableStateFlow("")
    val runStatsSummary: StateFlow<String> = _runStatsSummary.asStateFlow()

    private var gameLoopJob: Job? = null
    private var totalEnemiesToSpawn = 0
    private var enemiesSpawnedSoFar = 0
    private var spawnTimerTicks = 0

    init {
        viewModelScope.launch {
            // First time load or seed DB
            val account = repository.getAccountState()
            _accountState.value = account
            _runsCleared.value = account.totalRunsCleared
            _runsLosses.value = account.totalLosses

            // Resume run if active
            val activeRun = repository.getActiveRun()
            if (activeRun != null && activeRun.isRunActive) {
                resumeRun(activeRun)
            }
        }
    }

    // -------------------------------------------------------------
    // RUN RESUME & RESET
    // -------------------------------------------------------------

    private fun resumeRun(run: ActiveRunEntity) {
        _currentNight.value = run.currentNight
        _coreMaxHp.value = run.coreMhp
        _coreHp.value = run.coreHp
        _moonshards.value = run.moonshards
        _ember.value = run.ember
        _hope.value = run.hope
        _heroAtkBonus.value = run.heroAtkBonus
        _towerRangeBonus.value = run.towerRangeBonus

        // Decode Buildings
        val buildingsMap = mutableMapOf<String, BuildingInstance>()
        try {
            if (run.placedBuildingsJson.isNotEmpty()) {
                val saved = buildingsAdapter.fromJson(run.placedBuildingsJson)
                saved?.forEach { (key, saveModel) ->
                    val type = BuildingType.fromId(saveModel.typeId)
                    val parts = key.split("_")
                    val lane = PlayLane.valueOf(parts[0])
                    val pos = SlotPosition.valueOf(parts[1])
                    buildingsMap[key] = BuildingInstance(
                        id = saveModel.id,
                        type = type,
                        lane = lane,
                        position = pos,
                        level = saveModel.level,
                        maxHp = saveModel.maxHp,
                        currentHp = saveModel.currentHp
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        _placedBuildings.value = buildingsMap

        // Decode Bargains
        val bargainsList = mutableListOf<DuskBargain>()
        try {
            if (run.activeBargainsJson.isNotEmpty()) {
                val listIds = stringListAdapter.fromJson(run.activeBargainsJson)
                listIds?.forEach { id ->
                    bargainsList.add(getBargainById(id))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        _activeBargains.value = bargainsList

        _currentScreen.value = GameScreen.DAY_BUILD
    }

    fun startNewRun() {
        viewModelScope.launch {
            _currentNight.value = 1
            _coreMaxHp.value = 100 + if (hasUpgrade("core_mhp")) 20 else 0
            _coreHp.value = _coreMaxHp.value
            _moonshards.value = 60 + if (hasUpgrade("starting_shards")) 20 else 0
            _ember.value = 20
            _hope.value = 50
            _heroAtkBonus.value = 1f + if (hasUpgrade("hero_atk")) 0.25f else 0f
            _towerRangeBonus.value = 0f

            // Start placed buildings (Only Core in an implicit slot or keep slots clean for manual build)
            _placedBuildings.value = emptyMap()
            _activeBargains.value = emptyList()

            saveCurrentRunState(active = true)
            _currentScreen.value = GameScreen.DAY_BUILD
        }
    }

    private suspend fun saveCurrentRunState(active: Boolean) {
        val saveMap = _placedBuildings.value.mapValues { (_, b) ->
            BuildingSaveModel(b.id, b.type.id, b.level, b.maxHp, b.currentHp)
        }
        val buildingsJson = buildingsAdapter.toJson(saveMap) ?: ""
        val bargainsJson = stringListAdapter.toJson(_activeBargains.value.map { it.id }) ?: ""

        val entity = ActiveRunEntity(
            isRunActive = active,
            currentNight = _currentNight.value,
            coreMhp = _coreMaxHp.value,
            coreHp = _coreHp.value,
            moonshards = _moonshards.value,
            ember = _ember.value,
            hope = _hope.value,
            heroAtkBonus = _heroAtkBonus.value,
            towerRangeBonus = _towerRangeBonus.value,
            placedBuildingsJson = buildingsJson,
            activeBargainsJson = bargainsJson
        )
        repository.saveActiveRun(entity)
    }

    // -------------------------------------------------------------
    // DAY BUILD WORKERS
    // -------------------------------------------------------------

    fun buildBuilding(lane: PlayLane, pos: SlotPosition, type: BuildingType): Boolean {
        val key = "${lane.name}_${pos.name}"
        if (_placedBuildings.value.containsKey(key)) return false

        val cost = type.cost
        if (_moonshards.value >= cost) {
            _moonshards.value -= cost
            val newInstance = BuildingInstance(
                id = UUID.randomUUID().toString(),
                type = type,
                lane = lane,
                position = pos
            )
            val updated = _placedBuildings.value.toMutableMap()
            updated[key] = newInstance
            _placedBuildings.value = updated

            viewModelScope.launch { saveCurrentRunState(active = true) }
            return true
        }
        return false
    }

    fun upgradeBuilding(lane: PlayLane, pos: SlotPosition): Boolean {
        val key = "${lane.name}_${pos.name}"
        val b = _placedBuildings.value[key] ?: return false
        if (b.level >= b.type.maxLevel) return false

        val cost = b.getUpgradeCost()
        if (_moonshards.value >= cost) {
            _moonshards.value -= cost
            b.level += 1
            b.maxHp = b.getUpgradeHp()
            b.currentHp = b.maxHp

            val updated = _placedBuildings.value.toMutableMap()
            updated[key] = b
            _placedBuildings.value = updated

            viewModelScope.launch { saveCurrentRunState(active = true) }
            return true
        }
        return false
    }

    fun repairBuilding(lane: PlayLane, pos: SlotPosition): Boolean {
        val key = "${lane.name}_${pos.name}"
        val b = _placedBuildings.value[key] ?: return false
        if (b.currentHp >= b.maxHp) return false

        val repairCost = 10
        if (_moonshards.value >= repairCost) {
            _moonshards.value -= repairCost
            b.currentHp = b.maxHp

            val updated = _placedBuildings.value.toMutableMap()
            updated[key] = b
            _placedBuildings.value = updated

            viewModelScope.launch { saveCurrentRunState(active = true) }
            return true
        }
        return false
    }

    fun demolishBuilding(lane: PlayLane, pos: SlotPosition) {
        val key = "${lane.name}_${pos.name}"
        val b = _placedBuildings.value[key] ?: return
        val refund = b.type.cost / 2
        _moonshards.value += refund

        val updated = _placedBuildings.value.toMutableMap()
        updated.remove(key)
        _placedBuildings.value = updated
        viewModelScope.launch { saveCurrentRunState(active = true) }
    }

    fun repairCoreProgressive(): Boolean {
        if (_coreHp.value >= _coreMaxHp.value) return false
        val cost = 25
        if (_moonshards.value >= cost) {
            _moonshards.value -= cost
            _coreHp.value = minOf(_coreMaxHp.value, _coreHp.value + 40)
            viewModelScope.launch { saveCurrentRunState(active = true) }
            return true
        }
        return false
    }

    // -------------------------------------------------------------
    // DUSK OMEN & BARGAINS
    // -------------------------------------------------------------

    fun triggerBeginDusk() {
        _spentBargainThisDusk.value = false
        // Generate 2 random bargains from our 4 available styles
        val list = mutableListOf(
            DuskBargain("blood_mortar", "Blood Mortar", "Increases tower damages significantly tonight.", "Towers deal +35% damage", "Core loses 15 max HP permanently"),
            DuskBargain("hungry_walls", "Hungry Walls", "Defensive walls slowly repair themselves.", "Walls regenerate 4 HP/sec", "Enemies spawn +25% faster"),
            DuskBargain("ashen_tithe", "Ashen Tithe", "Steal from the future to build your defenses today.", "Gain 50 Moonshards now", "Loses 15 Hope immediately"),
            DuskBargain("lantern_oath", "Lantern Oath", "Ignite the lanterns for quicker skill charges.", "Skill cool-downs reduced by -30%", "Elite wave summons immediately")
        )
        list.shuffle()
        _duskBargainOptions.value = list.take(2)
        _currentScreen.value = GameScreen.DUSK_OMEN
    }

    fun acceptBargain(bargain: DuskBargain) {
        if (_spentBargainThisDusk.value) return
        _spentBargainThisDusk.value = true

        val list = _activeBargains.value.toMutableList()
        list.add(bargain)
        _activeBargains.value = list

        // Apply immediate costs or benefits
        when (bargain.id) {
            "blood_mortar" -> {
                _coreMaxHp.value = maxOf(20, _coreMaxHp.value - 15)
                if (_coreHp.value > _coreMaxHp.value) {
                    _coreHp.value = _coreMaxHp.value
                }
            }
            "ashen_tithe" -> {
                _moonshards.value += 50
                _hope.value = maxOf(0, _hope.value - 15)
            }
        }
        viewModelScope.launch { saveCurrentRunState(active = true) }
    }

    // -------------------------------------------------------------
    // BATTLE FIELD SIMULATOR LOOP
    // -------------------------------------------------------------

    fun launchNightBattle() {
        _currentScreen.value = GameScreen.NIGHT_BATTLE
        _nightVictory.value = false
        _nightLoss.value = false
        _floatingTexts.value = emptyList()
        _battleProjectiles.value = emptyList()

        // Setup Hero position & HP
        _battleHeroX.value = 350f
        _battleHeroY.value = 750f
        _battleHeroHp.value = 100f
        _lastLanternTriggered.value = false
        _lastLanternActive.value = false
        _wardensMarkTimeLeft.value = 0

        // Reset defensive buildings HP on battlefield
        _placedBuildings.value.forEach { (_, b) ->
            b.currentHp = b.maxHp
        }

        // Configure Waves depending on currentNight index
        enemiesSpawnedSoFar = 0
        spawnTimerTicks = 0
        totalEnemiesToSpawn = 10 + (_currentNight.value * 6)
        _waveSpawnProgressPercent.value = 0f
        _battleEnemies.value = emptyList()

        // Start Ticker
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            while (_currentScreen.value == GameScreen.NIGHT_BATTLE) {
                delay(30) // ~33 ticks per second
                runBattleTick()
            }
        }
    }

    private fun runBattleTick() {
        val currentEnemies = _battleEnemies.value.toMutableList()
        val currentProj = _battleProjectiles.value.toMutableList()
        val texts = _floatingTexts.value.toMutableList()

        // 1. Spawning Enemies periodically
        spawnTimerTicks++
        if (enemiesSpawnedSoFar < totalEnemiesToSpawn && spawnTimerTicks >= getSpawnDelayTicks()) {
            spawnTimerTicks = 0
            val lane = PlayLane.entries.random()
            val type = decideEnemyTypeToSpawn()
            val newEnemy = EnemyInstance(
                id = UUID.randomUUID().toString(),
                type = type,
                lane = lane,
                progress = 0f
            )
            currentEnemies.add(newEnemy)
            enemiesSpawnedSoFar++
            _waveSpawnProgressPercent.value = (enemiesSpawnedSoFar.toFloat() / totalEnemiesToSpawn.toFloat())
        }

        // 2. Ember brazier generator nearby
        val heroNearBrazier = checkForNearBrazier(_battleHeroX.value, _battleHeroY.value)
        if (heroNearBrazier) {
            if (Random.nextFloat() < 0.12f) { // Ember regen nearby
                _ember.value = minOf(100, _ember.value + 1)
            }
        }

        // Hungry walls bargain regeneration
        val hasHungryWalls = _activeBargains.value.any { it.id == "hungry_walls" }
        if (hasHungryWalls) {
            _placedBuildings.value.forEach { (_, b) ->
                if (b.type == BuildingType.THORN_WALL && b.currentHp < b.maxHp && Random.nextFloat() < 0.05f) {
                    b.currentHp = minOf(b.maxHp, b.currentHp + 3)
                }
            }
        }

        // Warden's mark duration decay
        if (_wardensMarkTimeLeft.value > 0) {
            _wardensMarkTimeLeft.value -= 1
        }

        // 3. Move/Update Enemies
        val enemiesSnapshot = _battleEnemies.value
        val toRemoveEnemies = mutableListOf<EnemyInstance>()

        currentEnemies.forEach { enemy ->
            if (enemy.isStunned) {
                enemy.stunnedTimer--
                if (enemy.stunnedTimer <= 0) enemy.isStunned = false
                return@forEach
            }

            if (enemy.isSlowed) {
                enemy.slowedTimer--
                if (enemy.slowedTimer <= 0) {
                    enemy.isSlowed = false
                    enemy.speed = enemy.type.baseSpeed
                }
            }

            // Check walls which might block this enemy
            val isBlocked = checkWallBlocking(enemy)
            if (isBlocked != null) {
                // Attack wall instead of moving
                enemy.chargeTicks++
                if (enemy.chargeTicks >= 40) { // attack every ~1.2s
                    enemy.chargeTicks = 0
                    isBlocked.currentHp -= enemy.type.baseDamage
                    texts.add(FloatingText("-${enemy.type.baseDamage}", getLaneX(enemy.lane), 700f - getPosOffset(isBlocked.position), 0xFFFF5555))
                    if (isBlocked.currentHp <= 0) {
                        texts.add(FloatingText("Wall Destroyed!", getLaneX(enemy.lane), 700f - getPosOffset(isBlocked.position), 0xFFFFCC00))
                        // demolished
                        val key = "${isBlocked.lane.name}_${isBlocked.position.name}"
                        val updatedMap = _placedBuildings.value.toMutableMap()
                        updatedMap.remove(key)
                        _placedBuildings.value = updatedMap
                    }
                }
            } else {
                // Standard march down
                val speedMod = if (_activeBargains.value.any { it.id == "hungry_walls" }) 1.25f else 1.0f
                val slowFactor = if (enemy.isSlowed) 0.5f else 1.0f
                enemy.progress += enemy.speed * speedMod * slowFactor

                // Trap triggering at mid (progress ~ 40)
                if (enemy.progress >= 40f && enemy.progress <= 43f) {
                    val trapKey = "${enemy.lane.name}_MID"
                    val trap = _placedBuildings.value[trapKey]
                    if (trap != null && trap.type == BuildingType.GRAVE_SNARE) {
                        // Explode trap!
                        texts.add(FloatingText("SNARE TRAP TRIGGERED!", getLaneX(enemy.lane), 450f, 0xFF00FFCC))
                        // Apply damage & slow to all enemies in this lane near that progress
                        enemiesSnapshot.forEach { other ->
                            if (other.lane == enemy.lane && other.progress in 30f..55f) {
                                other.currentHp -= 40
                                if (other.type != EnemyType.BONE_RUNNER) { // Bone Runner ignores slow
                                    other.isSlowed = true
                                    other.slowedTimer = 120 // 4 seconds
                                }
                            }
                        }
                        // Demolish trap slot
                        val updatedMap = _placedBuildings.value.toMutableMap()
                        updatedMap.remove(trapKey)
                        _placedBuildings.value = updatedMap
                    }
                }

                // Hit core when progress >= 100
                if (enemy.progress >= 100f) {
                    _coreHp.value = maxOf(0, _coreHp.value - enemy.type.baseDamage)
                    texts.add(FloatingText("CORE -${enemy.type.baseDamage}!", 350f, 850f, 0xFFFF0055))
                    toRemoveEnemies.add(enemy)

                    // Check last lantern passive
                    if (_coreHp.value < _coreMaxHp.value * 0.3f && !_lastLanternTriggered.value) {
                        _lastLanternTriggered.value = true
                        _lastLanternActive.value = true
                        texts.add(FloatingText("PASSSIVE: LAST LANTERN ACTIVE!", 350f, 750f, 0xFFFFCC00))
                        // Add temporary speed & power and automatically deal 50 damage to nearby
                        viewModelScope.launch {
                            delay(10000)
                            _lastLanternActive.value = false
                        }
                    }

                    if (_coreHp.value <= 0) {
                        triggerLoss("The Bastion Core was breached and corrupted by the Nightseed.")
                        return
                    }
                }
            }
        }
        currentEnemies.removeAll(toRemoveEnemies)

        // Clean dead ones & drop rewards
        val deadEnemies = currentEnemies.filter { it.currentHp <= 0 }
        deadEnemies.forEach { enemy ->
            val emberGot = enemy.type.emberReward
            _ember.value = minOf(100, _ember.value + emberGot)
            _moonshards.value += Random.nextInt(1, 4) // Drop loot
            texts.add(FloatingText("+ $emberGot Ember", getLaneX(enemy.lane) + Random.nextInt(-30, 30), 800f * (enemy.progress / 100f), 0xFFFFAA00))
        }
        currentEnemies.removeAll(deadEnemies)

        // 4. Update Towers Attacks
        _placedBuildings.value.forEach { (_, b) ->
            if (b.type == BuildingType.WATCHTOWER && b.currentHp > 0) {
                b.level // Upgrade modifier
                // Find nearest enemy in the tower's lane
                val target = currentEnemies.filter { it.lane == b.lane && it.currentHp > 0 }
                    .minByOrNull { Math.abs(it.progress - getPosProgress(b.position)) }

                if (target != null) {
                    // Range check: difference in progress shouldn't be too vast
                    val progressDiff = Math.abs(target.progress - getPosProgress(b.position))
                    if (progressDiff < (35f + _towerRangeBonus.value)) {
                        // Projectile spawn chance (rate limiter based on level)
                        if (Random.nextFloat() < (0.04f + b.level * 0.012f)) {
                            val projX = getLaneX(b.lane)
                            val projY = 800f * (getPosProgress(b.position) / 100f)
                            currentProj.add(
                                CombatProjectile(
                                    id = UUID.randomUUID().toString(),
                                    sourceLane = b.lane,
                                    currentX = projX,
                                    currentY = projY,
                                    targetEnemyId = target.id,
                                    damage = 12 + (b.level * 4)
                                )
                            )
                        }
                    }
                }
            }
        }

        // 5. Update projectiles
        val toRemoveProj = mutableListOf<CombatProjectile>()
        currentProj.forEach { p ->
            val tar = currentEnemies.find { it.id == p.targetEnemyId }
            if (tar == null) {
                toRemoveProj.add(p)
                return@forEach
            }

            val targetX = getLaneX(tar.lane)
            val targetY = 800f * (tar.progress / 100f)

            val dx = targetX - p.currentX
            val dy = targetY - p.currentY
            val dist = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()

            if (dist < p.speed) {
                // Collision!
                var finalDmg = p.damage
                // Warden's Mark 3x damage amplifier
                if (_wardensMarkTimeLeft.value > 0 && tar.type.isBoss) {
                    finalDmg *= 3
                }
                // Blood mortar bargain multiplier
                if (_activeBargains.value.any { it.id == "blood_mortar" }) {
                    finalDmg = (finalDmg * 1.35f).toInt()
                }

                tar.currentHp -= finalDmg
                texts.add(FloatingText("-$finalDmg", targetX, targetY, 0xFFFFAA33))
                toRemoveProj.add(p)
            } else {
                p.currentX += (dx / dist) * p.speed
                p.currentY += (dy / dist) * p.speed
            }
        }
        currentProj.removeAll(toRemoveProj)

        // 6. Hero Auto-Attack
        // Whichever lane the Hero is in, attack closest enemy in range (range: within 120 pixels)
        val activeHeroLane = getLaneFromX(_battleHeroX.value)
        val nearestToHero = currentEnemies.filter { it.lane == activeHeroLane && it.currentHp > 0 }
            .minByOrNull { Math.abs(800f * (it.progress / 100f) - _battleHeroY.value) }

        if (nearestToHero != null) {
            val distY = Math.abs(800f * (nearestToHero.progress / 100f) - _battleHeroY.value)
            if (distY < 120f) {
                if (Random.nextFloat() < 0.08f) { // attack frequency
                    val damageMod = if (_lastLanternActive.value) 2.0f else 1.00f
                    val rawDmg = (12f * _heroAtkBonus.value * damageMod).toInt()
                    nearestToHero.currentHp -= rawDmg
                    texts.add(FloatingText("⚔️ -$rawDmg", getLaneX(nearestToHero.lane), 800f * (nearestToHero.progress / 100f), 0xFFE0F0FF))
                }
            }
        }

        // 7. Decaying damage text popup durations
        texts.forEach { it.ticksRemaining-- }
        texts.removeAll { it.ticksRemaining <= 0 }

        // Save back
        _battleEnemies.value = currentEnemies
        _battleProjectiles.value = currentProj
        _floatingTexts.value = texts

        // 8. Victory conditions
        if (enemiesSpawnedSoFar >= totalEnemiesToSpawn && currentEnemies.isEmpty()) {
            triggerVictory()
        }
    }

    // -------------------------------------------------------------
    // INTERACTIVE HERO SKILLSERS & CONTROLS
    // -------------------------------------------------------------

    fun handleHeroTap(targetX: Float, targetY: Float) {
        viewModelScope.launch {
            // Smoothly path to target coordinates or jump
            _battleHeroX.value = targetX.coerceIn(100f, 600f)
            _battleHeroY.value = targetY.coerceIn(550f, 850f)
        }
    }

    fun useSkillMooncut() {
        if (_ember.value >= 15) {
            _ember.value -= 15
            _heroDashing.value = true
            val activeLane = getLaneFromX(_battleHeroX.value)

            // Deal severe dash slash damage (50) and stun for 2 seconds (60 ticks)
            val current = _battleEnemies.value.toMutableList()
            val texts = _floatingTexts.value.toMutableList()

            texts.add(FloatingText("🌙 MOONCUT SLASH!", _battleHeroX.value, _battleHeroY.value - 40f, 0xFF00FFCC))

            current.forEach { enemy ->
                if (enemy.lane == activeLane && Math.abs(800f * (enemy.progress / 100f) - _battleHeroY.value) < 320f) {
                    val damageVal = (50f * _heroAtkBonus.value).toInt()
                    enemy.currentHp -= damageVal
                    enemy.isStunned = true
                    enemy.stunnedTimer = 75 // 2.5 seconds
                    texts.add(FloatingText("-$damageVal STUN!", getLaneX(enemy.lane), 800f * (enemy.progress / 100f), 0xFF55FFFF))
                }
            }

            _battleEnemies.value = current
            _floatingTexts.value = texts

            viewModelScope.launch {
                delay(120) // quick dash aesthetic duration
                _heroDashing.value = false
            }
        }
    }

    fun useSkillWardensMark() {
        if (_ember.value >= 20) {
            _ember.value -= 20
            _wardensMarkTimeLeft.value = 240 // 8 seconds of amplified towers on elites/bosses
            val texts = _floatingTexts.value.toMutableList()
            texts.add(FloatingText("🎯 MARK TARGETS!", _battleHeroX.value, _battleHeroY.value - 40f, 0xFFE040FB))
            _floatingTexts.value = texts
        }
    }

    // -------------------------------------------------------------
    // WIN / LOSS RESOLUTION
    // -------------------------------------------------------------

    private fun triggerVictory() {
        gameLoopJob?.cancel()
        _nightVictory.value = true
        _nightLoss.value = false

        viewModelScope.launch {
            if (_currentNight.value >= 7) {
                // Total Run victory
                _runsCleared.value += 1
                val updatedAccount = _accountState.value.copy(
                    seedAsh = _accountState.value.seedAsh + 80,
                    totalRunsCleared = _runsCleared.value
                )
                repository.saveAccountState(updatedAccount)
                _accountState.value = updatedAccount
                _runStatsSummary.value = "Magnificent! You withstood all 7 nights at the Moonwell Bastion, neutralizing the corruption of the Nightseed and securing salvation. You earned 80 Seed Ash!"
                repository.clearActiveRun()
                _currentScreen.value = GameScreen.VICTORY_SCREEN
            } else {
                // Normal Dusk completed, proceed to Reward Selection screen
                saveCurrentRunState(active = true)
                generateDawnRewards()
                _currentScreen.value = GameScreen.DAWN_REWARDS
            }
        }
    }

    private fun triggerLoss(reason: String) {
        gameLoopJob?.cancel()
        _nightVictory.value = false
        _nightLoss.value = true

        viewModelScope.launch {
            _runsLosses.value += 1
            val partialSeedAshReward = _currentNight.value * 6 // 6 Seed Ash per survived Night
            val updatedAccount = _accountState.value.copy(
                seedAsh = _accountState.value.seedAsh + partialSeedAshReward,
                totalLosses = _runsLosses.value
            )
            repository.saveAccountState(updatedAccount)
            _accountState.value = updatedAccount
            _runStatsSummary.value = "Defeat. $reason\nSurvived until Night ${_currentNight.value}. Retained $partialSeedAshReward Seed Ash."
            repository.clearActiveRun()
            _currentScreen.value = GameScreen.LOSS_SCREEN
        }
    }

    // -------------------------------------------------------------
    // DAWN REWARDS WORKER
    // -------------------------------------------------------------

    private fun generateDawnRewards() {
        val list = mutableListOf(
            DawnRewardOption("shards_payout", "Replenish Resources", "Instantly secures +45 Moonshards from the wells."),
            DawnRewardOption("core_reinforce", "Emergency Retrofit", "Increases Bastion Core MAX HP by +20 and repairs it completely."),
            DawnRewardOption("hero_training", "Astral Empowerment", "Increases Hero attack damage multiplier by +20% permanently this run."),
            DawnRewardOption("tower_extension", "Lighthouse Extension", "Increases defensive Tower range by +15 pixels.")
        )
        list.shuffle()
        _dawnRewardOptions.value = list.take(3)
    }

    fun selectDawnReward(option: DawnRewardOption) {
        viewModelScope.launch {
            when (option.id) {
                "shards_payout" -> _moonshards.value += 45
                "core_reinforce" -> {
                    _coreMaxHp.value += 20
                    _coreHp.value = _coreMaxHp.value
                }
                "hero_training" -> {
                    _heroAtkBonus.value += 0.20f
                }
                "tower_extension" -> {
                    _towerRangeBonus.value += 15f
                }
            }

            // Generated Dawn payout + Well generations
            var totalWellsIncome = 0
            _placedBuildings.value.forEach { (_, b) ->
                if (b.type == BuildingType.MOONWELL) {
                    totalWellsIncome += 20 * b.level
                }
            }
            _moonshards.value += (20 + totalWellsIncome) // fixed Dawn bonus + well income

            _currentNight.value += 1
            saveCurrentRunState(active = true)
            _currentScreen.value = GameScreen.DAY_BUILD
        }
    }

    // -------------------------------------------------------------
    // META UPGRADES SHOP (Account level)
    // -------------------------------------------------------------

    fun buyMetaUpgrade(id: String, cost: Int): Boolean {
        val currentAsh = _accountState.value.seedAsh
        if (currentAsh >= cost) {
            val list = getUnlockedUpgrades().toMutableList()
            if (!list.contains(id)) {
                list.add(id)
                val newListJson = stringListAdapter.toJson(list) ?: "[]"
                val updatedAccount = _accountState.value.copy(
                    seedAsh = currentAsh - cost,
                    unlockedUpgradesJson = newListJson
                )
                _accountState.value = updatedAccount
                viewModelScope.launch {
                    repository.saveAccountState(updatedAccount)
                }
                return true
            }
        }
        return false
    }

    fun hasUpgrade(id: String): Boolean {
        return getUnlockedUpgrades().contains(id)
    }

    private fun getUnlockedUpgrades(): List<String> {
        return try {
            stringListAdapter.fromJson(_accountState.value.unlockedUpgradesJson) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun exitToMainMenu() {
        gameLoopJob?.cancel()
        _currentScreen.value = GameScreen.MAIN_MENU
    }

    // -------------------------------------------------------------
    // HELPERS FOR MAP CALCS
    // -------------------------------------------------------------

    fun getLaneX(lane: PlayLane): Float {
        return when (lane) {
            PlayLane.LEFT -> 170f
            PlayLane.CENTER -> 350f
            PlayLane.RIGHT -> 530f
        }
    }

    private fun getLaneFromX(x: Float): PlayLane {
        return if (x < 260f) PlayLane.LEFT
        else if (x < 440f) PlayLane.CENTER
        else PlayLane.RIGHT
    }

    fun getPosProgress(pos: SlotPosition): Float {
        return when (pos) {
            SlotPosition.OUTER -> 30f
            SlotPosition.MID -> 50f
            SlotPosition.INNER -> 70f
        }
    }

    private fun getPosOffset(pos: SlotPosition): Float {
        return when (pos) {
            SlotPosition.OUTER -> 320f
            SlotPosition.MID -> 160f
            SlotPosition.INNER -> 0f
        }
    }

    private fun getSpawnDelayTicks(): Int {
        val isFast = _activeBargains.value.any { it.id == "lantern_oath" }
        val baseDelay = 120 - (_currentNight.value * 12)
        return maxOf(50, if (isFast) (baseDelay * 0.7f).toInt() else baseDelay)
    }

    private fun decideEnemyTypeToSpawn(): EnemyType {
        val night = _currentNight.value
        val r = Random.nextFloat()

        // On Night 7 near the end: spawn Boss
        if (night >= 7 && enemiesSpawnedSoFar == 0) {
            return EnemyType.NIGHTSEED_HERALD
        }

        return if (night == 1) {
            EnemyType.HUSKLING
        } else if (night == 2) {
            if (r < 0.70f) EnemyType.HUSKLING else EnemyType.BONE_RUNNER
        } else if (night == 3) {
            if (r < 0.50f) EnemyType.HUSKLING
            else if (r < 0.85f) EnemyType.BONE_RUNNER
            else EnemyType.LANTERN_EATER
        } else if (night == 4) {
            if (r < 0.40f) EnemyType.HUSKLING
            else if (r < 0.70f) EnemyType.BONE_RUNNER
            else if (r < 0.90f) EnemyType.LANTERN_EATER
            else EnemyType.GRAVE_BRUTE
        } else if (night == 5) {
            if (r < 0.35f) EnemyType.HUSKLING
            else if (r < 0.60f) EnemyType.BONE_RUNNER
            else if (r < 0.80f) EnemyType.LANTERN_EATER
            else if (r < 0.92f) EnemyType.GRAVE_BRUTE
            else EnemyType.HEX_ARCHER
        } else {
            // Night 6 & 7 swarms
            if (r < 0.25f) EnemyType.HUSKLING
            else if (r < 0.50f) EnemyType.BONE_RUNNER
            else if (r < 0.70f) EnemyType.LANTERN_EATER
            else if (r < 0.85f) EnemyType.GRAVE_BRUTE
            else EnemyType.HEX_ARCHER
        }
    }

    private fun checkWallBlocking(enemy: EnemyInstance): BuildingInstance? {
        val keysToCheck = mapOf(
            "OUTER" to SlotPosition.OUTER,
            "MID" to SlotPosition.MID,
            "INNER" to SlotPosition.INNER
        )
        for ((keyStr, pos) in keysToCheck) {
            val wallKey = "${enemy.lane.name}_$keyStr"
            val building = _placedBuildings.value[wallKey]
            if (building != null && building.type == BuildingType.THORN_WALL && building.currentHp > 0) {
                val boundaryProgress = getPosProgress(pos)
                if (enemy.progress >= boundaryProgress && enemy.progress <= boundaryProgress + 4.5f) {
                    return building
                }
            }
        }
        return null
    }

    private fun checkForNearBrazier(x: Float, y: Float): Boolean {
        _placedBuildings.value.forEach { (_, b) ->
            if (b.type == BuildingType.EMBER_BRAZIER) {
                val bX = getLaneX(b.lane)
                val bY = 800f * (getPosProgress(b.position) / 100f)
                val dist = Math.hypot((bX - x).toDouble(), (bY - y).toDouble()).toFloat()
                if (dist < 180f) return true
            }
        }
        return false
    }

    private fun getBargainById(id: String): DuskBargain {
        return when (id) {
            "blood_mortar" -> DuskBargain("blood_mortar", "Blood Mortar", "Increases tower damages significantly tonight.", "Towers deal +35% damage", "Core loses 15 max HP permanently")
            "hungry_walls" -> DuskBargain("hungry_walls", "Hungry Walls", "Defensive walls slowly repair themselves.", "Walls regenerate 4 HP/sec", "Enemies spawn +25% faster")
            "ashen_tithe" -> DuskBargain("ashen_tithe", "Ashen Tithe", "Steal from the future to build your defenses today.", "Gain 50 Moonshards now", "Loses 15 Hope immediately")
            else -> DuskBargain("lantern_oath", "Lantern Oath", "Ignite the lanterns for quicker skill charges.", "Skill cool-downs reduced by -30%", "Elite wave summons immediately")
        }
    }

    override fun onCleared() {
        super.onCleared()
        gameLoopJob?.cancel()
    }
}

// Helper serializing model for clean Room persistence database
data class BuildingSaveModel(
    val id: String,
    val typeId: String,
    val level: Int,
    val maxHp: Int,
    val currentHp: Int
)
