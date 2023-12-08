package com.ame598.clinetappbasic

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView

import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.EditText
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.ByteArrayOutputStream
import retrofit2.converter.gson.GsonConverterFactory
import org.json.JSONObject



class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var text : EditText
    lateinit var cameraButton: Button
    private val cameraRequestId = 123
    lateinit var  request: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        cameraButton= findViewById(R.id.cameraButton)
        imageView = findViewById(R.id.imageView)
        text = findViewById(R.id.nameoftherequester)
        text.setOnClickListener(View.OnClickListener {
            text.text.clear()
        })

        cameraButton.setOnClickListener {
            if (cameraButton.text == "Ask Access"){
                RetrofitClient.service.sendString(request).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        Log.d("Vaishak","here")
                        if (response.isSuccessful) {
                            val responseBody = response.body()?.string()
                            if(responseBody!!.contains("approved")){
                                runOnUiThread(
                                    Runnable {
                                        cameraButton.text = "Success"
                                        val colorStateList = ColorStateList.valueOf(Color.GREEN)
                                        cameraButton.backgroundTintList = colorStateList
                                    }
                                )
                            }else{
                                runOnUiThread(
                                    Runnable {
                                        cameraButton.text = "Request sent! Check back later"
                                        cameraButton.setTextColor(Color.BLACK)
                                        val colorStateList = ColorStateList.valueOf(Color.CYAN)
                                        cameraButton.backgroundTintList = colorStateList
                                    }
                                )

                            }
                        } else {
                            Log.d("Vaishak","not here")

                        }

                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.d("Vaishak","not here 1")
                        Log.d("Vaishak",t.message.toString())
                    }
                })
            }else {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, cameraRequestId)
            }
        }

    }

    object RetrofitClient {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.0.57:1234")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == cameraRequestId && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(imageBitmap)
            runOnUiThread(Runnable {
                cameraButton.text = "Ask Access"
            })
            val resizedBitmap = resizeBitmap(imageBitmap, 240, 320)
            val base64String = bitmapToBase64(resizedBitmap)
            request = User(text.text.toString(), base64String)

        }
    }


    private fun resizeBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}