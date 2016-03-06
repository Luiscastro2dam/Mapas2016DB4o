package com.example.pica.zmap.servicios;


import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.pica.zmap.baseDatos.BaseDatosDB4;
import com.example.pica.zmap.tablas.Posicion;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

public class Servicio extends Service implements  com.google.android.gms.location.LocationListener,
                                                  GoogleApiClient.ConnectionCallbacks,
                                                  GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient cliente;
    private LocationRequest peticionLocalizaciones;
    private BaseDatosDB4 bd;

    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (status == ConnectionResult.SUCCESS) {
            cliente = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            cliente.connect();
            Toast.makeText(this, "Conecta", Toast.LENGTH_LONG).show();
            //tv.setText("Conecta" + "\n");
        } else {
            if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
                //GooglePlayServicesUtil.getErrorDialog(status, this, CTEPLAY).show();
            } else {
                Toast.makeText(this, "No", Toast.LENGTH_LONG).show();
            }
        }

        peticionLocalizaciones = new LocationRequest();
        //peticionLocalizaciones.setInterval(10000);
        peticionLocalizaciones.setSmallestDisplacement(1);
        peticionLocalizaciones.setFastestInterval(5000);
        peticionLocalizaciones.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        double la=location.getLatitude();
        double lo=location.getLongitude();
        LatLng myLocation = new LatLng(la,lo);
        //obtenemos el dia de la semana para guardar la posicion
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        Posicion po = new Posicion(la,lo,"",day);
        bd = new BaseDatosDB4(this);
        bd.insertar(po);
        bd.close();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
