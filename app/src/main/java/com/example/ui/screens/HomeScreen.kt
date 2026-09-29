package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalDrink
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Club
import com.example.model.Player
import com.example.ui.theme.CardRed
import com.example.ui.theme.ChampionGold
import com.example.ui.theme.EnergyBlue
import com.example.ui.theme.GoldDark
import com.example.ui.theme.StadiumGreenDark
import com.example.ui.theme.StadiumGreenLight
import com.example.ui.theme.StadiumGreenPrimary

@Composable
fun HomeScreen(
  player: Player,
  playerClub: Club,
  nextOpponent: Club,
  onPlayMatchClick: () -> Unit,
  onQuickDrinkClick: () -> Unit,
  onViewStandingsClick: () -> Unit,
  onGoToStartMenuClick: (() -> Unit)? = null,
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
    // 0. Top Bar Quick Action: Back to Main Menu
    if (onGoToStartMenuClick != null) {
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "CENTRAL DA CARREIRA",
            color = ChampionGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
          Surface(
            onClick = onGoToStartMenuClick,
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF142617),
            modifier = Modifier.testTag("back_to_start_menu_button")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.SportsSoccer,
                contentDescription = null,
                tint = ChampionGold,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "MENU INICIAL",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }

    // 1. Header Card: Player Profile & Quick Bank/Energy
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("player_overview_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              // Jersey Number Badge
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
                  fontSize = 24.sp,
                  fontWeight = FontWeight.Black
                )
              }

              Spacer(modifier = Modifier.width(14.dp))

              Column {
                Text(
                  text = player.name,
                  color = Color.White,
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "${playerClub.name} • ${player.position.label} • ${player.age} anos",
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 13.sp
                )
              }
            }

            // Overall & Stars
            Column(horizontalAlignment = Alignment.End) {
              Surface(
                color = ChampionGold.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = ChampionGold,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "${player.skills.overall} OVR",
                    color = ChampionGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                  )
                }
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "R$ ${player.money}",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Energy Bar
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = if (player.isExhausted) CardRed else EnergyBlue,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Energia: ${player.energy}/${player.maxEnergy} HP",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
              )
            }

            if (player.energy < (player.maxEnergy * 0.7f)) {
              Row(
                modifier = Modifier
                  .clip(RoundedCornerShape(12.dp))
                  .background(EnergyBlue.copy(alpha = 0.2f))
                  .clickable { onQuickDrinkClick() }
                  .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.LocalDrink,
                  contentDescription = null,
                  tint = EnergyBlue,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Recuperar",
                  color = EnergyBlue,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          LinearProgressIndicator(
            progress = { player.energyPercentage },
            modifier = Modifier
              .fillMaxWidth()
              .height(8.dp)
              .clip(RoundedCornerShape(4.dp)),
            color = if (player.isExhausted) CardRed else StadiumGreenLight,
            trackColor = Color(0xFF2C3E30)
          )
        }
      }
    }

    // 2. Next Fixture Banner Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("next_fixture_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "PRÓXIMO CONFRONTO",
              color = ChampionGold,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
            Text(
              text = "Rodada ${player.seasonMatches + 1}",
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 12.sp
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Clubs Matchup
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Home Club
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Box(
                modifier = Modifier
                  .size(52.dp)
                  .clip(CircleShape)
                  .background(Color(playerClub.primaryColorHex)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = playerClub.shortName,
                  color = Color.White,
                  fontWeight = FontWeight.Black,
                  fontSize = 16.sp
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = playerClub.name,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Mandante",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
              )
            }

            Text(
              text = "VS",
              color = ChampionGold,
              fontSize = 22.sp,
              fontWeight = FontWeight.Black
            )

            // Away Club
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Box(
                modifier = Modifier
                  .size(52.dp)
                  .clip(CircleShape)
                  .background(Color(nextOpponent.primaryColorHex)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = nextOpponent.shortName,
                  color = Color.White,
                  fontWeight = FontWeight.Black,
                  fontSize = 16.sp
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = nextOpponent.name,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Visitante",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = "Estádio: ${playerClub.stadiumName}",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
          )
          Text(
            text = "Status: ${player.coachConfidenceText}",
            color = ChampionGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )

          Spacer(modifier = Modifier.height(16.dp))

          Button(
            onClick = onPlayMatchClick,
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("play_match_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = StadiumGreenLight,
              contentColor = Color.White
            )
          ) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = null,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "ENTRAR EM CAMPO",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }

    // 3. Quick Stats & Standings Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onViewStandingsClick() }
          .testTag("season_stats_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "ESTATÍSTICAS DA TEMPORADA",
              color = ChampionGold,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
            Icon(
              imageVector = Icons.Default.EmojiEvents,
              contentDescription = null,
              tint = ChampionGold,
              modifier = Modifier.size(18.dp)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            StatItem(label = "Jogos", value = "${player.seasonMatches}")
            StatItem(label = "Gols", value = "${player.seasonGoals}")
            StatItem(label = "Assist.", value = "${player.seasonAssists}")
            StatItem(label = "MVPs", value = "${player.mvpAwards}")
          }
        }
      }
    }
  }
}

@Composable
private fun StatItem(label: String, value: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = value,
      color = Color.White,
      fontSize = 20.sp,
      fontWeight = FontWeight.Bold
    )
    Text(
      text = label,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      fontSize = 12.sp
    )
  }
}
