package com.example.aplikacijasenzorji;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import java.util.List;

public class AddGroupActivity extends AppCompatActivity implements View.OnClickListener {
    EditText ime;
    String filename = "devices.txt";
    String FILEPATH;
    Config config = new Config();
    Grupa grupa = new Grupa();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        FILEPATH = this.getFilesDir() +"/"+filename;
        config.getConfigurationValue(FILEPATH);


        //////////////////////////////////////////////////////////////////////////////////////////////////
        //Dodamo vse gumbe ter text viewe
        //apply button
        FloatingActionButton nov = (FloatingActionButton) findViewById(R.id.applay_button);
        nov.setTag(0);
        nov.setOnClickListener( AddGroupActivity.this);


        //color picker button
        FloatingActionButton color_picker = (FloatingActionButton) findViewById(R.id.color_button);
        color_picker.setTag(2);
        color_picker.setOnClickListener(AddGroupActivity.this);

        ime = (EditText) findViewById(R.id.editTextname);
    }

    @Override
    public void onClick(View view) {
        String str = view.getTag().toString();
        //pogledamo tag da ugotovimo ker gumb smo tag = 0 smo klukca, tag = 2 smo color picker
        if (str.equals("0")) {
            //preverimo najprej ce smo v vsa polja nekaj napisali
            boolean vsa_polja_izpolnjena = (!String.valueOf(ime.getText()).equals(""));
            if (vsa_polja_izpolnjena) {
                Log.d("config_class", "dodajam");

                //naredimo novo grupo in noter damo vse podatke, potem bomo to grupo dali v config in config zapisali v file

                grupa.setIme(String.valueOf(ime.getText()));
                Log.d("config_class", "dodajam ime grupe");
                grupa.setId(smallestMissingUnsorted(config.getIdGrup()));

                Log.d("config_class", "dodajam id grupe");
                Log.d("config_class", String.valueOf(grupa.getId()));
                //shranimo config file
                config.addVrstni_red(grupa.getId());
                Log.d("config_class", String.valueOf(config.getVrstni_red()));
                config.addGrupe(grupa);
                config.writeConfigurationsValues(FILEPATH);

                //gremo nazaj na main activity
                Intent refresh = new Intent(this, MainActivity.class);
                startActivity(refresh);
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

        } else if (str.equals("2"))  {

            final ColorPicker cp = new ColorPicker(this, Color.red(grupa.getBarva()), Color.green(grupa.getBarva()), Color.blue(grupa.getBarva()));
            cp.show();
            cp.enableAutoClose();

            cp.setCallback(new ColorPickerCallback() {
                @Override
                public void onColorChosen(@ColorInt int color) {
                    grupa.setBarva(color);
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

        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
        this.finish();
        return super.onKeyDown(keyCode, event);
    }

    // Function to find biggest missing element in a unsorted
    // array of distinct non-positive integers
    public static int smallestMissingUnsorted(List<Integer> seznam)
    {
        Log.i("config_class", String.valueOf(seznam));
        seznam.add(0);
        boolean[] flags = new boolean[seznam.size()];
        for (int number:seznam){
            Log.i("config_class", String.valueOf(number));
            if (absolute(number) < flags.length) {
                flags[absolute(number)] = true;
            }
        }
        for (int i = 0; i < flags.length; i++) {
            Log.i("config_class", String.valueOf(i));
            if (!flags[i]) {
                return -i;
            }
        }

        seznam.remove(seznam.size()-1);
        Log.i("config_class", String.valueOf(seznam));
        return -flags.length;
    }

    private static int absolute(int stevilo){
        if(stevilo<0){
            return -stevilo;
        }
        else{
            return stevilo;
        }
    }
}
