package in.tagbin.covid_tracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.MessagesClient;
import com.google.android.gms.nearby.messages.MessagesOptions;
import com.google.android.gms.nearby.messages.NearbyPermissions;
import com.google.android.gms.nearby.messages.PublishCallback;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentTransitionImpl;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.location.LocationManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import in.tagbin.covid_tracker.ui.add_pass.AddViewPageFragment;
import in.tagbin.covid_tracker.ui.gallery.GalleryFragment;
import in.tagbin.covid_tracker.ui.profile.profileFragment;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private static final int PERMISSIONS_REQUEST = 1;
    public CommonUtility util;
    public ImageView drawer_icon;
    public ImageView profile_icon;
    public DrawerLayout drawer;
    BluetoothAdapter mBluetoothAdapter;


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        drawer_icon = findViewById(R.id.drawer_icon);
        profile_icon = findViewById(R.id.profile);

        // Common Utility
        util = new CommonUtility(this);

        //Registler auth lister in case user is logged out
        util.registerAuthListener();

        //Open drawer here
        drawer_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        //open profile Page here
        profile_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               logOut();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
                Snackbar.make(view, "Logging you out.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        // Check GPS is enabled
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show();
            //finish();
            //TODO receive Location enabled as broadcast
        }

        // Check location permission is granted - if it is, start
        // the service, otherwise request the permission
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            Log.d("DEBUG","STARTING Tracker Service");
            startTrackerService();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }


        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_faq, R.id.nav_profile,R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //hide the title bar
        getSupportActionBar().hide();
    }



    private void startTrackerService() {
        startService(new Intent(this, TrackerService.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Start the service when the permission is granted
            startTrackerService();
        } else {
            finish();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            MessagesClient mMessagesClient = Nearby.getMessagesClient(this, new MessagesOptions.Builder()
                    .setPermissions(NearbyPermissions.BLE)
                    .build());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    public static abstract class StartAtBootServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if( "android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {

                    ComponentName comp = new ComponentName(context.getPackageName(), TrackerService.class.getName());
                    ComponentName service = context.startService(new Intent().setComponent(comp));
                    if (null == service){
                        // something really wrong here
                        Log.d("DEBUG " ,"Something Wrong while receiving Broadcast");
                    }
                }
                else {
                    //Log.Write("Received unexpected intent " + intent.toString(),Log._LogLevel.NORAML);
                }
            } catch (Exception e) {
                //Log.Write("Unexpected error occured in Licensing Server:" + e.toString(),Log._LogLevel.NORAML);
            }
        }
    }

    public  void logOut(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        util.alert("Logging Out");

                        //delete User Token
                        deleteToken();

                        //delete All the shared pref data
                        deleteSharedPreferences();

                        Intent myService = new Intent(MainActivity.this, TrackerService.class);
                        stopService(myService);

                        Intent i = new Intent(MainActivity.this, SplashActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
    }

    public String getToken(){
        SharedPreferences sharedpreferences;
        sharedpreferences = getSharedPreferences(APIUtility.otherStorage, Context.MODE_PRIVATE);
        String token = sharedpreferences.getString("token", null);
        if( null ==  token ){
            //TODO Log the user out
            logOut();
            Toast.makeText(this,"Token expired, please relogin",Toast.LENGTH_LONG).show();
            return "NO";
        }else{
            return token;
        }

    }

    public void deleteToken(){
        SharedPreferences sharedpreferences;
        sharedpreferences = getSharedPreferences(APIUtility.otherStorage, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("token",null);
        editor.commit();
    }

    public void deleteSharedPreferences(){
        try{
            SharedPreferences macBLE = getSharedPreferences(APIUtility.otherStorage, Context.MODE_PRIVATE);
            SharedPreferences otherData = getSharedPreferences(APIUtility.macSharedPref, Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = macBLE.edit();
            editor.clear();
            editor.commit();

            SharedPreferences.Editor editor1 = otherData.edit();
            editor.clear();
            editor.commit();
        }catch (Exception e){
            Log.d("DEBUG","Error while deleting Shared Pref"+e.getMessage());
        }


    }
}
