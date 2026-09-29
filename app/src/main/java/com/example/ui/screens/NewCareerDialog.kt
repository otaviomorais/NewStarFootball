package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Club
import com.example.model.PlayerPosition
import com.example.ui.theme.BallBlack
import com.example.ui.theme.ChampionGold
import com.example.ui.theme.EnergyBlue
import com.example.ui.theme.StadiumGreenLight

enum class CareerStartMode(val title: String, val subtitle: String) {
  SCOUT_TRIAL("Peneira de Olheiro", "Modo Difícil: Comece sem clube e faça os testes"),
  RANDOM_CLUB("Clube Aleatório", "O destino escolhe seu clube inicial"),
  MANUAL_CLUB("Escolher Clube", "Selecione diretamente o time de estreia")
}

@Composable
fun NewCareerDialog(
  availableClubs: List<Club>,
  onDismiss: () -> Unit,
  onCreateCareer: (
    name: String,
    nickname: String,
    pos: PlayerPosition,
    foot: String,
    age: Int,
    clubId: String,
    startWithTrial: Boolean
  ) -> Unit
) {
  var name by remember { mutableStateOf("Gabriel") }
  var nickname by remember { mutableStateOf("Craque") }
  var age by remember { mutableIntStateOf(17) }
  var position by remember { mutableStateOf(PlayerPosition.ATACANTE) }
  var foot by remember { mutableStateOf("Destro") }
  var startMode by remember { mutableStateOf(CareerStartMode.SCOUT_TRIAL) }
  var selectedClubId by remember { mutableStateOf(availableClubs.firstOrNull()?.id ?: "vila_real") }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("new_career_dialog"),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        Text(
          text = "NOVA CARREIRA PROFISSIONAL",
          color = ChampionGold,
          fontSize = 17.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 1.sp
        )
        Text(
          text = "Configure seu atleta e escolha a forma de entrada no futebol",
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Nome do Atleta") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ChampionGold,
            unfocusedBorderColor = Color(0xFF384E3C)
          )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = nickname,
          onValueChange = { nickname = it },
          label = { Text("Apelido da Torcida") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ChampionGold,
            unfocusedBorderColor = Color(0xFF384E3C)
          )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Age Selection
        Text(text = "Idade Inicial: $age anos", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf(16, 17, 18, 19, 20).forEach { a ->
            FilterChip(
              selected = age == a,
              onClick = { age = a },
              label = { Text("$a", fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = StadiumGreenLight,
                selectedLabelColor = Color.White
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Position & Foot Selection
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Column(modifier = Modifier.weight(1f)) {
            Text(text = "Posição", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              PlayerPosition.values().forEach { pos ->
                FilterChip(
                  selected = position == pos,
                  onClick = { position = pos },
                  label = { Text(pos.shortLabel, fontSize = 10.sp) },
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = StadiumGreenLight,
                    selectedLabelColor = Color.White
                  )
                )
              }
            }
          }

          Column(modifier = Modifier.weight(1f)) {
            Text(text = "Pé Preferido", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              listOf("Destro", "Canhoto").forEach { f ->
                FilterChip(
                  selected = foot == f,
                  onClick = { foot = f },
                  label = { Text(f, fontSize = 10.sp) },
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = StadiumGreenLight,
                    selectedLabelColor = Color.White
                  )
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Career Entry Mode: Peneira / Olheiro (Modo Difícil) vs Clube Aleatório vs Escolha Manual
        Text(text = "Modo de Início", color = ChampionGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          CareerStartMode.values().forEach { mode ->
            val isSelected = startMode == mode
            Surface(
              onClick = { startMode = mode },
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) ChampionGold else Color(0xFF142416),
              border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) ChampionGold else Color(0xFF2A422D)),
              modifier = Modifier.weight(1f)
            ) {
              Column(
                modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = mode.title,
                  color = if (isSelected) BallBlack else Color.White,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  maxLines = 1
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = startMode.subtitle,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 10.sp
        )

        if (startMode == CareerStartMode.MANUAL_CLUB) {
          Spacer(modifier = Modifier.height(8.dp))
          Text(text = "Escolha seu Clube", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            availableClubs.take(3).forEach { club ->
              FilterChip(
                selected = selectedClubId == club.id,
                onClick = { selectedClubId = club.id },
                label = { Text(club.shortName, fontSize = 10.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = StadiumGreenLight,
                  selectedLabelColor = Color.White
                )
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          TextButton(onClick = onDismiss) {
            Text("CANCELAR", color = MaterialTheme.colorScheme.onSurfaceVariant)
          }

          Spacer(modifier = Modifier.width(8.dp))

          Button(
            onClick = {
              val finalClubId = when (startMode) {
                CareerStartMode.SCOUT_TRIAL -> "" // Free agent! Will receive offers after trial
                CareerStartMode.RANDOM_CLUB -> availableClubs.randomOrNull()?.id ?: "vila_real"
                CareerStartMode.MANUAL_CLUB -> selectedClubId
              }
              val startWithTrial = startMode == CareerStartMode.SCOUT_TRIAL

              onCreateCareer(
                name.ifBlank { "Gabriel" },
                nickname.ifBlank { "Craque" },
                position,
                foot,
                age,
                finalClubId,
                startWithTrial
              )
              onDismiss()
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = ChampionGold,
              contentColor = BallBlack
            )
          ) {
            Text(
              text = if (startMode == CareerStartMode.SCOUT_TRIAL) "IR PARA A PENEIRA" else "COMEÇAR CARREIRA",
              fontWeight = FontWeight.Black,
              fontSize = 12.sp
            )
          }
        }
      }
    }
  }
}
