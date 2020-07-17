package com.example.aplikacijasenzorji;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.core.content.res.ResourcesCompat;

public class Gumb_Creation {


    //gumb za senzor
    public RelativeLayout OblikaGumbaSenzor(String ime_senzorja, int Barva, Boolean prizgan, Boolean prikazi_temp, Boolean stikalo, Boolean online, int id, float scale, View.OnClickListener listener, Context context){


        //v funkcijah moramo vnasat v pikslih pretvormo dp v pixle
        int dp120 = (int) (120 * scale + 0.5f);
        int dp5 = (int) (5 * scale + 0.5f);
        int dp8 = (int) (8 * scale + 0.5f);
        int dp3 = (int) (3 * scale + 0.5f);
        int dp10 = (int) (10 * scale + 0.5f);
        int dp20 = (int) (20 * scale + 0.5f);
        int dp40 = (int) (40 * scale + 0.5f);
        int dp90 = (int) (90 * scale + 0.5f);
        int sirina_sc = (int) (100000 * scale + 0.5f);

        int visina = dp120;


        // Creating a new RelativeLayout
        RelativeLayout rl = new RelativeLayout(context);

        // Defining the RelativeLayout layout parameters.
        // In this case I want to fill its parent
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        rlp.addRule(RelativeLayout.CENTER_HORIZONTAL);

        rl.setLayoutParams(rlp);


        ///////////////////////////////////////////////////////////////////////////
        //Creating the background of button
        ImageView im = new ImageView(context);
        im.setId(View.generateViewId());


        //kako ga bomo not dal
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                visina );
        //omejitve postavljanja
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.setMargins(dp5,dp5,dp5,dp5);
        im.setBackgroundResource(R.drawable.background_metal_empty);
        im.setLayoutParams(lp);

        rl.addView(im);

        //////////////////////////////////////////////////////////////////////////////

        //creating the colored rim of button
        View v = new View(context);
        v.setId(View.generateViewId());
        v.setTag("barva");

        //uporabimo prejsno spremenljivko za parametre in jo povozimo
        lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                visina );
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.ALIGN_LEFT,im.getId());
        lp.addRule(RelativeLayout.ALIGN_TOP,im.getId());
        lp.addRule(RelativeLayout.ALIGN_RIGHT,im.getId());
        lp.addRule(RelativeLayout.ALIGN_BOTTOM,im.getId());
        lp.setMargins(dp5,dp5,dp5,dp5);

        //nastavimo barvo in robove

        //naredimo 2 dodatni barvi za sencenje robu
        int barva_svetla = enlight(Barva,0.2f);
        int barva_temna = darken(Barva,0.2f);




        //naredimo nov drawable objekt in mu nastavimo nase barve ter ta objekt damo
        //za background viewa ki naredi rob
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setColors(new int[]{
                barva_temna,
                Barva,
                barva_svetla,
                Barva,
                barva_temna

        });
        gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gd.setOrientation(GradientDrawable.Orientation.TL_BR);
        gd.setCornerRadius(30);

        //dodamo viewu bacground
        v.setBackground(gd);
        v.setLayoutParams(lp);

        //dodamo view v relativ layout

        rl.addView(v);

        ///////////////////////////////////////////////////////////////////////////////////////

        //dodamo forground

        ImageView im_forground = new ImageView(context);
        im_forground.setId(View.generateViewId());
        im_forground.setOnClickListener(listener);
        im_forground.setTag(String.valueOf(id)+",vec");

        //uporabimo prejsno spremenljivko za parametre in jo povozimo
        lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                visina );
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.ALIGN_LEFT,v.getId());
        lp.addRule(RelativeLayout.ALIGN_TOP,v.getId());
        lp.addRule(RelativeLayout.ALIGN_RIGHT,v.getId());
        lp.addRule(RelativeLayout.ALIGN_BOTTOM,v.getId());
        lp.setMargins(dp3,dp3,dp3,dp3);

        im_forground.setBackgroundResource(R.drawable.tech_background);
        im_forground.setLayoutParams(lp);

        rl.addView(im_forground);

        ////////////////////////////////////////////////////////////////////////////////////////////

        //dodamo ONOFF gumb

        ToggleButton OnOff = new ToggleButton(context);
        OnOff.setId(View.generateViewId());
        OnOff.setTag(String.valueOf(id) + ",onoff");

        //odstranimo napise
        OnOff.setText("");
        OnOff.setTextOff("");
        OnOff.setTextOn("");


        //ce mora biti senzor prizgan ga prizgemo ce je od grupe mamo vmesno stanje ce niso vsi prizgani ali ugasnjeni
        OnOff.setBackgroundResource(R.drawable.gumb);

        lp = new RelativeLayout.LayoutParams(
                dp90,
                dp90 );
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        lp.addRule(RelativeLayout.ALIGN_RIGHT,im_forground.getId());
        //lp.addRule(RelativeLayout.ALIGN_TOP,im_forground.getId());
        //lp.addRule(RelativeLayout.ALIGN_BOTTOM,im_forground.getId());

        lp.setMargins(0,dp5,dp10,dp5);

        OnOff.setLayoutParams(lp);
        OnOff.setOnClickListener(listener);
        rl.addView(OnOff);

        ////////////////////////////////////////////////////////////////////////////////////////////

        //dodamo text senzor

        TextView opis = new TextView(context);
        opis.setId(View.generateViewId());

        if (stikalo){
            opis.setText("STIKALO");
        }
        else{
            opis.setText("SENZOR");
        }
        //pogledamo ker napis bomo dali

        opis.setTextColor(Color.WHITE);
        opis.setTextSize(dp8);
        opis.setGravity(Gravity.LEFT);

        //nastavimo font
        Typeface typeface = ResourcesCompat.getFont(context, R.font.chunky);
        opis.setTypeface(typeface);



        lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT );
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.ALIGN_LEFT,im_forground.getId());
        lp.addRule(RelativeLayout.ALIGN_TOP,im_forground.getId());
        lp.setMargins(dp10,dp10,dp10,0);

        opis.setLayoutParams(lp);
        rl.addView(opis);




        ///////////////////////////////////////////////////////////////////////////////////////////
