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
            cache.edit().putString("username", username).apply()
            cache.edit().putString("lastSync", LocalDate.now().toString()).apply()
            cache.edit().putBoolean("configDone", true).apply()

            val url = "https://boardgamegeek.com/xmlapi2/collection?username=" + cache.getString("username","") +
                      "&subtype=boardgame&excludesubtype=boardgameexpansion" + "&stats=1"
            val games = XmlParserTask().execute(url)
            val dbHandler = MyDBHandler(this, null,null,1)

            var numGames = 0
            for (game in games.get()) {
                numGames += 1
                dbHandler.addGame(game)
            }
            Log.d("RES", "Dodano: $numGames gier")
            dbHandler.close()

            cache.edit().putString("numGames", numGames.toString()).apply()

            startActivity(Intent(this, MainMenuActivity::class.java))
        }
    }
}