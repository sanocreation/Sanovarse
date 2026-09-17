package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediaItem
import com.example.ui.theme.CinePrimary
import com.example.ui.theme.CineTextMuted
import com.example.ui.theme.CineTextSecondary

@Composable
fun SectionRow(
  title: String,
  items: List<MediaItem>,
  onItemClick: (MediaItem) -> Unit,
  onBookmarkToggle: (MediaItem) -> Unit,
  isBookmarked: (String) -> Boolean,
  modifier: Modifier = Modifier,
  subtitle: String? = null,
  isRanked: Boolean = false,
  onSeeAllClick: (() -> Unit)? = null
) {
  if (items.isEmpty()) return

  Column(modifier = modifier.fillMaxWidth()) {
    // Section Header
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
      // Crimson bar indicator
      Box(
        modifier = Modifier
          .width(4.dp)
          .height(20.dp)
          .clip(RoundedCornerShape(2.dp))
          .background(CinePrimary)
      )

      Spacer(modifier = Modifier.width(8.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
          ),
          color = Color.White
        )
        if (!subtitle.isNullOrBlank()) {
          Text(
            text = subtitle,
            color = CineTextMuted,
            fontSize = 12.sp
          )
        }
      }

      if (onSeeAllClick != null) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onSeeAllClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = "See All",
            color = CinePrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
          Spacer(modifier = Modifier.width(2.dp))
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "See All",
            tint = CinePrimary,
            modifier = Modifier.size(14.dp)
          )
        }
      }
    }

    // Horizontal list of cards
    LazyRow(
      contentPadding = PaddingValues(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      itemsIndexed(items, key = { _, item -> item.id }) { index, item ->
        MediaCard(
          item = item,
          onClick = { onItemClick(item) },
          isBookmarked = isBookmarked(item.id),
          onBookmarkToggle = { onBookmarkToggle(item) },
          rankNumber = if (isRanked) index + 1 else null,
          modifier = Modifier.width(140.dp)
        )
      }
    }
  }
}
