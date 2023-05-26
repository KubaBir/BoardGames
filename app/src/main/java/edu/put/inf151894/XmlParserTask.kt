package edu.put.inf151894


import android.os.AsyncTask
import android.util.Log
import org.w3c.dom.Node
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class XmlParserTask : AsyncTask<String, Void, List<Game>>() {

    override fun doInBackground(vararg urls: String?): List<Game> {
        val url = URL(urls[0])
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.readTimeout = 10000
        connection.connectTimeout = 15000
        connection.requestMethod = "GET"
        connection.doInput = true

        try {
            connection.connect()
            return parseXml(connection.inputStream)
        } catch (e: Exception) {
            Log.e("XmlParserTask", "Error during parsing", e)
        } finally {
            connection.disconnect()
        }
        return emptyList()
    }

    private fun parseXml(inputStream: InputStream): List<Game> {
        val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc = docBuilder.parse(inputStream)
        doc.documentElement.normalize()


        val itemList = doc.getElementsByTagName("item")
        val games = mutableListOf<Game>()
        for (i in 0 until itemList.length) {
            val itemNode = itemList.item(i)
            if (itemNode.nodeType == org.w3c.dom.Node.ELEMENT_NODE) {
                val elem = itemNode as org.w3c.dom.Element

                val nameElement = elem.getElementsByTagName("name").item(0)
                val name = nameElement?.textContent ?: ""

                val yearPublishedElement = elem.getElementsByTagName("yearpublished").item(0)
                val released = yearPublishedElement?.textContent?.toIntOrNull() ?: 0

                val imageElement = elem.getElementsByTagName("image").item(0)
                val image = imageElement?.textContent ?: ""

                val thumbnailElement = elem.getElementsByTagName("thumbnail").item(0)
                val thumbnail = thumbnailElement?.textContent ?: ""

                val id = elem.getAttribute("objectid").toInt()

                var minPlayers = 0
                var maxPlayers = 0
                var avgRating = 0f
                val stats: Node? = elem.getElementsByTagName("stats").item(0)
                if (stats != null) {
                    val stats = stats as org.w3c.dom.Element
                    var temp1 = stats.getAttribute("minplayers")
                    var temp2 = stats.getAttribute("maxplayers")

                    if (temp1 != "") {
                        minPlayers = temp1.toInt()
                    }
                    if (temp2 != "") {
                        maxPlayers = temp2.toInt()
                    }

                    var rating = stats.getElementsByTagName("rating")?.item(0)
                    if (rating != null) {
                        rating = rating as org.w3c.dom.Element
                        var avgRatingField = rating.getElementsByTagName("average")?.item(0)
                        if (avgRatingField != null) {
                            avgRatingField = avgRatingField as org.w3c.dom.Element
                            var temp3 = avgRatingField.getAttribute("value")
                            if (temp3 != "") {
                                avgRating = temp3.toFloat()
                            }
                        }
                    }

                }


                val game = Game(
                    title = name,
                    titlePL = name,
                    released = released,
                    id = id,
                    image = image,
                    thumbnail = thumbnail,
                    minPlayers = minPlayers,
                    maxPlayers = maxPlayers,
                    avgRating = avgRating
                )
                games.add(game)
            }
        }
        return games
    }
}