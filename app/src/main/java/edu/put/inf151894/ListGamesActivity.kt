package edu.put.inf151894


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.min

class ListGamesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var nextPageButton: Button
    private lateinit var prevPageButton: Button
    private lateinit var backButton: Button

    private lateinit var titleText: TextView

    var page: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_games)

        page = intent.getIntExtra("page", 1)

        nextPageButton = findViewById(R.id.nextPageButton)
        prevPageButton = findViewById(R.id.prevPageButton)
        backButton = findViewById(R.id.backButton)

        if (page == 1) {
            prevPageButton.visibility = View.GONE
        }

        titleText = findViewById(R.id.titleText)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val dbHandler = MyDBHandler(this, null,null,1)

        val type = intent.getStringExtra("type")

        var games: List<Game>
        if (type == "games") {
            games = dbHandler.getAllGames()
            titleText.text = "Games"
        } else {
            games = dbHandler.getAllExpansions()
            titleText.text = "Expansions"

        }
        if (games.size < page*50) nextPageButton.visibility = View.GONE
        games = games.subList((page-1)*50, min(page*50, games.size))

        val adapter = Adapter(this, games)

        var intent: Intent = Intent(this, GameDetailActivity::class.java)

        recyclerView.adapter = adapter
        adapter.setOnClickListener(object: Adapter.OnClickListener {
            override fun onClick(position: Int, model: Game) {
                if (type == "games") {
                    intent.putExtra("type", "game")
                } else {
                    intent.putExtra("type", "expansion")
                }
                intent.putExtra("gameId", model.id)
                startActivity(intent)
            }
        })
        backButton.setOnClickListener {
            startActivity(Intent(this, MainMenuActivity::class.java))
        }
        nextPageButton.setOnClickListener {
            var paginationIntent: Intent = Intent(this, ListGamesActivity::class.java)
            paginationIntent.putExtra("page", page+1)
            paginationIntent.putExtra("type", type)
            Log.e("extra",type.toString())

            startActivity(paginationIntent)
        }
        prevPageButton.setOnClickListener {
            var paginationIntent: Intent = Intent(this, ListGamesActivity::class.java)
            paginationIntent.putExtra("page", page-1)
            paginationIntent.putExtra("type", type)
            Log.e("extra",type.toString())
            startActivity(paginationIntent)
        }
    }





}