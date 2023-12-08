package com.ame598.iotapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ame598.iotapp.network.IotApi
import com.ame598.iotapp.network.IotFaceObject
import com.ame598.iotapp.network.IotFaceServiceImpl
import com.ame598.iotapp.network.SimpleResponse
import kotlinx.coroutines.launch

enum class IotApiStatus { LOADING, ERROR, DONE }
class IotViewModel: ViewModel()  {


    private val _status = MutableLiveData<IotApiStatus>()
    // Internally, we use a MutableLiveData, because we will be updating the List of MarsPhoto
    // with new values
    private val _photo = MutableLiveData<IotFaceObject>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val photo: LiveData<IotFaceObject> = _photo


    private val _simpleResponse = MutableLiveData<SimpleResponse>()
    val simpleResponse: LiveData<SimpleResponse> = _simpleResponse


    fun getNewPhoto() {
        Log.d("vaishak","hello1")
        viewModelScope.launch {
            _status.value = IotApiStatus.LOADING
            try {
                Log.d("vaishak","hello2")
                _photo.value = IotApi.retrofitService.getPhotos()
                _status.value = IotApiStatus.DONE
            } catch (e: Exception) {
                Log.d("vaishak","hello3")
                Log.d("vaishak",e.message!!)
                _status.value = IotApiStatus.ERROR
                _photo.value = null
            }
        }
    }

    fun approveFace() {
        Log.d("vaishak","hello1")
        viewModelScope.launch {
            _status.value = IotApiStatus.LOADING
            try {
                Log.d("vaishak","hello2")
                _simpleResponse.value = IotApi.retrofitService.approve()
                _status.value = IotApiStatus.DONE
            } catch (e: Exception) {
                Log.d("vaishak","hello3")
                Log.d("vaishak",e.message!!)
                _status.value = IotApiStatus.ERROR
                _simpleResponse.value = null
            }
        }
    }

    fun approveOnceFace() {
        Log.d("vaishak","hello1")
        viewModelScope.launch {
            _status.value = IotApiStatus.LOADING
            try {
                Log.d("vaishak","hello2")
                _simpleResponse.value = IotApi.retrofitService.approveonce()
                _status.value = IotApiStatus.DONE
            } catch (e: Exception) {
                Log.d("vaishak","hello3")
                Log.d("vaishak",e.message!!)
                _status.value = IotApiStatus.ERROR
                _simpleResponse.value = null
            }
        }
    }

    fun denyFace() {
        Log.d("vaishak","hello1")
        viewModelScope.launch {
            _status.value = IotApiStatus.LOADING
            try {
                Log.d("vaishak","hello2")
                _simpleResponse.value = IotApi.retrofitService.deny()
                _status.value = IotApiStatus.DONE
            } catch (e: Exception) {
                Log.d("vaishak","hello3")
                Log.d("vaishak",e.message!!)
                _status.value = IotApiStatus.ERROR
                _simpleResponse.value = null
            }
        }
    }


}