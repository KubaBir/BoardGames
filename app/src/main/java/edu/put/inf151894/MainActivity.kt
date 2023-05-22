package edu.put.inf151894

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cache = getSharedPreferences("cache", Context.MODE_PRIVATE)
        if(cache.getBoolean("configDone", false)) {
            // List games if account is linked
            startActivity(Intent(this, MainMenu::class.java))
        } else {
            // Enter setup if not done already
            startActivity(Intent(this, Setup::class.java))
        }
    }
}