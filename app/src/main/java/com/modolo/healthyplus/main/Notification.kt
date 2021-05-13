package com.modolo.healthyplus.main

import android.graphics.Color

data class Notification(var title: String, var date: String, var description: String, var color: Int) {
    constructor(title: String, date: String) : this(title, date, "", Color.BLACK)
}