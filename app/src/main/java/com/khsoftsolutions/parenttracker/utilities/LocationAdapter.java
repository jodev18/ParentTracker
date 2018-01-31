package com.khsoftsolutions.parenttracker.utilities;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.khsoftsolutions.parenttracker.R;
import com.khsoftsolutions.parenttracker.objects.TrackObject;

import java.util.List;

/**
 * Created by myxroft on 07/10/2017.
 */

public class LocationAdapter extends BaseAdapter {

    private List<TrackObject> trackObjects;
    private Activity act;

    public LocationAdapter(List<TrackObject> trackObjectList, Activity activity){
        this.trackObjects = trackObjectList;
        this.act = activity;
    }


    @Override
    public int getCount() {
        return trackObjects.size();
    }

    @Override
    public TrackObject getItem(int i) {
        return trackObjects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return (long)i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        TrackObject trackObject = trackObjects.get(i);

        if(view == null){
            view = act.getLayoutInflater().inflate(R.layout.layout_location_list_item,null);
        }

        TextView tDate = (TextView)view.findViewById(R.id.tvLocDate);
        TextView tLocCoords = (TextView)view.findViewById(R.id.tvLocData);

        tDate.setText(trackObject.LOC_TIMESTAMP);
        tLocCoords.setText(Double.valueOf(trackObject.GEOPOINT.latitude).toString() + ","
                            + Double.valueOf(trackObject.GEOPOINT.longitude).toString());

        return view;
    }
}
