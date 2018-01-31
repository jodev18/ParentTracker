package com.khsoftsolutions.parenttracker.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.khsoftsolutions.parenttracker.R;
import com.khsoftsolutions.parenttracker.core.Globals;
import com.khsoftsolutions.parenttracker.database.ParentManager;
import com.khsoftsolutions.parenttracker.objects.ParentObject;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ParentInformationActivity extends AppCompatActivity {

    //Fields
    @BindView(R.id.etParentFN) EditText firstName;
    @BindView(R.id.etParentMN) EditText midName;
    @BindView(R.id.etParentLN) EditText lastName;
    @BindView(R.id.etAge) EditText age;
    @BindView(R.id.etHomeAddress) EditText address;
    @BindView(R.id.etPhoneNumber) EditText phone;

    @BindView(R.id.btnSaveParent) Button saveButton;

    ProgressDialog prg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_information);

        setTitle("Parent Information");

        ButterKnife.bind(this);

        String goal = this.getIntent().getStringExtra(Globals.PARENT_INFO_KEY);

        if(goal != null){
            if(goal.equals(Globals.PARENT_INFO_EDIT)){
                editInfo();
            }
        }
        else{
            initNewInfo();
        }

    }

    private void editInfo(){



    }

    private void initNewInfo(){

        Toast.makeText(this, "Before you add any children, " +
                "please enter your personal information below.", Toast.LENGTH_SHORT).show();

        initSaveButton();

        if(Build.VERSION.SDK_INT >= 26){

        }
        else{
            prg = new ProgressDialog(ParentInformationActivity.this);
            prg.setMessage("Saving parent information...");
        }
    }

    private void initSaveButton(){

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String fName = firstName.getText().toString();
                String mName = midName.getText().toString();
                String lName = lastName.getText().toString();
                String strAge = age.getText().toString();
                String strPhone = phone.getText().toString();
                String strAddr = address.getText().toString();

                if(fName.length() > 0){
                    if(mName.length() > 0){
                        if(lName.length() > 0){
                            if(strAge.length() > 0){
                                if(strPhone.length() > 0){
                                    if(strAddr.length() > 0){

                                        //In case the dialog's null,
                                        //display progress bar for API 26
                                        if(prg != null){
                                            prg.show();
                                        }

                                        ParentManager pman = new ParentManager(ParentInformationActivity.this);

                                        ParentObject parentObject = new ParentObject();
                                        parentObject.PARENT_FIRST_NAME = fName;
                                        parentObject.PARENT_MID_NAME = mName;
                                        parentObject.PARENT_LAST_NAME = lName;
                                        parentObject.PARENT_AGE = strAge;
                                        parentObject.PARENT_PHONE_NUMBER = strPhone;
                                        parentObject.PARENT_ADDRESS = strAddr;

                                        long ins = pman.insertParent(parentObject);
                                        
                                        if(ins > 0){
                                            prg.setMessage("Saving to cloud...");

                                            if(ParseUser.getCurrentUser() != null){

                                                ParseObject pObj = new ParseObject(Globals.ParentInformation.OBJ_NAME);
                                                pObj.put(Globals.ParentInformation.FIRST_NAME,parentObject.PARENT_FIRST_NAME);
                                                pObj.put(Globals.ParentInformation.MIDDLE_NAME,parentObject.PARENT_MID_NAME);
                                                pObj.put(Globals.ParentInformation.LAST_NAME,parentObject.PARENT_LAST_NAME);
                                                pObj.put(Globals.ParentInformation.PARENT_AGE,parentObject.PARENT_AGE);
                                                pObj.put(Globals.ParentInformation.PHONE_NUMBER,parentObject.PARENT_PHONE_NUMBER);
                                                pObj.put(Globals.ParentInformation.ADDRESS,parentObject.PARENT_ADDRESS);
                                                pObj.put(Globals.ParentInformation.PARENT_OBJ_ID,ParseUser.getCurrentUser().getObjectId());

                                                pObj.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {

                                                        if(e==null){
                                                            prg.dismiss();

                                                            Toast.makeText(ParentInformationActivity.this,
                                                                    "Saved parent information to cloud.", Toast.LENGTH_SHORT).show();

                                                            startActivity(new Intent().setClass(getApplicationContext(),MainActivity.class));
                                                            finish();
                                                        }
                                                        else{
                                                            //Display error
                                                            Toast.makeText(ParentInformationActivity.this,
                                                                    e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                        else{
                                            //Dismiss dialog
                                            prg.dismiss();

                                            Toast.makeText(ParentInformationActivity.this, "Failed to insert to database.", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                    else{
                                        Toast.makeText(ParentInformationActivity.this,
                                                "Please enter your address.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else{
                                    Toast.makeText(ParentInformationActivity.this,
                                            "Please enter your phone number.", Toast.LENGTH_SHORT).show();
                                }

                            }
                            else{
                                Toast.makeText(ParentInformationActivity.this,
                                        "Please enter your age.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(ParentInformationActivity.this,
                                    "Please enter your last name.", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else{
                        Toast.makeText(ParentInformationActivity.this,
                                "Please enter your middle name.", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(ParentInformationActivity.this,
                            "Please enter your first name.", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }




}
