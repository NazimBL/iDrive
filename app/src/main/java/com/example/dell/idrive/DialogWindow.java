package com.example.dell.idrive;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.backendless.Backendless;

/**
 * Created by DELL on 19/12/2016.
 */

public class DialogWindow extends DialogFragment {

    LayoutInflater inflater=null;
    View view=null;
    public static String phone="";
    private EditText editText;


    @Override

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Backendless.initApp(getActivity(), MainActivity.APP_ID, MainActivity.APP_KEY, MainActivity.APP_VERSION);


        inflater=getActivity().getLayoutInflater();
        view=inflater.inflate(R.layout.dialog_layout,null);
        editText=(EditText)view.findViewById(R.id.phoneDialog);

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        builder.setView(view).setCancelable(true).
                setCancelable(false).setNeutralButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Backendless.UserService.CurrentUser().setProperty("Phone",editText.getText().toString());
               dialog.cancel();
            }
        });


        return builder.create();


    }


}
