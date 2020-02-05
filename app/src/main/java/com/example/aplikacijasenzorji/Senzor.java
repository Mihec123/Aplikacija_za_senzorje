package com.example.aplikacijasenzorji;

import android.graphics.Color;
import android.util.Log;
import android.util.Pair;

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
    private List<String> imena_temperaturnih_senzorjev = new ArrayList<>();
    private float temperatura_prvi;
    private boolean online = false;
    private boolean prizgan = false;
    private int StatusBuffer = 0; //ce je 0 se ni bil online, ce je 1 je online, ce je n n-1 zaporednih ni bil online
    private int CommandBuffer = 0; //ce je 0 ni nc, ce je n je n krat probal dt komando
    private int Command = 0; //komande a hocmo ugasn al ne 0 ni komande na bufferju, -1 hocmo ugasnt, 1 hocmo przgt
    private int TIMEOUT = 1000;
    private float vlaga;


    private Runnable runnableCheckConnection = new Runnable() {
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
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };


    public boolean SensorCheckConnection() {
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
        } else {
            return false;
        }

    }


    private Runnable runnableCheckTemperature = new Runnable() {
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
            for (int i = 0; i < stevilo_podsenzorjev; i++) {

                String url;
                if (stevilo_podsenzorjev == 1) {
                    url = "http://" + ip + "/api/temperature?apikey=" + zeton;
                } else {
                    url = "http://" + ip + "/api/temperature/" + String.valueOf(i) + "?apikey=" + zeton;
                }
                Log.d("INTERNET", ip);
                Log.d("INTERNET", url);
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    responseCode = connection.getResponseCode();
                    InputStream inputStream;
                    if (responseCode == 200) {
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
                    } else {
                        temperatura.add(SLABATEMP);
                    }
                    connection.disconnect();
                    Log.d("INTERNET", String.valueOf(temperatura));

                } catch (IOException e) {
                    e.printStackTrace();
                    temperatura.add(SLABATEMP);
                }
            }
            return;

        }
    };

    private Runnable runnableCheckFirstTemperature = new Runnable() {
        /*
        Opis:
        naredimo nov objekt Runnable, ki ga potem lahko poklicemo v novem threadu
        runnableCheckTemperature vzame ip, zeton in stevilo_podsenzorjev ter nastavi dobljene temperature, na to kar dobi glede na te podatke
        Vhod:/
        Izhod:/
        */
        public void run() {
            String url;
            if (stevilo_podsenzorjev == 1) {
                url = "http://" + ip + "/api/temperature?apikey=" + zeton;
            } else {
                url = "http://" + ip + "/api/temperature/1?apikey=" + zeton;
            }
            Log.d("INTERNET", url);
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                responseCode = connection.getResponseCode();
                InputStream inputStream;
                if (responseCode == 200) {
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
                    temperatura_prvi = Float.valueOf(String.valueOf(response));
                } else {
                    temperatura_prvi = SLABATEMP;
                }
                connection.disconnect();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
    };

    private Runnable runnableGetOnline = new Runnable() {
        /*
        Opis:
        naredimo nov objekt Runnable, ki ga potem lahko poklicemo v novem threadu
        runnableCheckTemperature vzame ip, zeton in stevilo_podsenzorjev ter nastavi dobljene temperature, na to kar dobi glede na te podatke
        Vhod:/
        Izhod:/
        */
        public void run() {
            String url = "http://" + ip + "/api/relay/0?apikey=" + zeton;
            //Log.d("INTERNET", url);
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                responseCode = connection.getResponseCode();
                InputStream inputStream;
                if (responseCode == 200) {
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
                    online = true;
                    prizgan = StringtoBool(String.valueOf(response));
                } else {
                    online = false;
                    prizgan = false;
                }
                connection.disconnect();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
    };

    private Runnable runnableCheckVlaga = new Runnable() {
        /*
        Opis:
        naredimo nov objekt Runnable, ki ga potem lahko poklicemo v novem threadu
        runnableCheckTemperature vzame ip, zeton in stevilo_podsenzorjev ter nastavi dobljene temperature, na to kar dobi glede na te podatke
        Vhod:/
        Izhod:/
        */
        public void run() {
            String url = "http://" + ip + "/api/humidity?apikey=" + zeton;
            //Log.d("INTERNET", url);
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                responseCode = connection.getResponseCode();
                InputStream inputStream;
                if (responseCode == 200) {
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
                    vlaga = Float.valueOf(String.valueOf(response));
                } else {
                    vlaga = SLABATEMP;
                }
                connection.disconnect();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
    };

    public boolean SenzorPrizgiUgasni(boolean prizgi) {

        String url;
        if (prizgi) {
            url = "http://" + ip + "/api/relay/0?apikey=" + zeton + "&value=1";
        } else {
            url = "http://" + ip + "/api/relay/0?apikey=" + zeton + "&value=0";
        }
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            responseCode = connection.getResponseCode();
            InputStream inputStream;
            if (responseCode == 200) {
                connection.disconnect();
                //se pravilno nastavimo vrednost prizgan
                if (prizgi) {
                    prizgan = true;
                } else {
                    prizgan = false;
                }
                return true;
            } else {
                //pravilno nastavimo vrednost prizgan
                if (prizgi) {
                    prizgan = false;
                } else {
                    prizgan = true;
                }
                connection.disconnect();
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Pair<Boolean, Boolean> SenzorGetOnline() {
        Pair<Boolean, Boolean> par;
        Thread thread = new Thread(runnableGetOnline);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (online) {
            if (prizgan) {
                par = new Pair<>(true, true);
            } else {
                par = new Pair<>(true, false);
            }
        } else {
            par = new Pair<>(false, false);
        }
        return par;
    }

    public float getFirstTemp() {
        Thread thread = new Thread(runnableCheckFirstTemperature);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (temperatura_prvi == SLABATEMP) {
            return SLABATEMP;
        } else {
            return temperatura_prvi;
        }
    }

    public boolean SensorCheckTemerature() {
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
        } else {
            return false;
        }

    }

    public float getVlaga(){
        Thread thread = new Thread(runnableCheckVlaga);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            vlaga = SLABATEMP;
        }
        return vlaga;

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

    public List<String> getImena_temperaturnih_senzorjev() {
        return imena_temperaturnih_senzorjev;
    }

    public void addImena_temperaturnih_senzorjev(String ime) {
        this.imena_temperaturnih_senzorjev.add(ime);
    }

    public void setImena_temperaturnih_senzorjev(List<String> seznam) {
        this.imena_temperaturnih_senzorjev = seznam;
    }

    private Boolean StringtoBool(String s) {
        if (s.equals("1")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean getPrizgan() {
        return prizgan;
    }

    public int getStatusBuffer() {
        return StatusBuffer;
    }

    public void setStatusBuffer(int statusBuffer) {
        StatusBuffer = statusBuffer;
    }

    public int getCommandBuffer() {
        return CommandBuffer;
    }

    public void setCommandBuffer(int commandBuffer) {
        CommandBuffer = commandBuffer;
    }

    public int getCommand() {
        return Command;
    }

    public void setCommand(int command) {
        Command = command;
    }

    public void addCommandBuffer() {
        CommandBuffer += 1;
    }

    public void addStatusBuffer() {
        StatusBuffer += 1;
    }
}
