package edu.put.inf151894

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    //var imageView: ImageView
    var nameView: TextView
    var releaseView: TextView

    init {
        //imageView = itemView.findViewById<ImageView>(R.id.imageView)
        nameView = itemView.findViewById<TextView>(R.id.name)
        releaseView = itemView.findViewById<TextView>(R.id.release)
    }


}