package edu.put.inf151894

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import org.xml.sax.InputSource
import java.net.URL
import javax.xml.parsers.SAXParserFactory


class Adapter(var context: Context, var items: List<Game>) :
    RecyclerView.Adapter<Adapter.MyViewHolder>() {

    private var onClickListener: OnClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.list_element, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.nameView.text = items[position].title
        holder.releaseView.text = items[position].released.toString()
        holder.imageView.load(items[position].image)

        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, items[position] )
            }
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }
    interface OnClickListener {
        fun onClick(position: Int, model: Game)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView
        var nameView: TextView
        var releaseView: TextView

        init {
            imageView = itemView.findViewById<ImageView>(R.id.image)
            nameView = itemView.findViewById<TextView>(R.id.name)
            releaseView = itemView.findViewById<TextView>(R.id.release)
        }
    }
}