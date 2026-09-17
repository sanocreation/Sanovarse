package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.example.data.model.MediaType
import com.example.ui.theme.Badge1080p
import com.example.ui.theme.Badge4K
import com.example.ui.theme.Badge720p
import com.example.ui.theme.BadgeDualAudio
import com.example.ui.theme.CineBorder
import com.example.ui.theme.CinePrimary
import com.example.ui.theme.CineSecondary
import com.example.ui.theme.CineSurface
import com.example.ui.theme.CineSurfaceElevated
import com.example.ui.theme.CineTextMuted
import com.example.ui.theme.CineTextSecondary

@Composable
fun MediaCard(
  item: MediaItem,
  onClick: () -> Unit,
  isBookmarked: Boolean,
  onBookmarkToggle: () -> Unit,
  modifier: Modifier = Modifier,
  rankNumber: Int? = null
) {
  Column(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
      .testTag("media_card_${item.id}")
  ) {
    // Poster container
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(2f / 3f)
        .clip(RoundedCornerShape(12.dp))
        .background(CineSurfaceElevated)
        .border(1.dp, CineBorder, RoundedCornerShape(12.dp))
    ) {
      SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
          .data(item.posterUrl)
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
            CircularProgressIndicator(
              modifier = Modifier.size(24.dp),
              color = CinePrimary,
              strokeWidth = 2.dp
            )
          }
        },
        error = {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.verticalGradient(
                  colors = listOf(
                    CineSurfaceElevated,
                    CinePrimary.copy(alpha = 0.25f),
                    CineSurface
                  )
                )
              )
              .padding(8.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Text(
                text = item.category.icon,
                fontSize = 24.sp
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = item.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }
        }
      )

      // Gradient overlay for contrast
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              colors = listOf(
                Color.Black.copy(alpha = 0.5f),
                Color.Transparent,
                Color.Black.copy(alpha = 0.85f)
              )
            )
          )
      )

      // Quality badge (Top Left)
      val badgeColor = when {
        item.qualityBadge.contains("4K") -> Badge4K
        item.qualityBadge.contains("1080p") -> Badge1080p
        else -> Badge720p
      }
      Surface(
        color = badgeColor.copy(alpha = 0.9f),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
          .padding(8.dp)
          .align(Alignment.TopStart)
      ) {
        Text(
          text = item.qualityBadge,
          color = Color.White,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
      }

      // Bookmark action (Top Right)
      Box(
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(4.dp)
          .size(34.dp)
          .clip(CircleShape)
          .background(Color.Black.copy(alpha = 0.6f))
          .clickable { onBookmarkToggle() },
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
          contentDescription = "Bookmark",
          tint = if (isBookmarked) CineSecondary else Color.White,
          modifier = Modifier.size(18.dp)
        )
      }

      // Rating badge (Bottom Left)
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .align(Alignment.BottomStart)
          .padding(8.dp)
          .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
          .padding(horizontal = 6.dp, vertical = 2.dp)
      ) {
        Icon(
          imageVector = Icons.Filled.Star,
          contentDescription = null,
          tint = CineSecondary,
          modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
          text = String.format("%.1f", item.rating),
          color = CineSecondary,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }

      // Media Type badge (Bottom Right)
      val typeLabel = when (item.type) {
        MediaType.MOVIE -> "Movie"
        MediaType.SERIES -> "Series"
        MediaType.WEBSERIES -> "Web Series"
        MediaType.ANIME -> "Anime"
        else -> ""
      }
      Surface(
        color = CinePrimary.copy(alpha = 0.9f),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(8.dp)
      ) {
        Text(
          text = typeLabel,
          color = Color.White,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
        )
      }

      // Giant numeral rank overlay for Top 10
      if (rankNumber != null) {
        Text(
          text = "$rankNumber",
          color = Color.White.copy(alpha = 0.85f),
          fontSize = 46.sp,
          fontWeight = FontWeight.Black,
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(start = 4.dp, bottom = 26.dp)
            .shadow(12.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Title
    Text(
      text = item.title,
      style = MaterialTheme.typography.bodyMedium.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp
      ),
      color = Color.White,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )

    // Meta line (Year · Genre)
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth()
    ) {
      Text(
        text = "${item.year}",
        color = CineTextMuted,
        fontSize = 11.sp
      )
      Text(
        text = " • ",
        color = CineTextMuted,
        fontSize = 11.sp
      )
      Text(
        text = item.genres.firstOrNull() ?: "Cinema",
        color = CineTextSecondary,
        fontSize = 11.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.weight(1f)
      )
    }

    // Audio / Dual Audio badge text
    if (item.audioLanguage.isNotBlank()) {
      Text(
        text = item.audioLanguage,
        color = BadgeDualAudio,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}
