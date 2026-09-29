package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Club
import com.example.model.ClubContractOffer
import com.example.model.Player
import com.example.model.ScoutDrillType
import com.example.model.ScoutTrialEngine
import com.example.ui.theme.BallBlack
import com.example.ui.theme.CardRed
import com.example.ui.theme.ChampionGold
import com.example.ui.theme.EnergyBlue
import com.example.ui.theme.GoldDark
import com.example.ui.theme.StadiumGreenDark
import com.example.ui.theme.StadiumGreenLight

@Composable
fun ScoutTrialScreen(
  player: Player,
  availableClubs: List<Club>,
  onSignContract: (offer: ClubContractOffer) -> Unit,
  modifier: Modifier = Modifier
) {
  var currentStep by remember { mutableIntStateOf(0) } // 0: intro, 1: shooting, 2: passing, 3: agility, 4: offers
  var shootingScore by remember { mutableIntStateOf(0) }
  var passingScore by remember { mutableIntStateOf(0) }
  var agilityScore by remember { mutableIntStateOf(0) }
  var totalScoutScore by remember { mutableIntStateOf(0) }
  var contractOffers by remember { mutableStateOf<List<ClubContractOffer>>(emptyList()) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            Color(0xFF071209),
            Color(0xFF0F2615),
            Color(0xFF07140B)
          )
        )
      )
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 18.dp),
      contentPadding = PaddingValues(top = 36.dp, bottom = 48.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Header Banner
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
              Column {
                Text(
                  text = "PENEIRA OFICIAL DE OLHEIROS",
                  color = ChampionGold,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp
                )
                Text(
                  text = "${player.name} (${player.age} anos) • ${player.position.label}",
                  color = Color.White,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              Box(
                modifier = Modifier
                  .size(46.dp)
                  .clip(CircleShape)
                  .background(Color(0xFF1B311E))
                  .border(1.dp, ChampionGold, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Visibility,
                  contentDescription = null,
                  tint = ChampionGold,
                  modifier = Modifier.size(24.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar across 3 drills
            val progressFraction = when (currentStep) {
              0 -> 0.05f
              1 -> 0.33f
              2 -> 0.66f
              3 -> 0.95f
              else -> 1.0f
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = if (currentStep < 4) "Progresso da Avaliação" else "Propostas Recebidas",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
              )
              Text(
                text = "${(progressFraction * 100).toInt()}%",
                color = ChampionGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
              progress = { progressFraction },
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
              color = ChampionGold,
              trackColor = Color(0xFF26382A)
            )
          }
        }
      }

      // Step 0: Welcome & Instructions
      if (currentStep == 0) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(modifier = Modifier.padding(20.dp)) {
              Text(
                text = "\"Mostre sua raça, garoto!\"",
                color = ChampionGold,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "Você inicia sua jornada como um atleta sem clube aos ${player.age} anos. Olheiros de clubes profissionais estão na arquibancada com pranchetas nas mãos para avaliar seus 3 testes fundamentais:\n\n1. Finalização & Pontaria\n2. Passe & Visão de Jogo\n3. Circuito Físico & Fôlego\n\nSua nota final determinará os clubes interessados e o valor do seu primeiro salário profissional!",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                lineHeight = 18.sp
              )

              Spacer(modifier = Modifier.height(18.dp))

              Button(
                onClick = { currentStep = 1 },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(50.dp)
                  .testTag("start_trial_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = ChampionGold,
                  contentColor = BallBlack
                )
              ) {
                Text(
                  text = "INICIAR TESTE 1: FINALIZAÇÃO",
                  fontWeight = FontWeight.Black,
                  fontSize = 13.sp
                )
              }
            }
          }
        }
      }

      // Step 1: Shooting Drill
      if (currentStep == 1) {
        item {
          ScoutDrillInteractiveCard(
            drillType = ScoutDrillType.SHOOTING,
            player = player,
            onPerform = { quality ->
              val score = ScoutTrialEngine.calculateDrillScore(ScoutDrillType.SHOOTING, player, quality)
              shootingScore = score
              currentStep = 2
            }
          )
        }
      }

      // Step 2: Passing Drill
      if (currentStep == 2) {
        item {
          ScoutDrillInteractiveCard(
            drillType = ScoutDrillType.PASSING,
            player = player,
            onPerform = { quality ->
              val score = ScoutTrialEngine.calculateDrillScore(ScoutDrillType.PASSING, player, quality)
              passingScore = score
              currentStep = 3
            }
          )
        }
      }

      // Step 3: Agility & Fitness Drill
      if (currentStep == 3) {
        item {
          ScoutDrillInteractiveCard(
            drillType = ScoutDrillType.AGILITY,
            player = player,
            onPerform = { quality ->
              val score = ScoutTrialEngine.calculateDrillScore(ScoutDrillType.AGILITY, player, quality)
              agilityScore = score
              val finalScore = ((shootingScore * 0.4f) + (passingScore * 0.35f) + (score * 0.25f)).toInt()
              totalScoutScore = finalScore
              contractOffers = ScoutTrialEngine.generateTrialOffers(player, availableClubs, finalScore)
              currentStep = 4
            }
          )
        }
      }

      // Step 4: Final Evaluation & Contract Proposals
      if (currentStep == 4) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(modifier = Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
              Box(
                modifier = Modifier
                  .size(60.dp)
                  .clip(CircleShape)
                  .background(ChampionGold.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.MilitaryTech,
                  contentDescription = null,
                  tint = ChampionGold,
                  modifier = Modifier.size(36.dp)
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              Text(
                text = "PONTUAÇÃO DO OLHEIRO: $totalScoutScore / 100",
                color = ChampionGold,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
              )

              Row(modifier = Modifier.padding(vertical = 4.dp)) {
                val stars = (totalScoutScore / 20).coerceIn(1, 5)
                for (i in 1..5) {
                  Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = if (i <= stars) ChampionGold else Color(0xFF333333),
                    modifier = Modifier.size(20.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = "Chute: $shootingScore pts • Passe: $passingScore pts • Físico: $agilityScore pts",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
              )

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = "Com base no seu desempenho, ${contractOffers.size} clubes oficiais enviaram propostas formais de contrato profissional. Escolha seu destino!",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
              )
            }
          }
        }

        // List of Club Contract Offers
        items(contractOffers) { offer ->
          ClubOfferCard(
            offer = offer,
            onSign = { onSignContract(offer) }
          )
        }
      }
    }
  }
}

