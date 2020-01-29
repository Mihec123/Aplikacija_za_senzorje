package com.example.aplikacijasenzorji;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    private Config config;
    private String filename = "devices.txt";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //vprasamo za dovoljenje za pisanje na zunanji disk in branje ce tega se nimamo
        /////////////////////////////////////////////////////////////////////////////////////////
        boolean dovoljenje = checkPermissionForWriteExtertalStorage();
        if (!dovoljenje){
            try {
                requestPermissionForWriteExtertalStorage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        dovoljenje = checkPermissionForReadExtertalStorage();
        if (!dovoljenje){
            try {
                requestPermissionForReadExtertalStorage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /////////////////////////////////////////////////////////////////////////////////////
        config = new Config();


        //poberemo konfiguracijo, ce ta ostaja sicer config ostane nova konfiguracija
        config.getConfigurationValue(this.getFilesDir() +"/"+filename);

        /////////////////////////////////////////////////////////////////////////////////

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);
        final float scale = outMetrics.density;
        //scale bomo rabl da px pretvormo v dp
        /////////////////////////////////////////////////////////////////////

        //zacnemo postavljat gumbe
        LinearLayout okno = (LinearLayout) findViewById(R.id.prostor_za_gumbe);
        Gumb_Creation gumbi = new Gumb_Creation();
        Grupa grupa;
        Senzor senzor;
        RelativeLayout temp;
        for (int stevilo:config.getVrstni_red()){
            if (stevilo<0){
                grupa = config.getGrupe().get(config.getIdGrup().indexOf(stevilo));
                temp = gumbi.OblikaGumbaGrupa(grupa.getIme(),grupa.getBarva(),false,false,false,grupa.getStevilo_senzorjev(),false,scale,this);
            }
            else{
                senzor = config.getSenzorji().get(config.getIdSenzor().indexOf(stevilo));
                temp = gumbi.OblikaGumbaSenzor(senzor.getIme(),senzor.getBarva(),false,false,scale,this);
            }
            okno.addView(temp);
        }

    }

    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void requestPermissionForReadExtertalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean checkPermissionForWriteExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void requestPermissionForWriteExtertalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //naredimo meni in klicemo primeren layout iz folderja menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // obravnavamo izbrane primere iz menija
        Intent intent;
        switch (item.getItemId()) {
            case R.id.dodaj_senzor:
                intent = new Intent(this, AddSenzorActivity.class);
                startActivity(intent);
                this.finish();
                return true;
            case R.id.dodaj_grupo:
                intent = new Intent(this, AddGroupActivity.class);
                startActivity(intent);
                this.finish();
                return true;
            case R.id.sort:
                intent = new Intent(this, SortActivity.class);
                startActivity(intent);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}