package com.trezza.shinyhunter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.trezza.shinyhunter.R
import kotlinx.android.synthetic.main.adapter_pokemon.view.*

class AdapterShiny(private var context: Context,
                   private var listaTriplettePokemon : List<List<Drawable>>,
                   private var larghezzaDisplay: Int,
                   private var mostraSoloShinyCatturati : Boolean,
                   private var mostraSoloShinyMancanti: Boolean) : BaseAdapter() {

    @SuppressLint("ViewHolder")
    override fun getView(posizioneTripletta: Int, convertView: View?, parent: ViewGroup?): View {
        val newView: View = LayoutInflater.from(context).inflate(R.layout.adapter_pokemon, parent, false)
        val immaginiPokemon = listOf(newView.immaginePokemon1,newView.immaginePokemon2,newView.immaginePokemon3)
        val tripletta = listaTriplettePokemon[posizioneTripletta]
        setImmagini(immaginiPokemon, tripletta)
        setDimensioniImmagini(immaginiPokemon)
        return newView
    }

    private fun setImmagini(immaginiPokemon : List<ImageView>, tripletta : List<Drawable>){
        immaginiPokemon[0].setImageDrawable(tripletta[0])
        if(tripletta[0].alpha != 0 && mostraSoloShinyCatturati && !mostraSoloShinyMancanti)
            immaginiPokemon[0].setBackgroundResource(R.drawable.catched)
        immaginiPokemon[1].setImageDrawable(tripletta[1])
        if(tripletta[1].alpha != 0 && mostraSoloShinyCatturati && !mostraSoloShinyMancanti)
            immaginiPokemon[1].setBackgroundResource(R.drawable.catched)
        immaginiPokemon[2].setImageDrawable(tripletta[2])
        if(tripletta[2].alpha != 0 && mostraSoloShinyCatturati && !mostraSoloShinyMancanti)
            immaginiPokemon[2].setBackgroundResource(R.drawable.catched)
    }

    private fun setDimensioniImmagini(immaginiPokemon: List<ImageView>){
        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(larghezzaDisplay/3, larghezzaDisplay/3)
        immaginiPokemon[0].layoutParams = params
        immaginiPokemon[1].layoutParams = params
        immaginiPokemon[2].layoutParams = params
    }

    override fun getItem(position: Int): Any{
        return listaTriplettePokemon[position]
    }

    override fun getItemId(position: Int): Long {
        return listaTriplettePokemon.indexOf(listaTriplettePokemon[position]).toLong()
    }

    override fun getCount(): Int{
        return listaTriplettePokemon.size
    }
}