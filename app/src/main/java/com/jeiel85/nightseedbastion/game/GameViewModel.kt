package com.jeiel85.nightseedbastion.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jeiel85.nightseedbastion.R
import com.jeiel85.nightseedbastion.data.*
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

    private val appContext: Application = application
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
    private val _upcomingEnemies = MutableStateFlow<List<EnemyInstance>>(emptyList())
    val upcomingEnemies: StateFlow<List<EnemyInstance>> = _upcomingEnemies.asStateFlow()

    private val _rootedBuildings = MutableStateFlow<Map<String, Int>>(emptyMap())
    val rootedBuildings: StateFlow<Map<String, Int>> = _rootedBuildings.asStateFlow()

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

    // Hero movement target (smooth lerp toward this each tick)
    private val _battleHeroTargetX = MutableStateFlow(350f)
    val battleHeroTargetX: StateFlow<Float> = _battleHeroTargetX.asStateFlow()
    private val _battleHeroTargetY = MutableStateFlow(750f)
    val battleHeroTargetY: StateFlow<Float> = _battleHeroTargetY.asStateFlow()

    // Hero vitality
    private val _battleHeroMaxHp = MutableStateFlow(100f)
    val battleHeroMaxHp: StateFlow<Float> = _battleHeroMaxHp.asStateFlow()
    private val _heroDowned = MutableStateFlow(false)
    val heroDowned: StateFlow<Boolean> = _heroDowned.asStateFlow()
    private val _heroDownedSecondsLeft = MutableStateFlow(0)
    val heroDownedSecondsLeft: StateFlow<Int> = _heroDownedSecondsLeft.asStateFlow()

    // Combat feel: monotonically increasing counter the UI watches to fire a screen flash/shake
    private val _combatFlash = MutableStateFlow(0)
    val combatFlash: StateFlow<Int> = _combatFlash.asStateFlow()

    // Transient per-tick combat timers (not persisted)
    private var heroAttackCooldown = 0
    private var heroHitCooldown = 0
    private var heroDownedTimer = 0
    private val towerCooldowns = mutableMapOf<String, Int>()

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
    private var coreDamagedThisNight = false

    private fun s(resId: Int, vararg args: Any): String =
        if (args.isEmpty()) appContext.getString(resId)
        else appContext.getString(resId, *args)

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
        // Pre-generate the upcoming wave's exact enemies for Dusk telemetry
        generateUpcomingEnemies()

        // Generate 2 random bargains from our 4 available styles
        val list = mutableListOf(
            getBargainById("blood_mortar"),
            getBargainById("hungry_walls"),
            getBargainById("ashen_tithe"),
            getBargainById("lantern_oath")
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
        coreDamagedThisNight = false

        // Setup Hero position & HP
        _battleHeroX.value = 350f
        _battleHeroY.value = 750f
        _battleHeroTargetX.value = 350f
        _battleHeroTargetY.value = 750f
        _battleHeroMaxHp.value = 100f
        _battleHeroHp.value = 100f
        _heroDowned.value = false
        _heroDownedSecondsLeft.value = 0
        heroDownedTimer = 0
        heroAttackCooldown = 0
        heroHitCooldown = 0
        towerCooldowns.clear()
        _lastLanternTriggered.value = false
        _lastLanternActive.value = false
        _wardensMarkTimeLeft.value = 0
        _rootedBuildings.value = emptyMap() // Clear live rooted tower statuses

        // Reset defensive buildings HP on battlefield
        _placedBuildings.value.forEach { (_, b) ->
            b.currentHp = b.maxHp
        }

        // Configure Waves depending on currentNight index
        enemiesSpawnedSoFar = 0
        spawnTimerTicks = 0
        if (_upcomingEnemies.value.isEmpty()) {
            generateUpcomingEnemies()
        }
        totalEnemiesToSpawn = _upcomingEnemies.value.size
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

        // Decaying active rooted buildings durations
        val activeRoots = _rootedBuildings.value.toMutableMap()
        val expiredRoots = mutableListOf<String>()
        activeRoots.forEach { (key, ticks) ->
            if (ticks <= 1) {
                expiredRoots.add(key)
            } else {
                activeRoots[key] = ticks - 1
            }
        }
        expiredRoots.forEach { activeRoots.remove(it) }
        _rootedBuildings.value = activeRoots

        // 1. Spawning Enemies periodically
        spawnTimerTicks++
        if (enemiesSpawnedSoFar < totalEnemiesToSpawn && spawnTimerTicks >= getSpawnDelayTicks()) {
            spawnTimerTicks = 0
            val upcomingList = _upcomingEnemies.value
            if (enemiesSpawnedSoFar < upcomingList.size) {
                val template = upcomingList[enemiesSpawnedSoFar]
                val newEnemy = EnemyInstance(
                    id = UUID.randomUUID().toString(),
                    type = template.type,
                    lane = template.lane,
                    progress = 0f
                )
                currentEnemies.add(newEnemy)
                enemiesSpawnedSoFar++
                _waveSpawnProgressPercent.value = (enemiesSpawnedSoFar.toFloat() / totalEnemiesToSpawn.toFloat())
            }
        }

        // Active Lantern Eater disruption
        val hasLanternEater = currentEnemies.any { it.type == EnemyType.LANTERN_EATER }
        if (hasLanternEater && Random.nextFloat() < 0.025f) { // ~once every 40 ticks, drain 1 ember
            _ember.value = maxOf(0, _ember.value - 1)
            texts.add(FloatingText(s(R.string.float_ember_drained), _battleHeroX.value + Random.nextInt(-20, 20), _battleHeroY.value - 50f, 0xFFFF3333))
        }

        // 2. Ember brazier generator nearby
        val heroNearBrazier = checkForNearBrazier(_battleHeroX.value, _battleHeroY.value)
        if (heroNearBrazier) {
            val brazierRate = if (hasLanternEater) 0.06f else 0.12f // cut in half by Lantern Eater
            if (Random.nextFloat() < brazierRate) { // Ember regen nearby
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

            // Nightseed Herald Boss warp & root ability
            if (enemy.type == EnemyType.NIGHTSEED_HERALD) {
                enemy.specialAbilityCooldown--
                if (enemy.specialAbilityCooldown <= 0) {
                    enemy.specialAbilityCooldown = 150 // every 5 seconds
                    val chance = Random.nextFloat()
                    if (chance < 0.50f) {
                        // Warp lane
                        val newLane = PlayLane.entries.filter { it != enemy.lane }.random()
                        val oldX = getLaneX(enemy.lane)
                        val oldY = 50f + 700f * (enemy.progress / 100f)
                        texts.add(FloatingText(s(R.string.float_boss_warps), oldX, oldY, 0xFFE040FB))
                        enemy.progress = (enemy.progress - 5f).coerceAtLeast(0f)
                        enemy.lane = newLane
                    } else {
                        // Root target tower
                        val activeTowers = _placedBuildings.value.keys.filter { key ->
                            val b = _placedBuildings.value[key]
                            b != null && b.currentHp > 0 && (b.type == BuildingType.WATCHTOWER || b.type == BuildingType.MOONWELL)
                        }
                        if (activeTowers.isNotEmpty()) {
                            val targetKey = activeTowers.random()
                            val parts = targetKey.split("_")
                            val lane = PlayLane.valueOf(parts[0])
                            val pos = SlotPosition.valueOf(parts[1])
                            val roots = _rootedBuildings.value.toMutableMap()
                            roots[targetKey] = 240 // 8s root disable
                            _rootedBuildings.value = roots
                            texts.add(FloatingText(s(R.string.float_tower_rooted), getLaneX(lane), 700f - getPosOffset(pos), 0xFFFF3333))
                        } else {
                            _coreHp.value = maxOf(0, _coreHp.value - 10)
                            coreDamagedThisNight = true
                            texts.add(FloatingText(s(R.string.float_corrupt_root_core), 350f, 850f, 0xFFFF0055))
                        }
                    }
                }
            }

            // Check walls which might block this enemy
            val isBlocked = checkWallBlocking(enemy)
            if (isBlocked != null) {
                // Attack wall instead of moving
                enemy.chargeTicks++
                if (enemy.chargeTicks >= 40) { // attack every ~1.2s
                    enemy.chargeTicks = 0
                    val multiplier = if (enemy.type == EnemyType.GRAVE_BRUTE) 2.5f else 1.0f
                    val baseDmg = (enemy.type.baseDamage * multiplier).toInt()
                    isBlocked.currentHp -= baseDmg
                    val floatTextStr = if (enemy.type == EnemyType.GRAVE_BRUTE) s(R.string.float_crush_damage, baseDmg) else "-$baseDmg"
                    texts.add(FloatingText(floatTextStr, getLaneX(enemy.lane), 700f - getPosOffset(isBlocked.position), 0xFFFF5555))
                    if (isBlocked.currentHp <= 0) {
                        texts.add(FloatingText(s(R.string.float_wall_destroyed), getLaneX(enemy.lane), 700f - getPosOffset(isBlocked.position), 0xFFFFCC00))
                        val key = "${isBlocked.lane.name}_${isBlocked.position.name}"
                        val updatedMap = _placedBuildings.value.toMutableMap()
                        updatedMap.remove(key)
                        _placedBuildings.value = updatedMap
                    }
                }
            } else {
                // Hex Archer ranged shooting check
                var isHexArcherShooting = false
                if (enemy.type == EnemyType.HEX_ARCHER) {
                    val targetInfo = checkHexArcherRangedTarget(enemy)
                    if (targetInfo != null) {
                        isHexArcherShooting = true
                        enemy.chargeTicks++
                        if (enemy.chargeTicks >= 50) { // attack every 1.5s
                            enemy.chargeTicks = 0
                            val targetPos = targetInfo.first
                            if (targetPos != null) {
                                val targetKey = "${enemy.lane.name}_${targetPos.name}"
                                val building = _placedBuildings.value[targetKey]
                                if (building != null && building.currentHp > 0) {
                                    building.currentHp -= 5
                                    texts.add(FloatingText("🏹 -5", getLaneX(enemy.lane), 700f - getPosOffset(targetPos), 0xFFFF7733))
                                    if (building.currentHp <= 0) {
                                        texts.add(FloatingText(s(R.string.float_building_destroyed), getLaneX(enemy.lane), 700f - getPosOffset(targetPos), 0xFFFFCC00))
                                        val updatedMap = _placedBuildings.value.toMutableMap()
                                        updatedMap.remove(targetKey)
                                        _placedBuildings.value = updatedMap
                                    }
                                }
                            } else {
                                _coreHp.value = maxOf(0, _coreHp.value - 5)
                                coreDamagedThisNight = true
                                texts.add(FloatingText(s(R.string.float_core_damage, 5), 350f, 850f, 0xFFFF0055))
                                if (_coreHp.value <= 0) {
                                    triggerLoss(s(R.string.loss_core_breached))
                                    return
                                }
                            }
                        }
                    }
                }

                if (!isHexArcherShooting) {
                    // Standard march down
                    val speedMod = if (_activeBargains.value.any { it.id == "hungry_walls" }) 1.25f else 1.0f
                    val slowFactor = if (enemy.isSlowed) 0.5f else 1.0f
                    enemy.progress += enemy.speed * speedMod * slowFactor

                    // Trap triggering at mid (progress ~ 40)
                    if (enemy.progress >= 40f && enemy.progress <= 43f) {
                        val trapKey = "${enemy.lane.name}_MID"
                        val trap = _placedBuildings.value[trapKey]
                        if (trap != null && trap.type == BuildingType.GRAVE_SNARE) {
                        texts.add(FloatingText(s(R.string.float_snare_triggered), getLaneX(enemy.lane), 450f, 0xFF00FFCC))
                            enemiesSnapshot.forEach { other ->
                                if (other.lane == enemy.lane && other.progress in 30f..55f) {
                                    other.currentHp -= 40
                                    if (other.type != EnemyType.BONE_RUNNER) {
                                        other.isSlowed = true
                                        other.slowedTimer = 120
                                    }
                                }
                            }
                            val updatedMap = _placedBuildings.value.toMutableMap()
                            updatedMap.remove(trapKey)
                            _placedBuildings.value = updatedMap
                        }
                    }

                    // Hit core when progress >= 100
                    if (enemy.progress >= 100f) {
                        _coreHp.value = maxOf(0, _coreHp.value - enemy.type.baseDamage)
                        coreDamagedThisNight = true
                        texts.add(FloatingText(s(R.string.float_core_damage, enemy.type.baseDamage), 350f, 850f, 0xFFFF0055))
                        toRemoveEnemies.add(enemy)

                        // Check last lantern passive
                        if (_coreHp.value < _coreMaxHp.value * 0.3f && !_lastLanternTriggered.value) {
                            _lastLanternTriggered.value = true
                            _lastLanternActive.value = true
                            texts.add(FloatingText(s(R.string.float_last_lantern), 350f, 750f, 0xFFFFCC00))
                            currentEnemies.forEach { other ->
                                other.currentHp -= 50
                                val oY = 50f + 700f * (other.progress / 100f)
                                texts.add(FloatingText(s(R.string.float_passive_damage), getLaneX(other.lane) + Random.nextInt(-10, 10), oY + Random.nextInt(-10, 10), 0xFFFFCC00))
                            }
                            viewModelScope.launch {
                                delay(10000)
                                _lastLanternActive.value = false
                            }
                        }

                        if (_coreHp.value <= 0) {
                            triggerLoss(s(R.string.loss_core_breached))
                            return
                        }
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
            _moonshards.value += Random.nextInt(1, 4)
            texts.add(FloatingText(s(R.string.float_ember_gain, emberGot), getLaneX(enemy.lane) + Random.nextInt(-30, 30), 800f * (enemy.progress / 100f), 0xFFFFAA00))
        }
        currentEnemies.removeAll(deadEnemies)

        // 4. Update Towers Attacks (deterministic cooldown per tower for steady fire cadence)
        _placedBuildings.value.forEach { (key, b) ->
            // Tick down this tower's reload regardless of targeting
            val cd = (towerCooldowns[key] ?: 0)
            if (cd > 0) towerCooldowns[key] = cd - 1

            val isRooted = _rootedBuildings.value.containsKey(key)
            if (b.type == BuildingType.WATCHTOWER && b.currentHp > 0 && !isRooted) {
                val target = currentEnemies.filter { it.lane == b.lane && it.currentHp > 0 }
                    .minByOrNull { Math.abs(it.progress - getPosProgress(b.position)) }

                if (target != null && (towerCooldowns[key] ?: 0) <= 0) {
                    val progressDiff = Math.abs(target.progress - getPosProgress(b.position))
                    if (progressDiff < (35f + _towerRangeBonus.value)) {
                        // Reload time shrinks with tower level (~0.6s at L1 down to ~0.4s at L3)
                        towerCooldowns[key] = maxOf(8, 22 - b.level * 3)
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
        // Forget cooldowns for towers that no longer exist (destroyed/demolished)
        towerCooldowns.keys.retainAll(_placedBuildings.value.keys)

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

        // 6. Hero update: downed/revive, movement, contact damage, auto-attack
        if (heroAttackCooldown > 0) heroAttackCooldown--
        if (heroHitCooldown > 0) heroHitCooldown--

        if (_heroDowned.value) {
            // Hero is incapacitated: count down, then revive at the core
            heroDownedTimer--
            _heroDownedSecondsLeft.value = maxOf(0, (heroDownedTimer + 32) / 33)
            if (heroDownedTimer <= 0) {
                _heroDowned.value = false
                _heroDownedSecondsLeft.value = 0
                _battleHeroX.value = 350f
                _battleHeroY.value = 750f
                _battleHeroTargetX.value = 350f
                _battleHeroTargetY.value = 750f
                _battleHeroHp.value = _battleHeroMaxHp.value * 0.5f
                texts.add(FloatingText(s(R.string.float_hero_revive), 350f, 700f, 0xFF8EF6FF))
            }
        } else {
            // 6a. Smooth movement toward the tapped destination
            val hx = _battleHeroX.value
            val hy = _battleHeroY.value
            val tx = _battleHeroTargetX.value
            val ty = _battleHeroTargetY.value
            val mdx = tx - hx
            val mdy = ty - hy
            val mdist = Math.sqrt((mdx * mdx + mdy * mdy).toDouble()).toFloat()
            val heroSpeed = if (_heroDashing.value) 34f else 13f
            if (mdist > heroSpeed) {
                _battleHeroX.value = hx + mdx / mdist * heroSpeed
                _battleHeroY.value = hy + mdy / mdist * heroSpeed
            } else if (mdist > 0f) {
                _battleHeroX.value = tx
                _battleHeroY.value = ty
            }

            val activeHeroLane = getLaneFromX(_battleHeroX.value)
            val heroLogicalY = _battleHeroY.value

            // 6b. Contact damage: enemies pressing the hero bite back
            val contacting = currentEnemies.filter {
                it.lane == activeHeroLane && it.currentHp > 0 && !it.isStunned &&
                    Math.abs(800f * (it.progress / 100f) - heroLogicalY) < 55f
            }
            if (contacting.isNotEmpty() && heroHitCooldown <= 0) {
                val biter = contacting.maxByOrNull { it.type.baseDamage }!!
                val contactDmg = biter.type.baseDamage + (contacting.size - 1)
                _battleHeroHp.value = maxOf(0f, _battleHeroHp.value - contactDmg)
                heroHitCooldown = 18 // ~0.55s between bites
                texts.add(FloatingText(s(R.string.float_hero_hit, contactDmg), _battleHeroX.value, _battleHeroY.value - 20f, 0xFFFF5566))
                _combatFlash.value = _combatFlash.value + 1
                if (_battleHeroHp.value <= 0f) {
                    _heroDowned.value = true
                    heroDownedTimer = 90 // ~2.7s incapacitated
                    _heroDownedSecondsLeft.value = 3
                    texts.add(FloatingText(s(R.string.float_hero_downed), _battleHeroX.value, _battleHeroY.value - 40f, 0xFFFF3344))
                }
            } else if (heroHitCooldown <= 0 && _battleHeroHp.value < _battleHeroMaxHp.value) {
                // Slow regeneration when out of melee
                _battleHeroHp.value = minOf(_battleHeroMaxHp.value, _battleHeroHp.value + 0.3f)
            }

            // 6c. Auto-attack nearest enemy on a steady cooldown
            val nearestToHero = currentEnemies.filter { it.lane == activeHeroLane && it.currentHp > 0 }
                .minByOrNull { Math.abs(800f * (it.progress / 100f) - heroLogicalY) }
            if (nearestToHero != null && heroAttackCooldown <= 0) {
                val distY = Math.abs(800f * (nearestToHero.progress / 100f) - heroLogicalY)
                if (distY < 120f) {
                    val damageMod = if (_lastLanternActive.value) 2.0f else 1.0f
                    val rawDmg = (12f * _heroAtkBonus.value * damageMod).toInt()
                    nearestToHero.currentHp -= rawDmg
                    heroAttackCooldown = if (_lastLanternActive.value) 6 else 12 // lantern = double rate
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
        // Set a movement destination; the battle tick glides the hero toward it.
        // Wider movable zone: hero can roam most of the field, but not into the
        // top spawn band (keeps lanes readable) or below the core.
        _battleHeroTargetX.value = targetX.coerceIn(40f, 660f)
        _battleHeroTargetY.value = targetY.coerceIn(180f, 850f)
    }

    fun useSkillMooncut() {
        if (_ember.value >= 15 && !_heroDowned.value) {
            _ember.value -= 15
            _heroDashing.value = true
            val activeLane = getLaneFromX(_battleHeroX.value)

            // Deal severe dash slash damage (50) and stun for 2 seconds (60 ticks)
            val current = _battleEnemies.value.toMutableList()
            val texts = _floatingTexts.value.toMutableList()

            // Lunge toward the nearest hostile ahead in this lane for impact
            val lungeTarget = current
                .filter { it.lane == activeLane && it.currentHp > 0 && 800f * (it.progress / 100f) <= _battleHeroY.value }
                .maxByOrNull { it.progress }
            if (lungeTarget != null) {
                _battleHeroTargetY.value = (800f * (lungeTarget.progress / 100f) + 30f).coerceIn(180f, 850f)
                _battleHeroTargetX.value = getLaneX(activeLane).coerceIn(40f, 660f)
            }
            // Punchy feedback (screen flash/shake)
            _combatFlash.value = _combatFlash.value + 1

            texts.add(FloatingText(s(R.string.float_mooncut_slash), _battleHeroX.value, _battleHeroY.value - 40f, 0xFF00FFCC))

            current.forEach { enemy ->
                if (enemy.lane == activeLane && Math.abs(800f * (enemy.progress / 100f) - _battleHeroY.value) < 320f) {
                    val damageVal = (50f * _heroAtkBonus.value).toInt()
                    enemy.currentHp -= damageVal
                    enemy.isStunned = true
                    enemy.stunnedTimer = 75 // 2.5 seconds
                    texts.add(FloatingText(s(R.string.float_stun_damage, damageVal), getLaneX(enemy.lane), 800f * (enemy.progress / 100f), 0xFF55FFFF))
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
            _combatFlash.value = _combatFlash.value + 1
            val texts = _floatingTexts.value.toMutableList()
            texts.add(FloatingText(s(R.string.float_mark_targets), _battleHeroX.value, _battleHeroY.value - 40f, 0xFFE040FB))
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
            val hopeBonusText = if (!coreDamagedThisNight) {
                _hope.value = minOf(100, _hope.value + 10)
                s(R.string.perfect_defense_bonus)
            } else {
                ""
            }

            if (_currentNight.value >= 7) {
                // Total Run victory
                _runsCleared.value += 1
                val updatedAccount = _accountState.value.copy(
                    seedAsh = _accountState.value.seedAsh + 80,
                    accountExp = _accountState.value.accountExp + 150,
                    totalRunsCleared = _runsCleared.value
                )
                repository.saveAccountState(updatedAccount)
                _accountState.value = updatedAccount
                _runStatsSummary.value = s(R.string.run_victory_summary, hopeBonusText)
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
            val partialExpReward = _currentNight.value * 20 // 20 Exp per survived Night
            val updatedAccount = _accountState.value.copy(
                seedAsh = _accountState.value.seedAsh + partialSeedAshReward,
                accountExp = _accountState.value.accountExp + partialExpReward,
                totalLosses = _runsLosses.value
            )
            repository.saveAccountState(updatedAccount)
            _accountState.value = updatedAccount
            _runStatsSummary.value = s(R.string.run_loss_summary, reason, _currentNight.value, partialSeedAshReward, partialExpReward)
            repository.clearActiveRun()
            _currentScreen.value = GameScreen.LOSS_SCREEN
        }
    }

    // -------------------------------------------------------------
    // DAWN REWARDS WORKER
    // -------------------------------------------------------------

    private fun generateDawnRewards() {
        val list = mutableListOf(
            DawnRewardOption("shards_payout", s(R.string.reward_shards_payout), s(R.string.reward_shards_payout_desc)),
            DawnRewardOption("core_reinforce", s(R.string.reward_core_reinforce), s(R.string.reward_core_reinforce_desc)),
            DawnRewardOption("hero_training", s(R.string.reward_hero_training), s(R.string.reward_hero_training_desc)),
            DawnRewardOption("tower_extension", s(R.string.reward_tower_extension), s(R.string.reward_tower_extension_desc))
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
            _placedBuildings.value.forEach { (key, b) ->
                val isRooted = _rootedBuildings.value.containsKey(key)
                if (b.type == BuildingType.MOONWELL && !isRooted) {
                    totalWellsIncome += 20 * b.level
                }
            }
            // Morale Hope bonus/penalty: (Hope - 50) / 2
            val hopeBonus = (_hope.value - 50) / 2
            _moonshards.value = maxOf(0, _moonshards.value + (20 + totalWellsIncome + hopeBonus)) // fixed Dawn bonus + well income + hope influence

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

    private fun generateUpcomingEnemies() {
        val list = mutableListOf<EnemyInstance>()
        val total = 10 + (_currentNight.value * 6)
        val hasLanternOath = _activeBargains.value.any { it.id == "lantern_oath" }
        
        // If Lantern Oath is active, add 3 extra elite hostiles to the pool
        val finalTotal = if (hasLanternOath) total + 3 else total
        
        for (i in 0 until finalTotal) {
            val lane = PlayLane.entries.random()
            val type = if (hasLanternOath && i >= total) {
                // Covenant extra elites
                if (Random.nextFloat() < 0.5f) EnemyType.GRAVE_BRUTE else EnemyType.HEX_ARCHER
            } else {
                decideEnemyTypeForUpcoming(i, total)
            }
            list.add(
                EnemyInstance(
                    id = UUID.randomUUID().toString(),
                    type = type,
                    lane = lane,
                    progress = 0f
                )
            )
        }
        _upcomingEnemies.value = list
    }

    private fun decideEnemyTypeForUpcoming(index: Int, total: Int): EnemyType {
        val night = _currentNight.value
        val r = Random.nextFloat()

        // On Night 7, first enemy is the colossal Boss (Nightseed Herald)
        if (night >= 7 && index == 0) {
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

    private fun checkHexArcherRangedTarget(enemy: EnemyInstance): Pair<SlotPosition?, Float>? {
        val outerKey = "${enemy.lane.name}_OUTER"
        val midKey = "${enemy.lane.name}_MID"
        val innerKey = "${enemy.lane.name}_INNER"

        val outer = _placedBuildings.value[outerKey]
        if (outer != null && outer.currentHp > 0 && enemy.progress < 30f) {
            if (30f - enemy.progress <= 20f) return Pair(SlotPosition.OUTER, 30f)
        }

        val mid = _placedBuildings.value[midKey]
        if (mid != null && mid.currentHp > 0 && enemy.progress < 50f) {
            if (50f - enemy.progress <= 20f) return Pair(SlotPosition.MID, 50f)
        }

        val inner = _placedBuildings.value[innerKey]
        if (inner != null && inner.currentHp > 0 && enemy.progress < 70f) {
            if (70f - enemy.progress <= 20f) return Pair(SlotPosition.INNER, 70f)
        }

        // Shoot at core when sufficiently close
        if (enemy.progress >= 80f && enemy.progress < 100f) {
            return Pair(null, 100f)
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
            "blood_mortar" -> DuskBargain("blood_mortar", s(R.string.bargain_blood_mortar), s(R.string.bargain_blood_mortar_desc), s(R.string.bargain_blood_mortar_benefit), s(R.string.bargain_blood_mortar_cost))
            "hungry_walls" -> DuskBargain("hungry_walls", s(R.string.bargain_hungry_walls), s(R.string.bargain_hungry_walls_desc), s(R.string.bargain_hungry_walls_benefit), s(R.string.bargain_hungry_walls_cost))
            "ashen_tithe" -> DuskBargain("ashen_tithe", s(R.string.bargain_ashen_tithe), s(R.string.bargain_ashen_tithe_desc), s(R.string.bargain_ashen_tithe_benefit), s(R.string.bargain_ashen_tithe_cost))
            else -> DuskBargain("lantern_oath", s(R.string.bargain_lantern_oath), s(R.string.bargain_lantern_oath_desc), s(R.string.bargain_lantern_oath_benefit), s(R.string.bargain_lantern_oath_cost))
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
