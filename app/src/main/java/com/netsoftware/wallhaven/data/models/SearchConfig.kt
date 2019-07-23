package com.netsoftware.wallhaven.data.models

import android.os.Bundle
import com.netsoftware.wallhaven.data.dataSources.local.SharedPrefs
import com.netsoftware.wallhaven.data.models.User.Companion.TOPLIST_MONTH
import com.netsoftware.wallhaven.ui.main.ViewerFragmentArgs
import com.netsoftware.wallhaven.ui.main.ViewerViewModel.ViewerType.SUITABLE_TYPE

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
) {
    constructor(searchConfig: SearchConfig) : this(
        searchConfig.q,
        searchConfig.categories,
        searchConfig.purity,
        searchConfig.sorting,
        searchConfig.order,
        searchConfig.top_range,
        searchConfig.resolution_at_least,
        searchConfig.resolutions,
        searchConfig.ratios,
        searchConfig.colors,
        searchConfig.page
    )

    fun getRatioList(): MutableList<String> {
        return if (ratios.isNotEmpty()) ratios.split(",").toMutableList()
        else mutableListOf()
    }

    fun getResolutionList(): MutableList<String> =
        when {
            resolutions.isNotEmpty() -> resolutions.split(",").toMutableList()
            resolution_at_least.isNotEmpty() -> mutableListOf(resolution_at_least)
            else -> mutableListOf()
        }

    fun getCategoriesMap(): MutableMap<String, Boolean> {
        val result =
            mutableMapOf(User.CATEGORY_GENERAL to false, User.CATEGORY_ANIME to false, User.CATEGORY_PEOPLE to false)
        if (categories.length == 3) {
            categories.mapIndexed { index, c ->
                if (Character.getNumericValue(c) == 1) {
                    when (index) {
                        0 -> result[User.CATEGORY_GENERAL] = true
                        1 -> result[User.CATEGORY_ANIME] = true
                        2 -> result[User.CATEGORY_PEOPLE] = true
                    }
                }
            }
        }
        return result
    }

    fun getPurityMap(): Map<String, Boolean> {
        val result =
            mutableMapOf(User.PURITY_SFW to false, User.PURITY_SKETCHY to false, User.PURITY_NSFW to false)
        if (purity.length == 3) {
            purity.mapIndexed { index, c ->
                if (Character.getNumericValue(c) == 1) {
                    when (index) {
                        0 -> result[User.PURITY_SFW] = true
                        1 -> result[User.PURITY_SKETCHY] = true
                        2 -> result[User.PURITY_NSFW] = true
                    }
                }
            }
        }
        return result
    }

    fun toMap(): Map<String, String> {
        return mutableMapOf(
            Q to q,
            CATEGORIES to categories,
            PURITY to purity,
            SORTING to sorting,
            ORDER to order,
            RESOLUTION_AT_LEAST to resolution_at_least,
            RESOLUTIONS to resolutions,
            RATIOS to ratios,
            COLORS to colors,
            PAGE to page
        ).apply {
            if (sorting == SORTING_TOP_LIST) this[TOP_RANGE] = top_range
        }
    }

    companion object {
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

        fun configFromBundle(bundle: Bundle?): SearchConfig {
            val searchConfig = SearchConfig()
            val viewerType = bundle?.let { ViewerFragmentArgs.fromBundle(it).viewerType } ?: SUITABLE_TYPE
            if (viewerType == SUITABLE_TYPE) {
                searchConfig.apply {
                    SharedPrefs.getSharedPrefs().apply {
                        resolution_at_least = screenResolution
                        if (suitableRatioOn)
                            ratios = screenRatio
                    }
                }
            }
            searchConfig.q = bundle?.let { ViewerFragmentArgs.fromBundle(it).searchQuery } ?: ""
            return searchConfig
        }

        fun getReadableResolution(resolutions: List<String>): String {
            return when {
                resolutions.count() > 1 -> "${resolutions.sorted().first()} +${resolutions.count() - 1}"
                resolutions.count() == 1 -> resolutions.first()
                else -> ""
            }
        }

        fun listToProperty(list: List<String>): String {
            return list.toString().replace(Regex("[\\[\\]\\s]"), "")
        }

        fun getReadableRatio(ratios: String): String {
            return ratios.split(",").first()
        }
    }
}