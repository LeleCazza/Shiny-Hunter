package com.trezza.shinyhunter

import android.graphics.drawable.Drawable

class ShinyDataCreator(private var listaTriplettePokemonCatturati: MutableList<MutableList<Int>> = mutableListOf(),
                       private var pokemonDataCreator: PokemonDataCreator) {

    private var listaPokemon: MutableList<MutableList<Drawable>> = mutableListOf()
    private var triplettaPokemon: MutableList<Drawable> = mutableListOf()
    private var posizione = 0

    fun getListaSoloShinyCatturati() : MutableList<MutableList<Drawable>>{
        creaListaSoloShinyCatturati()
        return listaPokemon
    }

    fun getListaSoloShinyMancanti() : MutableList<MutableList<Drawable>>{
        creaListaSoloShinyMancanti()
        return listaPokemon
    }

    private fun creaListaSoloShinyCatturati(){
        for((i,tripletta) in listaTriplettePokemonCatturati.withIndex()){
            if(tripletta[0] == 1 || tripletta[1] == 1 || tripletta[2] == 1){
                if(isTriplettaFull())
                    aggiungiTriplettaAListaTriplette()
                var posizionePokemon = 0
                repeat(3){
                    if(!isTriplettaFull()) {
                        if (isShinyCatturato(tripletta[posizionePokemon]))
                            aggiungiATripletta(i, posizionePokemon)
                    }
                    else{
                        aggiungiTriplettaAListaTriplette()
                        if (isShinyCatturato(tripletta[posizionePokemon]))
                            aggiungiPrimoElementoATripletta(i,posizionePokemon)
                    }
                    posizionePokemon++
                }
            }
        }
        concludiTripletta()
    }

    private fun creaListaSoloShinyMancanti() {
        for((i,tripletta) in listaTriplettePokemonCatturati.withIndex()){
            if(tripletta[0] == 0 || tripletta[1] == 0 || tripletta[2] == 0){
                if(isTriplettaFull())
                    aggiungiTriplettaAListaTriplette()
                var posizionePokemon = 0
                repeat(3){
                    if(!isTriplettaFull()) {
                        if (!isShinyCatturato(tripletta[posizionePokemon]) && !isImmagineVuota(i,posizionePokemon))
                            aggiungiATripletta(i, posizionePokemon)
                    }
                    else{
                        aggiungiTriplettaAListaTriplette()
                        if (!isShinyCatturato(tripletta[posizionePokemon])  && !isImmagineVuota(i,posizionePokemon))
                            aggiungiPrimoElementoATripletta(i,posizionePokemon)
                    }
                    posizionePokemon++
                }
            }
        }
        concludiTripletta()
    }

    private fun aggiungiPrimoElementoATripletta(posizioneTripletta : Int, posizionePokemon : Int){
        triplettaPokemon.add(pokemonDataCreator.getPokemonDrawable(posizioneTripletta,posizionePokemon))
        posizione = 1
    }
    private fun aggiungiATripletta(posizioneTripletta : Int, posizionePokemon : Int){
        triplettaPokemon.add(pokemonDataCreator.getPokemonDrawable(posizioneTripletta,posizionePokemon))
        posizione++
    }

    private fun aggiungiTriplettaAListaTriplette(){
        listaPokemon.add(triplettaPokemon)
        triplettaPokemon = mutableListOf()
        posizione = 0
    }

    private fun concludiTripletta(){
        repeat(3 - posizione){
            triplettaPokemon.add(pokemonDataCreator.creaImmagineVuota())
        }
        listaPokemon.add(triplettaPokemon)
    }

    private fun isImmagineVuota(posizioneTripletta : Int, posizionePokemon : Int) : Boolean{
        return pokemonDataCreator.getPokemonDrawable(posizioneTripletta, posizionePokemon).alpha == 0
    }

    private fun isTriplettaFull() : Boolean{
        return posizione == 3
    }

    private fun isShinyCatturato(segnaposto : Int) : Boolean{
        return segnaposto == 1
    }
}