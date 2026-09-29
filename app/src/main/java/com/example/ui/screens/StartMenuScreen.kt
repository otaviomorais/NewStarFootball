package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Club
import com.example.model.Player
import com.example.ui.theme.BallBlack
import com.example.ui.theme.CardRed
import com.example.ui.theme.ChampionGold
import com.example.ui.theme.EnergyBlue
import com.example.ui.theme.GoldDark
import com.example.ui.theme.TurfLawnDark
import com.example.ui.theme.StadiumGreenDark
import com.example.ui.theme.StadiumGreenLight
import com.example.ui.theme.StadiumGreenPrimary

@Composable
fun StartMenuScreen(
  player: Player?,
  playerClub: Club?,
  nextOpponent: Club?,
  onPlayNextMatch: () -> Unit,
  onEnterCareerHub: () -> Unit,
  onGoToTraining: () -> Unit,
  onGoToProfile: () -> Unit,
  onNewCareerClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(1100, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "scale"
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            Color(0xFF071209),
            Color(0xFF0F2615),
            Color(0xFF09170D)
          )
        )
      )
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp),
      contentPadding = PaddingValues(top = 40.dp, bottom = 48.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Logo & Hero Title
      item {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(
            modifier = Modifier
              .size(80.dp)
              .scale(pulseScale)
              .clip(CircleShape)
              .background(
                Brush.radialGradient(
                  colors = listOf(ChampionGold, GoldDark, Color(0xFF142918))
                )
              )
              .border(2.dp, ChampionGold, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.SportsSoccer,
              contentDescription = "Logo Bola de Futebol",
              tint = BallBlack,
              modifier = Modifier.size(44.dp)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = "ESTRELA DO FUTEBOL",
            color = ChampionGold,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.5.sp,
            textAlign = TextAlign.Center
          )

          Text(
            text = "CARREIRA PROFISSIONAL INTERATIVA",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center
          )
        }
      }

      // 2. Active Player Summary Card
      if (player != null && playerClub != null) {
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("start_menu_player_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .background(
                  Brush.horizontalGradient(
                    colors = listOf(StadiumGreenDark, MaterialTheme.colorScheme.surface)
                  )
                )
                .padding(18.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color(playerClub.primaryColorHex)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "${player.number}",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                  )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "${player.name} \"${player.nickname}\"",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = if (player.isTrialCompleted)
                      "${playerClub.name} • ${player.position.label} • ${player.age} anos"
                    else
                      "Peneira de Olheiros • ${player.age} anos",
                    color = ChampionGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                  )
                  Text(
                    text = if (player.isTrialCompleted) player.careerTitle else "Modo Difícil: Sem clube (em avaliação)",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp
                  )
                }

                Column(horizontalAlignment = Alignment.End) {
                  Text(
                    text = "OVR",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = "${player.skills.overall}",
                    color = ChampionGold,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                  )
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              // In-game Energy Meter
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Energia Atual",
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 11.sp
                )
                Text(
                  text = "${player.energy}/${player.maxEnergy} HP",
                  color = if (player.isExhausted) CardRed else EnergyBlue,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              Spacer(modifier = Modifier.height(4.dp))

              LinearProgressIndicator(
                progress = { player.energyPercentage },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(6.dp)
                  .clip(RoundedCornerShape(3.dp)),
                color = if (player.isExhausted) CardRed else StadiumGreenLight,
                trackColor = Color(0xFF26382A)
              )
            }
          }
        }
      }

      // 3. Primary Play Next Match or Scout Trial Button
      item {
        val isTrialPending = player != null && !player.isTrialCompleted
        Button(
          onClick = onPlayNextMatch,
          modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .testTag("start_menu_play_button"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (isTrialPending) StadiumGreenLight else ChampionGold,
            contentColor = if (isTrialPending) Color.White else BallBlack
          ),
          elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
          Icon(
            imageVector = if (isTrialPending) Icons.Default.SportsSoccer else Icons.Default.PlayArrow,
            contentDescription = null,
            modifier = Modifier.size(26.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = if (isTrialPending)
              "REALIZAR PENEIRA DO OLHEIRO"
            else if (nextOpponent != null)
              "JOGAR CONTRA ${nextOpponent.name.uppercase()}"
            else
              "JOGAR PARTIDA",
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.5.sp
          )
        }
      }

      // 4. Secondary Action Buttons
      item {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          MenuOptionCard(
            title = "Modo Carreira (Central)",
            subtitle = "Acesse o painel completo, tabela, artilharia e notícias",
            icon = Icons.Default.EmojiEvents,
            onClick = onEnterCareerHub,
            testTag = "menu_career_hub_button"
          )

          MenuOptionCard(
            title = "Centro de Treinamento",
            subtitle = "Evolua Técnica, Força e Energia antes de ir a campo",
            icon = Icons.Default.FitnessCenter,
            onClick = onGoToTraining,
            testTag = "menu_training_button"
          )

          MenuOptionCard(
            title = "Perfil do Craque & Contratos",
            subtitle = "Atributos detalhados, relacionamento com técnico e torcida",
            icon = Icons.Default.Person,
            onClick = onGoToProfile,
            testTag = "menu_profile_button"
          )
        }
      }

      // 5. New Game / Reset Option
      item {
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedButton(
          onClick = onNewCareerClick,
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("start_menu_new_game_button"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.White.copy(alpha = 0.85f)
          )
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "INICIAR NOVA CARREIRA",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
          )
        }
      }
    }
  }
}

@Composable
private fun MenuOptionCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  onClick: () -> Unit,
  testTag: String
) {
  Surface(
    onClick = onClick,
    modifier = Modifier
      .fillMaxWidth()
      .testTag(testTag),
    shape = RoundedCornerShape(14.dp),
    color = Color(0xFF142416),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF263F2A))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(Color(0xFF1C341F)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = ChampionGold,
          modifier = Modifier.size(22.dp)
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          color = Color.White,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = subtitle,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 11.sp,
          lineHeight = 14.sp
        )
      }
    }
  }
}
