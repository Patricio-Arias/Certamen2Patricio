package com.example.certamen2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.certamen2.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private List<Localizacion> listaLocalizacion = new ArrayList<>();
    private boolean cargarMarcadoresDeFirebase = true;
    private boolean agregarMarcador = false;
    String titulo;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getLocalizacion();
        inicializarFirebase();

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK && result.getData() != null){
                    Intent intent = result.getData();
                    titulo = intent.getStringExtra("titulo");
                    agregarMarcador = intent.getBooleanExtra("addMarker", false);
                }
                else if (result.getResultCode() == RESULT_CANCELED){
                    agregarMarcador = result.getData().getBooleanExtra("agregarMarcador",false);
                }
            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

    }
    private void listaDatos(GoogleMap mMap) {
        databaseReference.child("Marcador").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaLocalizacion.clear();
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    Localizacion marcador = objSnapshot.getValue(Localizacion.class);
                    listaLocalizacion.add(marcador);
                    System.out.println(marcador.toString());
                }
                System.out.println(">>datos recuperados de firebase: "+listaLocalizacion.size());

                System.out.println("cargarMarcadoresDeFirebase: "+cargarMarcadoresDeFirebase);
                System.out.println("cantidad de datos en la lista: "+ listaLocalizacion.size());
                if (cargarMarcadoresDeFirebase){
                    for (int i = 0; i < listaLocalizacion.size(); i++) {
                        Localizacion localizacion =  listaLocalizacion.get(i);
                        LatLng punto = new LatLng(localizacion.getLatitud(), localizacion.getLongitud());
                        mMap.addMarker(new MarkerOptions().position(punto).title(localizacion.getTitulo()));
                    }
                    cargarMarcadoresDeFirebase = false;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    
    private void getLocalizacion() {
        int permiso = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permiso == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        listaDatos(mMap);

        mMap.setMyLocationEnabled(true);

        //mMap.getUiSettings().setMyLocationButtonEnabled(false);

        LocationManager locationManager = (LocationManager) MapsActivity.this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        // I suppressed the missing-permission warning because this wouldn't be executed in my
        // case without location services being enabled
        @SuppressLint("MissingPermission") android.location.Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        double userLat = lastKnownLocation.getLatitude();
        double userLong = lastKnownLocation.getLongitude();
        LatLng miUbicacion = new LatLng(userLat, userLong);         //captura ubicacion
        mMap.moveCamera(CameraUpdateFactory.newLatLng(miUbicacion));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(miUbicacion)
                .zoom(17)
                .bearing(90)
                .tilt(45)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //click largo marcador
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                myIntent.putExtra("latitud", point.latitude);
                myIntent.putExtra("longitud", point.longitude);
                activityResultLauncher.launch(myIntent);
                if (agregarMarcador) {
                    mMap.addMarker(new MarkerOptions().position(point).title(titulo));
                }
            }
        });
    }


}