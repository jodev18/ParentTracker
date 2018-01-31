package com.khsoftsolutions.parenttracker.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.khsoftsolutions.parenttracker.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    @BindView(R.id.etUsername) EditText etUsername;
    @BindView(R.id.etPassword) EditText etPassword;

    @BindView(R.id.btnGoSignup) Button btnGoSignup;

    ProgressDialog prg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        if(Build.VERSION.SDK_INT >= 26){

        }
        else{
            prg = new ProgressDialog(LoginActivity.this);
            prg.setMessage("Logging in");
        }

        Button login = (Button)findViewById(R.id.btnLogin);
        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                String userName = etUsername.getText().toString();
                String passWord = etPassword.getText().toString();

                if(userName.length() >= 8 && passWord.length() >= 8){

                    //Show progress dialog upon execution of login
                    prg.show();

                    ParseUser.logInInBackground(userName, passWord, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {

                            prg.dismiss();

                            if(e == null){
                                Toast.makeText(LoginActivity.this,
                                        "Successfully logged in!",Toast.LENGTH_LONG).show();
                                startActivity(new Intent().setClass(getApplicationContext(),
                                        MainActivity.class));
                            }
                            else{
                                //Toast.makeText(LoginActivity.this, "There was a problem " +
                                  //      "encountered while logging in.", Toast.LENGTH_SHORT).show();

                                //DEBUG CODE
                                Toast.makeText(LoginActivity.this, e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(LoginActivity.this,
                            "Username and password must be at least eight characters.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnGoSignup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent().setClass(getApplicationContext(),SignupActivity.class));
            }
        });

    }


}

