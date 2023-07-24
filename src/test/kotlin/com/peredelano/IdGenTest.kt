package com.peredelano

import com.repedelano.datagenerator.IdGen
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import java.util.UUID

class IdGenTest {

    private val key = UUID.randomUUID().toString()
//    companion object {
//
//        @JvmStatic
//        fun data(): List<Arguments> = listOf(
//            Arguments.of(
//
//            )
//        )
//    }

    @RepeatedTest(1000)
    fun nextUniqueIdTest() {
        val result = mutableSetOf<Int>()
        repeat(5) {
            result.add(IdGen.nextUniqueId(key, 5))
        }
        assertEquals(5, result.size)
    }

    @RepeatedTest(1000)
    fun nextUniqueIdErrorTest() {
        val result = mutableSetOf<Int>()
        repeat(5) {
            result.add(IdGen.nextUniqueId(key, 5))
        }
        assertThrows(AssertionError::class.java) {
            IdGen.nextUniqueId(key, 5)
        }
    }

    @RepeatedTest(1000)
    fun nextSingleUniqueIdTest() {
        val singleId = IdGen.nextUniqueId(key, 1)
        assertEquals(1, singleId)
    }

    @RepeatedTest(1000)
    fun nextSingleUniqueIdErrorTest() {
        IdGen.nextUniqueId(key, 1)
        assertThrows(AssertionError::class.java) {
            IdGen.nextUniqueId(key, 1)
        }
    }

    @Test
    fun nextUniqueWithZeroErrorTest() {
        assertThrows(IllegalArgumentException::class.java) {
            IdGen.nextUniqueId(key, 0)
        }
    }

    @RepeatedTest(1000)
    fun nextIdTest() {
        assertTrue(IdGen.nextId(5) < 6)
    }

    @Test
    fun nextIdHasMaxId() {
        val set = mutableSetOf<Int>()
        repeat(1000) {
            set.add(IdGen.nextId(12))
        }
        assertTrue(set.contains(12))
    }

    @Test
    @RepeatedTest(1000)
    fun nextIdMinimalIdTest() {
        assertEquals(1, IdGen.nextId(1))
    }

    @RepeatedTest(1000)
    fun nextUniqueIdListMinimalTest() {
        val result = IdGen.nextUniqueIdList(key, 1, 1)
        assertEquals(listOf(1), result)
    }

    @Test
    fun nextUniqueIdListHasMaxListSizeTest() {
        val set = mutableSetOf<Int>()
        repeat(1000) {
            set.add(
                IdGen.nextUniqueIdList(
                    UUID.randomUUID().toString(), 18, 50
                ).size
            )
        }
        assertTrue(set.contains(18), "$set")
        assertFalse(set.contains(19), "$set")
    }
}