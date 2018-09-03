package com.influx.fb

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.influx.fb.adapter.FoodAndBeverageAdapter
import com.influx.fb.model.FnB
import com.influx.fb.model.FnBSubItem
import com.influx.fb.model.Food
import com.influx.fb.viewmodel.FnBViewModel
import kotlinx.android.synthetic.main.sub_item.view.*

class MainActivity : AppCompatActivity() {

    private var tabContainer: LinearLayout? = null
    private var recyclerView: RecyclerView? = null
    private var foodAndBeverageAdapter: FoodAndBeverageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabContainer = findViewById<LinearLayout>(R.id.tabContainer);
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView);
        val tvInternetWarnMsg = findViewById<TextView>(R.id.tvInternetWarnMsg)

        //Initializing FnBView model
        val viewModel: FnBViewModel by lazy {
            ViewModelProviders.of(this).get(FnBViewModel::class.java)
        }

        //Observer to inflate recycler view once the API call completes
        viewModel.getFoodList().observe(this, Observer { foodList ->
            updateView(foodList!!, viewModel)
            tvInternetWarnMsg.visibility = View.GONE
        })

        //Making webservice call after checking the internet connectivity
        if (isNetworkAvailable(this)) {
            viewModel.init()
        } else {
            tvInternetWarnMsg.visibility = View.VISIBLE
        }
        tvInternetWarnMsg.setOnClickListener {
            if (isNetworkAvailable(this)) {
                viewModel.init()
            }
        }

        //Observer to update the F&B Summary data in the slider
        viewModel.updateFnBSummary().observe(this, Observer { fnbList ->

            var totalPrice: Int = 0
            val fb_summary_item_container = findViewById<LinearLayout>(R.id.fb_summary_item_container)
            fb_summary_item_container.removeAllViews()
            for (fnb in fnbList!!) {
                totalPrice += fnb.totalITemPrice.toInt()
                fb_summary_item_container.addView(getFnBSummaryItemView(fnb))
            }

            val tvTotalPrice = findViewById<TextView>(R.id.tvTotalPrice)
            tvTotalPrice.text = totalPrice.toString()

        })

        //Bottom Sheet Slider
        val bottomSheet = findViewById<NestedScrollView>(R.id.bs_food_beverage);
        val behaviour = BottomSheetBehavior.from(bottomSheet);
        val ivSliderIcon = findViewById<ImageView>(R.id.iv_slider_icon);
        behaviour.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                ivSliderIcon.rotation = slideOffset * 180;
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    behaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        })
    }

    /**
     * This methods used to check the internet availability
     * return positive if internet connectivity exists and vice versa
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var activeNetworkInfo: NetworkInfo? = null
        activeNetworkInfo = cm.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
    }

    /**
     * This method returns food item view in the F&B summary bottom slider
     */
    private fun getFnBSummaryItemView(fnb: FnB): View {

        val view = LayoutInflater.from(this).inflate(R.layout.food_beverage_summary_item, null)
        view.findViewById<TextView>(R.id.tvFoodName).text = fnb.Name + " (" + fnb.orderQty + ")"
        view.findViewById<TextView>(R.id.tvPrice).text = fnb.totalITemPrice.toString()

        return view
    }

    /**
     * This methods updates the food items in the recycler view based
     * on the tab click
     */
    private fun updateView(foodList: List<Food>, viewModel: FnBViewModel) {

        foodList.get(0).isSelected = true;
        loadTabs(foodList)

        recyclerView?.layoutManager = LinearLayoutManager(this)

        foodAndBeverageAdapter = FoodAndBeverageAdapter(foodList.get(0).fnblist, this, viewModel)
        recyclerView?.adapter = foodAndBeverageAdapter

    }

    /**
     * Dynamically loads the tab based on the data provided by the
     * web service
     */
    private fun loadTabs(foodList: List<Food>) {

        tabContainer?.removeAllViews()
        for (food in foodList) {
            val view = LayoutInflater.from(this).inflate(R.layout.tab, null)
            val tvTabName = view.findViewById<TextView>(R.id.tvTabName)
            val tabLine = view.findViewById<View>(R.id.tabLine)
            tvTabName.text = food.TabName
            if (food.isSelected) {
                tvTabName.typeface = Typeface.DEFAULT_BOLD;
                tabLine.visibility = View.VISIBLE
            }

            tvTabName.setOnClickListener {
                updateTabStatus(food.TabName, foodList)
                foodAndBeverageAdapter?.foodItemList = food.fnblist
                foodAndBeverageAdapter?.notifyDataSetChanged()
            }

            tabContainer?.addView(view)
        }
    }

    /**
     * Updated the tab view on clicking it
     */
    private fun updateTabStatus(tabName: String, foodList: List<Food>) {
        for (food in foodList) {
            food.isSelected = tabName.equals(food.TabName)
        }
        loadTabs(foodList)
    }
}
