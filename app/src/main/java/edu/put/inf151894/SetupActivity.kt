package edu.put.inf151894

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import java.time.LocalDate

class SetupActivity : AppCompatActivity() {

    private lateinit var usernameInput: EditText
    private lateinit var confirmBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        val cache = getSharedPreferences("cache", Context.MODE_PRIVATE)



        usernameInput = findViewById(R.id.usernameInput)
        confirmBtn = findViewById(R.id.confirmBtn)

        confirmBtn.setOnClickListener {
            val username = usernameInput.text.toString()

            val cache = getSharedPreferences("cache", Context.MODE_PRIVATE)
            if (cache.getBoolean("configDone", false))
                startActivity(Intent(this,MainMenuActivity::class.java))

            cache.edit().putString("username", username).apply()
            cache.edit().putString("lastSync", LocalDate.now().toString()).apply()
            cache.edit().putBoolean("configDone", true).apply()

            var url = "https://boardgamegeek.com/xmlapi2/collection?username=" + cache.getString("username","") +
                      "&subtype=boardgame&excludesubtype=boardgameexpansion" + "&stats=1"
            val games = XmlParserTask().execute(url)

            url = "https://boardgamegeek.com/xmlapi2/collection?username=" + cache.getString("username", "") +
                   "&subtype=boardgameexpansion" + "&stats=1"

            val expansions = XmlParserTask().execute(url)

            val dbHandler = MyDBHandler(this, null,null,1)
            var existingGameIds = mutableListOf<Int>()
            var existingExpansionIds = mutableListOf<Int>()


            var numGames = 0
            for (game in games.get()) {
                if (existingGameIds.contains(game.id)) continue
                numGames += 1
                dbHandler.addGame(game)
                existingGameIds.add(game.id)
            }
            Log.d("RES", "Dodano: $numGames gier")

            var numExpansions = 0
            for (game in expansions.get()) {
                if (existingExpansionIds.contains(game.id)) continue
                numExpansions += 1
                dbHandler.addExpansion(game)
                existingExpansionIds.add(game.id)
            }
            Log.d("RES", "Dodano: $numExpansions dodatkow")

            dbHandler.close()

            cache.edit().putString("numGames", numGames.toString()).apply()
            cache.edit().putString("numExpansions", numExpansions.toString()).apply()


            startActivity(Intent(this, MainMenuActivity::class.java))
        }
    }
}