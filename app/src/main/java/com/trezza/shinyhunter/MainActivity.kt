package com.trezza.shinyhunter

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
import android.widget.TextView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.gms.ads.MobileAds
import com.daimajia.numberprogressbar.NumberProgressBar

open class MainActivity : AppCompatActivity() {

    companion object{
        lateinit var listaTriplettePokemonCatturati : MutableList<MutableList<Int>>
    }

    private var totaleNumeroDiPokemonShiny = 0
    private lateinit var pokemonDataCreator : PokemonDataCreator
    private lateinit var barraDiPercentualeShinyCatturati : NumberProgressBar
    private lateinit var labelDiPercentualeShinyCatturati : TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var bannerPubblicita : AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        pokemonDataCreator = PokemonDataCreator(this.assets.list("pokemon")!!,this.application)
    }

    override fun onStart() {
        super.onStart()
        inizializzaComponenti()
        caricaListaPokemonShinyCatturati(pokemonDataCreator)
        creaAdapter(pokemonDataCreator)
        caricaBanner()
    }

    private fun inizializzaComponenti(){
        totaleNumeroDiPokemonShiny = this.assets.list("pokemon")!!.size
        aggiornaLabelDiPercentualeShinyCatturati(0)
        aggiornaBarraDiPercentualeShinyCatturati(0)
    }

    @SuppressLint("SetTextI18n")
    private fun aggiornaLabelDiPercentualeShinyCatturati(shinyCatturati : Int){
        labelDiPercentualeShinyCatturati = findViewById(R.id.contatore)
        labelDiPercentualeShinyCatturati.text = "Shiny: $shinyCatturati / $totaleNumeroDiPokemonShiny"
    }

    private fun aggiornaBarraDiPercentualeShinyCatturati(shinyCatturati : Int){
        barraDiPercentualeShinyCatturati = findViewById(R.id.progressBar)
        barraDiPercentualeShinyCatturati.max = totaleNumeroDiPokemonShiny
        barraDiPercentualeShinyCatturati.progress = shinyCatturati
    }

    private fun creaAdapter(pokemonDataCreator : PokemonDataCreator){
        listView.apply {
            adapter = Adapter(
                context,
                pokemonDataCreator.getListaTriplettePokemon(),
                barraDiPercentualeShinyCatturati,
                labelDiPercentualeShinyCatturati,
                getLarghezzaDisplay()
            )
        }
    }

    private fun getLarghezzaDisplay() : Int{
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    private fun caricaListaPokemonShinyCatturati(pokemonDataCreator: PokemonDataCreator){
        val stringaDiSalvataggio = sharedPreferences.getString("LISTA_CATTURATI", null)
        listaTriplettePokemonCatturati = pokemonDataCreator.getListaPokemonCatturati()
        for ((i,x) in listaTriplettePokemonCatturati.withIndex()){
            Log.i("TRIPLETTA $i:", x[0].toString() + " , " + x[1].toString() + " , " + x[2].toString())
        }
        if(stringaDiSalvataggio != null)
            caricaListaTriplettePokemonCatturati(stringaDiSalvataggio)
    }

    @SuppressLint("SetTextI18n")
    private fun caricaListaTriplettePokemonCatturati(stringaDiSalvataggio : String){
        val triplette = stringaDiSalvataggio.split("|")
        var numeroShinyCatturati = 0
        var i = 0
        for (tripletta in triplette){
            if(primoUtilizzo(triplette) && isNuovaTripletta(i)){
                Log.i("SGAMAAAATO", "SONO ENTRATO ERROREEE!")
                listaTriplettePokemonCatturati[i][0] = 0
                listaTriplettePokemonCatturati[i][1] = 0
                listaTriplettePokemonCatturati[i][2] = 0
                i++
            }
            val pokemon = tripletta.split(";")
            listaTriplettePokemonCatturati[i][0] = pokemon[0].toInt()
            listaTriplettePokemonCatturati[i][1] = pokemon[1].toInt()
            listaTriplettePokemonCatturati[i][2] = pokemon[2].toInt()
            if(pokemon[0].toInt() == 1)
                numeroShinyCatturati++
            if(pokemon[1].toInt() == 1)
                numeroShinyCatturati++
            if(pokemon[2].toInt() == 1)
                numeroShinyCatturati++
            i++
        }
        aggiornaLabelDiPercentualeShinyCatturati(numeroShinyCatturati)
        aggiornaBarraDiPercentualeShinyCatturati(numeroShinyCatturati)
    }

    private fun isNuovaTripletta(posizioneTripletta : Int) : Boolean {
        return  listaTriplettePokemonCatturati[posizioneTripletta][0] == -1 &&
                listaTriplettePokemonCatturati[posizioneTripletta][1] == -1 &&
                listaTriplettePokemonCatturati[posizioneTripletta][2] == -1
    }

    private fun primoUtilizzo(triplette : List<String>) : Boolean{
        return triplette.size != listaTriplettePokemonCatturati.size
    }

    private fun caricaBanner(){
        MobileAds.initialize(this){}
        bannerPubblicita = findViewById(R.id.adBanner)
        bannerPubblicita.loadAd(AdRequest.Builder().build())
    }

    override fun onPause() {
        super.onPause()
        salvaListaTriplettePokemonCatturati()
    }

    private fun salvaListaTriplettePokemonCatturati() {
        sharedPreferences.edit()
            .putString("LISTA_CATTURATI", listaTripletteToString(listaTriplettePokemonCatturati))
            .apply()
        for ((i,x) in listaTriplettePokemonCatturati.withIndex()){
            Log.i("TRIPLETTA $i:", x[0].toString() + " , " + x[1].toString() + " , " + x[2].toString())
        }
    }

    private fun listaTripletteToString(listaTriplettePokemonCatturati : MutableList<MutableList<Int>>) : String{
        var stringaDiSalvataggio = ""
        for(tripletta in listaTriplettePokemonCatturati){
            for(pokemon in tripletta)
                stringaDiSalvataggio += "$pokemon;"
            stringaDiSalvataggio = stringaDiSalvataggio.substringBeforeLast(";")
            stringaDiSalvataggio += "|"
        }
        stringaDiSalvataggio = stringaDiSalvataggio.substringBeforeLast("|")
        return stringaDiSalvataggio
    }
}