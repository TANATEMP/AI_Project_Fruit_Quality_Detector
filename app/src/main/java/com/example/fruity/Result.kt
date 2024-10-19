package com.example.fruity

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.fruity.ui.theme.FruityTheme

class Result : ComponentActivity() {
    lateinit var textView: TextView
    lateinit var imageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        textView=findViewById(R.id.resultText)
        imageView=findViewById(R.id.imageView)
        val result = intent.getStringExtra("EXTRA_RESULT")
        val uri: Uri? = intent.getParcelableExtra<Uri>("img")
        textView.text = result
        imageView.setImageURI(uri)
    }
    fun BackToSelectPic(view: View) {
        val Intent = Intent(this,Model::class.java)
        startActivity(Intent)
    }
}
