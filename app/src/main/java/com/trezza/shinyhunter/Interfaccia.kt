package com.trezza.shinyhunter

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import android.graphics.drawable.Drawable
import android.view.Gravity
import com.trezza.shinyhunter.MainActivity.Companion.barraDiPercentualeShinyCatturati
import com.trezza.shinyhunter.MainActivity.Companion.labelDiPercentualeShinyCatturati

class Interfaccia(var context: Context, private var dimesioniDysplay : Int, private var layout : LinearLayout) {

    fun creaRiga(pok1 : String, pok2 : String, pok3 : String){
        val layoutRiga = LinearLayout(layout.context)
        layoutRiga.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,1f)
        layoutRiga.orientation = LinearLayout.HORIZONTAL
        val immaginiRiga = mutableListOf<ImageView>()
        if(pok1 != "VUOTO")
            immaginiRiga.add(CreaImmagine(pok1))
        if(pok2 != "VUOTO")
            immaginiRiga.add(CreaImmagine(pok2))
        if(pok3 != "VUOTO")
            immaginiRiga.add(CreaImmagine(pok3))
        for(immagine in immaginiRiga)
            layoutRiga.addView(immagine)
        layout.addView(layoutRiga)
    }

    private fun CreaImmagine(nomeImmagine: String) : ImageView{
        val immagine = ImageView(layout.context)
        SetParametri(immagine, nomeImmagine)
        SetIfIsCatturato(immagine, nomeImmagine)
        SetEventClick(immagine,nomeImmagine)
        SetEventHoldClick(immagine,nomeImmagine)
        return immagine
    }

    private fun SetParametri(immagine : ImageView, nomeImmagine : String){
        immagine.setImageDrawable(Drawable.createFromStream(context.assets.open("pokemon/$nomeImmagine.png"),null))
        val layoutParams = LinearLayout.LayoutParams(dimesioniDysplay/3, dimesioniDysplay/3)
        layoutParams.gravity = Gravity.CENTER
        immagine.layoutParams = layoutParams
    }

    private fun SetIfIsCatturato(immagine : ImageView, nomeImmagine : String){
        if(POKEMON[nomeImmagine] == 1)
            immagine.setBackgroundResource(R.drawable.catched)
    }

    private fun SetEventClick(immagine : ImageView, nomeImmagine : String){
        immagine.setOnClickListener {
            if(POKEMON[nomeImmagine] == 0){
                it.setBackgroundResource(R.drawable.catched)
                POKEMON[nomeImmagine] = 1
                incrementaShinyCatturati()
                mostraInterstitialClickVeloce()
            }
        }
    }

    private fun SetEventHoldClick(immagine : ImageView, nomeImmagine : String){
        immagine.setOnLongClickListener {
            if(POKEMON[nomeImmagine] == 1){
                it.background = null
                POKEMON[nomeImmagine] = 0
                decrementaShinyCatturati()
                mostraInterstitialClickLungo()
            }
            true
        }
    }

    private fun incrementaShinyCatturati(){
        val shinyCatturati = ++barraDiPercentualeShinyCatturati.progress
        barraDiPercentualeShinyCatturati.progress = shinyCatturati
        labelDiPercentualeShinyCatturati.text = "Shiny: $shinyCatturati / ${barraDiPercentualeShinyCatturati.max}"
    }

    private fun decrementaShinyCatturati(){
        val shinyCatturati = --barraDiPercentualeShinyCatturati.progress
        barraDiPercentualeShinyCatturati.progress = shinyCatturati
        labelDiPercentualeShinyCatturati.text = "Shiny: $shinyCatturati / ${barraDiPercentualeShinyCatturati.max}"
    }

    private fun mostraInterstitialClickVeloce(){
        if(MainActivity.pubblicitaClickVeloceOnlyOne)
            if (MainActivity.interstitialPubblicita.isLoaded) {
                MainActivity.interstitialPubblicita.show()
                MainActivity.pubblicitaClickVeloceOnlyOne = false
            }
    }

    private fun mostraInterstitialClickLungo(){
        if (MainActivity.interstitialPubblicita.isLoaded)
            MainActivity.interstitialPubblicita.show()
    }
}