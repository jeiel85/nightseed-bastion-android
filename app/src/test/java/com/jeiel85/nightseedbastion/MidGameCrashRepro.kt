package com.jeiel85.nightseedbastion

import android.app.Application
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.jeiel85.nightseedbastion.data.ActiveRunEntity
import com.jeiel85.nightseedbastion.data.GameDatabase
import com.jeiel85.nightseedbastion.game.GameScreen
import com.jeiel85.nightseedbastion.game.GameViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class MidGameCrashRepro {

    private fun app(): Application = ApplicationProvider.getApplicationContext()

    private fun pumpUntil(timeoutMs: Long = 3000, predicate: () -> Boolean) {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (!predicate() && System.currentTimeMillis() < deadline) {
            shadowOf(Looper.getMainLooper()).idle()
            Thread.sleep(20)
        }
    }

    @Test
    fun `mid-turn orange button on night 4 does not crash`() {
        runBlocking {
            val db = GameDatabase.getDatabase(app())
            val placedBuildingsJson = """
                {
                  "LEFT_OUTER": {"id":"b1","typeId":"watchtower","level":2,"maxHp":150,"currentHp":80},
                  "CENTER_MID": {"id":"b2","typeId":"moonwell","level":1,"maxHp":80,"currentHp":80},
                  "RIGHT_INNER": {"id":"b3","typeId":"bell_shrine","level":1,"maxHp":70,"currentHp":70}
                }
            """.trimIndent()
            val activeBargainsJson = """["blood_mortar","lantern_oath"]"""
            db.gameDao().insertActiveRun(
                ActiveRunEntity(
                    id = 1,
                    isRunActive = true,
                    currentNight = 4,
                    coreMhp = 105,
                    coreHp = 75,
                    moonshards = 120,
                    ember = 30,
                    hope = 45,
                    heroAtkBonus = 1.2f,
                    towerRangeBonus = 15f,
                    placedBuildingsJson = placedBuildingsJson,
                    activeBargainsJson = activeBargainsJson,
                    runLogJson = "[]"
                )
            )
        }

        val vm = GameViewModel(app())
        pumpUntil { vm.currentNight.value == 4 }
        assertEquals(4, vm.currentNight.value)
        assertEquals(GameScreen.DAY_BUILD, vm.currentScreen.value)
        assertEquals(3, vm.placedBuildings.value.size)

        // The repro: press the orange button.
        vm.triggerBeginDusk()
        pumpUntil { vm.currentScreen.value == GameScreen.DUSK_OMEN }

        assertEquals(GameScreen.DUSK_OMEN, vm.currentScreen.value)
    }
}
