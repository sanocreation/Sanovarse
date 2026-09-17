package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tv
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.data.model.DownloadOption
import com.example.data.model.MediaItem
import com.example.data.model.ServerLink
import com.example.data.model.StreamSource
import com.example.data.repository.MovieRepository
import com.example.ui.components.MediaCard
import com.example.ui.theme.Badge1080p
import com.example.ui.theme.Badge4K
import com.example.ui.theme.Badge720p
import com.example.ui.theme.BadgeDualAudio
import com.example.ui.theme.CineAccentGreen
import com.example.ui.theme.CineBg
import com.example.ui.theme.CineBorder
import com.example.ui.theme.CinePrimary
import com.example.ui.theme.CineSecondary
import com.example.ui.theme.CineSurface
import com.example.ui.theme.CineSurfaceElevated
import com.example.ui.theme.CineTextMuted
import com.example.ui.theme.CineTextSecondary

@Composable
fun DetailScreen(
  item: MediaItem,
  onBackClick: () -> Unit,
  onItemClick: (MediaItem) -> Unit,
  onBookmarkToggle: () -> Unit,
  isBookmarked: Boolean,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var selectedSeasonIndex by remember { mutableIntStateOf(0) }
  var showStreamPlayer by remember { mutableStateOf(false) }
  var activeStreamSource by remember { mutableStateOf<StreamSource?>(null) }
  var selectedDownloadOption by remember { mutableStateOf<DownloadOption?>(null) }

  // More like this
  val similarItems = remember(item.id) {
    MovieRepository.allItems
      .filter { it.id != item.id && (it.category == item.category || it.genres.any { g -> item.genres.contains(g) }) }
      .take(8)
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(CineBg)
      .testTag("detail_screen")
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(bottom = 40.dp)
    ) {
      // Hero Backdrop Banner
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
        ) {
          SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
              .data(item.backdropUrl)
              .crossfade(true)
              .build(),
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            loading = {
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(CineSurface),
                contentAlignment = Alignment.Center
              ) {
                CircularProgressIndicator(color = CinePrimary, strokeWidth = 2.dp)
              }
            }
          )

          // Gradient overlay
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.verticalGradient(
                  colors = listOf(
                    Color.Black.copy(alpha = 0.4f),
                    Color.Transparent,
                    CineBg.copy(alpha = 0.85f),
                    CineBg
                  ),
                  startY = 0f,
                  endY = Float.POSITIVE_INFINITY
                )
              )
          )

          // Quick Play Watch Button in hero center
          Box(
            modifier = Modifier
              .align(Alignment.Center)
              .size(64.dp)
              .clip(CircleShape)
              .background(CinePrimary.copy(alpha = 0.9f))
              .clickable {
                activeStreamSource = item.streamSources.firstOrNull()
                showStreamPlayer = true
              },
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = "Watch Online",
              tint = Color.White,
              modifier = Modifier.size(38.dp)
            )
          }

          // Floating Badges
          Row(
            modifier = Modifier
              .align(Alignment.BottomStart)
              .padding(start = 16.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              color = when {
                item.qualityBadge.contains("4K") -> Badge4K
                item.qualityBadge.contains("1080p") -> Badge1080p
                else -> Badge720p
              },
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = item.qualityBadge,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }

            Surface(
              color = CineSecondary,
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = item.category.label,
                color = Color.Black,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }
        }
      }

      // Metadata section
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
        ) {
          Text(
            text = item.title,
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 22.sp,
            lineHeight = 28.sp
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Meta row (Year, Rating, Duration, Language)
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text(
              text = "${item.year}",
              color = CineTextSecondary,
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium
            )
            Text(text = "•", color = CineTextMuted)
            Text(
              text = item.duration,
              color = CineTextSecondary,
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium
            )
            Text(text = "•", color = CineTextMuted)
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = CineSecondary,
                modifier = Modifier.size(15.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "${item.rating}",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
              Text(text = "/10", color = CineTextMuted, fontSize = 11.sp)
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Audio Language Badge
          Surface(
            color = CineSurfaceElevated,
            shape = RoundedCornerShape(6.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CineBorder)
          ) {
            Text(
              text = "🔊 ${item.audioLanguage}",
              color = BadgeDualAudio,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Main Action Buttons: Watch Online + Trailer + Watchlist
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Watch Online Button
            Button(
              onClick = {
                activeStreamSource = item.streamSources.firstOrNull()
                showStreamPlayer = true
              },
              colors = ButtonDefaults.buttonColors(containerColor = CineAccentGreen),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.weight(1.3f)
            ) {
              Icon(imageVector = Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(text = "Watch Online", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            // Trailer Button
            Button(
              onClick = {
                val youtubeIntent = Intent(
                  Intent.ACTION_VIEW,
                  Uri.parse("https://www.youtube.com/watch?v=${item.trailerYoutubeId}")
                )
                try {
                  context.startActivity(youtubeIntent)
                } catch (e: Exception) {
                  Toast.makeText(context, "Opening YouTube trailer...", Toast.LENGTH_SHORT).show()
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = CinePrimary),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.weight(1f)
            ) {
              Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(text = "Trailer", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            // Watchlist Button
            OutlinedButton(
              onClick = onBookmarkToggle,
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.outlinedButtonColors(containerColor = CineSurfaceElevated),
              border = androidx.compose.foundation.BorderStroke(1.dp, CineBorder)
            ) {
              Icon(
                imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                contentDescription = null,
                tint = if (isBookmarked) CineSecondary else Color.White,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }

      // Storyline
      item {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Storyline & Synopsis",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = item.storyline,
            color = CineTextSecondary,
            fontSize = 13.sp,
            lineHeight = 20.sp
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Genre tags
          LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(item.genres) { g ->
              Surface(
                color = CineSurfaceElevated,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CineBorder)
              ) {
                Text(
                  text = g,
                  color = Color.White,
                  fontSize = 11.sp,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
              }
            }
          }
        }
      }

      // HDHub4U Signature Watch & Download Hub Card
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          colors = CardDefaults.cardColors(containerColor = CineSurface),
          shape = RoundedCornerShape(16.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, CineBorder)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            // Card Title
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.CloudDownload,
                contentDescription = null,
                tint = CinePrimary,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "HDHub4u Watch & Download Hub",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
              )
            }
            Text(
              text = "⚡ Instant 10Gbps Cloud Servers • Multi-Quality Downloads • Online Stream",
              color = CineTextMuted,
              fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Section 1: Watch Online Streaming Servers
            Surface(
              color = CineSurfaceElevated,
              shape = RoundedCornerShape(12.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, CineBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "▶️ Watch Online Fast Servers",
                    color = CineSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f)
                  )
                  Surface(
                    color = CineAccentGreen.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                  ) {
                    Text(
                      text = "⚡ NO ADS",
                      color = CineAccentGreen,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Black,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  item.streamSources.forEach { stream ->
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CineBg)
                        .clickable {
                          activeStreamSource = stream
                          showStreamPlayer = true
                        }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.PlayCircle,
                        contentDescription = null,
                        tint = CineAccentGreen,
                        modifier = Modifier.size(16.dp)
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                        text = stream.serverName,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                      )
                      Surface(
                        color = CinePrimary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                      ) {
                        Text(
                          text = stream.quality,
                          color = CinePrimary,
                          fontSize = 10.sp,
                          fontWeight = FontWeight.Bold,
                          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                      }
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = "Stream",
                        color = CineAccentGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // For Series/Anime with seasons
            if (item.seasons.isNotEmpty()) {
              Text(
                text = "Select Season & Episodes",
                color = CineSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
              Spacer(modifier = Modifier.height(6.dp))
              LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(item.seasons.indices.toList()) { sIdx ->
                  val season = item.seasons[sIdx]
                  val isSelected = selectedSeasonIndex == sIdx
                  Surface(
                    color = if (isSelected) CinePrimary else CineSurfaceElevated,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                      .clip(RoundedCornerShape(8.dp))
                      .clickable { selectedSeasonIndex = sIdx }
                  ) {
                    Text(
                      text = "Season ${season.seasonNumber} (${season.episodesCount} Ep)",
                      color = if (isSelected) Color.White else CineTextSecondary,
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              // Episodes list
              val currentSeason = item.seasons.getOrNull(selectedSeasonIndex)
              if (currentSeason != null) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  currentSeason.episodes.forEach { ep ->
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CineSurfaceElevated)
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                      Text(
                        text = "Ep ${ep.episodeNumber}",
                        color = CineSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                        text = ep.title,
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                      )

                      // Watch Episode button
                      IconButton(
                        onClick = {
                          activeStreamSource = StreamSource(
                            serverName = "HubStream Ep ${ep.episodeNumber}",
                            quality = "720p",
                            streamUrl = ep.streamUrl
                          )
                          showStreamPlayer = true
                        },
                        modifier = Modifier.size(28.dp)
                      ) {
                        Icon(
                          imageVector = Icons.Default.PlayArrow,
                          contentDescription = "Watch Episode",
                          tint = CineSecondary,
                          modifier = Modifier.size(18.dp)
                        )
                      }

                      // Download Episode button (triggers download!)
                      IconButton(
                        onClick = {
                          // Launch download in browser/download manager
                          try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ep.downloadLink))
                            context.startActivity(intent)
                          } catch (e: Exception) {
                            val clip = ClipData.newPlainText("Link", ep.downloadLink)
                            (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                              .setPrimaryClip(clip)
                          }
                          Toast.makeText(
                            context,
                            "⚡ Downloading Ep ${ep.episodeNumber} via HubCloud Direct!",
                            Toast.LENGTH_SHORT
                          ).show()
                        },
                        modifier = Modifier.size(28.dp)
                      ) {
                        Icon(
                          imageVector = Icons.Default.Download,
                          contentDescription = "Download Episode",
                          tint = CineAccentGreen,
                          modifier = Modifier.size(16.dp)
                        )
                      }
                    }
                  }
                }
              }
              Spacer(modifier = Modifier.height(16.dp))
            }

            // Section 2: Direct Download Links (480p, 720p, 1080p, 4K)
            Text(
              text = "📥 Direct Download Links (Full Releases)",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              item.downloadOptions.forEach { opt ->
                DownloadOptionItem(
                  option = opt,
                  title = item.title,
                  onDownloadClick = { selectedDownloadOption = opt }
                )
              }
            }
          }
        }
      }

      // Cast & Director
      item {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Star Cast & Director",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Directed by: ${item.director}",
            color = CineSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
          Spacer(modifier = Modifier.height(10.dp))
          LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(item.cast) { actor ->
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(80.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                      Brush.linearGradient(listOf(CinePrimary, CineSecondary))
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = actor.take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                  )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = actor,
                  color = CineTextSecondary,
                  fontSize = 11.sp,
                  maxLines = 2,
                  textAlign = TextAlign.Center
                )
              }
            }
          }
        }
      }

      // More Like This recommendations
      if (similarItems.isNotEmpty()) {
        item {
          Column(modifier = Modifier.padding(top = 8.dp)) {
            Text(
              text = "More Like This",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp,
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            LazyRow(
              contentPadding = PaddingValues(horizontal = 16.dp),
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              items(similarItems, key = { it.id }) { sim ->
                MediaCard(
                  item = sim,
                  onClick = { onItemClick(sim) },
                  isBookmarked = false,
                  onBookmarkToggle = {},
                  modifier = Modifier.width(135.dp)
                )
              }
            }
          }
        }
      }
    }

    // Top action bar (Back, Share)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(Color.Black.copy(alpha = 0.6f))
          .clickable { onBackClick() },
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = Color.White,
          modifier = Modifier.size(20.dp)
        )
      }

      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(Color.Black.copy(alpha = 0.6f))
          .clickable {
            val sendIntent = Intent().apply {
              action = Intent.ACTION_SEND
              putExtra(
                Intent.EXTRA_TEXT,
                "Check out ${item.title} (${item.year}) on CineVerse HDHub: ${item.storyline}"
              )
              type = "text/plain"
            }
            context.startActivity(Intent.createChooser(sendIntent, "Share via"))
          },
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Share,
          contentDescription = "Share",
          tint = Color.White,
          modifier = Modifier.size(18.dp)
        )
      }
    }

    // Interactive Online Stream Player Modal Dialog
    if (showStreamPlayer) {
      OnlineStreamPlayerDialog(
        item = item,
        initialSource = activeStreamSource ?: item.streamSources.firstOrNull(),
        onDismiss = { showStreamPlayer = false }
      )
    }

    // Interactive Download Server Picker Dialog
    selectedDownloadOption?.let { opt ->
      DownloadServerDialog(
        title = item.title,
        option = opt,
        onDismiss = { selectedDownloadOption = null }
      )
    }
  }
}