@Composable
private fun ScoutDrillInteractiveCard(
  drillType: ScoutDrillType,
  player: Player,
  onPerform: (quality: Float) -> Unit
) {
  val infiniteTransition = rememberInfiniteTransition(label = "scout_aim")
  val aimIndicator by infiniteTransition.animateFloat(
    initialValue = 0.05f,
    targetValue = 0.95f,
    animationSpec = infiniteRepeatable(
      animation = tween(800, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "aim"
  )

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(18.dp),
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
            text = drillType.subtitle.uppercase(),
            color = ChampionGold,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = drillType.title,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1D321F))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = "-${drillType.energyCost} HP",
            color = EnergyBlue,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = drillType.instruction,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 13.sp,
        lineHeight = 17.sp
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Tactical Drill Simulation Pitch Graphic & Precision Timing Bar
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(Color(0xFF0D1E12))
          .border(1.dp, Color(0xFF24442A), RoundedCornerShape(14.dp))
          .padding(14.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(
            imageVector = when (drillType) {
              ScoutDrillType.SHOOTING -> Icons.Default.SportsSoccer
              ScoutDrillType.PASSING -> Icons.Default.PlayArrow
              ScoutDrillType.AGILITY -> Icons.Default.FitnessCenter
            },
            contentDescription = null,
            tint = ChampionGold,
            modifier = Modifier.size(34.dp)
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Barômetro de Precisão do Olheiro",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Toque no botão quando a barra estiver no centro verde!",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Precision Timing Gauge
          BoxWithConstraints(
            modifier = Modifier
              .fillMaxWidth()
              .height(28.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(Color(0xFF142416))
              .border(1.dp, Color(0xFF385E3E), RoundedCornerShape(14.dp))
          ) {
            val totalWidth = maxWidth

            // Left Warning Zone
            Box(
              modifier = Modifier
                .width(totalWidth * 0.35f)
                .fillMaxHeight()
                .background(CardRed.copy(alpha = 0.35f))
            )

            // Center Sweet Spot (Zona de Ouro)
            Box(
              modifier = Modifier
                .offset(x = totalWidth * 0.35f)
                .width(totalWidth * 0.30f)
                .fillMaxHeight()
                .background(StadiumGreenLight.copy(alpha = 0.65f)),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "ALVO / GAVETA",
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black
              )
            }

            // Right Warning Zone
            Box(
              modifier = Modifier
                .offset(x = totalWidth * 0.65f)
                .width(totalWidth * 0.35f)
                .fillMaxHeight()
                .background(CardRed.copy(alpha = 0.35f))
            )

            // Dynamic Aim Needle Cursor
            val needleOffset = totalWidth * aimIndicator - 4.dp
            Box(
              modifier = Modifier
                .offset(x = needleOffset.coerceAtLeast(0.dp))
                .width(8.dp)
                .fillMaxHeight()
                .background(Color.White, RoundedCornerShape(4.dp))
                .border(1.5.dp, ChampionGold, RoundedCornerShape(4.dp))
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Button(
        onClick = {
          val dist = kotlin.math.abs(aimIndicator - 0.5f)
          val quality = when {
            dist <= 0.12f -> 0.98f // Perfect bullseye!
            dist <= 0.22f -> 0.78f // Good!
            dist <= 0.32f -> 0.55f // Average
            else -> 0.32f // Weak / Missed sweet spot
          }
          onPerform(quality)
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("execute_drill_${drillType.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = StadiumGreenLight,
          contentColor = Color.White
        )
      ) {
        Icon(
          imageVector = Icons.Default.CheckCircle,
          contentDescription = null,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "EXECUTAR EXERCÍCIO",
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp
        )
      }
    }
  }
}

@Composable
private fun ClubOfferCard(
  offer: ClubContractOffer,
  onSign: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(Color(offer.club.primaryColorHex)),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = offer.club.shortName.take(3),
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 13.sp
          )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = offer.club.name,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "${offer.squadRole} • ${offer.contractYears} Anos",
            color = ChampionGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "\"${offer.scoutNote}\"",
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 11.sp,
        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
      )

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text(text = "Salário / Jogo", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
          Text(text = "R$ ${offer.wagePerMatch}", color = ChampionGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        Column {
          Text(text = "Bônus Vitória", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
          Text(text = "R$ ${offer.winBonus}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        Column {
          Text(text = "Bônus Gol", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
          Text(text = "R$ ${offer.goalBonus}", color = EnergyBlue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Button(
        onClick = onSign,
        modifier = Modifier
          .fillMaxWidth()
          .height(46.dp)
          .testTag("sign_contract_${offer.club.id}"),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = ChampionGold,
          contentColor = BallBlack
        )
      ) {
        Text(
          text = "ASSINAR COM O ${offer.club.shortName.uppercase()}",
          fontWeight = FontWeight.Black,
          fontSize = 12.sp,
          letterSpacing = 0.5.sp
        )
      }
    }
  }
}
