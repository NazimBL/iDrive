package com.example.dell.idrive;

import android.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.BackendlessCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.geo.BackendlessGeoQuery;
import com.backendless.geo.GeoPoint;
import com.backendless.geo.Units;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.Iterator;
import java.util.List;

public class ListOfTaxis extends AppCompatActivity {

    ListView listView;
    String[] names = new String[20];
    GeoPoint[] geoPoints = new GeoPoint[20];
    String[] phone = new String[20];
    String[] detail = new String[20];
    double[] distance = new double[20];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_taxis);

        initialize();
        waitForList();
        settingListViewAdapter();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //  String detail=String.valueOf(parent.getItemIdAtPosition(position));
                dialogBox(position);
            }
        });
    }
    void searchPoint() {

        BackendlessGeoQuery geoQuery = new BackendlessGeoQuery();

        geoQuery.setLatitude(getIntent().getDoubleExtra("lat", 0));
        geoQuery.setLongitude(getIntent().getDoubleExtra("lng", 0));
        geoQuery.setIncludeMeta(true);
        geoQuery.setUnits(Units.KILOMETERS);
        geoQuery.setRadius(getIntent().getDoubleExtra("range",0));
        Backendless.Geo.getPoints(geoQuery, new BackendlessCallback<BackendlessCollection<GeoPoint>>() {
            @Override
            public void handleResponse(BackendlessCollection<GeoPoint> response) {
                int i = 0;
                Iterator<GeoPoint> iterator = response.getCurrentPage().iterator();

                while (iterator.hasNext()) {

                    geoPoints[i] = iterator.next();

                    try {
                        names[i] += geoPoints[i].getMetadata("name").toString();
                        phone[i] += geoPoints[i].getMetadata("phone").toString();
                        distance[i] = geoPoints[i].getDistance();
                        detail[i] += "Name: " + names[i] + " Distance: " + distance[i];
                    }catch(Exception e){
                        MainActivity.myToast(ListOfTaxis.this,"Connexion problem try again");
                    }
                    i++;
                }
            }
            @Override
            public void handleFault(BackendlessFault fault) {
                MainActivity.myToast(ListOfTaxis.this,"Network Error couldn't find taxis");
            }
        });
    }

    void initialize() {

        for (int i = 0; i < 20; i++) {
            names[i] = "";
            phone[i] = "";
            detail[i] = "";
        }
        listView = (ListView) findViewById(R.id.list);
        Backendless.initApp(ListOfTaxis.this, MainActivity.APP_ID, MainActivity.APP_KEY, MainActivity.APP_VERSION);

    }

    void settingListViewAdapter() {

        ListAdapter adapter = new ArrayAdapter<String>(ListOfTaxis.this, android.R.layout.simple_list_item_1, detail);
        listView.setAdapter(adapter);
    }

    void waitForList() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                searchPoint();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).run();
    }



   public  void dialogBox(final int position) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(ListOfTaxis.this).setCancelable(true)
                .setMessage("Are you sure you want to call the Taxi :" + "\n" +
                        names[position] + "\n" +
                        phone[position] + "\n" +
                        "who is " + distance[position] + " from you"
                ).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        call(phone[position]);
                    }
                }).setTitle("Call Taxi");

        dialog.show();
    }

    void call(String number) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        startActivity(callIntent);

    }

}
