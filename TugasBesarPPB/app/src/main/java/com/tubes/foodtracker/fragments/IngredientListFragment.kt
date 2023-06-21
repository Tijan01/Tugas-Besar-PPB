package com.tubes.foodtracker.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.tubes.foodtracker.R
import com.tubes.foodtracker.views.GroupTileView
import com.tubes.foodtracker.views.IngredientTileView
import com.tubes.foodtracker.data.Group
import com.tubes.foodtracker.data.IngredientAmount

import com.tubes.foodtracker.databinding.FragmentIngredientlistBinding
import com.tubes.foodtracker.fragments.arguments.IngredientListFragmentArguments

class IngredientListFragment : Fragment() {
    private var _binding: FragmentIngredientlistBinding? = null

    private val binding get() = _binding!!
    private var _group = Group("Invalid Group", emptyArray(), emptyArray())
    private var _onIngredientSelected : ((IngredientAmount) -> Unit)? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIngredientlistBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchArguments()
        instantiateObjects(view)
    }

    private fun fetchArguments(){
        val args = arguments?.get("ContainerGroup")
        if (args is IngredientListFragmentArguments){
            _group = args._group
            _onIngredientSelected = args._onIngredientAmountSelected
        }
        else {
            Log.e(null,"Cannot fetch arguments for IngredientListFragment")
        }
    }
    private fun instantiateObjects(view: View){
        val ingredientList = binding.ingredientlist
        for(sub in _group.subGroups) {
            if(sub.isEmpty(_onIngredientSelected == null)){
                continue
            }
            val groupView = GroupTileView(view.context, sub, ::onGroupClicked)
            ingredientList.addView(groupView)
        }
        val onIngredientSelected = _onIngredientSelected
        if(onIngredientSelected != null){
            for(ing in _group.ingredients) {
                val ingredientView = IngredientTileView(view.context, ing, onIngredientSelected)
                ingredientList.addView(ingredientView)
            }
        }
    }

    private fun onGroupClicked(group: Group){
        val bundle = bundleOf("ContainerGroup" to IngredientListFragmentArguments(group,_onIngredientSelected,))
        findNavController().navigate(R.id.action_IngredientList_to_IngredientList, bundle)
    }
}