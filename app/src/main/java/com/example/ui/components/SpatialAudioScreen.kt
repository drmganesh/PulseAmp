package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SpatialAudioConfig
import com.example.data.model.SpatialRoom
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
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SpatialAudioScreen(
    config: SpatialAudioConfig,
    onToggleEnabled: (Boolean) -> Unit,
    onAzimuthChange: (Float) -> Unit,
    onSoundstageWidthChange: (Float) -> Unit,
    onDepthChange: (Float) -> Unit,
    onElevationChange: (Float) -> Unit,
    onRoomChange: (SpatialRoom) -> Unit,
    onToggleAutoOrbit: () -> Unit,
    onOrbitSpeedChange: (Float) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CarbonBlack)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(NeonCyan, ElectricViolet))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.SurroundSound,
                        contentDescription = null,
                        tint = CarbonBlack,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        "360° SPATIAL AUDIO",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text("Binaural Soundstage & Head Tracking", color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (config.isEnabled) "ACTIVE" else "BYPASS",
                    color = if (config.isEnabled) NeonCyan else TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(end = 6.dp)
                )
                Switch(
                    checked = config.isEnabled,
                    onCheckedChange = onToggleEnabled,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = CarbonBlack,
                        checkedTrackColor = NeonCyan,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = SurfaceElevated
                    ),
                    modifier = Modifier.testTag("spatial_audio_switch")
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close 360 Audio", tint = TextPrimary)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Interactive 360° Soundstage Radar
        Card(
            colors = CardDefaults.cardColors(containerColor = DeepCharcoal),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "360° SOUNDSTAGE RADAR",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    // Direction azimuth readout
                    val azimuth = config.azimuthDeg
                    val directionLabel = when {
                        azimuth in 337.5f..360f || azimuth in 0f..22.5f -> "Front Center (0°)"
                        azimuth in 22.5f..67.5f -> "Front Right (45°)"
                        azimuth in 67.5f..112.5f -> "Right Surround (90°)"
                        azimuth in 112.5f..157.5f -> "Rear Right (135°)"
                        azimuth in 157.5f..202.5f -> "Rear Center (180°)"
                        azimuth in 202.5f..247.5f -> "Rear Left (225°)"
                        azimuth in 247.5f..292.5f -> "Left Surround (270°)"
                        else -> "Front Left (315°)"
                    }

                    Text(
                        text = directionLabel,
                        color = if (config.isEnabled) NeonCyan else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Circular Touch Soundfield
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .aspectRatio(1f),
                    contentAlignment = Alignment.Center
                ) {
                    SpatialSoundfieldRadar(
                        azimuthDeg = config.azimuthDeg,
                        isEnabled = config.isEnabled,
                        onAzimuthDragged = onAzimuthChange,
                        modifier = Modifier.fillMaxSize().testTag("spatial_radar_canvas")
                    )

                    // Center Listener Head
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(SurfaceDark)
                            .border(1.5.dp, if (config.isEnabled) NeonCyan else SurfaceBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Headphones,
                            contentDescription = "Listener",
                            tint = if (config.isEnabled) NeonCyan else TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Auto-Orbit and Recenter Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onToggleAutoOrbit,
                        enabled = config.isEnabled,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (config.isAutoOrbit) NeonCyan else SurfaceElevated,
                            contentColor = if (config.isAutoOrbit) CarbonBlack else TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1.5f).testTag("auto_orbit_button")
                    ) {
                        Icon(Icons.Default.RotateRight, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            if (config.isAutoOrbit) "ORBITING (360° ON)" else "AUTO-ORBIT 360°",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = { onAzimuthChange(0f) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).testTag("recenter_button")
                    ) {
                        Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("CENTER", fontSize = 11.sp)
                    }
                }

                if (config.isAutoOrbit) {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Orbit Speed:", color = TextSecondary, fontSize = 11.sp)
                        Spacer(Modifier.width(8.dp))
                        listOf(0.5f, 1.0f, 2.0f).forEach { spd ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (config.orbitSpeed == spd) NeonCyan else SurfaceDark)
                                    .clickable { onOrbitSpeedChange(spd) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    "${spd}x",
                                    color = if (config.orbitSpeed == spd) CarbonBlack else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Soundstage Dimension Sliders
        Card(
            colors = CardDefaults.cardColors(containerColor = DeepCharcoal),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    "SPATIAL DIMENSION CONTROLS",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                // Soundstage Width Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Soundstage Width", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Text("${config.soundstageWidth.toInt()}%", color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Slider(
                        value = config.soundstageWidth,
                        onValueChange = onSoundstageWidthChange,
                        valueRange = 100f..300f,
                        enabled = config.isEnabled,
                        colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan, inactiveTrackColor = SurfaceBorder),
                        modifier = Modifier.testTag("soundstage_width_slider")
                    )
                }

                // Spatial Depth / Immersion Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Spatial Depth & Distance", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Text("${config.depth.toInt()}%", color = ElectricViolet, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Slider(
                        value = config.depth,
                        onValueChange = onDepthChange,
                        valueRange = 0f..100f,
                        enabled = config.isEnabled,
                        colors = SliderDefaults.colors(thumbColor = ElectricViolet, activeTrackColor = ElectricViolet, inactiveTrackColor = SurfaceBorder),
                        modifier = Modifier.testTag("spatial_depth_slider")
                    )
                }

                // Vertical Elevation (Overhead Height Atmos)
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Vertical Elevation (3D Height)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        val elevStr = if (config.elevationDeg > 0) "+%.0f°".format(config.elevationDeg) else "%.0f°".format(config.elevationDeg)
                        Text(elevStr, color = HiResGold, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Slider(
                        value = config.elevationDeg,
                        onValueChange = onElevationChange,
                        valueRange = -45f..45f,
                        enabled = config.isEnabled,
                        colors = SliderDefaults.colors(thumbColor = HiResGold, activeTrackColor = HiResGold, inactiveTrackColor = SurfaceBorder),
                        modifier = Modifier.testTag("spatial_elevation_slider")
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Room Acoustic Environment Selector
        Text(
            "ROOM ACOUSTIC ENVIRONMENTS",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(SpatialRoom.entries) { room ->
                val isSelected = config.room == room
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) SurfaceElevated else DeepCharcoal
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .width(170.dp)
                        .border(
                            1.dp,
                            if (isSelected) NeonCyan else SurfaceBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onRoomChange(room) }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = room.title,
                            color = if (isSelected) NeonCyan else TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = room.subtitle,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            maxLines = 2
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(30.dp))
    }
}

