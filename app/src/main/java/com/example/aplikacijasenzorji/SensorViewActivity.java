package com.example.aplikacijasenzorji;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Sensor;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileOutputStream;
import java.util.List;

public class SensorViewActivity extends AppCompatActivity implements View.OnClickListener {

    Config config = new Config();
    int id_senzorja;
    int id_grupe; //bomo rabl ce pridemo iz grupe da bomo lahko back dal nazaj na grupo ne na main screen
    String filename = "devices.txt";
    Senzor senzor;
    LinearLayout lin; //layout v katerga bomo dajal notr senzorje in vlago
    float scale;
    String FILEPATH;
    private float SLABATEMP = -99999;
    private boolean koncajLoop = false;
    private int SLEEP_TIME = 6000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_view);

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        scale = outMetrics.density;

        Bundle bundle = getIntent().getExtras();
        id_senzorja = bundle.getInt("id");
        id_grupe = bundle.getInt("id_grupe");//ce nismo v grupi bo id 0
        Log.d("test", String.valueOf(id_grupe));

        FILEPATH = this.getFilesDir() + "/" + filename;
        config.getConfigurationValue(FILEPATH);
        senzor = config.getSenzorji().get(config.getIdSenzor().indexOf(id_senzorja));


        //dobimo vn glavne gumbe

        FloatingActionButton brisi = (FloatingActionButton) findViewById(R.id.fb_delete);
        brisi.setOnClickListener(SensorViewActivity.this);
        brisi.setTag(0);

        FloatingActionButton edit = (FloatingActionButton) findViewById(R.id.fb_edit);
        edit.setOnClickListener(SensorViewActivity.this);
        edit.setTag(1);


        TextView text = (TextView) findViewById(R.id.tv_ime);
        text.setText(senzor.getIme());


        View barva = findViewById(R.id.barva);
        barva.setBackgroundColor(senzor.getBarva());
        Log.d("onClick", "prezvel barvo");

