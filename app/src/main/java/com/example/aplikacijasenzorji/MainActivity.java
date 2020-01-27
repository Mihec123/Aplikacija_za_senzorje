package com.example.aplikacijasenzorji;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

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
                requestPermissionForWriteExtertalStorage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /////////////////////////////////////////////////////////////////////////////////////
        Config test_config = new Config();

        test_config.getConfigurationValue();

//        Senzor test = new Senzor();
//        test.setIp("213.157.239.205:8390");
//        test.setZeton("12908390");
//        test.setIme("testni_senzor");
//        test.setId(1);
//        boolean povezi = test.SensorCheckConnection();
//        Log.d("INTERNET", String.valueOf(povezi));
//        boolean temperature = test.SensorCheckTemerature();
//        if (temperature)
//        {
//         List temp = new ArrayList();
//         temp = test.getTemperatura();
//         Log.d("INTERNET", String.valueOf(temp));
//        }
//
//        Grupa grupa_test = new Grupa();
//        grupa_test.addSenzor(test);
//        grupa_test.setId(1);
//        grupa_test.setIme("testna_grupa");
//
//        test_config.addGrupe(grupa_test);
//        test_config.addSenzorji(test);
//        test_config.writeConfigurationsValues();
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
}
