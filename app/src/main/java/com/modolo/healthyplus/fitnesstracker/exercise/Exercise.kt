package com.modolo.healthyplus.fitnesstracker.exercise

import java.io.Serializable
/*semplice oggetto Exercise*/
data class Exercise(var name: String, var rep: Int, var set: Int, var kg: Int, var rec: Int) :
    Serializable