package me.glatteis.drawtalkandroid

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class ChatOverview : AppCompatActivity() {

    val roomItems = ArrayList<ChatOverviewItem>()

    var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_overview)

        val rooms = ChatData.rooms

        for (i in 0..rooms.length() - 1) {
            val room = rooms[i] as JSONObject
            val roomItem = ChatOverviewItem(room.getString("name"), room.getBoolean("hasPassword"),
                    room.getInt("userCount"))
            roomItems += roomItem
        }

        roomItems.sortWith(Comparator<me.glatteis.drawtalkandroid.ChatOverviewItem> { lhs, rhs ->
            val n = rhs.numUsers - lhs.numUsers
            if (n == 0) {
                return@Comparator lhs.name.compareTo(rhs.name)
            }
            n
        })

        val arrayAdapter = ChatOverviewItemAdapter(this, roomItems)
        val listView = (findViewById(R.id.listView) as ListView)
        listView.adapter = arrayAdapter

        val button = findViewById(R.id.addChatButton) as ImageButton
        button.setOnClickListener {
            alertDialog = AlertDialog.Builder(this)
                    .setTitle("New Room")
                    .setMessage("Create a new chatroom")
                    .setView(R.layout.new_chat_prompt)
                    .setPositiveButton("OK", { dialogInterface, button ->
                        println("OK")
                        val dialog = alertDialog ?: return@setPositiveButton
                        val text = (dialog.findViewById(R.id.popupUsernameInput) as EditText).text
                        val password = (dialog.findViewById(R.id.popupPasswordInput) as EditText).text
                        println("$text $password")
                        if (text.isBlank()) return@setPositiveButton
                        if (password.isBlank()) {
                            println("Sending create_room packet")
                            Connection.client?.sendPairs(
                                    Pair("type", "create_room"),
                                    Pair("hasPassword", false),
                                    Pair("name", text.trim())
                            )
                        } else {
                            Connection.client?.sendPairs(
                                    Pair("type", "create_room"),
                                    Pair("name", text.trim()),
                                    Pair("hasPassword", true),
                                    Pair("password", password)
                            )
                        }

                    }).setNegativeButton("Cancel", { dialogInterface, button ->
            }).show()
        }

        listView.setOnItemClickListener { adapterView, view, position, id ->
            val item = listView.getItemAtPosition(position) as ChatOverviewItem
            if (item.hasPassword) {
                val input = EditText(this)
                input.inputType = InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
                input.hint = "Password"
                AlertDialog.Builder(this)
                        .setTitle("Join Room")
                        .setView(input)
                        .setPositiveButton("OK", { dialogInterface, i ->
                            Connection.client?.sendPairs(
                                    Pair("type", "join_room"),
                                    Pair("room_name", item.name),
                                    Pair("password", input.text)
                            )
                        }).setNegativeButton("Cancel", { dialogInterface, i ->
                }).show()
            } else {
                Connection.client?.send(JSONObject()
                        .put("type", "join_room")
                        .put("room_name", item.name).toString())
                /*
                Connection.client?.sendPairs(
                        Pair("type", "join_room"),
                        Pair("room_name", item.name)
                )
                */
            }
        }

        Connection.client?.addListener(object : MessageListener {
            override fun onMessage(message: String) {
                println("Recieved message: $message")
                val json = JSONObject(message)
                if (!json.has("type")) return
                val type = json.getString("type")
                if (type == "error") {
                    if (!json.has("message")) return
                    Snackbar.make(listView, json.getString("message"), 2500).show()
                } else if (type == "joined_room") {
                    if (!json.has("name", "users")) return
                    val room = Room(json.getString("name"), json.getJSONArray("users"))
                    ChatData.room = room
                    Connection.client?.removeListener(this)
                    val intent = Intent(this@ChatOverview, ChatActivity::class.java)
                    startActivity(intent)
                }
            }
        })

    }
}
