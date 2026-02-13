package dev.eliaschen.skillflix.viewmodel

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


inline fun <reified T> String.parseJson(): T {
    return Gson().fromJson<T>(this, object : TypeToken<T>() {}.type)
}

fun Any.toJsonString(): String {
    return Gson().toJson(this)
}