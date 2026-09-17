package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.data.model.MediaItem
import com.example.ui.theme.BadgeDualAudio
import com.example.ui.theme.CineBorder
import com.example.ui.theme.CinePrimary
import com.example.ui.theme.CineSecondary
import com.example.ui.theme.CineSurfaceElevated
import com.example.ui.theme.CineTextMuted
import com.example.ui.theme.CineTextSecondary
import kotlinx.coroutines.delay

@Composable
fun HeroCarousel(
  items: List<MediaItem>,
  onItemClick: (MediaItem) -> Unit,
  onBookmarkToggle: (MediaItem) -> Unit,
  isBookmarked: (String) -> Boolean,
  modifier: Modifier = Modifier
) {
  if (items.isEmpty()) return

  var currentIndex by remember { mutableIntStateOf(0) }

  // Auto-scroll every 6 seconds
  LaunchedEffect(items.size) {
    while (true) {
      delay(6000)
      if (items.isNotEmpty()) {
        currentIndex = (currentIndex + 1) % items.size
      }
    }
  }

  val current = items[currentIndex]

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(340.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(CineSurfaceElevated)
      .border(1.dp, CineBorder, RoundedCornerShape(16.dp))
      .clickable { onItemClick(current) }
      .testTag("hero_carousel")
  ) {
    // Backdrop with smooth transition
    AnimatedContent(
      targetState = current,
      transitionSpec = { fadeIn() togetherWith fadeOut() },
      label = "hero_bg"
    ) { item ->
      SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
          .data(item.backdropUrl.ifBlank { item.posterUrl })
          .crossfade(true)
          .build(),
        contentDescription = item.title,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
        loading = {
          Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
          ) {
            CircularProgressIndicator(color = CinePrimary, strokeWidth = 2.dp)
          }
        },
        error = {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(CineSurfaceElevated),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = item.title,
              color = CineTextSecondary,
              fontWeight = FontWeight.Bold,
              fontSize = 20.sp
            )
          }
        }
      )
    }

    // Cinematic dark gradient scrims
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            colors = listOf(
              Color.Black.copy(alpha = 0.35f),
              Color.Black.copy(alpha = 0.6f),
              Color.Black.copy(alpha = 0.95f)
            )
          )
        )
    )
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.horizontalGradient(
            colors = listOf(
              Color.Black.copy(alpha = 0.85f),
              Color.Transparent
            )
          )
        )
    )

    // Content
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      verticalArrangement = Arrangement.Bottom
    ) {
      // Featured badge
      Surface(
        color = CinePrimary,
        shape = RoundedCornerShape(20.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(Color.White)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "FEATURED SPOTLIGHT",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Title
      Text(
        text = current.title,
        color = Color.White,
        fontSize = 24.sp,
        fontWeight = FontWeight.Black,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(4.dp))

      // Metadata pills
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Rating
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = CineSecondary,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = String.format("%.1f", current.rating),
            color = CineSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Text(text = "${current.year}", color = CineTextMuted, fontSize = 12.sp)
        Text(text = "•", color = CineTextMuted, fontSize = 12.sp)
        Text(text = current.duration, color = CineTextMuted, fontSize = 12.sp)
        Text(text = "•", color = CineTextMuted, fontSize = 12.sp)
        Text(
          text = current.qualityBadge,
          color = CinePrimary,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = current.audioLanguage,
        color = BadgeDualAudio,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = current.storyline,
        color = CineTextSecondary,
        fontSize = 12.sp,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        lineHeight = 16.sp
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Action buttons
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        Button(
          onClick = { onItemClick(current) },
          colors = ButtonDefaults.buttonColors(containerColor = Color.White),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.testTag("hero_watch_btn")
        ) {
          Icon(
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Watch / Download",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        val bookmarked = isBookmarked(current.id)
        OutlinedButton(
          onClick = { onBookmarkToggle(current) },
          shape = RoundedCornerShape(10.dp),
          border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = Brush.horizontalGradient(listOf(CineBorder, CineBorder))
          ),
          colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Black.copy(alpha = 0.5f)
          ),
          modifier = Modifier.testTag("hero_bookmark_btn")
        ) {
          Icon(
            imageVector = if (bookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
            contentDescription = null,
            tint = if (bookmarked) CineSecondary else Color.White,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (bookmarked) "In List" else "Watchlist",
            color = Color.White,
            fontSize = 12.sp
          )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Dots indicator
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          items.indices.forEach { index ->
            Box(
              modifier = Modifier
                .height(4.dp)
                .width(if (index == currentIndex) 16.dp else 6.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (index == currentIndex) CinePrimary else Color.White.copy(alpha = 0.3f))
                .clickable { currentIndex = index }
            )
          }
        }
      }
    }
  }
}
