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
  val radius: Float,
  var alpha: Float = 0.8f
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
 * 60 FPS Aerodynamic Physics Simulation Engine with Magnus Effect.
 */
class SoccerPhysicsEngine {

  // Ball 3D state
  var ballX: Float = 0.5f
  var ballY: Float = 0.7f
  var ballZ: Float = 0f

  var vx: Float = 0f
  var vy: Float = 0f
  var vz: Float = 0f

  // Angular velocity (Spin in rad/s)
  var spinX: Float = 0f // Topspin / Backspin
  var spinY: Float = 0f
  var spinZ: Float = 0f // Sidespin (Magnus Curve)

  var rotationAngle: Float = 0f

  // Shot telemetry
  var shotSpeedKmH: Float = 0f
  var isBallInPlay: Boolean = false
  var hasCrossedGoalLine: Boolean = false

  // Visual Effects
  val particles = mutableListOf<Particle>()
  val trailPoints = mutableListOf<TrailPoint>()

  // Elastic Goal Net Grid (Columns x Rows)
  val netCols = 9
  val netRows = 5
  val netVertices = mutableListOf<NetVertex>()

  init {
    resetNet(0.32f, 0.68f, 0.06f, 0.14f)
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
    val count = (18 + power * 15).toInt()
    val turfColors = listOf(
      Color(0xFF388E3C),
      Color(0xFF2E7D32),
      Color(0xFF8D6E63),
      Color(0xFF5D4037),
      Color(0xFF81C784)
    )

    for (i in 0 until count) {
      val angle = Random.nextFloat() * 2 * Math.PI.toFloat()
      val speed = (0.002f + Random.nextFloat() * 0.008f) * (0.8f + power * 0.5f)
      particles.add(
        Particle(
          x = startX + (Random.nextFloat() - 0.5f) * 0.015f,
          y = startY + (Random.nextFloat() - 0.5f) * 0.015f,
          vx = cos(angle) * speed,
          vy = sin(angle) * speed,
          size = 2.5f + Random.nextFloat() * 4f,
          color = turfColors.random(),
          alpha = 1f,
          life = 0.5f + Random.nextFloat() * 0.4f,
          maxLife = 0.9f
        )
      )
    }
  }

  fun launchShot(
    startX: Float,
    startY: Float,
    targetX: Float,
    targetY: Float,
    impactOffset: Offset,
    powerInput: Float,
    playerPowerSkill: Int,
    playerCurlSkill: Int,
    playerAccuracySkill: Int
  ) {
    ballX = startX
    ballY = startY
    ballZ = 0.02f

    // Calculate initial velocity vector
    val dx = targetX - startX
    val dy = targetY - startY
    val dist = sqrt(dx * dx + dy * dy)
    val dirX = if (dist > 0.001f) dx / dist else 0f
    val dirY = if (dist > 0.001f) dy / dist else -1f

    // Power calculation based on player stats and input
    val baseSpeed = 0.022f + (playerPowerSkill / 100f) * 0.018f
    val totalSpeed = baseSpeed * (0.6f + powerInput * 0.6f)

    // Convert to realistic km/h speed
    shotSpeedKmH = (65f + (powerInput * 45f) + (playerPowerSkill * 0.35f)).coerceIn(55f, 130f)

    vx = dirX * totalSpeed
    vy = dirY * totalSpeed

    // Vertical launch velocity based on impact:
    // impactOffset.y > 0 (hit underneath): lift into the air
    // impactOffset.y < 0 (hit top): low driving bullet
    val liftFactor = (impactOffset.y + 0.4f).coerceIn(-0.2f, 1.0f)
    vz = 0.012f + liftFactor * 0.024f

    // Spin / Magnus setup:
    // impactOffset.x: -1 (kick right side of ball) -> spins counter-clockwise -> curves right
    // +1 (kick left side of ball) -> spins clockwise -> curves left
    val curlPower = (playerCurlSkill / 100f) * 1.4f
    spinZ = -impactOffset.x * 25f * curlPower

    // Topspin / Backspin:
    // Hitting bottom creates backspin (floats), hitting top creates topspin (dips quickly)
    spinX = -impactOffset.y * 18f

    rotationAngle = 0f
    isBallInPlay = true
    hasCrossedGoalLine = false

    spawnKickParticles(startX, startY, powerInput)
  }

