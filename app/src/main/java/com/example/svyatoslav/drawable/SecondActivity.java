package com.example.svyatoslav.drawable;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText Name, Surname, Phone;
    private static String Longitude, Latitude;
    MapView mMapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Name = (EditText) findViewById(R.id.et_second_name);
        Surname = (EditText) findViewById(R.id.et_second_surname);
        Phone = (EditText) findViewById(R.id.et_second_phone);
        Button response = (Button) findViewById(R.id.btn_second_responce);
        response.setOnClickListener(this);


        Intent intent = getIntent();
        Latitude = String.valueOf(intent.getStringExtra("latitude"));
        Longitude = String.valueOf(intent.getStringExtra("longitude"));
        Toast.makeText(this, "Latitude " + Latitude, Toast.LENGTH_LONG).show();
        Toast.makeText(this, "Longitude " + Longitude, Toast.LENGTH_LONG).show();

        if((Latitude!= "null") && (Longitude!="null")) {
            mMapView = (MapView) findViewById(R.id.mapView);
            mMapView.onCreate(savedInstanceState);
            mMapView.onResume();

            try {
                MapsInitializer.initialize(this);
            } catch (Exception e) {
                e.printStackTrace();
            }

            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap mMap) {
                    LatLng point = new LatLng(Double.parseDouble(Latitude), Double.parseDouble(Longitude));
                    mMap.addMarker(new MarkerOptions().position(point));
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(point).zoom(50).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            });
        }
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, MainActivity.class);

        intent.putExtra(Config.KEY_NAME, Name.getText().toString());
        intent.putExtra(Config.KEY_SURNAME, Surname.getText().toString());
        intent.putExtra(Config.KEY_PHONE, Phone.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }
}
