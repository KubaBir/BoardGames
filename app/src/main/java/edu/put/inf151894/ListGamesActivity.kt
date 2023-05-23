package edu.put.inf151894


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListGamesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_games)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val dbHandler = MyDBHandler(this, null,null,1)
        val games = dbHandler.getAllBoardGames()
        val adapter = Adapter(this, games)

        var intent: Intent = Intent(this, GameDetailActivity::class.java)

        recyclerView.adapter = adapter
        adapter.setOnClickListener(object: Adapter.OnClickListener {
            override fun onClick(position: Int, model: Game) {
                Log.d("onclick","test ${model.title}")

                intent.putExtra("gameId", model.id)
                startActivity(intent)

            }
        })
    }





}