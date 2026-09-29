package com.example.gameplay.physics

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

data class Particle(
  var x: Float,
  var y: Float,
  var vx: Float,
  var vy: Float,
  var size: Float,
  var color: Color,
  var alpha: Float = 1f,
  var life: Float = 1f,
  var maxLife: Float = 1f
)

data class TrailPoint(
  val x: Float,
  val y: Float,
  val z: Float,
  val radius: Float,
  var alpha: Float = 0.75f
)

data class NetVertex(
  val origX: Float,
  val origY: Float,
  var currX: Float,
  var currY: Float,
  var vx: Float = 0f,
  var vy: Float = 0f
)

/**
 * Authentic New Star Soccer (NSS) Physics Engine:
 * - Parabolic elevation trajectory with authentic ball-shadow distance
 * - Dynamic Magnus Curve that bends cleanly throughout flight
 * - Ground vs Air physics: High lobbed balls (z >= 0.14) fly over ground defenders
 * - Realistic turf bounces with restitution and rolling deceleration
 * - Reactive deformable goal net and metal crossbar/post rebounds
 */
class SoccerPhysicsEngine {

  // Ball 3D state (pitch coordinates normalized 0.0 to 1.0)
  var ballX: Float = 0.5f
  var ballY: Float = 0.72f
  var ballZ: Float = 0f // Height above pitch (0 = grass, > 0 = airborne)

  var vx: Float = 0f
  var vy: Float = 0f
  var vz: Float = 0f

  // Angular velocities / Spin
  var spinZ: Float = 0f // Sidespin (Magnus Curve - Left/Right)
  var spinX: Float = 0f // Topspin / Backspin (Height dip/float)

  var rotationAngle: Float = 0f
  var bounceCount: Int = 0

  // Shot telemetry
  var shotSpeedKmH: Float = 0f
  var isBallInPlay: Boolean = false
  var hasCrossedGoalLine: Boolean = false
  var hasHitPost: Boolean = false

  // Visual effects
  val particles = mutableListOf<Particle>()
  val trailPoints = mutableListOf<TrailPoint>()

  // Elastic Goal Net Grid (9 columns x 5 rows)
  val netCols = 9
  val netRows = 5
  val netVertices = mutableListOf<NetVertex>()

  init {
    resetNet(0.30f, 0.70f, 0.06f, 0.14f)
  }

  fun resetNet(goalLeft: Float, goalRight: Float, netTopY: Float, goalLineY: Float) {
    netVertices.clear()
    for (r in 0 until netRows) {
      val fy = r / (netRows - 1).toFloat()
      val y = netTopY + (goalLineY - netTopY) * fy
      for (c in 0 until netCols) {
        val fx = c / (netCols - 1).toFloat()
        val x = goalLeft + (goalRight - goalLeft) * fx
        netVertices.add(NetVertex(x, y, x, y))
      }
    }
  }

  fun spawnKickParticles(startX: Float, startY: Float, power: Float) {
    particles.clear()
    trailPoints.clear()
    bounceCount = 0
    hasHitPost = false

    val count = (16 + power * 14).toInt()
    val turfColors = listOf(
      Color(0xFF388E3C),
      Color(0xFF2E7D32),
      Color(0xFF81C784),
      Color(0xFF689F38),
      Color(0xFF4CAF50)
    )

    for (i in 0 until count) {
      val angle = Random.nextFloat() * 2 * Math.PI.toFloat()
      val speed = (0.003f + Random.nextFloat() * 0.007f) * (0.8f + power * 0.5f)
      particles.add(
        Particle(
          x = startX + (Random.nextFloat() - 0.5f) * 0.015f,
          y = startY + (Random.nextFloat() - 0.5f) * 0.015f,
          vx = cos(angle) * speed,
          vy = sin(angle) * speed,
          size = 2.5f + Random.nextFloat() * 3.5f,
          color = turfColors.random(),
          alpha = 1f,
          life = 0.45f + Random.nextFloat() * 0.35f,
          maxLife = 0.8f
        )
      )
    }
  }

