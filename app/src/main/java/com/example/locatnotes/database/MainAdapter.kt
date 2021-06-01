package com.example.locatnotes.database

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.locatnotes.databinding.LocatNotesViewBinding
import com.example.locatnotes.models.LocatNotesModel

class MainAdapter(val context: Context, val items: ArrayList<LocatNotesModel>) : RecyclerView.Adapter<MainAdapter.LocatNotesViewHolder>() {

    private var onClickListener: OnClickListener? = null

    class LocatNotesViewHolder(private val locatNotesViewBinding: LocatNotesViewBinding) : RecyclerView.ViewHolder(locatNotesViewBinding.root) {
        fun bind(locatNotesModel: LocatNotesModel) {
            locatNotesViewBinding.tvTitle.text = locatNotesModel.title
            locatNotesViewBinding.tvDescription.text = locatNotesModel.description
            locatNotesViewBinding.civImage.setImageURI(Uri.parse(locatNotesModel.imageUri))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocatNotesViewHolder {
        val itemBinding = LocatNotesViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return LocatNotesViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: LocatNotesViewHolder, position: Int) {
        val locNotes = items[position]
        holder.bind(locNotes)

        holder.itemView.setOnClickListener {
            if(onClickListener != null) {
                onClickListener!!.onClick(position, locNotes)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, model : LocatNotesModel)
    }
}