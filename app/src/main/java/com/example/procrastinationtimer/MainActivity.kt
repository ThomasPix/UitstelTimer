package com.example.procrastinationtimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ProcrastinatorApp()
            }
        }
    }
}

@Composable
fun ProcrastinatorApp() {
    var seconds by remember { mutableStateOf(0) }
    var running by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    LaunchedEffect(running) {
        while (running) {
            delay(1000)
            seconds++
        }
    }

    val minutes = seconds / 60
    val secs = seconds % 60

    val status = when {
        seconds < 5 -> "Nog oké..."
        seconds < 300 -> "Dit wordt verdacht"
        seconds < 1200 -> "Je bent officieel aan het uitstellen"
        else -> "Geen redding meer 💀"
    }

    var badge by remember { mutableStateOf("") }

    LaunchedEffect(seconds) {
        when (seconds) {
            10 -> { badge = "🏅 Beginner Uitsteller"; showConfetti = true }
            1800 -> { badge = "🏅 Ervaren Uitsteller"; showConfetti = true }
            3600 -> { badge = "🏆 Master of Delay"; showConfetti = true }
            10800 -> { badge = "👑 Legendary Procrastinator"; showConfetti = true }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(status, fontSize = 22.sp)
            Spacer(Modifier.height(20.dp))
            Text(String.format("%02d:%02d", minutes, secs), fontSize = 32.sp)
            Spacer(Modifier.height(20.dp))
            Text(badge, fontSize = 20.sp)
        }

        val targetAlignment = if (running) Alignment.TopEnd else Alignment.Center
        val targetSize = if (running) 60.dp else 200.dp
        val padding = if (running) 16.dp else 0.dp

        val animatedSize by animateDpAsState(
            targetValue = targetSize,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )

        val animatedPadding by animateDpAsState(
            targetValue = padding,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )

        Button(
            onClick = { running = true },
            modifier = Modifier
                .align(targetAlignment)
                .padding(animatedPadding)
                .size(animatedSize)
        ) {
            Text(if (running) "⏸" else "Start")
        }

        if (showConfetti) {
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = listOf(
                    Party(
                        speed = 0f,
                        maxSpeed = 300f,
                        damping = 0.8f,
                        spread = 360,
                        timeToLive = 2000L,
                        position = Position.Relative(0.5, 0.0),
                        emitter = Emitter(1000L, TimeUnit.MILLISECONDS).max(100)
                    )
                )
            )

            LaunchedEffect(Unit) {
                delay(2000)
                showConfetti = false
            }
        }
    }
}
