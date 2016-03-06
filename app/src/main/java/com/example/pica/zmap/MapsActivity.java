package com.example.pica.zmap;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pica.zmap.baseDatos.BaseDatosDB4;
import com.example.pica.zmap.servicios.Servicio;
import com.example.pica.zmap.tablas.Posicion;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Calendar;

public class MapsActivity extends AppCompatActivity implements  OnMapReadyCallback,
                                                                GoogleApiClient.ConnectionCallbacks,
                                                                GoogleApiClient.OnConnectionFailedListener,
                                                                com.google.android.gms.location.LocationListener {


    private GoogleApiClient cliente;
    private LocationRequest peticionLocalizaciones;
    private final int CTEPLAY = 1;
    private GoogleMap mMap;
    private BaseDatosDB4 bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bd = new BaseDatosDB4(MapsActivity.this);
                final Dialog dialogo = new Dialog(MapsActivity.this);
                dialogo.setTitle("Coordenadas:");
                dialogo.setContentView(R.layout.coordenadas);
                Button btanadir = (Button) dialogo.findViewById(R.id.btanadir);
                btanadir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //entra aqui a elejido color lo recojemos
                        EditText etlong = (EditText)dialogo.findViewById(R.id.etlong);
                        EditText etlati = (EditText)dialogo.findViewById(R.id.etlati);
                        double lon=Double.parseDouble(etlong.toString());
                        double lat=Double.parseDouble(etlati.toString());
                        Posicion po = new Posicion(lat,lon,"Informacion",9);
                        bd.insertar(po);
                        bd.close();
                        dialogo.dismiss();
                    }
                });
                dialogo.show();

            }
        });
        init();

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CTEPLAY) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "No", Toast.LENGTH_LONG).show();
            }
        }
    }
/*----------------metodo de inicio ponemos el mapa de google------------------------*/
    public void init() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (status == ConnectionResult.SUCCESS) {
            cliente = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            cliente.connect();

        } else {
            if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
                GooglePlayServicesUtil.getErrorDialog(status, this, CTEPLAY).show();
            } else {
                Toast.makeText(this, "No", Toast.LENGTH_LONG).show();
            }
        }
    }