@Composable
private fun SpatialSoundfieldRadar(
    azimuthDeg: Float,
    isEnabled: Boolean,
    onAzimuthDragged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    change.consume()
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val touchPos = change.position
                    val dx = touchPos.x - center.x
                    val dy = touchPos.y - center.y

                    // Angle in degrees from top (0 deg is top / front)
                    var angleDeg = (atan2(dx.toDouble(), -dy.toDouble()) * 180.0 / PI).toFloat()
                    if (angleDeg < 0) angleDeg += 360f
                    onAzimuthDragged(angleDeg)
                }
            }
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val maxRadius = (size.width / 2f) - 20.dp.toPx()

        // Radar concentric circles
        val circles = listOf(0.35f, 0.65f, 0.95f)
        for (scale in circles) {
            drawCircle(
                color = SurfaceBorder.copy(alpha = 0.5f),
                radius = maxRadius * scale,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // Crosshairs (Front, Back, Left, Right)
        drawLine(
            color = SurfaceBorder.copy(alpha = 0.6f),
            start = Offset(center.x, center.y - maxRadius),
            end = Offset(center.x, center.y + maxRadius),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = SurfaceBorder.copy(alpha = 0.6f),
            start = Offset(center.x - maxRadius, center.y),
            end = Offset(center.x + maxRadius, center.y),
            strokeWidth = 1.dp.toPx()
        )

        // 8 Surround Speaker nodes
        val speakerAngles = listOf(
            0f to "C", 45f to "FR", 90f to "SR", 135f to "BR",
            180f to "LFE", 225f to "BL", 270f to "SL", 315f to "FL"
        )

        for ((deg, label) in speakerAngles) {
            val rad = deg * PI / 180.0
            val sx = center.x + maxRadius * sin(rad).toFloat()
            val sy = center.y - maxRadius * cos(rad).toFloat()

            drawCircle(
                color = if (isEnabled) SurfaceElevated else SurfaceDark,
                radius = 7.dp.toPx(),
                center = Offset(sx, sy)
            )
            drawCircle(
                color = if (isEnabled) NeonCyan.copy(alpha = 0.6f) else SurfaceBorder,
                radius = 7.dp.toPx(),
                center = Offset(sx, sy),
                style = Stroke(width = 1.5.dp.toPx())
            )
        }

        if (isEnabled) {
            // Source beam line to listener
            val rad = azimuthDeg * PI / 180.0
            val sourceDist = maxRadius * 0.85f
            val orbX = center.x + sourceDist * sin(rad).toFloat()
            val orbY = center.y - sourceDist * cos(rad).toFloat()

            // Directional glow beam
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(NeonCyan.copy(alpha = 0.8f), Color.Transparent),
                    start = Offset(orbX, orbY),
                    end = center
                ),
                start = Offset(orbX, orbY),
                end = center,
                strokeWidth = 2.dp.toPx()
            )

            // Outer pulse wave for audio source
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NeonCyan.copy(alpha = 0.45f), Color.Transparent),
                    center = Offset(orbX, orbY),
                    radius = 22.dp.toPx()
                ),
                radius = 22.dp.toPx(),
                center = Offset(orbX, orbY)
            )

            // Audio Source Orb
            drawCircle(
                color = NeonCyan,
                radius = 11.dp.toPx(),
                center = Offset(orbX, orbY)
            )
            drawCircle(
                color = Color.White,
                radius = 5.dp.toPx(),
                center = Offset(orbX, orbY)
            )
        }
    }
}
