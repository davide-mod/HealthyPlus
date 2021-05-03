package com.modolo.healthyplus.login

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.islamkhsh.CardSliderAdapter
import com.modolo.healthyplus.R

class LoginCardsAdapter(private val listaStringhe: ArrayList<String>) :
        CardSliderAdapter<LoginCardsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.text)
    }

    override fun getItemCount() = listaStringhe.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
                LayoutInflater.from(parent.context).inflate(R.layout.login_item_card, parent, false)
        return ViewHolder(view)
    }

    override fun bindVH(holder: ViewHolder, position: Int) {
        val testo = listaStringhe[position]
        with(holder) {
            text.text = testo
        }
    }
}
