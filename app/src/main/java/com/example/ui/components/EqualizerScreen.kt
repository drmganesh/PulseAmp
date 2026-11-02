package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EqualizerPreset
import com.example.ui.theme.AmberVUGlow
import com.example.ui.theme.CarbonBlack
import com.example.ui.theme.DeepCharcoal
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.HiResGold
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

private val BAND_LABELS = listOf("31Hz", "62Hz", "125Hz", "250Hz", "500Hz", "1kHz", "2kHz", "4kHz", "8kHz", "16kHz")

@Composable
fun EqualizerScreen(
    activePreset: EqualizerPreset,
    isEqualizerEnabled: Boolean,
    presets: List<EqualizerPreset>,
    onToggleEnabled: (Boolean) -> Unit,
    onBandChange: (bandIdx: Int, gainDb: Float) -> Unit,
    onBassChange: (Float) -> Unit,
    onTrebleChange: (Float) -> Unit,
    onPreampChange: (Float) -> Unit,
    onVirtualizerChange: (Float) -> Unit,
    onSelectPreset: (EqualizerPreset) -> Unit,
    onSaveCustomPreset: (String) -> Unit,
    onDeletePreset: (Long) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSaveDialog by remember { mutableStateOf(false) }
    var newPresetName by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CarbonBlack)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.GraphicEq, contentDescription = null, tint = NeonCyan)
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        "POWERAMP 10-BAND DSP",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text("High Definition Audio Equalizer", color = TextSecondary, fontSize = 11.sp)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (isEqualizerEnabled) "DSP ACTIVE" else "BYPASS",
                    color = if (isEqualizerEnabled) NeonCyan else TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(end = 6.dp)
                )
                Switch(
                    checked = isEqualizerEnabled,
                    onCheckedChange = onToggleEnabled,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = CarbonBlack,
                        checkedTrackColor = NeonCyan,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = SurfaceElevated
                    ),
                    modifier = Modifier.testTag("equalizer_toggle_switch")
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close Equalizer", tint = TextPrimary)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Live Frequency Response Curve Canvas
        Card(
            colors = CardDefaults.cardColors(containerColor = DeepCharcoal),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(100.dp)
        ) {
            FrequencyResponseCurve(
                gains = activePreset.bandGains,
                isEnabled = isEqualizerEnabled,
                modifier = Modifier.fillMaxSize().padding(8.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        // Tone & Hardware DSP Controls (Rotary Knobs)
        Card(
            colors = CardDefaults.cardColors(containerColor = DeepCharcoal),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RotaryKnob(
                    label = "BASS",
                    value = activePreset.bassBoost,
                    range = 0f..100f,
                    unit = "%",
                    accentColor = NeonCyan,
                    onValueChange = onBassChange,
                    modifier = Modifier.testTag("rotary_bass")
                )
                RotaryKnob(
                    label = "TREBLE",
                    value = activePreset.trebleBoost,
                    range = -10f..10f,
                    unit = "dB",
                    accentColor = ElectricViolet,
                    onValueChange = onTrebleChange,
                    modifier = Modifier.testTag("rotary_treble")
                )
                RotaryKnob(
                    label = "PREAMP",
                    value = activePreset.preampGain,
                    range = -6f..6f,
                    unit = "dB",
                    accentColor = AmberVUGlow,
                    onValueChange = onPreampChange,
                    modifier = Modifier.testTag("rotary_preamp")
                )
                RotaryKnob(
                    label = "STEREO",
                    value = activePreset.virtualizerStrength,
                    range = 0f..100f,
                    unit = "%",
                    accentColor = HiResGold,
                    onValueChange = onVirtualizerChange,
                    modifier = Modifier.testTag("rotary_virtualizer")
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Presets Selector Carousel
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("EQUALIZER PRESETS", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Row {
                IconButton(onClick = { showSaveDialog = true }) {
                    Icon(Icons.Default.BookmarkAdd, contentDescription = "Save Preset", tint = NeonCyan, modifier = Modifier.size(20.dp))
                }
                if (!activePreset.isBuiltIn && activePreset.id > 0) {
                    IconButton(onClick = { onDeletePreset(activePreset.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Preset", tint = NeonPink, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
        ) {
            items(presets) { preset ->
                val isSelected = preset.name == activePreset.name
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) NeonCyan else SurfaceElevated)
                        .border(1.dp, if (isSelected) NeonCyan else SurfaceBorder, RoundedCornerShape(8.dp))
                        .clickable { onSelectPreset(preset) }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = preset.name,
                        color = if (isSelected) CarbonBlack else TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // 10-Band Graphic Fader Rack
        Card(
            colors = CardDefaults.cardColors(containerColor = DeepCharcoal),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    "10-BAND GRAPHIC EQUALIZER (-12dB to +12dB)",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (i in 0 until minOf(10, activePreset.bandGains.size)) {
                        BandFaderColumn(
                            bandIndex = i,
                            label = BAND_LABELS[i],
                            gainDb = activePreset.bandGains[i],
                            isEnabled = isEqualizerEnabled,
                            onGainChange = { onBandChange(i, it) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            containerColor = DeepCharcoal,
            title = { Text("Save Custom Preset", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newPresetName,
                    onValueChange = { newPresetName = it },
                    label = { Text("Preset Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPresetName.isNotBlank()) {
                            onSaveCustomPreset(newPresetName.trim())
                            newPresetName = ""
                            showSaveDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = CarbonBlack)
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun BandFaderColumn(
    bandIndex: Int,
    label: String,
    gainDb: Float,
    isEnabled: Boolean,
    onGainChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Gain readout (e.g., "+6.0")
        val gainStr = if (gainDb > 0) "+%.0f".format(gainDb) else "%.0f".format(gainDb)
        Text(
            text = gainStr,
            color = if (isEnabled && gainDb != 0f) NeonCyan else TextMuted,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )

        Spacer(Modifier.height(4.dp))

        // Custom touch slider track
        Box(
            modifier = Modifier
                .height(130.dp)
                .width(28.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val midY = size.height / 2f
                val trackX = size.width / 2f

                // Zero line
                drawLine(
                    color = SurfaceBorder,
                    start = Offset(0f, midY),
                    end = Offset(size.width, midY),
                    strokeWidth = 1.5f
                )

                // Vertical track
                drawLine(
                    color = SurfaceDark,
                    start = Offset(trackX, 0f),
                    end = Offset(trackX, size.height),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Active colored indicator
                val norm = ((gainDb - (-12f)) / 24f).coerceIn(0f, 1f)
                val thumbY = (1f - norm) * size.height

                // Line from zero to thumb
                drawLine(
                    color = if (isEnabled) NeonCyan else TextMuted,
                    start = Offset(trackX, midY),
                    end = Offset(trackX, thumbY),
                    strokeWidth = 4.dp.toPx()
                )

                // Fader thumb
                drawRoundRect(
                    color = if (isEnabled) Color.White else TextSecondary,
                    topLeft = Offset(2f, thumbY - 6.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(size.width - 4f, 12.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                )
            }

            // Invisible slider overlay for touch handling
            Slider(
                value = gainDb,
                onValueChange = onGainChange,
                valueRange = -12f..12f,
                colors = SliderDefaults.colors(
                    thumbColor = Color.Transparent,
                    activeTrackColor = Color.Transparent,
                    inactiveTrackColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp)
                    .testTag("fader_band_$bandIndex")
            )
        }

        Spacer(Modifier.height(4.dp))

        // Frequency label (e.g. 31Hz, 1kHz)
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.Monospace,
            maxLines = 1
        )
    }
}

@Composable
private fun FrequencyResponseCurve(
    gains: List<Float>,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val midY = size.height / 2f
        val count = gains.size
        val stepX = size.width / (count - 1).coerceAtLeast(1)

        // Draw grid lines (-12dB, -6dB, 0dB, +6dB, +12dB)
        val gridLevels = listOf(0.1f, 0.3f, 0.5f, 0.7f, 0.9f)
        for (lvl in gridLevels) {
            val y = size.height * lvl
            drawLine(
                color = SurfaceBorder.copy(alpha = 0.5f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1f
            )
        }

        // 0dB Center line
        drawLine(
            color = SurfaceBorder,
            start = Offset(0f, midY),
            end = Offset(size.width, midY),
            strokeWidth = 1.5f
        )

        if (count > 1) {
            val path = Path()
            val fillPath = Path()

            val firstNorm = ((gains[0] - (-12f)) / 24f).coerceIn(0f, 1f)
            val firstY = if (isEnabled) (1f - firstNorm) * size.height else midY
            path.moveTo(0f, firstY)
            fillPath.moveTo(0f, size.height)
            fillPath.lineTo(0f, firstY)

            for (i in 1 until count) {
                val prevNorm = ((gains[i - 1] - (-12f)) / 24f).coerceIn(0f, 1f)
                val currNorm = ((gains[i] - (-12f)) / 24f).coerceIn(0f, 1f)

                val prevX = (i - 1) * stepX
                val prevY = if (isEnabled) (1f - prevNorm) * size.height else midY
                val currX = i * stepX
                val currY = if (isEnabled) (1f - currNorm) * size.height else midY

                val cx = (prevX + currX) / 2f
                path.cubicTo(cx, prevY, cx, currY, currX, currY)
                fillPath.cubicTo(cx, prevY, cx, currY, currX, currY)
            }

            fillPath.lineTo(size.width, size.height)
            fillPath.close()

            // Fill under curve
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        (if (isEnabled) NeonCyan else TextMuted).copy(alpha = 0.25f),
                        Color.Transparent
                    )
                )
            )

            // Line curve
            drawPath(
                path = path,
                brush = Brush.horizontalGradient(
                    colors = if (isEnabled) listOf(NeonCyan, ElectricViolet, HiResGold) else listOf(TextMuted, TextMuted)
                ),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}
