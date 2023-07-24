package com.repedelano.datagenerator

import java.util.UUID
import kotlin.random.Random

object IdGen {

    private val used: MutableMap<String, MutableSet<Int>> = mutableMapOf()

    fun nextUniqueId(key: String, maxId: Int): Int {
        checkMinId(maxId, "maxId")
        val usedByKey = used.getOrPut(key) { mutableSetOf() }
        val unused = (1..maxId).filterNot { usedByKey.contains(it) }
        if (unused.isEmpty()) {
            throw AssertionError("Все ID до $maxId использованы")
        }
        val randomPosition = Random.nextInt(0, unused.size)
        return unused[randomPosition].also {
            usedByKey.add(it)
        }
    }

    fun nextId(maxId: Int): Int {
        checkMinId(maxId, "maxId")
        return Random.nextInt(1, maxId.plus(1))
    }

    fun nextUniqueIdList(maxItems: Int, maxId: Int): List<Int> {
        checkMinId(maxItems, "maxItems")
        checkMinId(maxId, "maxId")
        val key = UUID.randomUUID().toString()
        return (0..Random.nextInt(0, maxItems.coerceAtLeast(0)))
            .map { nextUniqueId(key, maxId) }
    }

    fun nextUniqueIdList(key: String, maxItems: Int, maxId: Int): List<Int> {
        checkMinId(maxItems, "maxItems")
        checkMinId(maxId, "maxId")
        return (0..Random.nextInt(0, maxItems.coerceAtLeast(0)))
            .map { nextUniqueId(key, maxId) }
    }

    private fun checkMinId(id: Int, paramName: String) {
        if (id < 1) throw IllegalArgumentException("'$paramName' must be bigger than 0")
    }
}