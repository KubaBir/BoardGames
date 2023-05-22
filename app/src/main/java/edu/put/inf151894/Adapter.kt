package edu.put.inf151894

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import org.xml.sax.InputSource
import java.net.URL
import javax.xml.parsers.SAXParserFactory


class Adapter(var context: Context, var items: List<Game>) :
    RecyclerView.Adapter<MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.list_element, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.nameView.text = items[position].title
        holder.releaseView.text = items[position].released.toString()
        //Log.d("img", "a")
        holder.imageView.load(items[position].image)

    }


    override fun getItemCount(): Int {
        return items.size
    }
}