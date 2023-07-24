package com.repedelano.datagenerator

import io.github.serpro69.kfaker.Faker

object StringGen {

    fun nextString(maxSentences: Int, maxParagraphs: Int): String {
        return List(IdGen.nextId(maxParagraphs)) {
            List(IdGen.nextId(maxSentences)) {
                RequestGenerators.faker.vForVendetta.quotes()
            }.joinToString(". ")
        }.joinToString("\n")
    }
}