//        //dodamo image view ki kaze a smo online al offline
//        ImageView status = new ImageView(context);
//        status.setTag("status");
//        status.setBackgroundResource(R.drawable.ic_signal_wifi_off_black_24dp);
//        lp = new RelativeLayout.LayoutParams(
//                dp40,
//                dp40 );
//        //lp.addRule(RelativeLayout.ALIGN_TOP,im_forground.getId());
//        //lp.addRule(RelativeLayout.ALIGN_BOTTOM,im_forground.getId());
//        lp.addRule(RelativeLayout.LEFT_OF,OnOff.getId());
//
//        lp.setMargins(0,dp20,dp5,0);
//
//        status.setLayoutParams(lp);
//        rl.addView(status);

        //////////////////////////////////////////////////////////////////////////////////////////

        //dodamo okno ki kaze temperaturo senzorja

        if (prikazi_temp) {

            TextView temperatura = new TextView(context);
            temperatura.setId(View.generateViewId());
            temperatura.setTag("temp");
            temperatura.setText("/");

            temperatura.setTextColor(Color.GREEN);
            temperatura.setTextSize(dp3);
            temperatura.setBackgroundResource(R.drawable.techframeshort);
            temperatura.setGravity(Gravity.CENTER);
            temperatura.setPadding(10, 0, 10, 0);

            typeface = ResourcesCompat.getFont(context, R.font.clock_font);
            temperatura.setTypeface(typeface);


            lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            //lp.addRule(RelativeLayout.CENTER_VERTICAL);
            lp.addRule(RelativeLayout.ALIGN_TOP, im_forground.getId());
            lp.addRule(RelativeLayout.LEFT_OF, OnOff.getId());
            lp.addRule(RelativeLayout.RIGHT_OF, opis.getId());
            lp.setMargins(dp10, dp10, dp10, 0);

            temperatura.setLayoutParams(lp);
            rl.addView(temperatura);

            ///////////////////////////////////////////////////////////////////////////////////////////


            //dodamo ime senzorja

            TextView ime = new TextView(context);
            ime.setId(View.generateViewId());
            ime.setText(ime_senzorja);
            ime.setTextColor(Color.WHITE);
            ime.setTextSize(dp5);
            ime.setGravity(Gravity.LEFT);


            lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT );
            lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            lp.addRule(RelativeLayout.ALIGN_LEFT,im_forground.getId());
            lp.addRule(RelativeLayout.BELOW,opis.getId());
            lp.addRule(RelativeLayout.ALIGN_BOTTOM,im_forground.getId());
            lp.addRule(RelativeLayout.LEFT_OF,OnOff.getId());
            lp.addRule(RelativeLayout.BELOW, temperatura.getId());
            lp.setMargins(dp10,dp5,dp10,dp10);

            ime.setLayoutParams(lp);
            rl.addView(ime);
        }
        else{

            //dodamo ime senzorja

            TextView ime = new TextView(context);
            ime.setId(View.generateViewId());
            ime.setText(ime_senzorja);
            ime.setTextColor(Color.WHITE);
            ime.setTextSize(dp5);
            ime.setGravity(Gravity.LEFT);


            lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT );
            lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            lp.addRule(RelativeLayout.ALIGN_LEFT,im_forground.getId());
            lp.addRule(RelativeLayout.BELOW,opis.getId());
            lp.addRule(RelativeLayout.ALIGN_BOTTOM,im_forground.getId());
            lp.addRule(RelativeLayout.LEFT_OF,OnOff.getId());
            lp.addRule(RelativeLayout.BELOW, opis.getId());
            lp.setMargins(dp10,dp5,dp10,dp10);

            ime.setLayoutParams(lp);
            rl.addView(ime);

        }
        /////////////////////////////////////////////////////////////////////////////////////////

        ///////////////////////////////////////////////////////////////////////////////////////////





        return rl;
    }

    //gumb za grupo
    public RelativeLayout OblikaGumbaGrupa(String ime_grupe, int Barva, Boolean prizgan, Boolean online, Boolean prizgani_vsi_v_grupi, int stevilo_senzorjev, Boolean prizgan_noben, float scale, int id, View.OnClickListener listener, Context context){


        //v funkcijah moramo vnasat v pikslih pretvormo dp v pixle
        int dp120 = (int) (120 * scale + 0.5f);
        int dp5 = (int) (5 * scale + 0.5f);
        int dp8 = (int) (8 * scale + 0.5f);
        int dp3 = (int) (3 * scale + 0.5f);
        int dp10 = (int) (10 * scale + 0.5f);
        int dp20 = (int) (20 * scale + 0.5f);
        int dp40 = (int) (40 * scale + 0.5f);
        int dp90 = (int) (90 * scale + 0.5f);
        int dp160 = (int) (160 * scale + 0.5f);
        int dp35 = (int) (35 * scale + 0.5f);

        int visina = dp160;


        // Creating a new RelativeLayout
        RelativeLayout rl = new RelativeLayout(context);

        // Defining the RelativeLayout layout parameters.
        // In this case I want to fill its parent
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);


        ///////////////////////////////////////////////////////////////////////////
        //Creating the background of button
        ImageView im = new ImageView(context);
        im.setId(View.generateViewId());


        //kako ga bomo not dal
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                visina );
        //omejitve postavljanja
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.setMargins(dp5,dp5,dp5,dp5);
        im.setBackgroundResource(R.drawable.background_metal_empty);
        im.setLayoutParams(lp);

        rl.addView(im);

        //////////////////////////////////////////////////////////////////////////////

        //creating the colored rim of button
        View v = new View(context);
        v.setId(View.generateViewId());
        v.setTag("barva");

        //uporabimo prejsno spremenljivko za parametre in jo povozimo
        lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                visina );
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.ALIGN_LEFT,im.getId());
        lp.addRule(RelativeLayout.ALIGN_TOP,im.getId());
        lp.addRule(RelativeLayout.ALIGN_RIGHT,im.getId());
        lp.addRule(RelativeLayout.ALIGN_BOTTOM,im.getId());
        lp.setMargins(dp5,dp5,dp5,dp5);

        //nastavimo barvo in robove

        //naredimo 2 dodatni barvi za sencenje robu
        int barva_svetla = enlight(Barva,0.2f);
        int barva_temna = darken(Barva,0.2f);




        //naredimo nov drawable objekt in mu nastavimo nase barve ter ta objekt damo
        //za background viewa ki naredi rob
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setColors(new int[]{
                barva_temna,
                Barva,
                barva_svetla,
                Barva,
                barva_temna

        });
        gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gd.setOrientation(GradientDrawable.Orientation.TL_BR);
        gd.setCornerRadius(30);

        //dodamo viewu bacground
        v.setBackground(gd);
        v.setLayoutParams(lp);

        //dodamo view v relativ layout

        rl.addView(v);

        ///////////////////////////////////////////////////////////////////////////////////////

        //dodamo forground

        ImageView im_forground = new ImageView(context);
        im_forground.setId(View.generateViewId());
        im_forground.setOnClickListener(listener);
        im_forground.setTag(String.valueOf(id)+",vec");

        //uporabimo prejsno spremenljivko za parametre in jo povozimo
        lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                visina );
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.ALIGN_LEFT,v.getId());
        lp.addRule(RelativeLayout.ALIGN_TOP,v.getId());
        lp.addRule(RelativeLayout.ALIGN_RIGHT,v.getId());
        lp.addRule(RelativeLayout.ALIGN_BOTTOM,v.getId());
        lp.setMargins(dp3,dp3,dp3,dp3);

        im_forground.setBackgroundResource(R.drawable.tech_background);
        im_forground.setLayoutParams(lp);

        rl.addView(im_forground);

        ////////////////////////////////////////////////////////////////////////////////////////////

        //dodamo text a je senzor al grupa

        TextView opis = new TextView(context);
        opis.setId(View.generateViewId());

        //pogledamo ker napis bomo dali
        opis.setText("GRUPA");


        opis.setTextColor(Color.WHITE);
        opis.setTextSize(dp8);

        //nastavimo font
        Typeface typeface = ResourcesCompat.getFont(context, R.font.chunky);
        opis.setTypeface(typeface);



        lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT );
        //lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.ALIGN_LEFT,im_forground.getId());
        lp.addRule(RelativeLayout.ALIGN_TOP,im_forground.getId());
        lp.setMargins(dp10,dp10,0,0);

        opis.setLayoutParams(lp);
        rl.addView(opis);

        //////////////////////////////////////////////////////////////////////////////////////////

        //dodamo ONOFF gumb

        ToggleButton OnOff = new ToggleButton(context);
        OnOff.setId(View.generateViewId());
        OnOff.setTag(String.valueOf(id)+",onoff");

        //odstranimo napise
        OnOff.setText("");
        OnOff.setTextOff("");
        OnOff.setTextOn("");


        //ce mora biti senzor prizgan ga prizgemo ce je od grupe mamo vmesno stanje ce niso vsi prizgani ali ugasnjeni
        OnOff.setBackgroundResource(R.drawable.gumb);
        lp = new RelativeLayout.LayoutParams(
                dp90,
                dp90 );
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        lp.addRule(RelativeLayout.ALIGN_RIGHT,im_forground.getId());

        lp.setMargins(0,dp10,dp10,dp10);

        OnOff.setLayoutParams(lp);
        OnOff.setOnClickListener(listener);
        rl.addView(OnOff);




        ///////////////////////////////////////////////////////////////////////////////////////////


        //dodamo ime senzorja ali grupe

        TextView ime = new TextView(context);
        ime.setId(View.generateViewId());
        ime.setText(ime_grupe);
        ime.setTextColor(Color.WHITE);
        ime.setTextSize(dp5);


        lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT );
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.ALIGN_LEFT,im_forground.getId());
        lp.addRule(RelativeLayout.BELOW,opis.getId());
        lp.addRule(RelativeLayout.LEFT_OF,OnOff.getId());
        //lp.addRule(RelativeLayout.ABOVE,stevec.getId());
        lp.setMargins(dp10,0,0,dp5);

        ime.setLayoutParams(lp);
        rl.addView(ime);


        ////////////////////////////////////////////////////////////////////////////////////////////
        //dodamo stevec senzorjev v grupi

        TextView stevec = new TextView(context);
        stevec.setId(View.generateViewId());
        stevec.setText(String.valueOf(stevilo_senzorjev));
        stevec.setText("Online:0/" + String.valueOf(stevilo_senzorjev) + ", On:0");
        stevec.setTextColor(Color.GREEN);
        stevec.setTextSize(dp3);
        stevec.setBackgroundResource(R.drawable.techframelong);
        stevec.setGravity(Gravity.CENTER);
        stevec.setPadding(30,0,30,0);

        typeface = ResourcesCompat.getFont(context, R.font.clock_font);
        stevec.setTypeface(typeface);

        stevec.setTag("stevila");


        lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT );
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.ALIGN_LEFT,im_forground.getId());
        lp.addRule(RelativeLayout.BELOW,ime.getId());
        //lp.addRule(RelativeLayout.ALIGN_BOTTOM,im_forground.getId());
        lp.setMargins(dp10,0,0,dp10);

        stevec.setLayoutParams(lp);
        rl.addView(stevec);




        ///////////////////////////////////////////////////////////////////////////////////////////
        //dodamo image view ki kaze a smo online al offline
