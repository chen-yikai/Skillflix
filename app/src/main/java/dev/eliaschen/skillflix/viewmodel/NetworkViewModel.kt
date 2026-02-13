package dev.eliaschen.skillflix.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.eliaschen.skillflix.schema.CollectionResponse
import dev.eliaschen.skillflix.schema.Episode
import dev.eliaschen.skillflix.schema.EpisodesResponse
import dev.eliaschen.skillflix.schema.Featured
import dev.eliaschen.skillflix.schema.FeaturedDetail
import dev.eliaschen.skillflix.schema.Order
import dev.eliaschen.skillflix.schema.Season
import dev.eliaschen.skillflix.schema.SeasonResponse
import dev.eliaschen.skillflix.schema.SortBy
import dev.eliaschen.skillflix.schema.VideoType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Headers
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

//const val host = "https://skillflix.skills.eliaschen.dev"
const val host = "http://10.0.2.2:3000"
const val token = "kitty-secret-key"

class NetworkViewModel(val context: Application) : ApiClient(context) {

    suspend fun getFeatured(
        search: String = "",
        sortBy: SortBy = SortBy.Year,
        order: Order = Order.DESC,
        type: VideoType? = null
    ): List<Featured> {
        try {
            val queries = mapOf(
                "search" to search,
                "sortBy" to sortBy.name.lowercase(),
                "order" to order.name.lowercase(),
                "type" to (type?.value ?: "")
            )
            val data: List<Featured> = get("/featured", queries)
            return data
        } catch (e: Exception) {
            errorLog(e.message)
            return emptyList()
        }
    }

    suspend fun getDetail(titleId: String): FeaturedDetail? {
        try {
            return get("/featured/${titleId}")
        } catch (e: Exception) {
            errorLog(e.message)
            return null
        }
    }

    suspend fun getSeason(titleId: String): List<Season> {
        try {
            val data: SeasonResponse = get("/seasons/${titleId}")
            return data.seasons
        } catch (e: Exception) {
            errorLog(e.message)
            return emptyList()
        }
    }

    suspend fun getEpisodes(titleId: String, season: String): List<Episode> {
        try {
            val queries = mapOf(
                "season" to season
            )
            val data: EpisodesResponse = get("/episodes/${titleId}", queries)
            return data.episodes
        } catch (e: Exception) {
            errorLog(e.message)
            return emptyList()
        }
    }

    suspend fun getCollection(): List<Featured> {
        try {
            val data: CollectionResponse = get("/collection")
            return data.items
        } catch (e: Exception) {
            errorLog(e.message)
            return emptyList()
        }
    }

    fun addCollection(titleId: String) {
        viewModelScope.launch {
            val payload = mapOf(
                "titleId" to titleId
            )
            try {
                post("/collection", payload)
            } catch (e: Exception) {
                Log.e("Add","${e.message}!!")
            }
        }
    }

    fun removeCollection(titleId: String) {
        viewModelScope.launch {
            try {
                delete("/collection/$titleId")
            } catch (e: Exception) {
                Log.e("Remove","${e.message!!}")
            }
        }
    }
}
