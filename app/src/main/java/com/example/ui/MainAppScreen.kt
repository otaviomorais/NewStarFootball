package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.DefaultShopItems
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MatchScreen
import com.example.ui.screens.NewCareerDialog
import com.example.ui.screens.PlayerScreen
import com.example.ui.screens.ScoutTrialScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.screens.StandingsScreen
import com.example.ui.screens.StartMenuScreen
import com.example.ui.screens.TrainingScreen
import com.example.ui.theme.BallBlack
import com.example.ui.theme.ChampionGold
import com.example.ui.theme.StadiumGreenDark
import com.example.ui.theme.StadiumGreenLight
import com.example.viewmodel.GameViewModel
import kotlinx.coroutines.launch

enum class MainTab(val title: String, val icon: ImageVector) {
  MATCH("Partida", Icons.Default.SportsSoccer),
  PLAYER("Jogador", Icons.Default.Person),
  TRAINING("Treino", Icons.Default.FitnessCenter),
  SHOP("Loja", Icons.Default.ShoppingBag),
  TABLE("Tabela", Icons.Default.EmojiEvents)
}

@Composable
fun MainAppScreen(
  viewModel: GameViewModel = viewModel()
) {
  val player by viewModel.player.collectAsStateWithLifecycle()
  val clubs by viewModel.clubs.collectAsStateWithLifecycle()
  val ownedItems by viewModel.ownedItems.collectAsStateWithLifecycle()
  val liveMatch by viewModel.liveMatch.collectAsStateWithLifecycle()

  var isAtStartMenu by remember { mutableStateOf(true) }
  var selectedTab by remember { mutableStateOf(MainTab.MATCH) }
  var showNewCareerDialog by remember { mutableStateOf(false) }

  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()

  // Fullscreen Match Mode when in a match
  val activeMatch = liveMatch
  if (activeMatch != null && player != null) {
    MatchScreen(
      matchState = activeMatch,
      player = player!!,
      homeClub = viewModel.getPlayerClub(),
      onShotFinished = { outcome ->
        viewModel.onShotOutcome(outcome)
      },
      onResumeAfterGoal = {
        viewModel.resumeMatchAfterGoal()
      },
      onExitPostMatch = {
        viewModel.exitPostMatch()
        selectedTab = MainTab.MATCH
      },
      onSelectTacticalAction = { action ->
        viewModel.selectTacticalAction(action)
      }
    )
    return
  }

  // Initial Start Menu Screen
  if (isAtStartMenu) {
    StartMenuScreen(
      player = player,
      playerClub = player?.let { viewModel.getPlayerClub() },
      nextOpponent = viewModel.getNextOpponent(),
      onPlayNextMatch = {
        if (player != null && !player!!.isTrialCompleted) {
          viewModel.soundManager.playClick()
          isAtStartMenu = false
        } else {
          viewModel.soundManager.playSuccess()
          viewModel.startMatch(viewModel.getNextOpponent())
        }
      },
      onEnterCareerHub = {
        viewModel.soundManager.playClick()
        isAtStartMenu = false
        if (player == null || player!!.isTrialCompleted) {
          selectedTab = MainTab.MATCH
        }
      },
      onGoToTraining = {
        viewModel.soundManager.playClick()
        isAtStartMenu = false
        selectedTab = MainTab.TRAINING
      },
      onGoToProfile = {
        viewModel.soundManager.playClick()
        isAtStartMenu = false
        selectedTab = MainTab.PLAYER
      },
      onNewCareerClick = {
        viewModel.soundManager.playClick()
        showNewCareerDialog = true
      }
    )

    if (showNewCareerDialog) {
      NewCareerDialog(
        availableClubs = clubs,
        onDismiss = { showNewCareerDialog = false },
        onCreateCareer = { name, nickname, pos, foot, age, clubId, startWithTrial ->
          viewModel.resetOrNewCareer(
            name = name,
            nickname = nickname,
            pos = pos,
            foot = foot,
            age = age,
            clubId = clubId,
            startWithTrial = startWithTrial
          )
          showNewCareerDialog = false
          isAtStartMenu = false
          selectedTab = MainTab.MATCH
        }
      )
    }
    return
  }

  // Fullscreen Scout Trial Mode (Modo Difícil: Peneira de Olheiros)
  val trialPlayer = player
  if (trialPlayer != null && !trialPlayer.isTrialCompleted) {
    ScoutTrialScreen(
      player = trialPlayer,
      availableClubs = clubs,
      onSignContract = { offer ->
        viewModel.signContractOffer(offer)
        scope.launch {
          snackbarHostState.showSnackbar("Parabéns! Contrato assinado com o ${offer.club.name}!")
        }
        selectedTab = MainTab.MATCH
      }
    )
    BackHandler {
      isAtStartMenu = true
    }
    return
  }

  BackHandler {
    isAtStartMenu = true
  }

  // Regular Hub Mode with Bottom Navigation
  Scaffold(
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    bottomBar = {
      NavigationBar(
        modifier = Modifier
          .windowInsetsPadding(WindowInsets.navigationBars)
          .testTag("main_bottom_nav"),
        containerColor = StadiumGreenDark,
        tonalElevation = 8.dp
      ) {
        MainTab.values().forEach { tab ->
          val isSelected = selectedTab == tab
          NavigationBarItem(
            selected = isSelected,
            onClick = {
              viewModel.soundManager.playClick()
              selectedTab = tab
            },
            icon = {
              Icon(
                imageVector = tab.icon,
                contentDescription = tab.title
              )
            },
            label = {
              Text(
                text = tab.title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = BallBlack,
              selectedTextColor = ChampionGold,
              indicatorColor = ChampionGold,
              unselectedIconColor = Color(0xFFA5B8A8),
              unselectedTextColor = Color(0xFFA5B8A8)
            )
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      val curPlayer = player
      if (curPlayer != null) {
        val playerClub = viewModel.getPlayerClub()
        val nextOpponent = viewModel.getNextOpponent()

        when (selectedTab) {
          MainTab.MATCH -> {
            HomeScreen(
              player = curPlayer,
              playerClub = playerClub,
              nextOpponent = nextOpponent,
              onPlayMatchClick = {
                viewModel.startMatch(nextOpponent)
              },
              onQuickDrinkClick = {
                val drink = DefaultShopItems.getAllItems().firstOrNull { it.id == "energy_isocool" }
                if (drink != null) {
                  viewModel.buyShopItem(
                    item = drink,
                    onSuccess = {
                      scope.launch { snackbarHostState.showSnackbar("Energia recuperada em +30%!") }
                    },
                    onFail = { error ->
                      scope.launch { snackbarHostState.showSnackbar(error) }
                    }
                  )
                }
              },
              onViewStandingsClick = {
                selectedTab = MainTab.TABLE
              },
              onGoToStartMenuClick = {
                isAtStartMenu = true
              }
            )
          }

          MainTab.PLAYER -> {
            PlayerScreen(
              player = curPlayer,
              playerClub = playerClub,
              onSocialAction = { category ->
                viewModel.socialInteract(
                  category = category,
                  onSuccess = { msg ->
                    scope.launch { snackbarHostState.showSnackbar(msg) }
                  },
                  onFail = { err ->
                    scope.launch { snackbarHostState.showSnackbar(err) }
                  }
                )
              },
              onResetCareerClick = {
                showNewCareerDialog = true
              }
            )
          }

          MainTab.TRAINING -> {
            TrainingScreen(
              player = curPlayer,
              onTrainSkill = { skillKey ->
                viewModel.trainSkill(
                  skillTypeKey = skillKey,
                  onSuccess = { msg ->
                    scope.launch { snackbarHostState.showSnackbar(msg) }
                  },
                  onFail = { err ->
                    scope.launch { snackbarHostState.showSnackbar(err) }
                  }
                )
              },
              onRestClick = {
                viewModel.restPlayer { msg ->
                  scope.launch { snackbarHostState.showSnackbar(msg) }
                }
              }
            )
          }

          MainTab.SHOP -> {
            ShopScreen(
              player = curPlayer,
              ownedItems = ownedItems,
              onBuyItem = { item ->
                viewModel.buyShopItem(
                  item = item,
                  onSuccess = {
                    scope.launch {
                      snackbarHostState.showSnackbar(
                        if (item.isConsumable) "${item.name} consumido com sucesso!"
                        else "${item.name} adquirido!"
                      )
                    }
                  },
                  onFail = { err ->
                    scope.launch { snackbarHostState.showSnackbar(err) }
                  }
                )
              }
            )
          }

          MainTab.TABLE -> {
            StandingsScreen(
              clubs = clubs,
              player = curPlayer
            )
          }
        }
      }
    }

    if (showNewCareerDialog) {
      NewCareerDialog(
        availableClubs = clubs,
        onDismiss = { showNewCareerDialog = false },
        onCreateCareer = { name, nickname, pos, foot, age, clubId, startWithTrial ->
          viewModel.resetOrNewCareer(
            name = name,
            nickname = nickname,
            pos = pos,
            foot = foot,
            age = age,
            clubId = clubId,
            startWithTrial = startWithTrial
          )
          showNewCareerDialog = false
          if (startWithTrial) {
            isAtStartMenu = false
          } else {
            isAtStartMenu = false
            selectedTab = MainTab.MATCH
          }
          scope.launch {
            snackbarHostState.showSnackbar("Nova carreira iniciada com sucesso! Boa sorte, craque!")
          }
        }
      )
    }
  }
}
