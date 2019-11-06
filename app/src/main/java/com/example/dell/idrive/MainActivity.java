package com.example.dell.idrive;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.BackendlessCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.geo.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String APP_VERSION="v1";
    public static final String APP_ID="AB75E210-5DA1-3653-FF8C-3FA76015AF00";
    public static final String APP_KEY="FC0BA76F-8255-C257-FFCC-78F516170D00";
    Button sign_b,login_b,facebook_b;
    EditText email,password;
    RadioButton radio;
    public static boolean logged=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();

        sign_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                register();
            }
        });

        login_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        fbLog();

    }

    void register(){

        startActivity(new Intent(MainActivity.this,RegisterActivity.class));
    }
    void login(){

                   Backendless.UserService.login(email.getText().toString(), password.getText().toString(), new BackendlessCallback<BackendlessUser>() {
                       @Override
                       public void handleResponse(BackendlessUser response) {

                           logged=true;
                           startActivity(new Intent(MainActivity.this, NavigationActivity.class));
                           finish();

                       }

                       @Override
                       public void handleFault(BackendlessFault fault) {

                           myToast(MainActivity.this,"Connexion Problem");
                       }
                   });
}

    @Override
    protected void onRestart() {
        super.onRestart();
       loadPreferences();

    }
    void loadPreferences(){
//        SharedPreferences load = getSharedPreferences("save",0);
//        logged = load.getBoolean("log",false);
        if(logged)startActivity(new Intent(MainActivity.this, NavigationActivity.class));

    }

    void initialize(){

        loadPreferences();
        sign_b=(Button)findViewById(R.id.sign_up);
        login_b=(Button)findViewById(R.id.login_id);
        Backendless.initApp(this, APP_ID, APP_KEY, APP_VERSION);
        email=(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);
        radio=(RadioButton)findViewById(R.id.radioButton);
        facebook_b=(Button)findViewById(R.id.fb_id);

//
//        saveDummyGeoPoint(36.37,3.88,"Dimis","079452");
//        saveDummyGeoPoint(36,4,"Djamel","16845");
//        saveDummyGeoPoint(36.3,3.88,"Luffy","156424");
//        saveDummyGeoPoint(40,4,"Nez","153435");
//

    }
    void fbLog(){

        facebook_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Backendless.UserService.loginWithFacebook(MainActivity.this, new BackendlessCallback<BackendlessUser>() {
                    @Override
                    public void handleResponse(BackendlessUser response) {

                        logged=true;
                        Intent i=new Intent(MainActivity.this, NavigationActivity.class);
                        i.putExtra("fb",true);
                        startActivity(i);

                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {

                        Toast.makeText(MainActivity.this,"Connexion Problem",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }



    void saveDummyGeoPoint(double lat,double lng,String name,String phone){

        Map<String, Object> meta = new HashMap<String, Object>();
        meta.put("name", name );
        meta.put("phone",phone);

        GeoPoint point=new GeoPoint(lat,lng);
        point.putAllMetadata(meta);
        Backendless.Geo.savePoint(point, new BackendlessCallback<GeoPoint>() {
            @Override
            public void handleResponse(GeoPoint response) {

            }

            @Override
            public void handleFault(BackendlessFault fault) {
                myToast(MainActivity.this,"Geo Data save Error");
            }
        });


    }

    @Override
    public void onBackPressed() {

        finish();
    }

    public static void myToast(Context activity, String msg){

        Toast.makeText(activity,msg,Toast.LENGTH_SHORT).show();

    }
}
