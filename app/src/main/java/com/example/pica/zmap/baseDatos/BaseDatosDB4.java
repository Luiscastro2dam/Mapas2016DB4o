package com.example.pica.zmap.baseDatos;

import android.content.Context;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;
import com.example.pica.zmap.tablas.Posicion;

import java.util.ArrayList;


public class BaseDatosDB4 {
    private ObjectContainer bd;

    public BaseDatosDB4(Context ctx) {
        bd = Db4oEmbedded.openFile(
                Db4oEmbedded.newConfiguration(), ctx.getExternalFilesDir(null) +
                        "/basedatosposicicones.db4o");
    }
    public void datosInicio(){
        Posicion p1 = new Posicion(37.160811, -3.597497,"", 2);
        Posicion p2 = new Posicion(37.161939, -3.606295,"",2);
        Posicion p3 = new Posicion(37.167445, -3.596639,"",2);

        Posicion p55 = new Posicion(37.160811, -3.597497,"", 1);
        Posicion p66 = new Posicion(37.161939, -3.606295,"",1);
        Posicion p77 = new Posicion(37.167445, -3.596639,"",1);

        Posicion p4 = new Posicion(337.158895, -3.584536,"", 3);
        Posicion p5 = new Posicion(37.163273, -3.587240,"",3);
        Posicion p6 = new Posicion(37.161871, -3.590609,"",3);

        Posicion p7 = new Posicion(37.160828, -3.604363,"", 4);
        Posicion p8 = new Posicion(37.159579, -3.602346,"",4);
        Posicion p9 = new Posicion(37.159169, -3.595416,"",4);
        bd.store(p1);
        bd.store(p2);
        bd.store(p3);
        bd.store(p4);
        bd.store(p5);
        bd.store(p6);
        bd.store(p7);
        bd.store(p8);
        bd.store(p9);
        bd.store(p55);
        bd.store(p66);
        bd.store(p77);
        bd.commit();
    }


    public void insertar(Posicion posicion){
        bd.store(posicion);
        bd.commit();
    }
    public void close(){
        bd.close();
    }
   //-----------consulta psamos el dia devolvemos los puntos guardados ese dia..
    public ArrayList<Posicion> getCamino(int dia){
        Query q = bd.query();
        q.constrain(Posicion.class);
        q.descend("fecha").constrain(dia).like();
        ObjectSet<Posicion> p= q.execute();
        ArrayList<Posicion> ruta = new ArrayList<Posicion>();
        for(Posicion a: p){
            ruta.add(a);
        }
        return ruta;
    }

}