  /**
   * Continuous integration step (e.g. 60 FPS step dt ~ 0.016s)
   */
  fun update(dt: Float = 0.016f) {
    if (!isBallInPlay) {
      updateParticles(dt)
      updateNet(dt)
      return
    }

    // 1. Aerodynamic Drag: F_drag = -c * |v| * v
    val dragCoeff = 0.025f
    val speed = sqrt(vx * vx + vy * vy + vz * vz)
    vx -= vx * dragCoeff * speed * (dt * 60f)
    vy -= vy * dragCoeff * speed * (dt * 60f)
    vz -= vz * dragCoeff * speed * (dt * 60f)

    // 2. The Magnus Force: F_magnus = S * (omega x v)
    // Horizontal swerve from spinZ:
    val magnusForceX = spinZ * vy * 0.00035f
    vx += magnusForceX * (dt * 60f)

    // Vertical lift / dip from spinX:
    val magnusForceZ = -spinX * vy * 0.00025f
    vz += magnusForceZ * (dt * 60f)

    // 3. Gravity
    val gravity = 0.00095f
    vz -= gravity * (dt * 60f)

    // 4. Update Position
    ballX += vx * (dt * 60f)
    ballY += vy * (dt * 60f)
    ballZ += vz * (dt * 60f)

    // Rotation increment
    rotationAngle += (spinZ * 0.2f + speed * 120f) * (dt * 60f)

    // 5. Ground Contact & Bounce
    if (ballZ <= 0.01f) {
      ballZ = 0.01f
      if (abs(vz) > 0.003f) {
        // Bounce with restitution
        vz = -vz * 0.55f
        vx *= 0.88f
        vy *= 0.88f
        spinZ *= 0.8f
        // Spawn small ground grass dust
        spawnGroundDust(ballX, ballY)
      } else {
        vz = 0f
        // Rolling friction
        vx *= 0.94f
        vy *= 0.94f
      }
    }

    // 6. Record Speed Trail
    if (shotSpeedKmH > 75f && Random.nextFloat() < 0.6f) {
      trailPoints.add(TrailPoint(ballX, ballY, 6f + ballZ * 12f, 0.7f))
    }
    updateTrail()

    // 7. Update Particles
    updateParticles(dt)

    // 8. Goal Net Deformation Reaction
    updateNet(dt)
    if (ballY in 0.06f..0.14f && ballX in 0.31f..0.69f) {
      hasCrossedGoalLine = true
      deformNetAroundBall(ballX, ballY)
    }
  }

  private fun spawnGroundDust(x: Float, y: Float) {
    for (i in 0..3) {
      particles.add(
        Particle(
          x = x + (Random.nextFloat() - 0.5f) * 0.01f,
          y = y + (Random.nextFloat() - 0.5f) * 0.01f,
          vx = (Random.nextFloat() - 0.5f) * 0.002f,
          vy = (Random.nextFloat() - 0.5f) * 0.002f,
          size = 2f + Random.nextFloat() * 2f,
          color = Color(0x884E7D32),
          life = 0.3f,
          maxLife = 0.3f
        )
      )
    }
  }

  private fun updateTrail() {
    val iter = trailPoints.iterator()
    while (iter.hasNext()) {
      val pt = iter.next()
      pt.alpha -= 0.045f
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
      if (dist < 0.07f) {
        val force = (0.07f - dist) * 0.4f
        // Push net backward into the goal
        vert.vy -= force * 0.8f
        vert.vx += (if (dx > 0) force else -force) * 0.5f
      }
    }
    // Dampen ball speed when it hits the net
    vx *= 0.7f
    vy *= 0.6f
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
