package com.tubes.foodtracker.data

import kotlinx.serialization.Serializable
import java.time.LocalDate

class EatingDay (val date : LocalDate = LocalDate.now()) : CaloricIntake {

        val _meals = MutableList<CaloricIntake>(0) { Meal("")}

    override val displayName: String
        get() = date.toString()
    override val intakeValues : NutritionalValues
        get() = _meals.fold(NutritionalValues.empty){ acc, new -> acc + new.intakeValues}
    val asSerialized : SerializedEatingDay
        get() {
            val meals = MutableList(0){Meal("")}
            val customMeals = MutableList(0){ CustomMeal.empty}

            _meals.forEach{
                when (it){
                    is Meal -> meals.add(it)
                    is CustomMeal -> customMeals.add(it)
                }
            }
            return SerializedEatingDay(
                date.year,
                date.monthValue,
                date.dayOfMonth,
                meals.map{it.asSerialized},
                customMeals,
            )
        }
    val asThumb : ThumbEatingDay get() = ThumbEatingDay(date.year, date.monthValue, date.dayOfMonth,_meals.fold(NutritionalValues.empty){acc, new -> acc + new.intakeValues})
}

@Serializable
class SerializedEatingDay(val year : Int,
                          val month: Int,
                          val day : Int,
                          val meals: List<SerializedMeal> = emptyList(),
                          val customMeal: List<CustomMeal> = emptyList(),
){
    val deSerialize : EatingDay get() {
        val day = EatingDay(LocalDate.of(year, month, day))
        meals.forEach {day._meals.add(it.deserialize)}
        customMeal.forEach {day._meals.add(it)}
        return day
    }
}

@Serializable
class ThumbEatingDay(var year : Int, val month: Int, val day: Int, val nutrition : NutritionalValues)
{

}