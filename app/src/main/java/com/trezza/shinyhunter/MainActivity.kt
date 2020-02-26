package com.trezza.shinyhunter

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.util.Log
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.gms.ads.MobileAds
import com.daimajia.numberprogressbar.NumberProgressBar

open class MainActivity : AppCompatActivity() {

    companion object{
        lateinit var catchedPokemon : MutableList<MutableList<Int>>
        lateinit var progress : NumberProgressBar
        lateinit var contator : TextView
        var shinyChatched = 0
        var pokemonTot = 0
    }

    private lateinit var imagesPokemon : MutableList<MutableList<Drawable>>
    private lateinit var imagesTripletta : MutableList<Drawable>
    private lateinit var catchedTripletta : MutableList<Int>
    private var oldEvoluzione = 0 ; private var sizeTripletta = 0
    private lateinit var sharedPreferences: SharedPreferences
    private val LISTA_CATTURATI = "LISTA_CATTURATI"
    private lateinit var mAdView : AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        inizializzaProgressBar()
        loadPokemon()
        loadSaveList()
        listView.apply {
            adapter = Adapter(context,imagesPokemon, getLarghezzaDisplay())
        }
        caricaBanner()
    }

    override fun onResume() {
        super.onResume()
        val contat = findViewById<TextView>(R.id.contatore)
        contat.text = "Shiny: $shinyChatched / $pokemonTot"
        contator = contat
        listView.apply {
            adapter = Adapter(context,imagesPokemon, getLarghezzaDisplay())
        }
    }

    private fun caricaBanner(){
        MobileAds.initialize(this){}
        mAdView = findViewById(R.id.adBanner)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

    private fun inizializzaProgressBar(){
        pokemonTot = this.assets.list("pokemon")!!.size
        val progressBar = findViewById<NumberProgressBar>(R.id.progressBar)
        progressBar.max = pokemonTot
        val contat = findViewById<TextView>(R.id.contatore)
        contat.text = "Shiny: 0 / $pokemonTot"
        progress = progressBar
        progress.progress = 0
        contator = contat
    }

    private fun loadSaveList(){
        val stringaDiSalvataggio = sharedPreferences.getString(LISTA_CATTURATI, null)
        if(stringaDiSalvataggio != null){
            val triplette = stringaDiSalvataggio?.split("|")?.iterator()
            for ((i,tripletta) in triplette!!.withIndex()){
                val pokemon = tripletta.split(";")
                catchedPokemon[i][0] = pokemon[0].toInt()
                catchedPokemon[i][1] = pokemon[1].toInt()
                catchedPokemon[i][2] = pokemon[2].toInt()
                if(pokemon[0].toInt() == 1)
                    shinyChatched++
                if(pokemon[1].toInt() == 1)
                    shinyChatched++
                if(pokemon[2].toInt() == 1)
                    shinyChatched++
            }
            progressBar.progress = shinyChatched
            contator.text = "Shiny: $shinyChatched / $pokemonTot"
        }
    }

    private fun loadPokemon(){
        val files = this.assets.list("pokemon")
        imagesPokemon = mutableListOf() ; imagesTripletta = mutableListOf()
        catchedPokemon = mutableListOf() ; catchedTripletta = mutableListOf()
        val immagineVuota = creaImmagineVuota()
        for (file in files!!){
            val evoluzione = file.split("_")[1].toInt()
            if(sizeTripletta == 3){
                creaNuovaTripletta()
            }
            if(oldEvoluzione < evoluzione)
                aggiungiATripletta(file, evoluzione)
            else{
                aggiungiImmaginiVuote(evoluzione, immagineVuota)
                aggiungiAListaPokemon(file)
            }
        }
    }

    private fun creaImmagineVuota() : Drawable{
        val vuoto = ContextCompat.getDrawable(this,R.drawable.vuoto)!!
        vuoto.alpha = 0
        return vuoto
    }

    private fun creaNuovaTripletta(){
        imagesPokemon.add(imagesTripletta)
        imagesTripletta = mutableListOf()
        catchedPokemon.add(catchedTripletta)
        catchedTripletta = mutableListOf()
        sizeTripletta = 0
        oldEvoluzione = 0
    }

    private fun aggiungiATripletta(file : String, evoluzione : Int){
        imagesTripletta.add(Drawable.createFromStream(application.assets.open("pokemon/$file"),null))
        catchedTripletta.add(0)
        oldEvoluzione = evoluzione
        sizeTripletta++
    }

    private fun aggiungiImmaginiVuote(evoluzione : Int, immagineVuota : Drawable){
        oldEvoluzione = evoluzione
        repeat(3 - sizeTripletta){
            imagesTripletta.add(immagineVuota)
            catchedTripletta.add(0)
        }
    }

    private fun aggiungiAListaPokemon(file : String){
        imagesPokemon.add(imagesTripletta)
        imagesTripletta = mutableListOf()
        catchedPokemon.add(catchedTripletta)
        catchedTripletta = mutableListOf()
        imagesTripletta.add(Drawable.createFromStream(application.assets.open("pokemon/$file"),null))
        catchedTripletta.add(0)
        sizeTripletta = 1
    }

    private fun getLarghezzaDisplay() : Int{
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    override fun onPause() {
        super.onPause()
        saveData()
    }

    private fun saveData() {
        sharedPreferences.edit()
            .putString(LISTA_CATTURATI, listaToString(catchedPokemon))
            .apply()
    }

    private fun listaToString(catchedPokemon : MutableList<MutableList<Int>>) : String{
        var stringaDiSalvataggio = ""
        for(tripletta in catchedPokemon){
            for(pokemon in tripletta)
                stringaDiSalvataggio += "$pokemon;"
            stringaDiSalvataggio = stringaDiSalvataggio.substringBeforeLast(";")
            stringaDiSalvataggio += "|"
        }
        stringaDiSalvataggio = stringaDiSalvataggio.substringBeforeLast("|")
        Log.i("Stringa di salvataggio", stringaDiSalvataggio)
        return stringaDiSalvataggio
    }
}