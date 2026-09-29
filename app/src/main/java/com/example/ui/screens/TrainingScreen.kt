package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Player
import com.example.model.SkillCategory
import com.example.model.SkillType
import com.example.ui.theme.CardRed
import com.example.ui.theme.ChampionGold
import com.example.ui.theme.EnergyBlue
import com.example.ui.theme.StadiumGreenLight

@Composable
fun TrainingScreen(
  player: Player,
  onTrainSkill: (skillKey: String) -> Unit,
  onRestClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header & Energy Status
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("training_header_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "CENTRO DE TREINAMENTO",
                color = ChampionGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
              Text(
                text = "Evolua Técnica, Força e Energia com treinos diários",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
              )
            }

            Icon(
              imageVector = Icons.Default.FitnessCenter,
              contentDescription = null,
              tint = ChampionGold,
              modifier = Modifier.size(28.dp)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Energia: ${player.energy}/${player.maxEnergy} HP",
              color = Color.White,
              fontSize = 14.sp,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = "Custo: -15 HP por treino",
              color = if (player.energy < 15) CardRed else EnergyBlue,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          LinearProgressIndicator(
            progress = { player.energyPercentage },
            modifier = Modifier
              .fillMaxWidth()
              .height(8.dp)
              .clip(RoundedCornerShape(4.dp)),
            color = if (player.isExhausted) CardRed else StadiumGreenLight,
            trackColor = Color(0xFF26382A)
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Rest Action / Day off
          if (onRestClick != null) {
            OutlinedButton(
              onClick = onRestClick,
              enabled = player.energy < player.maxEnergy,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("rest_day_button"),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.outlinedButtonColors(
                contentColor = EnergyBlue
              )
            ) {
              Icon(
                imageVector = Icons.Default.Bedtime,
                contentDescription = null,
                tint = EnergyBlue,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (player.energy >= player.maxEnergy) "ENERGIA TOTALMENTE CARREGADA" else "TIRAR DIA DE FOLGA / DESCANSAR (+25 HP)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }

    // 1. Técnica (Core Attribute 1)
    item {
      TrainingDrillCard(
        title = "Técnica & Drible",
        badge = "TÉCNICO",
        badgeColor = Color(0xFF2E7D32),
        description = "Controle de bola de primeira, domínio orientado sob pressão e facilidade em dribles.",
        currentValue = player.skills.technique,
        currentXp = player.skills.techniqueXp,
        icon = Icons.Default.Psychology,
        canTrain = player.energy >= 15,
        onTrain = { onTrainSkill(SkillType.TECHNIQUE.id) }
      )
    }

    // 2. Força (Core Attribute 2)
    item {
      TrainingDrillCard(
        title = "Força & Potência",
        badge = "FÍSICO",
        badgeColor = Color(0xFFC62828),
        description = "Potência explosiva nas finalizações, chutes fortes de fora da área e divididas físicas.",
        currentValue = player.skills.power,
        currentXp = player.skills.powerXp,
        icon = Icons.Default.FlashOn,
        canTrain = player.energy >= 15,
        onTrain = { onTrainSkill(SkillType.POWER.id) }
      )
    }

    // 3. Fôlego & Resistência (Core Attribute 3: Energia)
    item {
      TrainingDrillCard(
        title = "Energia & Resistência (Fôlego)",
        badge = "FÍSICO",
        badgeColor = Color(0xFF0277BD),
        description = "Resistência aeróbica nas partidas de 90 min. Reduz perda de energia e expande a barra máxima para até 130 HP.",
        currentValue = player.skills.stamina,
        currentXp = player.skills.staminaXp,
        icon = Icons.Default.Timer,
        canTrain = player.energy >= 15,
        onTrain = { onTrainSkill(SkillType.STAMINA.id) }
      )
    }

    // 4. Precisão
    item {
      TrainingDrillCard(
        title = "Precisão & Mira",
        badge = "TÉCNICO",
        badgeColor = Color(0xFF2E7D32),
        description = "Finalizações nas gavetas e bochechas da rede. Diminui a tolerância de erro do chute.",
        currentValue = player.skills.accuracy,
        currentXp = player.skills.accuracyXp,
        icon = Icons.Default.Adjust,
        canTrain = player.energy >= 15,
        onTrain = { onTrainSkill(SkillType.ACCURACY.id) }
      )
    }

    // 5. Visão de Jogo
    item {
      TrainingDrillCard(
        title = "Visão de Jogo & Passe",
        badge = "TÁTICO",
        badgeColor = Color(0xFF6A1B9A),
        description = "Passes em profundidade que deixam companheiros na cara do gol. Aumenta assistências.",
        currentValue = player.skills.vision,
        currentXp = player.skills.visionXp,
        icon = Icons.Default.RemoveRedEye,
        canTrain = player.energy >= 15,
        onTrain = { onTrainSkill(SkillType.VISION.id) }
      )
    }

    // 6. Velocidade
    item {
      TrainingDrillCard(
        title = "Velocidade & Arranque",
        badge = "FÍSICO",
        badgeColor = Color(0xFFEF6C00),
        description = "Aceleração nos primeiros metros e velocidade máxima para escapar da zaga adversária.",
        currentValue = player.skills.pace,
        currentXp = player.skills.paceXp,
        icon = Icons.Default.Bolt,
        canTrain = player.energy >= 15,
        onTrain = { onTrainSkill(SkillType.PACE.id) }
      )
    }

    // 7. Curva
    item {
      TrainingDrillCard(
        title = "Curva & Efeito Magnus",
        badge = "TÉCNICO",
        badgeColor = Color(0xFF2E7D32),
        description = "Chutes colocados com curva de trivela ou chapa contornando barreira e goleiro.",
        currentValue = player.skills.curl,
        currentXp = player.skills.curlXp,
        icon = Icons.Default.TrendingUp,
        canTrain = player.energy >= 15,
        onTrain = { onTrainSkill(SkillType.CURL.id) }
      )
    }

    // 8. Falta & Pênalti
    item {
      TrainingDrillCard(
        title = "Bolas Paradas (Faltas e Pênaltis)",
        badge = "TÉCNICO",
        badgeColor = Color(0xFF2E7D32),
        description = "Especialista em bolas paradas decisivas para decidir clássicos nos acréscimos.",
        currentValue = player.skills.freeKick,
        currentXp = player.skills.freeKickXp,
        icon = Icons.Default.SportsSoccer,
        canTrain = player.energy >= 15,
        onTrain = { onTrainSkill(SkillType.FREE_KICK.id) }
      )
    }
  }
}

@Composable
private fun TrainingDrillCard(
  title: String,
  badge: String,
  badgeColor: Color,
  description: String,
  currentValue: Int,
  currentXp: Int,
  icon: ImageVector,
  canTrain: Boolean,
  onTrain: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0xFF1B311E)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = ChampionGold,
          modifier = Modifier.size(24.dp)
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = title,
              color = Color.White,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(badgeColor.copy(alpha = 0.35f))
                .padding(horizontal = 5.dp, vertical = 1.dp)
            ) {
              Text(
                text = badge,
                color = badgeColor,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
          Text(
            text = "$currentValue / 99",
            color = ChampionGold,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
          text = description,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 11.sp,
          lineHeight = 15.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        // XP towards next attribute point
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "Evolução do Atributo",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp
          )
          Text(
            text = "$currentXp/100 XP",
            color = ChampionGold,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.height(3.dp))

        LinearProgressIndicator(
          progress = { currentXp / 100f },
          modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp)),
          color = ChampionGold,
          trackColor = Color(0xFF2B3A2E)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
          onClick = onTrain,
          enabled = canTrain && currentValue < 99,
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = StadiumGreenLight,
            contentColor = Color.White
          )
        ) {
          Text(
            text = if (currentValue >= 99) "MÁXIMO" else "TREINAR (+50 XP)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}
