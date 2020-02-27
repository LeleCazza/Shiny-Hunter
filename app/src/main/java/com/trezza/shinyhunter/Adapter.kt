package com.trezza.shinyhunter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.daimajia.numberprogressbar.NumberProgressBar
import com.trezza.shinyhunter.MainActivity.Companion.listaTriplettePokemonCatturati
import kotlinx.android.synthetic.main.adapter_pokemon.view.*

class Adapter(private var context: Context,
              private var listaTriplettePokemon : List<List<Drawable>>,
              private var barraDiPercentualeShinyCatturati : NumberProgressBar,
              private var labelDiPercentualeShinyCatturati : TextView,
              private var larghezzaDisplay: Int) : BaseAdapter() {

    @SuppressLint("ViewHolder")
    override fun getView(posizioneTripletta: Int, convertView: View?, parent: ViewGroup?): View {
        val newView: View = LayoutInflater.from(context).inflate(R.layout.adapter_pokemon, parent, false)
        val tripletta = listaTriplettePokemon[posizioneTripletta]
        val immaginiPokemon = listOf(newView.immaginePokemon1,newView.immaginePokemon2,newView.immaginePokemon3)
        inizializzaShinyCatturati(immaginiPokemon, posizioneTripletta)
        eventoClickVeloce(immaginiPokemon, tripletta, posizioneTripletta)
        eventoClickLungo(immaginiPokemon, posizioneTripletta)
        setImmagine(immaginiPokemon, tripletta)
        setDimensioniImmagini(immaginiPokemon, tripletta)
        return newView
    }

    private fun inizializzaShinyCatturati(immaginiPokemon : List<ImageView>, posizioneTripletta: Int){
        for((i,immaginePokemon) in immaginiPokemon.withIndex())
            if(listaTriplettePokemonCatturati[posizioneTripletta][i] == 1)
                immaginePokemon.setBackgroundResource(R.drawable.catched)
    }

    private fun eventoClickVeloce(immaginiPokemon : List<ImageView>, tripletta: List<Drawable>, posizioneTripletta: Int){
        for((i,immaginePokemon) in immaginiPokemon.withIndex())
            clickVeloce(immaginePokemon,i, tripletta, posizioneTripletta)
    }

    private fun eventoClickLungo(immaginiPokemon : List<ImageView>, posizioneTripletta: Int){
        for((i,immaginePokemon) in immaginiPokemon.withIndex())
            clickLungo(immaginePokemon,i, posizioneTripletta)
    }

    private fun clickVeloce(pokemon : ImageView, posizionePokemon: Int, tripletta : List<Drawable>, posizioneTripletta: Int){
        pokemon.setOnClickListener {
            if(!isShinyCatturato(posizioneTripletta,posizionePokemon) && !isPosizioneVuota(tripletta, posizionePokemon)){
                incrementaShinyCatturati()
                catturaShiny(pokemon,posizioneTripletta,posizionePokemon)
            }
        }
    }

    private fun clickLungo(pokemon : ImageView, posizionePokemon: Int, posizioneTripletta: Int){
        pokemon.setOnLongClickListener{
            if(isShinyCatturato(posizioneTripletta,posizionePokemon)){
                decrementaShinyCatturati()
                liberaShiny(pokemon, posizioneTripletta, posizionePokemon)
            }
            true
        }
    }

    @SuppressLint("SetTextI18n")
    private fun incrementaShinyCatturati(){
        val shinyCatturati = ++barraDiPercentualeShinyCatturati.progress
        barraDiPercentualeShinyCatturati.progress = shinyCatturati
        labelDiPercentualeShinyCatturati.text = "Shiny: $shinyCatturati / ${barraDiPercentualeShinyCatturati.max}"
    }

    @SuppressLint("SetTextI18n")
    private fun decrementaShinyCatturati(){
        val shinyCatturati = --barraDiPercentualeShinyCatturati.progress
        barraDiPercentualeShinyCatturati.progress = shinyCatturati
        labelDiPercentualeShinyCatturati.text = "Shiny: $shinyCatturati / ${barraDiPercentualeShinyCatturati.max}"
    }

    private fun catturaShiny(pokemon : ImageView, posizioneTripletta : Int, posizionePokemon: Int ){
        listaTriplettePokemonCatturati[posizioneTripletta][posizionePokemon] = 1
        pokemon.setBackgroundResource(R.drawable.catched)
    }

    private fun liberaShiny(pokemon : ImageView, posizioneTripletta : Int, posizionePokemon: Int ){
        listaTriplettePokemonCatturati[posizioneTripletta][posizionePokemon] = 0
        pokemon.background = null
    }

    private fun isShinyCatturato(posizioneTripletta : Int, posizionePokemon: Int ) : Boolean{
        return listaTriplettePokemonCatturati[posizioneTripletta][posizionePokemon] == 1
    }

    private fun isPosizioneVuota(tripletta: List<Drawable>, posizionePokemon: Int) : Boolean{
        return tripletta[posizionePokemon].alpha == 0
    }

    private fun setImmagine(immaginiPokemon : List<ImageView>, tripletta : List<Drawable>){
        for((i,immaginePokemon) in immaginiPokemon.withIndex())
            immaginePokemon.setImageDrawable(tripletta[i])
    }

    private fun setDimensioniImmagini(immaginiPokemon : List<ImageView>, tripletta : List<Drawable>){
        var immaginiVuote = 0
        for(disegnoPokemon in tripletta)
            if(disegnoPokemon.alpha == 0)
                immaginiVuote++
        when (immaginiVuote) {
            1 -> mostraDueImmagini(immaginiPokemon)
            2 -> mostraUnImmagine(immaginiPokemon)
            else -> mostraTreImmagini(immaginiPokemon)
        }
    }

    private fun mostraUnImmagine(immaginiPokemon : List<ImageView>){
        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(larghezzaDisplay/3, larghezzaDisplay/3)
        params.leftMargin = larghezzaDisplay/3
        params.rightMargin = larghezzaDisplay/3
        immaginiPokemon[0].layoutParams = params
        immaginiPokemon[1].visibility = View.GONE
        immaginiPokemon[2].visibility = View.GONE
    }

    private fun mostraDueImmagini(immaginiPokemon : List<ImageView>){
        var params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(larghezzaDisplay/3, larghezzaDisplay/3)
        params.leftMargin = larghezzaDisplay/6
        immaginiPokemon[0].layoutParams = params
        params = LinearLayout.LayoutParams(larghezzaDisplay/3, larghezzaDisplay/3)
        immaginiPokemon[1].layoutParams = params
        immaginiPokemon[2].visibility = View.GONE
    }

    private fun mostraTreImmagini(immaginiPokemon: List<ImageView>){
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