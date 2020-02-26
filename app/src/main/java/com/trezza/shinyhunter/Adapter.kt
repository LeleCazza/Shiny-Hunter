package com.trezza.shinyhunter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.trezza.shinyhunter.MainActivity.Companion.catchedPokemon
import com.trezza.shinyhunter.MainActivity.Companion.contator
import com.trezza.shinyhunter.MainActivity.Companion.pokemonTot
import com.trezza.shinyhunter.MainActivity.Companion.progress
import com.trezza.shinyhunter.MainActivity.Companion.shinyChatched
import kotlinx.android.synthetic.main.adapter_pokemon.view.*

class Adapter(private var context: Context,
              private var imagesPokemonList : List<List<Drawable>>,
              private var larghezzaDisplay: Int) : BaseAdapter() {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val newView: View = LayoutInflater.from(context).inflate(R.layout.adapter_pokemon, parent, false)

        val pokemon1 = newView.immaginePokemon1
        val pokemon2 = newView.immaginePokemon2
        val pokemon3 = newView.immaginePokemon3
        val item = imagesPokemonList[position]

        setDrawable(pokemon1,pokemon2,pokemon3,item)
        setGrandezzaImmagine(pokemon1,pokemon2,pokemon3)
        eventiClickVeloce(pokemon1,pokemon2,pokemon3,position,item)
        eventiClickLunghi(pokemon1, pokemon2, pokemon3, position)

        if(catchedPokemon[position][0] == 1)
            pokemon1.setBackgroundResource(R.drawable.catched)
        if(catchedPokemon[position][1] == 1)
            pokemon2.setBackgroundResource(R.drawable.catched)
        if(catchedPokemon[position][2] == 1)
            pokemon3.setBackgroundResource(R.drawable.catched)

        return newView
    }

    private fun setDrawable(pokemon1 : ImageView, pokemon2 : ImageView, pokemon3 : ImageView, tripletta : List<Drawable>){
        pokemon1.setImageDrawable(tripletta[0])
        pokemon2.setImageDrawable(tripletta[1])
        pokemon3.setImageDrawable(tripletta[2])
    }

    private fun setGrandezzaImmagine(pokemon1 : ImageView, pokemon2 : ImageView, pokemon3 : ImageView){
        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, larghezzaDisplay/3)
        params.width = larghezzaDisplay/3
        pokemon1.layoutParams = params
        pokemon2.layoutParams = params
        pokemon3.layoutParams = params
    }

    private fun eventiClickVeloce(pokemon1 : ImageView, pokemon2 : ImageView, pokemon3 : ImageView, position: Int, tripletta : List<Drawable>){
        clickVeloce(pokemon1,position,0, tripletta[0].alpha)
        clickVeloce(pokemon2,position,1, tripletta[1].alpha)
        clickVeloce(pokemon3,position,2, tripletta[2].alpha)
    }

    private fun clickVeloce(pokemon : ImageView, positionTripletta: Int, positionPokemon: Int, alpha : Int){
        pokemon.setOnClickListener {
            if(alpha != 0){
                if(catchedPokemon[positionTripletta][positionPokemon] == 0){
                    progress.progress = ++shinyChatched
                    contator.text = "Shiny: $shinyChatched / $pokemonTot"
                }
                catchedPokemon[positionTripletta][positionPokemon] = 1
                pokemon.setBackgroundResource(R.drawable.catched)
            }
        }
    }

    private fun eventiClickLunghi(pokemon1 : ImageView, pokemon2 : ImageView, pokemon3 : ImageView, position: Int){
        clickLungo(pokemon1,position,0)
        clickLungo(pokemon2,position,1)
        clickLungo(pokemon3,position,2)
    }

    private fun clickLungo(pokemon : ImageView, positionTripletta: Int, positionPokemon: Int){
        pokemon.setOnLongClickListener{
            if(catchedPokemon[positionTripletta][positionPokemon] == 1){
                progress.progress = --shinyChatched
                contator.text = "Shiny: $shinyChatched / $pokemonTot"
            }
            pokemon.background = null
            catchedPokemon[positionTripletta][positionPokemon] = 0
            true
        }
    }

    override fun getItem(position: Int): Any{
        return imagesPokemonList[position]
    }

    override fun getItemId(position: Int): Long {
        return imagesPokemonList.indexOf(imagesPokemonList[position]).toLong()
    }

    override fun getCount(): Int{
        return imagesPokemonList.size
    }
}