/*-------------------------------------------------------------------------------------*/
    @Override
    public void onConnected(Bundle bundle) {
        peticionLocalizaciones = new LocationRequest();
        peticionLocalizaciones.setInterval(10000);
        peticionLocalizaciones.setFastestInterval(5000);
        peticionLocalizaciones.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(cliente, peticionLocalizaciones, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
//----------------------metodo cuando cambiamos de posicion-----------------------
    @Override
    public void onLocationChanged(Location location) {
        double la=location.getLatitude();
        double lo=location.getLongitude();
        //obtenemos el dia de la semana para guardar la posicion
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        Posicion po = new Posicion(la,lo,"",day);
        bd = new BaseDatosDB4(this);
        bd.insertar(po);
        bd.close();

    }
//-------------------------------------------------------------------
    /*------------------------------------pintar puntos en el mapa-----------------*/
   private void setMarker(Posicion posicion) {
    LatLng positi= new LatLng(posicion.getLatitud(),posicion.getLongitud());
    String titu=posicion.getInfo();
    String inf="";
    // Agregamos marcadores para indicar sitios de interéses.
    Marker myMaker = mMap.addMarker(new MarkerOptions()
            .position(positi)
            .title(titu)  //Agrega un titulo al marcador
            .snippet(inf)   //Agrega información detalle relacionada con el marcador
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))); //Color del marcador
}
//--------------------------------------------------------------------------------------
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
  /*----------------------------menu principal----------------------*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_servicioinicio:{
                Intent intent = new Intent(this, Servicio.class);
                startService(intent);
            }
            case R.id.menu_anadir: {
                bd = new BaseDatosDB4(this);
                bd.datosInicio();
                PolylineOptions rectOptions = new PolylineOptions();
                ArrayList<Posicion> pos = bd.getCamino(1);
                for (Posicion p : pos) {
                    rectOptions.add(new LatLng(p.getLatitud(), p.getLongitud()));
                }
                mMap.clear();
                mMap.addPolyline(rectOptions);
                bd.close();
                return true;
            }
            case R.id.menu_martes:{
                bd = new BaseDatosDB4(this);
                bd.datosInicio();
                PolylineOptions rectOptions = new PolylineOptions();
                ArrayList<Posicion> pos = bd.getCamino(2);
                System.out.println("posiciones"+pos.toString());
                for (Posicion p : pos) {
                    rectOptions.add(new LatLng(p.getLatitud(), p.getLongitud()));
                }
                mMap.clear();
                mMap.addPolyline(rectOptions);
                bd.close();
            }
            case R.id.menu_miercoles:{
                bd = new BaseDatosDB4(this);
                bd.datosInicio();
                PolylineOptions rectOptions = new PolylineOptions();
                ArrayList<Posicion> pos = bd.getCamino(3);
                for (Posicion p : pos) {
                    rectOptions.add(new LatLng(p.getLatitud(), p.getLongitud()));
                }
                mMap.clear();
                mMap.addPolyline(rectOptions);
                bd.close();
            }
            case R.id.menu_jueves:{
                bd = new BaseDatosDB4(this);
                bd.datosInicio();
                PolylineOptions rectOptions = new PolylineOptions();
                ArrayList<Posicion> pos = bd.getCamino(4);
                for (Posicion p : pos) {
                    rectOptions.add(new LatLng(p.getLatitud(), p.getLongitud()));
                }
                mMap.clear();
                mMap.addPolyline(rectOptions);
                bd.close();
            }case R.id.menu_viernes:{
                bd = new BaseDatosDB4(this);
                bd.datosInicio();
                PolylineOptions rectOptions = new PolylineOptions();
                ArrayList<Posicion> pos = bd.getCamino(5);
                for (Posicion p : pos) {
                    rectOptions.add(new LatLng(p.getLatitud(), p.getLongitud()));
                }
                mMap.clear();
                mMap.addPolyline(rectOptions);
                bd.close();
            }case R.id.menu_sabado:{
                bd = new BaseDatosDB4(this);
                bd.datosInicio();
                PolylineOptions rectOptions = new PolylineOptions();
                ArrayList<Posicion> pos = bd.getCamino(6);
                for (Posicion p : pos) {
                    rectOptions.add(new LatLng(p.getLatitud(), p.getLongitud()));
                }
                mMap.clear();
                mMap.addPolyline(rectOptions);
                bd.close();
            }case R.id.menu_domingo:{
                bd = new BaseDatosDB4(this);
                bd.datosInicio();
                PolylineOptions rectOptions = new PolylineOptions();
                ArrayList<Posicion> pos = bd.getCamino(7);
                for (Posicion p : pos) {
                    rectOptions.add(new LatLng(p.getLatitud(), p.getLongitud()));
                }
                mMap.clear();
                mMap.addPolyline(rectOptions);
                bd.close();
            }
            case R.id.menu_puntos:{
                Posicion pl = new Posicion(37.160811, -3.597497,"mihUBICACION",1);
                setMarker(pl);
            }
        }
        return super.onOptionsItemSelected(item);
    }
    //-----------------------------------------------------------
//    private EmbeddedConfiguration dbConfig() throws IOException {
//        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
//        configuration.common().add(new AndroidSupport());
//        configuration.common().activationDepth(25);
//        configuration.common().objectClass(GregorianCalendar.class).storeTransientFields(true);
//        configuration.common().objectClass(GregorianCalendar.class).callConstructor(true);
//        configuration.common().exceptionsOnNotStorable(false);
//        configuration.common().objectClass(Posicion.class).objectField("fecha").indexed(true);
//        return configuration;
//    }
}
