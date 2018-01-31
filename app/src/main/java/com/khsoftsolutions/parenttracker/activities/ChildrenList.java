package com.khsoftsolutions.parenttracker.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.khsoftsolutions.parenttracker.R;
import com.khsoftsolutions.parenttracker.core.Globals;
import com.khsoftsolutions.parenttracker.objects.ChildObject;
import com.khsoftsolutions.parenttracker.utilities.ChildAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChildrenList extends AppCompatActivity {

    @BindView(R.id.lvChildrenList) ListView childListItems;
    @BindView(R.id.tvEmptyLayout) TextView emptyLayout;

    ProgressDialog prg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Children List");

        ButterKnife.bind(this);

        if(Build.VERSION.SDK_INT >= 26){

        }
        else{
            prg = new ProgressDialog(ChildrenList.this);
        }

        loadChildren();

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fabAddChildren);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Initialize add children dialog activity
                startActivity(new Intent().setClass(getApplicationContext(),ChildInformationActivity.class));
            }
        });

    }

    private void loadChildren(){

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Globals.ChildInformation.OBJ_NAME);
        query.whereEqualTo(Globals.ChildInformation.PARENT_OBJ_ID, ParseUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> childList, ParseException e) {

                if(prg != null){
                    if(prg.isShowing()){
                        prg.dismiss();
                    }
                }

                if(childList.size() > 0){

                    List<ChildObject> childObjects = new ArrayList<ChildObject>();

                    for(int i=0;i<childList.size();i++){

                        ChildObject cObj = new ChildObject();
                        ParseObject currObj = childList.get(i);

                        cObj.CHILD_ADDRESS = currObj.getString(Globals.ChildInformation.ADDRESS);
                        cObj.CHILD_AGE = currObj.getString(Globals.ChildInformation.CHILD_AGE);
                        cObj.CHILD_FIRST_NAME = currObj.getString(Globals.ChildInformation.FIRST_NAME);
                        cObj.CHILD_MIDDLE_NAME = currObj.getString(Globals.ChildInformation.MIDDLE_NAME);
                        cObj.CHILD_LAST_NAME = currObj.getString(Globals.ChildInformation.LAST_NAME);
                        cObj.CHILD_GENDER = currObj.getString(Globals.ChildInformation.GENDER);
                        cObj.OBJECT_ID = currObj.getObjectId();
                        cObj.CHILD_DEVICE_MAC = currObj.getString(Globals.ChildInformation.DEVICE_MAC);

                        childObjects.add(cObj);
                    }

                    childListItems.setEmptyView(emptyLayout);

                    childListItems.setAdapter(new ChildAdapter(childObjects,ChildrenList.this,emptyLayout));

                }
                else{
                    emptyLayout.setText("No child registered yet.");
                    Toast.makeText(ChildrenList.this, "You don't have any child registered yet.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResume(){
        prg.setMessage("Refreshing children list...");
        prg.show();
        loadChildren();
        super.onResume();
    }

}
