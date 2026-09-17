package com.example.data.model

enum class MediaType(val label: String) {
  ALL("All"),
  MOVIE("Movies"),
  SERIES("TV Series"),
  WEBSERIES("Web Series"),
  ANIME("Anime")
}

enum class ContentCategory(val id: String, val label: String, val icon: String) {
  ALL("all", "All", "🔥"),
  BOLLYWOOD("bollywood", "Bollywood", "🎬"),
  SOUTH_HINDI("south_hindi", "South Hindi", "🇮🇳"),
  WEB_SERIES("web_series", "Web Series", "📺"),
  HOLLYWOOD("hollywood", "Hollywood", "🌎"),
  TV_SERIES("tv_series", "TV Series", "🍿"),
  ANIME("anime", "Anime", "🌸"),
  UHD_4K("4k_uhd", "4K UHD", "💎")
}

data class ServerLink(
  val name: String,
  val speedBadge: String = "⚡ 10Gbps",
  val url: String
)

data class StreamSource(
  val serverName: String,
  val quality: String, // "1080p", "720p", "480p"
  val streamUrl: String
)

data class DownloadOption(
  val quality: String, // "480p", "720p", "1080p", "4K UHD"
  val size: String,    // "350 MB", "950 MB", "2.4 GB", "7.2 GB"
  val codec: String,   // "HEVC 10-Bit", "x264 AAC", "HDR10 DTS"
  val servers: List<ServerLink>
)

data class EpisodeInfo(
  val episodeNumber: Int,
  val title: String,
  val duration: String = "45m",
  val downloadLink: String,
  val streamUrl: String = ""
)

data class SeasonInfo(
  val seasonNumber: Int,
  val title: String,
  val episodesCount: Int,
  val episodes: List<EpisodeInfo>
)

data class MediaItem(
  val id: String,
  val title: String,
  val type: MediaType,
  val category: ContentCategory = ContentCategory.ALL,
  val year: Int,
  val rating: Double,
  val genres: List<String>,
  val audioLanguage: String, // "Hindi + English Dual Audio", "Hindi Dubbed", "Japanese [Eng Sub]"
  val qualityBadge: String,  // "4K UHD", "1080p", "720p"
  val duration: String,
  val posterUrl: String,
  val backdropUrl: String,
  val storyline: String,
  val director: String,
  val cast: List<String>,
  val trailerYoutubeId: String,
  val isTrending: Boolean = false,
  val isTop10: Boolean = false,
  val top10Rank: Int? = null,
  val isFeatured: Boolean = false,
  val downloadOptions: List<DownloadOption> = emptyList(),
  val seasons: List<SeasonInfo> = emptyList(),
  val screenshots: List<String> = emptyList(),
  val streamSources: List<StreamSource> = emptyList(),
  val aliases: List<String> = emptyList()
)
