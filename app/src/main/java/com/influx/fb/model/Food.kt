package com.influx.fb.model

data class Food(
        var TabName: String,
        var isSelected : Boolean,
        var fnblist: List<FnB>
)