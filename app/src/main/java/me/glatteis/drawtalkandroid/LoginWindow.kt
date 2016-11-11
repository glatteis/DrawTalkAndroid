package me.glatteis.drawtalkandroid

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import android.widget.TextView
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONArray
import org.json.JSONObject
import java.net.URI

class LoginWindow : AppCompatActivity() {

    var rooms: String? = null //for onSaveInstanceState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_window)

        val errorText = findViewById(R.id.error_text)!! as TextView

        val loginButton = findViewById(R.id.login_button)!!
        loginButton.setOnClickListener {
            errorText.text = ""

            val username = (findViewById(R.id.username)!! as EditText).text.toString().trim()
            if (username.isBlank()) {
                errorText.text = getString(R.string.login_error_field_blank)
                return@setOnClickListener
            }

            ChatData.username = username

            val thread = Thread {
                val client = Client()
                Connection.client = client
                client.addListener(object : MessageListener {
                    override fun onOpen(handshakeData: ServerHandshake) {
                        client.send(JSONObject()
                                .put("type", "set_username")
                                .put("username", username).toString())
                    }

                    override fun onMessage(message: String) {
                        println(message)
                        val jsonObject = JSONObject(message)

                        if (jsonObject["type"] == "rooms") {
                            client.removeListener(this)
                            ChatData.rooms = jsonObject["rooms"] as JSONArray
                            val intent = Intent(this@LoginWindow, ChatOverview::class.java)
                            startActivity(intent)
                        }

                    }
                })
                client.connect()
            }
            thread.start()
        }
    }
}
