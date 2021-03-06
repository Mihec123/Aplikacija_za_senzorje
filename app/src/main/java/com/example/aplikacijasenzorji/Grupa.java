package com.example.aplikacijasenzorji;

import android.graphics.Color;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Grupa {
    private List<Senzor> senzorji = new ArrayList();
    private int stevilo_senzorjev = 0;
    private int id;
    private String ime;
    private int barva = Color.GREEN;
    private int stevilo_prizganih = 0;
    private int stevilo_online = 0;


    public Senzor findSenzorById(int id){
        /*
        Opis:
        funkcija findSenzorById pregleda senzorje v tej grupi in vrne tistega s pravim id-jom ce ta obstaja
        v nasprotnem primeru vrne nov senzor
        Vhod:
        -int id
        Izhod:
        -Senzor senzor
        */
        int dolzina = senzorji.size();
        Senzor senzor;
        for(int i= 0;i <dolzina;i++)
        {
            senzor = senzorji.get(i);
            if (senzor.getId() == id)
            {
                return senzor;
            }
        }
        return new Senzor();
    }

    private int findSenzorByIdInt(int id){
        /*
        Opis:
        funkcija findSenzorByIdInt pregleda senzorje v tej grupi in mesto na katerem se senzor nahaja v tej grupi,
        ce senzorja ni v grupi vrne -1
        Vhod:
        -int id
        Izhod:
        -int i
        */
        int dolzina = this.senzorji.size();
        Senzor senzor;
        for(int i= 0;i <dolzina;i++)
        {
            senzor = this.senzorji.get(i);
            if (senzor.getId() == id)
            {
                return i;
            }
        }
        return -1;
    }

    public Senzor findSenzorByPosition(int i){
        /*
        Opis:
        funkcija findSenzorById pregleda senzorje v tej grupi in vrne tistega s pravim id-jom ce ta obstaja
        v nasprotnem primeru vrne nov senzor
        Vhod:
        -int id
        Izhod:
        -Senzor senzor
        */
        return this.senzorji.get(i);
    }

    public void addSenzor(Senzor senzor){
        this.senzorji.add(senzor);
        this.stevilo_senzorjev += 1;
    }

    public void addSenzorPosition(Senzor senzor, int pozicija){
        this.senzorji.add(pozicija,senzor);
        this.stevilo_senzorjev += 1;
    }

    public void removeSenzorId(int id){
        /*
        Opis:
        funkcija removeSenzorId odstrani senzor z id-jem id ce ta obstaja
        Vhod:
        -int id
        Izhod:/
        */
        int pozicija = findSenzorByIdInt(id);
        if (pozicija != -1){
            this.senzorji.remove(pozicija);
            this.stevilo_senzorjev -= 1;
        }

    }
    public void removeSenzorPosition(int pozicija){
         /*
        Opis:
        funkcija removeSenzorPosition odstrani senzor na poziciji
        Vhod:
        -int pozicija
        Izhod:/
        */
        this.senzorji.remove(pozicija);
        this.stevilo_senzorjev -= 1;
    }

    public int getStevilo_senzorjev() {
        return stevilo_senzorjev;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public int getBarva() {
        return barva;
    }

    public void setBarva(int barva) {
        this.barva = barva;
    }

    public List<Senzor> getSenzorji(){return senzorji;}
}
