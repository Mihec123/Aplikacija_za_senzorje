package com.example.aplikacijasenzorji;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GroupViewActivity extends AppCompatActivity implements View.OnClickListener {

    String filename = "devices.txt";
    Config config = new Config();
    int id_grupe;
    Grupa grupa;
    String FILEPATH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_view);

        //dobimo id grupe
        Bundle bundle = getIntent().getExtras();
        id_grupe = bundle.getInt("id");

        FILEPATH = this.getFilesDir() +"/"+filename;
        config.getConfigurationValue(FILEPATH);


        /////////////////////////////////////////////////////////////////////////////////

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);
        final float scale = outMetrics.density;
        //scale bomo rabl da px pretvormo v dp
        /////////////////////////////////////////////////////////////////////

        //dobimo vn glavne gumbe

        FloatingActionButton brisi = (FloatingActionButton) findViewById(R.id.fb_delete);
        brisi.setOnClickListener(GroupViewActivity.this);
        brisi.setTag(0);

        FloatingActionButton edit = (FloatingActionButton)  findViewById(R.id.fb_edit);
        edit.setOnClickListener(GroupViewActivity.this);
        edit.setTag(1);


        //zacnemo postavljat gumbe
        LinearLayout okno = (LinearLayout) findViewById(R.id.lin_scroll);
        Gumb_Creation gumbi = new Gumb_Creation();

        //nastavimo grupo
        grupa = config.getGrupe().get(config.getIdGrup().indexOf(id_grupe));
        RelativeLayout temp;
        for (Senzor sen:grupa.getSenzorji()){
            temp = gumbi.OblikaGumbaSenzor(sen.getIme(),sen.getBarva(),false,false,sen.getId(),scale,GroupViewActivity.this,this);
            okno.addView(temp);
            }



        }

    @Override
    public void onClick(View view) {
        String str = view.getTag().toString();
        if (str.equals("0")){
            //prtisnl smo delete
            //zbrisat mormo celo grupo z vsemi senzorji
            //nardimo dialog ce je odgovor ja zbrisemo sicer ignoriramo klik
            AlertDialog.Builder alert = new AlertDialog.Builder(GroupViewActivity.this);
            alert.setTitle(getString(R.string.delete));
            alert.setMessage(getString(R.string.vprasanje_brisanje_grupe));
            alert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {


                    for(Senzor sen:grupa.getSenzorji()){
                        //zbrisemo vse senzorje v grupi
                        config.getSenzorji().remove(config.getIdSenzor().indexOf(sen.getId()));
                    }
                    //zbrisemo grupo
                    config.getGrupe().remove(config.getIdGrup().indexOf(id_grupe));

                    //zbrisemo grupo iz vrstnega reda
                    config.getVrstni_red().remove(config.getVrstni_red().indexOf(id_grupe));

                    //zapisemo v config
                    config.writeConfigurationsValues(FILEPATH);



                    //vrnemo se na activity grupe
                    Intent refresh = new Intent(GroupViewActivity.this, MainActivity.class);
                    startActivity(refresh);
                    GroupViewActivity.this.finish();

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
        else if(str.equals("1")){
            //prtisnl smo edit
            Intent intent = new Intent(GroupViewActivity.this, EditGroupActivity.class);
            //nardimo nov bundle da loh not damo id senzorja
            Bundle bundle = new Bundle();
            bundle.putInt("id", id_grupe);
            intent.putExtras(bundle);
            startActivity(intent);
            this.finish();

        }
        else {
            //prtisnl smo na nek senzor
            String[] temp = str.split(",");
            int id = Integer.valueOf(temp[0]);
            String tip = temp[1];
            if (tip.equals("OnOff")) {
                //mamo toggle button
            } else if (tip.equals("vec")) {
                //mamo gumb za vec opcij
                //gumb prtisnen na senzorju
                Intent intent = new Intent(this, SensorViewActivity.class);
                //nardimo nov bundle da loh not damo id senzorja
                Bundle bundle = new Bundle();
                bundle.putInt("id", id);
                bundle.putInt("id_grupe", id_grupe);
                intent.putExtras(bundle);
                startActivity(intent);
                this.finish();
            }
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
        this.finish();
        return super.onKeyDown(keyCode, event);
    }
}
