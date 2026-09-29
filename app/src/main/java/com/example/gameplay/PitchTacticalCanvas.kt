package com.example.gameplay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.North
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gameplay.physics.SoccerPhysicsEngine
import com.example.model.Club
import com.example.model.MatchChance
import com.example.model.MatchChanceType
import com.example.model.PitchPlayer
import com.example.model.Player
import com.example.model.TacticalActionType
import com.example.ui.theme.BallBlack
import com.example.ui.theme.ChalkWhite
import com.example.ui.theme.ChampionGold
import com.example.ui.theme.TurfLawnDark
import com.example.ui.theme.TurfLawnLight
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

enum class ShotOutcome {
  GOAL,
  SAVED,
  POST,
  WIDE,
  PASS_COMPLETE,
  INTERCEPTED,
  TACKLE_WON,
  TACKLE_LOST
}

@Composable
fun PitchTacticalCanvas(
  chance: MatchChance,
  userPlayer: Player,
  homeClub: Club,
  awayClub: Club,
  isExecutingShot: Boolean,
  onShotFinished: (ShotOutcome) -> Unit,
  onTriggerShotWithImpact: (impactOffset: Offset, aimPower: Float, aimAngleRad: Float) -> Unit,
  selectedAction: TacticalActionType = TacticalActionType.POWER_SHOT,
  modifier: Modifier = Modifier
) {
  // Physics engine instance for this chance
  val physics = remember(chance.id) { SoccerPhysicsEngine() }

  // Phase 1: Aiming state on pitch
  var aimTarget by remember(chance.id) {
    mutableStateOf(Offset(chance.ballPosition.first, chance.ballPosition.second - 0.28f))
  }
  var aimPower by remember(chance.id) { mutableFloatStateOf(0.80f) }

  // Phase 2: NSS Ball Strike Modal
  var showStrikeModal by remember(chance.id) { mutableStateOf(false) }
  var activeImpactOffset by remember(chance.id) { mutableStateOf(Offset(0f, 0.25f)) }

  // Goalkeeper dynamic state
  var gkX by remember(chance.id) { mutableFloatStateOf(chance.goalkeeper.x) }
  var gkY by remember(chance.id) { mutableFloatStateOf(chance.goalkeeper.y) }
  var gkDiveAngle by remember(chance.id) { mutableFloatStateOf(0f) }
  var gkArmStretch by remember(chance.id) { mutableFloatStateOf(0f) }

  // Live telemetry display
  var currentSpeedDisplay by remember(chance.id) { mutableFloatStateOf(0f) }
  var showSpeedBadge by remember(chance.id) { mutableStateOf(false) }

  // Idle goalkeeper patrol when waiting for shot
  LaunchedEffect(chance.id, isExecutingShot) {
    if (!isExecutingShot) {
      while (true) {
        val targetX = 0.50f + (if (kotlin.random.Random.nextBoolean()) 0.045f else -0.045f)
        val steps = 24
        val dx = (targetX - gkX) / steps
        for (i in 0 until steps) {
          gkX += dx
          delay(45)
        }
        delay(250)
      }
    }
  }

  // 60 FPS Physics Simulation Loop when shot is triggered
  LaunchedEffect(isExecutingShot) {
    if (!isExecutingShot) {
      physics.isBallInPlay = false
      showSpeedBadge = false
      return@LaunchedEffect
    }

    val effectivePower = (userPlayer.skills.power * selectedAction.powerMultiplier).toInt().coerceIn(10, 99)
    val effectiveAccuracy = (userPlayer.skills.accuracy + selectedAction.accuracyBonus).coerceIn(10, 99)
    val effectiveCurl = (userPlayer.skills.curl + selectedAction.curlBonus).coerceIn(10, 99)

    physics.launchShot(
      startX = chance.ballPosition.first,
      startY = chance.ballPosition.second,
      targetX = aimTarget.x,
      targetY = aimTarget.y,
      impactOffset = activeImpactOffset,
      powerInput = aimPower,
      playerPower = effectivePower,
      playerCurl = effectiveCurl,
      playerAccuracy = effectiveAccuracy
    )

    currentSpeedDisplay = physics.shotSpeedKmH
    showSpeedBadge = true

    val goalLineY = 0.14f
    val predictedGkTargetX = (aimTarget.x + (activeImpactOffset.x * -0.05f)).coerceIn(0.32f, 0.68f)
    val keeperDivingRight = predictedGkTargetX > gkX

    var outcome: ShotOutcome? = null
    var frameCount = 0
    var lastTimeNanos = 0L

    while (outcome == null) {
      withFrameNanos { nowNanos ->
        val dt = if (lastTimeNanos == 0L) 0.016f else ((nowNanos - lastTimeNanos) / 1_000_000_000f).coerceIn(0.008f, 0.033f)
        lastTimeNanos = nowNanos

        frameCount++
        physics.update(dt)

        // Goalkeeper dive logic
        if (physics.ballY < 0.45f && chance.type != MatchChanceType.PASS_TO_TEAMMATE) {
          val diveSpeed = 0.0065f
          val targetAngle = if (keeperDivingRight) 55f else -55f
          gkDiveAngle += (targetAngle - gkDiveAngle) * 0.18f
          gkArmStretch = (gkArmStretch + 0.1f).coerceAtMost(1f)

          val dir = if (predictedGkTargetX > gkX) 1f else -1f
          gkX = (gkX + dir * diveSpeed).coerceIn(0.32f, 0.68f)
        }

        // 1. NSS Ground vs Air Defender Interception:
        // In NSS: If the ball is elevated in the air (ballZ >= 0.12f), it flies OVER ground defenders!
        // Ground defenders can only tackle if ball is on the grass (ballZ < 0.12f).
        if (physics.ballZ < 0.12f) {
          for (defender in chance.defenders) {
            val dist = sqrt((physics.ballX - defender.x) * (physics.ballX - defender.x) + (physics.ballY - defender.y) * (physics.ballY - defender.y))
            if (dist < 0.040f) {
              outcome = if (chance.type == MatchChanceType.TACKLE_INTERCEPTION) ShotOutcome.TACKLE_LOST else ShotOutcome.INTERCEPTED
              break
            }
          }
        }

        // 2. Teammate Reception for Passes (Through-balls & Crosses)
        if (chance.type == MatchChanceType.PASS_TO_TEAMMATE && outcome == null) {
          for (teammate in chance.teammates) {
            val dist = sqrt((physics.ballX - teammate.x) * (physics.ballX - teammate.x) + (physics.ballY - teammate.y) * (physics.ballY - teammate.y))
            if (dist < 0.075f) {
              outcome = ShotOutcome.PASS_COMPLETE
              break
            }
          }
        }

        // 3. Goalkeeper Parry / Save
        if (chance.type != MatchChanceType.PASS_TO_TEAMMATE && chance.type != MatchChanceType.TACKLE_INTERCEPTION && outcome == null) {
          val distToKeeper = sqrt((physics.ballX - gkX) * (physics.ballX - gkX) + (physics.ballY - gkY) * (physics.ballY - gkY))
          if (distToKeeper < 0.052f && physics.ballZ < 0.90f) {
            physics.vx = if (keeperDivingRight) 0.014f else -0.014f
            physics.vy = 0.010f
            outcome = ShotOutcome.SAVED
          }
        }

        // 4. Goal Line Crossing, Posts & Crossbar
        if (outcome == null && physics.ballY <= goalLineY) {
          val goalLeft = chance.goalBounds.first
          val goalRight = chance.goalBounds.second
          val isHitPost = abs(physics.ballX - goalLeft) <= 0.022f || abs(physics.ballX - goalRight) <= 0.022f
          val isOverCrossbar = physics.ballZ > 1.25f

          if (isHitPost && !isOverCrossbar) {
            physics.vx = -physics.vx * 0.65f
            physics.vy = -physics.vy * 0.40f
            outcome = ShotOutcome.POST
          } else if (physics.ballX in (goalLeft + 0.015f)..(goalRight - 0.015f) && !isOverCrossbar) {
            outcome = ShotOutcome.GOAL
          } else {
            outcome = ShotOutcome.WIDE
          }
        }

        // Safeguard timeout
        if (frameCount > 85 && outcome == null) {
          outcome = if (chance.type == MatchChanceType.PASS_TO_TEAMMATE) ShotOutcome.INTERCEPTED else ShotOutcome.WIDE
        }
      }
    }

    delay(400)
    onShotFinished(outcome ?: ShotOutcome.WIDE)
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF091E0F))
      .pointerInput(chance.id, isExecutingShot) {
        if (isExecutingShot) return@pointerInput
        detectDragGestures(
          onDragStart = { offset ->
            val normX = (offset.x / size.width).coerceIn(0.08f, 0.92f)
            val normY = (offset.y / size.height).coerceIn(0.06f, 0.92f)
            aimTarget = Offset(normX, normY)
          },
          onDrag = { change, _ ->
            change.consume()
            val normX = (change.position.x / size.width).coerceIn(0.08f, 0.92f)
            val normY = (change.position.y / size.height).coerceIn(0.06f, 0.92f)
            aimTarget = Offset(normX, normY)

            val dx = normX - chance.ballPosition.first
            val dy = normY - chance.ballPosition.second
            val distance = sqrt(dx * dx + dy * dy)
            aimPower = (distance * 2.4f).coerceIn(0.35f, 1.0f)
          }
        )
      }
      .testTag("pitch_tactical_canvas")
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height

      // 1. Draw Field Stripes
      val stripeCount = 9
      val stripeHeight = h / stripeCount
      for (i in 0 until stripeCount) {
        drawRect(
          color = if (i % 2 == 0) TurfLawnDark else TurfLawnLight,
          topLeft = Offset(0f, i * stripeHeight),
          size = Size(w, stripeHeight)
        )
      }

      // 2. Chalk Markings
      val chalkColor = ChalkWhite.copy(alpha = 0.92f)
      val chalkStroke = Stroke(width = 3.5f)
      val goalLineY = h * 0.14f

      drawLine(chalkColor, Offset(w * 0.05f, goalLineY), Offset(w * 0.95f, goalLineY), strokeWidth = 4f)
      drawLine(chalkColor, Offset(w * 0.05f, goalLineY), Offset(w * 0.05f, h * 0.96f), strokeWidth = 4f)
      drawLine(chalkColor, Offset(w * 0.95f, goalLineY), Offset(w * 0.95f, h * 0.96f), strokeWidth = 4f)

      // 3. Elastic Mesh Goal Net
      val goalLeft = w * chance.goalBounds.first
      val goalRight = w * chance.goalBounds.second
      val netTopY = h * 0.06f

      for (vert in physics.netVertices) {
        drawCircle(color = Color(0x33FFFFFF), radius = 2f, center = Offset(vert.currX * w, vert.currY * h))
      }

      for (r in 0 until physics.netRows) {
        for (c in 0 until physics.netCols - 1) {
          val v1 = physics.netVertices[r * physics.netCols + c]
          val v2 = physics.netVertices[r * physics.netCols + c + 1]
          drawLine(Color(0x66FFFFFF), Offset(v1.currX * w, v1.currY * h), Offset(v2.currX * w, v2.currY * h), strokeWidth = 1.8f)
        }
      }

      for (c in 0 until physics.netCols) {
        for (r in 0 until physics.netRows - 1) {
          val v1 = physics.netVertices[r * physics.netCols + c]
          val v2 = physics.netVertices[(r + 1) * physics.netCols + c]
          drawLine(Color(0x66FFFFFF), Offset(v1.currX * w, v1.currY * h), Offset(v2.currX * w, v2.currY * h), strokeWidth = 1.8f)
        }
      }

      // Goal Frame (Posts & Crossbar)
      drawLine(Color.White, Offset(goalLeft, goalLineY), Offset(goalLeft, netTopY), strokeWidth = 6.5f)
      drawLine(Color.White, Offset(goalRight, goalLineY), Offset(goalRight, netTopY), strokeWidth = 6.5f)
      drawLine(Color.White, Offset(goalLeft, netTopY), Offset(goalRight, netTopY), strokeWidth = 6.5f)

      // Penalty Area Box (Grande Área)
      val boxLeft = w * 0.18f
      val boxRight = w * 0.82f
      val boxBottom = h * 0.50f
      drawRect(chalkColor, Offset(boxLeft, goalLineY), Size(boxRight - boxLeft, boxBottom - goalLineY), style = chalkStroke)

      // 6-Yard Box (Pequena Área)
      val smallBoxLeft = w * 0.30f
      val smallBoxRight = w * 0.70f
      val smallBoxBottom = h * 0.28f
      drawRect(chalkColor, Offset(smallBoxLeft, goalLineY), Size(smallBoxRight - smallBoxLeft, smallBoxBottom - goalLineY), style = chalkStroke)

      // Penalty Spot
      drawCircle(chalkColor, radius = 4.5f, center = Offset(w * 0.50f, h * 0.38f))

      // Penalty Arc (Meia-Lua)
      drawArc(chalkColor, 20f, 140f, false, Offset(w * 0.38f, h * 0.32f), Size(w * 0.24f, h * 0.12f), style = chalkStroke)

      // 4. Trajectory Aiming Line (When in Phase 1 aiming)
      if (!isExecutingShot) {
        val start = Offset(chance.ballPosition.first * w, chance.ballPosition.second * h)
        val target = Offset(aimTarget.x * w, aimTarget.y * h)

        // Trajectory with curve preview
        val steps = 24
        var prevPoint = start
        val curlIntensity = -activeImpactOffset.x * 60f * (userPlayer.skills.curl / 100f)

        for (s in 1..steps) {
          val frac = s / steps.toFloat()
          val currX = start.x + (target.x - start.x) * frac + sin(frac * Math.PI.toFloat()) * curlIntensity
          val currY = start.y + (target.y - start.y) * frac
          val point = Offset(currX, currY)

          if (s % 2 == 1) {
            drawLine(ChampionGold.copy(alpha = 0.95f), prevPoint, point, strokeWidth = 4.5f)
          }
          prevPoint = point
        }

        // Aiming Target Reticle
        drawCircle(color = ChampionGold, radius = 17f, center = target, style = Stroke(width = 3.5f))
        drawCircle(color = Color(0x66FFB800), radius = 8f, center = target)
      }

      // 5. Render Speed Motion Trail
      for (tp in physics.trailPoints) {
        val shadowY = tp.y * h
        val ballDrawY = (tp.y - tp.z * 0.16f) * h
        drawCircle(Color.White.copy(alpha = tp.alpha * 0.45f), tp.radius, Offset(tp.x * w, ballDrawY))
      }

      // 6. Render Grass Dust / Particles
      for (p in physics.particles) {
        drawCircle(p.color.copy(alpha = p.alpha), p.size, Offset(p.x * w, p.y * h))
      }

      // 7. Render Teammates with Motion / Direction Arrows
      val homeColor = Color(homeClub.primaryColorHex)
      val awayColor = Color(awayClub.primaryColorHex)

      chance.teammates.forEach { tm ->
        val pos = Offset(tm.x * w, tm.y * h)
        // Reception radius
        drawCircle(
          color = Color(0x66FFFFFF),
          radius = 24f,
          center = pos,
          style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)))
        )
        drawCircle(color = homeColor, radius = 17f, center = pos)
        drawCircle(color = Color.White, radius = 17f, center = pos, style = Stroke(width = 2.5f))

        // Forward run motion indicator
        drawLine(
          color = ChampionGold.copy(alpha = 0.8f),
          start = pos,
          end = Offset(pos.x, pos.y - 28f),
          strokeWidth = 3f
        )
      }

      // 8. Render Defenders with tackle zone
      chance.defenders.forEach { def ->
        val pos = Offset(def.x * w, def.y * h)
        drawCircle(color = awayColor, radius = 17f, center = pos)
        drawCircle(color = Color(0xFF212121), radius = 17f, center = pos, style = Stroke(width = 2.5f))
      }

      // 9. Render Goalkeeper with Dynamic Dive
      val gkPos = Offset(gkX * w, gkY * h)
      rotate(degrees = gkDiveAngle, pivot = gkPos) {
        drawCircle(color = Color(0xFFFFD600), radius = 18f, center = gkPos)
        drawCircle(color = Color.Black, radius = 18f, center = gkPos, style = Stroke(width = 3f))

        val armDist = 18f + (gkArmStretch * 10f)
        val leftGlove = Offset(gkPos.x - armDist, gkPos.y - 8f)
        val rightGlove = Offset(gkPos.x + armDist, gkPos.y - 8f)

        drawCircle(color = Color(0xFFFF6D00), radius = 7f, center = leftGlove)
        drawCircle(color = Color.White, radius = 7f, center = leftGlove, style = Stroke(width = 1.5f))

        drawCircle(color = Color(0xFFFF6D00), radius = 7f, center = rightGlove)
        drawCircle(color = Color.White, radius = 7f, center = rightGlove, style = Stroke(width = 1.5f))
      }

      // 10. Render User Player
      val userPos = Offset(chance.userPosition.first * w, chance.userPosition.second * h)
      drawCircle(color = ChampionGold.copy(alpha = 0.45f), radius = 25f, center = userPos)
      drawCircle(color = homeColor, radius = 18f, center = userPos)
      drawCircle(color = ChampionGold, radius = 18f, center = userPos, style = Stroke(width = 3.5f))

      // 11. Render Authentic New Star Soccer 3D Ball with Elevation and Ground Shadow!
      val curBallX = if (isExecutingShot) physics.ballX else chance.ballPosition.first
      val curBallY = if (isExecutingShot) physics.ballY else chance.ballPosition.second
      val curBallZ = if (isExecutingShot) physics.ballZ else 0f

      // Ground Shadow: stays at (X, Y) on the pitch!
      val shadowPos = Offset(curBallX * w, curBallY * h)
      val shadowRadius = (8.5f * (1f - (curBallZ * 0.4f))).coerceAtLeast(3f)
      drawOval(
        color = Color(0x66000000),
        topLeft = Offset(shadowPos.x - shadowRadius * 1.2f, shadowPos.y - shadowRadius * 0.5f),
        size = Size(shadowRadius * 2.4f, shadowRadius * 1.0f)
      )

      // Ball in Flight: elevated vertically based on altitude (curBallZ)
      // Visual perspective scale: ball expands when high in the air!
      val ballElevationPixels = curBallZ * (h * 0.22f)
      val ballDrawY = (curBallY * h) - ballElevationPixels
      val ballPos = Offset(curBallX * w, ballDrawY)
      val ballRadius = 8.5f + (curBallZ * 10f)

      // Ball body
      drawCircle(color = Color.White, radius = ballRadius, center = ballPos)

      // Rotating Pentagons pattern
      rotate(degrees = physics.rotationAngle, pivot = ballPos) {
        val pentagon = Path().apply {
          val pr = ballRadius * 0.52f
          for (k in 0..4) {
            val a = (k * 72f - 90f) * (Math.PI.toFloat() / 180f)
            val px = ballPos.x + cos(a) * pr
            val py = ballPos.y + sin(a) * pr
            if (k == 0) moveTo(px, py) else lineTo(px, py)
          }
          close()
        }
        drawPath(pentagon, color = Color(0xFF1E2022))
        drawCircle(color = Color(0xFF333333), radius = ballRadius, center = ballPos, style = Stroke(width = 1.5f))
      }
    }

    // Top Speedometer Telemetry Badge
    if (showSpeedBadge) {
      Surface(
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(16.dp),
        color = BallBlack.copy(alpha = 0.85f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, ChampionGold)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(imageVector = Icons.Default.Speed, contentDescription = null, tint = ChampionGold, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "${currentSpeedDisplay.toInt()} km/h",
            color = ChampionGold,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black
          )
        }
      }
    }

    // Phase 1 Bottom Action Bar: "DEFINIR CONTATO DA BOLA >"
    if (!isExecutingShot) {
      Box(
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .padding(bottom = 20.dp)
      ) {
        Button(
          onClick = { showStrikeModal = true },
          colors = ButtonDefaults.buttonColors(containerColor = ChampionGold),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.testTag("open_strike_modal_button")
        ) {
          Icon(imageVector = Icons.Default.SportsSoccer, contentDescription = null, tint = BallBlack, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "ESCOLHER CONTATO NA BOLA >",
            color = BallBlack,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black
          )
        }
      }
    }

    // Phase 2: NSS Ball Strike Modal
    if (showStrikeModal) {
      NssBallStrikeDialog(
        initialImpact = activeImpactOffset,
        playerCurlSkill = userPlayer.skills.curl,
        playerAccuracySkill = userPlayer.skills.accuracy,
        onImpactConfirmed = { impact ->
          activeImpactOffset = impact
          showStrikeModal = false
          val dx = aimTarget.x - chance.ballPosition.first
          val dy = aimTarget.y - chance.ballPosition.second
          val angle = atan2(dy, dx)
          onTriggerShotWithImpact(impact, aimPower, angle)
        },
        onCancel = { showStrikeModal = false }
      )
    }
  }
}
