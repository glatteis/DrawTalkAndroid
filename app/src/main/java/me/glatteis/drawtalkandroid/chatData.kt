package me.glatteis.drawtalkandroid

import org.json.JSONArray
import org.json.JSONObject
import java.util.*

/**
 * Created by Linus on 31.10.2016!
 */
object ChatData {

    var rooms: JSONArray = JSONArray()
    var username = ""
    var room: Room? = null

}

class Room(name: String, users: JSONArray)