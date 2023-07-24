package com.repedelano.dtos.vacancy

enum class VacancyStatus(val value: String) {
    OPEN("Открыта"),
    DECLINED("Отменена"),
    CLOSED("Закрыта"),
    ;

    companion object {
        fun of(status: String?): VacancyStatus? {
           return when (status) {
               null -> null
               else -> VacancyStatus.valueOf(status)
           }
        }
    }
}