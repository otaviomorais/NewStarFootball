package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.ui.theme.GoldDark
import com.example.ui.theme.StadiumGreenDark

/**
 * Authentic Arcade Game Title Screen & Start Menu:
 * Clean, cinematic, zero in-game clutter before starting or loading a career!
 */
@Composable
fun StartMenuScreen(
  player: Player?,
  playerClub: Club?,
  soundEnabled: Boolean = true,
  vibrationEnabled: Boolean = true,
  onContinueCareer: () -> Unit,
  onNewCareerClick: () -> Unit,
  onToggleSound: () -> Unit = {},
  onToggleVibration: () -> Unit = {},
  onDeleteCareer: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  var showTutorialDialog by remember { mutableStateOf(false) }
  var showSettingsDialog by remember { mutableStateOf(false) }
  var showOverwriteWarning by remember { mutableStateOf(false) }

  val infiniteTransition = rememberInfiniteTransition(label = "pulse_star")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.98f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "star_scale"
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            Color(0xFF041006),
            Color(0xFF0B2612),
            Color(0xFF06140A)
          )
        )
      ),
    contentAlignment = Alignment.Center
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp, vertical = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // 1. TOP HEADER: Stars & Game Logo
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 28.dp)
      ) {
        // Five Stars Badge
        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.scale(pulseScale)
        ) {
          repeat(5) { index ->
            val starSize = if (index == 2) 26.dp else 20.dp
            Icon(
              imageVector = Icons.Default.Star,
              contentDescription = null,
              tint = ChampionGold,
              modifier = Modifier.size(starSize)
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Main Ball Logo Icon
        Box(
          modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                colors = listOf(ChampionGold, GoldDark, Color(0xFF142E1B))
              )
            )
            .border(2.5.dp, ChampionGold, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.SportsSoccer,
            contentDescription = null,
            tint = BallBlack,
            modifier = Modifier.size(44.dp)
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "ESTRELA DO FUTEBOL",
          color = ChampionGold,
          fontSize = 28.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 1.5.sp,
          textAlign = TextAlign.Center
        )

        Text(
          text = "RUMO AO ESTRELATO",
          color = Color(0xFFA5C4AC),
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 2.sp,
          textAlign = TextAlign.Center
        )
      }

      // 2. CENTER: Clean Action Buttons (Authentic Game Menu!)
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Option A: CONTINUAR CARREIRA (If saved game exists)
        if (player != null && playerClub != null) {
          Card(
            onClick = onContinueCareer,
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = ChampionGold),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("continue_career_button")
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 18.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                Icon(
                  imageVector = Icons.Default.PlayArrow,
                  contentDescription = null,
                  tint = BallBlack,
                  modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "CONTINUAR CARREIRA",
                  color = BallBlack,
                  fontSize = 17.sp,
                  fontWeight = FontWeight.Black,
                  letterSpacing = 0.5.sp
                )
              }

              Spacer(modifier = Modifier.height(4.dp))

              // Sleek badge showing whose career it is
              Surface(
                color = BallBlack.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text(
                  text = "${player.name} (${player.nickname}) • ${playerClub.shortName} • Nível ${player.careerLevel}",
                  color = BallBlack.copy(alpha = 0.85f),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
              }
            }
          }
        }

        // Option B: NOVA CARREIRA
        Button(
          onClick = {
            if (player != null) {
              showOverwriteWarning = true
            } else {
              onNewCareerClick()
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag("new_career_button"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (player == null) ChampionGold else Color(0xFF1B3821),
            contentColor = if (player == null) BallBlack else Color.White
          ),
          border = if (player != null) androidx.compose.foundation.BorderStroke(1.5.dp, ChampionGold.copy(alpha = 0.6f)) else null
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "NOVA CARREIRA",
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.5.sp
          )
        }

        // Option C: COMO JOGAR (Tutorial do New Star Soccer)
        OutlinedButton(
          onClick = { showTutorialDialog = true },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("how_to_play_button"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFFA5C4AC)
          ),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF264D2F))
        ) {
          Icon(
            imageVector = Icons.Default.HelpOutline,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "COMO JOGAR (GUIA DO JOGO)",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
        }

        // Option D: CONFIGURAÇÕES
        OutlinedButton(
          onClick = { showSettingsDialog = true },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("settings_button"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFFA5C4AC)
          ),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF264D2F))
        ) {
          Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "CONFIGURAÇÕES",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // 3. BOTTOM FOOTER
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(bottom = 12.dp)
      ) {
        Text(
          text = "ESTRELA DO FUTEBOL v1.0.0",
          color = Color(0xFF6B8B72),
          fontSize = 10.5.sp,
          fontWeight = FontWeight.SemiBold
        )
        Text(
          text = "Inspirado na clássica jogabilidade de New Star Soccer",
          color = Color(0xFF4C6652),
          fontSize = 10.sp
        )
      }
    }

    // Modal: COMO JOGAR
    if (showTutorialDialog) {
      AlertDialog(
        onDismissRequest = { showTutorialDialog = false },
        containerColor = Color(0xFF0F2615),
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.SportsSoccer, contentDescription = null, tint = ChampionGold)
            Spacer(modifier = Modifier.width(8.dp))
            Text("COMO JOGAR", color = ChampionGold, fontWeight = FontWeight.Black, fontSize = 17.sp)
          }
        },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            TutorialStepItem(
              step = "1",
              title = "O Momento Decisivo",
              description = "A partida avança pelo radar e texto. Quando a jogada chega até você, o lance congela no campo tático!"
            )
            TutorialStepItem(
              step = "2",
              title = "Mire no Campo (Arrastar)",
              description = "Arraste o dedo na tela para mirar a direção e força do chute ou passe para um companheiro livre."
            )
            TutorialStepItem(
              step = "3",
              title = "Bata na Bola (Efeito e Altura)",
              description = "A bola aparece em close-up girando! Toque embaixo para cavadinha por cima dos zagueiros, nas laterais para curva (Efeito Magnus), ou no meio para bomba!"
            )
            TutorialStepItem(
              step = "4",
              title = "Evolua sua Carreira",
              description = "Ganhe salários, compre chuteiras, treine energia/stamina, equilibre a vida pessoal e conquiste títulos!"
            )
          }
        },
        confirmButton = {
          Button(
            onClick = { showTutorialDialog = false },
            colors = ButtonDefaults.buttonColors(containerColor = ChampionGold)
          ) {
            Text("ENTENDI!", color = BallBlack, fontWeight = FontWeight.Bold)
          }
        }
      )
    }

    // Modal: CONFIGURAÇÕES
    if (showSettingsDialog) {
      AlertDialog(
        onDismissRequest = { showSettingsDialog = false },
        containerColor = Color(0xFF0F2615),
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = ChampionGold)
            Spacer(modifier = Modifier.width(8.dp))
            Text("CONFIGURAÇÕES", color = ChampionGold, fontWeight = FontWeight.Black, fontSize = 17.sp)
          }
        },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Som
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Efeitos Sonoros", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
              }
              Switch(
                checked = soundEnabled,
                onCheckedChange = { onToggleSound() },
                colors = SwitchDefaults.colors(checkedThumbColor = ChampionGold, checkedTrackColor = StadiumGreenDark)
              )
            }

            // Vibração
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Vibration, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Vibração Háptica", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
              }
              Switch(
                checked = vibrationEnabled,
                onCheckedChange = { onToggleVibration() },
                colors = SwitchDefaults.colors(checkedThumbColor = ChampionGold, checkedTrackColor = StadiumGreenDark)
              )
            }

            // Excluir Carreira Salva
            if (player != null) {
              Spacer(modifier = Modifier.height(6.dp))
              OutlinedButton(
                onClick = {
                  onDeleteCareer()
                  showSettingsDialog = false
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CardRed),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardRed.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, tint = CardRed, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Apagar Carreira Salva", color = CardRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        },
        confirmButton = {
          Button(
            onClick = { showSettingsDialog = false },
            colors = ButtonDefaults.buttonColors(containerColor = ChampionGold)
          ) {
            Text("FECHAR", color = BallBlack, fontWeight = FontWeight.Bold)
          }
        }
      )
    }

    // Modal: AVISO DE SOBRESCREVER CARREIRA
    if (showOverwriteWarning) {
      AlertDialog(
        onDismissRequest = { showOverwriteWarning = false },
        containerColor = Color(0xFF142417),
        title = {
          Text("INICIAR NOVA CARREIRA?", color = ChampionGold, fontWeight = FontWeight.Black)
        },
        text = {
          Text(
            "Você já possui uma carreira salva com ${player?.name} (${playerClub?.name}). Iniciar uma nova carreira apagará esse progresso. Tem certeza?",
            color = Color.White,
            fontSize = 13.sp
          )
        },
        confirmButton = {
          Button(
            onClick = {
              showOverwriteWarning = false
              onNewCareerClick()
            },
            colors = ButtonDefaults.buttonColors(containerColor = CardRed)
          ) {
            Text("SIM, CRIAR NOVA", color = Color.White, fontWeight = FontWeight.Bold)
          }
        },
        dismissButton = {
          TextButton(onClick = { showOverwriteWarning = false }) {
            Text("CANCELAR", color = Color(0xFFA5C4AC))
          }
        }
      )
    }
  }
}

@Composable
private fun TutorialStepItem(step: String, title: String, description: String) {
  Row(verticalAlignment = Alignment.Top) {
    Box(
      modifier = Modifier
        .size(24.dp)
        .clip(CircleShape)
        .background(ChampionGold),
      contentAlignment = Alignment.Center
    ) {
      Text(text = step, color = BallBlack, fontWeight = FontWeight.Black, fontSize = 12.sp)
    }
    Spacer(modifier = Modifier.width(10.dp))
    Column {
      Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
      Text(text = description, color = Color(0xFFA5C4AC), fontSize = 11.5.sp, lineHeight = 15.sp)
    }
  }
}
