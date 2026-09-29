package com.example.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.CombinedVibration
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

  init {
    try {
      toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 85)
    } catch (_: Exception) {
      toneGenerator = null
    }
  }

  fun playKick() {
    vibrate(30)
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 60)
    } catch (_: Exception) {}
  }

  fun playWhistle() {
    vibrate(80)
    CoroutineScope(Dispatchers.Default).launch {
      try {
        toneGenerator?.startTone(ToneGenerator.TONE_DTMF_D, 180)
        delay(120)
        toneGenerator?.startTone(ToneGenerator.TONE_DTMF_D, 350)
      } catch (_: Exception) {}
    }
  }

  fun playGoal() {
    vibratePattern(longArrayOf(0, 100, 50, 200, 80, 400))
    CoroutineScope(Dispatchers.Default).launch {
      try {
        toneGenerator?.startTone(ToneGenerator.TONE_DTMF_9, 150)
        delay(100)
        toneGenerator?.startTone(ToneGenerator.TONE_DTMF_0, 150)
        delay(100)
        toneGenerator?.startTone(ToneGenerator.TONE_DTMF_P, 400)
      } catch (_: Exception) {}
    }
  }

  fun playCrossbar() {
    vibratePattern(longArrayOf(0, 60, 40, 90))
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 120)
    } catch (_: Exception) {}
  }

  fun playSuccess() {
    vibrate(40)
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 100)
    } catch (_: Exception) {}
  }

  fun playClick() {
    vibrate(15)
  }

  private fun vibrate(durationMs: Long) {
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
