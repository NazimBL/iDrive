package com.example.dell.idrive;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class FindTaxi extends AppCompatActivity  {


    EditText range;
    Button search_b;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_taxi);

        initialize();

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(FindTaxi.this, MapsActivity.class);
                i.putExtra("lat",getIntent().getDoubleExtra("lat",0));
                i.putExtra("lng",getIntent().getDoubleExtra("lng",0));
                startActivity(i);
            }
        });

        search_b.setOnClickListener(new View.OnClickListener() {
            double distance;
            @Override
            public void onClick(View v) {
                try{

                    distance=Double.parseDouble(range.getText().toString());
                }catch(Exception e){
                    Toast.makeText(FindTaxi.this,"Wrong Number Format",Toast.LENGTH_SHORT).show();
                }

                Intent i=new Intent(FindTaxi.this,ListOfTaxis.class);
                i.putExtra("range",distance);
                i.putExtra("lat",getIntent().getDoubleExtra("lat",0));
                i.putExtra("lng",getIntent().getDoubleExtra("lng",0));
                startActivity(i);


            }
        });

    }

    void initialize(){

        search_b=(Button)findViewById(R.id.search);
        img=(ImageView) findViewById(R.id.map);
        range=(EditText) findViewById(R.id.edit);
    }

}
