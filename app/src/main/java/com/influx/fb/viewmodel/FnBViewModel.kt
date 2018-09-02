package com.influx.fb.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.influx.fb.model.FnB
import com.influx.fb.model.Food
import com.influx.fb.model.Response.FnBResponse
import com.influx.fb.webservice.APIClient
import com.influx.fb.webservice.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FnBViewModel() : ViewModel() {

    val foodList = MutableLiveData<List<Food>>()
    val selectedFoodList = MutableLiveData<ArrayList<FnB>>()
    var selectedFoodArrayList: ArrayList<FnB> = arrayListOf()

    fun getFoodList(): LiveData<List<Food>> {
        return foodList
    }

    fun updateFnBSummary(): MutableLiveData<ArrayList<FnB>> {
        return selectedFoodList
    }

    fun init() {

        val apiService = APIClient.client.create(APIInterface::class.java)

        apiService.getFoodList().enqueue(object : Callback<FnBResponse> {

            override fun onResponse(call: Call<FnBResponse>?, response: Response<FnBResponse>?) {

                val fnBResponse: FnBResponse = response?.body()!!
                foodList.value = fnBResponse.FoodList
            }

            override fun onFailure(call: Call<FnBResponse>?, t: Throwable?) {
                Log.e("WEB SERVICE ERROR", call.toString())
            }
        })
    }

    fun updateSelectedList(fnb: FnB) {
        val tempSelectedFoodArrayList : ArrayList<FnB> = selectedFoodArrayList
//        if (tempSelectedFoodArrayList.size == 0) {
            selectedFoodArrayList.add(fnb)
//        }
        for (selectedFoodListItem in tempSelectedFoodArrayList) {
            if (fnb.VistaFoodItemId.equals(selectedFoodListItem.VistaFoodItemId)) {
                selectedFoodListItem.totalITemPrice = fnb.totalITemPrice
                selectedFoodListItem.orderQty = fnb.orderQty
            } else {
//                selectedFoodArrayList.add(fnb)
            }
        }
        selectedFoodList.value = selectedFoodArrayList
    }

    fun removeItem(fnb: FnB) {
        val tempSelectedFoodArrayList : ArrayList<FnB> = selectedFoodArrayList
        for (selectedFoodListItem in tempSelectedFoodArrayList) {
            if (fnb.VistaFoodItemId.equals(selectedFoodListItem.VistaFoodItemId)) {
//                selectedFoodArrayList.remove(fnb)
            }
        }
        selectedFoodList.value = selectedFoodArrayList
    }

}