package com.modolo.healthyplus.login

import android.graphics.Color
import com.modolo.healthyplus.R

data class LoginCard(var title: String, var description: String, var titleColor: Int, var drawableID: Int){
    constructor(title: String):this(title, "", Color.BLACK, R.drawable.card_healthyplus)
}
