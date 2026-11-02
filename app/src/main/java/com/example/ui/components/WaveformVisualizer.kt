package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VisualizerStyle
import com.example.ui.theme.AmberVUGlow
import com.example.ui.theme.CarbonBlack
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.HiResGold
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WaveformVisualizer(
    bands: FloatArray,
    vuLeft: Float,
    vuRight: Float,
    isPlaying: Boolean,
    style: VisualizerStyle,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        when (style) {
            VisualizerStyle.SPECTRUM_BARS -> SpectrumBarsVisualizer(bands = bands)
            VisualizerStyle.WAVEFORM_OSCILLOSCOPE -> OscilloscopeVisualizer(bands = bands, isPlaying = isPlaying)
            VisualizerStyle.ANALOG_VU_METER -> DualAnalogVUMeter(vuLeft = vuLeft, vuRight = vuRight)
            VisualizerStyle.CIRCULAR_PULSE -> CircularPulseVisualizer(bands = bands)
        }
    }
}

@Composable
private fun SpectrumBarsVisualizer(bands: FloatArray) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val count = bands.size
        val barSpacing = 4.dp.toPx()
        val totalSpacing = barSpacing * (count - 1)
        val barWidth = ((size.width - totalSpacing) / count).coerceAtLeast(2f)

        val gradient = Brush.verticalGradient(
            colors = listOf(NeonPink, ElectricViolet, NeonCyan),
            startY = 0f,
            endY = size.height
        )

        for (i in 0 until count) {
            val amplitude = bands[i].coerceIn(0.04f, 1.0f)
            val barHeight = size.height * amplitude
            val left = i * (barWidth + barSpacing)
            val top = size.height - barHeight

            // Draw glowing bar
            drawRoundRect(
                brush = gradient,
                topLeft = Offset(left, top),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
            )

            // Draw floating peak cap
            val capTop = (top - 5f).coerceAtLeast(0f)
            drawRoundRect(
                color = Color.White.copy(alpha = 0.9f),
                topLeft = Offset(left, capTop),
                size = Size(barWidth, 3f),
                cornerRadius = CornerRadius(1.5f, 1.5f)
            )
        }
    }
}

@Composable
private fun OscilloscopeVisualizer(bands: FloatArray, isPlaying: Boolean) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val midY = size.height / 2f
        val path = Path()
        val count = bands.size
        val stepX = size.width / (count - 1).coerceAtLeast(1)

        path.moveTo(0f, midY)

        for (i in 0 until count) {
            val amp = bands[i]
            val x = i * stepX
            val sign = if (i % 2 == 0) 1f else -1f
            val y = midY + sign * amp * (size.height * 0.42f)

            if (i == 0) {
                path.moveTo(x, y)
            } else {
                val prevX = (i - 1) * stepX
                val prevSign = if ((i - 1) % 2 == 0) 1f else -1f
                val prevY = midY + prevSign * bands[i - 1] * (size.height * 0.42f)
                val cX = (prevX + x) / 2f
                path.quadraticTo(cX, prevY, x, y)
            }
        }

        // Center zero-crossing line
        drawLine(
            color = SurfaceDark,
            start = Offset(0f, midY),
            end = Offset(size.width, midY),
            strokeWidth = 1.5f
        )

        // Waveform neon glow line
        drawPath(
            path = path,
            brush = Brush.horizontalGradient(
                colors = listOf(NeonCyan, ElectricViolet, NeonCyan)
            ),
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun DualAnalogVUMeter(vuLeft: Float, vuRight: Float) {
    val animLeft by animateFloatAsState(targetValue = vuLeft, animationSpec = tween(70), label = "vuLeft")
    val animRight by animateFloatAsState(targetValue = vuRight, animationSpec = tween(70), label = "vuRight")

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AnalogVUGauge(
            channelName = "CH-L (LEFT)",
            level = animLeft,
            modifier = Modifier.weight(1f)
        )
        AnalogVUGauge(
            channelName = "CH-R (RIGHT)",
            level = animRight,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AnalogVUGauge(
    channelName: String,
    level: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(modifier = Modifier.weight(1f).fillMaxWidth()) {
            val centerX = size.width / 2f
            val needlePivotY = size.height * 1.05f
            val radius = size.height * 0.95f

            // Dial scale arc (-50 deg to +50 deg)
            val startAngle = 220f
            val sweepTotal = 100f

            // Draw arc background
            drawArc(
                color = SurfaceDark,
                startAngle = startAngle,
                sweepAngle = sweepTotal,
                useCenter = false,
                topLeft = Offset(centerX - radius, needlePivotY - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw normal zone (green/cyan)
            drawArc(
                color = NeonCyan.copy(alpha = 0.8f),
                startAngle = startAngle,
                sweepAngle = sweepTotal * 0.75f,
                useCenter = false,
                topLeft = Offset(centerX - radius, needlePivotY - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw peak overload zone (+3dB red)
            drawArc(
                color = NeonPink,
                startAngle = startAngle + (sweepTotal * 0.75f),
                sweepAngle = sweepTotal * 0.25f,
                useCenter = false,
                topLeft = Offset(centerX - radius, needlePivotY - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw needle
            val needleAngleDeg = (startAngle + sweepTotal * level.coerceIn(0f, 1f))
            val needleAngleRad = (needleAngleDeg * PI / 180.0).toFloat()
            val needleLength = radius * 0.92f

            val needleTipX = centerX + needleLength * cos(needleAngleRad)
            val needleTipY = needlePivotY + needleLength * sin(needleAngleRad)

            drawLine(
                color = AmberVUGlow,
                start = Offset(centerX, needlePivotY),
                end = Offset(needleTipX, needleTipY),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Pivot cap
            drawCircle(
                color = Color.White,
                radius = 6.dp.toPx(),
                center = Offset(centerX, needlePivotY)
            )
        }

        Text(
            text = channelName,
            color = TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun CircularPulseVisualizer(bands: FloatArray) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val baseRadius = minOf(size.width, size.height) * 0.28f
        val count = bands.size
        val angleStep = (2 * PI / count).toFloat()

        val path = Path()

        for (i in 0 until count) {
            val amp = bands[i].coerceIn(0.05f, 1.0f)
            val r = baseRadius + amp * (baseRadius * 0.7f)
            val angle = i * angleStep
            val x = center.x + r * cos(angle)
            val y = center.y + r * sin(angle)

            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()

        // Inner glowing core
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(NeonCyan.copy(alpha = 0.35f), Color.Transparent),
                center = center,
                radius = baseRadius
            ),
            center = center,
            radius = baseRadius
        )

        // Pulsing perimeter ring
        drawPath(
            path = path,
            brush = Brush.sweepGradient(
                colors = listOf(NeonCyan, ElectricViolet, NeonPink, HiResGold, NeonCyan),
                center = center
            ),
            style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}