@Composable
fun DownloadOptionItem(
  option: DownloadOption,
  title: String,
  onDownloadClick: () -> Unit
) {
  Surface(
    color = CineSurfaceElevated,
    shape = RoundedCornerShape(10.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, CineBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(12.dp)
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = option.quality,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
          Spacer(modifier = Modifier.width(8.dp))
          Surface(
            color = CinePrimary.copy(alpha = 0.2f),
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = option.size,
              color = CinePrimary,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
        Text(
          text = option.codec,
          color = CineTextMuted,
          fontSize = 11.sp
        )
      }

      // Fast Server Action
      Button(
        onClick = onDownloadClick,
        colors = ButtonDefaults.buttonColors(containerColor = CineAccentGreen),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Download,
          contentDescription = null,
          modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(text = "Download", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
fun DownloadServerDialog(
  title: String,
  option: DownloadOption,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .clip(RoundedCornerShape(16.dp)),
      color = CineSurface,
      border = androidx.compose.foundation.BorderStroke(1.dp, CineBorder)
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Select Download Server",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 17.sp
            )
            Text(
              text = "$title • ${option.quality} • ${option.size}",
              color = CineSecondary,
              fontSize = 12.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = CineTextMuted)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Text(
          text = "Click any server to launch download in your browser / ADM / 1DM:",
          color = CineTextMuted,
          fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          option.servers.forEach { server ->
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(CineSurfaceElevated)
                .clickable {
                  // Direct download launch
                  try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(server.url))
                    context.startActivity(intent)
                  } catch (e: Exception) {
                    val clip = ClipData.newPlainText("Link", server.url)
                    (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                      .setPrimaryClip(clip)
                  }
                  Toast.makeText(
                    context,
                    "⚡ Download Started via ${server.name}!",
                    Toast.LENGTH_SHORT
                  ).show()
                  onDismiss()
                }
                .padding(12.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Download,
                contentDescription = null,
                tint = CineAccentGreen,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = server.name,
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
                Text(
                  text = "Resume supported • High speed cloud",
                  color = CineTextMuted,
                  fontSize = 10.sp
                )
              }
              Surface(
                color = CinePrimary.copy(alpha = 0.2f),
                shape = RoundedCornerShape(4.dp)
              ) {
                Text(
                  text = server.speedBadge,
                  color = CinePrimary,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Copy direct link fallback
        OutlinedButton(
          onClick = {
            val mainUrl = option.servers.firstOrNull()?.url ?: "https://hubcloud.club/dl"
            val clip = ClipData.newPlainText("Download Link", mainUrl)
            (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
            Toast.makeText(context, "Direct link copied to clipboard!", Toast.LENGTH_SHORT).show()
          },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
          border = androidx.compose.foundation.BorderStroke(1.dp, CineBorder)
        ) {
          Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "Copy Direct Download Link", fontSize = 12.sp)
        }
      }
    }
  }
}

@Composable
fun OnlineStreamPlayerDialog(
  item: MediaItem,
  initialSource: StreamSource?,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  var currentSource by remember { mutableStateOf(initialSource ?: item.streamSources.firstOrNull()) }
  var selectedAudio by remember { mutableStateOf("Hindi 5.1 Org") }
  var isPlayerActive by remember { mutableStateOf(true) }
  var activeVideoId by remember { mutableStateOf(item.trailerYoutubeId.ifBlank { "dQw4w9WgXcQ" }) }
  var currentEpisodeIndex by remember { mutableIntStateOf(1) }
  var webViewRef by remember { mutableStateOf<WebView?>(null) }

  // Animated pulse for "LIVE STREAMING"
  val transition = rememberInfiniteTransition(label = "pulse")
  val alpha by transition.animateFloat(
    initialValue = 0.4f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(800, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "alpha"
  )

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.96f)
        .clip(RoundedCornerShape(18.dp)),
      color = CineBg,
      border = androidx.compose.foundation.BorderStroke(1.dp, CineBorder)
    ) {
      Column {
        // Player Header
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier
            .fillMaxWidth()
            .background(CineSurface)
            .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(CineAccentGreen)
                .alpha(alpha)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "HDHub4u Live Stream Player",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
              Text(
                text = if (item.seasons != null) "${item.title} • Episode $currentEpisodeIndex" else item.title,
                color = CineSecondary,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = {
                webViewRef?.reload()
                Toast.makeText(context, "Reloading stream...", Toast.LENGTH_SHORT).show()
              },
              modifier = Modifier.size(28.dp)
            ) {
              Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reload", tint = Color.White, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
              Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
          }
        }

        // True Interactive Video Player Screen
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(Color.Black)
        ) {
          if (isPlayerActive) {
            AndroidView(
              factory = { ctx ->
                WebView(ctx).apply {
                  layoutParams = android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT
                  )
                  setBackgroundColor(android.graphics.Color.BLACK)
                  settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    mediaPlaybackRequiresUserGesture = false
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    builtInZoomControls = false
                    displayZoomControls = false
                    cacheMode = WebSettings.LOAD_DEFAULT
                  }
                  webChromeClient = WebChromeClient()
                  webViewClient = object : WebViewClient() {}
                  
                  val embedHtml = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                      <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                      <style>
                        * { margin:0; padding:0; box-sizing:border-box; background-color:#000000; }
                        html, body { width:100%; height:100%; overflow:hidden; background-color:#000000; }
                        iframe { width:100%; height:100%; border:none; }
                      </style>
                    </head>
                    <body>
                      <iframe 
                        src="https://www.youtube-nocookie.com/embed/$activeVideoId?autoplay=1&playsinline=1&controls=1&rel=0&modestbranding=1" 
                        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" 
                        allowfullscreen>
                      </iframe>
                    </body>
                    </html>
                  """.trimIndent()
                  loadDataWithBaseURL("https://www.youtube.com", embedHtml, "text/html", "UTF-8", null)
                  webViewRef = this
                }
              },
              update = { wv ->
                webViewRef = wv
              },
              modifier = Modifier.fillMaxSize()
            )
          } else {
            // Poster standby view
            SubcomposeAsyncImage(
              model = ImageRequest.Builder(context)
                .data(item.backdropUrl)
                .crossfade(true)
                .build(),
              contentDescription = null,
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize().alpha(0.8f)
            )
            Box(
              modifier = Modifier
                .align(Alignment.Center)
                .size(56.dp)
                .clip(CircleShape)
                .background(CinePrimary.copy(alpha = 0.9f))
                .clickable { isPlayerActive = true },
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Start Stream",
                tint = Color.White,
                modifier = Modifier.size(34.dp)
              )
            }
          }

          // Top stream status badge
          Row(
            modifier = Modifier
              .align(Alignment.TopStart)
              .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Surface(
              color = Color.Black.copy(alpha = 0.75f),
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = "⚡ ${currentSource?.quality ?: "1080p FHD"} • LIVE STREAM",
                color = CineSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
              )
            }
          }
        }

        // Stream Controls & Servers below player
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
        ) {
          // If TV Series or Anime, show episode selector row
          if (item.seasons != null && item.seasons.isNotEmpty()) {
            val totalEps = item.seasons.firstOrNull()?.episodes?.size ?: 8
            Text(
              text = "Choose Episode to Stream:",
              color = CineTextMuted,
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              items((1..totalEps).toList()) { epNum ->
                val isEpSelected = currentEpisodeIndex == epNum
                Surface(
                  color = if (isEpSelected) CineSecondary else CineSurfaceElevated,
                  shape = RoundedCornerShape(6.dp),
                  border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isEpSelected) CineSecondary else CineBorder
                  ),
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable {
                      currentEpisodeIndex = epNum
                      Toast.makeText(context, "Switching to Episode $epNum...", Toast.LENGTH_SHORT).show()
                      webViewRef?.reload()
                    }
                ) {
                  Text(
                    text = "Ep $epNum",
                    color = if (isEpSelected) Color.Black else Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                  )
                }
              }
            }
            Spacer(modifier = Modifier.height(10.dp))
          }

          // Server selector chips
          Text(text = "Choose Streaming Server:", color = CineTextMuted, fontSize = 11.sp)
          Spacer(modifier = Modifier.height(4.dp))
          LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(item.streamSources) { src ->
              val isSelected = currentSource == src
              Surface(
                color = if (isSelected) CinePrimary else CineSurfaceElevated,
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(
                  1.dp,
                  if (isSelected) CinePrimary else CineBorder
                ),
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .clickable {
                    currentSource = src
                    Toast.makeText(context, "Connected to ${src.serverName}", Toast.LENGTH_SHORT).show()
                    if (!isPlayerActive) isPlayerActive = true
                  }
              ) {
                Text(
                  text = src.serverName,
                  color = if (isSelected) Color.White else CineTextSecondary,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Audio stream selector
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "Audio Language:", color = CineTextMuted, fontSize = 11.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              listOf("Hindi 5.1 Org", "English Dual", "Original").forEach { aud ->
                val isSelected = selectedAudio == aud
                Surface(
                  color = if (isSelected) CineSecondary else CineSurfaceElevated,
                  shape = RoundedCornerShape(6.dp),
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable {
                      selectedAudio = aud
                      Toast.makeText(context, "Audio set to $aud", Toast.LENGTH_SHORT).show()
                    }
                ) {
                  Text(
                    text = aud,
                    color = if (isSelected) Color.Black else CineTextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Primary Action: Open Fullscreen / External Video Stream
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = {
                // Open YouTube or browser cleanly
                val cleanUrl = "https://www.youtube.com/watch?v=$activeVideoId"
                try {
                  val ytIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$activeVideoId"))
                  context.startActivity(ytIntent)
                } catch (e: Exception) {
                  val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(cleanUrl))
                  context.startActivity(webIntent)
                }
              },
              modifier = Modifier.weight(1.3f),
              colors = ButtonDefaults.buttonColors(containerColor = CineAccentGreen),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(
                imageVector = Icons.Default.PlayCircle,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Watch Fullscreen / App",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }

            OutlinedButton(
              onClick = {
                val streamUrl = currentSource?.streamUrl ?: "https://www.youtube.com/watch?v=$activeVideoId"
                try {
                  val intent = Intent(Intent.ACTION_VIEW, Uri.parse(streamUrl))
                  context.startActivity(Intent.createChooser(intent, "Stream via"))
                } catch (e: Exception) {
                  val clip = ClipData.newPlainText("Stream URL", streamUrl)
                  (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
                  Toast.makeText(context, "Stream link copied to clipboard!", Toast.LENGTH_SHORT).show()
                }
              },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
              border = androidx.compose.foundation.BorderStroke(1.dp, CineBorder)
            ) {
              Icon(imageVector = Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(text = "External Stream", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
          }
        }
      }
    }
  }
}
