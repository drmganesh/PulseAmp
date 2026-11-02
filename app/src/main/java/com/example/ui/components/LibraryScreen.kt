package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.LibraryTab
import com.example.data.model.Playlist
import com.example.data.model.SortType
import com.example.data.model.Track
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

@Composable
fun LibraryScreen(
    currentTab: LibraryTab,
    tracks: List<Track>,
    allTracks: List<Track>,
    playlists: List<Playlist>,
    searchQuery: String,
    sortType: SortType,
    currentPlayingTrack: Track?,
    isPlaying: Boolean,
    onSelectTab: (LibraryTab) -> Unit,
    onSearchChange: (String) -> Unit,
    onSortChange: (SortType) -> Unit,
    onPlayTrack: (Track, List<Track>) -> Unit,
    onToggleFavorite: (Track) -> Unit,
    onOpenPlaylist: (Playlist) -> Unit,
    onCreatePlaylist: () -> Unit,
    onOpenEqualizer: () -> Unit,
    onOpenSpatialAudio: () -> Unit,
    onOpenTagEditor: (Track) -> Unit,
    onOpenAddToPlaylist: (Track) -> Unit,
    onScanStorage: () -> Unit,
    onOpenSettings: () -> Unit = {},
    onOpenCarMode: () -> Unit = {},
    isMultiSelectMode: Boolean = false,
    selectedTrackIds: Set<Long> = emptySet(),
    onToggleMultiSelectMode: (Boolean?) -> Unit = {},
    onToggleTrackSelection: (Long) -> Unit = {},
    onSelectAllTracks: (List<Track>) -> Unit = {},
    onClearSelection: () -> Unit = {},
    onBatchPlay: (List<Track>) -> Unit = {},
    onBatchAddToQueue: (List<Track>) -> Unit = {},
    onBatchAddToPlaylist: () -> Unit = {},
    onBatchFavorite: (List<Track>) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isSearchActive by remember { mutableStateOf(false) }
    var isSortMenuOpen by remember { mutableStateOf(false) }
    var trackForDetails by remember { mutableStateOf<Track?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CarbonBlack)
    ) {
        // App Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // PulseAmp Branding
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(NeonCyan, ElectricViolet))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = CarbonBlack,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        text = "PULSEAMP",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Audiophile Engine",
                        color = NeonCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Top action buttons (Search, Multi-Select, Drive Mode, Settings, 360, EQ)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { isSearchActive = !isSearchActive }, modifier = Modifier.testTag("search_toggle_button")) {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = if (isSearchActive) NeonCyan else TextPrimary)
                }
                IconButton(onClick = { onToggleMultiSelectMode(null) }, modifier = Modifier.testTag("top_multi_select_button")) {
                    Icon(
                        imageVector = if (isMultiSelectMode) Icons.Default.CheckCircle else Icons.Default.Checklist,
                        contentDescription = "Multi Select",
                        tint = if (isMultiSelectMode) NeonCyan else TextSecondary
                    )
                }
                IconButton(onClick = onOpenCarMode, modifier = Modifier.testTag("top_car_mode_button")) {
                    Icon(Icons.Default.DirectionsCar, contentDescription = "Drive Mode", tint = TextSecondary)
                }
                IconButton(onClick = onOpenSettings, modifier = Modifier.testTag("top_settings_button")) {
                    Icon(Icons.Default.Tune, contentDescription = "Audio Settings", tint = TextSecondary)
                }
                IconButton(onClick = onOpenSpatialAudio, modifier = Modifier.testTag("top_spatial_audio_button")) {
                    Icon(Icons.Default.SurroundSound, contentDescription = "360 Spatial Audio", tint = ElectricViolet)
                }
                IconButton(onClick = onOpenEqualizer, modifier = Modifier.testTag("top_equalizer_button")) {
                    Icon(Icons.Default.GraphicEq, contentDescription = "Equalizer", tint = NeonCyan)
                }
            }
        }

        // Collapsible Search Input Field
        if (isSearchActive) {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Search songs, artists, formats, genres...", color = TextMuted, fontSize = 13.sp) },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary)
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = NeonCyan
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("library_search_input")
                )
            }
        }

        // Tab Navigation Row
        ScrollableTabRow(
            selectedTabIndex = currentTab.ordinal,
            containerColor = DeepCharcoal,
            contentColor = NeonCyan,
            edgePadding = 12.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[currentTab.ordinal]),
                    height = 3.dp,
                    color = NeonCyan
                )
            }
        ) {
            LibraryTab.entries.forEach { tab ->
                val selected = currentTab == tab
                Tab(
                    selected = selected,
                    onClick = { onSelectTab(tab) },
                    text = {
                        Text(
                            text = tab.displayName.uppercase(),
                            fontSize = 12.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            color = if (selected) NeonCyan else TextSecondary,
                            letterSpacing = 1.sp
                        )
                    },
                    modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                )
            }
        }

        // Subheader with sort selector and count
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (currentTab) {
                    LibraryTab.TRACKS -> "${tracks.size} TRACKS"
                    LibraryTab.HI_RES -> "${tracks.size} HI-RES LOSSLESS TRACKS"
                    LibraryTab.PLAYLISTS -> "${playlists.size + 4} PLAYLISTS"
                    LibraryTab.ALBUMS -> "${tracks.map { it.album }.distinct().size} ALBUMS"
                    LibraryTab.ARTISTS -> "${tracks.map { it.artist }.distinct().size} ARTISTS"
                    LibraryTab.FOLDERS -> "${tracks.map { it.folder }.distinct().size} FOLDERS"
                },
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )

            if (currentTab in listOf(LibraryTab.TRACKS, LibraryTab.HI_RES)) {
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { isSortMenuOpen = true }
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Sort, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = sortType.displayName,
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    DropdownMenu(
                        expanded = isSortMenuOpen,
                        onDismissRequest = { isSortMenuOpen = false },
                        modifier = Modifier.background(DeepCharcoal)
                    ) {
                        SortType.entries.forEach { sort ->
                            DropdownMenuItem(
                                text = { Text(sort.displayName, color = if (sortType == sort) NeonCyan else TextPrimary) },
                                onClick = {
                                    onSortChange(sort)
                                    isSortMenuOpen = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Tab Content
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (currentTab) {
                LibraryTab.TRACKS, LibraryTab.HI_RES -> {
                    TrackListView(
                        tracks = tracks,
                        currentPlayingTrack = currentPlayingTrack,
                        isPlaying = isPlaying,
                        isMultiSelectMode = isMultiSelectMode,
                        selectedTrackIds = selectedTrackIds,
                        onToggleSelect = onToggleTrackSelection,
                        onPlayTrack = onPlayTrack,
                        onToggleFavorite = onToggleFavorite,
                        onOpenAddToPlaylist = onOpenAddToPlaylist,
                        onOpenTagEditor = onOpenTagEditor,
                        onOpenDetails = { trackForDetails = it }
                    )
                }

                LibraryTab.ALBUMS -> {
                    AlbumGridView(
                        tracks = allTracks,
                        onSelectAlbum = { albumTracks ->
                            if (albumTracks.isNotEmpty()) onPlayTrack(albumTracks.first(), albumTracks)
                        }
                    )
                }

                LibraryTab.ARTISTS -> {
                    ArtistListView(
                        tracks = allTracks,
                        onSelectArtist = { artistTracks ->
                            if (artistTracks.isNotEmpty()) onPlayTrack(artistTracks.first(), artistTracks)
                        }
                    )
                }

                LibraryTab.FOLDERS -> {
                    FolderListView(
                        tracks = allTracks,
                        onSelectFolder = { folderTracks ->
                            if (folderTracks.isNotEmpty()) onPlayTrack(folderTracks.first(), folderTracks)
                        }
                    )
                }

                LibraryTab.PLAYLISTS -> {
                    PlaylistListView(
                        playlists = playlists,
                        allTracks = allTracks,
                        onCreatePlaylist = onCreatePlaylist,
                        onOpenPlaylist = onOpenPlaylist
                    )
                }
            }

            // Floating Batch Action Bar when Multi-Select is active
            if (isMultiSelectMode) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceElevated,
                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 76.dp)
                        .fillMaxWidth()
                        .testTag("batch_action_bar")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { onToggleMultiSelectMode(false) }) {
                                Icon(Icons.Default.Close, contentDescription = "Exit Selection", tint = TextMuted)
                            }
                            Text(
                                text = "${selectedTrackIds.size} Selected",
                                color = NeonCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (selectedTrackIds.size == tracks.size) "Clear" else "All",
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .clickable {
                                        if (selectedTrackIds.size == tracks.size) onClearSelection()
                                        else onSelectAllTracks(tracks)
                                    }
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            )

                            IconButton(
                                onClick = { onBatchPlay(tracks) },
                                enabled = selectedTrackIds.isNotEmpty(),
                                modifier = Modifier.testTag("batch_play_button")
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Play Selected", tint = NeonCyan)
                            }

                            IconButton(
                                onClick = { onBatchAddToQueue(tracks) },
                                enabled = selectedTrackIds.isNotEmpty(),
                                modifier = Modifier.testTag("batch_queue_button")
                            ) {
                                Icon(Icons.Default.QueueMusic, contentDescription = "Queue Selected", tint = ElectricViolet)
                            }

                            IconButton(
                                onClick = onBatchAddToPlaylist,
                                enabled = selectedTrackIds.isNotEmpty(),
                                modifier = Modifier.testTag("batch_playlist_button")
                            ) {
                                Icon(Icons.Default.PlaylistAdd, contentDescription = "Add to Playlist", tint = HiResGold)
                            }

                            IconButton(
                                onClick = { onBatchFavorite(tracks) },
                                enabled = selectedTrackIds.isNotEmpty(),
                                modifier = Modifier.testTag("batch_favorite_button")
                            ) {
                                Icon(Icons.Default.Favorite, contentDescription = "Favorite Selected", tint = NeonPink)
                            }
                        }
                    }
                }
            }
        }
    }

    trackForDetails?.let { track ->
        TrackDetailsDialog(track = track, onDismiss = { trackForDetails = null })
    }
}

