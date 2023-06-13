package com.tehronshoh.touristmap.model

import com.tehronshoh.touristmap.R

enum class Filter(val label: Int) {
    DEFAULT(R.string.by_default),
    BY_DESTINATION(R.string.nearest),
    BY_NAME(R.string.name)
}