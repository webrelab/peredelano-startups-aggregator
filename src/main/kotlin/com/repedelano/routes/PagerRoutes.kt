package com.repedelano.routes

class PagerRoutes {

    companion object {

        const val PAGE = "page"
        const val ITEMS_PER_PAGE = "itemsPerPage"

        fun clientAddPager(
            url: String,
            page: Any? = null,
            itemsPerPage: Any? = null
        ) = listOfNotNull(
            page?.let { "$PAGE=$it" },
            itemsPerPage?.let { "$ITEMS_PER_PAGE=$it" }
        ).joinToString("&").let { union(url, it) }

        private fun union(url: String, pager: String) =
            if (pager.isBlank()) url
            else if (url.contains("?")) "$url&$pager"
            else "$url?$pager"
    }
}