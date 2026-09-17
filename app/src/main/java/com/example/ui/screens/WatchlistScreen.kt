package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.data.repository.MovieRepository
import com.example.ui.components.MediaCard
import com.example.ui.theme.CineBg
import com.example.ui.theme.CinePrimary
import com.example.ui.theme.CineSecondary
import com.example.ui.theme.CineSurface
import com.example.ui.theme.CineSurfaceElevated
import com.example.ui.theme.CineTextMuted
import com.example.ui.theme.CineTextSecondary

@Composable
fun WatchlistScreen(
  watchlistIds: Set<String>,
  onItemClick: (MediaItem) -> Unit,
  onBookmarkToggle: (MediaItem) -> Unit,
  onExploreClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedType by remember { mutableStateOf(MediaType.ALL) }

  val allBookmarkedItems by remember(watchlistIds) {
    derivedStateOf {
      watchlistIds.mapNotNull { MovieRepository.getById(it) }
    }
  }

  val filteredItems by remember(allBookmarkedItems, selectedType) {
    derivedStateOf {
      if (selectedType == MediaType.ALL) {
        allBookmarkedItems
      } else {
        allBookmarkedItems.filter { it.type == selectedType }
      }
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(CineBg)
      .testTag("watchlist_screen")
  ) {
    // Header
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .fillMaxWidth()
        .background(CineSurface)
        .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
      Icon(
        imageVector = Icons.Default.Bookmark,
        contentDescription = null,
        tint = CineSecondary,
        modifier = Modifier.size(24.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "My Watchlist",
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
      )
      Spacer(modifier = Modifier.width(8.dp))
      Surface(
        color = CinePrimary,
        shape = RoundedCornerShape(12.dp)
      ) {
        Text(
          text = "${allBookmarkedItems.size}",
          color = Color.White,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
      }
    }

    if (allBookmarkedItems.isEmpty()) {
      // Empty state
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Icon(
          imageVector = Icons.Default.Bookmark,
          contentDescription = null,
          tint = CineTextMuted,
          modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
          text = "Your Watchlist is empty",
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Save your favorite movies, web series, and anime to watch or download later.",
          color = CineTextSecondary,
          fontSize = 13.sp,
          lineHeight = 18.sp,
          modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
          onClick = onExploreClick,
          colors = ButtonDefaults.buttonColors(containerColor = CinePrimary),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Explore,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "Explore Movies & Shows")
        }
      }
    } else {
      // Filter Type Pills
      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(MediaType.values()) { type ->
          val isSelected = selectedType == type
          val count = if (type == MediaType.ALL) {
            allBookmarkedItems.size
          } else {
            allBookmarkedItems.count { it.type == type }
          }
          Surface(
            color = if (isSelected) CinePrimary else CineSurfaceElevated,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .clip(RoundedCornerShape(14.dp))
              .clickable { selectedType = type }
          ) {
            Text(
              text = "${type.label} ($count)",
              color = if (isSelected) Color.White else CineTextSecondary,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
          }
        }
      }

      // Grid of Saved items
      LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(filteredItems, key = { it.id }) { item ->
          MediaCard(
            item = item,
            onClick = { onItemClick(item) },
            isBookmarked = true,
            onBookmarkToggle = { onBookmarkToggle(item) },
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }
  }
}
