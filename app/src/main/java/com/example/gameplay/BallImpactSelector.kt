package com.example.gameplay

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ChampionGold

/**
 * The Signature New Star Soccer mechanic:
 * Tap or drag on the football to choose where your boot makes contact.
 * - Bottom: lifts the ball into the air (lob / height).
 * - Left / Right: adds swerve / curl (banana kick).
 * - Center: flat, high-velocity bullet drive.
 */
@Composable
fun BallImpactSelector(
  impactPoint: Offset, // Normalized between -1f and 1f (0,0 is center)
  onImpactChange: (Offset) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = "PONTO DE IMPACTO NA BOLA",
      color = ChampionGold,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 1.sp
    )

    Spacer(modifier = Modifier.height(4.dp))

    Box(
      modifier = Modifier
        .size(110.dp)
        .clip(CircleShape)
        .background(
          Brush.radialGradient(
            colors = listOf(Color.White, Color(0xFFE0E0E0), Color(0xFF9E9E9E)),
            center = Offset(45f, 45f)
          )
        )
        .pointerInput(enabled) {
          if (!enabled) return@pointerInput
          detectTapGestures(
            onPress = { offset ->
              val radius = size.width / 2f
              val dx = (offset.x - radius) / radius
              val dy = (offset.y - radius) / radius
              val dist = kotlin.math.sqrt(dx * dx + dy * dy)
              val clampedDist = kotlin.math.min(1f, dist)
              val angle = kotlin.math.atan2(dy, dx)
              val normX = clampedDist * kotlin.math.cos(angle)
              val normY = clampedDist * kotlin.math.sin(angle)
              onImpactChange(Offset(normX, normY))
            }
          )
        }
        .testTag("ball_impact_selector"),
      contentAlignment = Alignment.Center
    ) {
      Canvas(modifier = Modifier.size(110.dp)) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val r = w / 2f

        // Football pentagon details
        val centerPentagon = Path().apply {
          moveTo(cx, cy - 14f)
          lineTo(cx + 13f, cy - 5f)
          lineTo(cx + 8f, cy + 12f)
          lineTo(cx - 8f, cy + 12f)
          lineTo(cx - 13f, cy - 5f)
          close()
        }
        drawPath(centerPentagon, color = Color(0xFF212121))

        // Seams
        val seamColor = Color(0xFF424242)
        drawLine(seamColor, Offset(cx, cy - 14f), Offset(cx, cy - 35f), strokeWidth = 2f)
        drawLine(seamColor, Offset(cx + 13f, cy - 5f), Offset(cx + 34f, cy - 14f), strokeWidth = 2f)
        drawLine(seamColor, Offset(cx + 8f, cy + 12f), Offset(cx + 25f, cy + 30f), strokeWidth = 2f)
        drawLine(seamColor, Offset(cx - 8f, cy + 12f), Offset(cx - 25f, cy + 30f), strokeWidth = 2f)
        drawLine(seamColor, Offset(cx - 13f, cy - 5f), Offset(cx - 34f, cy - 14f), strokeWidth = 2f)

        // Outer border
        drawCircle(
          color = Color(0xFF333333),
          radius = r - 1f,
          style = Stroke(width = 3f)
        )

        // Center crosshair reference (subtle)
        drawLine(
          color = Color(0x44000000),
          start = Offset(cx - 15f, cy),
          end = Offset(cx + 15f, cy),
          strokeWidth = 1f
        )
        drawLine(
          color = Color(0x44000000),
          start = Offset(cx, cy - 15f),
          end = Offset(cx, cy + 15f),
          strokeWidth = 1f
        )

        // Impact target crosshair / red boot mark
        val targetX = cx + impactPoint.x * (r * 0.82f)
        val targetY = cy + impactPoint.y * (r * 0.82f)

        // Glowing outer indicator
        drawCircle(
          color = Color(0x88FF1744),
          radius = 12f,
          center = Offset(targetX, targetY)
        )
        drawCircle(
          color = Color(0xFFFF1744),
          radius = 7f,
          center = Offset(targetX, targetY)
        )
        drawCircle(
          color = Color.White,
          radius = 3f,
          center = Offset(targetX, targetY)
        )
      }
    }

    Spacer(modifier = Modifier.height(2.dp))

    // Helper text describing the effect
    val effectDescription = when {
      impactPoint.y > 0.4f && kotlin.math.abs(impactPoint.x) < 0.3f -> "Cavadinha / Chute por Cima"
      impactPoint.y < -0.4f && kotlin.math.abs(impactPoint.x) < 0.3f -> "Bomba Rasante"
      impactPoint.x < -0.3f -> "Efeito para a Direita"
      impactPoint.x > 0.3f -> "Efeito para a Esquerda"
      else -> "Chute Equilibrado"
    }

    Text(
      text = effectDescription,
      color = Color.White.copy(alpha = 0.85f),
      fontSize = 11.sp,
      fontWeight = FontWeight.Medium
    )
  }
}
