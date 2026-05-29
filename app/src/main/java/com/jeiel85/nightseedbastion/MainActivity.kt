package com.jeiel85.nightseedbastion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.jeiel85.nightseedbastion.game.GameScreen
import com.jeiel85.nightseedbastion.game.GameViewModel
import com.jeiel85.nightseedbastion.ui.screens.*
import com.jeiel85.nightseedbastion.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = true, dynamicColor = false) {
                val currentScreen by viewModel.currentScreen.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentScreen) {
                        GameScreen.MAIN_MENU -> MainMenuScreen(viewModel)
                        GameScreen.DAY_BUILD -> DayBuildScreen(viewModel)
                        GameScreen.DUSK_OMEN -> DuskScreen(viewModel)
                        GameScreen.NIGHT_BATTLE -> NightBattleScreen(viewModel)
                        GameScreen.DAWN_REWARDS -> DawnRewardScreen(viewModel)
                        GameScreen.VICTORY_SCREEN -> VictoryScreen(viewModel)
                        GameScreen.LOSS_SCREEN -> LossScreen(viewModel)
                    }
                }
            }
        }
    }
}
