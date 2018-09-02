package com.influx.fb

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.LinearLayoutManager
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
import com.influx.fb.model.Food
import com.influx.fb.viewmodel.FnBViewModel

class MainActivity : AppCompatActivity() {

    private var tabContainer: LinearLayout? = null
    private var recyclerView: RecyclerView? = null
    private var foodAndBeverageAdapter: FoodAndBeverageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabContainer = findViewById<LinearLayout>(R.id.tabContainer);
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView);

        val viewModel: FnBViewModel by lazy {
            ViewModelProviders.of(this).get(FnBViewModel::class.java)
        }

        viewModel.getFoodList().observe(this, Observer { foodList ->
            Log.d("FOOD LIST COUNT : ", foodList?.size.toString())
            updateView(foodList!!)
        })

        viewModel.init()

        val bottomSheet = findViewById<LinearLayout>(R.id.bs_food_beverage);
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

    private fun updateView(foodList: List<Food>) {

        foodList.get(0).isSelected = true;
        loadTabs(foodList)

        recyclerView?.layoutManager = LinearLayoutManager(this)

        foodAndBeverageAdapter = FoodAndBeverageAdapter(foodList.get(0).fnblist, this)
        recyclerView?.adapter = foodAndBeverageAdapter

    }

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

    private fun updateTabStatus(tabName: String, foodList: List<Food>) {
        for (food in foodList) {
            food.isSelected = tabName.equals(food.TabName)
        }
        loadTabs(foodList)
    }
}
