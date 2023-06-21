package com.tubes.foodtracker.fragments.arguments

import com.tubes.foodtracker.data.*
import java.io.Serializable

class SelectIngredientAmountFragmentArguments (val _ingredient : Ingredient,
                                               val _onSelected : (IngredientAmount)-> Unit = { _ -> }) : Serializable