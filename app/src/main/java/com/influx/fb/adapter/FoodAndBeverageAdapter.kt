package com.influx.fb.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.influx.fb.R
import com.influx.fb.model.FnB
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.food_beverage_list_item.view.*
import kotlinx.android.synthetic.main.sub_item.view.*

class FoodAndBeverageAdapter(var foodItemList: List<FnB>, val context: Context) : RecyclerView.Adapter<FoodAndBeverageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.food_beverage_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return foodItemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val fnb = foodItemList.get(position)

        Picasso.get().load(fnb.ImgUrl).into(holder.ivFoodPic)
        holder.tvFoodName.text = fnb.Name
        holder.tvPrice.text = "AED " + fnb.ItemPrice
        holder.tvQty.text = "0"

        holder.llSubItemsContainer.removeAllViews()
        for (fnbSubItem in fnb.subitems) {
            val view = LayoutInflater.from(context).inflate(R.layout.sub_item, null)
            view.tvSubItemType.text = fnbSubItem.Name
            holder.llSubItemsContainer.addView(view)

        }

        holder.ivSub.setOnClickListener { view ->
            if (fnb.orderQty > 0) {
                fnb.orderQty--
                holder.tvQty.text = fnb.orderQty.toString()
            }
        }

        holder.ivAdd.setOnClickListener { view ->
            fnb.orderQty++
            holder.tvQty.text = fnb.orderQty.toString()
        }

    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var ivFoodPic = view.ivFoodPic
        var llSubItemsContainer = view.llSubItemsContainer
        var tvFoodName = view.tvFoodName
        var tvPrice = view.tvPrice
        var ivSub = view.ivSub
        var tvQty = view.tvQty
        var ivAdd = view.ivAdd

    }

}