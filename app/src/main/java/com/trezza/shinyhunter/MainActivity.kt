package com.trezza.shinyhunter

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.daimajia.numberprogressbar.NumberProgressBar
import com.google.android.gms.ads.*

open class MainActivity : AppCompatActivity() {

    companion object{
        lateinit var barraDiPercentualeShinyCatturati : NumberProgressBar
        var pubblicitaClickVeloceOnlyOne = true
        lateinit var labelDiPercentualeShinyCatturati : TextView
        lateinit var interstitialPubblicita: InterstitialAd
    }
    private var totaleNumeroDiPokemonShiny = 0
    private lateinit var sharedPreferences: SharedPreferences
    private var mostraSoloShinyCatturati = true
    private var mostraSoloShinyMancanti = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        setContentView(R.layout.activity_main)
        CaricaPubblicita()
        inizializzaComponenti()
    }

    private fun inizializzaComponenti(){
        totaleNumeroDiPokemonShiny = this.assets.list("pokemon")!!.size
        aggiornaLabelDiPercentualeShinyCatturati(0)
        aggiornaBarraDiPercentualeShinyCatturati(0)
    }

    override fun onStart() {
        super.onStart()
        CaricaStatoSalvato()
        val layout = findViewById<LinearLayout>(R.id.layout)
        ShinyDataCreator(Interfaccia(this,getLarghezzaDisplay(),layout))
        val totShinyCatturati = POKEMON.values.count{ v -> v == 1 }
        aggiornaLabelDiPercentualeShinyCatturati(totShinyCatturati)
        aggiornaBarraDiPercentualeShinyCatturati(totShinyCatturati)
    }

    private fun CaricaPubblicita(){
        MobileAds.initialize(this){}
        caricaBanner()
        caricaInterstitial()
    }

    private fun aggiornaLabelDiPercentualeShinyCatturati(shinyCatturati : Int){
        labelDiPercentualeShinyCatturati = findViewById(R.id.contatore)
        labelDiPercentualeShinyCatturati.text = "Shiny: $shinyCatturati / $totaleNumeroDiPokemonShiny"
    }

    private fun aggiornaBarraDiPercentualeShinyCatturati(shinyCatturati : Int){
        barraDiPercentualeShinyCatturati = findViewById(R.id.progressBar)
        barraDiPercentualeShinyCatturati.max = totaleNumeroDiPokemonShiny
        barraDiPercentualeShinyCatturati.progress = shinyCatturati
    }


    private fun getLarghezzaDisplay() : Int{
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val shinyMancanti = menu?.findItem(R.id.ShinyMancanti)
        val shinyCatturati = menu?.findItem(R.id.ShinyCatturati)
        val istruzioni = menu?.findItem(R.id.Istruzioni)
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
        istruzioni?.setOnMenuItemClickListener {
            val istr = DialogIstruzioni()
            istr.show(supportFragmentManager,"ISTRUZIONI")
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
        val layout = findViewById<LinearLayout>(R.id.layout)
        layout.removeAllViews()
        ShinyDataCreator(Interfaccia(this,getLarghezzaDisplay(),layout))
        iconaCatturati.icon = getDrawable(R.drawable.shiny_catturati)!!
        iconaMancanti.icon = getDrawable(R.drawable.shiny_mancanti)!!
        mostraSoloShinyCatturati = true
        mostraSoloShinyMancanti = true
    }

    private fun mostraSoloShinyCatturati(){
        val layout = findViewById<LinearLayout>(R.id.layout)
        layout.removeAllViews()
        val interfaccia = Interfaccia(this,getLarghezzaDisplay(),layout)
        var riga = mutableListOf<String>()
        val chiavi = POKEMON.keys
        for(c in chiavi){
            if(POKEMON[c] == 1){
                riga.add(c)
                if(riga.size == 3){
                    interfaccia.creaRiga(riga[0],riga[1],riga[2])
                    riga = mutableListOf()
                }
            }
        }
        if(riga.size != 0){
            repeat(3 - riga.size){
                riga.add("VUOTO")
            }
            interfaccia.creaRiga(riga[0],riga[1],riga[2])
        }
    }

    private fun mostraSoloShinyMancanti(){
        val layout = findViewById<LinearLayout>(R.id.layout)
        layout.removeAllViews()
        val interfaccia = Interfaccia(this,getLarghezzaDisplay(),layout)
        var riga = mutableListOf<String>()
        val chiavi = POKEMON.keys
        for(c in chiavi){
            if(POKEMON[c] == 0){
                riga.add(c)
                if(riga.size == 3){
                    interfaccia.creaRiga(riga[0],riga[1],riga[2])
                    riga = mutableListOf()
                }
            }
        }
        if(riga.size != 0){
            repeat(3 - riga.size){
                riga.add("VUOTO")
            }
            interfaccia.creaRiga(riga[0],riga[1],riga[2])
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
        SalvaStatoCorrente()
    }

    override fun onStop() {
        super.onStop()
        SalvaStatoCorrente()
    }

    override fun onDestroy() {
        super.onDestroy()
        SalvaStatoCorrente()
    }

    private fun SalvaStatoCorrente(){
        var stringaDiSalvataggio = ""
        for(pokemon in POKEMON){
            val chiave = pokemon.key
            val valore = pokemon.value
            stringaDiSalvataggio += "$chiave;$valore|"
        }
        stringaDiSalvataggio = stringaDiSalvataggio.substringBeforeLast("|")
        Log.i("STRINGA_DI_SALVATAGGIO", stringaDiSalvataggio)
        sharedPreferences.edit()
            .putString("DATABASE", stringaDiSalvataggio)
            .apply()
    }

    private fun CaricaStatoSalvato(){
        val stringaDiSalvataggio = sharedPreferences.getString("DATABASE", null)
        if(stringaDiSalvataggio != null){
            val pokemons = stringaDiSalvataggio.split("|")
            for(pokemon in pokemons){
                val chiaveValore = pokemon.split(";")
                POKEMON[chiaveValore[0]] = chiaveValore[1].toInt()
            }
        }
    }
}