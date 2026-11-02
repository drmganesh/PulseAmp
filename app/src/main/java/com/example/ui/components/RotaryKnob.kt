package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CarbonBlack
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RotaryKnob(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    unit: String = "",
    accentColor: Color = NeonCyan,
    sizeDp: Dp = 80.dp,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalRange = range.endInclusive - range.start
    var dragAccumulator by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(sizeDp)
                .pointerInput(range) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        // Drag up/down adjusts knob value
                        val sensitivity = totalRange / 200f
                        val delta = -dragAmount.y * sensitivity
                        val newValue = (value + delta).coerceIn(range.start, range.endInclusive)
                        onValueChange(newValue)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(sizeDp)) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val outerRadius = (size.width / 2f) - 6.dp.toPx()
                val innerRadius = outerRadius * 0.75f

                // Dial angle range: 140 degrees to 400 degrees (260 degree arc)
                val startAngle = 140f
                val sweepTotal = 260f

                // Draw background track
                drawArc(
                    color = SurfaceDark,
                    startAngle = startAngle,
                    sweepAngle = sweepTotal,
                    useCenter = false,
                    topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
                    size = Size(outerRadius * 2, outerRadius * 2),
                    style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                )

                // Normalized position: 0.0 to 1.0
                val progress = ((value - range.start) / totalRange).coerceIn(0f, 1f)
                val activeSweep = sweepTotal * progress

                // Draw active arc with neon glow
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(accentColor.copy(alpha = 0.6f), accentColor),
                        center = center
                    ),
                    startAngle = startAngle,
                    sweepAngle = activeSweep,
                    useCenter = false,
                    topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
                    size = Size(outerRadius * 2, outerRadius * 2),
                    style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                )

                // Center metallic body
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(SurfaceElevated, SurfaceDark, CarbonBlack),
                        center = center,
                        radius = innerRadius
                    ),
                    radius = innerRadius,
                    center = center
                )

                drawCircle(
                    color = SurfaceBorder,
                    radius = innerRadius,
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )

                // Current pointer dot on knob perimeter
                val currentAngleDeg = startAngle + activeSweep
                val currentAngleRad = (currentAngleDeg * PI / 180.0).toFloat()
                val dotRadius = innerRadius * 0.72f
                val dotX = center.x + dotRadius * cos(currentAngleRad)
                val dotY = center.y + dotRadius * sin(currentAngleRad)

                drawCircle(
                    color = accentColor,
                    radius = 3.dp.toPx(),
                    center = Offset(dotX, dotY)
                )
            }

            // Value text in center
            val displayText = when {
                unit == "dB" -> if (value > 0) "+%.1fdB".format(value) else "%.1fdB".format(value)
                unit == "%" -> "%.0f%%".format(value)
                else -> "%.1f".format(value)
            }

            Text(
                text = displayText,
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = label,
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
        )
    }
}
