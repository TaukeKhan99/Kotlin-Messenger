package com.messenger.kotlinmessenger.views

import com.messenger.kotlinmessenger.R
import com.messenger.kotlinmessenger.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatToItem(val text: String, val user: User): Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row.text = text

        val uri = user.profileImageurl
        val targetImageView = viewHolder.itemView.imageview_from_row
        Picasso.get().load(uri).into(targetImageView)
    }

}
class ChatFromItem(val text: String, val user: User): Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text = text

        val uri = user.profileImageurl
        val targetImageView = viewHolder.itemView.imageview_to_row
        Picasso.get().load(uri).into(targetImageView)
    }

}