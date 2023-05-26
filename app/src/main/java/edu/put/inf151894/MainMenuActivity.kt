package edu.put.inf151894

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import java.time.LocalDate

class MainMenuActivity : AppCompatActivity() {

    private lateinit var usernameField: TextView
    private lateinit var numGames: TextView
    private lateinit var numExpansions: TextView
    private lateinit var lastSync: TextView

    private lateinit var gameList: Button
    private lateinit var expansionList: Button
    private lateinit var sync: Button
    private lateinit var restoreDefaults: Button




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        val cache = getSharedPreferences("cache", Context.MODE_PRIVATE)
        if(!cache.getBoolean("configDone", false)) {
            // List games if account is linked
            startActivity(Intent(this, SetupActivity::class.java))
        }

        usernameField = findViewById(R.id.username)
        numGames = findViewById(R.id.numGames)
        numExpansions = findViewById(R.id.numExpansions)
        lastSync = findViewById(R.id.lastSync)

        gameList = findViewById(R.id.gameList)
        expansionList = findViewById(R.id.expansionList)
        sync = findViewById(R.id.sync)
        restoreDefaults = findViewById(R.id.restoreDefaults)

        usernameField.text = cache.getString("username", "")
        lastSync.text = cache.getString("lastSync", "Never")
        numGames.text = cache.getString("numGames", "0")
        numExpansions.text = cache.getString("numExpansions", "0")

        restoreDefaults.setOnClickListener {
            // Format cache
            cache.edit().clear().apply()

            // Format db
            val dbHandler = MyDBHandler(this, null,null,1)
            dbHandler.format()
            dbHandler.close()

            // Enter configuration
            startActivity(Intent(this, SetupActivity::class.java))
        }

        sync.setOnClickListener {
            //val dbHandler = MyDBHandler(this, null,null,1)

            // IMPLEMENT SYNC

            cache.edit().putString("lastSync", LocalDate.now().toString()).apply()
        }

        gameList.setOnClickListener {
            var intent = Intent(this, ListGamesActivity::class.java)
            intent.putExtra("type", "games")
            startActivity(intent)
        }

        expansionList.setOnClickListener {
            var intent = Intent(this, ListGamesActivity::class.java)
            intent.putExtra("type", "expansions")
            startActivity(intent)
        }
    }
}