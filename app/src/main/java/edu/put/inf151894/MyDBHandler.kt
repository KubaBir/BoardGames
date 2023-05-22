package edu.put.inf151894

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date


class MyDBHandler(context: Context, name: String?,
                  factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context,
    DATABASE_NAME, factory, DATABASE_VERSION) {
                      companion object {
                          val DATABASE_VERSION = 1
                          const val DATABASE_NAME = "productDB.db"
                          const val TABLE_GAMES = "games"
                          const val COLUMN_ID = "_id"
                          const val COLUMN_TITLE = "title"
                          const val COLUMN_TITLE_PL = "title_pl"
                          const val COLUMN_RELEASED = "released"
                          const val COLUMN_IMAGE = "image"
                      }

    private val dateFormat: DateFormat = SimpleDateFormat("MM-dd-yyyy HH:mm:ss")


    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_GAMES_TABLE = ("CREATE TABLE " +
                TABLE_GAMES + "(" +
                COLUMN_ID + " DOUBLE PRIMARY KEY," +
                COLUMN_TITLE + " TEXT," +
                COLUMN_TITLE_PL + " TEXT," +
                COLUMN_RELEASED + " INTEGER," +
                COLUMN_IMAGE + " TEXT" + ")")
        db.execSQL(CREATE_GAMES_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GAMES")
        onCreate(db)
    }

    fun addGame(game: Game) {
        val values = ContentValues()
        values.put(COLUMN_TITLE, game.title)
        values.put(COLUMN_TITLE_PL, game.titlePL)
        values.put(COLUMN_RELEASED, game.released)
        values.put(COLUMN_ID, game.id)
        values.put(COLUMN_IMAGE, game.image)

        val db = this.writableDatabase
        db.insert(TABLE_GAMES, null, values)
        db.close()
    }

    fun format() {
        val db = this.writableDatabase
        //db.execSQL("DROP TABLE IF EXISTS $TABLE_GAMES")
        db.delete(TABLE_GAMES, null, null)
    }

    fun getAllBoardGames(): List<Game> {
        val db = this.writableDatabase

        val boardGames = mutableListOf<Game>()
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_GAMES", null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val title_pl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE_PL))
            val yearPublished = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RELEASED))
            val image = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE))
            val game = Game(title, title_pl, yearPublished, id, image)
            boardGames.add(game)
        }
        cursor.close()
        db.close()
        return boardGames
    }


}