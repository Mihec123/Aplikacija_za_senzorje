package com.example.aplikacijasenzorji;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private Config config;
    private String filename = "devices.txt";
    String FILEPATH;
    private int BUFFER = 3;
    private List<RelativeLayout> gumbi_oblika = new ArrayList<>(); //sem bomo shranjevali toggle butne da jim bomo lahko menjal ozadja
    private float SLABATEMP = -99999;
    private boolean koncajLoop = false;
    private int SLEEP_TIME = 2000;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        //vprasamo za dovoljenje za pisanje na zunanji disk in branje ce tega se nimamo
//        /////////////////////////////////////////////////////////////////////////////////////////
//        boolean dovoljenje = checkPermissionForWriteExtertalStorage();
//        if (!dovoljenje) {
//            try {
//                requestPermissionForWriteExtertalStorage();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        dovoljenje = checkPermissionForReadExtertalStorage();
//        if (!dovoljenje) {
//            try {
//                requestPermissionForReadExtertalStorage();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        /////////////////////////////////////////////////////////////////////////////////////
        config = new Config();

        FILEPATH = this.getFilesDir() + "/" + filename;

        //poberemo konfiguracijo, ce ta ostaja sicer config ostane nova konfiguracija
        config.getConfigurationValue(FILEPATH);

        /////////////////////////////////////////////////////////////////////////////////

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
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
        for (int stevilo : config.getVrstni_red()) {
            if (stevilo < 0) {
                grupa = config.getGrupe().get(config.getIdGrup().indexOf(stevilo));
                temp = gumbi.OblikaGumbaGrupa(grupa.getIme(), Color.GRAY, false, false, false, grupa.getStevilo_senzorjev(), false, scale, grupa.getId(), MainActivity.this, this);
                gumbi_oblika.add(temp);
            } else {
                senzor = config.getSenzorji().get(config.getIdSenzor().indexOf(stevilo));
                temp = gumbi.OblikaGumbaSenzor(senzor.getIme(), Color.GRAY, false, false, senzor.getId(), scale, MainActivity.this, this);
                gumbi_oblika.add(temp);
            }
            okno.addView(temp);
        }

        //zacnemo while zanko za pregled senzorjev in grup
        run_main();


    }

    private Runnable pozeni_check = new Runnable() {
        public void run() {
            while (!koncajLoop) {
                Log.i("run_del", "zacel zanko");
                for (int id : config.getIdSenzor()) {
                    //senzor
                    if (config.getVrstni_red().contains(id)) {
                        //senzor je na glavnem screenu, mormo barvat pa popravljat gumbe
                        int index = config.getIdSenzor().indexOf(id);
                        final Senzor sen = config.getSenzorji().get(index);
                        Pair<Boolean, Boolean> online = sen.SenzorGetOnline();
                        RelativeLayout relativeLayout = gumbi_oblika.get(config.getVrstni_red().indexOf(id));
                        int command = sen.getCommand();
                        if (online.first.equals(true)) {
                            Log.d("run_del", "senzor: " + String.valueOf(id) + " je online");
                            //smo online
//                            ImageView temp_image = relativeLayout.findViewWithTag("status");
//                            temp_image.setBackgroundResource(R.drawable.ic_wifi_black_24dp);

                            //pobarvamo senzor
                            View view = relativeLayout.findViewWithTag("barva");
                            view.setBackground(create_gd(sen.getBarva()));

                            //probamo dobit temperaturo

                            final TextView text_temperatura = relativeLayout.findViewWithTag("temp");

                            //pogledamo temp prvega senzorja
                            float temp_prvega_senzorja = sen.getFirstTemp();
                            String temp_prvega_senzorja_text;
                            if (temp_prvega_senzorja == SLABATEMP) {
                                temp_prvega_senzorja_text = "/";
                            } else {
                                temp_prvega_senzorja_text = String.valueOf(temp_prvega_senzorja)  + (char) 0x00B0 +"C";
                            }
                            final String temp_prvega_senzorja_text_final = temp_prvega_senzorja_text;

                            //ui updati morjo bit na main threadu zato kle text nastavmo z main threadom

                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    text_temperatura.setText(temp_prvega_senzorja_text_final);
                                }
                            });

                            //StatusBufferList.set(index, 1);
                            sen.setStatusBuffer(1);
                            //pogledamo ce mormo przgat ali ugasnt senzor
                            if (command == 1 && online.second.equals(false)) {
                                Log.d("run_del", "hocmo prizgat, ni prizgan");
                                //hocmo przgat senozor, trenutno je ugasnen
                                boolean uspeli = sen.SenzorPrizgiUgasni(true);
                                if (uspeli) {
                                    //uspesno smo izvedli prizig
                                    sen.setCommandBuffer(0);
                                    sen.setCommand(0);
                                    //popravmo da ne rabmo vec przgat
                                    //spremenimo barvo gumba
                                    ToggleButton temp_toggle = relativeLayout.findViewWithTag(String.valueOf(id) + ",onoff");
                                    temp_toggle.setChecked(true);
                                    temp_toggle.setBackgroundResource(R.drawable.gumb_zelen);

                                } else if (sen.getCommandBuffer() < BUFFER) {
                                    //nismo uspel prizgat ampak se bomo poskusal
                                    sen.addCommandBuffer();
                                } else {
                                    //obupamo in vrnemo toast da nismo uspel prizgat senozrja
                                    //popravimo bufferlist na 0
                                    sen.setCommandBuffer(0);
                                    //popravimo comande na 0
                                    sen.setCommand(0);
                                    CharSequence text = getString(R.string.neuspesen_prizig_senzorja) + sen.getIme();
                                    int duration = Toast.LENGTH_SHORT;

                                    Toast toast = Toast.makeText(MainActivity.this, text, duration);
                                    toast.show();
                                }

                            } else if (command == -1 && online.second.equals(true)) {
                                //hocmo ugasnit senozor, je trenutno przgan
                                Log.d("run_del", "hocmo ugasnt, je prizgan");
                                boolean uspeli = sen.SenzorPrizgiUgasni(false);
                                if (uspeli) {
                                    //uspesno smo ugasnili
                                    sen.setCommand(0);
                                    sen.setCommandBuffer(0);
                                    //spremenimo barvo gumba
                                    ToggleButton temp_toggle = relativeLayout.findViewWithTag(String.valueOf(id) + ",onoff");
                                    temp_toggle.setBackgroundResource(R.drawable.gumb);
                                    temp_toggle.setChecked(false);

                                } else if (sen.getCommandBuffer() < BUFFER) {
                                    //nismo uspel ugasnt ampak se bomo poskusal
                                    sen.addCommandBuffer();
                                } else {
                                    //obupamo in vrnemo toast da nismo uspel prizgat senozrja
                                    //popravimo bufferlist na 0
                                    sen.setCommand(0);
                                    sen.setCommandBuffer(0);
                                    //CommandBufferList.set(index, 0);
                                    //CommandList.set(index, 0);
                                    CharSequence text = getString(R.string.neuspeseno_ugasanje_senzorja) + sen.getIme();
                                    int duration = Toast.LENGTH_SHORT;

                                    Toast toast = Toast.makeText(MainActivity.this, text, duration);
                                    toast.show();
                                }
                            } else if (command == -1 && online.second.equals(false)) {
                                //hocmo ugasnt senzor, pa je ze ugasnen
                                Log.d("run_del", "hocmo ugasnt, ni prizgan");

                                //pocistt mormo samo buffer pa gumbe pobarvat
                                ToggleButton temp_toggle = relativeLayout.findViewWithTag(String.valueOf(id) + ",onoff");
                                temp_toggle.setBackgroundResource(R.drawable.gumb);
                                temp_toggle.setChecked(false);
                                //pocistmo buffer
                                //popravmo da ne rabmo vec ugasnt
                                sen.setCommand(0);
                                sen.setCommandBuffer(0);
                            } else if (command == 1 && online.second.equals(true)) {
                                //hocmo przgat senzor, pa je ze przgan
                                Log.d("run_del", "hocmo prizgat, je prizgan");

                                //pocistt mormo samo buffer pa gumbe pobarvat
                                ToggleButton temp_toggle = relativeLayout.findViewWithTag(String.valueOf(id) + ",onoff");
                                temp_toggle.setBackgroundResource(R.drawable.gumb_zelen);
                                temp_toggle.setChecked(true);
                                //pocistmo buffer
                                //popravmo da ne rabmo vec przgat
                                sen.setCommand(0);
                                sen.setCommandBuffer(0);

                            } else {
                                //nocmo nardit nc, torej samo nastavmo pravo barvo gumba
                                if (online.second.equals(true)) {
                                    Log.d("run_del", "nas ne zanima je prizgan");
                                    //more bit pobarvan na zeleno
                                    ToggleButton temp_toggle = relativeLayout.findViewWithTag(String.valueOf(id) + ",onoff");
                                    temp_toggle.setBackgroundResource(R.drawable.gumb_zelen);
                                    temp_toggle.setChecked(true);
                                } else {
                                    Log.d("run_del", "nas ne zanima je ugasnjen");
                                    //more bit pobarvan na sivo
                                    ToggleButton temp_toggle = relativeLayout.findViewWithTag(String.valueOf(id) + ",onoff");
                                    temp_toggle.setBackgroundResource(R.drawable.gumb);
                                    temp_toggle.setChecked(false);
                                }

                            }
                        } else {
                            Log.d("run_del", "senzor: " + String.valueOf(id) + " ni online");
                            //nismo online
                            //pogledamo ce smo prej bli online pa je sam lag potem ohranmo status online
                            int vrednost_status_buffer = sen.getStatusBuffer();
                            if (vrednost_status_buffer < BUFFER && vrednost_status_buffer > 0) {
                                //bli smo online
                                //povecamo buffer za +1 in pustimo online status
                                //StatusBufferList.set(index, vrednost_status_buffer + 1);
                                sen.addStatusBuffer();

                                //ce mormo kej nardit z switchom se vseeno probamo nardit
                                if (command == 1) {
                                    //hocmo przgat senozor
                                    boolean uspeli = sen.SenzorPrizgiUgasni(true);
                                    if (uspeli) {
                                        //uspesno smo izvedli prizig
                                        //ne rabmo vec przgat
                                        sen.setCommand(0);
                                        sen.setCommandBuffer(0);
                                        //spremenimo barvo gumba
                                        ToggleButton temp_toggle = relativeLayout.findViewWithTag(String.valueOf(id) + ",onoff");
                                        temp_toggle.setBackgroundResource(R.drawable.gumb_zelen);
                                        temp_toggle.setChecked(true);

                                    } else if (sen.getCommandBuffer() < BUFFER) {
                                        //nismo uspel prizgat ampak se bomo poskusal
                                        sen.addCommandBuffer();
                                    } else {
                                        //obupamo in vrnemo toast da nismo uspel prizgat senozrja
                                        //popravimo bufferlist na 0
                                        //ker smo obupal popucamo buffer
                                        sen.setCommand(0);
                                        sen.setCommandBuffer(0);
                                        CharSequence text = getString(R.string.neuspesen_prizig_senzorja) + sen.getIme();
                                        int duration = Toast.LENGTH_SHORT;

                                        Toast toast = Toast.makeText(MainActivity.this, text, duration);
                                        toast.show();
                                    }
                                } else if (command == -1) {
                                    //hocmo ugasnit senozor
                                    boolean uspeli = sen.SenzorPrizgiUgasni(false);
                                    if (uspeli) {
                                        //uspesno smo ugasnili
                                        sen.setCommand(0);
                                        sen.setCommandBuffer(0);

                                        //spremenimo barvo gumba
                                        ToggleButton temp_toggle = relativeLayout.findViewWithTag(String.valueOf(id) + ",onoff");
                                        temp_toggle.setBackgroundResource(R.drawable.gumb);
                                        temp_toggle.setChecked(false);

                                    } else if (sen.getCommandBuffer() < BUFFER) {
                                        //nismo uspel ugasnt ampak se bomo poskusal
                                        sen.addCommandBuffer();
                                    } else {
                                        //obupamo in vrnemo toast da nismo uspel prizgat senozrja
                                        //popravimo bufferlist na 0
                                        sen.setCommand(0);
                                        sen.setCommandBuffer(0);
                                        CharSequence text = getString(R.string.neuspeseno_ugasanje_senzorja) + sen.getIme();
                                        int duration = Toast.LENGTH_SHORT;

                                        Toast toast = Toast.makeText(MainActivity.this, text, duration);
                                        toast.show();
                                    }
                                } else {
                                    //nocmo nc nardit nas ne zanima
                                }

                            } else if (vrednost_status_buffer == BUFFER) {
                                //bli ofline tokrat da recemo da smo ofline
                                sen.setCommand(0);
                                sen.setCommandBuffer(0);
                                sen.setStatusBuffer(0);
                                //dodal bomo ofline znak
//                                ImageView temp_image = relativeLayout.findViewWithTag("status");
//                                temp_image.setBackgroundResource(R.drawable.ic_signal_wifi_off_black_24dp);

                            } else {
                                //nismo bli online pustimo pri meru

                            }
                        }
                    } else {
                        int index = config.getIdSenzor().indexOf(id);
                        final Senzor sen = config.getSenzorji().get(index);
                        Pair<Boolean, Boolean> online = sen.SenzorGetOnline();
                        int command = sen.getCommand();
                        if (online.first.equals(true)) {
                            Log.d("run_del", "senzor: " + String.valueOf(id) + " je online");
                            //smo online
                            sen.setStatusBuffer(1);
                            //StatusBufferList.set(index, 1);
                            //pogledamo ce mormo przgat ali ugasnt senzor
                            if (command == 1 && online.second.equals(false)) {
                                Log.d("run_del", "hocmo prizgat, ni prizgan");
                                //hocmo przgat senozor, trenutno je ugasnen
                                boolean uspeli = sen.SenzorPrizgiUgasni(true);
                                if (uspeli) {
                                    //uspesno smo izvedli prizig
                                    //popravmo da ne rabmo vec przgat
                                    sen.setCommand(0);
                                    sen.setCommandBuffer(0);

                                } else if (sen.getCommandBuffer() < BUFFER) {
                                    //nismo uspel prizgat ampak se bomo poskusal
                                    sen.addCommandBuffer();
                                } else {
                                    //obupamo in vrnemo toast da nismo uspel prizgat senozrja
                                    //popravimo bufferlist na 0
                                    //popravimo comande na 0
                                    sen.setCommand(0);
                                    sen.setCommandBuffer(0);
                                    CharSequence text = getString(R.string.neuspesen_prizig_senzorja) + sen.getIme();
                                    int duration = Toast.LENGTH_SHORT;

                                    Toast toast = Toast.makeText(MainActivity.this, text, duration);
                                    toast.show();
                                }

                            } else if (command == -1 && online.second.equals(true)) {
                                //hocmo ugasnit senozor, je trenutno przgan
                                Log.d("run_del", "hocmo ugasnt, je prizgan");
                                boolean uspeli = sen.SenzorPrizgiUgasni(false);
                                if (uspeli) {
                                    //uspesno smo ugasnili
                                    sen.setCommand(0);
                                    sen.setCommandBuffer(0);
                                    //spremenimo barvo gumba

                                } else if (sen.getCommandBuffer() < BUFFER) {
                                    //nismo uspel ugasnt ampak se bomo poskusal
                                    sen.addCommandBuffer();
                                } else {
                                    //obupamo in vrnemo toast da nismo uspel prizgat senozrja
                                    //popravimo bufferlist na 0
                                    sen.setCommand(0);
                                    sen.setCommandBuffer(0);
                                    CharSequence text = getString(R.string.neuspeseno_ugasanje_senzorja) + sen.getIme();
                                    int duration = Toast.LENGTH_SHORT;

                                    Toast toast = Toast.makeText(MainActivity.this, text, duration);
                                    toast.show();
                                }
                            } else if (command == -1 && online.second.equals(false)) {
                                //hocmo ugasnt senzor, pa je ze ugasnen
                                Log.d("run_del", "hocmo ugasnt, ni prizgan");

                                //pocistt mormo samo buffer
                                //pocistmo buffer
                                //popravmo da ne rabmo vec ugasnt
                                sen.setCommand(0);
                                sen.setCommandBuffer(0);
                            } else if (command == 1 && online.second.equals(true)) {
                                //hocmo przgat senzor, pa je ze przgan
                                Log.d("run_del", "hocmo prizgat, je prizgan");

                                //pocistmo buffer
                                //popravmo da ne rabmo vec przgat
                                sen.setCommand(0);
                                sen.setCommandBuffer(0);

                            } else {
                                //nocmo nardit nc, torej samo nastavmo pravo barvo gumba

                            }
                        } else {
                            Log.d("run_del", "senzor: " + String.valueOf(id) + " ni online");
                            //nismo online
                            //pogledamo ce smo prej bli online pa je sam lag potem ohranmo status online
                            int vrednost_status_buffer = sen.getStatusBuffer();
                            if (vrednost_status_buffer < BUFFER && vrednost_status_buffer > 0) {
                                //bli smo online
                                //povecamo buffer za +1 in pustimo online status
                                //StatusBufferList.set(index, vrednost_status_buffer + 1);
                                sen.addStatusBuffer();

                                //ce mormo kej nardit z switchom se vseeno probamo nardit
                                if (command == 1) {
                                    //hocmo przgat senozor
                                    boolean uspeli = sen.SenzorPrizgiUgasni(true);
                                    if (uspeli) {
                                        //uspesno smo izvedli prizig
                                        //ne rabmo vec przgat
                                        sen.setCommand(0);
                                        sen.setCommandBuffer(0);

                                    } else if (sen.getCommandBuffer() < BUFFER) {
                                        //nismo uspel prizgat ampak se bomo poskusal
                                        sen.addCommandBuffer();
                                    } else {
                                        //obupamo in vrnemo toast da nismo uspel prizgat senozrja
                                        //popravimo bufferlist na 0
                                        //ker smo obupal popucamo buffer
                                        sen.setCommand(0);
                                        sen.setCommandBuffer(0);
                                        CharSequence text = getString(R.string.neuspesen_prizig_senzorja) + sen.getIme();
                                        int duration = Toast.LENGTH_SHORT;

                                        Toast toast = Toast.makeText(MainActivity.this, text, duration);
                                        toast.show();
                                    }
                                } else if (command == -1) {
                                    //hocmo ugasnit senozor
                                    boolean uspeli = sen.SenzorPrizgiUgasni(false);
                                    if (uspeli) {
                                        //uspesno smo ugasnili
                                        sen.setCommand(0);
                                        sen.setCommandBuffer(0);

                                    } else if (sen.getCommandBuffer() < BUFFER) {
                                        //nismo uspel ugasnt ampak se bomo poskusal
                                        //CommandBufferList.set(index, CommandBufferList.get(index) + 1);
                                        sen.addCommandBuffer();
                                    } else {
                                        //obupamo in vrnemo toast da nismo uspel prizgat senozrja
                                        //popravimo bufferlist na 0
                                        sen.setCommand(0);
                                        sen.setCommandBuffer(0);
                                        CharSequence text = getString(R.string.neuspeseno_ugasanje_senzorja) + sen.getIme();
                                        int duration = Toast.LENGTH_SHORT;

                                        Toast toast = Toast.makeText(MainActivity.this, text, duration);
                                        toast.show();
                                    }
                                } else {
                                    //nocmo nc nardit nas ne zanima
                                }

                            } else if (vrednost_status_buffer == BUFFER) {
                                //bli ofline tokrat da recemo da smo ofline
                                sen.setCommand(0);
                                sen.setCommandBuffer(0);
                                sen.setStatusBuffer(0);

                            } else {
                                //nismo bli online pustimo pri meru

                            }
                        }
                    }
                }
                for (int id : config.getIdGrup()) {
                    Log.d("run_del", "grupa: " + String.valueOf(id));
                    //grupa
                    int index = config.getIdGrup().indexOf(id);
                    final Grupa grupa = config.getGrupe().get(index);
                    //pogledamo koliko senzorjev v grupi je online ter pogledamo koliko senzorjev v grupi je prizganih
                    int st_online = 0;
                    int st_prizganih = 0;
                    for (Senzor sen : grupa.getSenzorji()) {
                        int index_senzorja = config.getSenzorji().indexOf(sen);
                        if (sen.getStatusBuffer() > 0 && sen.getStatusBuffer() < BUFFER) {
                            //ce je 1 online, ce je manjsi od buferja ga se smatramo da je online
                            st_online += 1;
                        }
                        if (sen.getPrizgan()) {
                            st_prizganih += 1;
                        }
                    }
                    RelativeLayout relativeLayout = gumbi_oblika.get(config.getVrstni_red().indexOf(id));

                    if (st_online > 0) {
                        //recemo da je grupa online
//                        ImageView temp_image = relativeLayout.findViewWithTag("status");
//                        temp_image.setBackgroundResource(R.drawable.ic_wifi_black_24dp);
                        //pobarvamo grupo
                        View view = relativeLayout.findViewWithTag("barva");
                        view.setBackground(create_gd(grupa.getBarva()));
                        if (st_prizganih == 0) {
                            Log.d("run_del", "grupa: " + String.valueOf(id) + " st_ptizganih je 0");
                            //pobarvamo gumb na sivo in nastavimo toggle button na off
                            ToggleButton temp_toggle = relativeLayout.findViewWithTag(String.valueOf(id) + ",onoff");
                            temp_toggle.setChecked(false);
                            temp_toggle.setBackgroundResource(R.drawable.gumb);
                        } else if (st_prizganih == grupa.getStevilo_senzorjev() && grupa.getStevilo_senzorjev() != 0) {
                            //pobarvamo gumb na zeleno in nastavimo toggle button na on
                            ToggleButton temp_toggle = relativeLayout.findViewWithTag(String.valueOf(id) + ",onoff");
                            temp_toggle.setChecked(true);
                            temp_toggle.setBackgroundResource(R.drawable.gumb_zelen);
                        } else if (st_prizganih > 0 && grupa.getStevilo_senzorjev() != 0) {
                            //pobarvamo senzor na modro in nastavimo toggle button na on
                            ToggleButton temp_toggle = relativeLayout.findViewWithTag(String.valueOf(id) + ",onoff");
                            temp_toggle.setChecked(true);
                            temp_toggle.setBackgroundResource(R.drawable.gumb_moder);
                        }
                    } else {
                        //recemo da je grupa offline
//                        ImageView temp_image = relativeLayout.findViewWithTag("status");
//                        temp_image.setBackgroundResource(R.drawable.ic_signal_wifi_off_black_24dp);
                        //pobarvamo grupo
                        View view = relativeLayout.findViewWithTag("barva");
                        view.setBackground(create_gd(Color.GRAY));
                        //recemo da ni noben prizgan ker ne vemo
                        st_prizganih = 0;

                        ToggleButton temp_toggle = relativeLayout.findViewWithTag(String.valueOf(id) + ",onoff");
                        temp_toggle.setChecked(false);
                        temp_toggle.setBackgroundResource(R.drawable.gumb);


                    }

                    //damo napise na grupo
                    final String opis_grupe = "Online:"+ String.valueOf(st_online)+"/" + String.valueOf(grupa.getStevilo_senzorjev()) + ", On:" + String.valueOf(st_prizganih);

                    final TextView text_grupe = relativeLayout.findViewWithTag("stevila");
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            text_grupe.setText(opis_grupe);
                        }
                    });


                }
                Log.i("run_del", "koncal zanko");
                SystemClock.sleep(SLEEP_TIME);

            }
            return;
        }
    };

    public void run_main() {
        Thread thread = new Thread(pozeni_check);
        thread.start();
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

    private GradientDrawable create_gd(int Color) {
        int barva_temna = darken(Color, 0.2f);
        int barva_svetla = enlight(Color, 0.2f);
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setColors(new int[]{
                barva_temna,
                Color,
                barva_svetla,
                Color,
                barva_temna

        });
        gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gd.setOrientation(GradientDrawable.Orientation.TL_BR);
        gd.setCornerRadius(30);
        return gd;
    }

    private int enlight(int color, float amount) {
        //Vzame barvo in parameter amount, ki mora bit med 0,1 in iz dane barve za
        //procente amount naredi svetlejso barvo
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = Math.min(1.0f, amount + hsv[2]);
        return Color.HSVToColor(hsv);
    }

    private int darken(int color, float amount) {
        //Vzame barvo in parameter amount, ki mora bit med 0,1 in iz dane barve za
        //procente amount naredi temnejso barvo
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = Math.max(.0f, hsv[2] - amount);
        return Color.HSVToColor(hsv);
    }


    private void exportFile(){
        if (!checkPermissionForWriteExtertalStorage()){
            try {
                requestPermissionForWriteExtertalStorage();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            shranifile();
        }
    }

    private void shranifile() {
        final String m_Text = "devices"; //default ime je devices
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Name of the file");

// Set up the input
        EditText input1 = new EditText(this);
        input1.setText(m_Text);
        final EditText input = input1;
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String ime = input.getText().toString();
                if (!ime.equals("")){
                    boolean uspeli = config.writeConfigurationsValues(getExternalFilesDir(null).getPath() + "/" + ime + ".txt");
                    if(uspeli){
                        Context context = getApplicationContext();
                        CharSequence text = getString(R.string.sucExport);
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                    }
                    else{
                        Context context = getApplicationContext();
                        CharSequence text = getString(R.string.failExport);
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                }
                else {

                    Context context = getApplicationContext();
                    CharSequence text = "Name can't be empty!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    private void importFile(){
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.STORAGE_DIR+"/sdcard");
        properties.error_dir = new File(DialogConfigs.STORAGE_DIR + "/sdcard");
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;
        FilePickerDialog dialog = new FilePickerDialog(MainActivity.this,properties);
        dialog.setTitle("Select a File");
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                //files is the array of the paths of files selected by the Application User.
                String new_file = files[0];
                Log.d("import",new_file);
                Log.d("import", String.valueOf(files));
                config.getConfigurationValue(new_file);
                config.writeConfigurationsValues(FILEPATH);
                Log.d("import","importal");
                Intent refresh = new Intent(MainActivity.this, MainActivity.class);
                koncajLoop = true; //zakljuci main loop
                finish();
                startActivity(refresh);
                Log.d("import","loudal");
            }
        });
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //naredimo meni in klicemo primeren layout iz folderja menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // obravnavamo izbrane primere iz menija
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.dodaj_senzor:
                        intent = new Intent(MainActivity.this, AddSenzorActivity.class);
                        koncajLoop = true; //zakljuci main loop
                        startActivity(intent);
                        MainActivity.this.finish();
                        return true;
                    case R.id.dodaj_grupo:
                        intent = new Intent(MainActivity.this, AddGroupActivity.class);
                        koncajLoop = true; //zakljuci main loop
                        startActivity(intent);
                        MainActivity.this.finish();
                        return true;
                    case R.id.sort:
                        intent = new Intent(MainActivity.this, SortActivity.class);
                        koncajLoop = true; //zakljuci main loop
                        startActivity(intent);
                        MainActivity.this.finish();
                        return true;
                    case R.id.exportD:
                        exportFile();
                        return true;
                    case R.id.importD:
                        importFile();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // obravnavamo izbrane primere iz menija
        Intent intent;
        switch (item.getItemId()) {
            case R.id.dodaj_senzor:
                intent = new Intent(this, AddSenzorActivity.class);
                koncajLoop = true; //zakljuci main loop
                startActivity(intent);
                this.finish();
                return true;
            case R.id.dodaj_grupo:
                intent = new Intent(this, AddGroupActivity.class);
                koncajLoop = true; //zakljuci main loop
                startActivity(intent);
                this.finish();
                return true;
            case R.id.sort:
                intent = new Intent(this, SortActivity.class);
                koncajLoop = true; //zakljuci main loop
                startActivity(intent);
                this.finish();
                return true;
            case R.id.exportD:
                exportFile();
                return true;
            case R.id.importD:
                importFile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        Log.i("onClick", String.valueOf(v.getTag()));
        Intent intent;

        //tag gumbov je id,OnOff za toggle buttn ali id,vec za gumb ki odpre nov activity
        String str = v.getTag().toString();
        String[] temp = str.split(",");
        int id = Integer.valueOf(temp[0]);
        String tip = temp[1];
        if (tip.equals("onoff")) {
            //mamo toggle button
            ToggleButton gumb_onoff = (ToggleButton) v;
            boolean stanje = gumb_onoff.isChecked();
            if (stanje) {
                //poklici metodo za prizgat ker se je ob pritisku gumba iz off spremenilo na on in smo sedaj v on
                Log.i("onClick", "prizgi");
                if (id > 0) {
                    //senzor
                    Senzor sen = config.getSenzorji().get(config.getIdSenzor().indexOf(id));
                    sen.setCommand(1);
                    //damo na buffer

                } else {
                    //grupa
                    Log.i("run_del", "prizgi grupo");
                    Grupa grupa = config.getGrupe().get(config.getIdGrup().indexOf(id));
                    for (Senzor sen : grupa.getSenzorji()) {
                        Log.i("onClick", String.valueOf(sen));
                        sen.setCommand(1);
                    }
                    Log.i("run_del", "prizgi grupo koncal");
                }
            } else {
                //poklici metodo za ugasnt
                Log.i("onClick", "ugasni");
                if (id > 0) {
                    //senzor
                    Senzor sen = config.getSenzorji().get(config.getIdSenzor().indexOf(id));
                    sen.setCommand(-1);
                } else {
                    //grupa
                    Log.i("run_del", "ugasni grupo");
                    Grupa grupa = config.getGrupe().get(config.getIdGrup().indexOf(id));
                    for (Senzor sen : grupa.getSenzorji()) {
                        sen.setCommand(-1);
                    }
                    Log.i("run_del", "ugasni grupo koncal");
                }
            }
        } else if (tip.equals("vec")) {
            //mamo gumb za vec opcij
            if (id < 0) {
                //gumb bil prtisnen na grupi
                intent = new Intent(this, GroupViewActivity.class);
                //nardimo nov bundle da loh not damo id grupe
                Bundle bundle = new Bundle();
                bundle.putInt("id", id);
                intent.putExtras(bundle);
                koncajLoop = true; //zakljuci main loop
                startActivity(intent);
                this.finish();
            } else {
                //gumb prtisnen na senzorju
                intent = new Intent(this, SensorViewActivity.class);
                //nardimo nov bundle da loh not damo id senzorja
                Bundle bundle = new Bundle();
                bundle.putInt("id", id);
                bundle.putInt("id_grupe", 0);
                intent.putExtras(bundle);
                koncajLoop = true; //zakljuci main loop
                startActivity(intent);
                this.finish();
            }
        }

    }
}
