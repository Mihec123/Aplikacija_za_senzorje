package com.example.aplikacijasenzorji;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.angads25.filepicker.widget.OnCheckedChangeListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

import java.util.ArrayList;
import java.util.List;

public class EditSenzorActivity extends AppCompatActivity implements View.OnClickListener {
    String filename = "devices.txt";
    int id_senzorja;
    int id_grupe;
    String FILEPATH;
    LinearLayout lin;
    Config config = new Config();
    Senzor senzor;
    EditText ime;
    EditText ip;
    EditText token;
    EditText dolzina; //stevilo senzorjev
    CheckBox vlaga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_senzor);

        Bundle bundle = getIntent().getExtras();
        id_senzorja = bundle.getInt("id");
        id_grupe = bundle.getInt("id_grupe");

        FILEPATH = this.getFilesDir() +"/"+filename;
        config.getConfigurationValue(FILEPATH);
        senzor = config.getSenzorji().get(config.getIdSenzor().indexOf(id_senzorja));



        //Dodamo vse gumbe ter text viewe
        //apply button
        FloatingActionButton nov = (FloatingActionButton) findViewById(R.id.applay_button1);
        nov.setTag(0);
        nov.setOnClickListener( EditSenzorActivity.this);

        //test connection button
        FloatingActionButton test = (FloatingActionButton) findViewById(R.id.test_button1);
        test.setTag(1);
        test.setOnClickListener(EditSenzorActivity.this);

        //color picker button
        FloatingActionButton color_picker = (FloatingActionButton) findViewById(R.id.color_button1);
        color_picker.setTag(2);
        color_picker.setOnClickListener(EditSenzorActivity.this);

        ime = (EditText) findViewById(R.id.editTextname1);
        ime.setText(senzor.getIme());

        ip = (EditText) findViewById(R.id.editTextip1);
        ip.setText(senzor.getIp());

        token = (EditText) findViewById(R.id.editTexttoken1);
        token.setText(senzor.getZeton());

        lin = findViewById(R.id.imena_podsenzorjev);

        dolzina = (EditText) findViewById(R.id.editTextNS1);
        dolzina.setText(String.valueOf(senzor.getStevilo_podsenzorjev()));
        dolzina.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("test1", "kle smo");
                if (s.length() != 0) {
                    //ni prazno okno
                    //prevert mormo se ce mamo vlago prizgano najvec en senzor + vlaga
                    int stevilo = Integer.parseInt(String.valueOf(s));
                    if (vlaga.isChecked() && stevilo < 2) {
                        if (stevilo < senzor.getStevilo_podsenzorjev()) {
                            //ce zmanjsamo stevilo senzorjev
                            Log.d("test1", "zmanjsal stevilo");
                            senzor.setStevilo_podsenzorjev(stevilo);
                            senzor.setImena_temperaturnih_senzorjev(senzor.getImena_temperaturnih_senzorjev().subList(0, stevilo));
                            Log.d("test1", String.valueOf(senzor.getImena_temperaturnih_senzorjev()));
                            Log.d("test1", String.valueOf(senzor.getStevilo_podsenzorjev()));
                        } else {
                            //ce povecamo stevilo senzorjev
                            Log.d("test1", "povecal stevilo");

                            for (int i = senzor.getStevilo_podsenzorjev() + 1; i <= stevilo; i++) {
                                senzor.addImena_temperaturnih_senzorjev(getString(R.string.sensor) + String.valueOf(i));
                            }
                            senzor.setStevilo_podsenzorjev(stevilo);
                            Log.d("test1", String.valueOf(senzor.getImena_temperaturnih_senzorjev()));
                            Log.d("test1", String.valueOf(senzor.getStevilo_podsenzorjev()));
                        }
                        lin.removeAllViews();
                        //pozenemo zanko za spreminjanje imen temperaturnih podsenzorjev
                        for (int i = 0; i < senzor.getStevilo_podsenzorjev(); i++) {
                            EditText temp = new EditText(EditSenzorActivity.this);
                            temp.setId(i);
                            temp.setText(senzor.getImena_temperaturnih_senzorjev().get(i));
                            lin.addView(temp);
                        }

                    }
                    else if(!vlaga.isChecked()){
                        if (stevilo < senzor.getStevilo_podsenzorjev()) {
                            //ce zmanjsamo stevilo senzorjev
                            Log.d("test1", "zmanjsal stevilo");
                            senzor.setStevilo_podsenzorjev(stevilo);
                            senzor.setImena_temperaturnih_senzorjev(senzor.getImena_temperaturnih_senzorjev().subList(0, stevilo));
                            Log.d("test1", String.valueOf(senzor.getImena_temperaturnih_senzorjev()));
                            Log.d("test1", String.valueOf(senzor.getStevilo_podsenzorjev()));
                        } else {
                            //ce povecamo stevilo senzorjev
                            Log.d("test1", "povecal stevilo");

                            for (int i = senzor.getStevilo_podsenzorjev() + 1; i <= stevilo; i++) {
                                senzor.addImena_temperaturnih_senzorjev(getString(R.string.sensor) + String.valueOf(i));
                            }
                            senzor.setStevilo_podsenzorjev(stevilo);
                            Log.d("test1", String.valueOf(senzor.getImena_temperaturnih_senzorjev()));
                            Log.d("test1", String.valueOf(senzor.getStevilo_podsenzorjev()));
                        }
                        lin.removeAllViews();
                        //pozenemo zanko za spreminjanje imen temperaturnih podsenzorjev
                        for (int i = 0; i < senzor.getStevilo_podsenzorjev(); i++) {
                            EditText temp = new EditText(EditSenzorActivity.this);
                            temp.setId(i);
                            temp.setText(senzor.getImena_temperaturnih_senzorjev().get(i));
                            lin.addView(temp);
                        }
                    }
                    else{
                        //ponastavmo vrednosti
                        CharSequence text = getString(R.string.toast_prevec_senzorjev);
                        int duration = Toast.LENGTH_LONG;

                        Toast toast = Toast.makeText(EditSenzorActivity.this, text, duration);
                        toast.show();
                        dolzina.setText(String.valueOf(senzor.getStevilo_podsenzorjev()));
                    }
                }
            }
        });

        vlaga = (CheckBox) findViewById(R.id.checkBoxH1);
        vlaga.setChecked(senzor.isPrikazi_vlago());
        vlaga.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                             @Override
                                             public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                                                 if(isChecked && senzor.getStevilo_podsenzorjev() > 1){
                                                     CharSequence text = getString(R.string.toast_prevec_senzorjev);
                                                     int duration = Toast.LENGTH_LONG;

                                                     Toast toast = Toast.makeText(EditSenzorActivity.this, text, duration);
                                                     toast.show();
                                                     vlaga.setChecked(false);
                                                 }
                                                 else{
                                                     senzor.setPrikazi_vlago(isChecked);
                                                     vlaga.setChecked(isChecked);
                                                 }

                                             }
                                         }
        );


        //pozenemo zanko za spreminjanje imen temperaturnih podsenzorjev
        for(int i = 0; i < senzor.getStevilo_podsenzorjev();i++){
            EditText temp = new EditText(this);
            temp.setId(i);
            temp.setText(senzor.getImena_temperaturnih_senzorjev().get(i));
            lin.addView(temp);
        }
    }

    @Override
    public void onClick(View view) {
        String str = view.getTag().toString();
        //pogledamo tag da ugotovimo ker gumb smo tag = 0 smo klukca, tag=1 smo check connection, tag = 2 smo color picker
        if (str.equals("0")) {
            //preverimo najprej ce smo v vsa polja nekaj napisali
            boolean vsa_polja_izpolnjena = (!String.valueOf(ime.getText()).equals("") && !String.valueOf(ip.getText()).equals("") && !String.valueOf(token.getText()).equals(""));
            //prevermo se ce mamo vlago mamo lahko najvec en senzor za temperaturo
            if (vsa_polja_izpolnjena) {

                //popravimo senzor in ga shranimo

                Log.d("test","tuki");
                senzor.setIp(String.valueOf(ip.getText()));
                senzor.setZeton(String.valueOf(token.getText()));
                senzor.setIme(String.valueOf(ime.getText()));
                List<String> temp_imena = new ArrayList<String>();

                //dodamo default imena temperaturnih senzorjev
                for(int j=1; j <= senzor.getStevilo_podsenzorjev();j++)
                {
                    EditText edit = findViewById(j-1);
                    temp_imena.add(String.valueOf(edit.getText()));
                }
                senzor.setImena_temperaturnih_senzorjev(temp_imena);
                Log.d("test","tuki");

                //shranimo config file
                config.writeConfigurationsValues(FILEPATH);

                //gremo nazaj na senzor
                Intent intent = new Intent(this, SensorViewActivity.class);
                //nardimo nov bundle da loh not damo id senzorja
                Bundle bundle = new Bundle();
                bundle.putInt("id", id_senzorja);
                bundle.putInt("id_grupe", id_grupe);
                intent.putExtras(bundle);
                startActivity(intent);
                this.finish();
            }
            else{
                //vsa polja niso izpolnjena zato vrnemo toast
                Context context = getApplicationContext();
                CharSequence text = getString(R.string.toast_ni_vse_izpolnjeno);
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

        }
        else if (str.equals("1")) {
            //preverimo povezavo
            Senzor temp_senzor = new Senzor();
            temp_senzor.setIp(String.valueOf(ip.getText()));
            temp_senzor.setZeton(String.valueOf(token.getText()));
            temp_senzor.setIme(String.valueOf(ime.getText()));

            boolean test = temp_senzor.SensorCheckConnection();
            if (test){
                //uspel smo se povezat na senzor
                Context context = getApplicationContext();
                CharSequence text = getString(R.string.povezava_uspesna);
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            else{
                //nismo se uspel povezat na senzor
                Context context = getApplicationContext();
                CharSequence text = getString(R.string.povezava_neuspesna);
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

            }

        } else if (str.equals("2"))  {

            final ColorPicker cp = new ColorPicker(this, Color.red(senzor.getBarva()), Color.green(senzor.getBarva()), Color.blue(senzor.getBarva()));
            cp.show();
            cp.enableAutoClose();

            cp.setCallback(new ColorPickerCallback() {
                @Override
                public void onColorChosen(@ColorInt int color) {
                    senzor.setBarva(color);
                    Log.d("Alpha", Integer.toString(Color.alpha(color)));
                    Log.d("Red", Integer.toString(Color.red(color)));
                    Log.d("Green", Integer.toString(Color.green(color)));
                    Log.d("Blue", Integer.toString(Color.blue(color)));

                    Log.d("Pure Hex", Integer.toHexString(color));
                    Log.d("#Hex no alpha", String.format("#%06X", (0xFFFFFF & color)));
                    Log.d("#Hex with alpha", String.format("#%08X", (0xFFFFFFFF & color)));

                }
            });

        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        Intent intent = new Intent(this, SensorViewActivity.class);
        //nardimo nov bundle da loh not damo id senzorja
        Bundle bundle = new Bundle();
        bundle.putInt("id", id_senzorja);
        bundle.putInt("id_grupe", id_grupe);
        intent.putExtras(bundle);
        startActivity(intent);
        this.finish();
        return super.onKeyDown(keyCode, event);
    }
}
