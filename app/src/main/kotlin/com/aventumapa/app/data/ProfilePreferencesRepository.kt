package com.aventumapa.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.aventumapa.core.model.ChildProfile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfilePreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val profile: Flow<ChildProfile> = dataStore.data
        .catch { error ->
            if (error is IOException) emit(androidx.datastore.preferences.core.emptyPreferences()) else throw error
        }
        .map { preferences ->
            ChildProfile(
                alias = preferences[Keys.ALIAS].orEmpty(),
                avatarId = preferences[Keys.AVATAR] ?: "brujula",
                experience = preferences[Keys.EXPERIENCE] ?: 0,
                stars = preferences[Keys.STARS] ?: 0,
                completedRounds = preferences[Keys.COMPLETED_ROUNDS] ?: 0,
                correctAnswers = preferences[Keys.CORRECT_ANSWERS] ?: 0,
                totalAnswers = preferences[Keys.TOTAL_ANSWERS] ?: 0,
            )
        }

    suspend fun createOrUpdateProfile(alias: String, avatarId: String) {
        dataStore.edit { preferences ->
            preferences[Keys.ALIAS] = alias.trim().take(20)
            preferences[Keys.AVATAR] = avatarId
        }
    }

    suspend fun recordRound(correctAnswers: Int, totalAnswers: Int, completionStars: Int) {
        dataStore.edit { preferences ->
            preferences[Keys.EXPERIENCE] = (preferences[Keys.EXPERIENCE] ?: 0) + (correctAnswers * 20)
            preferences[Keys.STARS] = (preferences[Keys.STARS] ?: 0) + completionStars
            preferences[Keys.COMPLETED_ROUNDS] = (preferences[Keys.COMPLETED_ROUNDS] ?: 0) + 1
            preferences[Keys.CORRECT_ANSWERS] = (preferences[Keys.CORRECT_ANSWERS] ?: 0) + correctAnswers
            preferences[Keys.TOTAL_ANSWERS] = (preferences[Keys.TOTAL_ANSWERS] ?: 0) + totalAnswers
        }
    }

    suspend fun deleteLocalProfile() {
        dataStore.edit { it.clear() }
    }

    private object Keys {
        val ALIAS = stringPreferencesKey("alias")
        val AVATAR = stringPreferencesKey("avatar")
        val EXPERIENCE = intPreferencesKey("experience")
        val STARS = intPreferencesKey("stars")
        val COMPLETED_ROUNDS = intPreferencesKey("completed_rounds")
        val CORRECT_ANSWERS = intPreferencesKey("correct_answers")
        val TOTAL_ANSWERS = intPreferencesKey("total_answers")
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = androidx.datastore.preferences.core.PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("aventumapa.preferences_pb") },
    )
}

