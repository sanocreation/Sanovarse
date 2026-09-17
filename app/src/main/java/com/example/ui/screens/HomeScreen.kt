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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.data.repository.MovieRepository
import com.example.ui.components.HeroCarousel
import com.example.ui.components.MediaCard
import com.example.ui.components.SectionRow
import com.example.ui.theme.CineTextMuted
import com.example.ui.theme.CineBg
import com.example.ui.theme.CineBorder
import com.example.ui.theme.CinePrimary
import com.example.ui.theme.CineSecondary
import com.example.ui.theme.CineSurface
import com.example.ui.theme.CineSurfaceElevated
import com.example.ui.theme.CineTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  onItemClick: (MediaItem) -> Unit,
  onSearchClick: () -> Unit,
  onSurpriseClick: () -> Unit,
  onBookmarkToggle: (MediaItem) -> Unit,
  isBookmarked: (String) -> Boolean,
  onNavigateToCategory: (MediaType) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableStateOf("All") }

  val tabs = listOf(
    "All", "Bollywood", "South Hindi", "Web Series", "Hollywood", "TV Series", "Anime", "4K UHD"
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(CineBg)
      .testTag("home_screen")
  ) {
    // Header Bar
    TopAppBar(
      colors = TopAppBarDefaults.topAppBarColors(containerColor = CineBg),
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .clip(CircleShape)
              .background(CinePrimary)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "CineVerse",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 22.sp,
            letterSpacing = (-0.5).sp
          )
          Spacer(modifier = Modifier.width(6.dp))
          Surface(
            color = CinePrimary.copy(alpha = 0.2f),
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = "HDHUB",
              color = CinePrimary,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
          }
        }
      },
      actions = {
        // Random / Surprise Pick
        IconButton(
          onClick = onSurpriseClick,
          modifier = Modifier.testTag("surprise_btn")
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = "Surprise Me",
            tint = CineSecondary
          )
        }
        // Search icon
        IconButton(
          onClick = onSearchClick,
          modifier = Modifier.testTag("search_action_btn")
        ) {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = Color.White
          )
        }
      }
    )

    // Category Tabs Pill Row
    LazyRow(
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(tabs) { tab ->
        val isSelected = selectedTab == tab
        Surface(
          color = if (isSelected) CinePrimary else CineSurfaceElevated,
          shape = RoundedCornerShape(20.dp),
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { selectedTab = tab }
        ) {
          Text(
            text = tab,
            color = if (isSelected) Color.White else CineTextSecondary,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(4.dp))

    // Main content scroll
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(bottom = 80.dp),
      verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
      // Hero Spotlight
      item {
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
          HeroCarousel(
            items = MovieRepository.getFeaturedItems(),
            onItemClick = onItemClick,
            onBookmarkToggle = onBookmarkToggle,
            isBookmarked = isBookmarked
          )
        }
      }

      // Filtered mode if a specific tab is chosen
      if (selectedTab != "All") {
        val (categoryTitle, categorySub, catItems) = when (selectedTab) {
          "Bollywood" -> Triple("🎬 Bollywood Movies", "Stree 2, Jawan, Animal, 12th Fail, Chhaava & more", MovieRepository.getBollywood())
          "South Hindi" -> Triple("🇮🇳 South Indian Hindi Dubbed", "Pushpa 2, KGF 2, RRR, Baahubali, Kalki 2898 AD", MovieRepository.getSouthHindi())
          "Web Series" -> Triple("📺 Indian Web Series Hub", "Mirzapur, Panchayat, Family Man, Farzi, Gullak & more", MovieRepository.getWebSeries())
          "Hollywood" -> Triple("🌎 Hollywood Movies (Dual Audio)", "Deadpool, Dune 2, Oppenheimer, Avengers Endgame", MovieRepository.getHollywood())
          "TV Series" -> Triple("🍿 Global Binge TV Series", "Breaking Bad, Game of Thrones, Stranger Things, Money Heist", MovieRepository.getTVSeries())
          "Anime" -> Triple("🌸 Anime Central (Sub & Hindi Dub)", "Attack on Titan, Solo Leveling, Jujutsu Kaisen, Naruto", MovieRepository.getAnime())
          "4K UHD" -> Triple("💎 4K Ultra HD 2160p HDR", "Ultra high bitrate releases with Dolby Vision", MovieRepository.get4KCollection())
          else -> Triple(selectedTab, "", emptyList())
        }

        item {
          Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = categoryTitle,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
              )
              Surface(
                color = CinePrimary.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
              ) {
                Text(
                  text = "${catItems.size} Titles",
                  color = CinePrimary,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
            if (categorySub.isNotBlank()) {
              Text(
                text = categorySub,
                color = CineTextMuted,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
              )
            }
          }
        }

        // 3-column Grid for all items in this category
        val rows = catItems.chunked(3)
        items(rows) { triplet ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            triplet.forEach { media ->
              Box(modifier = Modifier.weight(1f)) {
                MediaCard(
                  item = media,
                  onClick = { onItemClick(media) },
                  onBookmarkToggle = { onBookmarkToggle(media) },
                  isBookmarked = isBookmarked(media.id),
                  modifier = Modifier.fillMaxWidth()
                )
              }
            }
            repeat(3 - triplet.size) {
              Spacer(modifier = Modifier.weight(1f))
            }
          }
        }
      } else {
          // "All" Tab - Full HDHub4u style discovery catalog

          // Top 10 Today in India (Ranked)
          item {
            SectionRow(
              title = "Top 10 Today in India",
              subtitle = "🔥 Most watched movies & series right now",
              items = MovieRepository.getTop10(),
              onItemClick = onItemClick,
              onBookmarkToggle = onBookmarkToggle,
              isBookmarked = isBookmarked,
              isRanked = true
            )
          }

          // Trending Now
          item {
            SectionRow(
              title = "Trending Now",
              subtitle = "Hot releases across all platforms",
              items = MovieRepository.getTrending(),
              onItemClick = onItemClick,
              onBookmarkToggle = onBookmarkToggle,
              isBookmarked = isBookmarked,
              onSeeAllClick = { onNavigateToCategory(MediaType.ALL) }
            )
          }

          // Bollywood Blockbusters
          item {
            SectionRow(
              title = "🎬 Bollywood Blockbusters (Hindi)",
              subtitle = "Stree 2, Jawan, Animal, 12th Fail, Bhool Bhulaiyaa 3",
              items = MovieRepository.getBollywood(),
              onItemClick = onItemClick,
              onBookmarkToggle = onBookmarkToggle,
              isBookmarked = isBookmarked
            )
          }

          // South Indian Hindi Dubbed
          item {
            SectionRow(
              title = "🇮🇳 South Indian Hindi Dubbed",
              subtitle = "Pushpa 2, K.G.F 2, RRR, Baahubali 2, Kalki 2898 AD",
              items = MovieRepository.getSouthHindi(),
              onItemClick = onItemClick,
              onBookmarkToggle = onBookmarkToggle,
              isBookmarked = isBookmarked
            )
          }

          // Indian Web Series Hub
          item {
            SectionRow(
              title = "📺 Indian Web Series (HDHub Special)",
              subtitle = "Mirzapur 3, Panchayat 3, Farzi, Gullak, Asur",
              items = MovieRepository.getWebSeries(),
              onItemClick = onItemClick,
              onBookmarkToggle = onBookmarkToggle,
              isBookmarked = isBookmarked,
              onSeeAllClick = { onNavigateToCategory(MediaType.WEBSERIES) }
            )
          }

          // Hollywood Dual Audio
          item {
            SectionRow(
              title = "🌎 Hollywood Dual Audio (Hindi + Eng)",
              subtitle = "Deadpool & Wolverine, Dune 2, Oppenheimer, Endgame",
              items = MovieRepository.getHollywood(),
              onItemClick = onItemClick,
              onBookmarkToggle = onBookmarkToggle,
              isBookmarked = isBookmarked,
              onSeeAllClick = { onNavigateToCategory(MediaType.MOVIE) }
            )
          }

          // Anime Universe
          item {
            SectionRow(
              title = "🌸 Anime Universe (Sub & Hindi Dub)",
              subtitle = "Attack on Titan, Solo Leveling, Jujutsu Kaisen, Naruto",
              items = MovieRepository.getAnime(),
              onItemClick = onItemClick,
              onBookmarkToggle = onBookmarkToggle,
              isBookmarked = isBookmarked,
              onSeeAllClick = { onNavigateToCategory(MediaType.ANIME) }
            )
          }

          // Global TV Series
          item {
            SectionRow(
              title = "🍿 Binge-Worthy TV Series (Dual Audio)",
              subtitle = "Breaking Bad, Stranger Things, The Boys, Money Heist",
              items = MovieRepository.getTVSeries(),
              onItemClick = onItemClick,
              onBookmarkToggle = onBookmarkToggle,
              isBookmarked = isBookmarked,
              onSeeAllClick = { onNavigateToCategory(MediaType.SERIES) }
            )
          }

          // 4K Ultra HD Showcase
          item {
            SectionRow(
              title = "💎 4K Ultra HD & Remux Collection",
              subtitle = "2160p HDR10+ Dolby Atmos files",
              items = MovieRepository.get4KCollection(),
              onItemClick = onItemClick,
              onBookmarkToggle = onBookmarkToggle,
              isBookmarked = isBookmarked
            )
          }
        }
      }
    }
  }
