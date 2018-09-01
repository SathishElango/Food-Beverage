package com.influx.fb.model.Response

import com.influx.fb.model.Food
import com.influx.fb.model.Status

data class FnBResponse(
        var status: Status,
        var Currency: String,
        var FoodList: List<Food>
)