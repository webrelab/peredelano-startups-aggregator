package com.repedelano.extensions

import com.repedelano.dtos.idea.IdeaStage
import java.util.UUID

fun String.toUuidOrNull(): UUID? = try {
    UUID.fromString(this)
} catch (ignored: IllegalArgumentException) {
    null
}

fun String.toIdeaStage(): IdeaStage? = try {
    IdeaStage.valueOf(this)
} catch (ignored: Throwable) {
    null
}