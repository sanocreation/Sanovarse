package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ContentCategory
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.data.repository.MovieRepository
import com.example.ui.components.MediaCard
import com.example.ui.theme.CineBg
import com.example.ui.theme.CineBorder
import com.example.ui.theme.CinePrimary
import com.example.ui.theme.CineSecondary
import com.example.ui.theme.CineSurface
import com.example.ui.theme.CineSurfaceElevated
import com.example.ui.theme.CineTextMuted
import com.example.ui.theme.CineTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
  onItemClick: (MediaItem) -> Unit,
  onBookmarkToggle: (MediaItem) -> Unit,
  isBookmarked: (String) -> Boolean,
  recentSearches: List<String>,
  onAddRecentSearch: (String) -> Unit,
  onClearRecentSearches: () -> Unit,
  initialMediaType: MediaType = MediaType.ALL,
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedType by remember { mutableStateOf(initialMediaType) }
  var selectedCategory by remember { mutableStateOf(ContentCategory.ALL) }
  var selectedGenre by remember { mutableStateOf("All") }
  var selectedQuality by remember { mutableStateOf("All") }
  var selectedLanguage by remember { mutableStateOf("All") }
  var selectedSort by remember { mutableStateOf("Popular") }
  var showFiltersRow by remember { mutableStateOf(false) }

  val focusManager = LocalFocusManager.current

  // Filtered Results
  val searchResults by remember(
    searchQuery, selectedType, selectedCategory, selectedGenre, selectedQuality, selectedLanguage, selectedSort
  ) {
    derivedStateOf {
      MovieRepository.search(
        query = searchQuery,
        type = selectedType,
        category = selectedCategory,
        genre = if (selectedGenre == "All") null else selectedGenre,
        quality = if (selectedQuality == "All") null else selectedQuality,
        languageFilter = if (selectedLanguage == "All") null else selectedLanguage,
        sortBy = selectedSort
      )
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(CineBg)
      .testTag("explore_screen")
  ) {
    // Header & Search Bar
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(CineSurface)
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "Search & Filter Hub",
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 20.sp,
          modifier = Modifier.weight(1f)
        )
        // Filter toggle button
        IconButton(
          onClick = { showFiltersRow = !showFiltersRow },
          modifier = Modifier
            .clip(CircleShape)
            .background(if (showFiltersRow) CinePrimary else CineSurfaceElevated)
            .testTag("filter_toggle_btn")
        ) {
          Icon(
            imageVector = Icons.Default.Tune,
            contentDescription = "Filter",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Text Input
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("search_input"),
        placeholder = {
          Text(
            text = "Search movies, series, anime, Hindi dubbed...",
            color = CineTextMuted,
            fontSize = 13.sp
          )
        },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = CinePrimary
          )
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { searchQuery = "" }) {
              Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Clear",
                tint = CineTextSecondary
              )
            }
          }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = CineSurfaceElevated,
          unfocusedContainerColor = CineSurfaceElevated,
          focusedBorderColor = CinePrimary,
          unfocusedBorderColor = CineBorder,
          focusedTextColor = Color.White,
          unfocusedTextColor = Color.White
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
          onSearch = {
            if (searchQuery.isNotBlank()) {
              onAddRecentSearch(searchQuery)
            }
            focusManager.clearFocus()
          }
        )
      )

      // Category Filter Chips
      Spacer(modifier = Modifier.height(8.dp))
      LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        items(ContentCategory.values()) { cat ->
          val isSelected = selectedCategory == cat
          Surface(
            color = if (isSelected) CinePrimary else CineSurfaceElevated,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .clickable { selectedCategory = cat }
          ) {
            Text(
              text = cat.label,
              color = if (isSelected) Color.White else CineTextSecondary,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
          }
        }
      }

      // Expandable High-Performance Advanced Filters Row
      AnimatedVisibility(visible = showFiltersRow) {
        Column(modifier = Modifier.padding(top = 10.dp)) {
          // Genre selection
          Text(
            text = "Genre",
            color = CineTextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
          )
          Spacer(modifier = Modifier.height(4.dp))
          LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(MovieRepository.allGenres) { g ->
              val isSelected = selectedGenre == g
              Surface(
                color = if (isSelected) CineSecondary else CineSurfaceElevated,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .clickable { selectedGenre = g }
              ) {
                Text(
                  text = g,
                  color = if (isSelected) Color.Black else CineTextSecondary,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Quality & Audio filter row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Quality
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Quality",
                color = CineTextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
              )
              Spacer(modifier = Modifier.height(4.dp))
              Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf("All", "4K", "1080p", "720p").forEach { q ->
                  val isSelected = selectedQuality == q
                  Surface(
                    color = if (isSelected) CinePrimary else CineSurfaceElevated,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                      .clip(RoundedCornerShape(6.dp))
                      .clickable { selectedQuality = q }
                  ) {
                    Text(
                      text = q,
                      color = if (isSelected) Color.White else CineTextSecondary,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }
                }
              }
            }

            // Reset filters button
            Button(
              onClick = {
                selectedType = MediaType.ALL
                selectedGenre = "All"
                selectedQuality = "All"
                selectedLanguage = "All"
                selectedSort = "Popular"
                searchQuery = ""
              },
              colors = ButtonDefaults.buttonColors(containerColor = CineSurfaceElevated),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.align(Alignment.Bottom)
            ) {
              Text(text = "Reset", color = CinePrimary, fontSize = 11.sp)
            }
          }
        }
      }
    }

    // Main Results Grid
    LazyVerticalGrid(
      columns = GridCells.Adaptive(minSize = 150.dp),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      modifier = Modifier.fillMaxSize()
    ) {
      // Trending Keywords & Recent Searches if search query is blank
      if (searchQuery.isBlank()) {
        item(span = { GridItemSpan(maxLineSpan) }) {
          Column {
            Text(
              text = "🔥 Trending Searches",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              items(MovieRepository.trendingKeywords) { kw ->
                Surface(
                  color = CineSurfaceElevated,
                  shape = RoundedCornerShape(12.dp),
                  border = androidx.compose.foundation.BorderStroke(1.dp, CineBorder),
                  modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                      searchQuery = kw
                      onAddRecentSearch(kw)
                    }
                ) {
                  Text(
                    text = kw,
                    color = CineTextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                  )
                }
              }
            }

            // Recent searches
            if (recentSearches.isNotEmpty()) {
              Spacer(modifier = Modifier.height(14.dp))
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
              ) {
                Icon(
                  imageVector = Icons.Default.History,
                  contentDescription = null,
                  tint = CineTextMuted,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Recent Searches",
                  color = CineTextMuted,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                  text = "Clear",
                  color = CinePrimary,
                  fontSize = 11.sp,
                  modifier = Modifier
                    .clickable { onClearRecentSearches() }
                    .padding(4.dp)
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(recentSearches) { term ->
                  Surface(
                    color = CineSurface,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CineBorder),
                    modifier = Modifier
                      .clip(RoundedCornerShape(10.dp))
                      .clickable { searchQuery = term }
                  ) {
                    Text(
                      text = term,
                      color = Color.White,
                      fontSize = 12.sp,
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = "Explore All Content",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "(${searchResults.size} titles)",
                color = CineTextMuted,
                fontSize = 12.sp
              )
            }
          }
        }
      } else {
        // Query results header
        item(span = { GridItemSpan(maxLineSpan) }) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "Results for \"$searchQuery\"",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "(${searchResults.size})",
              color = CinePrimary,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      // Empty state
      if (searchResults.isEmpty()) {
        item(span = { GridItemSpan(maxLineSpan) }) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = null,
              tint = CineTextMuted,
              modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "No movies or series found",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Try searching for another keyword, actor or adjusting your filters.",
              color = CineTextSecondary,
              fontSize = 12.sp
            )
          }
        }
      }

      // Media Cards Grid
      items(searchResults, key = { it.id }) { mediaItem ->
        MediaCard(
          item = mediaItem,
          onClick = { onItemClick(mediaItem) },
          isBookmarked = isBookmarked(mediaItem.id),
          onBookmarkToggle = { onBookmarkToggle(mediaItem) },
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
  }
}
