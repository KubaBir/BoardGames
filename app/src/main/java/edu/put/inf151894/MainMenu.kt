package edu.put.inf151894

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import java.time.LocalDate

class MainMenu : AppCompatActivity() {

    lateinit var usernameField: TextView
    lateinit var numGames: TextView
    lateinit var numExpansions: TextView
    lateinit var lastSync: TextView

    lateinit var gameList: Button
    lateinit var expansionList: Button
    lateinit var sync: Button
    lateinit var restoreDefaults: Button




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        usernameField = findViewById(R.id.username)
        numGames = findViewById(R.id.numGames)
        numExpansions = findViewById(R.id.numExpansions)
        lastSync = findViewById(R.id.lastSync)

        gameList = findViewById(R.id.gameList)
        expansionList = findViewById(R.id.expansionList)
        sync = findViewById(R.id.sync)
        restoreDefaults = findViewById(R.id.restoreDefaults)

        val cache = getSharedPreferences("cache", Context.MODE_PRIVATE)

        usernameField.text = cache.getString("username", "")
        lastSync.text = cache.getString("lastSync", "Never")
        numGames.text = cache.getString("numGames", "0")

        restoreDefaults.setOnClickListener {
            // Format cache
            cache.edit().clear().apply()

            // Format db
            val dbHandler = MyDBHandler(this, null,null,1)
            dbHandler.format()
            dbHandler.close()

            // Enter configuration
            startActivity(Intent(this, Setup::class.java))
        }

        sync.setOnClickListener {
            //val dbHandler = MyDBHandler(this, null,null,1)

            // IMPLEMENT SYNC

            cache.edit().putString("lastSync", LocalDate.now().toString()).apply()
        }

        gameList.setOnClickListener {
            startActivity(Intent(this, ListGames::class.java))

        }
    }
}