package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gameplay.PitchTacticalCanvas
import com.example.gameplay.ShotOutcome
import com.example.model.Club
import com.example.model.MatchChanceType
import com.example.model.Player
import com.example.model.TacticalActionType
import com.example.ui.theme.BallBlack
import com.example.ui.theme.CardRed
import com.example.ui.theme.ChampionGold
import com.example.ui.theme.EnergyBlue
import com.example.ui.theme.GoldDark
import com.example.ui.theme.StadiumGreenDark
import com.example.ui.theme.StadiumGreenLight
import com.example.viewmodel.LiveMatchState
import com.example.viewmodel.MatchPhase

@Composable
fun MatchScreen(
  matchState: LiveMatchState,
  player: Player,
  homeClub: Club,
  onShotFinished: (ShotOutcome) -> Unit,
  onResumeAfterGoal: () -> Unit,
  onExitPostMatch: () -> Unit,
  onSelectTacticalAction: (TacticalActionType) -> Unit = {},
  modifier: Modifier = Modifier
) {
  BackHandler {
    if (matchState.phase == MatchPhase.POST_MATCH) {
      onExitPostMatch()
    }
  }

  // Tactical strike controls state
  var impactOffset by remember(matchState.currentChanceIndex) { mutableStateOf(Offset(0f, 0f)) }
  var isExecutingShot by remember(matchState.currentChanceIndex) { mutableStateOf(false) }
  var aimPower by remember(matchState.currentChanceIndex) { mutableFloatStateOf(0.75f) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF0D180F))
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // 1. Live Match Scoreboard Header
      ScoreboardHeader(
        homeClub = homeClub,
        awayClub = matchState.opponentClub,
        homeGoals = matchState.homeGoals,
        awayGoals = matchState.awayGoals,
        minute = matchState.currentMinute,
        playerGoals = matchState.playerGoals
      )

      // 2. Middle Content: Either Interactive Chance OR Live Commentary Ticker
      val currentChance = matchState.chances.getOrNull(matchState.currentChanceIndex)

      if (matchState.phase == MatchPhase.INTERACTIVE_CHANCE && currentChance != null) {
        // --- INTERACTIVE PITCH & SHOT SELECTOR ---
        Column(
          modifier = Modifier
            .fillMaxSize()
            .weight(1f)
        ) {
          // Objective Prompt Banner
          Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF142417),
            tonalElevation = 2.dp
          ) {
            Column(
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "${currentChance.type.title.uppercase()} • ${currentChance.minute}'",
                color = ChampionGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
              Text(
                text = currentChance.promptMessage,
                color = Color.White,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
              )
            }
          }

          // Tactical Actions & In-Match Energy Selector
          Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF0F1B11),
            tonalElevation = 3.dp
          ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "AÇÃO TÁTICA & ENERGIA",
                  color = ChampionGold,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 0.5.sp
                )
                Text(
                  text = "${matchState.currentEnergy} / ${player.maxEnergy} HP",
                  color = if (matchState.currentEnergy < 15) CardRed else EnergyBlue,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              Spacer(modifier = Modifier.height(3.dp))

              LinearProgressIndicator(
                progress = { (matchState.currentEnergy.toFloat() / player.maxEnergy).coerceIn(0f, 1f) },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(4.dp)
                  .clip(RoundedCornerShape(2.dp)),
                color = if (matchState.currentEnergy < 15) CardRed else StadiumGreenLight,
                trackColor = Color(0xFF26382A)
              )

              Spacer(modifier = Modifier.height(6.dp))

              // Tactical Action Selection Chips
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                val allActions = listOf(
                  TacticalActionType.POWER_SHOT,
                  TacticalActionType.PASS,
                  TacticalActionType.PLACED_SHOT,
                  TacticalActionType.DRIBBLE
                )

                allActions.forEach { action ->
                  val isSelected = matchState.selectedAction == action
                  val canAfford = matchState.currentEnergy >= action.energyCost

                  Surface(
                    onClick = {
                      if (canAfford && !isExecutingShot) {
                        onSelectTacticalAction(action)
                      }
                    },
                    enabled = canAfford && !isExecutingShot,
                    shape = RoundedCornerShape(8.dp),
                    color = when {
                      isSelected -> ChampionGold
                      canAfford -> Color(0xFF1B2F1F)
                      else -> Color(0xFF1E1E1E)
                    },
                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(
                      1.dp,
                      if (canAfford) Color(0xFF2E4D34) else CardRed.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                      .weight(1f)
                      .testTag("action_chip_${action.id}")
                  ) {
                    Column(
                      modifier = Modifier.padding(vertical = 5.dp, horizontal = 2.dp),
                      horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                      Text(
                        text = action.shortLabel,
                        color = if (isSelected) BallBlack else if (canAfford) Color.White else Color.Gray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                      )
                      Text(
                        text = if (canAfford) "-${action.energyCost} HP" else "SEM HP",
                        color = if (isSelected) BallBlack.copy(alpha = 0.7f) else if (canAfford) EnergyBlue else CardRed,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.SemiBold
                      )
                    }
                  }
                }
              }
            }
          }

          // Tactical Pitch Canvas (Fluxo Autêntico New Star Soccer)
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f)
          ) {
            PitchTacticalCanvas(
              chance = currentChance,
              userPlayer = player,
              homeClub = homeClub,
              awayClub = matchState.opponentClub,
              isExecutingShot = isExecutingShot,
              selectedAction = matchState.selectedAction,
              onTriggerShotWithImpact = { impact, power, _ ->
                impactOffset = impact
                aimPower = power
                isExecutingShot = true
              },
              onShotFinished = { outcome ->
                isExecutingShot = false
                onShotFinished(outcome)
              }
            )
          }
        }
      } else {
        // --- LIVE COMMENTARY TICKER ---
        LiveCommentaryFeed(
          commentary = matchState.commentary,
          modifier = Modifier
            .fillMaxSize()
            .weight(1f)
        )
      }
    }

    // 3. Goal Celebration Popup Overlay
    AnimatedVisibility(
      visible = matchState.phase == MatchPhase.GOAL_CELEBRATION,
      enter = fadeIn(tween(200)) + scaleIn(tween(300)),
      exit = fadeOut(tween(200))
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color(0xCC000000)),
        contentAlignment = Alignment.Center
      ) {
        Card(
          modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth()
            .testTag("goal_celebration_card"),
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF142918)),
          border = androidx.compose.foundation.BorderStroke(2.dp, ChampionGold)
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.SportsSoccer,
              contentDescription = null,
              tint = ChampionGold,
              modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "GOOOOOOOOOOL!",
              color = ChampionGold,
              fontSize = 32.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = matchState.lastOutcomeMessage ?: "Gol antológico do camisa 10!",
              color = Color.White,
              fontSize = 15.sp,
              textAlign = TextAlign.Center,
              fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
              onClick = onResumeAfterGoal,
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("resume_match_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = StadiumGreenLight,
                contentColor = Color.White
              )
            ) {
              Text(
                text = "CONTINUAR PARTIDA",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
            }
          }
        }
      }
    }

    // 4. Post-Match Newspaper Overlay
    AnimatedVisibility(
      visible = matchState.phase == MatchPhase.POST_MATCH,
      enter = fadeIn(tween(250)),
      exit = fadeOut(tween(200))
    ) {
      matchState.postMatchResult?.let { result ->
        PostMatchNewspaperOverlay(
          result = result,
          homeClub = homeClub,
          player = player,
          onBackToHub = onExitPostMatch
        )
      }
    }
  }
}

