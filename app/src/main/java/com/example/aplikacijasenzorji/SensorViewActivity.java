package com.example.aplikacijasenzorji;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_view);

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);
        scale = outMetrics.density;

        Bundle bundle = getIntent().getExtras();
        id_senzorja = bundle.getInt("id");
        id_grupe = bundle.getInt("id_grupe");
        Log.d("test",String.valueOf(id_grupe));

        config.getConfigurationValue(this.getFilesDir() +"/"+filename);
        senzor = config.getSenzorji().get(config.getIdSenzor().indexOf(id_senzorja));


        //dobimo vn glavne gumbe

        FloatingActionButton brisi = (FloatingActionButton) findViewById(R.id.fb_delete);
        brisi.setOnClickListener(SensorViewActivity.this);
        brisi.setTag(0);

        FloatingActionButton edit = (FloatingActionButton)  findViewById(R.id.fb_edit);
        edit.setOnClickListener(SensorViewActivity.this);
        edit.setTag(1);


        TextView text = (TextView) findViewById(R.id.tv_ime);
        text.setText(senzor.getIme());


        View barva = findViewById(R.id.barva);
        barva.setBackgroundColor(senzor.getBarva());
        Log.d("onClick","prezvel barvo");

        lin = findViewById(R.id.linear);

        ////////////////////////////////////////////////////////////////////////////////////////////////

        //dodamo senzorje in pozenemo loop da popravlja vrednosti
        if(senzor.isPrikazi_vlago()){
            //prikazat mormo vlago + mogoce en senzor
            if(senzor.getStevilo_podsenzorjev() > 0){
                //mamo vlago + en senzor
                //ker je sam en senzor ga nastavmo na id 0
                RelativeLayout temp = senzor_view(0);
                lin.addView(temp);
                //ker je sam en senzor za vlago ga nastavmo na id -1
                temp = senzor_view_vlaga(-1);
                lin.addView(temp);

            }
            else{
                //mamo samo vlago
                //nastavmo id vlage na -1
                RelativeLayout temp = senzor_view_vlaga(-1);
                lin.addView(temp);
            }
        }
        else {
            //nimamo vlage imamo samo senzorje
            for (int i = 0; i < senzor.getStevilo_podsenzorjev(); i++) {
                RelativeLayout temp = senzor_view(i);
                lin.addView(temp);
            }
            //ker jih imamo lahko veliko dodamo na koncu se prazno vrstico da lahko scroolamo mal nizi
            View view = new View(this);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    (int )(200 * scale + 0.5f), //60dp
                    (int )(200 * scale + 0.5f) );//60dp
            view.setLayoutParams(lp);
            lin.addView(view);
        }

        //sprehajamo se cez senzorje in nastavljamo temperature ali vlago

    }

    private RelativeLayout senzor_view(int id){

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
                dp40 );
        //omejitve postavljanja
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.setMargins(0,dp5,0,dp5);

        view.setLayoutParams(lp);
        okno.addView(view);
        ///////////////////////////////////////////////////////////////////////////////////////
        //dodamo text view

        TextView temperatura_senzorja = new TextView(this);
        temperatura_senzorja.setId(View.generateViewId());
        temperatura_senzorja.setId(id);

        temperatura_senzorja.setText(senzor.getImena_temperaturnih_senzorjev().get(id) + ": " + getString(R.string.no_value));
        lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT );
        //omejitve postavljanja
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.setMargins(0,dp5,0,dp5);
        lp.addRule(RelativeLayout.BELOW,view.getId());
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        temperatura_senzorja.setLayoutParams(lp);

        okno.addView(temperatura_senzorja);

        //////////////////////////////////////////////////////////////////////////////////////////
        return okno;
    }

    private RelativeLayout senzor_view_vlaga(int id){

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
                dp40 );
        //omejitve postavljanja
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.setMargins(0,dp5,0,dp5);

        view.setLayoutParams(lp);
        okno.addView(view);
        ///////////////////////////////////////////////////////////////////////////////////////
        //dodamo text view

        TextView temperatura_senzorja = new TextView(this);
        temperatura_senzorja.setId(View.generateViewId());
        temperatura_senzorja.setId(id);

        temperatura_senzorja.setText(getString(R.string.no_value));
        lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT );
        //omejitve postavljanja
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.setMargins(0,dp5,0,dp5);
        lp.addRule(RelativeLayout.BELOW,view.getId());
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        temperatura_senzorja.setLayoutParams(lp);

        okno.addView(temperatura_senzorja);

        //////////////////////////////////////////////////////////////////////////////////////////
        return okno;
    }

    private int isInGroup(){
        if (config.getVrstni_red().contains(id_senzorja)){
            //ce senzor je v main vrsnem redu potem ni v nobeni grupi vrnemo 0
            return 0;
        }
        else{
            //je v neki grupi hocemo vrnt id grupe
            Grupa group;
            for(Grupa el:config.getGrupe()){
                for(Senzor el1:el.getSenzorji()){
                    if(el1.getId() == id_senzorja){
                        //najdl smo v tej grupi senzor
                        return el.getId();
                    }
                }
            }
            //neki je slo narobe
            return 1;
        }
    }

    @Override
    public void onClick(View view) {
        String tag = String.valueOf(view.getTag());
        if (tag.equals("0")){
            //smo prtisnl gumb brisi
            int id = isInGroup();
            if(id == 0){
                //zbrisat mormo samo iz configa vrsntni red
                //nardimo dialog ce je odgovor ja zbrisemo sicer ignoriramo klik
                AlertDialog.Builder alert = new AlertDialog.Builder(SensorViewActivity.this);
                alert.setTitle(getString(R.string.delete));
                alert.setMessage(getString(R.string.vprasanje));
                alert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        config.getVrstni_red().remove(config.getVrstni_red().indexOf(id_senzorja));
                        config.writeConfigurationsValues(getFilesDir() +"/"+filename);

                        //vrnemo se na main activity
                        Intent refresh = new Intent(SensorViewActivity.this, MainActivity.class);
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
        }
        else if(tag.equals("1")){
            //smo prtisnl gumb edit
            //se ni sprogramiran
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(id_grupe == 0) {
            //prsli iz main screena
            Intent refresh = new Intent(this, MainActivity.class);
            startActivity(refresh);
            this.finish();
        }
        else {
            //prsli iz neke grupe
            Intent refresh = new Intent(this, GroupViewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("id",id_grupe);
            refresh.putExtras(bundle);
            startActivity(refresh);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
