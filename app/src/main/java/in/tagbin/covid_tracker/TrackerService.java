package in.tagbin.covid_tracker;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.Manifest;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import in.tagbin.covid_tracker.ui.home.HomeFragment;

public class TrackerService extends Service {

    private static final String TAG = TrackerService.class.getSimpleName();
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    public Location lastLocation;
    public CommonUtility util;
    public static DatabaseReference dbRef;
    public static FirebaseDatabase database;
    private PowerManager.WakeLock mWakelock;
    public APIUtility api;

    BluetoothAdapter mBluetoothAdapter;
    public String[] macArray = new String[]{};

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        //        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(FirebaseAuth.getInstance().getCurrentUser() != null) {

                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    switch (state) {
                        case BluetoothAdapter.STATE_OFF:
                            Log.d("DEBUG", "BLUETOOTH OFF");
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            Log.d("DEBUG", "BLUETOOTH TRUNING ON");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            Log.d("DEBUG", "BLUETOOTH ON");
                            // START NEARBY SERVICES AGAIN
                            startNearByServices();
                            break;
                        case BluetoothAdapter.STATE_TURNING_ON:
                            Log.d("DEBUG", "BLUETOOTH TURNING ON");
                            break;
                    }

                }

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                    //Check if mac already exist
//                    Object macSavedCallback = null;
//                    if (!checkIfMacExistInSharedPref("")) {
//                        sendBluetoothMapping("");
//                        Log.d("DEBUG", "MAC Not IN Shared pref -" + device.getAddress());
//                    } else {
//                        Log.d("DEBUG", "MAC In Shared pref -" + device.getAddress());
//                    }


                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    //Log.v("DEBUG", "discovery Finished ");
                    mBluetoothAdapter.startDiscovery();
                } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                   // Log.v("DEBUG", "discovery Starting ");
                }
            }
        }

    };

    @Override
    public IBinder onBind(Intent intent) {return null;}

    @Override
    public void onCreate() {
        super.onCreate();
        lastLocation = new Location("");
        //loginToFirebase();
        util = new CommonUtility( getApplication());


        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            Log.d("DEBUG","requestLocationUpdate calledl");
            // Hold a partial wake lock to keep CPU awake when the we're tracking location.
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            mWakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TAG:WAKE_LOCK_TAG");
            mWakelock.acquire();

            requestLocationUpdates();
            //startBluetoothSearch();

        }else{
            Log.d("DEBUG","Tracker Service Not Created Auth Fail");
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            createNotificationChannel();
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, 0);
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.notification_text))
                    .setSmallIcon(R.drawable.logo_small)
                    .setContentIntent(pendingIntent)
                    .build();
            startForeground(1, notification);
            //do heavy work on a background thread
            //stopSelf();



            startNearByServices();
