package com.trezza.shinyhunter

import android.app.Application
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContextCompat

class PokemonDataCreator(private var listaNomiImmaginiPokemon : Array<String>, private var application: Application){

    private var listaTriplettePokemon: MutableList<MutableList<Drawable>> = mutableListOf()
    private var triplettaPokemon: MutableList<Drawable> = mutableListOf()
    private var listaTriplettePokemonCatturati: MutableList<MutableList<Int>> = mutableListOf()
    private var triplettaPokemonCatturati: MutableList<Int> = mutableListOf()

    init{ creaDatiPokemon() }

    fun getListaTriplettePokemon() : MutableList<MutableList<Drawable>> {
        return listaTriplettePokemon
    }

    fun getListaPokemonCatturati() : MutableList<MutableList<Int>>{
        return listaTriplettePokemonCatturati
    }

    private fun creaDatiPokemon(){
        val immagineVuota = creaImmagineVuota()
        var posizione = 0
        var posizionePokemonPrecedente = 0
        for (pokemon in listaNomiImmaginiPokemon){
            val posizionePokemon = pokemon.split("_")[1].toInt()
            if(posizione == 3){
                aggiungiTriplettaAListaTriplette()
                creaNuovaTripletta()
                posizionePokemonPrecedente = 0
                posizione = 0
            }
            if(posizionePokemonPrecedente < posizionePokemon){
                aggiungiPokemonATripletta(pokemon)
                posizionePokemonPrecedente = posizionePokemon
                posizione++
            }
            else{
                aggiungiImmaginiVuoteATripletta(immagineVuota, posizione)
                aggiungiTriplettaAListaTriplette()
                creaNuovaTripletta()
                aggiungiPokemonATripletta(pokemon)
                posizionePokemonPrecedente = posizionePokemon
                posizione = 1
            }
        }
    }

    private fun creaImmagineVuota() : Drawable{
        val immagineVuota = ContextCompat.getDrawable(application.baseContext,R.drawable.vuoto)!!
        immagineVuota.alpha = 0
        return immagineVuota
    }

    private fun aggiungiTriplettaAListaTriplette(){
        listaTriplettePokemon.add(triplettaPokemon)
        listaTriplettePokemonCatturati.add(triplettaPokemonCatturati)
    }

    private fun creaNuovaTripletta(){
        triplettaPokemon = mutableListOf()
        triplettaPokemonCatturati = mutableListOf()
    }

    private fun aggiungiPokemonATripletta(file : String){
        triplettaPokemon.add(Drawable.createFromStream(application.assets.open("pokemon/$file"),null))
        triplettaPokemonCatturati.add(0)
    }

    private fun aggiungiImmaginiVuoteATripletta(immagineVuota : Drawable, sizeTripletta : Int){
        repeat(3 - sizeTripletta){
            triplettaPokemon.add(immagineVuota)
            triplettaPokemonCatturati.add(0)
        }
    }
}