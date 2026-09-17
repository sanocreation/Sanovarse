package com.example.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.WatchlistManager
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.data.repository.MovieRepository
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.DetailScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.WatchlistScreen
import com.example.ui.theme.CineBg
import com.example.ui.theme.CineBorder
import com.example.ui.theme.CinePrimary
import com.example.ui.theme.CineSecondary
import com.example.ui.theme.CineSurface
import com.example.ui.theme.CineTextMuted
import com.example.ui.theme.CineTextSecondary

enum class NavDestination(
  val label: String,
  val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
  val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector
) {
  HOME("Home", Icons.Filled.Home, Icons.Outlined.Home),
  EXPLORE("Explore", Icons.Filled.Search, Icons.Outlined.Search),
  CATEGORIES("Hubs", Icons.Filled.Category, Icons.Outlined.Category),
  WATCHLIST("Watchlist", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder)
}

@Composable
fun MainScreen(
  watchlistManager: WatchlistManager,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var currentTab by remember { mutableStateOf(NavDestination.HOME) }
  var selectedItem by remember { mutableStateOf<MediaItem?>(null) }
  var exploreInitialType by remember { mutableStateOf(MediaType.ALL) }

  val watchlistIds by watchlistManager.watchlistIds.collectAsState()
  val recentSearches by watchlistManager.recentSearches.collectAsState()

  // Handle system back button when looking at detail view
  BackHandler(enabled = selectedItem != null) {
    selectedItem = null
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = CineBg,
    bottomBar = {
      if (selectedItem == null) {
        NavigationBar(
          containerColor = CineSurface,
          contentColor = Color.White,
          tonalElevation = 8.dp,
          modifier = Modifier.testTag("bottom_nav_bar")
        ) {
          NavDestination.values().forEach { dest ->
            val isSelected = currentTab == dest
            NavigationBarItem(
              selected = isSelected,
              onClick = { currentTab = dest },
              icon = {
                if (dest == NavDestination.WATCHLIST && watchlistIds.isNotEmpty()) {
                  BadgedBox(
                    badge = {
                      Badge(
                        containerColor = CinePrimary,
                        contentColor = Color.White
                      ) {
                        Text(text = "${watchlistIds.size}", fontSize = 10.sp)
                      }
                    }
                  ) {
                    Icon(
                      imageVector = if (isSelected) dest.selectedIcon else dest.unselectedIcon,
                      contentDescription = dest.label
                    )
                  }
                } else {
                  Icon(
                    imageVector = if (isSelected) dest.selectedIcon else dest.unselectedIcon,
                    contentDescription = dest.label
                  )
                }
              },
              label = {
                Text(
                  text = dest.label,
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                )
              },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CinePrimary,
                selectedTextColor = CinePrimary,
                unselectedIconColor = CineTextMuted,
                unselectedTextColor = CineTextMuted,
                indicatorColor = CinePrimary.copy(alpha = 0.15f)
              ),
              modifier = Modifier.testTag("nav_item_${dest.name.lowercase()}")
            )
          }
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      if (selectedItem != null) {
        DetailScreen(
          item = selectedItem!!,
          onBackClick = { selectedItem = null },
          onItemClick = { newItem -> selectedItem = newItem },
          onBookmarkToggle = {
            val added = watchlistManager.toggleWatchlist(selectedItem!!.id)
            Toast.makeText(
              context,
              if (added) "Added to Watchlist" else "Removed from Watchlist",
              Toast.LENGTH_SHORT
            ).show()
          },
          isBookmarked = watchlistManager.isBookmarked(selectedItem!!.id)
        )
      } else {
        AnimatedContent(
          targetState = currentTab,
          transitionSpec = { fadeIn() togetherWith fadeOut() },
          label = "nav_content"
        ) { tab ->
          when (tab) {
            NavDestination.HOME -> {
              HomeScreen(
                onItemClick = { selectedItem = it },
                onSearchClick = { currentTab = NavDestination.EXPLORE },
                onSurpriseClick = {
                  val randomItem = MovieRepository.allItems.random()
                  selectedItem = randomItem
                  Toast.makeText(
                    context,
                    "🎲 Surprise Pick: ${randomItem.title}!",
                    Toast.LENGTH_SHORT
                  ).show()
                },
                onBookmarkToggle = { item ->
                  val added = watchlistManager.toggleWatchlist(item.id)
                  Toast.makeText(
                    context,
                    if (added) "Added ${item.title} to Watchlist" else "Removed from Watchlist",
                    Toast.LENGTH_SHORT
                  ).show()
                },
                isBookmarked = { id -> watchlistManager.isBookmarked(id) },
                onNavigateToCategory = { type ->
                  exploreInitialType = type
                  currentTab = NavDestination.EXPLORE
                }
              )
            }
            NavDestination.EXPLORE -> {
              ExploreScreen(
                onItemClick = { selectedItem = it },
                onBookmarkToggle = { item ->
                  val added = watchlistManager.toggleWatchlist(item.id)
                  Toast.makeText(
                    context,
                    if (added) "Added ${item.title} to Watchlist" else "Removed from Watchlist",
                    Toast.LENGTH_SHORT
                  ).show()
                },
                isBookmarked = { id -> watchlistManager.isBookmarked(id) },
                recentSearches = recentSearches,
                onAddRecentSearch = { query -> watchlistManager.addRecentSearch(query) },
                onClearRecentSearches = { watchlistManager.clearRecentSearches() },
                initialMediaType = exploreInitialType
              )
            }
            NavDestination.CATEGORIES -> {
              CategoriesScreen(
                onItemClick = { selectedItem = it },
                onBookmarkToggle = { item ->
                  val added = watchlistManager.toggleWatchlist(item.id)
                  Toast.makeText(
                    context,
                    if (added) "Added ${item.title} to Watchlist" else "Removed from Watchlist",
                    Toast.LENGTH_SHORT
                  ).show()
                },
                isBookmarked = { id -> watchlistManager.isBookmarked(id) }
              )
            }
            NavDestination.WATCHLIST -> {
              WatchlistScreen(
                watchlistIds = watchlistIds,
                onItemClick = { selectedItem = it },
                onBookmarkToggle = { item ->
                  val added = watchlistManager.toggleWatchlist(item.id)
                  Toast.makeText(
                    context,
                    if (added) "Added ${item.title} to Watchlist" else "Removed from Watchlist",
                    Toast.LENGTH_SHORT
                  ).show()
                },
                onExploreClick = { currentTab = NavDestination.EXPLORE }
              )
            }
          }
        }
      }
    }
  }
}
