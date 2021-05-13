package com.modolo.healthyplus.login

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.islamkhsh.CardSliderAdapter
import com.modolo.healthyplus.R

/*classico adapter che ricever una lista di "LoginCards" per la schermata di login e le mette nel layout*/
class LoginCardsAdapter(private val cardList: ArrayList<LoginCard>) :
        CardSliderAdapter<LoginCardsAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.title)
        val image: ImageView = itemView.findViewById(R.id.cardImage)
        val desc: TextView = itemView.findViewById(R.id.cardDescription)
    }

    override fun getItemCount() = cardList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
                LayoutInflater.from(parent.context).inflate(R.layout.login_item_card, parent, false)
        return ViewHolder(view)
    }

    override fun bindVH(holder: ViewHolder, position: Int) {
        val card = cardList[position]
        with(holder) {
            text.text = card.title
            text.setTextColor(card.titleColor)
            image.setBackgroundResource(card.drawableID)
            desc.text = card.description
        }
    }
}
