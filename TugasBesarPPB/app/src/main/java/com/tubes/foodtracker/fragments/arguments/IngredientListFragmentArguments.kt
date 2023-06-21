package com.tubes.foodtracker.fragments.arguments

import com.tubes.foodtracker.data.Group
import com.tubes.foodtracker.data.IngredientAmount
import java.io.Serializable

class IngredientListFragmentArguments (val _group : Group,
                                       val _onIngredientAmountSelected : ((IngredientAmount) -> Unit)? = null) : Serializable