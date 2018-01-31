package com.khsoftsolutions.parenttracker.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.khsoftsolutions.parenttracker.R;
import com.khsoftsolutions.parenttracker.database.ParentManager;
import com.khsoftsolutions.parenttracker.objects.MenuItem;
import com.khsoftsolutions.parenttracker.utilities.MainMenuAdapter;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MainMenu extends AppCompatActivity {

    ProgressDialog prg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.GREEN);
        setSupportActionBar(toolbar);

        setTitle("Main Menu");

        prg = new ProgressDialog(MainMenu.this);

        setupMenu();

    }

    private void setupMenu(){

        ListView main = (ListView)findViewById(R.id.lvMainMenu);

        List<MenuItem> items = new ArrayList<MenuItem>();

        items.add(new MenuItem("My Children",R.drawable.kid));
        items.add(new MenuItem("About Me",R.drawable.parent));
        items.add(new MenuItem("Logout",R.drawable.logout_icon));

        MainMenuAdapter menuAdapter = new MainMenuAdapter(items,MainMenu.this);

        main.setAdapter(menuAdapter);

        main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i){
                    case 0:
                        //Show list of children
                        startActivity(new Intent().setClass(getApplicationContext(),ChildrenList.class));
                        break;
                    case 1:
                        startActivity(new Intent().setClass(getApplicationContext(),DisplayParentInfo.class));
                        break;
                    case 2:
                        onLogoutDialog();
                        break;
                    default:
                        Log.e("ERROR_MAIN_MENU","DEFAULT REACHED");
                }
            }
        });

        //Snackbar.make(main,"More features will be added soon!",Snackbar.LENGTH_LONG).show();
    }

    private void onLogoutDialog(){

        AlertDialog.Builder logout = new AlertDialog.Builder(MainMenu.this);
        logout.setTitle("Logout");
        logout.setMessage("Are you sure you want to log out?");
        logout.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                prg.setMessage("Logging out...");
                prg.show();

                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        prg.dismiss();

                        if(e==null){

                            Toast.makeText(MainMenu.this,
                                    "You have successfully logged out.", Toast.LENGTH_SHORT).show();

                            ParentManager parentManager = new ParentManager(MainMenu.this);
                            parentManager.dropParent();
                            parentManager.cleanUp();

                            startActivity(new Intent().setClass(getApplicationContext(),LoginActivity.class));
                            finish();
                        }
                        else{
                            Toast.makeText(MainMenu.this,
                                    e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        logout.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        logout.create().show();
    }

    private void onExitDialog(){

        AlertDialog.Builder exit = new AlertDialog.Builder(MainMenu.this);

        exit.setTitle("Exit?");

        exit.setMessage("Are you sure you want to exit? " +
                "Your account will still be logged in.");

        exit.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        exit.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        exit.create().show();
    }

    @Override
    public void onBackPressed(){
        onExitDialog();
    }
}
