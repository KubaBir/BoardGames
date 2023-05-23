package edu.put.inf151894

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cache = getSharedPreferences("cache", Context.MODE_PRIVATE)
        if(cache.getBoolean("configDone", false)) {
            // List games if account is linked
            startActivity(Intent(this, MainMenuActivity::class.java))
        } else {
            // Enter setup if not done already
            startActivity(Intent(this, SetupActivity::class.java))
        }
    }
}