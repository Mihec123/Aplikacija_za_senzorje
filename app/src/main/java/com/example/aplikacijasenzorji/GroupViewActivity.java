package com.example.aplikacijasenzorji;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class GroupViewActivity extends AppCompatActivity implements View.OnClickListener {

    String filename = "devices.txt";
    Config config = new Config();
    int id_grupe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_view);

        //dobimo id grupe
        Bundle bundle = getIntent().getExtras();
        id_grupe = bundle.getInt("id");

        config.getConfigurationValue(this.getFilesDir() +"/"+filename);


        /////////////////////////////////////////////////////////////////////////////////

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);
        final float scale = outMetrics.density;
        //scale bomo rabl da px pretvormo v dp
        /////////////////////////////////////////////////////////////////////


        //zacnemo postavljat gumbe
        LinearLayout okno = (LinearLayout) findViewById(R.id.lin_scroll);
        Gumb_Creation gumbi = new Gumb_Creation();
        Grupa grupa = config.getGrupe().get(config.getIdGrup().indexOf(id_grupe));
        Senzor senzor;
        RelativeLayout temp;
        for (Senzor sen:grupa.getSenzorji()){
            temp = gumbi.OblikaGumbaSenzor(sen.getIme(),sen.getBarva(),false,false,sen.getId(),scale,GroupViewActivity.this,this);
            okno.addView(temp);
            }



        }

    @Override
    public void onClick(View view) {
        String str = view.getTag().toString();
        String[] temp = str.split(",");
        int id = Integer.valueOf(temp[0]);
        String tip = temp[1];
        if( tip.equals("OnOff")){
            //mamo toggle button
        }
        else if(tip.equals("vec")) {
            //mamo gumb za vec opcij
            //gumb prtisnen na senzorju
            Intent intent = new Intent(this, SensorViewActivity.class);
            //nardimo nov bundle da loh not damo id senzorja
            Bundle bundle = new Bundle();
            bundle.putInt("id", id);
            bundle.putInt("id_grupe",id_grupe);
            intent.putExtras(bundle);
            startActivity(intent);
            this.finish();
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