//            final Handler handler = new Handler(Looper.getMainLooper());
//            final int delay = 15000; //milliseconds
//
//            handler.postDelayed(new Runnable(){
//                public void run(){
//                    //do something
//                    Log.d("DEBUG", "Restarting Nearby services`.");
//                    startNearByServices();
//                }
//            }, delay);

            Log.d("DEBUG", "Corona Shield Service onStartComamand");

        }
        return START_STICKY;
    }

    public void startNearByServices(){
        Nearby.getConnectionsClient(getApplicationContext()).stopAllEndpoints();
        startAdvertising();
        startDiscovery();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onDestroy() {

        // Release the wakelock
        if (mWakelock != null) {
            mWakelock.release();
        }
        super.onDestroy();

    }


    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "received stop broadcast");
            // Stop the service when the notification is tapped
            unregisterReceiver(stopReceiver);
            stopSelf();
        }
    };

    private void loginToFirebase() {
        // Authenticate with Firebase, and request location updates
        String email = getString(R.string.firebase_email);
        String password = getString(R.string.firebase_password);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "firebase auth success");
                    requestLocationUpdates();
                } else {
                    Log.d(TAG, "firebase auth failed");
                }
            }
        });
    }

    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();
        request.setInterval(10000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Log.d(TAG, "Location Update Received");
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);

        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            // Request location updates and when an update is
            // received, store the location in Firebase
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {


                    Location location = locationResult.getLastLocation();
                    float distance = location.distanceTo(lastLocation);
                    //Log.d("DEBUG","Distance was: "+ distance);
                    Log.d("DEBUG","Service Running...");

                    if(distance >= CommonUtility.DISTANCE_THRESHOLD)
                    {

                        if (location != null) {

                            try {
//
                                Map<String, Object> lat_long = new HashMap<>();
                                lat_long.put("lat", location.getLatitude());
                                lat_long.put("lng", location.getLongitude());
                                lat_long.put("time", new Date().getTime());

                                // Update chile Element
                                //ref.updateChildren(transportStatus);
                                util.saveSensorDataInFirestore(util.USER_COLLECTION, util.user.getPhoneNumber(), lat_long,util.LOCATION_COLLECTION);

                                Log.d("DEBUG", " UPDATED " );
                            }catch(Exception e){
                                Log.d("DEBUG", "Error: "+ e.getMessage());
                            }
                        }
                    }
                    lastLocation = location;

                }
            }, null);
        }
    }


    public void sendBluetoothMapping(final String mac){
        updateMacSharedPreferences(mac);
    }

    public void updateMacSharedPreferences(String mac){
        SharedPreferences sharedpreferences;
        sharedpreferences = getSharedPreferences(APIUtility.macSharedPref, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(mac,mac);
        editor.commit();

    }

    public boolean checkIfMacExistInSharedPref(String mac){
        SharedPreferences sharedpreferences;
        sharedpreferences = getSharedPreferences("scannedMAC", Context.MODE_PRIVATE);
        if(null ==  sharedpreferences.getString(mac,null)){
            return false;
        }else{
            return true;
        }

    }

    //ALL THE NEARBY CODES
    public String getUserNickname(){

        try {
            return util.getCurrentUser().getPhoneNumber();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("DEBUG","User Unauthorised or not signed in");
            return null;
        }
    }

    private void startAdvertising() {
        AdvertisingOptions advertisingOptions =
                new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build();
        Nearby.getConnectionsClient(getApplicationContext())
                .startAdvertising(
                        getUserNickname(), "TAGBIN", connectionLifecycleCallback, advertisingOptions)
                .addOnSuccessListener(
                        (Void unused) -> {
                            Log.d("DEBUG","STARTED ADVERTISING");
                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            Log.d("DEBUG","FAILURE"+e.getMessage());
                        });
    }

    private void startDiscovery() {
        DiscoveryOptions discoveryOptions =
                new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build();
        Nearby.getConnectionsClient(getApplicationContext())
                .startDiscovery("TAGBIN", endpointDiscoveryCallback, discoveryOptions)
                .addOnSuccessListener(
                        (Void unused) -> {
                            Log.d("DEBUG","STARTED DISCOVERING");
                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            // We're unable to start discovering.
                            Log.d("DEBUG","DISCOVERY FAILURE"+e.getMessage());
                        });
    }

    private final EndpointDiscoveryCallback endpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {

                    Log.d("DEBUG",info.getEndpointName());


                    String firebase_mobile = null;
                    String user_mobile = info.getEndpointName();
                    firebase_mobile = user_mobile.substring(user_mobile.length() - 12);

                    if (!checkIfMacExistInSharedPref(info.getEndpointName())) {

                        sendBluetoothMapping(firebase_mobile);

                        Map<String, Object> nearby_data = new HashMap<>();
                        nearby_data.put("my_phone", util.user.getPhoneNumber());
                        nearby_data.put("their_phone", firebase_mobile);
                        nearby_data.put("time", new Date().getTime());

                        try {
                            //Save data in Firestore
                            util.saveSensorDataInFirestore(util.USER_COLLECTION, util.user.getPhoneNumber(), nearby_data, util.NEARBY_COLLECTION);
                        }catch(Exception e){
                            Log.d("DEBUG","Error while saving nearby "+e.getMessage());
                        }


                        Log.d("DEBUG", "New device Found -" +firebase_mobile);
                    } else {
                        Log.d("DEBUG", "Old Device -" + firebase_mobile);
                    }

                    // An endpoint was found. We request a connection to it.
                    Nearby.getConnectionsClient(getApplicationContext())
                            .requestConnection(getUserNickname(), endpointId, connectionLifecycleCallback)
                            .addOnSuccessListener(
                                    (Void unused) -> {
                                        Log.d("DEBUG","ENDPOINT DISCOVERY Connection received"+endpointId);

                                    })
                            .addOnFailureListener(
                                    (Exception e) -> {
                                        // Nearby Connections failed to request the connection.
                                        Log.d("DEBUG","ENDPOINT DISCOVERY Connection failed"+e.getMessage());
                                    });
                }

                @Override
                public void onEndpointLost(String endpointId) {
                    // A previously discovered endpoint has gone away.
                    Log.d("DEBUG","A previously discovered endpoint has gone away.");
                }
            };


    private final ConnectionLifecycleCallback connectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                    // Automatically accept the connection on both sides.
                    Nearby.getConnectionsClient(getApplicationContext()).acceptConnection(endpointId, new PayloadCallback() {
                        @Override
                        public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {
                            byte[] receivedBytes = payload.asBytes();
                            String receivedString = new String(receivedBytes);
//                            Log.d("DEBUG","Payload Received:"+receivedString +" - "+receivedBytes.toString());
                        }

                        @Override
                        public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {
//                            Log.d("DEBUG","Payload Received "+payloadTransferUpdate.getStatus());
                        }
                    });
                }

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {
                    switch (result.getStatus().getStatusCode()) {
                        case ConnectionsStatusCodes.STATUS_OK:
                            // We're connected! Can now start sending and receiving data.
                            Log.d("DEBUG"," CONNECTION LIFE Connected Success"+endpointId);
                            // WAS SENDING DATA HERE
                            break;
                        case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                            // The connection was rejected by one or both sides.
                            Log.d("DEBUG","CONNECTION LIFE Connected Rejected");
                            break;
                        case ConnectionsStatusCodes.STATUS_ERROR:
                            // The connection broke before it was able to be accepted.
                            Log.d("DEBUG","CONNECTION LIFE Connected ERROR");
                            break;
                        default:
                            // Unknown status code
                            Log.d("DEBUG","CONNECTION LIFE Connected Unknown error code");
                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    // We've been disconnected from this endpoint. No more data can be
                    // sent or received.
                    Log.d("DEBUG"," Disconnected from endpoint");
                }
            };


}