@Composable
private fun TrackListView(
    tracks: List<Track>,
    currentPlayingTrack: Track?,
    isPlaying: Boolean,
    isMultiSelectMode: Boolean = false,
    selectedTrackIds: Set<Long> = emptySet(),
    onToggleSelect: (Long) -> Unit = {},
    onPlayTrack: (Track, List<Track>) -> Unit,
    onToggleFavorite: (Track) -> Unit,
    onOpenAddToPlaylist: (Track) -> Unit,
    onOpenTagEditor: (Track) -> Unit,
    onOpenDetails: (Track) -> Unit
) {
    if (tracks.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No tracks found.", color = TextMuted)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = if (isMultiSelectMode) 140.dp else 80.dp)
        ) {
            items(tracks, key = { it.id }) { track ->
                val isCurrent = currentPlayingTrack?.id == track.id
                val isSelected = selectedTrackIds.contains(track.id)
                TrackRowItem(
                    track = track,
                    isCurrent = isCurrent,
                    isPlaying = isPlaying,
                    isMultiSelectMode = isMultiSelectMode,
                    isSelected = isSelected,
                    onToggleSelect = { onToggleSelect(track.id) },
                    onClick = {
                        if (isMultiSelectMode) {
                            onToggleSelect(track.id)
                        } else {
                            onPlayTrack(track, tracks)
                        }
                    },
                    onToggleFavorite = { onToggleFavorite(track) },
                    onOpenAddToPlaylist = { onOpenAddToPlaylist(track) },
                    onOpenTagEditor = { onOpenTagEditor(track) },
                    onOpenDetails = { onOpenDetails(track) }
                )
            }
        }
    }
}

