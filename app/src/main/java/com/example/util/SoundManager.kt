package com.example.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SoundManager(context: Context) {
  private val appContext = context.applicationContext
  private var toneGenerator: ToneGenerator? = null
  private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    val vibratorManager = appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
    vibratorManager?.defaultVibrator
  } else {
    @Suppress("DEPRECATION")
    appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
  }

  var soundEnabled: Boolean = true
  var vibrationEnabled: Boolean = true

  init {
    try {
      toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 85)
    } catch (_: Exception) {
      toneGenerator = null
    }
  }

  fun playKick() {
    vibrate(35)
    if (!soundEnabled) return
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 55)
    } catch (_: Exception) {}
  }

  fun playHeaderVolley() {
    vibrate(45)
    if (!soundEnabled) return
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_DTMF_4, 90)
    } catch (_: Exception) {}
  }

  fun playWhistle() {
    vibrate(80)
    if (!soundEnabled) return
    CoroutineScope(Dispatchers.Default).launch {
      try {
        toneGenerator?.startTone(ToneGenerator.TONE_DTMF_D, 180)
        delay(110)
        toneGenerator?.startTone(ToneGenerator.TONE_DTMF_D, 350)
      } catch (_: Exception) {}
    }
  }

  fun playWhistleShort() {
    vibrate(50)
    if (!soundEnabled) return
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_DTMF_D, 150)
    } catch (_: Exception) {}
  }

  fun playGoal() {
    vibratePattern(longArrayOf(0, 100, 60, 220, 80, 450))
    if (!soundEnabled) return
    CoroutineScope(Dispatchers.Default).launch {
      try {
        toneGenerator?.startTone(ToneGenerator.TONE_DTMF_9, 140)
        delay(90)
        toneGenerator?.startTone(ToneGenerator.TONE_DTMF_0, 140)
        delay(90)
        toneGenerator?.startTone(ToneGenerator.TONE_DTMF_P, 420)
      } catch (_: Exception) {}
    }
  }

  fun playCrossbar() {
    vibratePattern(longArrayOf(0, 70, 40, 110))
    if (!soundEnabled) return
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 130)
    } catch (_: Exception) {}
  }

  fun playSuccess() {
    vibrate(40)
    if (!soundEnabled) return
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 110)
    } catch (_: Exception) {}
  }

  fun playClick() {
    vibrate(15)
  }

  private fun vibrate(durationMs: Long) {
    if (!vibrationEnabled) return
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(durationMs)
      }
    } catch (_: Exception) {}
  }

  private fun vibratePattern(timings: LongArray) {
    if (!vibrationEnabled) return
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator?.vibrate(VibrationEffect.createWaveform(timings, -1))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(timings, -1)
      }
    } catch (_: Exception) {}
  }
}