//        ImageView status = new ImageView(context);
//        status.setTag("status");
//        status.setBackgroundResource(R.drawable.ic_signal_wifi_off_black_24dp);
//        lp = new RelativeLayout.LayoutParams(
//                dp40,
//                dp40 );
//        //lp.addRule(RelativeLayout.ALIGN_TOP,im_forground.getId());
//        //lp.addRule(RelativeLayout.ALIGN_BOTTOM,im_forground.getId());
//        lp.addRule(RelativeLayout.LEFT_OF,OnOff.getId());
//        lp.addRule(RelativeLayout.CENTER_VERTICAL);
//
//        lp.setMargins(0,0,dp20,dp5);
//
//        status.setLayoutParams(lp);
//        rl.addView(status);

        //////////////////////////////////////////////////////////////////////////////////////////

        return rl;
    }

    public RelativeLayout emptySenzor(float scale,Context context){
        int dp120 = (int) (120 * scale + 0.5f);
        int dp5 = (int) (5 * scale + 0.5f);
        int dp3 = (int) (3 * scale + 0.5f);
        int dp10 = (int) (10 * scale + 0.5f);
        int dp20 = (int) (20 * scale + 0.5f);
        int dp40 = (int) (40 * scale + 0.5f);
        int dp90 = (int) (90 * scale + 0.5f);
        int sirina_sc = (int) (100000 * scale + 0.5f);

        int visina = dp120;


        // Creating a new RelativeLayout
        RelativeLayout rl = new RelativeLayout(context);

        // Defining the RelativeLayout layout parameters.
        // In this case I want to fill its parent
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                visina);

        rlp.addRule(RelativeLayout.CENTER_HORIZONTAL);

        rl.setLayoutParams(rlp);

        return rl;
    }

    public RelativeLayout emptySpace(int visina,Context context){

        // Creating a new RelativeLayout
        RelativeLayout rl = new RelativeLayout(context);

        // Defining the RelativeLayout layout parameters.
        // In this case I want to fill its parent
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                visina);

        rlp.addRule(RelativeLayout.CENTER_HORIZONTAL);

        rl.setLayoutParams(rlp);

        return rl;
    }

    private int enlight(int color, float amount) {
        //Vzame barvo in parameter amount, ki mora bit med 0,1 in iz dane barve za
        //procente amount naredi svetlejso barvo
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = Math.min(1.0f, amount + hsv[2]);
        return Color.HSVToColor(hsv);
    }

    private int darken(int color, float amount) {
        //Vzame barvo in parameter amount, ki mora bit med 0,1 in iz dane barve za
        //procente amount naredi temnejso barvo
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = Math.max(.0f, hsv[2]-amount);
        return Color.HSVToColor(hsv);
    }
}
