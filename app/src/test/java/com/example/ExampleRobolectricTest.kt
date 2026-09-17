package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.MediaType
import com.example.data.repository.MovieRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("CineVerse", appName)
  }

  @Test
  fun `movie repository has rich content across all sections`() {
    assertTrue(MovieRepository.allItems.isNotEmpty())
    assertTrue(MovieRepository.getWebSeries().isNotEmpty())
    assertTrue(MovieRepository.getAnime().isNotEmpty())
    assertTrue(MovieRepository.getBollywood().isNotEmpty())
    assertTrue(MovieRepository.get4KCollection().isNotEmpty())

    val searchResults = MovieRepository.search("Dune")
    assertTrue(searchResults.any { it.title.contains("Dune") })

    val animeResults = MovieRepository.search("", type = MediaType.ANIME)
    assertTrue(animeResults.all { it.type == MediaType.ANIME })
  }
}

