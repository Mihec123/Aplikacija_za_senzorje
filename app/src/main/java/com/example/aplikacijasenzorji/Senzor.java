package com.example.aplikacijasenzorji;

import android.graphics.Color;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Senzor {

    private int id;
    private String ime;
    private String ip;
    private String zeton;
    private int stevilo_podsenzorjev = 1;
    private boolean prikazi_vlago = false;
    private int barva = Color.GREEN;
    private List<Float> temperatura;
    private int responseCode;
    private boolean vsaj_ena_temeratura = false;
    private float SLABATEMP = -99999;



    private Runnable runnableCheckConnection = new Runnable(){
        /*
        Opis:
        naredimo nov objekt Runnable, ki ga potem lahko poklicemo v novem threadu
        runnableCheckConnection vzame ip in zeton ter nastavi response code, na to kar dobi glede na te podatke
        Vhod:/
        Izhod:/
        */
        public void run() {
            String url = "http://" + ip + "/api/relay/0?apikey=" + zeton;
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                responseCode = connection.getResponseCode();
                connection.disconnect();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        }
    };


    public boolean SensorCheckConnection(){
        /*
        Opis:
        Funkcija SensorCheckConnection pozene runnableCheckConnection na novem threadu, ter pocaka da se zakljuci
        in ce je odgovor 200 vrne true v nasprotnem primeru pa false
        Vhod:/
        Izhod:
            -boolean, dobljen glede na response code
        */
        Thread thread = new Thread(runnableCheckConnection);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (this.responseCode == 200) {
            return true;
        }
        else {
            return false;
        }

    }

    private Runnable runnableCheckTemperature = new Runnable(){
        /*
        Opis:
        naredimo nov objekt Runnable, ki ga potem lahko poklicemo v novem threadu
        runnableCheckTemperature vzame ip, zeton in stevilo_podsenzorjev ter nastavi dobljene temperature, na to kar dobi glede na te podatke
        Vhod:/
        Izhod:/
        */
        public void run() {
            temperatura = new ArrayList<Float>();
            vsaj_ena_temeratura = false;
            for(int i = 1;i <= stevilo_podsenzorjev;i++) {

                String url;
                if(stevilo_podsenzorjev == 1){
                    url = "http://" + ip + "/api/temperature?apikey=" + zeton;
                }
                else {
                    url = "http://" + ip + "/api/temperature/" + String.valueOf(i) + "?apikey=" + zeton;
                }
                Log.d("INTERNET", url);
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    responseCode = connection.getResponseCode();
                    InputStream inputStream;
                    if(responseCode == 200) {
                        inputStream = connection.getInputStream();
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                        inputStream));

                        StringBuilder response = new StringBuilder();
                        String currentLine;

                        while ((currentLine = in.readLine()) != null)
                            response.append(currentLine);
                        Log.d("INTERNET", "temperatura: " + String.valueOf(response));

                        in.close();
                        temperatura.add(Float.valueOf(String.valueOf(response)));
                        vsaj_ena_temeratura = true;
                    }
                    else{
                        temperatura.add(SLABATEMP);
                    }
                    connection.disconnect();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    };

    public boolean SensorCheckTemerature(){
        /*
        Opis:
        Funkcija SensorCheckTemerature pozene runnableCheckTemperature na novem threadu, ter pocaka da se zakljuci
        in ce je odgovor 200 vrne true v nasprotnem primeru pa false
        Vhod:/
        Izhod:
            -boolean, dobljen glede na response code
        */
        Thread thread = new Thread(runnableCheckTemperature);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (vsaj_ena_temeratura) {
            return true;
        }
        else {
            return false;
        }

    }


    public boolean isPrikazi_vlago() {
        return prikazi_vlago;
    }

    public void setPrikazi_vlago(boolean prikazi_vlago) {
        this.prikazi_vlago = prikazi_vlago;
    }

    public int getStevilo_podsenzorjev() {
        return stevilo_podsenzorjev;
    }

    public void setStevilo_podsenzorjev(int stevilo_podsenzorjev) {
        this.stevilo_podsenzorjev = stevilo_podsenzorjev;
    }

    public String getZeton() {
        return zeton;
    }

    public void setZeton(String zeton) {
        this.zeton = zeton;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBarva() {
        return barva;
    }

    public void setBarva(int barva) {
        this.barva = barva;
    }

    public List<Float> getTemperatura() {
        return temperatura;
    }
}
