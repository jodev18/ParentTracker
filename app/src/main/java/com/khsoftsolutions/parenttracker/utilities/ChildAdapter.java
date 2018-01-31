package com.khsoftsolutions.parenttracker.utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.khsoftsolutions.parenttracker.activities.ChildTracking;
import com.khsoftsolutions.parenttracker.R;
import com.khsoftsolutions.parenttracker.activities.LocationHistory;
import com.khsoftsolutions.parenttracker.core.Globals;
import com.khsoftsolutions.parenttracker.objects.ChildObject;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by myxroft2 on 9/8/17.
 */

public class ChildAdapter extends BaseAdapter {

    private List<ChildObject> children;
    private Activity activity;
    private TextView emptyTV;

    private ProgressDialog prg;

    public ChildAdapter(List<ChildObject> childObjectList, Activity act, TextView empty){
        this.children = childObjectList;
        this.activity = act;
        this.emptyTV = empty;

        if(Build.VERSION.SDK_INT >= 26){
            //Use progressbar for android O
        }
        else{
            //Initialize progressdialog
            prg = new ProgressDialog(act);
            prg.setCancelable(false);
        }
    }

    @Override
    public int getCount() {
        return children.size();
    }

    @Override
    public ChildObject getItem(int i) {
        return children.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        TextView childName;
        TextView childAge;

        ImageView imgGender;
        ImageButton imgBtEdit;
        ImageButton imgBtRemove;
        ImageButton imgBtMap;

        ChildObject cObj = children.get(i);

        if(view == null) {
            view = activity.getLayoutInflater().inflate(R.layout.list_item_child, null);
        }

        childName = view.findViewById(R.id.tvListChildName);
        childAge = view.findViewById(R.id.tvListChildAge);

        childAge.setText(cObj.CHILD_AGE + " y. o.");
        childName.setText(cObj.CHILD_FIRST_NAME);

        imgGender = view.findViewById(R.id.imgChildGender);

        imgGender.setImageResource(cObj.CHILD_GENDER.equals("F")
                ? R.drawable.girl_child:R.drawable.boy_child);

        imgBtEdit = view.findViewById(R.id.imgBtnEdit);
        imgBtRemove = view.findViewById(R.id.imgBtnRemove);
        imgBtMap = view.findViewById(R.id.imgGoToMap);

        //Listeners for edit and remove button
        initButtonListeners(imgBtEdit,imgBtRemove,imgBtMap,i);


        return view;
    }

    private void confirmRemoveDialog(final ChildObject childObject, final int pos){

        AlertDialog.Builder confRemove = new AlertDialog.Builder(activity);

        confRemove.setTitle("Remove Tracker");
        confRemove.setMessage("Are you sure you want to remove tracker for this child? " +
                "This will remove all information about the child and is irreversible.");

        confRemove.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Perform removal
                removeChildTracker(childObject.OBJECT_ID, pos);
            }
        });

        confRemove.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        confRemove.create().show();
    }


    private void initButtonListeners(ImageButton edit, ImageButton remove, ImageButton map,final int pos){

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder showOptions = new AlertDialog.Builder(activity);
                showOptions.setTitle("Location Options--" + children.get(pos).CHILD_FIRST_NAME);

                String[] choices = {"Show Latest Location","Location History"};

                showOptions.setSingleChoiceItems(choices, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                activity.startActivity(new Intent()
                                        .setClass(activity, ChildTracking.class)
                                        .putExtra(Globals.CHILD_OBJID_EXTRA,children.get(pos).OBJECT_ID));
                                break;
                            case 1:
                                activity.startActivity(new Intent()
                                        .setClass(activity, LocationHistory.class)
                                        .putExtra(Globals.CHILD_OBJID_EXTRA,children.get(pos).OBJECT_ID));
                                break;

                            default:
                        }
                    }
                });

                showOptions.create().show();

            }
        });

        //TODO Passing of params
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmRemoveDialog(children.get(pos),pos);
            }
        });
    }

    private void removeChildTracker(String objId, final int position){
        prg.setMessage("Removing tracker...");
        prg.show();

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Globals.ChildInformation.OBJ_NAME);
        query.getInBackground(objId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    object.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null){
                                prg.dismiss();

                                Toast.makeText(activity, "Successfully removed tracker!", Toast.LENGTH_SHORT).show();

                                //remove item from listview as well.
                                children.remove(position);

                                if(children.size() == 0){
                                    emptyTV.setText("There are no children saved yet." +
                                            " Tap the plus button below to add child.");
                                }

                                notifyDataSetChanged();

                            }
                            else{
                                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    // something went wrong
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
