package com.example.dell.idrive;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.BackendlessCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.geo.GeoPoint;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.MapsInitializer;

import static com.example.dell.idrive.MainActivity.APP_ID;
import static com.example.dell.idrive.MainActivity.APP_KEY;
import static com.example.dell.idrive.MainActivity.APP_VERSION;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , GoogleApiClient.ConnectionCallbacks ,
        GoogleApiClient.OnConnectionFailedListener,LocationListener {

    GoogleApiClient client;
    Location userLocation;
    public static String phoneFb="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initialize();
        client.connect();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

          finish();
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {


        Log.d("Nazim", "Google Client Connnected");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            userLocation = LocationServices.FusedLocationApi.getLastLocation(client);
        }

        userLocation = LocationServices.FusedLocationApi.getLastLocation(client);

    }
    @Override
    public void onLocationChanged(Location location) {

        userLocation=location;
    }

    @Override
    protected void onStop() {
        client.disconnect();
        super.onStop();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_slideshow) {
          Intent i=  new Intent(NavigationActivity.this,Taxi.class);

            try {
                i.putExtra("lat", userLocation.getLatitude());
                i.putExtra("lng", userLocation.getLongitude());
                startActivity(i);
            }catch (Exception e){
                MainActivity.myToast(NavigationActivity.this,"Localisation not done yet wait a moment..");
            }
            // Handle the camera action
        } else if (id == R.id.nav_manage) {

            if(userLocation !=null) {
                Intent i = new Intent(NavigationActivity.this, FindTaxi.class);
                i.putExtra("lat", userLocation.getLatitude());
                i.putExtra("lng", userLocation.getLongitude());
                startActivity(i);
            }else MainActivity.myToast(NavigationActivity.this,"Connexion problem try again");
        }
        else if(id==R.id.logout){
            myDialogBox("Logout","Are you sure you want to Logout ?");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

       MainActivity.myToast(NavigationActivity.this,"Connexion to Network Failed");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
void initialize(){

    Backendless.initApp(NavigationActivity.this, MainActivity.APP_ID, MainActivity.APP_KEY, MainActivity.APP_VERSION);
    client=new GoogleApiClient.Builder(NavigationActivity.this).addApi(LocationServices.API)
            .addOnConnectionFailedListener(NavigationActivity.this)
            .addConnectionCallbacks(NavigationActivity.this).build();

    statusCheck();
    facebookPhoneManage();
}

    void facebookPhoneManage(){
        if(getIntent().getBooleanExtra("fb",false)){

            DialogWindow dialogWindow=new DialogWindow();
            dialogWindow.show(getFragmentManager(), "Naz");

        }
    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    public void myDialogBox(String title, String msg){

        AlertDialog.Builder builder=new AlertDialog.Builder(NavigationActivity.this).setCancelable(true)
                .setMessage(msg).setTitle(title).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        logout();

                    }
                });

        builder.show();
    }

    public void logout(){

        Backendless.UserService.logout(new BackendlessCallback<Void>() {
            @Override
            public void handleResponse(Void response) {

                MainActivity.logged=false;
                startActivity(new Intent(NavigationActivity.this,MainActivity.class));
                finish();

            }

            @Override
            public void handleFault(BackendlessFault fault) {

                MainActivity.myToast(NavigationActivity.this,"Logout Error Try Again");
            }
        });


    }
}