@Composable
private fun TrackRowItem(
    track: Track,
    isCurrent: Boolean,
    isPlaying: Boolean,
    isMultiSelectMode: Boolean = false,
    isSelected: Boolean = false,
    onToggleSelect: () -> Unit = {},
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onOpenAddToPlaylist: () -> Unit,
    onOpenTagEditor: () -> Unit,
    onOpenDetails: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SurfaceElevated else if (isCurrent) SurfaceElevated else DeepCharcoal
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isSelected) NeonCyan else if (isCurrent) NeonCyan.copy(alpha = 0.8f) else SurfaceBorder,
                RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .testTag("track_item_${track.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isMultiSelectMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onToggleSelect() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = NeonCyan,
                        uncheckedColor = SurfaceBorder,
                        checkmarkColor = CarbonBlack
                    ),
                    modifier = Modifier.padding(end = 6.dp).testTag("track_checkbox_${track.id}")
                )
            }
            // Thumbnail with Playing indicator
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceDark),
                contentAlignment = Alignment.Center
            ) {
                if (track.albumArtResId != null) {
                    Image(
                        painter = painterResource(id = track.albumArtResId),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (!track.albumArtUri.isNullOrEmpty()) {
                    AsyncImage(
                        model = track.albumArtUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = NeonCyan)
                }

                if (isCurrent && isPlaying) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.45f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.GraphicEq, contentDescription = "Playing", tint = NeonCyan, modifier = Modifier.size(22.dp))
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    color = if (isCurrent) NeonCyan else TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${track.artist} • ${track.durationFormatted}",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                    if (track.isHiRes) {
                        Spacer(Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(HiResGold.copy(alpha = 0.18f))
                                .padding(horizontal = 4.dp, vertical = 0.5.dp)
                        ) {
                            Text(
                                text = "${track.format} ${track.bitDepth}B",
                                color = HiResGold,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Favorite Button
            IconButton(onClick = onToggleFavorite, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = if (track.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (track.isFavorite) NeonPink else TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Overflow Options Menu
            Box {
                IconButton(onClick = { showMenu = true }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More", tint = TextSecondary, modifier = Modifier.size(18.dp))
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(DeepCharcoal)
                ) {
                    DropdownMenuItem(
                        text = { Text("Add to Playlist", color = TextPrimary) },
                        onClick = {
                            onOpenAddToPlaylist()
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Edit Tags", color = TextPrimary) },
                        onClick = {
                            onOpenTagEditor()
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Audio Engine Specs", color = HiResGold) },
                        onClick = {
                            onOpenDetails()
                            showMenu = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AlbumGridView(
    tracks: List<Track>,
    onSelectAlbum: (List<Track>) -> Unit
) {
    val albums = tracks.groupBy { it.album }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(albums.entries.toList()) { (albumName, albumTracks) ->
            val firstTrack = albumTracks.first()
            Card(
                colors = CardDefaults.cardColors(containerColor = DeepCharcoal),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectAlbum(albumTracks) }
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .background(SurfaceDark),
                        contentAlignment = Alignment.Center
                    ) {
                        if (firstTrack.albumArtResId != null) {
                            Image(
                                painter = painterResource(id = firstTrack.albumArtResId),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(Icons.Default.Album, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(48.dp))
                        }
                    }

                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = albumName,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = firstTrack.artist,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                        Text(
                            text = "${albumTracks.size} Songs",
                            color = NeonCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ArtistListView(
    tracks: List<Track>,
    onSelectArtist: (List<Track>) -> Unit
) {
    val artists = tracks.groupBy { it.artist }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(artists.entries.toList()) { (artistName, artistTracks) ->
            Card(
                colors = CardDefaults.cardColors(containerColor = DeepCharcoal),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().clickable { onSelectArtist(artistTracks) }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(SurfaceElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = ElectricViolet)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(artistName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("${artistTracks.size} Tracks", color = TextSecondary, fontSize = 11.sp)
                    }
                    Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = NeonCyan)
                }
            }
        }
    }
}

@Composable
private fun FolderListView(
    tracks: List<Track>,
    onSelectFolder: (List<Track>) -> Unit
) {
    val folders = tracks.groupBy { it.folder }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(folders.entries.toList()) { (folderPath, folderTracks) ->
            Card(
                colors = CardDefaults.cardColors(containerColor = DeepCharcoal),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().clickable { onSelectFolder(folderTracks) }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Folder, contentDescription = null, tint = HiResGold)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(folderPath, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                        Text("${folderTracks.size} Audio Files", color = TextSecondary, fontSize = 11.sp)
                    }
                    Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = NeonCyan)
                }
            }
        }
    }
}

@Composable
private fun PlaylistListView(
    playlists: List<Playlist>,
    allTracks: List<Track>,
    onCreatePlaylist: () -> Unit,
    onOpenPlaylist: (Playlist) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Create Playlist Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCreatePlaylist() }
                    .testTag("create_playlist_button")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(NeonCyan),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PlaylistAdd, contentDescription = null, tint = CarbonBlack)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Create New Playlist", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Build customized high-res playlists", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }

        // Smart Playlists Section
        item {
            Text(
                "SMART POWERAMP PLAYLISTS",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }

        // Smart Favorites
        item {
            val favTracks = allTracks.filter { it.isFavorite }
            SmartPlaylistCard(
                title = "Favorites",
                subtitle = "${favTracks.size} Liked Songs",
                icon = Icons.Default.Favorite,
                iconTint = NeonPink,
                onClick = {
                    onOpenPlaylist(Playlist(id = -1, name = "Favorites", description = "Liked Songs", trackCount = favTracks.size))
                }
            )
        }

        // Smart Hi-Res Master
        item {
            val hiResTracks = allTracks.filter { it.isHiRes }
            SmartPlaylistCard(
                title = "Hi-Res Master Collection",
                subtitle = "${hiResTracks.size} Lossless FLAC & DSD Tracks",
                icon = Icons.Default.Stars,
                iconTint = HiResGold,
                onClick = {
                    onOpenPlaylist(Playlist(id = -2, name = "Hi-Res Master Collection", description = "Lossless Master Files", trackCount = hiResTracks.size))
                }
            )
        }

        // Custom Playlists Section
        item {
            Text(
                "CUSTOM PLAYLISTS (${playlists.size})",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }

        items(playlists) { playlist ->
            Card(
                colors = CardDefaults.cardColors(containerColor = DeepCharcoal),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(10.dp))
                    .clickable { onOpenPlaylist(playlist) }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.MusicNote, contentDescription = null, tint = NeonCyan)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(playlist.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        if (playlist.description.isNotBlank()) {
                            Text(playlist.description, color = TextSecondary, fontSize = 11.sp, maxLines = 1)
                        }
                    }
                    Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = NeonCyan)
                }
            }
        }
    }
}

@Composable
private fun SmartPlaylistCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DeepCharcoal),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SurfaceBorder, RoundedCornerShape(10.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(subtitle, color = TextSecondary, fontSize = 11.sp)
            }
            Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = NeonCyan)
        }
    }
}
