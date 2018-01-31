package com.khsoftsolutions.parenttracker.activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.khsoftsolutions.parenttracker.R;
import com.khsoftsolutions.parenttracker.core.Globals;
import com.khsoftsolutions.parenttracker.objects.TrackObject;
import com.khsoftsolutions.parenttracker.utilities.LocationAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class LocationHistory extends AppCompatActivity {

    @BindView(R.id.lvLocHist) ListView locHistory;

    private String CHILD_OBJECT_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_history);

        ButterKnife.bind(this);

        CHILD_OBJECT_ID = this.getIntent().getStringExtra(Globals.CHILD_OBJID_EXTRA);

        if(CHILD_OBJECT_ID.length() > 0){
            displayLocationHistory();
        }
        else{
            Toasty.error(getApplicationContext(),
                    "No child object assigned.",Toast.LENGTH_LONG).show();
            finish();
        }

    }

    private void displayLocationHistory(){

        ParseQuery<ParseObject> pq = ParseQuery.getQuery(Globals.TrackInformation.OBJ_NAME);
        pq.whereEqualTo(Globals.TrackInformation.CHILD_OBJ_ID,CHILD_OBJECT_ID);
        //pq.setLimit(1);
        pq.orderByDescending("createdAt");

        pq.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> objects, ParseException e) {

                if(e == null){

                    List<TrackObject> tObjs = new ArrayList<TrackObject>();

                    int sizes = objects.size();

                    for(int i=0;i<sizes;i++){
                        TrackObject trackObject = new TrackObject();

                        ParseObject currObj = objects.get(i);

                        trackObject.CHILD_OBJ_ID = currObj.getString(Globals.TrackInformation.CHILD_OBJ_ID);
                        trackObject.LOC_TIMESTAMP = currObj.getString(Globals.TrackInformation.TRACK_TIMESTAMP);
                        trackObject.GEOPOINT = new LatLng(currObj.getParseGeoPoint(Globals.TrackInformation.GEO_POINT).getLatitude()
                                ,currObj.getParseGeoPoint(Globals.TrackInformation.GEO_POINT).getLongitude());

                        tObjs.add(trackObject);
                    }

                    LocationAdapter locationAdapter = new LocationAdapter(tObjs,LocationHistory.this);

                    locHistory.setAdapter(locationAdapter);

                }
                else{
                    Toasty.error(LocationHistory.this,
                            e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
