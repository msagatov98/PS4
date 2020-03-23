package com.example.ps4.util

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.client_list_item.view.*

@GlideModule
class CustomGlideModule : AppGlideModule()

class ClientViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var clientImage = itemView.client_image
    var clientFullName = itemView.client_fullname1
    var clientId = itemView.client_id1
    var clientDelete = itemView.ic_delete_client
    var clientEdit = itemView.ic_edit_client
}

class TopSpacingItemDecoration(private val padding: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = padding/2
        outRect.right = padding/2
        outRect.left = padding/2
        outRect.top = padding/2
    }
}

class ValueEventListenerAdapter(val handler: (DataSnapshot) -> Unit) : ValueEventListener {
    override fun onDataChange(p0: DataSnapshot) {
        handler(p0)
    }

    override fun onCancelled(p0: DatabaseError) {
        Log.e("EditClientActivity", "onCancelled", p0.toException())
    }
}