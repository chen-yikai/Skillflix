package dev.eliaschen.skillflix.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Headers
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


open class ApiClient(private val context: Application) : AndroidViewModel(context) {
    val client = OkHttpClient()

    val tokenHeader =
        Headers.Builder().add("x-api-key", token).build()

    protected suspend inline fun <reified T> get(
        path: String, query: Map<String, String> = emptyMap()
    ): T = withContext(Dispatchers.IO) {
        val urlBuilder = "$host/api$path".toHttpUrl().newBuilder()
        query.forEach { (key, value) ->
            urlBuilder.addQueryParameter(key, value)
        }
        val req = Request.Builder().headers(tokenHeader).url(urlBuilder.build())
        val res = client.newCall(req.build()).execute().let {
            it.body?.string()?.parseJson<T>()
        }
        if (res == null) {
            val message = "Failed to GET $path"
            throw Exception(message)
        } else {
            return@withContext res
        }
    }

    protected suspend fun post(path: String, payload: Map<String, String> = emptyMap()) =
        withContext(Dispatchers.IO) {
            val body = payload.toJsonString().toRequestBody("application/json".toMediaType())
            val req =
                Request.Builder().post(body).headers(tokenHeader).url("$host/api$path").build()
            val res = client.newCall(req).execute()
            if (res.code >= 300) {
                throw Exception("Failed to POST $path")
            }
        }

    protected suspend fun delete(path: String) = withContext(Dispatchers.IO) {
        val req = Request.Builder().delete().headers(tokenHeader).url("$host/api$path").build()
        val res = client.newCall(req).execute()
        if (res.code >= 300) {
            throw Exception("Failed to DELETE $path")
        }
    }

    fun errorLog(message: String?) {
        Log.e("ApiClient", message ?: "")
        Toast.makeText(context, "API 連線異常", Toast.LENGTH_SHORT).show()
    }
}
