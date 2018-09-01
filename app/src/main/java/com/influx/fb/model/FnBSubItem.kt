package com.influx.fb.model

data class FnBSubItem(
        var Name: String,
        var PriceInCents: String,
        var SubitemPrice: String,
        var VistaParentFoodItemId: String,
        var VistaSubFoodItemId: String
)