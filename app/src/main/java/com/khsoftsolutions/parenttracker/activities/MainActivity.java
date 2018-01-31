package com.khsoftsolutions.parenttracker.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.khsoftsolutions.parenttracker.R;
import com.khsoftsolutions.parenttracker.core.Globals;
import com.khsoftsolutions.parenttracker.database.ParentManager;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/*
    Splash

    Performs checks.
 */
public class MainActivity extends AppCompatActivity {

    Handler h;

    ProgressDialog prg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0x90BE6D));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        h = new Handler(this.getMainLooper());

        initChecks();
    }

    private void initChecks(){

        checkParentLogin();

    }

    private void checkParentLogin(){

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            checkParentInfo();
        } else {
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent().setClass(getApplicationContext(),
                            LoginActivity.class));
                    finish();
                }
            },1800);
        }
    }

    private void checkParentInfo(){

        if(Build.VERSION.SDK_INT >= 23){
            prg = new ProgressDialog(MainActivity.this);
            prg.setMessage("Checking parent information...");
            prg.setCancelable(false);
            prg.show();
        }

        ParseQuery<ParseObject> parentInfo = ParseQuery.getQuery(Globals.ParentInformation.OBJ_NAME);
        parentInfo.whereEqualTo(Globals.ParentInformation.PARENT_OBJ_ID,ParseUser.getCurrentUser().getObjectId());

        parentInfo.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(prg != null) prg.dismiss();

                if(e == null){
                    if(objects.size() > 0){
                        initActivity();
                    }
                    else{
                        startActivity(new Intent().setClass(getApplicationContext(),
                                ParentInformationActivity.class));
                        finish();
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "Failed to connect to server.", Toast.LENGTH_SHORT).show();

                    showRequireInternetConnection();
                }
            }
        });


    }
    private void initActivity(){
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent().setClass(getApplicationContext(),MainMenu.class));
                finish();
            }
        },3000);
    }

    private void showRequireInternetConnection(){

        AlertDialog.Builder req = new AlertDialog.Builder(MainActivity.this);

        req.setTitle("Internet Connection Required");
        req.setMessage("This app requires active internet connection to work. " +
                "Please connect to Internet before using this app.");

        req.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
                dialogInterface.cancel();
            }
        });

        req.create().show();
    }
}
