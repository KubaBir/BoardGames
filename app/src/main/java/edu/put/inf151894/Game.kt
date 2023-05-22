package edu.put.inf151894

import android.provider.ContactsContract.CommonDataKinds.Note
import java.util.Date


class Game {
    var gameArrayList = ArrayList<Game>()

    var title: String? = null
    var titlePL: String? = null
    var released: Int = 0
    var id: Int? = null
    var image: String? = null

    constructor(title: String?, titlePL: String?, released: Int, id: Int, image: String?) {
        this.title = title
        this.titlePL = titlePL
        this.released = released
        this.id = id
        this.image = image
    }

    constructor(title: String?, titlePL: String?, released: Int, id: Int) {
        this.title = title
        this.titlePL = titlePL
        this.released = released
        this.id = id
    }

    constructor(title: String?, released: Int, id: Int) {
        this.title = title
        this.released = released
        this.id = id
    }

    constructor(title: String?) {
        this.title = title
    }




}