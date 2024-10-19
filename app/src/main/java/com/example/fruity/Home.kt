package com.example.fruity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity

class Home : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

    }

    fun goNext(view: View) {
        val Intent = Intent(this,Model::class.java)
        startActivity(Intent)
    }
}

