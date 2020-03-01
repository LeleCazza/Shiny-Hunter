package com.trezza.shinyhunter

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import com.daimajia.numberprogressbar.NumberProgressBar
import com.google.android.gms.ads.*

@Suppress("UNREACHABLE_CODE")
open class MainActivity : AppCompatActivity() {

    companion object{
        lateinit var listaTriplettePokemonCatturati : MutableList<MutableList<Int>>
        lateinit var interstitialPubblicita: InterstitialAd
        private var mostraSoloShinyCatturati = true
        private var mostraSoloShinyMancanti = true
    }

    private var totaleNumeroDiPokemonShiny = 0
    private lateinit var pokemonDataCreator : PokemonDataCreator
    private lateinit var barraDiPercentualeShinyCatturati : NumberProgressBar
    private lateinit var labelDiPercentualeShinyCatturati : TextView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        pokemonDataCreator = PokemonDataCreator(this.assets.list("pokemon")!!,this.application)
        MobileAds.initialize(this){}
        caricaBanner()
        caricaInterstitial()
    }

    override fun onStart() {
        super.onStart()
        inizializzaComponenti()
        caricaListaPokemonShinyCatturati(pokemonDataCreator)
        creaAdapter()
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

    private fun creaAdapter(){
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
        val layoutBanner = findViewById<RelativeLayout>(R.id.LayoutBanner)
        val adView = AdView(this)
        adView.adSize = AdSize.BANNER
        if (BuildConfig.DEBUG)
            adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"
        else
            adView.adUnitId = "ca-app-pub-4338558741002224/8225441708"
        adView.loadAd(AdRequest.Builder().build())
        layoutBanner.addView(adView)
    }

    private fun caricaInterstitial(){
        interstitialPubblicita = InterstitialAd(this)
        if (BuildConfig.DEBUG)
            interstitialPubblicita.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        else
            interstitialPubblicita.adUnitId = "ca-app-pub-4338558741002224/4574875265"
        interstitialPubblicita.loadAd(AdRequest.Builder().build())
        interstitialPubblicita.adListener = object : AdListener() {
            override fun onAdClosed() {
                interstitialPubblicita.loadAd(AdRequest.Builder().build())
            }
        }
    }

    override fun onPause() {
        super.onPause()
        salvaListaTriplettePokemonCatturati()
    }

    private fun salvaListaTriplettePokemonCatturati() {
        sharedPreferences.edit()
            .putString("LISTA_CATTURATI", listaTripletteToString(listaTriplettePokemonCatturati))
            .apply()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val sm = menu?.findItem(R.id.ShinyMancanti)
        val sc = menu?.findItem(R.id.ShinyCatturati)
        sm?.setOnMenuItemClickListener {
            if(mostraSoloShinyMancanti){
                mostraSoloShinyMancanti()
                sm.icon = ContextCompat.getDrawable(application.baseContext,R.drawable.shiny_mancanti_selezionato)!!
                sc?.icon = ContextCompat.getDrawable(application.baseContext,R.drawable.shiny_catturati)!!
                mostraSoloShinyMancanti = false
                mostraSoloShinyCatturati = true
            }
            else{
                creaAdapter()
                sc?.icon = ContextCompat.getDrawable(application.baseContext,R.drawable.shiny_catturati)!!
                sm.icon = ContextCompat.getDrawable(application.baseContext,R.drawable.shiny_mancanti)!!
                mostraSoloShinyCatturati = true
                mostraSoloShinyMancanti = true
            }
            onOptionsItemSelected(sm)
            true
        }
        sc?.setOnMenuItemClickListener {
            if(mostraSoloShinyCatturati){
                mostraSoloShinyMancanti = false
                mostraSoloShinyCatturati()
                sc.icon = ContextCompat.getDrawable(application.baseContext,R.drawable.shiny_catturati_selezionato)!!
                sm?.icon = ContextCompat.getDrawable(application.baseContext,R.drawable.shiny_mancanti)!!
                mostraSoloShinyCatturati = false
                mostraSoloShinyMancanti = true
            }
            else{
                creaAdapter()
                sc.icon = ContextCompat.getDrawable(application.baseContext,R.drawable.shiny_catturati)!!
                sm?.icon = ContextCompat.getDrawable(application.baseContext,R.drawable.shiny_mancanti)!!
                mostraSoloShinyCatturati = true
                mostraSoloShinyMancanti = true
            }
            onOptionsItemSelected(sc)
            true
        }
        return true
    }

    private fun mostraSoloShinyCatturati(){
        val listaSoloShinyCatturati: MutableList<MutableList<Drawable>> = mutableListOf()
        var triplettaShiny: MutableList<Drawable> = mutableListOf()
        var posizione = 0
        for((i,tripletta) in listaTriplettePokemonCatturati.withIndex()){
            if(tripletta[0] == 1 || tripletta[1] == 1 || tripletta[2] == 1){
                if(posizione == 3){
                    listaSoloShinyCatturati.add(triplettaShiny)
                    triplettaShiny = mutableListOf()
                    posizione = 0
                }
                if(posizione < 3){
                    if(tripletta[0] == 1){
                        triplettaShiny.add(pokemonDataCreator.getPokemonDrawable(i,0))
                        posizione++
                    }
                }
                else{
                    listaSoloShinyCatturati.add(triplettaShiny)
                    triplettaShiny = mutableListOf()
                    posizione = 0
                    if(tripletta[0] == 1){
                        triplettaShiny.add(pokemonDataCreator.getPokemonDrawable(i,0))
                        posizione = 1
                    }
                }
                if(posizione < 3){
                    if(tripletta[1] == 1){
                        triplettaShiny.add(pokemonDataCreator.getPokemonDrawable(i,1))
                        posizione++
                    }
                }
                else{
                    listaSoloShinyCatturati.add(triplettaShiny)
                    triplettaShiny = mutableListOf()
                    posizione = 0
                    if(tripletta[1] == 1) {
                        triplettaShiny.add(pokemonDataCreator.getPokemonDrawable(i, 1))
                        posizione = 1
                    }
                }
                if(posizione < 3){
                    if(tripletta[2] == 1) {
                        triplettaShiny.add(pokemonDataCreator.getPokemonDrawable(i, 2))
                        posizione++
                    }
                }
                else{
                    listaSoloShinyCatturati.add(triplettaShiny)
                    triplettaShiny = mutableListOf()
                    posizione = 0
                    if(tripletta[2] == 1){
                        triplettaShiny.add(pokemonDataCreator.getPokemonDrawable(i,2))
                        posizione = 1
                    }
                }
            }
        }
        repeat(3 - posizione){
            triplettaShiny.add(pokemonDataCreator.creaImmagineVuota())
        }
        listaSoloShinyCatturati.add(triplettaShiny)
        listView.apply {
            adapter = AdapterCriterio(
                context,
                listaSoloShinyCatturati,
                getLarghezzaDisplay(),
                mostraSoloShinyCatturati,
                mostraSoloShinyMancanti
            )
        }
    }

    private fun mostraSoloShinyMancanti(){
        val listaSoloShinyCatturati: MutableList<MutableList<Drawable>> = mutableListOf()
        var triplettaShiny: MutableList<Drawable> = mutableListOf()
        var posizione = 0
        for((i,tripletta) in listaTriplettePokemonCatturati.withIndex()){
            if(tripletta[0] == 0 || tripletta[1] == 0 || tripletta[2] == 0){
                if(posizione == 3){
                    listaSoloShinyCatturati.add(triplettaShiny)
                    triplettaShiny = mutableListOf()
                    posizione = 0
                }
                if(posizione < 3){
                    if(tripletta[0] == 0 && pokemonDataCreator.getPokemonDrawable(i,0).alpha != 0){
                        triplettaShiny.add(pokemonDataCreator.getPokemonDrawable(i,0))
                        posizione++
                    }
                }
                else{
                    listaSoloShinyCatturati.add(triplettaShiny)
                    triplettaShiny = mutableListOf()
                    posizione = 0
                    if(tripletta[0] == 0 && pokemonDataCreator.getPokemonDrawable(i,0).alpha != 0){
                        triplettaShiny.add(pokemonDataCreator.getPokemonDrawable(i,0))
                        posizione = 1
                    }
                }
                if(posizione < 3){
                    if(tripletta[1] == 0 && pokemonDataCreator.getPokemonDrawable(i,1).alpha != 0){
                        triplettaShiny.add(pokemonDataCreator.getPokemonDrawable(i,1))
                        posizione++
                    }
                }
                else{
                    listaSoloShinyCatturati.add(triplettaShiny)
                    triplettaShiny = mutableListOf()
                    posizione = 0
                    if(tripletta[1] == 0 && pokemonDataCreator.getPokemonDrawable(i,1).alpha != 0) {
                        triplettaShiny.add(pokemonDataCreator.getPokemonDrawable(i, 1))
                        posizione = 1
                    }
                }
                if(posizione < 3){
                    if(tripletta[2] == 0 && pokemonDataCreator.getPokemonDrawable(i,2).alpha != 0) {
                        triplettaShiny.add(pokemonDataCreator.getPokemonDrawable(i, 2))
                        posizione++
                    }
                }
                else{
                    listaSoloShinyCatturati.add(triplettaShiny)
                    triplettaShiny = mutableListOf()
                    posizione = 0
                    if(tripletta[2] == 0 && pokemonDataCreator.getPokemonDrawable(i,2).alpha != 0){
                        triplettaShiny.add(pokemonDataCreator.getPokemonDrawable(i,2))
                        posizione = 1
                    }
                }
            }
        }
        repeat(3 - posizione){
            triplettaShiny.add(pokemonDataCreator.creaImmagineVuota())
        }
        listaSoloShinyCatturati.add(triplettaShiny)
        listView.apply {
            adapter = AdapterCriterio(
                context,
                listaSoloShinyCatturati,
                getLarghezzaDisplay(),
                mostraSoloShinyCatturati,
                mostraSoloShinyMancanti
            )
        }
    }
}