package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Playlist
import com.example.data.model.Track
import com.example.ui.theme.CarbonBlack
import com.example.ui.theme.DeepCharcoal
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.HiResGold
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TagEditorDialog(
    track: Track,
    onDismiss: () -> Unit,
    onSave: (id: Long, title: String, artist: String, album: String, genre: String, year: Int) -> Unit
) {
    var title by remember { mutableStateOf(track.title) }
    var artist by remember { mutableStateOf(track.artist) }
    var album by remember { mutableStateOf(track.album) }
    var genre by remember { mutableStateOf(track.genre) }
    var yearText by remember { mutableStateOf(track.year.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepCharcoal,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Audiotrack, contentDescription = null, tint = NeonCyan)
                Spacer(Modifier.width(8.dp))
                Text("Edit Track Tags", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    colors = textFieldColors(),
                    modifier = Modifier.fillMaxWidth().testTag("tag_title_input")
                )
                OutlinedTextField(
                    value = artist,
                    onValueChange = { artist = it },
                    label = { Text("Artist") },
                    colors = textFieldColors(),
                    modifier = Modifier.fillMaxWidth().testTag("tag_artist_input")
                )
                OutlinedTextField(
                    value = album,
                    onValueChange = { album = it },
                    label = { Text("Album") },
                    colors = textFieldColors(),
                    modifier = Modifier.fillMaxWidth().testTag("tag_album_input")
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = genre,
                        onValueChange = { genre = it },
                        label = { Text("Genre") },
                        colors = textFieldColors(),
                        modifier = Modifier.weight(1.5f).testTag("tag_genre_input")
                    )
                    OutlinedTextField(
                        value = yearText,
                        onValueChange = { yearText = it },
                        label = { Text("Year") },
                        colors = textFieldColors(),
                        modifier = Modifier.weight(1f).testTag("tag_year_input")
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val year = yearText.toIntOrNull() ?: track.year
                    onSave(track.id, title, artist, album, genre, year)
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = CarbonBlack),
                modifier = Modifier.testTag("tag_save_button")
            ) {
                Text("Save Tags", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
fun SleepTimerDialog(
    secondsLeft: Int?,
    onDismiss: () -> Unit,
    onStartTimer: (minutes: Int) -> Unit,
    onCancelTimer: () -> Unit
) {
    var selectedMinutes by remember { mutableFloatStateOf(30f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepCharcoal,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Timer, contentDescription = null, tint = NeonCyan)
                Spacer(Modifier.width(8.dp))
                Text("Sleep Timer", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (secondsLeft != null && secondsLeft > 0) {
                    val m = secondsLeft / 60
                    val s = secondsLeft % 60
                    Text(
                        text = "Active: %02d:%02d remaining".format(m, s),
                        color = NeonCyan,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        "Music will smoothly fade out and pause when the timer expires.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                } else {
                    Text(
                        text = "${selectedMinutes.toInt()} Minutes",
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(Modifier.height(8.dp))
                    Slider(
                        value = selectedMinutes,
                        onValueChange = { selectedMinutes = it },
                        valueRange = 5f..120f,
                        steps = 22,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonCyan,
                            activeTrackColor = NeonCyan,
                            inactiveTrackColor = SurfaceBorder
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("sleep_timer_slider")
                    )

                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf(15, 30, 45, 60).forEach { mins ->
                            OutlinedButton(
                                onClick = { selectedMinutes = mins.toFloat() },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (selectedMinutes.toInt() == mins) NeonCyan else TextSecondary
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("${mins}m", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (secondsLeft != null && secondsLeft > 0) {
                Button(
                    onClick = onCancelTimer,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink, contentColor = Color.White),
                    modifier = Modifier.testTag("cancel_sleep_timer_button")
                ) {
                    Text("Turn Off Timer")
                }
            } else {
                Button(
                    onClick = { onStartTimer(selectedMinutes.toInt()) },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = CarbonBlack),
                    modifier = Modifier.testTag("start_sleep_timer_button")
                ) {
                    Text("Start Timer", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = TextSecondary)
            }
        }
    )
}

@Composable
fun CreatePlaylistDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, desc: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepCharcoal,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PlaylistAdd, contentDescription = null, tint = NeonCyan)
                Spacer(Modifier.width(8.dp))
                Text("New Playlist", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Playlist Name") },
                    colors = textFieldColors(),
                    modifier = Modifier.fillMaxWidth().testTag("new_playlist_name_input")
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description (Optional)") },
                    colors = textFieldColors(),
                    modifier = Modifier.fillMaxWidth().testTag("new_playlist_desc_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) onCreate(name.trim(), desc.trim())
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = CarbonBlack),
                modifier = Modifier.testTag("create_playlist_confirm_button")
            ) {
                Text("Create", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
fun AddToPlaylistDialog(
    track: Track,
    playlists: List<Playlist>,
    onDismiss: () -> Unit,
    onSelectPlaylist: (playlistId: Long) -> Unit,
    onCreateNewPlaylist: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepCharcoal,
        title = {
            Text("Add to Playlist", color = TextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text(
                    text = "Track: ${track.title}",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    maxLines = 1,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceElevated)
                        .clickable { onCreateNewPlaylist() }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = NeonCyan)
                    Spacer(Modifier.width(10.dp))
                    Text("Create New Playlist...", color = NeonCyan, fontWeight = FontWeight.SemiBold)
                }

                Spacer(Modifier.height(8.dp))

                if (playlists.isEmpty()) {
                    Text(
                        "No playlists created yet.",
                        color = TextMuted,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    LazyColumn(modifier = Modifier.height(180.dp)) {
                        items(playlists) { playlist ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onSelectPlaylist(playlist.id) }
                                    .padding(vertical = 10.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.PlaylistAdd, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(playlist.name, color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                    if (playlist.description.isNotBlank()) {
                                        Text(playlist.description, color = TextMuted, fontSize = 11.sp, maxLines = 1)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
fun TrackDetailsDialog(
    track: Track,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepCharcoal,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = HiResGold)
                Spacer(Modifier.width(8.dp))
                Text("Audio Engine Pipeline", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DetailRow("Audio Format", "${track.format} Lossless", isAccent = true)
                DetailRow("Resolution", "${track.bitDepth}-Bit / ${track.sampleRate / 1000} kHz")
                DetailRow("Bitrate", "${track.bitrateKbps} kbps")
                DetailRow("Channels", "2.0 Stereo (Lossless PCM)")
                DetailRow("Audio Pipeline", "Direct Hi-Res Output (Bit-Perfect)")
                DetailRow("File Location", track.path.ifEmpty { track.folder }, isMonospace = true)
                DetailRow("Genre", track.genre)
                DetailRow("Year", track.year.toString())
                DetailRow("Play Count", "${track.playCount} times")
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceElevated, contentColor = TextPrimary)
            ) {
                Text("Dismiss")
            }
        }
    )
}

@Composable
private fun DetailRow(label: String, value: String, isAccent: Boolean = false, isMonospace: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = TextSecondary, fontSize = 12.sp)
        Text(
            text = value,
            color = if (isAccent) HiResGold else TextPrimary,
            fontSize = 12.sp,
            fontWeight = if (isAccent) FontWeight.Bold else FontWeight.Medium,
            fontFamily = if (isMonospace) FontFamily.Monospace else FontFamily.Default,
            maxLines = 1
        )
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = NeonCyan,
    unfocusedBorderColor = SurfaceBorder,
    focusedLabelColor = NeonCyan,
    unfocusedLabelColor = TextSecondary,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    cursorColor = NeonCyan
)