//        lin = findViewById(R.id.linear);
//
//        ////////////////////////////////////////////////////////////////////////////////////////////////
//
//        //dodamo senzorje in pozenemo loop da popravlja vrednosti
//        if (senzor.isPrikazi_vlago()) {
//            //prikazat mormo vlago + mogoce en senzor
//            if (senzor.getStevilo_podsenzorjev() > 0) {
//                //mamo vlago + en senzor
//                //ker je sam en senzor ga nastavmo na id 0
//                RelativeLayout temp = senzor_view(0);
//                lin.addView(temp);
//                //ker je sam en senzor za vlago ga nastavmo na id 1
//                temp = senzor_view_vlaga(1);
//                lin.addView(temp);
//                run_main2();
//
//            } else {
//                //mamo samo vlago
//                //nastavmo id vlage na 1
//                RelativeLayout temp = senzor_view_vlaga(1);
//                lin.addView(temp);
//                Log.d("senzor_main", "pozeni main3");
//                run_main3();
//            }
//        } else {
//            //nimamo vlage imamo samo senzorje
//            for (int i = 0; i < senzor.getStevilo_podsenzorjev(); i++) {
//                RelativeLayout temp = senzor_view(i);
//                lin.addView(temp);
//            }
//            //ker jih imamo lahko veliko dodamo na koncu se prazno vrstico da lahko scroolamo mal nizi
//            View view = new View(this);
//            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//                    (int) (200 * scale + 0.5f), //60dp
//                    (int) (200 * scale + 0.5f));//60dp
//            view.setLayoutParams(lp);
//            lin.addView(view);
//            run_main1();
//        }

        //sprehajamo se cez senzorje in nastavljamo temperature ali vlago

    }

    private RelativeLayout senzor_view(int id) {

        int dp40 = (int) (40 * scale + 0.5f);
        int dp5 = (int) (5 * scale + 0.5f);
        RelativeLayout okno = new RelativeLayout(this);

        ///////////////////////////////////////////////////////////////////////////////////////
        //dodamo view
        ImageView view = new ImageView(this);
        view.setBackgroundResource(R.drawable.temperatura);
        view.setId(View.generateViewId());

        //dolocmo parametre
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                dp40,
                dp40);
        //omejitve postavljanja
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.setMargins(0, dp5, 0, dp5);

        view.setLayoutParams(lp);
        okno.addView(view);
        ///////////////////////////////////////////////////////////////////////////////////////
        //dodamo text view

        TextView temperatura_senzorja = new TextView(this);
        temperatura_senzorja.setId(View.generateViewId());
        temperatura_senzorja.setId(id);
        temperatura_senzorja.setPadding(10,0,10,0);

        // Initialize a new GradientDrawable
        GradientDrawable gd = new GradientDrawable();

        // Specify the shape of drawable
        gd.setShape(GradientDrawable.RECTANGLE);

        // Set the fill color of drawable
        gd.setColor(Color.WHITE); // make the background transparent

        // Create a 2 pixels width red colored border for drawable
        gd.setStroke(2, Color.BLACK); // border width and color

        // Make the border rounded
        gd.setCornerRadius(15.0f); // border corner radius

        // Finally, apply the GradientDrawable as TextView background
        temperatura_senzorja.setBackground(gd);


        temperatura_senzorja.setText(senzor.getImena_temperaturnih_senzorjev().get(id) + ": " + getString(R.string.no_value));
        lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        //omejitve postavljanja
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.setMargins(0, dp5, 0, dp5);
        lp.addRule(RelativeLayout.BELOW, view.getId());
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        temperatura_senzorja.setLayoutParams(lp);

        okno.addView(temperatura_senzorja);

        //////////////////////////////////////////////////////////////////////////////////////////
        return okno;
    }

    private RelativeLayout senzor_view_vlaga(int id) {

        int dp40 = (int) (40 * scale + 0.5f);
        int dp5 = (int) (5 * scale + 0.5f);
        RelativeLayout okno = new RelativeLayout(this);

        ///////////////////////////////////////////////////////////////////////////////////////
        //dodamo view
        ImageView view = new ImageView(this);
        view.setBackgroundResource(R.drawable.vlaga);
        view.setId(View.generateViewId());

        //dolocmo parametre
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                dp40,
                dp40);
        //omejitve postavljanja
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.setMargins(0, dp5, 0, dp5);

        view.setLayoutParams(lp);
        okno.addView(view);
        ///////////////////////////////////////////////////////////////////////////////////////
        //dodamo text view

        TextView temperatura_senzorja = new TextView(this);
        temperatura_senzorja.setId(View.generateViewId());
        temperatura_senzorja.setId(id);

        temperatura_senzorja.setPadding(10,0,10,0);

        // Initialize a new GradientDrawable
        GradientDrawable gd = new GradientDrawable();

        // Specify the shape of drawable
        gd.setShape(GradientDrawable.RECTANGLE);

        // Set the fill color of drawable
        gd.setColor(Color.WHITE); // make the background transparent

        // Create a 2 pixels width red colored border for drawable
        gd.setStroke(2, Color.BLACK); // border width and color

        // Make the border rounded
        gd.setCornerRadius(15.0f); // border corner radius

        // Finally, apply the GradientDrawable as TextView background
        temperatura_senzorja.setBackground(gd);

        temperatura_senzorja.setText(getString(R.string.no_value));
        lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        //omejitve postavljanja
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.setMargins(0, dp5, 0, dp5);
        lp.addRule(RelativeLayout.BELOW, view.getId());
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        temperatura_senzorja.setLayoutParams(lp);

        okno.addView(temperatura_senzorja);

        //////////////////////////////////////////////////////////////////////////////////////////
        return okno;
    }

    public void run_main3() {
        //ce mamo samo senzorje brez vlage
        Log.d("senzor_main", "zacetek main3");
        Thread thread = new Thread(pozeni_check3);
        thread.start();
    }

    private Runnable pozeni_check3 = new Runnable() {
        public void run() {
            while (!koncajLoop) {
                Log.d("senzor_main", "zacetek3");

                final Float vlaga = senzor.getVlaga();
                Log.d("senzor_main", String.valueOf(vlaga));
                SensorViewActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        @SuppressLint("ResourceType") TextView text_grupe = lin.findViewById(1);
                        Log.d("senzor_main", String.valueOf(text_grupe));
                        if (vlaga != SLABATEMP) {
                            text_grupe.setText(String.valueOf(vlaga) + "%");
                        }
                        else{
                            text_grupe.setText(getString(R.string.no_value));
                        }
                    }
                });


                Log.d("senzor_main", "konec3");
                SystemClock.sleep(SLEEP_TIME);

            }
            return;
        }
    };

    public void run_main2() {
        //ce mamo samo senzorje brez vlage
        Thread thread = new Thread(pozeni_check2);
        thread.start();
    }

    private Runnable pozeni_check2 = new Runnable() {
        public void run() {
            while (!koncajLoop) {
                Log.d("senzor_main", "zacetek2");

                senzor.SensorCheckTemerature();
                Log.d("senzor_main", String.valueOf(senzor.getTemperatura()));
                final Float temperatura = senzor.getTemperatura().get(0);
                Log.d("senzor_main", "zacetek2");
                final Float vlaga = senzor.getVlaga();
                SensorViewActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        @SuppressLint("ResourceType") TextView text_grupe = lin.findViewById(1);
                        if (vlaga != SLABATEMP) {
                            text_grupe.setText(String.valueOf(vlaga)+ "%");
                        }
                        else{
                            text_grupe.setText(getString(R.string.no_value));
                        }

                        if (temperatura != SLABATEMP) {
                            text_grupe = lin.findViewById(0);
                            text_grupe.setText(senzor.getImena_temperaturnih_senzorjev().get(0) + ": " + String.valueOf(temperatura)+ (char) 0x00B0 +"C");
                        }
                        else{
                            text_grupe.setText(getString(R.string.no_value));
                        }
                    }
                });


                Log.d("senzor_main", "konec2");
                SystemClock.sleep(SLEEP_TIME);

            }
            return;
        }
    };

    public void run_main1() {
        //ce mamo samo senzorje brez vlage
        Thread thread = new Thread(pozeni_check1);
        thread.start();
    }

    private Runnable pozeni_check1 = new Runnable() {
        public void run() {
            while (!koncajLoop) {
                Log.d("senzor_main", "zacetek1");

                senzor.SensorCheckTemerature();
                SensorViewActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final List<Float> temperature = senzor.getTemperatura();
                        Log.d("senzor_main", String.valueOf(temperature));
                        for (int i = 0; i < senzor.getStevilo_podsenzorjev(); i++) {
                            Log.d("senzor_main", String.valueOf(i));
                            TextView text_grupe = lin.findViewById(i);
                            Log.d("senzor_main", String.valueOf(text_grupe));
                            if (temperature.get(i) != SLABATEMP) {
                                Log.d("senzor_main", "nastavi text");
                                text_grupe.setText(senzor.getImena_temperaturnih_senzorjev().get(i) + ": " +String.valueOf(temperature.get(i))+ (char) 0x00B0 +"C");
                                Log.d("senzor_main", "nastavl text");
                            } else {
                                Log.d("senzor_main", "nastavi text slab");
                                text_grupe.setText(getString(R.string.no_value));
                                Log.d("senzor_main", "nastavl text slab");
                            }

                        }

                    }
                });


                Log.d("senzor_main", "konec1");
                SystemClock.sleep(SLEEP_TIME);

            }
            return;
        }
    };


    @Override
    public void onClick(View view) {
        String tag = String.valueOf(view.getTag());
        if (tag.equals("0")) {
            //smo prtisnl gumb brisi
            if (id_grupe == 0) {
                //zbrisat mormo samo iz configa vrsntni red
                //nardimo dialog ce je odgovor ja zbrisemo sicer ignoriramo klik
                AlertDialog.Builder alert = new AlertDialog.Builder(SensorViewActivity.this);
                alert.setTitle(getString(R.string.delete));
                alert.setMessage(getString(R.string.vprasanje));
                alert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        config.getVrstni_red().remove(config.getVrstni_red().indexOf(id_senzorja));
                        config.getSenzorji().remove(config.getIdSenzor().indexOf(id_senzorja));
                        config.writeConfigurationsValues(FILEPATH);

                        //vrnemo se na main activity
                        Intent refresh = new Intent(SensorViewActivity.this, MainActivity.class);
                        koncajLoop = true;
                        startActivity(refresh);
                        SensorViewActivity.this.finish();

                        dialog.dismiss();

                    }
                });
                alert.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.show();
            } else if (id_grupe < 0) {
                //smo v eni grupi z idjom id
                //zbrisat mormo senzor iz grupe
                //nardimo dialog ce je odgovor ja zbrisemo sicer ignoriramo klik
                AlertDialog.Builder alert = new AlertDialog.Builder(SensorViewActivity.this);
                alert.setTitle(getString(R.string.delete));
                alert.setMessage(getString(R.string.vprasanje));
                alert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Grupa grupa = config.getGrupe().get(config.getIdGrup().indexOf(id_grupe));
                        grupa.removeSenzorId(id_senzorja);
                        config.getSenzorji().remove(config.getIdSenzor().indexOf(id_senzorja));
                        config.writeConfigurationsValues(FILEPATH);

                        //vrnemo se na activity grupe
                        Intent refresh = new Intent(SensorViewActivity.this, GroupViewActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("id", id_grupe);
                        refresh.putExtras(bundle);
                        koncajLoop = true;
                        startActivity(refresh);
                        SensorViewActivity.this.finish();

                        dialog.dismiss();

                    }
                });
                alert.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        } else if (tag.equals("1")) {
            //smo prtisnl gumb edit
            Intent refresh = new Intent(SensorViewActivity.this, EditSenzorActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("id", id_senzorja);
            bundle.putInt("id_grupe", id_grupe);
            refresh.putExtras(bundle);
            koncajLoop = true;
            startActivity(refresh);
            SensorViewActivity.this.finish();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {

            if (id_grupe == 0) {
                //prsli iz main screena
                Intent refresh = new Intent(this, MainActivity.class);
                koncajLoop = true;
                startActivity(refresh);
                this.finish();
            } else {
                //prsli iz neke grupe
                Intent refresh = new Intent(this, GroupViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", id_grupe);
                refresh.putExtras(bundle);
                koncajLoop = true;
                startActivity(refresh);
                this.finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        Log.d("ACTIV1","stop");
        koncajLoop = true;
        lin = findViewById(R.id.linear);
        lin.removeAllViews();
        super.onStop();
    }

    @Override
    protected void onStart() {
        Log.d("ACTIV1","start");
        koncajLoop = false;
        lin = findViewById(R.id.linear);

        ////////////////////////////////////////////////////////////////////////////////////////////////

        //dodamo senzorje in pozenemo loop da popravlja vrednosti
        if (senzor.isPrikazi_vlago()) {
            //prikazat mormo vlago + mogoce en senzor
            if (senzor.getStevilo_podsenzorjev() > 0) {
                //mamo vlago + en senzor
                //ker je sam en senzor ga nastavmo na id 0
                RelativeLayout temp = senzor_view(0);
                lin.addView(temp);
                //ker je sam en senzor za vlago ga nastavmo na id 1
                temp = senzor_view_vlaga(1);
                lin.addView(temp);
                run_main2();

            } else {
                //mamo samo vlago
                //nastavmo id vlage na 1
                RelativeLayout temp = senzor_view_vlaga(1);
                lin.addView(temp);
                Log.d("senzor_main", "pozeni main3");
                run_main3();
            }
        } else {
            //nimamo vlage imamo samo senzorje
            for (int i = 0; i < senzor.getStevilo_podsenzorjev(); i++) {
                RelativeLayout temp = senzor_view(i);
                lin.addView(temp);
            }
            //ker jih imamo lahko veliko dodamo na koncu se prazno vrstico da lahko scroolamo mal nizi
            View view = new View(this);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    (int) (200 * scale + 0.5f), //60dp
                    (int) (200 * scale + 0.5f));//60dp
            view.setLayoutParams(lp);
            lin.addView(view);
            run_main1();
        }
        super.onStart();
    }
}
