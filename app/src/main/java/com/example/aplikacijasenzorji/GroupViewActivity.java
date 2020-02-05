package com.example.aplikacijasenzorji;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class GroupViewActivity extends AppCompatActivity implements View.OnClickListener {

    String filename = "devices.txt";
    Config config = new Config();
    int id_grupe;
    Grupa grupa;
    String FILEPATH;
    int BUFFER = 3;
    private float SLABATEMP = -99999;
    private List<RelativeLayout> gumbi_oblika = new ArrayList<>(); //sem bomo shranjevali toggle butne da jim bomo lahko menjal ozadja
    private boolean koncajLoop = false;
    private int SLEEP_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_view);

        //dobimo id grupe
        Bundle bundle = getIntent().getExtras();
        id_grupe = bundle.getInt("id");

        FILEPATH = this.getFilesDir() + "/" + filename;
        config.getConfigurationValue(FILEPATH);


        /////////////////////////////////////////////////////////////////////////////////

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        final float scale = outMetrics.density;
        //scale bomo rabl da px pretvormo v dp
        /////////////////////////////////////////////////////////////////////

        //dobimo vn glavne gumbe

        FloatingActionButton brisi = (FloatingActionButton) findViewById(R.id.fb_delete);
        brisi.setOnClickListener(GroupViewActivity.this);
        brisi.setTag(0);

        FloatingActionButton edit = (FloatingActionButton) findViewById(R.id.fb_edit);
        edit.setOnClickListener(GroupViewActivity.this);
        edit.setTag(1);


        //zacnemo postavljat gumbe
        LinearLayout okno = (LinearLayout) findViewById(R.id.lin_scroll);
        Gumb_Creation gumbi = new Gumb_Creation();

        //nastavimo grupo
        grupa = config.getGrupe().get(config.getIdGrup().indexOf(id_grupe));
        RelativeLayout temp;
        for (Senzor sen : grupa.getSenzorji()) {
            temp = gumbi.OblikaGumbaSenzor(sen.getIme(), Color.GRAY, false, false, sen.getId(), scale, GroupViewActivity.this, this);
            okno.addView(temp);
            gumbi_oblika.add(temp);
        }


        run_main();
    }


    private Runnable pozeni_check = new Runnable() {
        public void run() {
            while (!koncajLoop) {
                Log.i("run_del", "zacel zanko");
                for (Senzor sen : grupa.getSenzorji()) {
                    //senzor
                    //index pozicije senzorja
                    int index = grupa.getSenzorji().indexOf(sen);
                    //senzor je na glavnem screenu, mormo barvat pa popravljat gumbe
                    Pair<Boolean, Boolean> online = sen.SenzorGetOnline();
                    RelativeLayout relativeLayout = gumbi_oblika.get(index);
                    int command = sen.getCommand();
                    if (online.first.equals(true)) {
                        Log.d("run_del", "senzor: " + String.valueOf(sen.getId()) + " je online");
                        //smo online
                        ImageView temp_image = relativeLayout.findViewWithTag("status");
                        temp_image.setBackgroundResource(R.drawable.ic_wifi_black_24dp);

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
                            temp_prvega_senzorja_text = String.valueOf(temp_prvega_senzorja);
                        }
                        final String temp_prvega_senzorja_text_final = temp_prvega_senzorja_text;

                        //ui updati morjo bit na main threadu zato kle text nastavmo z main threadom

                        GroupViewActivity.this.runOnUiThread(new Runnable() {
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
                                ToggleButton temp_toggle = relativeLayout.findViewWithTag(String.valueOf(sen.getId()) + ",onoff");
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

                                Toast toast = Toast.makeText(GroupViewActivity.this, text, duration);
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
                                ToggleButton temp_toggle = relativeLayout.findViewWithTag(String.valueOf(sen.getId()) + ",onoff");
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

                                Toast toast = Toast.makeText(GroupViewActivity.this, text, duration);
                                toast.show();
                            }
                        } else if (command == -1 && online.second.equals(false)) {
                            //hocmo ugasnt senzor, pa je ze ugasnen
                            Log.d("run_del", "hocmo ugasnt, ni prizgan");

                            //pocistt mormo samo buffer pa gumbe pobarvat
                            ToggleButton temp_toggle = relativeLayout.findViewWithTag(String.valueOf(sen.getId()) + ",onoff");
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
                            ToggleButton temp_toggle = relativeLayout.findViewWithTag(String.valueOf(sen.getId()) + ",onoff");
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
                                ToggleButton temp_toggle = relativeLayout.findViewWithTag(String.valueOf(sen.getId()) + ",onoff");
                                temp_toggle.setBackgroundResource(R.drawable.gumb_zelen);
                                temp_toggle.setChecked(true);
                            } else {
                                Log.d("run_del", "nas ne zanima je ugasnjen");
                                //more bit pobarvan na sivo
                                ToggleButton temp_toggle = relativeLayout.findViewWithTag(String.valueOf(sen.getId()) + ",onoff");
                                temp_toggle.setBackgroundResource(R.drawable.gumb);
                                temp_toggle.setChecked(false);
                            }

                        }
                    }
                    else {
                        Log.d("run_del", "senzor: " + String.valueOf(sen.getId()) + " ni online");
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
                                    ToggleButton temp_toggle = relativeLayout.findViewWithTag(String.valueOf(sen.getId()) + ",onoff");
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

                                    Toast toast = Toast.makeText(GroupViewActivity.this, text, duration);
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
                                    ToggleButton temp_toggle = relativeLayout.findViewWithTag(String.valueOf(sen.getId()) + ",onoff");
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

                                    Toast toast = Toast.makeText(GroupViewActivity.this, text, duration);
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
                            ImageView temp_image = relativeLayout.findViewWithTag("status");
                            temp_image.setBackgroundResource(R.drawable.ic_signal_wifi_off_black_24dp);

                        } else {
                            //nismo bli online pustimo pri meru

                        }
                    }
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

    @Override
    public void onClick(View view) {
        String str = view.getTag().toString();
        Log.i("onClick", str);
        if (str.equals("0")) {
            //prtisnl smo delete
            //zbrisat mormo celo grupo z vsemi senzorji
            //nardimo dialog ce je odgovor ja zbrisemo sicer ignoriramo klik
            AlertDialog.Builder alert = new AlertDialog.Builder(GroupViewActivity.this);
            alert.setTitle(getString(R.string.delete));
            alert.setMessage(getString(R.string.vprasanje_brisanje_grupe));
            alert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {


                    for (Senzor sen : grupa.getSenzorji()) {
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
                    koncajLoop = true;

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
        } else if (str.equals("1")) {
            //prtisnl smo edit
            Intent intent = new Intent(GroupViewActivity.this, EditGroupActivity.class);
            //nardimo nov bundle da loh not damo id senzorja
            Bundle bundle = new Bundle();
            bundle.putInt("id", id_grupe);
            intent.putExtras(bundle);
            koncajLoop = true;
            startActivity(intent);
            this.finish();

        } else {
            Log.i("onClick", "kliknl senzor");
            //prtisnl smo na nek senzor
            String[] temp = str.split(",");
            int id = Integer.valueOf(temp[0]);
            String tip = temp[1];
            if (tip.equals("onoff")) {
                //mamo toggle button
                ToggleButton gumb_onoff = (ToggleButton) view;
                boolean stanje = gumb_onoff.isChecked();
                if (stanje) {
                    //poklici metodo za prizgat ker se je ob pritisku gumba iz off spremenilo na on in smo sedaj v on
                    Log.i("onClick", "prizgi");
                    //senzor
                    Senzor sen = config.getSenzorji().get(config.getIdSenzor().indexOf(id));
                    sen.setCommand(1);
                    //damo na buffer
                } else {
                    //poklici metodo za ugasnt
                    Log.i("onClick", "ugasni");
                    Senzor sen = config.getSenzorji().get(config.getIdSenzor().indexOf(id));
                    sen.setCommand(-1);
                }
            } else if (tip.equals("vec")) {
                //mamo gumb za vec opcij
                //gumb prtisnen na senzorju
                Intent intent = new Intent(this, SensorViewActivity.class);
                //nardimo nov bundle da loh not damo id senzorja
                Bundle bundle = new Bundle();
                bundle.putInt("id", id);
                bundle.putInt("id_grupe", id_grupe);
                intent.putExtras(bundle);
                koncajLoop = true;
                startActivity(intent);
                this.finish();
            }
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        Intent refresh = new Intent(this, MainActivity.class);
        koncajLoop = true;
        startActivity(refresh);
        this.finish();
        return super.onKeyDown(keyCode, event);
    }
}
