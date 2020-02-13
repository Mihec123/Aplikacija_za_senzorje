package com.example.aplikacijasenzorji;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import androidx.annotation.RequiresApi;

public final class Config{

    private static final String TAG = "config_class";
    private static Config sInstance = null;

    private List<Grupa> grupe = new ArrayList();
    private List<Senzor> senzorji = new ArrayList();
    private List<Integer> idGrup = new ArrayList<>();
    private List<Integer> idSenzor = new ArrayList<>();
    private List<Integer> vrstni_red = new ArrayList<>();

    /**
     * Write configurations values boolean.
     *
     * @return the boolean
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean writeConfigurationsValues(String filepath) {
        String tag="";
        String id = "";

        Log.d(TAG, "zacenjamo pisanje");


        try (OutputStream output = new FileOutputStream(filepath)) {
            Log.d(TAG, "probamo pisat");

            Properties prop = new Properties();

            int dolzinaGrup = grupe.size();
            int dolzinaSenzorjev = senzorji.size();

            prop.setProperty("stevilo_senzorjev", String.valueOf(dolzinaSenzorjev));
            prop.setProperty("stevilo_grup", String.valueOf(dolzinaGrup));
            prop.setProperty("vrstni_red", TextUtils.join(",,,",vrstni_red));

            //dodamo lastnosti grup
            for(int i= 0;i < dolzinaGrup; i++){
                Grupa grupa = grupe.get(i);
                //ime grupe
                tag = "Grupa"+String.valueOf(i)+"_ime";
                prop.setProperty(tag,grupa.getIme());

                //stevilo senzorjev v grupi
                tag = "Grupa"+String.valueOf(i)+"_stevilo_senzorjev";
                prop.setProperty(tag, String.valueOf(grupa.getStevilo_senzorjev()));

                //barva grupe
                tag = "Grupa"+String.valueOf(i)+"_barva";
                prop.setProperty(tag, String.valueOf(grupa.getBarva()));

                //id grupe
                tag = "Grupa"+String.valueOf(i)+"_id";
                prop.setProperty(tag, String.valueOf(grupa.getId()));

                //id-iji senzorjev
                tag = "Grupa"+String.valueOf(i)+"_id-ji_senzorjev";
                //gremo cez vse idje v grupi
                id = "";
                for(int j= 0;j<grupa.getStevilo_senzorjev();j++){
                    Senzor senzor = grupa.findSenzorByPosition(j);
                    id += String.valueOf(senzor.getId()) + ",,,";
                    Log.d(TAG, id);
                }
                prop.setProperty(tag, id);

            }

            Log.d(TAG, "pohendlal grupe");

            //dodamo lastnosti senzorjev
            for(int i= 0;i < dolzinaSenzorjev; i++){
                Log.d(TAG, "senzorpisemo");
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

                Log.d(TAG, "pred imeni temp");
                //dodamo imena podsenzorjev v seznam

                tag = "Senzor"+String.valueOf(i)+"_temp_senzorji_imena";
                List<String> seznam = senzor.getImena_temperaturnih_senzorjev();
                id = "";
                for(int j= 0;j<senzor.getStevilo_podsenzorjev();j++){
                    id += seznam.get(j) + ",,,";

                    Log.d(TAG, "senzor nehal pisat");
                }
                prop.setProperty(tag, id);


            }

            // save properties
            prop.store(output, null);

            Log.i(TAG, "Configuration stored  properties: " + prop);
            return true;
        } catch (IOException io) {
            Log.d(TAG, String.valueOf(io));
            io.printStackTrace();
            return false;
        }
    }

    /**
     * Get configuration value string.
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getConfigurationValue(String filepath){
        String value = "";
        String tag="";
        int poz;
        try (InputStream input = new FileInputStream(filepath)) {

            Properties prop = new Properties();
            Log.i(TAG, "zacetek");

            // load a properties file
            prop.load(input);
            Log.i(TAG, "poloudal file");
            int dolzinaSenzorjev = Integer.parseInt(prop.getProperty("stevilo_senzorjev"));
            int dolzinaGrup = Integer.parseInt(prop.getProperty("stevilo_grup"));
            Log.i(TAG, "prezvel dolzine");
            //loadamo vrstni red+ mal motoviljenja da pretvormo v int vrednosti pa pravi tip lista
            Log.i(TAG, String.valueOf(prop.getProperty("vrstni_red").isEmpty()));
            if(!prop.getProperty("vrstni_red").isEmpty()){
            String[] temp = prop.getProperty("vrstni_red").split(",,,");
            List<String> temp1= Arrays.asList(temp);
            vrstni_red = StringtoInt(temp1);
            }

            //pogledamo za vsak senzor lastnosti in ustavrimo nov razred senzor za vsakega iz med njih
            for(int i= 0;i < dolzinaSenzorjev; i++) {
                Log.i(TAG, "senzor");
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

                //imena temperaturnih senzorjev
                tag = "Senzor"+String.valueOf(i)+"_temp_senzorji_imena";
                List<String> seznam;
                value = prop.getProperty(tag);
                String[] imena = value.split(",,,");
                for(int j= 0;j<senzor.getStevilo_podsenzorjev();j++){
                    senzor.addImena_temperaturnih_senzorjev(imena[j]);
                }

                addSenzorji(senzor);

            }

            //se za grupe
            for(int i= 0;i < dolzinaGrup; i++){
                Log.i(TAG, "grupa");
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

                //barva grupe
                tag = "Grupa"+String.valueOf(i)+"_barva";
                value = prop.getProperty(tag);
                grupa.setBarva(Integer.parseInt(value));

                tag = "Grupa"+String.valueOf(i)+"_stevilo_senzorjev";
                value = prop.getProperty(tag);
                int stevilo_senzorjev = Integer.parseInt(value);

                //id-iji senzorjev
                tag = "Grupa"+String.valueOf(i)+"_id-ji_senzorjev";
                value = prop.getProperty(tag);
                if (stevilo_senzorjev != 0){
                    Log.d(TAG, "test2");
                    String[] idji = value.split(",,,");
                    Log.d(TAG, String.valueOf(value));
                    Log.d(TAG, String.valueOf(idji));
                    Log.d(TAG, "test1");
                    Log.d(TAG, String.valueOf(idji.length));
                    for(int j=0; j< idji.length;j++){
                        //pozicija senzorja v listu
                        poz = idSenzor.indexOf(Integer.parseInt(idji[j]));
                        //dodamo ta senzor v grupo
                        grupa.addSenzor(senzorji.get(poz));
                    }
                }
                addGrupe(grupa);

            }
            Log.i(TAG, "Configuration stored  properties value: " + prop);
        } catch (IOException ex) {
            ex.printStackTrace();
            Log.i(TAG, String.valueOf(ex));
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

    public List<Integer> getVrstni_red() {
        return vrstni_red;
    }
    public void setVrstni_red(List<Integer> seznam) {
        this.vrstni_red = seznam;
    }
    public void addVrstni_red(Integer id) {
        this.vrstni_red.add(id);
    }
    public void addVrstni_redPosition(Integer id,Integer pozicija) {
        this.vrstni_red.add(pozicija,id);

    }
    private List<Integer> StringtoInt(List<String> stringi){
        List<Integer> seznam = new ArrayList<>();
        for(int j=0;j<stringi.size();j++){
            seznam.add(Integer.parseInt(stringi.get(j)));
        }
        return seznam;
    }
}
