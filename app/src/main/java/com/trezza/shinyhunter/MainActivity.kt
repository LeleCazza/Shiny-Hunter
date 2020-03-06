package com.trezza.shinyhunter

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.widget.RelativeLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import com.daimajia.numberprogressbar.NumberProgressBar
import com.google.android.gms.ads.*

@Suppress("UNREACHABLE_CODE")
open class MainActivity : AppCompatActivity() {

    companion object{
        lateinit var listaTriplettePokemonCatturati : MutableList<MutableList<Int>>
        lateinit var interstitialPubblicita: InterstitialAd
        var pubblicitaClickVeloceOnlyOne = true
    }

    private var totaleNumeroDiPokemonShiny = 0
    private lateinit var pokemonDataCreator : PokemonDataCreator
    private lateinit var barraDiPercentualeShinyCatturati : NumberProgressBar
    private lateinit var labelDiPercentualeShinyCatturati : TextView
    private lateinit var sharedPreferences: SharedPreferences
    private var mostraSoloShinyCatturati = true
    private var mostraSoloShinyMancanti = true

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
            adapter = AdapterPokemon(
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val shinyMancanti = menu?.findItem(R.id.ShinyMancanti)
        val shinyCatturati = menu?.findItem(R.id.ShinyCatturati)
        shinyMancanti?.setOnMenuItemClickListener {
            if(mostraSoloShinyMancanti)
                impostaIconaShinyCatturati(shinyCatturati!!,shinyMancanti)
            else
                resettaIconeMenu(shinyCatturati!!,shinyMancanti)
            true
        }
        shinyCatturati?.setOnMenuItemClickListener {
            if(mostraSoloShinyCatturati)
                impostaIconaShinyMancanti(shinyCatturati,shinyMancanti!!)
            else
                resettaIconeMenu(shinyCatturati,shinyMancanti!!)
            true
        }
        return true
    }

    private fun impostaIconaShinyCatturati(iconaCatturati : MenuItem, iconaMancanti : MenuItem){
        mostraSoloShinyCatturati = false
        mostraSoloShinyMancanti()
        iconaMancanti.icon = getDrawable(R.drawable.shiny_mancanti_selezionato)!!
        iconaCatturati.icon = getDrawable(R.drawable.shiny_catturati)!!
        mostraSoloShinyCatturati = true
        mostraSoloShinyMancanti = false
    }

    private fun impostaIconaShinyMancanti(iconaCatturati : MenuItem, iconaMancanti : MenuItem){
        mostraSoloShinyMancanti = false
        mostraSoloShinyCatturati()
        iconaCatturati.icon = getDrawable(R.drawable.shiny_catturati_selezionato)!!
        iconaMancanti.icon = getDrawable(R.drawable.shiny_mancanti)!!
        mostraSoloShinyCatturati = false
        mostraSoloShinyMancanti = true
    }

    private fun resettaIconeMenu(iconaCatturati : MenuItem, iconaMancanti : MenuItem){
        creaAdapter()
        iconaCatturati.icon = getDrawable(R.drawable.shiny_catturati)!!
        iconaMancanti.icon = getDrawable(R.drawable.shiny_mancanti)!!
        mostraSoloShinyCatturati = true
        mostraSoloShinyMancanti = true
    }

    private fun mostraSoloShinyCatturati(){
        val shinyListCreator = ShinyDataCreator(listaTriplettePokemonCatturati,pokemonDataCreator)
        listView.apply {
            adapter = AdapterShiny(
                context,
                shinyListCreator.getListaSoloShinyCatturati(),
                getLarghezzaDisplay(),
                mostraSoloShinyCatturati,
                mostraSoloShinyMancanti
            )
        }
    }

    private fun mostraSoloShinyMancanti(){
        val shinyListCreator = ShinyDataCreator(listaTriplettePokemonCatturati,pokemonDataCreator)
        listView.apply {
            adapter = AdapterShiny(
                context,
                shinyListCreator.getListaSoloShinyMancanti(),
                getLarghezzaDisplay(),
                mostraSoloShinyCatturati,
                mostraSoloShinyMancanti
            )
        }
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
}