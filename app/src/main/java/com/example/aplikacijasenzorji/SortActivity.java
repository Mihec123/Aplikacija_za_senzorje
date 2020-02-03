package com.example.aplikacijasenzorji;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;


import java.util.List;

public class SortActivity extends AppCompatActivity {

    String filename = "devices.txt";
    String FILEPATH;
    Config config = new Config();
    TreeNode main;
    TreeNode child;
    Grupa grupa;
    Senzor senzor;
    String napis;
    int zacetna_pozicija;
    int koncna_pozicija;
    TreeNode stars; //to bomo uporablal pri sortiranju da bomo vedl ce smo nek senzor vzel iz neke grupe al ne
    boolean sprememba = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);
        //nalozimo config ker neznam drgac spravt configa not k z branjem fila
        FILEPATH = this.getFilesDir() +"/"+filename;
        config.getConfigurationValue(FILEPATH);


        //pripravmo drevo
        DraggableTreeView draggableTreeView = (DraggableTreeView)findViewById(R.id.dtv);
        TreeNode root = new TreeNode(this);
        draggableTreeView.maxLevels=2;

        //gremo najprej cez vrstni red in dodamo elemente v drevo
        for(int el:config.getVrstni_red()){
            if (el<0){
                //id je od grupe
                grupa = config.getGrupe().get(config.getIdGrup().indexOf(el));
                napis = getString(R.string.grupa)+": " + grupa.getIme();

                //nastavimo tree node
                main = new TreeNode(napis);
                main.setSenzor(false);
                main.setId(grupa.getId());
                //to vozlisce nastavimo, na grupo
                //pogledamo da ne gre kej s praznimi listi narobe
                if(grupa.getStevilo_senzorjev()>0){
                    for(Senzor sen:grupa.getSenzorji()){
                        napis = getString(R.string.sensor)+": " + sen.getIme();
                        child = new TreeNode(napis);
                        child.setId(sen.getId());
                        main.addChild(child);
                    }
                }

            }
            else{
                //id od senzorja
                senzor = config.getSenzorji().get(config.getIdSenzor().indexOf(el));
                napis = getString(R.string.sensor)+": " + senzor.getIme();
                main = new TreeNode(napis);
                main.setId(senzor.getId());
            }

            //dodamo main v drevo
            root.addChild(main);


        }

        SimpleTreeViewAdapter adapter = new SimpleTreeViewAdapter(this,root);
        draggableTreeView.setAdapter(adapter);
        draggableTreeView.setOnDragItemListener(new DraggableTreeView.DragItemCallback() {
            @Override
            public void onStartDrag(View item, TreeNode node) {
                Log.e("start",(String)node.getData());
                zacetna_pozicija = node.getPosition();
                Log.e("start", String.valueOf(zacetna_pozicija));

                //pogledamo kdo je stars tega objekta
                stars = node.getParent();
            }

            @Override
            public void onChangedPosition(View item, TreeNode child, TreeNode parent, int position) {
                Log.e("changed",(String)parent.getData()+" > "+(String)child.getData()+":"+String.valueOf(position));
            }

            @Override
            public void onEndDrag(View item, TreeNode child, TreeNode parent, int position) {
                Log.e("end",(String)parent.getData()+" > "+(String)child.getData()+":"+String.valueOf(position));
                //position se zacne z 1 ne z nic kot hocmo
                koncna_pozicija = position-1;
                Log.e("end", String.valueOf(koncna_pozicija));
                sprememba = true;

                if (child.getParent().getData() != null){
                    //nek senzor smo postavl v grupo
                    //pogledat mormo ce smo ga prestavl iz neke druge grupe al iz main viewa
                    if (stars.getData() != null){
                        Log.d("sort","senzor je bil prej v grupi damo ga v grupo");
                        Log.d("sort", String.valueOf(child.getParent().getData()) + ", " + String.valueOf(stars.getData()));
                        //prestavll smo ga iz druge grupe
                        //v stari grupi mormo ta senzor zbrisat
                        //pogledamo id grupe ki je shranjen v treenodu
                        int id_ex_starsa = stars.getId();
                        int id_senzorja = child.getId();

                        senzor = config.getSenzorji().get(config.getIdSenzor().indexOf(id_senzorja));
                        //odstranimo senzor iz stare grupe
                        grupa = config.getGrupe().get(config.getIdGrup().indexOf(id_ex_starsa));
                        grupa.removeSenzorId(id_senzorja);
                        //dodamo senzor v novo grupo
                        grupa = config.getGrupe().get(config.getIdGrup().indexOf(child.getParent().getId()));
                        grupa.addSenzorPosition(senzor,koncna_pozicija);

                    }
                    else{
                        Log.d("sort","senzor ni bil v grupi, damo ga v grupo");
                        //senzor smo dodal v grupo, pa prej ni bil v grupi
                        int id_senzorja = child.getId();
                        Log.d("sort","id objekta premika: " + String.valueOf(id_senzorja));
                        senzor = config.getSenzorji().get(config.getIdSenzor().indexOf(id_senzorja));

                        //dodamo senzor v novo grupo
                        grupa = config.getGrupe().get(config.getIdGrup().indexOf(child.getParent().getId()));
                        Log.d("sort","id grupe v kero dajemo: " + String.valueOf(grupa.getId()));
                        grupa.addSenzorPosition(senzor,koncna_pozicija);
                        Log.d("sort","grupa: " + String.valueOf(grupa.getSenzorji()));

                        //ponastavimo vrstni red v config filu, ker senzor prej ni bil v grupi mormo pobrisat njegov id
                        List<Integer> temp_vrstni_red= config.getVrstni_red();
                        Log.d("sort","grupa: " + String.valueOf(temp_vrstni_red));
                        temp_vrstni_red.remove(temp_vrstni_red.indexOf(id_senzorja));
                        config.setVrstni_red(temp_vrstni_red);
                        Log.d("sort","grupa: " + String.valueOf(config.getVrstni_red()));
                    }
                }
                else{
                    //senzorja ne damo v grupo ali pa prestavimo samo grupo
                    if (stars.getData() != null){
                        Log.d("sort","senzor je bil prej v grupi ne damo ga v grupo");
                        //senzor je prej bil v grupi
                        //v stari grupi mormo ta senzor zbrisat
                        //pogledamo id grupe ki je shranjen v treenodu
                        int id_ex_starsa = stars.getId();
                        int id_senzorja = child.getId();

                        senzor = config.getSenzorji().get(config.getIdSenzor().indexOf(id_senzorja));
                        //odstranimo senzor iz stare grupe
                        grupa = config.getGrupe().get(config.getIdGrup().indexOf(id_ex_starsa));
                        grupa.removeSenzorId(id_senzorja);

                        //dodamo senzor v main_view

                        config.addVrstni_redPosition(id_senzorja,koncna_pozicija);


                    }
                    else{
                        //senzor ni bil v grupi in ga ne damo v grupo, ali pa samo premikamo grupo

                        //samo v vrstnem redu config fila mormo popravt
                        Log.d("sort","preikamo nekaj");
                        int id_senzorja = child.getId();
                        Log.d("sort","id: " + String.valueOf(id_senzorja));

                        List<Integer> temp_vrstni_red= config.getVrstni_red();
                        Log.d("sort",String.valueOf(temp_vrstni_red));
                        temp_vrstni_red.remove(temp_vrstni_red.indexOf(id_senzorja));
                        config.setVrstni_red(temp_vrstni_red);
                        config.addVrstni_redPosition(id_senzorja,koncna_pozicija);

                    }
                }
                Log.e("end", String.valueOf(child.getParent().getData()));
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(sprememba) {
                config.writeConfigurationsValues(FILEPATH);
            }
            Intent refresh = new Intent(this, MainActivity.class);
            startActivity(refresh);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
