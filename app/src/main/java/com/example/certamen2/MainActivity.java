package com.example.certamen2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    EditText titulo, descripcion;
    double latitud, longitud; FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titulo = findViewById(R.id.textTitulo);
        descripcion = findViewById(R.id.textDescripcion);

        latitud = getIntent().getDoubleExtra("latitud", 0);
        longitud = getIntent().getDoubleExtra("longitud", 0);

        inicializarFirebase();

    }




    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

    }

    public void cancelar(View v) {
        Intent returnData = new Intent();
        returnData.putExtra("agregarMarcador", false);
        setResult(RESULT_CANCELED,returnData);
        finish();
    }



    public void guardar(View v){
        String  tituloDb = titulo.getText().toString();
        String  descripcionDB = descripcion.getText().toString();


        Localizacion posicion = new Localizacion();
        posicion.setIdLocalizacion(UUID.randomUUID().toString());
        posicion.setTitulo(tituloDb);
        posicion.setDescripcion(descripcionDB);
        posicion.setLatitud(latitud);
        posicion.setLongitud(longitud);


        databaseReference.child("Marcador").child(posicion.getIdLocalizacion()).setValue(posicion);
        Toast.makeText(this, "agregado correctamente", Toast.LENGTH_SHORT).show();

        Intent returnData = new Intent();
        returnData.putExtra("agregarMarcador", true);
        returnData.putExtra("titulo", tituloDb);
        returnData.putExtra("descripcion", descripcionDB);
        setResult(RESULT_OK,returnData);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onCLick(View v){
        startActivity(new Intent(this, MapsActivity.class));

    }
}