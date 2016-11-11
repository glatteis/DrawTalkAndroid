package me.glatteis.drawtalkandroid

import android.content.Intent
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_10
import org.java_websocket.drafts.Draft_17
import org.java_websocket.exceptions.WebsocketNotConnectedException
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONArray
import org.json.JSONObject
import java.net.URI
import java.util.*

/**
 * Created by Linus on 29.10.2016!
 */
object Connection {

    var client: Client? = null

}

class Client() : WebSocketClient(URI("ws://192.168.178.34:4567/chat/"), Draft_17()) {

    private val listeners = ArrayList<MessageListener>()

    fun addListener(listener: MessageListener) {
        listeners += listener
    }

    fun removeListener(listener: MessageListener) {
        listeners -= listener
    }

    override fun onOpen(handshakeData: ServerHandshake?) {
        handshakeData ?: return
        for (listener in listeners) {
            listener.onOpen(handshakeData)
        }
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        println("Closed: $code $reason $remote")
    }

    override fun onMessage(message: String?) {
        message ?: return
        for (listener in listeners) {
            listener.onMessage(message)
        }
    }

    override fun onError(ex: Exception?) {
        ex?.printStackTrace()
    }

}

interface MessageListener {
    fun onMessage(message: String) {
    }
    fun onOpen(handshakeData: ServerHandshake) {
    }
}

fun JSONObject.has(vararg keys: String): Boolean {
    for (key in keys) {
        if (!has(key)) {
            return false
        }
    }
    return true
}

fun Client.sendPairs(vararg args: Pair<String, Any>) {
    val jsonObject = org.json.JSONObject()
    for (arg in args) {
        jsonObject.put(arg.first, arg.second)
    }
    println(jsonObject.toString())
    send(jsonObject.toString())
    println("Sent")
    /*
    try {

    } catch (e: WebsocketNotConnectedException) {
        e.printStackTrace()
        val thread = Thread {
            val client = Client()
            Connection.client = client
            client.addListener(object : MessageListener {
                override fun onOpen(handshakeData: ServerHandshake) {
                    client.send(JSONObject()
                            .put("type", "set_username")
                            .put("username", ChatData.username).toString())
                }
            })
            client.connect()
        }
        thread.start()
    }
    */
}