  /**
   * Launches shot based on:
   * @param startX Initial ball X
   * @param startY Initial ball Y
   * @param targetX Direction target X
   * @param targetY Direction target Y
   * @param impactOffset Contact point on the ball (-1 to +1):
   *        - impactOffset.y > 0 (hit bottom) -> chip / lob into air (high z)
   *        - impactOffset.y < 0 (hit top) -> low bullet / driven ground shot
   *        - impactOffset.x < 0 (hit left) -> curls right
   *        - impactOffset.x > 0 (hit right) -> curls left
   * @param powerInput Normalized power 0.0 to 1.0
   * @param playerPower Player power attribute
   * @param playerCurl Player curve/technique attribute
   * @param playerAccuracy Player accuracy attribute
   */
  fun launchShot(
    startX: Float,
    startY: Float,
    targetX: Float,
    targetY: Float,
    impactOffset: Offset,
    powerInput: Float,
    playerPower: Int = 50,
    playerCurl: Int = 50,
    playerAccuracy: Int = 50,
    playerPowerSkill: Int = playerPower,
    playerCurlSkill: Int = playerCurl,
    playerAccuracySkill: Int = playerAccuracy
  ) {
    val finalPower = if (playerPowerSkill != 50) playerPowerSkill else playerPower
    val finalCurl = if (playerCurlSkill != 50) playerCurlSkill else playerCurl
    val finalAccuracy = if (playerAccuracySkill != 50) playerAccuracySkill else playerAccuracy
    ballX = startX
    ballY = startY
    ballZ = 0.005f

    // 1. Calculate direction vector
    val dx = targetX - startX
    val dy = targetY - startY
    val dist = sqrt(dx * dx + dy * dy)
    val dirX = if (dist > 0.001f) dx / dist else 0f
    val dirY = if (dist > 0.001f) dy / dist else -1f

    // 2. Velocity calculation (speed calibrated for responsive NSS feel)
    val baseSpeed = 0.024f + (finalPower / 100f) * 0.016f
    val totalSpeed = baseSpeed * (0.65f + powerInput * 0.65f)

    // Accurate km/h telemetry
    shotSpeedKmH = (60f + (powerInput * 48f) + (finalPower * 0.32f)).coerceIn(50f, 135f)

    vx = dirX * totalSpeed
    vy = dirY * totalSpeed

    // 3. Vertical Launch velocity (vz) based on NSS bottom impact
    // In NSS: bottom of the ball creates high loft that clears defenders!
    // Center/top creates driven ground balls that defenders can block.
    val liftNormalized = (impactOffset.y + 0.35f).coerceIn(-0.25f, 1.0f)
    if (liftNormalized > 0.1f) {
      vz = 0.012f + (liftNormalized * 0.026f) * (0.8f + powerInput * 0.4f)
    } else {
      // Driven low shot along ground
      vz = 0.002f
    }

    // 4. Magnus Sidespin (spinZ):
    // Off-center impact creates intense curve proportional to technique
    val curveEfficiency = (finalCurl / 100f) * 1.5f
    spinZ = -impactOffset.x * 28f * curveEfficiency

    // Topspin / Backspin (spinX)
    spinX = -impactOffset.y * 16f

    rotationAngle = 0f
    isBallInPlay = true
    hasCrossedGoalLine = false
    hasHitPost = false

    spawnKickParticles(startX, startY, powerInput)
  }

