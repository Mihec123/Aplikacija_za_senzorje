package com.example.aplikacijasenzorji;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import androidx.annotation.RequiresApi;

public final class Config {

    private static final String TAG = "config_class";
    private static final String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/config.txt";
    private static Config sInstance = null;

    private List<Grupa> grupe = new ArrayList();
    private List<Senzor> senzorji = new ArrayList();
    private List<Integer> idGrup = new ArrayList<>();
    private List<Integer> idSenzor = new ArrayList<>();

    /**
     * Write configurations values boolean.
     *
     * @return the boolean
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean writeConfigurationsValues() {
        String tag="";

        Log.i(TAG, "zacenjamo pisanje");

        try (OutputStream output = new FileOutputStream(FILE_PATH)) {

            Properties prop = new Properties();

            int dolzinaGrup = grupe.size();
            int dolzinaSenzorjev = senzorji.size();

            prop.setProperty("stevilo_senzorjev", String.valueOf(dolzinaSenzorjev));
            prop.setProperty("stevilo_grup", String.valueOf(dolzinaGrup));

            //dodamo lastnosti grup
            for(int i= 0;i < dolzinaGrup; i++){
                Grupa grupa = grupe.get(i);
                //ime grupe
                tag = "Grupa"+String.valueOf(i)+"_ime";
                prop.setProperty(tag,grupa.getIme());

                //stevilo senzorjev v grupi
                tag = "Grupa"+String.valueOf(i)+"_stevilo_senzorjev";
                prop.setProperty(tag, String.valueOf(grupa.getStevilo_senzorjev()));

                //id grupe
                tag = "Grupa"+String.valueOf(i)+"_id";
                prop.setProperty(tag, String.valueOf(grupa.getId()));

                //id-iji senzorjev
                tag = "Grupa"+String.valueOf(i)+"_id-ji_senzorjev";
                String id = "";
                //gremo cez vse idje v grupi
                for(int j= 0;j<grupa.getStevilo_senzorjev();j++){
                    Senzor senzor = grupa.findSenzorByPosition(i);
                    id += String.valueOf(senzor.getId()) + ",";
                }
                prop.setProperty(tag, id);

            }

            //dodamo lastnosti senzorjev
            for(int i= 0;i < dolzinaSenzorjev; i++){
                Senzor senzor = senzorji.get(i);

                //ime senzorja
                tag = "Senzor"+String.valueOf(i)+"_ime";
                prop.setProperty(tag,senzor.getIme());

                //id senzorja
                tag = "Senzor"+String.valueOf(i)+"_id";
                prop.setProperty(tag, String.valueOf(senzor.getId()));

                //barva senzorja
                tag = "Senzor"+String.valueOf(i)+"_barva";
                prop.setProperty(tag, String.valueOf(senzor.getBarva()));

                //ip senzorja
                tag = "Senzor"+String.valueOf(i)+"_ip";
                prop.setProperty(tag, senzor.getIp());

                //token senzorja
                tag = "Senzor"+String.valueOf(i)+"_token";
                prop.setProperty(tag, senzor.getZeton());

                //stevilo temperaturnih senzorjev na enoti senzorja
                tag = "Senzor"+String.valueOf(i)+"_st_temp_senzorjev";
                prop.setProperty(tag, String.valueOf(senzor.getStevilo_podsenzorjev()));

                //prikazi vlago
                tag = "Senzor"+String.valueOf(i)+"_vlaga";
                prop.setProperty(tag, String.valueOf(senzor.isPrikazi_vlago()));


            }

            // save properties
            prop.store(output, null);

            Log.i(TAG, "Configuration stored  properties: " + prop);
            return true;
        } catch (IOException io) {
            io.printStackTrace();
            return false;
        }
    }

    /**
     * Get configuration value string.
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getConfigurationValue(){
        String value = "";
        String tag="";
        int poz;
        try (InputStream input = new FileInputStream(FILE_PATH)) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);
            int dolzinaSenzorjev = Integer.parseInt(prop.getProperty("stevilo_senzorjev"));
            int dolzinaGrup = Integer.parseInt(prop.getProperty("stevilo_grup"));

            //pogledamo za vsak senzor lastnosti in ustavrimo nov razred senzor za vsakega iz med njih
            for(int i= 0;i < dolzinaSenzorjev; i++) {
                Senzor senzor = new Senzor();

                //ime senzorja
                tag = "Senzor" + String.valueOf(i) + "_ime";
                value = prop.getProperty(tag);
                senzor.setIme(value);

                //id senzorja
                tag = "Senzor" + String.valueOf(i) + "_id";
                value = prop.getProperty(tag);
                senzor.setId(Integer.parseInt(value));

                //barva senzorja
                tag = "Senzor" + String.valueOf(i) + "_barva";
                value = prop.getProperty(tag);
                senzor.setBarva(Integer.parseInt(value));

                //ip senzorja
                tag = "Senzor" + String.valueOf(i) + "_ip";
                value = prop.getProperty(tag);
                senzor.setIp(value);

                //token senzorja
                tag = "Senzor" + String.valueOf(i) + "_token";
                value = prop.getProperty(tag);
                senzor.setZeton(value);

                //stevilo temperaturnih senzorjev na enoti senzorja
                tag = "Senzor" + String.valueOf(i) + "_st_temp_senzorjev";
                value = prop.getProperty(tag);
                senzor.setStevilo_podsenzorjev(Integer.parseInt(value));

                //prikazi vlago
                tag = "Senzor" + String.valueOf(i) + "_vlaga";
                value = prop.getProperty(tag);
                senzor.setPrikazi_vlago(Boolean.parseBoolean(value));
                addSenzorji(senzor);
            }

            Log.d(TAG, "smo pr grupi");
            //se za grupe
            for(int i= 0;i < dolzinaGrup; i++){
                Grupa grupa = new Grupa();

                //ime grupe
                tag = "Grupa"+String.valueOf(i)+"_ime";
                value = prop.getProperty(tag);
                grupa.setIme(value);

                //stevilo senzorjev v grupi ne rabmo gledat se bo samo nastavl ko bomo dodajali senzorje

                //id grupe
                tag = "Grupa"+String.valueOf(i)+"_id";
                value = prop.getProperty(tag);
                grupa.setId(Integer.parseInt(value));

                //id-iji senzorjev
                tag = "Grupa"+String.valueOf(i)+"_id-ji_senzorjev";
                value = prop.getProperty(tag);
                String[] idji = value.split(",");


                Log.d(TAG, String.valueOf(idji));
                for(int j=0; j< idji.length;j++){
                    //pozicija senzorja v listu
                    poz = idSenzor.indexOf(Integer.parseInt(idji[j]));
                    //dodamo ta senzor v grupo
                    grupa.addSenzor(senzorji.get(poz));
                }

                addGrupe(grupa);

            }
            Log.i(TAG, "Configuration stored  properties value: " + prop);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void addGrupe(Grupa g){
        grupe.add(g);
        idGrup.add(g.getId());
    }

    public void addSenzorji(Senzor s){
        senzorji.add(s);
        idSenzor.add(s.getId());
    }

    public List<Grupa> getGrupe() {
        return grupe;
    }

    public List<Senzor> getSenzorji() {
        return senzorji;
    }

    public List<Integer> getIdGrup() {
        return idGrup;
    }

    public List<Integer> getIdSenzor() {
        return idSenzor;
    }
}
