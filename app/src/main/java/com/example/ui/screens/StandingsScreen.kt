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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Club
import com.example.model.Player
import com.example.ui.theme.ChampionGold
import com.example.ui.theme.StadiumGreenDark

@Composable
fun StandingsScreen(
  clubs: List<Club>,
  player: Player,
  modifier: Modifier = Modifier
) {
  val sortedClubs = clubs.sortedWith(
    compareByDescending<Club> { it.points }
      .thenByDescending { it.goalDifference }
      .thenByDescending { it.goalsFor }
  )

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. League Header
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("standings_header_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "CAMPEONATO NACIONAL",
              color = ChampionGold,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
            Text(
              text = "Tabela de Classificação da Temporada",
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 12.sp
            )
          }

          Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = null,
            tint = ChampionGold,
            modifier = Modifier.size(28.dp)
          )
        }
      }
    }

    // 2. Table Header
    item {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF142417),
        shape = RoundedCornerShape(8.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = "#", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(26.dp))
          Text(text = "CLUBE", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
          Text(text = "P", color = ChampionGold, fontSize = 11.sp, fontWeight = FontWeight.Black, modifier = Modifier.width(28.dp))
          Text(text = "J", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, modifier = Modifier.width(24.dp))
          Text(text = "V", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, modifier = Modifier.width(24.dp))
          Text(text = "SG", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, modifier = Modifier.width(28.dp))
        }
      }
    }

    // 3. Table Rows
    itemsIndexed(sortedClubs) { index, club ->
      val isUserClub = club.id == player.currentClubId
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isUserClub) Color(0xFF1E3821) else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
        border = if (isUserClub) androidx.compose.foundation.BorderStroke(1.dp, ChampionGold) else null
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "${index + 1}",
            color = if (index < 4) ChampionGold else Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(26.dp)
          )

          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .background(Color(club.primaryColorHex)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = club.shortName.take(2),
              color = Color.White,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          Text(
            text = club.name,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = if (isUserClub) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.weight(1f)
          )

          Text(
            text = "${club.points}",
            color = ChampionGold,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.width(28.dp)
          )

          Text(
            text = "${club.played}",
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.width(24.dp)
          )

          Text(
            text = "${club.won}",
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.width(24.dp)
          )

          Text(
            text = "${club.goalDifference}",
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.width(28.dp)
          )
        }
      }
    }

    // 4. Top Scorers Leaderboard
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("top_scorers_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "ARTILHARIA DO CAMPEONATO",
            color = ChampionGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )

          Spacer(modifier = Modifier.height(10.dp))

          ScorerRow(rank = 1, name = "${player.name} (${player.nickname})", club = "Seu Clube", goals = player.seasonGoals, isUser = true)
          ScorerRow(rank = 2, name = "Rodrigo Bala", club = "Estrela Dourada", goals = (player.seasonGoals - 1).coerceAtLeast(3))
          ScorerRow(rank = 3, name = "Matheus Artilheiro", club = "Rubro-Sul AC", goals = (player.seasonGoals - 2).coerceAtLeast(2))
        }
      }
    }
  }
}

@Composable
private fun ScorerRow(rank: Int, name: String, club: String, goals: Int, isUser: Boolean = false) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = "${rank}º",
      color = if (rank == 1) ChampionGold else MaterialTheme.colorScheme.onSurfaceVariant,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.width(24.dp)
    )
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = name,
        color = if (isUser) ChampionGold else Color.White,
        fontWeight = if (isUser) FontWeight.Bold else FontWeight.Normal,
        fontSize = 13.sp
      )
      Text(
        text = club,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 11.sp
      )
    }
    Text(
      text = "$goals gols",
      color = Color.White,
      fontWeight = FontWeight.Bold,
      fontSize = 13.sp
    )
  }
}
