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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Nightlife
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolunteerActivism
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Club
import com.example.model.LifestyleActivity
import com.example.model.Player
import com.example.ui.theme.CardRed
import com.example.ui.theme.ChampionGold
import com.example.ui.theme.EnergyBlue
import com.example.ui.theme.StadiumGreenLight

@Composable
fun PlayerScreen(
  player: Player,
  playerClub: Club,
  onSocialAction: (category: String) -> Unit,
  onResetCareerClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Player Card Banner & Career Level
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("player_details_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(playerClub.primaryColorHex)),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "${player.number}",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black
              )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
              Text(
                text = player.name,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
              )
              Text(
                text = "\"${player.nickname}\"",
                color = ChampionGold,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = player.careerTitle,
                color = ChampionGold.copy(alpha = 0.9f),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
              )
              Text(
                text = "${player.age} anos • ${player.nationality} • Pé ${player.preferredFoot}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Career Level Bar
          Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF142417),
            shape = RoundedCornerShape(10.dp)
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "Nível de Carreira: ${player.careerLevel}",
                  color = Color.White,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "${player.careerXp} XP Acumulado",
                  color = ChampionGold,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              LinearProgressIndicator(
                progress = { ((player.careerXp % 250) / 250f).coerceIn(0f, 1f) },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(6.dp)
                  .clip(RoundedCornerShape(3.dp)),
                color = ChampionGold,
                trackColor = Color(0xFF26382A)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Contract info
          Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF142417),
            shape = RoundedCornerShape(10.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceAround
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Clube Atual", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                Text(text = playerClub.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
              }
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Salário / Jogo", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                Text(text = "R$ ${player.wagePerMatch}", color = ChampionGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
              }
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Energia Máx.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                Text(text = "${player.maxEnergy} HP", color = EnergyBlue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
              }
            }
          }
        }
      }
    }

    // 2. Technical, Physical & Energy Attributes Section
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("player_skills_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "ATRIBUTOS DE HABILIDADE",
              color = ChampionGold,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
            Text(
              text = "OVERALL: ${player.skills.overall}",
              color = Color.White,
              fontSize = 14.sp,
              fontWeight = FontWeight.Black
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          SkillEvolutionBar(label = "Técnica & Drible", value = player.skills.technique, xp = player.skills.techniqueXp)
          SkillEvolutionBar(label = "Força & Potência", value = player.skills.power, xp = player.skills.powerXp)
          SkillEvolutionBar(label = "Resistência (Fôlego)", value = player.skills.stamina, xp = player.skills.staminaXp)
          SkillEvolutionBar(label = "Precisão & Finalização", value = player.skills.accuracy, xp = player.skills.accuracyXp)
          SkillEvolutionBar(label = "Visão de Jogo & Passe", value = player.skills.vision, xp = player.skills.visionXp)
          SkillEvolutionBar(label = "Velocidade & Arranque", value = player.skills.pace, xp = player.skills.paceXp)
          SkillEvolutionBar(label = "Curva & Efeito", value = player.skills.curl, xp = player.skills.curlXp)
          SkillEvolutionBar(label = "Bolas Paradas", value = player.skills.freeKick, xp = player.skills.freeKickXp)

          Spacer(modifier = Modifier.height(10.dp))

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFF142416))
              .padding(horizontal = 12.dp, vertical = 8.dp)
          ) {
            Text(
              text = "⚡ Resistência & Energia: Redução de ${(player.staminaFatigueReduction * 100).toInt()}% no desgaste pós-partida. Fôlego alto permite disputar várias partidas em sequência.",
              color = EnergyBlue,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }
    }

    // 3. Relationships & Social Life
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("player_relationships_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "VIDA PESSOAL & RELACIONAMENTOS",
            color = ChampionGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
          Text(
            text = "Gaste energia em encontros sociais para manter a moral alta!",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
          )

          Spacer(modifier = Modifier.height(14.dp))

          RelationshipItem(
            label = "Treinador",
            value = player.relationships.coach,
            icon = Icons.Default.Person,
            actionLabel = "Conversar Tática",
            onAction = { onSocialAction("coach") }
          )

          RelationshipItem(
            label = "Companheiros",
            value = player.relationships.teammates,
            icon = Icons.Default.Groups,
            actionLabel = "Churrasco Elenco",
            onAction = { onSocialAction("teammates") }
          )

          RelationshipItem(
            label = "Torcida",
            value = player.relationships.fans,
            icon = Icons.Default.Sports,
            actionLabel = "Sessão Autógrafos",
            onAction = { onSocialAction("fans") }
          )

          RelationshipItem(
            label = "Patrocinador",
            value = player.relationships.sponsor,
            icon = Icons.Default.BusinessCenter,
            actionLabel = "Gravar Comercial",
            onAction = { onSocialAction("sponsor") }
          )

          RelationshipItem(
            label = "Família / Namorada",
            value = player.relationships.partner,
            icon = Icons.Default.Favorite,
            actionLabel = "Jantar Romântico",
            onAction = { onSocialAction("partner") }
          )
        }
      }
    }

    // 4. Career History Totals & Restart
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("career_totals_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "HISTÓRICO NA CARREIRA",
            color = ChampionGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "${player.matchesPlayed}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
              Text(text = "Partidas", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "${player.goalsScored}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
              Text(text = "Gols", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "${player.assistsGiven}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
              Text(text = "Assistências", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "${player.trophiesWon}", color = ChampionGold, fontWeight = FontWeight.Bold, fontSize = 18.sp)
              Text(text = "Títulos", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          OutlinedButton(
            onClick = onResetCareerClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
              contentColor = Color.White.copy(alpha = 0.8f)
            )
          ) {
            Text(text = "NOVA CARREIRA / EDITAR JOGADOR", fontSize = 12.sp)
          }
        }
      }
    }
  }
}

@Composable
private fun SkillEvolutionBar(label: String, value: Int, xp: Int) {
  Column(modifier = Modifier.padding(vertical = 5.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(text = label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "$xp/100 XP", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "$value", color = ChampionGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
      }
    }
    Spacer(modifier = Modifier.height(3.dp))
    LinearProgressIndicator(
      progress = { (value / 99f).coerceIn(0f, 1f) },
      modifier = Modifier
        .fillMaxWidth()
        .height(6.dp)
        .clip(RoundedCornerShape(3.dp)),
      color = StadiumGreenLight,
      trackColor = Color(0xFF26382A)
    )
  }
}

@Composable
private fun RelationshipItem(
  label: String,
  value: Int,
  icon: ImageVector,
  actionLabel: String,
  onAction: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = ChampionGold,
      modifier = Modifier.size(20.dp)
    )

    Spacer(modifier = Modifier.width(10.dp))

    Column(modifier = Modifier.weight(1f)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(text = label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Text(text = "$value%", color = if (value > 60) StadiumGreenLight else ChampionGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
      Spacer(modifier = Modifier.height(3.dp))
      LinearProgressIndicator(
        progress = { value / 100f },
        modifier = Modifier
          .fillMaxWidth()
          .height(5.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = if (value > 60) StadiumGreenLight else ChampionGold,
        trackColor = Color(0xFF26382A)
      )
    }

    Spacer(modifier = Modifier.width(10.dp))

    OutlinedButton(
      onClick = onAction,
      shape = RoundedCornerShape(8.dp),
      contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
      colors = ButtonDefaults.outlinedButtonColors(
        contentColor = Color.White
      )
    ) {
      Text(text = actionLabel, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
  }
}
