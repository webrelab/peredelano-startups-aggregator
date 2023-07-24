package com.peredelano.ext

import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import kotlinx.serialization.encodeToString

fun TestApplicationEngine.get(url: String) =
    handleRequest(HttpMethod.Get, url) {}.response

fun TestApplicationEngine.getOk(url: String) =
    get(url)
        .also { Assertions.assertEquals(
            HttpStatusCode.OK,
            it.status(),
            "${it.content}"
        ) }.content

inline fun <reified T> TestApplicationEngine.getConvertedNullable(url: String) =
    getOk(url)?.let { Json.decodeFromString<T>(it) }

inline fun <reified T> TestApplicationEngine.getConverted(url: String) =
    getConvertedNullable<T>(url)!!

inline fun <reified T : Any> TestApplicationEngine.post(url: String, obj: T?) =
    handleRequest(HttpMethod.Post, url) {
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        obj?.let { setBody(Json.encodeToString(obj)) } ?: setBody("{}")
    }.response

inline fun <reified T : Any> TestApplicationEngine.postCreated(url: String, obj: T) =
    post(url, obj)
        .also { Assertions.assertEquals(
            HttpStatusCode.Created,
            it.status(),
            "${it.content}"
        ) }
        .content

inline fun <reified T : Any, reified R : Any> TestApplicationEngine.postConvertedNullable(url: String, obj: T) =
    postCreated(url, obj)?.let { Json.decodeFromString<R>(it) }

inline fun <reified T : Any, reified R : Any> TestApplicationEngine.postConverted(url: String, obj: T) =
    postConvertedNullable<T, R>(url, obj)!!

inline fun <reified T : Any> TestApplicationEngine.put(url: String, obj: T?) =
    handleRequest(HttpMethod.Put, url) {
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        obj?.let{ setBody(Json.encodeToString(obj)) }
    }.response

inline fun <reified T : Any> TestApplicationEngine.putOk(url: String, obj: T?) =
    put(url, obj)
        .also { Assertions.assertEquals(
            HttpStatusCode.OK,
            it.status(),
            "${it.content}"
        ) }
        .content

inline fun <reified T : Any, reified R : Any> TestApplicationEngine.putConvertedNullable(url: String, obj: T?) =
    putOk(url, obj)?.let { Json.decodeFromString<R>(it) }

inline fun <reified T : Any, reified R : Any> TestApplicationEngine.putConverted(url: String, obj: T?) =
    putConvertedNullable<T, R>(url, obj)!!

