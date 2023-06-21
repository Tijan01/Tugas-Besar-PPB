package com.tubes.foodtracker.fragments.arguments

import com.tubes.foodtracker.data.CustomMeal
import java.io.Serializable

class CustomMealFragmentArguments (
    val _meal : CustomMeal?,
    val _onFinished : (CustomMeal) -> Unit = {}) : Serializable