  /**
   * Continuous integration update (dt ~ 0.016s for 60 FPS)
   */
  fun update(dt: Float = 0.016f) {
    if (!isBallInPlay) {
      updateParticles(dt)
      updateNet(dt)
      return
    }

    val timeScale = dt * 60f

    // 1. Aerodynamic drag
    val speed = sqrt(vx * vx + vy * vy + vz * vz)
    val drag = 0.018f
    vx -= vx * drag * speed * timeScale
    vy -= vy * drag * speed * timeScale
    vz -= vz * drag * speed * timeScale

    // 2. Magnus Effect (Continuous curving swerve)
    // Horizontal swerve from spinZ as ball moves through air:
    val magnusX = spinZ * vy * 0.00042f
    vx += magnusX * timeScale

    // Vertical lift/dip from spinX:
    val magnusZ = -spinX * vy * 0.00022f
    vz += magnusZ * timeScale

    // 3. Gravity
    val gravity = 0.00092f
    vz -= gravity * timeScale

    // 4. Update coordinates
    ballX += vx * timeScale
    ballY += vy * timeScale
    ballZ += vz * timeScale

    // Rotation angle increment
    rotationAngle += (spinZ * 0.3f + speed * 150f) * timeScale

    // 5. Ground Contact & Bounce (NSS style restitution)
    if (ballZ <= 0.005f) {
      ballZ = 0.005f
      if (abs(vz) > 0.0035f && bounceCount < 3) {
        bounceCount++
        vz = -vz * 0.48f // 48% restitution
        vx *= 0.86f
        vy *= 0.86f
        spinZ *= 0.75f
        spawnGroundDust(ballX, ballY)
      } else {
        vz = 0f
        // Smooth rolling friction on turf
        vx *= 0.94f
        vy *= 0.94f
      }
    }

    // 6. Record Speed Trail
    if (shotSpeedKmH > 70f && Random.nextFloat() < 0.65f) {
      val trailRadius = 7f + ballZ * 14f
      trailPoints.add(TrailPoint(ballX, ballY, ballZ, trailRadius, 0.75f))
    }
    updateTrail()
    updateParticles(dt)

    // 7. Goal Net Deformation Reaction
    updateNet(dt)
    if (ballY in 0.06f..0.14f && ballX in 0.31f..0.69f) {
      hasCrossedGoalLine = true
      deformNetAroundBall(ballX, ballY)
    }
  }

  private fun spawnGroundDust(x: Float, y: Float) {
    for (i in 0..4) {
      particles.add(
        Particle(
          x = x + (Random.nextFloat() - 0.5f) * 0.012f,
          y = y + (Random.nextFloat() - 0.5f) * 0.012f,
          vx = (Random.nextFloat() - 0.5f) * 0.003f,
          vy = (Random.nextFloat() - 0.5f) * 0.003f,
          size = 2f + Random.nextFloat() * 2.5f,
          color = Color(0x994E7D32),
          life = 0.32f,
          maxLife = 0.32f
        )
      )
    }
  }

  private fun updateTrail() {
    val iter = trailPoints.iterator()
    while (iter.hasNext()) {
      val pt = iter.next()
      pt.alpha -= 0.05f
      if (pt.alpha <= 0.05f) {
        iter.remove()
      }
    }
  }

  private fun updateParticles(dt: Float) {
    val iter = particles.iterator()
    while (iter.hasNext()) {
      val p = iter.next()
      p.x += p.vx * (dt * 60f)
      p.y += p.vy * (dt * 60f)
      p.life -= dt
      p.alpha = (p.life / p.maxLife).coerceIn(0f, 1f)
      if (p.life <= 0f) {
        iter.remove()
      }
    }
  }

  private fun deformNetAroundBall(bx: Float, by: Float) {
    for (vert in netVertices) {
      val dx = vert.currX - bx
      val dy = vert.currY - by
      val dist = sqrt(dx * dx + dy * dy)
      if (dist < 0.075f) {
        val force = (0.075f - dist) * 0.42f
        vert.vy -= force * 0.85f
        vert.vx += (if (dx > 0) force else -force) * 0.5f
      }
    }
    // Dampen ball speed when inside net
    vx *= 0.68f
    vy *= 0.58f
  }

  private fun updateNet(dt: Float) {
    val springK = 0.14f
    val damping = 0.84f
    for (v in netVertices) {
      val ax = (v.origX - v.currX) * springK
      val ay = (v.origY - v.currY) * springK
      v.vx = (v.vx + ax) * damping
      v.vy = (v.vy + ay) * damping
      v.currX += v.vx
      v.currY += v.vy
    }
  }
}
