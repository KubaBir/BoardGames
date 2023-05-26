package edu.put.inf151894

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import java.time.Instant
import java.time.LocalDate

class SetupActivity : AppCompatActivity() {

    private lateinit var usernameInput: EditText
    private lateinit var confirmBtn: Button
    private lateinit var syncProgressText: TextView
    private lateinit var progressBar: ProgressBar



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        val cache = getSharedPreferences("cache", Context.MODE_PRIVATE)


        syncProgressText = findViewById(R.id.syncProgressText)
        usernameInput = findViewById(R.id.usernameInput)
        confirmBtn = findViewById(R.id.btnConfirm)
        progressBar = findViewById(R.id.progressBar)

        progressBar.max = 100
        progressBar.visibility = View.INVISIBLE

        confirmBtn.setOnClickListener {
//            if (cache.getBoolean("configDone", false))
//                startActivity(Intent(this,MainMenuActivity::class.java))
            loadData()

        }
    }

    private fun loadData() {
        val cache = getSharedPreferences("cache", MODE_PRIVATE)
        val username = usernameInput.text.toString()
        cache.edit().putString("username", username).apply()
        cache.edit().putBoolean("configDone", true).apply()
        cache.edit().putString("lastSync", LocalDate.now().toString()).apply()

        Toast.makeText(this, "Loading data...", Toast.LENGTH_SHORT).show()
        syncProgressText.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE

        var numExpansions = 0
        var numGames = 0

        Thread(Runnable {
            var url = "https://boardgamegeek.com/xmlapi2/collection?username=" + cache.getString("username","") +
                    "&subtype=boardgame&excludesubtype=boardgameexpansion" + "&stats=1"
            var games = XmlParserTask().execute(url)
            if(games.get().isEmpty()) {
                games = XmlParserTask().execute(url)
            }
            runOnUiThread {
                progressBar.setProgress(20, true)
            }
            Thread.sleep(250)

            url = "https://boardgamegeek.com/xmlapi2/collection?username=" + cache.getString("username", "") +
                    "&subtype=boardgameexpansion" + "&stats=1"
            var expansions = XmlParserTask().execute(url)
            if(expansions.get().isEmpty()) {
                expansions = XmlParserTask().execute(url)
            }
            runOnUiThread {
                progressBar.setProgress(40, true)
            }
            Thread.sleep(250)

            val dbHandler = MyDBHandler(this, null,null,1)
            dbHandler.format()
            runOnUiThread {
                progressBar.setProgress(50, true)
            }
            Thread.sleep(250)

            var existingGameIds = mutableListOf<Int>()
            var existingExpansionIds = mutableListOf<Int>()

            for (game in expansions.get()) {
                if (existingExpansionIds.contains(game.id)) continue
                numExpansions += 1
                dbHandler.addExpansion(game)
                existingExpansionIds.add(game.id)
            }

            runOnUiThread {
                progressBar.setProgress(75, true)
            }
            Thread.sleep(250)

            for (game in games.get()) {
                if (existingGameIds.contains(game.id)) continue
                numGames += 1
                dbHandler.addGame(game)
                existingGameIds.add(game.id)
            }

            dbHandler.close()

            cache.edit().putString("numGames", numGames.toString()).apply()
            cache.edit().putString("numExpansions", numExpansions.toString()).apply()


            runOnUiThread {
                progressBar.setProgress(100, true)
                Toast.makeText(this, "Loading completed!", Toast.LENGTH_SHORT).show()
            }
            startActivity(Intent(this, MainMenuActivity::class.java))

        }).start()

    }
}