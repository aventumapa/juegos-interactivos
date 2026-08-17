package com.aventumapa.app.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.aventumapa.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

enum class SoundCue {
    TAP,
    SUCCESS,
    TRY_AGAIN,
    MATCH,
}

@Singleton
class AventuSoundEffects @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val soundPool = SoundPool.Builder()
        .setMaxStreams(3)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build(),
        )
        .build()

    private val soundIds = mapOf(
        SoundCue.TAP to soundPool.load(context, R.raw.aventu_flip, 1),
        SoundCue.SUCCESS to soundPool.load(context, R.raw.aventu_success, 1),
        SoundCue.TRY_AGAIN to soundPool.load(context, R.raw.aventu_try_again, 1),
        SoundCue.MATCH to soundPool.load(context, R.raw.aventu_match, 1),
    )

    fun play(cue: SoundCue) {
        soundIds[cue]?.let { soundId ->
            soundPool.play(soundId, 0.72f, 0.72f, 1, 0, 1f)
        }
    }
}
