package com.influx.fb.model

data class FnB(

        var Cinemaid: String,
        var TabName: String,
        var VistaFoodItemId: String,
        var Name: String,
        var PriceInCents: String,
        var ItemPrice: String,
        var SevenStarExperience: String,
        var OtherExperience: String,
        var SubItemCount: Int,
        var ImgUrl: String,
        var subitems: List<FnBSubItem>
)