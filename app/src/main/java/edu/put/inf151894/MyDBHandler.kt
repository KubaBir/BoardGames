package edu.put.inf151894

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


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
                          const val COLUMN_THUMBNAIL = "thumbnail"
                          const val COLUMN_MINPLAYERS = "minplayers"
                          const val COLUMN_MAXPLAYERS = "maxplayers"
                          const val COLUMN_AVGRATING = "avgrating"
                      }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_GAMES_TABLE = ("CREATE TABLE IF NOT EXISTS " +
                TABLE_GAMES + "(" +
                COLUMN_ID + " DOUBLE PRIMARY KEY," +
                COLUMN_TITLE + " TEXT," +
                COLUMN_TITLE_PL + " TEXT," +
                COLUMN_RELEASED + " INTEGER," +
                COLUMN_IMAGE + " TEXT," +
                COLUMN_THUMBNAIL + " TEXT," +
                COLUMN_MINPLAYERS + " INTEGER," +
                COLUMN_MAXPLAYERS + " INTEGER," +
                COLUMN_AVGRATING + " FLOAT" + ")")
        try{
            db.execSQL(CREATE_GAMES_TABLE)
        } catch (e: SQLException) {
            Log.e("sql", "${e.message}")
        }
        Log.d("test","creating db")
//        val CREATE_GAMES_TABLE = ("CREATE TABLE " +
//                TABLE_GAMES + "(" +
//                COLUMN_ID + " DOUBLE PRIMARY KEY," +
//                COLUMN_TITLE + " TEXT," +
//                COLUMN_TITLE_PL + " TEXT," +
//                COLUMN_RELEASED + " INTEGER," +
//                COLUMN_IMAGE + " TEXT" + ")")
//        db.execSQL(CREATE_GAMES_TABLE)
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
        values.put(COLUMN_THUMBNAIL, game.thumbnail)
        values.put(COLUMN_MINPLAYERS, game.minPlayers)
        values.put(COLUMN_MAXPLAYERS, game.maxPlayers)
        values.put(COLUMN_AVGRATING, game.avgRating)

        val db = this.writableDatabase
        db.insert(TABLE_GAMES, null, values)
        db.close()
    }

    fun format() {
        val db = this.writableDatabase
        db.delete(TABLE_GAMES, null, null)
        //db.execSQL("DROP TABLE IF EXISTS $TABLE_GAMES")
    }

    fun getAllBoardGames(): List<Game> {
        val db = this.writableDatabase

        val boardGames = mutableListOf<Game>()
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_GAMES ORDER BY $COLUMN_TITLE", null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val titlePl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE_PL))
            val yearPublished = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RELEASED))
            val image = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE))
            val thumbnail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_THUMBNAIL))
            val minPlayers = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MINPLAYERS))
            val maxPlayers = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MAXPLAYERS))
            val avgRating = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_AVGRATING))

            val game = Game(title, titlePl, yearPublished, id, image, thumbnail, minPlayers,maxPlayers,avgRating)
            boardGames.add(game)
        }
        cursor.close()
        db.close()
        return boardGames
    }

    fun getGameById(id: Int): Game? {
        val db = this.writableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_GAMES WHERE $COLUMN_ID = $id", null)
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val titlePl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE_PL))
            val yearPublished = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RELEASED))
            val image = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE))
            val thumbnail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_THUMBNAIL))
            val minPlayers = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MINPLAYERS))
            val maxPlayers = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MAXPLAYERS))
            val avgRating = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_AVGRATING))
            val game = Game(title, titlePl, yearPublished, id, image, thumbnail, minPlayers,maxPlayers,avgRating)
            return game
        }
        return null
    }


}