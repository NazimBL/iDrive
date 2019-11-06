package com.example.dell.idrive;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.BackendlessCallback;
import com.backendless.exceptions.BackendlessFault;

public class RegisterActivity extends AppCompatActivity {

    AutoCompleteTextView town,country;
    EditText email,pass,phone,name,range_e;
    int range=0;
    Button done;
    boolean tag=false;
    BackendlessUser user=new BackendlessUser();
    String[] countries={"Mexico","Rwanda","USA", "Russia","Germany", "France", "Canada","Algeria"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initilize();
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentManagement();
                if(!tag)Snackbar.make(v,"Incomplete Content",Snackbar.LENGTH_SHORT).show();
                register();
            }
        });


    }

    void initilize(){

        email=(EditText)findViewById(R.id.email);
        pass=(EditText)findViewById(R.id.password);
        phone=(EditText)findViewById(R.id.phone);
        name=(EditText)findViewById(R.id.name);
        town=(AutoCompleteTextView)findViewById(R.id.town);
        country=(AutoCompleteTextView)findViewById(R.id.country);
        range_e=(EditText)findViewById(R.id.range);
        done=(Button)findViewById(R.id.done);

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(RegisterActivity.this,android.R.layout.simple_list_item_1,countries);
        country.setAdapter(adapter);
        Backendless.initApp(RegisterActivity.this, MainActivity.APP_ID, MainActivity.APP_KEY, MainActivity.APP_VERSION);

    }

    void contentManagement(){

       if(email.getText().toString().equals("") || pass.getText().toString().equals("") ||name.getText().toString().equals("")
               || phone.getText().toString().equals("")){
          // Toast.makeText(RegisterActivity.this,"Incomplete content !",Toast.LENGTH_SHORT).show();
         tag=false;
       }
        else {
           tag=true;
           try {
               range = Integer.parseInt(range_e.getText().toString());
           } catch (Exception e) {
               MainActivity.myToast(RegisterActivity.this,"Range content is empty!");
           }

           user.setEmail(email.getText().toString());
           user.setPassword(pass.getText().toString());
           user.setProperty("name", name.getText().toString());
           user.setProperty("Phone", phone.getText().toString());

           if (!country.getText().toString().equals(""))
               user.setProperty("Country", country.getText().toString());
           if (!town.getText().toString().equals(""))
               user.setProperty("Town", town.getText().toString());
           if (!range_e.getText().toString().equals(""))
               user.setProperty("Range",range);

       }   }

    void register(){

        Backendless.UserService.register(user, new BackendlessCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response) {

                Backendless.UserService.logout(new BackendlessCallback<Void>() {
                    @Override
                    public void handleResponse(Void response) {
                        MainActivity.logged=false;
                        startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                        finish();

                    }
                });

            }

            @Override
            public void handleFault(BackendlessFault fault) {
                MainActivity.myToast(RegisterActivity.this,"Something Wrong happened try again");
            }
        });
    }
}
