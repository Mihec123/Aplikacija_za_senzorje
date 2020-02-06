package com.example.aplikacijasenzorji;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

public class EditGroupActivity extends AppCompatActivity implements View.OnClickListener {

    Grupa grupa;
    int id_grupe;
    Config config = new Config();
    String filename = "devices.txt";
    EditText ime;
    String FILEPATH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);


        Log.d("edit","notr");
        //dobimo vn iz bundla id grupe
        Bundle bundle = getIntent().getExtras();
        id_grupe = bundle.getInt("id");
        Log.d("edit","bundle");
        FILEPATH = this.getFilesDir() +"/"+filename;

        config.getConfigurationValue(FILEPATH);
        Log.d("edit","config");


        grupa = config.getGrupe().get(config.getIdGrup().indexOf(id_grupe));

        //dodamo gumbe

        FloatingActionButton apply = (FloatingActionButton) findViewById(R.id.fb_apply);
        apply.setOnClickListener(EditGroupActivity.this);
        apply.setTag(0);
        Log.d("edit","apply");

        FloatingActionButton barva = (FloatingActionButton)  findViewById(R.id.fb_color);
        barva.setOnClickListener(EditGroupActivity.this);
        barva.setTag(1);
        Log.d("edit","barva");


        ime = (EditText) findViewById(R.id.ime_grupe);
        ime.setText(grupa.getIme());
    }

    @Override
    public void onClick(View view) {
        String str = view.getTag().toString();
        if(str.equals("0")){
            //hocmo shrant in zapret ta screen
            grupa.setIme(String.valueOf(ime.getText()));
            config.writeConfigurationsValues(FILEPATH);

            //gremo nazaj v grupo

            Intent refresh = new Intent(EditGroupActivity.this, GroupViewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("id",id_grupe);
            refresh.putExtras(bundle);
            startActivity(refresh);
            EditGroupActivity.this.finish();



        }
        else if(str.equals("1")){
            //hocmo spremenit barvo grupe
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

        Intent intent = new Intent(this, GroupViewActivity.class);
        //nardimo nov bundle da loh not damo id grupe
        Bundle bundle = new Bundle();
        bundle.putInt("id", id_grupe);
        intent.putExtras(bundle);
        startActivity(intent);
        this.finish();
        return super.onKeyDown(keyCode, event);
    }
}
