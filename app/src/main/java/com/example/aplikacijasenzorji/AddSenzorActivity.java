package com.example.aplikacijasenzorji;

import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddSenzorActivity extends AppCompatActivity implements View.OnClickListener {

    EditText ime;
    EditText ip;
    EditText token;
    EditText dolzina; //stevilo senzorjev
    CheckBox vlaga;
    String filename = "devices.txt";
    String FILEPATH;
    Config config = new Config();
    Senzor senzor = new Senzor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_senzor);

        FILEPATH = this.getFilesDir() +"/"+filename;
        config.getConfigurationValue(FILEPATH);



//////////////////////////////////////////////////////////////////////////////////////////////////
        //Dodamo vse gumbe ter text viewe
        //apply button
        FloatingActionButton nov = (FloatingActionButton) findViewById(R.id.applay_button);
        nov.setTag(0);
        nov.setOnClickListener( AddSenzorActivity.this);

        //test connection button
        FloatingActionButton test = (FloatingActionButton) findViewById(R.id.test_button);
        test.setTag(1);
        test.setOnClickListener(AddSenzorActivity.this);

        //color picker button
        FloatingActionButton color_picker = (FloatingActionButton) findViewById(R.id.color_button);
        color_picker.setTag(2);
        color_picker.setOnClickListener(AddSenzorActivity.this);

        ime = (EditText) findViewById(R.id.editTextname);
        ip = (EditText) findViewById(R.id.editTextip);
        token = (EditText) findViewById(R.id.editTexttoken);
        dolzina = (EditText) findViewById(R.id.editTextNS);
        dolzina.setText("1");
        vlaga = (CheckBox) findViewById(R.id.checkBoxH);
    }

    @Override
    public void onClick(View view) {
        String str = view.getTag().toString();
        //pogledamo tag da ugotovimo ker gumb smo tag = 0 smo klukca, tag=1 smo check connection, tag = 2 smo color picker
        if (str.equals("0")) {
            //preverimo najprej ce smo v vsa polja nekaj napisali
                boolean vsa_polja_izpolnjena = (!String.valueOf(ime.getText()).equals("") && !String.valueOf(ip.getText()).equals("") && !String.valueOf(token.getText()).equals("")  && !String.valueOf(dolzina.getText()).equals(""));
                //prevermo se ce mamo vlago mamo lahko najvec en senzor za temperaturo
                boolean vlaga_temperatura = ((vlaga.isChecked() && Integer.parseInt(String.valueOf(dolzina.getText())) < 2) || !vlaga.isChecked());
                if (vsa_polja_izpolnjena && vlaga_temperatura) {
                    Log.d("config_class", "dodajam");

                    //naredimo nov senzor in noter damo vse podatke, potem bomo ta senzor dali v config in config zapisali v file

                    senzor.setIp(String.valueOf(ip.getText()));
                    senzor.setPrikazi_vlago(vlaga.isChecked());
                    senzor.setStevilo_podsenzorjev(Integer.parseInt(String.valueOf(dolzina.getText())));
                    senzor.setZeton(String.valueOf(token.getText()));
                    senzor.setIme(String.valueOf(ime.getText()));
                    senzor.setId(smallestMissingUnsorted(config.getIdSenzor()));
                    Log.d("config_class", String.valueOf(senzor.getId()));

                    //dodamo default imena temperaturnih senzorjev
                    for(int j=1; j <= senzor.getStevilo_podsenzorjev();j++)
                    {
                        Log.i("config_class", getString(R.string.sensor)+String.valueOf(j));
                        senzor.addImena_temperaturnih_senzorjev(getString(R.string.sensor)+String.valueOf(j));
                    }

                    //shranimo config file
                    config.addVrstni_red(senzor.getId());
                    config.addSenzorji(senzor);
                    config.writeConfigurationsValues(FILEPATH);

                    //gremo nazaj na main activity
                    Intent refresh = new Intent(this, MainActivity.class);
                    startActivity(refresh);
                    this.finish();
                }
                else if(vlaga_temperatura){
                    //vsa polja niso izpolnjena zato vrnemo toast
                    Context context = getApplicationContext();
                    CharSequence text = getString(R.string.toast_ni_vse_izpolnjeno);
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else{
                    //vlaga prevec senzorjev
                    Context context = getApplicationContext();
                    CharSequence text = getString(R.string.toast_prevec_senzorjev);
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }

        }
        else if (str.equals("1")) {
            //preverimo povezavo
            Senzor temp_senzor = new Senzor();
            temp_senzor.setIp(String.valueOf(ip.getText()));
            temp_senzor.setPrikazi_vlago(vlaga.isChecked());
            temp_senzor.setStevilo_podsenzorjev(Integer.parseInt(String.valueOf(dolzina.getText())));
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

    // Function to find smallest missing element in a unsorted
    // array of distinct non-negative integers
    public static int smallestMissingUnsorted(List<Integer> seznam)
    {
        Log.i("config_class", String.valueOf(seznam));
        seznam.add(0);
        boolean[] flags = new boolean[seznam.size()];
        for (int number:seznam){
            if (number < flags.length) {
                flags[number] = true;
            }
        }
        for (int i = 0; i < flags.length; i++) {
            if (!flags[i]) {
                return i;
            }
        }

        seznam.remove(seznam.size()-1);
        Log.i("config_class", String.valueOf(seznam));
        return flags.length;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent refresh = new Intent(this, MainActivity.class);
            startActivity(refresh);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