@Composable
private fun ScoreboardHeader(
  homeClub: Club,
  awayClub: Club,
  homeGoals: Int,
  awayGoals: Int,
  minute: Int,
  playerGoals: Int
) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    color = StadiumGreenDark,
    tonalElevation = 4.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp, bottom = 12.dp, start = 16.dp, end = 16.dp)
    ) {
      // Top: Minute Pill & Stadium
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          color = Color(0xFF1E3A23),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text(
            text = "${minute}'",
            color = ChampionGold,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          )
        }

        Text(
          text = homeClub.stadiumName,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 12.sp
        )

        if (playerGoals > 0) {
          Text(
            text = "★ $playerGoals gol(s)",
            color = ChampionGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        } else {
          Spacer(modifier = Modifier.width(40.dp))
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Match Scoreline
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Home Club
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(Color(homeClub.primaryColorHex)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = homeClub.shortName,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = homeClub.name,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
        }

        // Score Box
        Surface(
          color = Color(0xFF09170C),
          shape = RoundedCornerShape(10.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C4430))
        ) {
          Text(
            text = "$homeGoals - $awayGoals",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
          )
        }

        // Away Club
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = awayClub.name,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
          Spacer(modifier = Modifier.width(8.dp))
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(Color(awayClub.primaryColorHex)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = awayClub.shortName,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }
        }
      }
    }
  }
}

