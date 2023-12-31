package com.tubes.foodtracker.data

import android.content.Context
import android.content.res.Resources
import android.util.Log
import com.tubes.foodtracker.R
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception

object IngredientProvider {

    private var group: Group? = null
    private lateinit var ingredientsByIdent : Map<String,Ingredient>

    fun init(resources: Resources, context: Context){
        val ingredientsFileContent = getFileContent(resources, R.raw.data_ingredients)
        val groupsFileContent = getFileContent(resources, R.raw.data_groups)
        val ingredientSnackFileContent = getFileContent(resources, R.raw.data_ingredientsnacks)
        val standaloneSnackFileContent = getFileContent(resources, R.raw.data_standalonesnacks)
        val resourcesFileContent = getFileContent(resources, R.raw.data_resources)

        val serializedResources = Json.decodeFromString<SerializedResourcesList>(resourcesFileContent).resources
        val serializedIngredients = Json.decodeFromString<SerializedIngredientList>(ingredientsFileContent).ingredients
        val serializedGroups = Json.decodeFromString<SerializedGroupList>(groupsFileContent).groups

        Log.e("", "Decoding Resources")
        val resourcesFromIdent = serializedResources.associate { r -> r.ident to Resource.fromSerialized(r,context) }
        Log.e("", "Decoded Resources")
        val ingredientsToGroups = getMapOf(serializedIngredients,{ing -> ing.group},{ing->Ingredient.fromSerialized(ing,resourcesFromIdent)})
        ingredientsByIdent =
            ingredientsToGroups.values.fold(emptyList<Ingredient>()) { acc, ing -> acc + ing }.associateBy { it.ident }
        // snacks require ingredientsByIdent


        val groupsToHierarchy = getMapOf(serializedGroups,{grp -> grp.parent},{grp->grp})
        val convertedGroups = emptyMap<String,Group>().toMutableMap()
        val parentGroups = groupsToHierarchy[""]?.mapNotNull call@{
            val newGroup = getGroup(it, convertedGroups, ingredientsToGroups, groupsToHierarchy)
            if (newGroup != null) return@call Pair(it.ident, newGroup) else return@call null
        }?.toMap()?: emptyMap()

        group = parentGroups["ingredients"]
    }

    private fun getGroup (group: SerializedGroup,
                          convertedGroups: MutableMap<String, Group>,
                          allIngredients : Map<String,List<Ingredient>>,
                          groups: Map<String, List<SerializedGroup>>) : Group?
    {
        if(convertedGroups.containsKey(group.ident)){
            return convertedGroups[group.ident]
        }

        val subgroups =
            (if (groups.contains(group.ident)) groups[group.ident]!! else emptyList())
            .map{
            getGroup(it, convertedGroups, allIngredients, groups)
        }.filterNotNull().toTypedArray()

        val ingredients = allIngredients?.get(group.ident)?.toTypedArray()?: emptyArray()

        val convertedGroup = Group(group.resourceId, subgroups, ingredients)
        convertedGroups[group.ident] = convertedGroup
        return convertedGroup
    }

    private fun <T,T2> getMapOf(collection : Collection<T>, getKey: (T) -> String, getValue: (T) -> T2): Map<String,MutableList<T2>>{
        val map : MutableMap<String, MutableList<T2>> = emptyMap<String,MutableList<T2>>().toMutableMap()
        collection.forEach{obj ->
            val key = getKey(obj)
            val value = getValue(obj)
            if(!map.containsKey(key)){
                map[key] = mutableListOf(value)
            }
            else{
                map[key]?.add(value)
            }
        }
        return map.toMap()
    }

    private fun <T> mergeMaps(a:Map<String, MutableList<T>>, b:Map<String, MutableList<T>>) : Map<String, MutableList<T>>{
        val newList = a.toMutableMap()
        b.forEach{
            if(newList.containsKey(it.key)){
                newList[it.key]?.addAll(it.value)
            }
            else{
                newList[it.key] = it.value
            }
        }
        return newList.toMap()
    }

    private fun getFileContent(source:Resources, file : Int): String{
        return try{
            val stream = source.openRawResource(file)
            val isr = InputStreamReader(stream)
            val reader = BufferedReader(isr)
            val text = reader.readText()
            reader.close()
            isr.close()
            stream.close()
            text
        } catch(e:Exception){
            Log.e(null, e.toString())
            ""
        }

    }

    fun getIngredients(): Group {
        if(group == null){
            return Group("", emptyArray(), emptyArray())
        }
        return group!!
    }

    fun findIngredient(ident : String) : Ingredient?{
        return ingredientsByIdent[ident]
    }


    @Serializable
    data class SerializedIngredientList(val ingredients :List<SerializedIngredient> = emptyList())

    @Serializable
    data class SerializedGroupList(val groups : List<SerializedGroup> = emptyList())

    @Serializable
    data class SerializedResourcesList(val resources: List<SerializedResource> = emptyList())
}