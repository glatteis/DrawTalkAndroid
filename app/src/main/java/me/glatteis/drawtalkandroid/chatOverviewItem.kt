package me.glatteis.drawtalkandroid

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import java.util.*

/**
 * Created by Linus on 31.10.2016!
 */
class ChatOverviewItem(val name: String, val hasPassword: Boolean, val numUsers: Int)

class ChatOverviewItemAdapter(context: Context, items: ArrayList<ChatOverviewItem>) : ArrayAdapter<ChatOverviewItem>(context, 0, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val item = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.fragment_chat_overview_list_item, parent, false)

        val title = view.findViewById(R.id.itemTitle) as TextView
        val locked = view.findViewById(R.id.locked) as ImageView
        val users = view.findViewById(R.id.itemUsers) as TextView

        title.text = item.name
        if (item.hasPassword) {
            locked.setImageResource(R.drawable.ic_lock_outline_black_24dp)
        } else {
            locked.setImageResource(R.drawable.ic_lock_open_black_24dp)
        }
        users.text = "${item.numUsers} participating"

        return view
    }
}