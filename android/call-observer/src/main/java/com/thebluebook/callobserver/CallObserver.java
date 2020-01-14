package com.thebluebook.callobserver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

@NativePlugin()
public class CallObserver extends Plugin { //implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = CallObserver.class.getSimpleName();

    private String[] permissions = {
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.PROCESS_OUTGOING_CALLS
    };
    private static final String CALL_RECEIVER_BROADCAST_ACTION = "com.thebluebook.callobserver.CALL_RECEIVER_BROADCAST";
    //    private CallReceiver callReceiver;
    private BroadcastReceiver callReceiver;
    private String callStatus = CallStatus.NONE.toString();

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static boolean isIncoming;
    private static String savedNumber;


    @Override
    public void load() {
        super.load();
        this.setupReceiver();
    }

    private void setupReceiver() {
        Log.d(TAG, "Plugin setupReceiver method called.");
        if (callReceiver == null) {
            this.callReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d("RECEIVERX", "IS UP AGAIN....");
                    String action = intent.getAction();
                    String PhoneNumber = "UNKNOWN";

                    if (action == null) return;

                    if (action.equalsIgnoreCase(Intent.ACTION_NEW_OUTGOING_CALL)) {
                        PhoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                        Log.d(TAG, "Outgoing number: " + PhoneNumber);
                        CallObserver.this.callStatus = CallStatus.DIALING.toString();
                    } else if (action.equalsIgnoreCase(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
                        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                            PhoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                            Log.d(TAG, "Incoming number: " + PhoneNumber);
                            CallObserver.this.callStatus = CallStatus.DIALING.toString();
                        }
                        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                            Log.d(TAG, "CALL ENDED... ");
                            CallObserver.this.callStatus = CallStatus.ENDED.toString();

                        }
                        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                            Log.d(TAG, "ACTIVE OUTGOING CALL : ");
                            CallObserver.this.callStatus = CallStatus.ONGOING.toString();
                        }
                        String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
                        Log.d(TAG, stateStr);
                        String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                        int state = 0;
                        if (stateStr != null) {
                            if(stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                                state = TelephonyManager.CALL_STATE_IDLE;
                            }
                            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                                state = TelephonyManager.CALL_STATE_OFFHOOK;
                            }
                            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                                state = TelephonyManager.CALL_STATE_RINGING;
                            }
                        }

                        onCallStateChanged(context, state, number);
                    }
                }

                //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
                //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
                public void onCallStateChanged(Context context, int state, String number) {
                    if(lastState == state){
                        //No change, denounce extras
                        return;
                    }
                    switch (state) {
                        case TelephonyManager.CALL_STATE_RINGING:
//                            isIncoming = true;
//                            callStartTime = new Date();
//                            savedNumber = number;
//                            onIncomingCallReceived(context, number, callStartTime);
                            CallObserver.this.callStatus = CallStatus.DIALING.toString();
                            break;
                        case TelephonyManager.CALL_STATE_OFFHOOK:
                            //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                            if(lastState != TelephonyManager.CALL_STATE_RINGING) {
                                isIncoming = false;
//                                callStartTime = new Date();
//                                onOutgoingCallStarted(context, savedNumber, callStartTime);
                                CallObserver.this.callStatus = CallStatus.ONGOING.toString();
                            }
                            else {
                                isIncoming = true;
//                                callStartTime = new Date();
//                                onIncomingCallAnswered(context, savedNumber, callStartTime);
                                CallObserver.this.callStatus = CallStatus.ONGOING.toString();
                            }
                            break;
                        case TelephonyManager.CALL_STATE_IDLE:
                            //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                            if(lastState == TelephonyManager.CALL_STATE_RINGING) {
                                //Ring but no pickup-  a miss
//                                onMissedCall(context, savedNumber, callStartTime);
                                CallObserver.this.callStatus = CallStatus.MISSED.toString();
                            }
                            else if(isIncoming) {
//                                onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                                CallObserver.this.callStatus = CallStatus.ENDED.toString();
                            }
                            else {
//                                onOutgoingCallEnded(context, savedNumber, callStartTime, new Date());
                                CallObserver.this.callStatus = CallStatus.ENDED.toString();
                            }
                            break;
                    }
                    lastState = state;
                    Log.d(TAG, CallObserver.this.callStatus);
                }
            };

            registerBroadcastReceiver();
        }


    }

    private void registerBroadcastReceiver() {
        Log.d(TAG, "Plugin registerBroadcastReceiver method called.");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        intentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        getActivity().registerReceiver(this.callReceiver, intentFilter);
    }

    @PluginMethod()
    public void observe(PluginCall call) {
        if (checkAndRequestPerms()) {
            Log.d(TAG, "All permissions granted.");
            call.success(getStatusJSObject());
        } else {
            call.error("Permissions needed.");
        }
    }

    private boolean checkAndRequestPerms() {
        int PERMISSIONS_REQUEST_CODE_ASK_MULT_PERMS = 300;
        List<String> permissionsNeeded = new ArrayList<>();

        for (String perm : this.permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(getContext(), perm);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(perm);
            }
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(),
                    permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                    PERMISSIONS_REQUEST_CODE_ASK_MULT_PERMS
            );
            return false;
        }

        return true;
    }

    private JSObject getStatusJSObject() {
        Log.d(TAG, this.callStatus);
        JSObject ret = new JSObject();
        JSObject item = new JSObject();

        item.put("status", this.callStatus);
        ret.put("data", item);

        try {
            Log.d(TAG, ret.toString(4));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ret;
    }
}
