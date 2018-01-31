package com.khsoftsolutions.parenttracker.activities;

import android.app.ProgressDialog;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.khsoftsolutions.parenttracker.R;
import com.khsoftsolutions.parenttracker.core.Globals;
import com.khsoftsolutions.parenttracker.utilities.HTMLConstructor;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DisplayParentInfo extends AppCompatActivity {

    @BindView(R.id.btnEditParentInfo) Button editInfo;
    @BindView(R.id.wvParentDisp) WebView pInfoDisp;

    ProgressDialog prg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diplay_parent_info);

        setTitle("Parent Information");

        ButterKnife.bind(this);

        if(Build.VERSION.SDK_INT >= 23){

        }
        else{
            prg = new ProgressDialog(DisplayParentInfo.this);
            prg.setMessage("Loading parent information...");
            prg.setCancelable(false);
        }

        initEditInfo();
    }



    private void initEditInfo(){

        if(prg != null) prg.show();

        ParseQuery<ParseObject> parentInfo
                = ParseQuery.getQuery(Globals.ParentInformation.OBJ_NAME);

        parentInfo.whereEqualTo(Globals.ParentInformation.PARENT_OBJ_ID,
                ParseUser.getCurrentUser().getObjectId());
        parentInfo.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if(prg != null) prg.dismiss();

                if(e == null){

                    if(objects.size() > 0){

                        //Get parent information
                        ParseObject pObj = objects.get(0);

                        String p_fn = pObj.getString(Globals.ParentInformation.FIRST_NAME);
                        String p_mn = pObj.getString(Globals.ParentInformation.MIDDLE_NAME);
                        String p_ln = pObj.getString(Globals.ParentInformation.LAST_NAME);
                        String p_age = pObj.getString(Globals.ParentInformation.PARENT_AGE);
                        String p_phone = pObj.getString(Globals.ParentInformation.PHONE_NUMBER);
                        String p_addr = pObj.getString(Globals.ParentInformation.ADDRESS);

                        String[] labels = {"First Name: ", "Middle Name: ", "Last Name: ", "Age: ",
                                            "Phone Number: ", "Address: "};

                        String[] info = {p_fn,p_mn,p_ln,p_age,p_phone,p_addr};

                        HTMLConstructor ht = new HTMLConstructor();

                        ht.start();
                        ht.addHeader("About " + p_fn);
                        ht.addTable(labels,info);
                        ht.end();

                        //Toast.makeText(DisplayParentInfo.this, ht.getHTMLString()
                            //   , Toast.LENGTH_LONG).show();

                        pInfoDisp.loadData(ht.getHTMLString(),"text/html","UTF-8");

                    }
                    else{
                        Toast.makeText(DisplayParentInfo.this, "No parent information yet (?)", Toast.LENGTH_SHORT).show();
                    }
                }
                else{

                }
            }
        });



        editInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


}
