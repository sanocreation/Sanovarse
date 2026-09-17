package com.example.ui.screens

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

data class CategoryTile(
  val id: String,
  val title: String,
  val subtitle: String,
  val icon: String,
  val gradient: List<Color>,
  val filterAction: () -> List<MediaItem>
)

@Composable
fun CategoriesScreen(
  onItemClick: (MediaItem) -> Unit,
  onBookmarkToggle: (MediaItem) -> Unit,
  isBookmarked: (String) -> Boolean,
  modifier: Modifier = Modifier
) {
  var selectedCategory by remember { mutableStateOf<CategoryTile?>(null) }

  val categories = remember {
    listOf(
      CategoryTile(
        id = "bollywood",
        title = "Bollywood Movies",
        subtitle = "Stree 2, Jawan, Animal, 12th Fail, Chhaava",
        icon = "🎬",
        gradient = listOf(Color(0xFFE11D48), Color(0xFF9F1239)),
        filterAction = { MovieRepository.getBollywood() }
      ),
      CategoryTile(
        id = "south_hindi",
        title = "South Indian Hindi Dubbed",
        subtitle = "Pushpa 2, KGF 2, RRR, Baahubali, Kalki 2898",
        icon = "🇮🇳",
        gradient = listOf(Color(0xFFD97706), Color(0xFF92400E)),
        filterAction = { MovieRepository.getSouthHindi() }
      ),
      CategoryTile(
        id = "web_series",
        title = "Indian Web Series",
        subtitle = "Mirzapur, Panchayat, Family Man, Farzi, Gullak",
        icon = "📺",
        gradient = listOf(Color(0xFF7C3AED), Color(0xFF4C1D95)),
        filterAction = { MovieRepository.getWebSeries() }
      ),
      CategoryTile(
        id = "hollywood_dual",
        title = "Hollywood (Dual Audio)",
        subtitle = "Deadpool, Dune 2, Oppenheimer, Endgame",
        icon = "🌎",
        gradient = listOf(Color(0xFF2563EB), Color(0xFF1E3A8A)),
        filterAction = { MovieRepository.getHollywood() }
      ),
      CategoryTile(
        id = "tv_series",
        title = "Global TV Series",
        subtitle = "Breaking Bad, Game of Thrones, Stranger Things, Money Heist",
        icon = "🍿",
        gradient = listOf(Color(0xFFDC2626), Color(0xFF7F1D1D)),
        filterAction = { MovieRepository.getTVSeries() }
      ),
      CategoryTile(
        id = "anime_central",
        title = "Anime Hub (Sub & Dub)",
        subtitle = "Attack on Titan, Solo Leveling, Naruto, Bleach",
        icon = "🌸",
        gradient = listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)),
        filterAction = { MovieRepository.getAnime() }
      ),
      CategoryTile(
        id = "4k_uhd",
        title = "4K Ultra HD 2160p",
        subtitle = "HDR10+ Dolby Vision 10Gbps Links",
        icon = "💎",
        gradient = listOf(Color(0xFF059669), Color(0xFF064E3B)),
        filterAction = { MovieRepository.get4KCollection() }
      ),
      CategoryTile(
        id = "top_rated",
        title = "IMDb Top Rated (8.5+)",
        subtitle = "Critically acclaimed all-time masterpieces",
        icon = "⭐",
        gradient = listOf(Color(0xFFF59E0B), Color(0xFFB45309)),
        filterAction = { MovieRepository.allItems.filter { it.rating >= 8.5 } }
      ),
      CategoryTile(
        id = "sci_fi",
        title = "Sci-Fi & Multiverse",
        subtitle = "Dune 2, Interstellar, Inception, Kalki",
        icon = "🚀",
        gradient = listOf(Color(0xFF0284C7), Color(0xFF075985)),
        filterAction = { MovieRepository.allItems.filter { it.genres.contains("Sci-Fi") } }
      )
    )
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(CineBg)
      .testTag("categories_screen")
  ) {
    // Header
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .fillMaxWidth()
        .background(CineSurface)
        .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
      if (selectedCategory != null) {
        IconButton(
          onClick = { selectedCategory = null },
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color.White
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
      }

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = selectedCategory?.title ?: "HDHub4u Categories",
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 20.sp
        )
        Text(
          text = selectedCategory?.subtitle ?: "Browse huge catalog by collection",
          color = CineTextMuted,
          fontSize = 12.sp
        )
      }
    }

    if (selectedCategory == null) {
      // Category Tiles Grid
      LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(categories, key = { it.id }) { cat ->
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(115.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(
                Brush.linearGradient(cat.gradient)
              )
              .border(1.dp, CineBorder, RoundedCornerShape(14.dp))
              .clickable { selectedCategory = cat }
              .padding(14.dp)
          ) {
            Column(
              modifier = Modifier.fillMaxSize(),
              verticalArrangement = Arrangement.SpaceBetween
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(text = cat.icon, fontSize = 24.sp)
                Icon(
                  imageVector = Icons.Default.ChevronRight,
                  contentDescription = null,
                  tint = Color.White.copy(alpha = 0.7f),
                  modifier = Modifier.size(18.dp)
                )
              }
              Column {
                Text(
                  text = cat.title,
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                )
                Text(
                  text = cat.subtitle,
                  color = Color.White.copy(alpha = 0.75f),
                  fontSize = 11.sp,
                  maxLines = 1
                )
              }
            }
          }
        }
      }
    } else {
      // Show Media Items in Selected Category
      val categoryItems = selectedCategory!!.filterAction()

      LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
          Text(
            text = "Total ${categoryItems.size} Titles Available",
            color = CineSecondary,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier.padding(bottom = 6.dp)
          )
        }

        items(categoryItems, key = { it.id }) { item ->
          MediaCard(
            item = item,
            onClick = { onItemClick(item) },
            isBookmarked = isBookmarked(item.id),
            onBookmarkToggle = { onBookmarkToggle(item) },
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }
  }
}
