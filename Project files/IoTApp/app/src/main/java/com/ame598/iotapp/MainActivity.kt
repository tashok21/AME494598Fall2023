package com.ame598.iotapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ame598.iotapp.ui.theme.IoTAppTheme
import com.google.firebase.messaging.FirebaseMessaging
import java.nio.Buffer


class MainActivity : ComponentActivity() {

    var newMessageAvailable : Boolean = false
    private val _viewModel: IotViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = this.getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
        newMessageAvailable = sharedPreferences.getBoolean("newimageexistz", false)
        setContent {
            IoTAppTheme {
                // A surface container using the 'background' color from the theme
                MyApp(newMessageAvailable,modifier = Modifier.fillMaxSize(), viewModel = _viewModel, context = this)
            }
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            // Log and toast
        }
    }


    }



@Composable
fun MyApp(newMessageAvailable: Boolean, viewModel : IotViewModel, modifier: Modifier = Modifier, context: Context) {
    var texter = "You have no new requests"
    if(newMessageAvailable){
        texter = "You have a new request"
    }
    var shouldShowNewFace by remember { mutableStateOf(false) }
    Surface(modifier) {
        if(shouldShowNewFace){
            ShowFace( viewModel, onButtonClicked = {
                texter = "You have no new messages"
                shouldShowNewFace = false
            })
        }else {

            Greeting(viewModel,texter, onContinueClicked = {
                shouldShowNewFace = true
                val sharedPreferences = context.getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean("newimageexistz", false) // Replace true with your boolean value
                editor.apply()
            })
        }
    }
}

private fun getBitmap(buffer: Buffer): Bitmap {
    buffer.rewind()
    val bitmap = Bitmap.createBitmap(598, 598, Bitmap.Config.ARGB_8888)
    bitmap.copyPixelsFromBuffer(buffer)
    return bitmap
}


@Composable
fun ShowFace(viewModel: IotViewModel,
             onButtonClicked : () -> Unit,
             modifier: Modifier = Modifier) {
    val newphoto by viewModel.photo.observeAsState(null)
    LaunchedEffect(Unit) {
        viewModel.getNewPhoto()
    }
    Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .weight(3f)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),

                    shape = RectangleShape,
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    if(newphoto == null) {
                        Text(
                            text = "Loading...",
                            modifier = Modifier.fillMaxWidth()
                        )

                    }else{
                        val decodedString: ByteArray = Base64.decode(newphoto!!.imagedata, Base64.DEFAULT)
                        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                        Image(
                            bitmap = decodedByte.asImageBitmap(),
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )


                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .weight(1f)
            ) {
                Button(onClick = {
                    sendDecision(1, viewModel = viewModel)
                    onButtonClicked
                } ) {
                    Text(
                        text = "Approve",
                        style = TextStyle(fontSize = 15.sp)
                    )
                }
                Button(onClick = {
                    sendDecision(2, viewModel = viewModel)
                    onButtonClicked
                } ) {
                    Text(
                        text = "Approve Once",
                        style = TextStyle(fontSize = 15.sp)
                    )
                }
                Button(onClick = {
                    sendDecision(3, viewModel = viewModel)
                    onButtonClicked
                } ) {
                    Text(
                        text = "Deny",
                        style = TextStyle(fontSize = 15.sp)
                    )
                }
            }
        }
    }


@Composable
fun Greeting(viewModel: IotViewModel,
             name: String,
             onContinueClicked: () -> Unit,
             modifier: Modifier = Modifier) {
    Column(modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.Center)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center)
                .padding(8.dp)
            //.weight(1f)
        ) {
            Text(
                text = name,
                style = TextStyle(fontSize = 15.sp)
            )
        }
        if (!name.contains("no")) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                //.weight(1f)
            ) {
                Button(
                    onClick = onContinueClicked
                ) {
                    Text(
                        text = "View",
                        style = TextStyle(fontSize = 15.sp)
                    )
                }
                Button(onClick = { /*TODO*/ }) {
                    Text(
                        text = "Ignore",
                        style = TextStyle(fontSize = 15.sp)
                    )
                }

            }
        }
    }
}



private fun sendDecision(decision : Int, viewModel: IotViewModel){
    if(decision == 1){
        viewModel.approveFace()
    }else if (decision == 2){
        viewModel.approveOnceFace()
    }else{
        viewModel.denyFace()
    }
}