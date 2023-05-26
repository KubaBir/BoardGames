package edu.put.inf151894


class Game {
    var title: String? = null
    var titlePL: String? = null
    var released: Int = 0
    var id: Int = 0
    var image: String? = null
    var thumbnail: String? = null
    var minPlayers: Int = 0
    var maxPlayers: Int = 0
    var avgRating: Float = 0.0f


    constructor(
        title: String?,
        titlePL: String?,
        released: Int,
        id: Int,
        image: String?,
        thumbnail: String?,
        minPlayers: Int,
        maxPlayers: Int,
        avgRating: Float
    ) {
        this.title = title
        this.titlePL = titlePL
        this.released = released
        this.id = id
        this.image = image
        this.thumbnail = thumbnail
        this.minPlayers = minPlayers
        this.maxPlayers = maxPlayers
        this.avgRating = avgRating
    }


}