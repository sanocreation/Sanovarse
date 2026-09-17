package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WatchlistManager(context: Context) {

  private val prefs: SharedPreferences =
    context.getSharedPreferences("cineverse_prefs", Context.MODE_PRIVATE)

  private val _watchlistIds = MutableStateFlow<Set<String>>(loadWatchlist())
  val watchlistIds: StateFlow<Set<String>> = _watchlistIds.asStateFlow()

  private val _recentSearches = MutableStateFlow<List<String>>(loadRecentSearches())
  val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

  private fun loadWatchlist(): Set<String> {
    return prefs.getStringSet(KEY_WATCHLIST, emptySet())?.toSet() ?: emptySet()
  }

  fun isBookmarked(id: String): Boolean {
    return _watchlistIds.value.contains(id)
  }

  fun toggleWatchlist(id: String): Boolean {
    val current = _watchlistIds.value.toMutableSet()
    val added = if (current.contains(id)) {
      current.remove(id)
      false
    } else {
      current.add(id)
      true
    }
    prefs.edit().putStringSet(KEY_WATCHLIST, current).apply()
    _watchlistIds.value = current
    return added
  }

  private fun loadRecentSearches(): List<String> {
    val raw = prefs.getString(KEY_RECENT_SEARCHES, "") ?: ""
    return if (raw.isBlank()) emptyList() else raw.split("|||")
  }

  fun addRecentSearch(query: String) {
    val trimmed = query.trim()
    if (trimmed.isBlank()) return
    val current = _recentSearches.value.toMutableList()
    current.remove(trimmed)
    current.add(0, trimmed)
    val limited = current.take(8)
    prefs.edit().putString(KEY_RECENT_SEARCHES, limited.joinToString("|||")).apply()
    _recentSearches.value = limited
  }

  fun clearRecentSearches() {
    prefs.edit().remove(KEY_RECENT_SEARCHES).apply()
    _recentSearches.value = emptyList()
  }

  companion object {
    private const val KEY_WATCHLIST = "user_watchlist_ids"
    private const val KEY_RECENT_SEARCHES = "user_recent_searches"
  }
}
