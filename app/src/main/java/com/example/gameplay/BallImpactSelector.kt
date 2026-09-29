package com.example.gameplay

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.North
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.BallBlack
import com.example.ui.theme.CardRed
import com.example.ui.theme.ChampionGold
import com.example.ui.theme.EnergyBlue
import com.example.ui.theme.GoldDark
import com.example.ui.theme.StadiumGreenDark
import com.example.ui.theme.TurfLawnDark
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Authentic New Star Soccer (NSS) Ball Strike Modal:
 * Triggered after setting the aim trajectory on the pitch.
 * The player sees a close-up of the rotating football and taps where to strike:
 * - Bottom: Elevation / Chip / Cavadinha (flies OVER defenders)
 * - Sides: Banana Swerve / Efeito Magnus (curves around defenders and keeper)
 * - Center: High-speed flat bullet
 */
@Composable
fun NssBallStrikeDialog(
  initialImpact: Offset,
  playerCurlSkill: Int,
  playerAccuracySkill: Int,
  onImpactConfirmed: (Offset) -> Unit,
  onCancel: () -> Unit
) {
  var impactPoint by remember { mutableStateOf(initialImpact) }

  // Animated subtle spin of the ball preview to evoke NSS realism
  val infiniteTransition = rememberInfiniteTransition(label = "nss_ball_spin")
  val wobbleAngle by infiniteTransition.animateFloat(
    initialValue = -3f,
    targetValue = 3f,
    animationSpec = infiniteRepeatable(
      animation = tween(1400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "wobble"
  )

  Dialog(
    onDismissRequest = onCancel,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color(0xD906140A)),
      contentAlignment = Alignment.Center
    ) {
      Card(
        modifier = Modifier
          .fillMaxWidth(0.92f)
          .clip(RoundedCornerShape(24.dp))
          .border(2.dp, ChampionGold.copy(alpha = 0.8f), RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2414))
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 22.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Header Badge
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.SportsSoccer,
              contentDescription = null,
              tint = ChampionGold,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "PONTO DE CONTATO NA BOLA",
              color = ChampionGold,
              fontSize = 13.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 1.2.sp
            )
          }

          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Toque na bola para definir a altura e o efeito do chute",
            color = Color(0xFFA5C4AC),
            fontSize = 12.sp,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(18.dp))

          // Large Interactive 3D Soccer Ball
          val ballDiameter = 210.dp
          Box(
            modifier = Modifier
              .size(ballDiameter)
              .clip(CircleShape)
              .background(
                Brush.radialGradient(
                  colors = listOf(Color.White, Color(0xFFE8ECE9), Color(0xFF8B9B8F)),
                  center = Offset(130f, 130f)
                )
              )
              .pointerInput(Unit) {
                detectTapGestures { offset ->
                  val radius = size.width / 2f
                  val dx = (offset.x - radius) / radius
                  val dy = (offset.y - radius) / radius
                  val dist = kotlin.math.sqrt(dx * dx + dy * dy)
                  val clampedDist = min(1f, dist)
                  val angle = atan2(dy, dx)
                  val normX = clampedDist * cos(angle)
                  val normY = clampedDist * sin(angle)
                  impactPoint = Offset(normX, normY)
                }
              }
              .testTag("nss_ball_strike_canvas"),
            contentAlignment = Alignment.Center
          ) {
            Canvas(modifier = Modifier.size(ballDiameter)) {
              val w = size.width
              val h = size.height
              val cx = w / 2f
              val cy = h / 2f
              val r = w / 2f

              rotate(degrees = wobbleAngle, pivot = Offset(cx, cy)) {
                // Football pentagon details
                val centerPentagon = Path().apply {
                  moveTo(cx, cy - 24f)
                  lineTo(cx + 22f, cy - 8f)
                  lineTo(cx + 14f, cy + 20f)
                  lineTo(cx - 14f, cy + 20f)
                  lineTo(cx - 22f, cy - 8f)
                  close()
                }
                drawPath(centerPentagon, color = Color(0xFF1E2022))

                // Corner pentagons
                val seamColor = Color(0xFF333333)
                drawLine(seamColor, Offset(cx, cy - 24f), Offset(cx, cy - 65f), strokeWidth = 3f)
                drawLine(seamColor, Offset(cx + 22f, cy - 8f), Offset(cx + 62f, cy - 22f), strokeWidth = 3f)
                drawLine(seamColor, Offset(cx + 14f, cy + 20f), Offset(cx + 46f, cy + 56f), strokeWidth = 3f)
                drawLine(seamColor, Offset(cx - 14f, cy + 20f), Offset(cx - 46f, cy + 56f), strokeWidth = 3f)
                drawLine(seamColor, Offset(cx - 22f, cy - 8f), Offset(cx - 62f, cy - 22f), strokeWidth = 3f)
              }

              // Subtle crosshair guidelines
              val guideColor = Color(0x33000000)
              drawLine(guideColor, Offset(cx - 30f, cy), Offset(cx + 30f, cy), strokeWidth = 1.5f)
              drawLine(guideColor, Offset(cx, cy - 30f), Offset(cx, cy + 30f), strokeWidth = 1.5f)

              // Outer ball rim
              drawCircle(
                color = Color(0xFF222B24),
                radius = r - 1.5f,
                style = Stroke(width = 4f)
              )

              // Sweet Spot circle based on player technique / accuracy
              val sweetSpotRadius = (35f + (playerAccuracySkill / 100f) * 15f)
              drawCircle(
                color = Color(0x44FFB800),
                radius = sweetSpotRadius,
                center = Offset(cx, cy),
                style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)))
              )

              // Impact Point indicator
              val targetX = cx + impactPoint.x * (r * 0.82f)
              val targetY = cy + impactPoint.y * (r * 0.82f)

              // Target Reticle with glowing ring and crosshair
              drawCircle(color = Color(0x66FF1744), radius = 18f, center = Offset(targetX, targetY))
              drawCircle(color = Color(0xFFFF1744), radius = 10f, center = Offset(targetX, targetY))
              drawCircle(color = Color.White, radius = 4f, center = Offset(targetX, targetY))

              // Target cross lines
              drawLine(Color.White, Offset(targetX - 12f, targetY), Offset(targetX + 12f, targetY), strokeWidth = 2f)
              drawLine(Color.White, Offset(targetX, targetY - 12f), Offset(targetX, targetY + 12f), strokeWidth = 2f)
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Trajectory Description Badge
          val (descriptionText, effectColor, icon) = when {
            impactPoint.y > 0.35f && abs(impactPoint.x) < 0.25f ->
              Triple("Cavadinha / Balãozinho (Passa por cima dos zagueiros!)", EnergyBlue, Icons.Default.North)
            impactPoint.y > 0.35f && impactPoint.x < -0.25f ->
              Triple("Cavadinha com Curva à Direita", ChampionGold, Icons.Default.TrendingUp)
            impactPoint.y > 0.35f && impactPoint.x > 0.25f ->
              Triple("Cavadinha com Curva à Esquerda", ChampionGold, Icons.Default.TrendingUp)
            impactPoint.y < -0.35f && abs(impactPoint.x) < 0.25f ->
              Triple("Bomba Rasante / Foguete Rente ao Gramado", CardRed, Icons.Default.Bolt)
            impactPoint.x < -0.30f ->
              Triple("Chute de Chapa / Trivela (Curva Acertada à Direita)", ChampionGold, Icons.Default.RotateRight)
            impactPoint.x > 0.30f ->
              Triple("Chute Colocado (Curva Acertada à Esquerda)", ChampionGold, Icons.Default.RotateRight)
            else ->
              Triple("Chute Firme Equilibrado no Meio da Bola", Color.White, Icons.Default.Adjust)
          }

          Surface(
            color = Color(0xFF142E1B),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(imageVector = icon, contentDescription = null, tint = effectColor, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = descriptionText,
                color = effectColor,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
              )
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Action Buttons: Cancel and Kick!
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = onCancel,
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF203625)),
              shape = RoundedCornerShape(14.dp)
            ) {
              Text("VOLTAR", color = Color(0xFFA5C4AC), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
              onClick = { onImpactConfirmed(impactPoint) },
              modifier = Modifier
                .weight(1.8f)
                .testTag("confirm_strike_button"),
              colors = ButtonDefaults.buttonColors(containerColor = ChampionGold),
              shape = RoundedCornerShape(14.dp)
            ) {
              Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = BallBlack, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "BATER NA BOLA!",
                color = BallBlack,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black
              )
            }
          }
        }
      }
    }
  }
}
