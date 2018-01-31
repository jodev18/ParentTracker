package com.khsoftsolutions.parenttracker.activities;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.khsoftsolutions.parenttracker.R;
import com.khsoftsolutions.parenttracker.core.Globals;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class ChildTracking extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Handler h;

    private String CHILD_OBJECT_ID;

    private ProgressDialog prg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_child_tracking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        h = new Handler(this.getMainLooper());
        prg = new ProgressDialog(ChildTracking.this);
        prg.setMessage("Getting location...");

        CHILD_OBJECT_ID = this.getIntent().getStringExtra(Globals.CHILD_OBJID_EXTRA);

        if(CHILD_OBJECT_ID != null){
            if(CHILD_OBJECT_ID.length() > 0){
                prg.show();
                initLocationUpdater();
            }
            else{
                finish();
                Toasty.error(getApplicationContext(),"Unexpected error encountered.", Toast.LENGTH_LONG).show();
            }
        }
        else{
            finish();
        }

    }

    private void initLocationUpdater(){
        h.postDelayed(trackPosition,10000);

    }

    private Runnable trackPosition = new Runnable() {
        @Override
        public void run() {

            //Toasty.info(ChildTracking.this,"Updating coordinates...",Toast.LENGTH_LONG).show();

            ParseQuery<ParseObject> pq = ParseQuery.getQuery(Globals.TrackInformation.OBJ_NAME);
            pq.whereEqualTo(Globals.TrackInformation.CHILD_OBJ_ID,CHILD_OBJECT_ID);
            pq.setLimit(1);
            pq.orderByDescending("createdAt");

            pq.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(final List<ParseObject> objects, ParseException e) {
                    if(e == null){

                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);


                        int ss = objects.size();

                        final LatLng loc = new LatLng(objects.get(ss-1).getParseGeoPoint(Globals.TrackInformation.GEO_POINT).getLatitude(),
                                objects.get(ss-1).getParseGeoPoint(Globals.TrackInformation.GEO_POINT).getLongitude());

                        final int sss = ss;
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                prg.dismiss();
                                mMap.addCircle(new CircleOptions().center(loc).radius(1.0).strokeColor(Color.GREEN));
                                mMap.addMarker(new MarkerOptions().position(loc).title(objects.get(sss-1).getString(Globals.TrackInformation.TRACK_TIMESTAMP)));
                            }
                        },600);

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc , 24.0f));

                    }
                    else{
                        Toasty.error(ChildTracking.this,
                                e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });

            h.postDelayed(this,10000);
        }
    };



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        initLocationUpdater();
    }

    @Override
    public void onDestroy(){

        h.removeCallbacks(trackPosition);

        super.onDestroy();
    }
}
