package com.khsoftsolutions.parenttracker.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.khsoftsolutions.parenttracker.R;
import com.khsoftsolutions.parenttracker.core.Globals;
import com.khsoftsolutions.parenttracker.objects.ChildObject;
import com.khsoftsolutions.parenttracker.objects.ParentObject;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import me.aflak.bluetooth.Bluetooth;

public class ChildInformationActivity extends AppCompatActivity {

    @BindView(R.id.etChildFName) EditText childFN;
    @BindView(R.id.etChildMName) EditText childMN;
    @BindView(R.id.etChildLName) EditText childLN;
    @BindView(R.id.etChildAge) EditText childAge;
    @BindView(R.id.spGender) Spinner spGender;
    @BindView(R.id.etChildAddress) EditText childAddr;
    @BindView(R.id.spUpdateInterval) Spinner spUpdateInt;

    @BindView(R.id.btnAddChild) Button addChild;
    //@BindView(R.id.btnAssignDevice) Button addDevice;

    private String selectedGender;

    private ProgressDialog prg;

    //private SmoothBluetooth mSmoothBluetooth;
    private Bluetooth ble2;

    private Handler h;

    private String CHILD_MAC;

    private AlertDialog blueList;

    private Integer[] TimeInterval = {60,600,900,1800,3600,7200};
    private Integer selectedInterval = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_information);

        ButterKnife.bind(this);

        setTitle("Add Child");

        h = new Handler(getMainLooper());

        initSpinner();

        Snackbar.make(addChild,"Scroll down to see the " +
                "\"add child\" button.",Snackbar.LENGTH_LONG).show();

        //For progress updates beyond Marshmallow
        if(Build.VERSION.SDK_INT >= 26){

        }
        else{
            prg = new ProgressDialog(ChildInformationActivity.this);
            prg.setMessage("Adding child tracker...");
            prg.setCancelable(false);
        }


        initAddChildBtn();

        if(Build.VERSION.SDK_INT >= 17){
            Dexter.withActivity(ChildInformationActivity.this)
                    .withPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    .withListener(new PermissionListener() {
                        @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                            //initBlueTooth();
                        }
                        @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                            coarseLocDenied();
                        }
                        @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        }
                    }).check();
        }

        // initBlueTooth();
        //initSmooth();
    }

    private void initBlueTooth(){

        //Initialize progressdialog and display info
        prg.setMessage("Initializing bluetooth...");
        prg.show();

        ble2 = new Bluetooth(this);
        ble2.enableBluetooth();

        final List<BluetoothDevice> devices = new ArrayList<>();

        ble2.setDiscoveryCallback(new Bluetooth.DiscoveryCallback() {
            @Override
            public void onFinish() {

                prg.dismiss();

                if(devices.size() > 0){
                    showListDevices(devices);
                }
                else{
                    Toasty.info(ChildInformationActivity.this, "There were no devices found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDevice(BluetoothDevice device) {
                
                if(!devices.contains(device)) devices.add(device);

                prg.setMessage("Discovered " + device.getName());
                //Toast.makeText(ChildInformationActivity.this, "Discovered "
                        //+ device.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPair(BluetoothDevice device) {
                Toasty.info(ChildInformationActivity.this,
                        "Paired with " + device.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUnpair(BluetoothDevice device) {
                Toasty.warning(ChildInformationActivity.this, "Unpaired with "
                        + device.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String message) {

                if(prg != null && prg.isShowing()) prg.dismiss();

                Toasty.error(ChildInformationActivity.this,
                        "Error while connecting: " + message, Toast.LENGTH_SHORT).show();
            }
        });

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                prg.setMessage("Scanning for devices...");
            }
        },1200);

        ble2.scanDevices();

        ble2.setCommunicationCallback(new Bluetooth.CommunicationCallback() {
            @Override
            public void onConnect(final BluetoothDevice device) {
                //Toast.makeText(ChildInformationActivity.this, "Connected to "
                        //+ device.getName(), Toast.LENGTH_SHORT).show();
                h.post(new Runnable() {
                    @Override
                    public void run() {

                        //if(prg.isShowing()) prg.dismiss();

                        Toasty.info(ChildInformationActivity.this, "Connected to: " + device.getName(), Toast.LENGTH_LONG).show();

                        CHILD_MAC = device.getAddress();

                        addChildFinal();
                    }
                });


            }

            @Override
            public void onDisconnect(final BluetoothDevice device, String message) {
                //Toast.makeText(ChildInformationActivity.this,
                       // "Disconnected from " + device.getName(), Toast.LENGTH_SHORT).show();
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        Toasty.info(ChildInformationActivity.this,
                                "Disconnected from: " + device.getName(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onMessage(final String message) {
                //Toast.makeText(ChildInformationActivity.this,
                        //"Received message: " + message, Toast.LENGTH_SHORT).show();
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        Toasty.info(ChildInformationActivity.this,
                                "Received message: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String message) {
                //Toast.makeText(ChildInformationActivity.this,
                  //      "Error encountered: " + message, Toast.LENGTH_SHORT).show();
                h.post(new Runnable() {
                    @Override
                    public void run() {

                        if(prg != null && prg.isShowing()) prg.dismiss();

                        Toasty.error(ChildInformationActivity.this,
                                "There was an error encountered.", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("BLUE_CONN_ERROR", "Error encountered onError: " + message);
            }

            @Override
            public void onConnectError(BluetoothDevice device, String message) {
                h.post(new Runnable() {
                    @Override
                    public void run() {

                        if(prg != null && prg.isShowing()) prg.dismiss();

                        Toasty.error(ChildInformationActivity.this,
                                "There was an error encountered.", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("BLUE_CONN_ERROR", "Error encountered onConnect: " + message);
            }
        });
    }

    private void showListDevices(final List<BluetoothDevice> devices){

        final String[] deviceNames =  new String[devices.size()];
        int devSize = devices.size();

        for(int i=0;i<devSize;i++){
            deviceNames[i] = devices.get(i).getName();
        }

        AlertDialog.Builder bList = new AlertDialog.Builder(ChildInformationActivity.this);
        bList.setTitle("Devices");

        bList.setSingleChoiceItems(deviceNames, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //ble2.pair(devices.get(i));

                prg.setMessage("Connecting to " + deviceNames[i] + "...");
                prg.show();

                ble2.connectToDevice(devices.get(i));

                dialogInterface.dismiss();
            }
        });

        bList.setCancelable(false);

        blueList = bList.create();

        blueList.show();
    }

    private void addChildFinal(){

        final String fn = childFN.getText().toString();
        final String mn = childMN.getText().toString();
        final String ln = childLN.getText().toString();
        final String age = childAge.getText().toString();
        final String addr = childAddr.getText().toString();

        if(prg != null){
            prg.setMessage("Saving child information...");
            prg.show();
        }

        final ParseObject childInfo
                = new ParseObject(Globals.ChildInformation.OBJ_NAME);

        childInfo.put(Globals.ChildInformation.FIRST_NAME,fn);
        childInfo.put(Globals.ChildInformation.MIDDLE_NAME,mn);
        childInfo.put(Globals.ChildInformation.LAST_NAME,ln);
        childInfo.put(Globals.ChildInformation.ADDRESS,addr);
        childInfo.put(Globals.ChildInformation.CHILD_AGE,age);
        childInfo.put(Globals.ChildInformation.GENDER,selectedGender);
        childInfo.put(Globals.ChildInformation.DEVICE_MAC,CHILD_MAC);
        childInfo.put(Globals.ChildInformation.PARENT_OBJ_ID,
                ParseUser.getCurrentUser().getObjectId());

        //Wrap all fields in a child object
        final ChildObject childObject = new ChildObject();

        childObject.CHILD_FIRST_NAME = fn;
        childObject.CHILD_MIDDLE_NAME = mn;
        childObject.CHILD_LAST_NAME = ln;
        childObject.CHILD_ADDRESS = addr;
        childObject.CHILD_AGE = age;
        childObject.CHILD_GENDER = selectedGender;
        childObject.CHILD_DEVICE_MAC = CHILD_MAC;
        childObject.CHILD_PARENT_OBJ_ID = ParseUser.getCurrentUser().getObjectId();

        childInfo.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                //prg.dismiss();

                if(e==null){

                    prg.setMessage("Sending information to the child tracker...");

                    //Get parent information to be sent to the child app as well
                    final ParseQuery<ParseObject> parentInfo
                            = ParseQuery.getQuery(Globals.ParentInformation.OBJ_NAME);

                    parentInfo.whereEqualTo(Globals.ParentInformation.PARENT_OBJ_ID,
                            ParseUser.getCurrentUser().getObjectId());

                    parentInfo.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {

                            if(e==null){

                                //Get parent information
                                ParseObject pObj = objects.get(0);

                                ParentObject parentObject = new ParentObject();

                                parentObject.PARENT_FIRST_NAME = pObj.getString(Globals.ParentInformation.FIRST_NAME);
                                parentObject.PARENT_MID_NAME = pObj.getString(Globals.ParentInformation.MIDDLE_NAME);
                                parentObject.PARENT_LAST_NAME = pObj.getString(Globals.ParentInformation.LAST_NAME);
                                parentObject.PARENT_AGE = pObj.getString(Globals.ParentInformation.PARENT_AGE);
                                parentObject.PARENT_PHONE_NUMBER = pObj.getString(Globals.ParentInformation.PHONE_NUMBER);
                                parentObject.PARENT_ADDRESS = pObj.getString(Globals.ParentInformation.ADDRESS);


                                //Object id
                                childObject.OBJECT_ID = childInfo.getObjectId();

                                Gson gson = new Gson();

                                String childJson = gson.toJson(childObject);
                                String parentJson = gson.toJson(parentObject);

                                String toSend = parentJson + "|" + childJson + "|" + selectedInterval.toString();

                                Log.d("DATA_SENT",toSend);

                                ble2.send(toSend + "\n");

                                Toasty.info(ChildInformationActivity.this,toSend,Toast.LENGTH_LONG).show();

                                if(prg != null){
                                    if(prg.isShowing()){
                                        prg.dismiss();
                                    }
                                }

                                childSuccess();
                            }
                            else{
                                Toasty.error(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
                else{
                    Toasty.error(ChildInformationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    Toasty.error(ChildInformationActivity.this, "Failed to save " +
                            "child information online. This will be saved " +
                            "at a later moment.", Toast.LENGTH_LONG).show();
                    //Save child info at a later time.
                    childInfo.saveEventually(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e==null){
                                Toasty.success(ChildInformationActivity.this,
                                        "Saved information for "
                                                + fn + ".", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                //Toast.makeText(ChildInformationActivity.this,
                                  //      e.getMessage(), Toast.LENGTH_SHORT).show();
                                Toasty.error(ChildInformationActivity.this,
                                        "Failed saving information for a child named "
                                                + fn, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    private void initSpinner(){

        String[] choices = new String[]{"Male","Female"};

        ArrayAdapter<String> arrGender
                = new ArrayAdapter<String>(ChildInformationActivity.this,
                android.R.layout.simple_dropdown_item_1line,choices);

        spGender.setAdapter(arrGender);
        spGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedGender = (i==0 ? "M":"F");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String[] timeChoices = new String[] {"1 min","10 min", "15 min"
                                                ,"30 min","1 Hour","2 Hours"};


        ArrayAdapter<String> arrTime = new ArrayAdapter<String>(ChildInformationActivity.this,
                        android.R.layout.simple_dropdown_item_1line,timeChoices);

        spUpdateInt.setAdapter(arrTime);
        spUpdateInt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedInterval = TimeInterval[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void onCancelInputDialog(){
        AlertDialog.Builder cancelAdd
                = new AlertDialog.Builder(ChildInformationActivity.this);
        cancelAdd.setTitle("Cancel Add?");

        cancelAdd.setMessage("Are you sure you want to cancel adding tracker for this child?");
        cancelAdd.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        cancelAdd.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        cancelAdd.create().show();
    }

    private void initAddChildBtn(){

        addChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String fn = childFN.getText().toString();
                final String mn = childMN.getText().toString();
                final String ln = childLN.getText().toString();
                final String age = childAge.getText().toString();
                final String addr = childAddr.getText().toString();

                //Perform validation
                if(fn.length() > 0){
                    if(mn.length() > 0){
                        if(ln.length() > 0){
                            if(age.length() > 0){
                                if(addr.length() > 0){
                                    initBlueTooth();
                                }
                                else
                                    Snackbar.make(childFN,"Please enter your home address.",Snackbar.LENGTH_SHORT).show();
                            }
                            else
                                Snackbar.make(childAge,"Please enter your child's age.",Snackbar.LENGTH_SHORT).show();
                        }
                        else
                            Snackbar.make(childLN,"Please enter your child's last name.",Snackbar.LENGTH_SHORT).show();
                    }
                    else
                        Snackbar.make(childMN,"Please enter your child's middle name.",Snackbar.LENGTH_SHORT).show();
                }
                else
                    Snackbar.make(childFN,"Please enter your child's first name.",Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void childSuccess(){

        AlertDialog.Builder succ = new AlertDialog.Builder(ChildInformationActivity.this);
        succ.setTitle("Child Added!");
        succ.setMessage("Child successfully added! Add another?");
        succ.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                childFN.setText("");
                childAddr.setText("");
                childAge.setText("");
                childLN.setText("");
                childMN.setText("");

                childFN.requestFocus(); //request focus on the first form.
            }
        });
        succ.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        succ.create().show();
    }

    /**
     * Displays a dialog telling the user that he can't add a child
     * because he/she has no adapter required for this process.

    private void showNoBluetoothSupport(){

        AlertDialog.Builder bb = new AlertDialog.Builder(ChildInformationActivity.this);

        bb.setTitle("No Bluetooth");
        bb.setMessage("The device has no bluetooth which is necessary for adding children information." +
                " The form will now close.");

        bb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });

        bb.create().show();
    }*/

    private void coarseLocDenied(){
        AlertDialog.Builder bb = new AlertDialog.Builder(ChildInformationActivity.this);

        bb.setTitle("Permission Denied");
        bb.setMessage("Location permission and device MAC " +
                "which is necessary for setup was denied and is unselected. This form will now close.");

        bb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });

        bb.create().show();
    }
    @Override
    public void onBackPressed(){
        //Checks for any user input and
        //if there's any, confirm the exit of the user.
        String fn = childFN.getText().toString();
        String mn = childMN.getText().toString();
        String ln = childLN.getText().toString();
        String age = childAge.getText().toString();
        String addr = childAddr.getText().toString();

        if(fn.length() > 0 || mn.length() > 0 || ln.length() > 0 ||
                age.length() > 0 || addr.length() > 0){
            onCancelInputDialog();
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Bluetooth cleanup stuff
        if(ble2 != null){
            if(ble2.isConnected()) ble2.disconnect();
            ble2.disableBluetooth();
            ble2.removeCommunicationCallback();
            ble2.removeDiscoveryCallback();
        }

        //mSmoothBluetooth.stop();
    }
}
