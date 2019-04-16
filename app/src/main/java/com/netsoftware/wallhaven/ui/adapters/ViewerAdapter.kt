package com.netsoftware.wallhaven.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.netsoftware.wallhaven.R
//
//class ViewerAdapter(val onClick: (Note) -> Unit) : RecyclerView.Adapter<ViewerAdapter.ViewerItemViewHolder>() {
//    var items: List<Note> = emptyList()
//
//    fun loadItems(newItems: List<Note>) {
//        items = newItems
//    }
//
//    override fun getItemCount(): Int = items.size
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewerItemViewHolder
//            = ViewerItemViewHolder(
//        LayoutInflater.from(parent.context)
//        .inflate(R.layout.note_list_item, parent, false))
//
//
//    override fun onBindViewHolder(holder: ViewerItemViewHolder, position: Int) {
//        holder.note = items[position]
//        holder.view.setOnClickListener { onClick(items[position]) }
//    }
//
//    class ViewerItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
//        var note: Note? = null
//            set(value) {
//                field = value
//                view.list_item_id.text = value?.noteId
//                view.list_item_title.text = value?.title
//            }
//    }
//}

