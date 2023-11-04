package com.telent.t_player.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.telent.t_player.R
import com.telent.t_player.activities.PlayerActivity
import com.telent.t_player.model.VideoModel

class VideoRecyclerAdapter(
    private val activity: Activity,
    private val itemList: ArrayList<VideoModel>
) :
    RecyclerView.Adapter<VideoRecyclerAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val itemName: TextView = itemView.findViewById(R.id.txt_Title)
        val itemImage: ImageView = itemView.findViewById(R.id.img_thumb)
        val itemCount: TextView = itemView.findViewById(R.id.txt_duration)
        val cView: androidx.cardview.widget.CardView = itemView.findViewById(R.id.cardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.sing_row_video_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pic = itemList[position]
        holder.itemName.text = pic.resName
        holder.itemCount.text = pic.resId

        Glide.with(activity).load(pic.resUri).error(R.drawable.ic_baseline_music_video_24)
            .into(holder.itemImage)

        holder.cView.setOnClickListener {
            val intent = Intent(activity, PlayerActivity::class.java)
            intent.putExtra("videoUri", pic.resUri)
            intent.putExtra("videoName", pic.resName)
            intent.putExtra("videoWidth", pic.resWidth)
            intent.putExtra("currentFolder", pic.folderName)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity.startActivity(intent)

        }


    }

    override fun getItemCount(): Int {
        return itemList.size

    }

}
