package edu.put.inf151894

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.time.Instant
import java.time.LocalDate


class SyncActivity : AppCompatActivity() {

    private lateinit var lastSync: TextView
    private lateinit var syncProgressText: TextView
    private lateinit var btnSync: Button
    private lateinit var btnBack: Button
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync)

        lastSync = findViewById(R.id.lastSync)
        syncProgressText = findViewById(R.id.syncProgressText)
        btnSync = findViewById(R.id.btnSync)
        btnBack = findViewById(R.id.btnBack)
        progressBar = findViewById(R.id.progressBar)

        progressBar.max = 100
        progressBar.visibility = View.INVISIBLE


        val cache = getSharedPreferences("cache", MODE_PRIVATE)
        lastSync.text = cache.getString("lastSync", "")

        btnSync.setOnClickListener {
            sync()
        }
        btnBack.setOnClickListener {
            startActivity(Intent(this, MainMenuActivity::class.java))
        }
    }

    private fun sync() {
        val cache = getSharedPreferences("cache", MODE_PRIVATE)
        val lastSyncLong = cache.getLong("lastSyncMillis", Instant.now().minusSeconds(100000).toEpochMilli())

        if(lastSyncLong < Instant.now().minusSeconds(86400).toEpochMilli()) {
            Toast.makeText(this, "Synchronization started", Toast.LENGTH_SHORT).show()
            syncProgressText.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE
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

                var numExpansions = 0
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

                var numGames = 0
                for (game in games.get()) {
                    if (existingGameIds.contains(game.id)) continue
                    numGames += 1
                    dbHandler.addGame(game)
                    existingGameIds.add(game.id)
                }

                runOnUiThread {
                    progressBar.setProgress(100, true)

                    cache.edit().putString("lastSync", LocalDate.now().toString()).apply()
                    cache.edit().putLong("lastSyncMillis", Instant.now().toEpochMilli()).apply()
                    lastSync.text = cache.getString("lastSync", "")
                    Toast.makeText(this, "Synchronization completed!", Toast.LENGTH_SHORT).show()
                }
            }).start()
        }
        else Toast.makeText(this, "Data is already synchronised!", Toast.LENGTH_SHORT).show()

    }
}