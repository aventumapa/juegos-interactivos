package com.aventumapa.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aventumapa.app.R
import com.aventumapa.app.audio.SoundCue
import com.aventumapa.app.ui.components.AventuBackground
import com.aventumapa.app.ui.components.CompassGuide
import com.aventumapa.app.ui.components.GuideMood
import com.aventumapa.app.ui.components.InfoBanner
import com.aventumapa.app.ui.theme.ExplorerTeal
import com.aventumapa.app.ui.theme.PaleTeal
import com.aventumapa.app.ui.theme.PaleYellow
import com.aventumapa.app.ui.theme.SuccessGreen
import com.aventumapa.app.ui.theme.WarmCoral
import com.aventumapa.content.mexico.MexicoContent
import com.aventumapa.core.model.ChildProfile
import com.aventumapa.gameengine.MotivationCoach
import com.aventumapa.gameengine.QuizFactory

@Composable
fun QuizScreen(
    profile: ChildProfile,
    onBack: () -> Unit,
    onRoundFinished: (correct: Int, total: Int, stars: Int) -> Unit,
    onSpeak: (String) -> Unit,
    onSound: (SoundCue) -> Unit,
) {
    val questions = remember {
        QuizFactory.capitalQuestions(
            entities = MexicoContent.entities,
            questionCount = 8,
            optionCount = 4,
            seed = System.currentTimeMillis(),
        )
    }
    var questionIndex by remember { mutableIntStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var correctCount by remember { mutableIntStateOf(0) }
    var finished by remember { mutableStateOf(false) }
    var rewardClaimed by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("") }

    if (!finished) {
        LaunchedEffect(questionIndex) {
            onSpeak(questions[questionIndex].prompt)
        }
    }

    AventuBackground {
        if (finished) {
            ResultScreen(
                alias = profile.alias,
                correct = correctCount,
                total = questions.size,
                rewardClaimed = rewardClaimed,
                onSpeak = onSpeak,
                onSound = onSound,
                onClaimAndExit = {
                    if (!rewardClaimed) {
                        val stars = when {
                            correctCount == questions.size -> 3
                            correctCount >= 3 -> 2
                            else -> 1
                        }
                        onRoundFinished(correctCount, questions.size, stars)
                        rewardClaimed = true
                    }
                    onBack()
                },
            )
        } else {
            val question = questions[questionIndex]
            val answerIsCorrect = selectedAnswer == question.correctAnswer
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 28.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp),
            ) {
                TextButton(onClick = onBack) { Text(stringResource(R.string.back)) }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                    CompassGuide(
                        modifier = Modifier.size(64.dp),
                        mood = if (selectedAnswer == null || answerIsCorrect) GuideMood.HAPPY else GuideMood.THINKING,
                    )
                    Column(Modifier.weight(1f)) {
                        Text(stringResource(R.string.quiz_title), style = MaterialTheme.typography.headlineMedium)
                        Text("Pregunta ${questionIndex + 1} de ${questions.size}")
                    }
                }
                LinearProgressIndicator(
                    progress = { (questionIndex + 1) / questions.size.toFloat() },
                    modifier = Modifier.fillMaxWidth().height(10.dp),
                    color = ExplorerTeal,
                    trackColor = PaleTeal,
                )
                Spacer(Modifier.height(5.dp))
                Text(question.prompt, style = MaterialTheme.typography.headlineMedium)
                TextButton(onClick = { onSpeak(question.prompt) }) {
                    Text("Escuchar la pregunta")
                }

                question.options.forEach { option ->
                    AnswerCard(
                        answer = option,
                        selected = selectedAnswer == option,
                        correctAnswer = if (selectedAnswer != null) question.correctAnswer else null,
                        onClick = {
                            if (selectedAnswer == null) {
                                selectedAnswer = option
                                val correct = option == question.correctAnswer
                                feedbackMessage = if (correct) {
                                    onSound(SoundCue.SUCCESS)
                                    val praise = if (MotivationCoach.shouldUsePersonalPraise()) {
                                        MotivationCoach.success(profile.alias)
                                    } else {
                                        "¡Respuesta correcta!"
                                    }
                                    "$praise ${question.explanation}"
                                } else {
                                    onSound(SoundCue.TRY_AGAIN)
                                    "Casi, ${profile.alias.ifBlank { "explorador" }}. ${question.explanation} Inténtalo de nuevo en la próxima ronda."
                                }
                                onSpeak(feedbackMessage)
                            }
                        },
                    )
                }

                if (selectedAnswer != null) {
                    InfoBanner(feedbackMessage)
                    Button(
                        onClick = {
                            val updatedCorrect = correctCount + if (answerIsCorrect) 1 else 0
                            correctCount = updatedCorrect
                            if (questionIndex == questions.lastIndex) {
                                finished = true
                            } else {
                                questionIndex += 1
                                selectedAnswer = null
                                feedbackMessage = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(18.dp),
                    ) {
                        Text(if (questionIndex == questions.lastIndex) stringResource(R.string.finish) else stringResource(R.string.next))
                    }
                }
            }
        }
    }
}

@Composable
private fun AnswerCard(
    answer: String,
    selected: Boolean,
    correctAnswer: String?,
    onClick: () -> Unit,
) {
    val isRevealedCorrect = correctAnswer != null && answer == correctAnswer
    val container = when {
        isRevealedCorrect -> SuccessGreen.copy(alpha = 0.18f)
        selected && correctAnswer != null -> WarmCoral.copy(alpha = 0.18f)
        selected -> PaleYellow
        else -> MaterialTheme.colorScheme.surface
    }
    val borderColor = when {
        isRevealedCorrect -> SuccessGreen
        selected -> ExplorerTeal
        else -> Color.Transparent
    }
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = container),
        border = BorderStroke(if (borderColor == Color.Transparent) 0.dp else 2.dp, borderColor),
    ) {
        Text(
            answer,
            modifier = Modifier.padding(18.dp),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ResultScreen(
    alias: String,
    correct: Int,
    total: Int,
    rewardClaimed: Boolean,
    onSpeak: (String) -> Unit,
    onSound: (SoundCue) -> Unit,
    onClaimAndExit: () -> Unit,
) {
    LaunchedEffect(Unit) {
        onSound(SoundCue.SUCCESS)
        onSpeak("Misión completada, ${alias.ifBlank { "explorador" }}. Acertaste $correct de $total capitales.")
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CompassGuide(Modifier.size(150.dp))
        Spacer(Modifier.height(22.dp))
        Text("¡Misión completada!", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(10.dp))
        Text("Acertaste $correct de $total capitales.", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(20.dp))
        Card(colors = CardDefaults.cardColors(containerColor = PaleYellow), shape = RoundedCornerShape(22.dp)) {
            Text(
                "+${correct * 20} XP",
                modifier = Modifier.padding(horizontal = 34.dp, vertical = 18.dp),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
            )
        }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onClaimAndExit,
            enabled = !rewardClaimed,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(18.dp),
        ) {
            Text("Guardar progreso")
        }
    }
}
