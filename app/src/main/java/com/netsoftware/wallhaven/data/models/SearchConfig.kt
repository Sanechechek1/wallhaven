package com.netsoftware.wallhaven.data.models

data class SearchConfig(
    var q: String = "",
    var categories: String = getCategoriesCode(general = true, anime = false, people = false),
    var purity: String = getPurityCode(sfw = true, sketchy = false, nsfw = false),
    var sorting: String = SORTING_DATE_ADDED,
    var order: String = ORDER_DESC,
    var top_range: String = TOPLIST_MONTH,
    var resolution_at_least: String = "",
    var resolutions: String = "",
    var ratios: String = "",
    var colors: String = "",
    var page: String = 1.toString()
){
    companion object{
        /**
         * Params for [Q]:
         * tagname - search fuzzily for a tag/keyword
         * -tagname - exclude a tag/keyword
         * +tag1 +tag2 - must have tag1 and tag2
         * +tag1 -tag2 - must have tag1 and NOT tag2
         * @username - user uploads
         * id:123 - Exact tag search (can not be combined)
         * type:{png/jpg} - Search for file type (jpg = jpeg)
         * like:wallpaper ID - Find wallpapers with similar tags
         */
        const val Q = "q"
        const val CATEGORIES = "categories"
        const val PURITY = "purity"
        const val SORTING = "sorting"
        const val ORDER = "order"
        const val TOP_RANGE = "topRange"
        const val RESOLUTION_AT_LEAST = "atleast"
        const val RESOLUTIONS = "resolutions"
        const val RATIOS = "ratios"
        const val COLORS = "colors"
        const val PAGE = "page"

        const val SORTING_DATE_ADDED = "date_added"
        const val SORTING_RANDOM = "random"
        const val SORTING_TOP_LIST = "toplist"
        const val SORTING_RELEVANCE = "relevance"
        const val SORTING_VIEWS = "views"
        const val SORTING_FAVORITES = "favorites"

        const val ORDER_DESC = "desc"
        const val ORDER_ASC = "asc"

        fun getCategoriesCode(general: Boolean, anime: Boolean, people: Boolean): String {
            val result = StringBuilder("000")
            if (general) result.setCharAt(0, '1')
            if (anime) result.setCharAt(1, '1')
            if (people) result.setCharAt(2, '1')
            return result.toString()
        }

        fun getPurityCode(sfw: Boolean, sketchy: Boolean, nsfw: Boolean): String {
            val result = StringBuilder("000")
            if (sfw) result.setCharAt(0, '1')
            if (sketchy) result.setCharAt(1, '1')
            if (nsfw) result.setCharAt(2, '1')
            return result.toString()
        }
    }
}