package com.tubes.foodtracker.data

import kotlinx.serialization.Serializable

@Serializable
data class NutritionalValues (val Calories : Float,
                              val Carbs : Float,
                              val Protein: Float,
                              val Fat: Float) {
    operator fun plus (b : NutritionalValues) : NutritionalValues =
        NutritionalValues(Calories + b.Calories,
            Carbs + b.Carbs,
            Protein + b.Protein,
            Fat + b.Fat)
    operator fun times (b: Float) : NutritionalValues =
        NutritionalValues(Calories * b,
            Carbs * b,
            Protein * b,
            Fat * b)
    companion object{
        val empty get() = NutritionalValues(0f,0f,0f,0f)
        val recommended get() = NutritionalValues(2000f, 325f, 75f, 44f)
    }
}