package edu.put.inf151894

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import coil.load
import java.lang.Exception
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class GameDetailActivity : AppCompatActivity() {

    private lateinit var image: ImageView
    private lateinit var avgRating: TextView
    private lateinit var playerCount: TextView
    private lateinit var releasedIn: TextView

    private lateinit var btnNext: Button
    private lateinit var btnPrev: Button
    private lateinit var btnAdd: Button
    private lateinit var btnDelete: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_detail_view)

        btnNext = findViewById(R.id.btnNext)
        btnPrev = findViewById(R.id.btnPrev)
        btnAdd = findViewById(R.id.btnAdd)
        btnDelete = findViewById(R.id.btnDelete)


        val gameId = intent.getIntExtra("gameId", 0)
        val dbHandler = MyDBHandler(this, null,null,1)
        val game: Game? = dbHandler.getGameById(gameId)
        if (game != null){
            image = findViewById(R.id.imageView)
            image.load(game.image)

            avgRating = findViewById(R.id.avgRating)
            playerCount = findViewById(R.id.playerCount)
            releasedIn = findViewById(R.id.released)

            avgRating.text = "Average rating: ${(game.avgRating * 10).roundToInt().toDouble() / 10}"
            playerCount.text = "Players: ${game.minPlayers} - ${game.maxPlayers}"
            releasedIn.text = "Released in: ${game.released}"

        }


    }
}