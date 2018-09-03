package com.influx.fb.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.influx.fb.Utils.Constants
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

    /**
     * Initiates the retrofit web service call
     */
    fun init() {

        val apiService = APIClient.client.create(APIInterface::class.java)

        apiService.getFoodList().enqueue(object : Callback<FnBResponse> {

            override fun onResponse(call: Call<FnBResponse>?, response: Response<FnBResponse>?) {

                val fnBResponse: FnBResponse = response?.body()!!
                Constants.CURRENCY = fnBResponse.Currency
                foodList.value = fnBResponse.FoodList
            }

            override fun onFailure(call: Call<FnBResponse>?, t: Throwable?) {
                Log.e("WEB SERVICE ERROR", call.toString())
            }
        })
    }

    /**
     * Adds the selected food item into selected list which later updates
     * the F&B Summary slider
     */
    fun addItemToSelectedList(fnb: FnB) {
        selectedFoodArrayList.add(fnb)
        selectedFoodList.value = selectedFoodArrayList
    }

    /**
     * Updates quantity and price of the respective food item in selected list
     * which updates the F&B Summary slider
     */
    fun updateSelectedList(fnb: FnB) {
        for (selectedFoodListItem in selectedFoodArrayList) {
            if (fnb.VistaFoodItemId.equals(selectedFoodListItem.VistaFoodItemId)) {
                selectedFoodListItem.totalITemPrice = fnb.totalITemPrice
                selectedFoodListItem.orderQty = fnb.orderQty
                break;
            }
        }
        selectedFoodList.value = selectedFoodArrayList
    }

    /**
     * Removes a food item (when its quantity decreased to 0) from the selected list which updates
     * the F&B Summary slider
     */
    fun removeItem(fnb: FnB) {
        val tempSelectedFoodArrayList: ArrayList<FnB> = arrayListOf()
        tempSelectedFoodArrayList.addAll(selectedFoodArrayList)
        for (selectedFoodListItem in tempSelectedFoodArrayList) {
            if (fnb.VistaFoodItemId.equals(selectedFoodListItem.VistaFoodItemId)) {
                selectedFoodArrayList.remove(fnb)
            }
        }
        selectedFoodList.value = selectedFoodArrayList
    }

}