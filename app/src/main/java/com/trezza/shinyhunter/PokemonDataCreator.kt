package com.trezza.shinyhunter

import android.app.Application
import android.graphics.drawable.Drawable
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

    fun getPokemonDrawable(posizioneTripletta: Int, posizionePokemon: Int) : Drawable{
        return listaTriplettePokemon[posizioneTripletta][posizionePokemon]
    }

    private fun creaDatiPokemon(){
        val immagineVuota = creaImmagineVuota()
        var posizione = 0
        var posizionePokemonPrecedente = 0
        var ultimoValorePrecedente = ""
        var ultimoValore = ""
        for (pokemon in listaNomiImmaginiPokemon){
            val posizionePokemon = pokemon.split("_")[1].toInt()
            ultimoValore = pokemon.split("_")[2]
            if(posizione == 3){
                aggiungiTriplettaAListaTriplette()
                creaNuovaTripletta()
                posizionePokemonPrecedente = 0
                posizione = 0
            }
            if(posizionePokemonPrecedente < posizionePokemon){
                aggiungiPokemonATripletta(pokemon,ultimoValore)
                ultimoValorePrecedente = ultimoValore
                posizionePokemonPrecedente = posizionePokemon
                posizione++
            }
            else{
                aggiungiImmaginiVuoteATripletta(immagineVuota, posizione, ultimoValorePrecedente)
                aggiungiTriplettaAListaTriplette()
                creaNuovaTripletta()
                aggiungiPokemonATripletta(pokemon, ultimoValore)
                ultimoValorePrecedente = ultimoValore
                posizionePokemonPrecedente = posizionePokemon
                posizione = 1
            }
        }
        concludiUltimaTripletta(immagineVuota,posizione, ultimoValore)
    }

    fun creaImmagineVuota() : Drawable{
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

    private fun aggiungiPokemonATripletta(file : String, ultimoValore: String){
        triplettaPokemon.add(Drawable.createFromStream(application.assets.open("pokemon/$file"),null))
        if(isNuovoPokemon(ultimoValore))
            triplettaPokemonCatturati.add(-1)
        else
            triplettaPokemonCatturati.add(0)
    }

    private fun aggiungiImmaginiVuoteATripletta(immagineVuota : Drawable, posizione : Int, ultimoValore: String){
        repeat(3 - posizione){
            triplettaPokemon.add(immagineVuota)
            if(isNuovoPokemon(ultimoValore))
                triplettaPokemonCatturati.add(-1)
            else
                triplettaPokemonCatturati.add(0)
        }
    }

    private fun concludiUltimaTripletta(immagineVuota : Drawable, posizione : Int, ultimoValore: String){
        if(triplettaPokemon.size < 3){
            aggiungiImmaginiVuoteATripletta(immagineVuota,posizione, ultimoValore)
            aggiungiTriplettaAListaTriplette()
        }
    }

    private fun isNuovoPokemon(ultimoValore : String) : Boolean{
        return ultimoValore == "N.png"
    }
}