package dev.eliaschen.skillflix.schema

import com.google.gson.annotations.SerializedName

enum class SortBy(val label: String) {
    Rating("評分"), Year("發行年份"), Title("標題")
}

enum class Order {
    ASC, DESC
}

enum class VideoType(val label: String, val value: String) {
    @SerializedName("tvSeries")
    TVSeries("影集", "tvSeries"),

    @SerializedName("movie")
    Movie("電影", "movie")
}

data class Featured(
    val id: String,
    val primaryTitle: String,
    val type: VideoType,
    val primaryImage: VideoImage,
    val startYear: Int,
    val rating: Rating,
    val genres: List<String>,
)

data class Rating(
    val aggregateRating: Float
)

data class VideoImage(
    val url: String,
)

data class FeaturedDetail(
    val id: String,
    val type: VideoType,
    val primaryTitle: String,
    val primaryImage: VideoImage,
    val startYear: Int,
    val rating: Rating,
    val runtimeSeconds: Int,
    val genres: List<String>,
    val plot: String,
    val directors: List<People>,
    val writers: List<People>,
    val stars: List<People>
)

enum class PeopleType(val label: String) {
    Directors("導演"),
    Writers("編劇"),
    Stars("演員")
}

data class People(
    val id: String,
    val displayName: String,
    val primaryImage: VideoImage?,
    val primaryProfessions: List<String>
)

data class EpisodesResponse(
    val episodes: List<Episode>
)

data class Episode(
    val id: String,
    val title: String,
    val primaryImage: VideoImage,
    val season: String,
    val episodeNumber: Int,
    val runtimeSeconds: Int,
    val plot: String?,
    val rating: Rating,
    val releaseDate: ReleaseDate
)

data class ReleaseDate(
    val year: Int,
    val month: Int,
    val day: Int
)

data class SeasonResponse(
    val seasons: List<Season>,
)

data class Season(
    val season: String,
    val episodeCount: Int,
)

data class CollectionResponse(
    val items: List<Featured>
)