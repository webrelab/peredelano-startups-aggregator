package com.repedelano.dtos.idea

enum class IdeaStatus(val value: String) {
    SEARCH_SOLUTION("Поиск решения"),
    COLLECT_SOCIO_ECONOMIC_INFO("Сбор социально-экономической информации"),
    FINANCIAL_JUSTIFICATION_REQUIRED("Требуется финансовое обоснование"),
    SEARCH_TECHNOLOGICAL_SOLUTION("Поиск технологического решения"),
    SEARCH_PARTICIPANTS("Поиск участников"),
    MVP_DEVELOPMENT("Разработка MVP"),
    PROJECT_CLOSED("Проект закрыт")
}