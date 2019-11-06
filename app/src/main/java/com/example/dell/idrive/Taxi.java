package com.example.dell.idrive;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.Geo;
import com.backendless.async.callback.BackendlessCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.geo.BackendlessGeoQuery;
import com.backendless.geo.GeoPoint;
import com.backendless.geo.Units;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Taxi extends AppCompatActivity {

   Button work,quit;
    GeoPoint point;
    Map<String,Object> meta=new HashMap<String,Object>();;
    BackendlessUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi);

        intialize();

        double lat,lng;
        lat=getIntent().getDoubleExtra("lat",0);
        lng=getIntent().getDoubleExtra("lng",0);
        point=new GeoPoint(lat,lng);

        work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                saveCurrentGeoPoint();
                work.setEnabled(false);
                quit.setEnabled(true);

            }
        });

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                removePoint();
                quit.setEnabled(false);
               work.setEnabled(true);

            }
        });
    }



    void intialize(){

         user=Backendless.UserService.CurrentUser();
        work=(Button)findViewById(R.id.work);
        quit=(Button)findViewById(R.id.quit);
        Backendless.initApp(Taxi.this, MainActivity.APP_ID, MainActivity.APP_KEY, MainActivity.APP_VERSION);

    }

    void saveCurrentGeoPoint(){


        String name="",phone="";
        user=Backendless.UserService.CurrentUser();
try {
    name = "" + user.getProperty("name");
    phone = "" + user.getProperty("Phone");
}catch (Exception e){
    MainActivity.myToast(Taxi.this,"Network problem try again ina moment");
    work.setEnabled(true);
}
        meta.put("name",name);
        meta.put("phone",phone);
        point.putAllMetadata(meta);

        Backendless.Geo.savePoint(point, new BackendlessCallback<GeoPoint>() {
            @Override
            public void handleResponse(GeoPoint response) {

                Toast.makeText(Taxi.this,"You've been Successfully registred , wait for some Client to call you",Toast.LENGTH_LONG).show();


            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(Taxi.this,"ErrorFromSaveMethode",Toast.LENGTH_SHORT).show();
            }
        });

    }

    void removePoint(){

        BackendlessGeoQuery query=new BackendlessGeoQuery();
        query.setLatitude(point.getLatitude());
        query.setLongitude(point.getLongitude());
        query.setUnits(Units.METERS);
        query.setRadius(1d);

        Backendless.Geo.getPoints(query, new BackendlessCallback<BackendlessCollection<GeoPoint>>() {
            @Override
            public void handleResponse(BackendlessCollection<GeoPoint> response) {

                Iterator<GeoPoint> iterator = response.getCurrentPage().iterator();
                  while (iterator.hasNext()) {


                          Backendless.Geo.removePoint(iterator.next(), new BackendlessCallback<Void>() {
                              @Override
                              public void handleResponse(Void response) {
                                  Log.d("Nazim", "point removed");
                              }
                          });

                  }
            }
        });
    }

}
