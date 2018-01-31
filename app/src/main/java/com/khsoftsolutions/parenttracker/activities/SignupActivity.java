package com.khsoftsolutions.parenttracker.activities;

import android.app.ProgressDialog;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.khsoftsolutions.parenttracker.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity {

    //Button action
    @BindView(R.id.btnSignUp) Button signUp;

    //Fields
    @BindView(R.id.etNewUsername) EditText nUser;
    @BindView(R.id.etNewPassword) EditText nPass;
    @BindView(R.id.etConfPassword) EditText cPass;
    @BindView(R.id.etNewEmail) EditText nEmail;

    ProgressDialog prg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ButterKnife.bind(this);


        if(Build.VERSION.SDK_INT >= 26){
            //TODO ProgressBar
        }
        else{
            prg = new ProgressDialog(SignupActivity.this);
            prg.setMessage("Signing up...");
        }

        initSignupButton();

    }

    private void initSignupButton(){

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Validation
                String userName = nUser.getText().toString();
                String passWord = nPass.getText().toString();
                String confPass = cPass.getText().toString();
                String eMail = nEmail.getText().toString();


                if(userName.length() >= 8){
                    if(passWord.length() >= 8){
                        if(confPass.length() >= 8){
                            if(passWord.equals(confPass)){
                                if(eMail.length() >= 6){
                                    if(eMail.contains("@")){
                                        saveToParse(userName,passWord,eMail);
                                    }
                                    else{
                                        Toast.makeText(SignupActivity.this, "Incorrect email format.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else{
                                    Toast.makeText(SignupActivity.this, "Insufficient email length.", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(SignupActivity.this, "Password and confirm password must be equal.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(SignupActivity.this, "Confirm password must be at least 8 characters.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(SignupActivity.this, "Password must be at least 8 characters.", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(SignupActivity.this, "Username must be at least 8 characters.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveToParse(String username, String password, String email){

        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);

        //Progressbar check for ui notification
        //adjustments for android O. Will not use progressdialog
        //if the version is above or equal to android O
        if(prg != null){
            prg.show();
        }

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                prg.dismiss();

                if(e == null){
                    Toast.makeText(SignupActivity.this, "Your have successfully " +
                            "signed up. You can now log in.", Toast.LENGTH_LONG).show();
                    finish();
                }
                else{
                    Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