@Composable
private fun LiveCommentaryFeed(
  commentary: List<com.example.model.CommentaryEvent>,
  modifier: Modifier = Modifier
) {
  val listState = rememberLazyListState()

  LaunchedEffect(commentary.size) {
    if (commentary.isNotEmpty()) {
      listState.animateScrollToItem(commentary.size - 1)
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp)
  ) {
    Text(
      text = "LANCE A LANCE EM TEMPO REAL",
      color = ChampionGold,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 1.sp,
      modifier = Modifier.padding(bottom = 8.dp)
    )

    LazyColumn(
      state = listState,
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(commentary) { event ->
        Surface(
          modifier = Modifier.fillMaxWidth(),
          color = if (event.isGoal) Color(0xFF1E3821) else Color(0xFF132015),
          shape = RoundedCornerShape(10.dp),
          border = if (event.isGoal) androidx.compose.foundation.BorderStroke(1.dp, ChampionGold) else null
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "${event.minute}'",
              color = if (event.isGoal) ChampionGold else MaterialTheme.colorScheme.onSurfaceVariant,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              modifier = Modifier.width(36.dp)
            )

            Text(
              text = event.text,
              color = if (event.isGoal) Color.White else Color(0xFFD6E2D8),
              fontSize = 13.sp,
              fontWeight = if (event.isGoal || event.isImportant) FontWeight.Bold else FontWeight.Normal,
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun PostMatchNewspaperOverlay(
  result: com.example.model.MatchResult,
  homeClub: Club,
  player: Player,
  onBackToHub: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xE6000000))
      .padding(16.dp),
    contentAlignment = Alignment.Center
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .testTag("post_match_newspaper_card"),
      colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F5EB)) // Retro Newspaper Papyrus Paper
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Newspaper Header Masthead
        Text(
          text = "A GAZETA DO FUTEBOL",
          color = Color(0xFF111111),
          fontSize = 24.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 2.sp
        )
        Text(
          text = "EDIÇÃO DE DOMINGO • COBERTURA ESPECIAL",
          color = Color(0xFF666666),
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))
        Box(modifier = Modifier.fillMaxWidth().height(1.5.dp).background(Color(0xFF222222)))
        Spacer(modifier = Modifier.height(10.dp))

        // Headline
        Text(
          text = result.headline,
          color = Color(0xFF1A1A1A),
          fontSize = 18.sp,
          fontWeight = FontWeight.Black,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = result.subHeadline,
          color = Color(0xFF444444),
          fontSize = 13.sp,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Star Rating & Placar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceAround,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "PLACAR FINAL",
              color = Color(0xFF555555),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "${result.userGoals} x ${result.opponentGoals}",
              color = Color(0xFF111111),
              fontSize = 22.sp,
              fontWeight = FontWeight.Black
            )
          }

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "NOTA DA PARTIDA",
              color = Color(0xFF555555),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = GoldDark,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = String.format("%.1f", result.playerRating),
                color = Color(0xFF111111),
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Financial & Energy Rewards Box
        Surface(
          modifier = Modifier.fillMaxWidth(),
          color = Color(0xFFE8E5D8),
          shape = RoundedCornerShape(8.dp)
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = "Salário + Bônus:", color = Color(0xFF333333), fontSize = 12.sp)
              Text(text = "+ R$ ${result.wageEarned + result.bonusEarned}", color = StadiumGreenDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = "Desgaste Físico:", color = Color(0xFF333333), fontSize = 12.sp)
              Text(text = "- ${result.energyCost} HP", color = CardRed, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
          }
        }

        // Skill & Career Evolution Section
        result.evolutionReport?.let { report ->
          Spacer(modifier = Modifier.height(10.dp))
          Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFDED9C7),
            shape = RoundedCornerShape(8.dp)
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text(
                text = "EVOLUÇÃO DOS ATRIBUTOS",
                color = Color(0xFF222222),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
              )
              Spacer(modifier = Modifier.height(4.dp))
              if (report.leveledUpSkills.isNotEmpty()) {
                report.leveledUpSkills.forEach { (skillName, newVal) ->
                  Text(
                    text = "★ $skillName subiu para $newVal!",
                    color = StadiumGreenDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                  )
                }
              }
              if (report.careerLevelUp) {
                Text(
                  text = "🎉 NOVO NÍVEL DE CARREIRA: Nível ${report.newCareerLevel}!",
                  color = Color(0xFF8D6E63),
                  fontWeight = FontWeight.Black,
                  fontSize = 12.sp
                )
              }
              if (report.maxEnergyIncreased) {
                Text(
                  text = "⚡ Fôlego Expandido: Energia Máx subiu para ${report.newMaxEnergy} HP!",
                  color = Color(0xFF0277BD),
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp
                )
              }
              Text(
                text = "+${report.careerXpGained} XP de Carreira ganhos",
                color = Color(0xFF555555),
                fontSize = 11.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
          onClick = onBackToHub,
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("exit_post_match_button"),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1E3A23),
            contentColor = Color.White
          )
        ) {
          Text(text = "AVANÇAR PARA O HUB", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
