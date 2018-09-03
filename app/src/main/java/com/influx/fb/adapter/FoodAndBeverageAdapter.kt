package com.influx.fb.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.influx.fb.Utils.Constants
import com.influx.fb.R
import com.influx.fb.model.FnB
import com.influx.fb.model.FnBSubItem
import com.influx.fb.viewmodel.FnBViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.food_beverage_list_item.view.*
import kotlinx.android.synthetic.main.sub_item.view.*

class FoodAndBeverageAdapter(var foodItemList: List<FnB>, val context: Context, val viewModel: FnBViewModel) : RecyclerView.Adapter<FoodAndBeverageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.food_beverage_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return foodItemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val fnb = foodItemList.get(position)

        Picasso.get().load(fnb.ImgUrl).fit().centerCrop().into(holder.ivFoodPic)
        holder.tvFoodName.text = fnb.Name
        holder.tvPrice.text = Constants.CURRENCY + " " + fnb.totalITemPrice.toInt()
        holder.tvQty.text = fnb.orderQty.toString()



        //Inflates subItem [small, medium, large] into the container dynamically
        holder.llSubItemsContainer.removeAllViews()
        for (fnbSubItem in fnb.subitems) {
            holder.llSubItemsContainer.addView(getSubItemView(fnbSubItem, position))
        }

        holder.ivSub.setOnClickListener { view ->
            //On decreasing the quantity of the food item, it updates the view and also
            //view model instance
            if (fnb.orderQty > 0) {
                fnb.orderQty = fnb.orderQty - 1

                fnb.totalITemPrice = getTotalItemPrice(fnb)
                holder.tvPrice.text = Constants.CURRENCY + " " + getTotalItemPrice(fnb).toInt()
                holder.tvQty.text = fnb.orderQty.toString()

                if (fnb.orderQty > 0) {
                    viewModel.updateSelectedList(fnb)
                } else {
                    viewModel.removeItem(fnb)
                }
            }
        }

        holder.ivAdd.setOnClickListener { view ->
            //On increasing the quantity of the food item, it updates the view and also
            //view model instance
            if (isAnyOnSubItemSelected(fnb.subitems)) {
                fnb.orderQty = fnb.orderQty + 1

                fnb.totalITemPrice = getTotalItemPrice(fnb)
                holder.tvPrice.text = Constants.CURRENCY + " " + getTotalItemPrice(fnb).toInt()
                holder.tvQty.text = fnb.orderQty.toString()

                if (fnb.orderQty == 1) {
                    viewModel.addItemToSelectedList(fnb)
                } else {
                    viewModel.updateSelectedList(fnb)
                }

            } else {
                Toast.makeText(context, "Select a size", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Returns the total price of the food item calculates based on
     * the price and the quantity
     */
    private fun getTotalItemPrice(fnb: FnB): Double {
        var itemPrice: Double = 0.0
        if (fnb.subitems.size > 0) {
            itemPrice = getFoodItemPrice(fnb.subitems)
        } else {
            itemPrice = fnb.ItemPrice.toDouble()
        }
        return itemPrice * fnb.orderQty
    }

    /**
     * Returns the price of a food item based on the selected subItem [small, medium, large]
     */
    private fun getFoodItemPrice(subItemList: List<FnBSubItem>): Double {
        for (subItem in subItemList) {
            if (subItem.isSelected) {
                return subItem.SubitemPrice.toDouble()
            }
        }
        return 0.0
    }

    /**
     * Validates whether one of the subItem is selected. If it does returns true, only then the user is let
     * increase the item quantity
     */
    private fun isAnyOnSubItemSelected(fnbSubItems: List<FnBSubItem>): Boolean {
        if (fnbSubItems.size > 0) {
            for (subItem in fnbSubItems) {
                if (subItem.isSelected) {
                    return true
                }
            }
            return false
        } else {
            return true
        }
    }

    /**
     * Returns the view of the sub item [small, medium, large]
     */
    private fun getSubItemView(fnbSubItem: FnBSubItem, position: Int): View {
        val view = LayoutInflater.from(context).inflate(R.layout.sub_item, null)
        view.tvSubItemType.text = fnbSubItem.Name
        if (fnbSubItem.isSelected) {
            view.tvSubItemType.setBackgroundColor(Color.parseColor("#FFDB00"))
            view.tvSubItemType.setTextColor(Color.BLACK)
        }
        view.setOnClickListener {
            updateSubItem(fnbSubItem.Name, position)
        }
        return view
    }

    /**
     * Updates the subItem [small, medium, large] selected status and updates the
     * total price of the respective food item
     */
    private fun updateSubItem(subItemName: String, position: Int) {
        for (subItem in foodItemList.get(position).subitems) {
            subItem.isSelected = subItem.Name.equals(subItemName)
        }
        foodItemList.get(position).totalITemPrice = getTotalItemPrice(foodItemList.get(position))
        viewModel.updateSelectedList(foodItemList.get(position))
        notifyItemChanged(position)
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