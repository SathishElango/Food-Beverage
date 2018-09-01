package com.influx.fb

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.influx.fb.model.Food
import com.influx.fb.viewmodel.FnBViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel: FnBViewModel by lazy {
            ViewModelProviders.of(this).get(FnBViewModel::class.java)
        }

        viewModel.getFoodList().observe(this, Observer { foodList ->
            Log.d("FOOD LIST COUNT : ", foodList?.size.toString())
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


    }
}
