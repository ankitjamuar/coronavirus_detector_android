package in.tagbin.covid_tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import in.tagbin.covid_tracker.ui.slideshow.PassReaderDbHelper;

public class CommonUtility {


    public boolean SHOW_POPUP = true;
    public Activity context;
    public Context con;
    public static String DATABASE_NAME = "locations";
    public static int DISTANCE_THRESHOLD = 10;


    public static String USER_COLLECTION = "users";

    //PROFILE
    public static String USER_LOCATION_TRACKING_BOOLEAN = "location_tracking";
    public static String USER_CONTACT_SYNC_BOOLEAN = "contact_sync";
    public String USER_FULL_NAME = "full_name";
    public String USER_MOBILE = "mobile";
    public String USER_GENDER = "gender";
    public String USER_DISTRICT = "district";
    public String USER_TEHSIL = "tehsil";
    public String USER_ADDRESS = "address";

    //EPASS
    public String EPASS_COLLECTION = "e_pass";
    public String EPASS_CATEGORY = "category";
    public String EPASS_CATEGORY_TYPE = "category_type";
    public String EPASS_FROM = "from";
    public String EPASS_TO = "to";
    public String EPASS_START_DATE = "start_date";
    public String EPASS_START_TIME = "start_time";
    public String EPASS_END_TIME = "end_time";
    public String EPASS_REASON = "reason";
    public String EPASS_VECHILE_NUMBER = "vechile";
    public String EPASS_STATUS = "status";
    public String EPASS_DOCUMENT = "document";

    //LOCATION
    public String LOCATION_COLLECTION = "locations";

    //NEARBY
    public String NEARBY_COLLECTION = "near_by";

    //STATISTICS
    public static String COVID_STATS = "statistics";
    public static String COVID_STATS_INDIA_CURED_TOTAL = "india_cured_case";
    public static String COVID_STATS_INDIA_CASE_TOTAL = "india_total_case";
    public static String COVID_STATS_INDIA_DEATH_TOTAL = "india_death_case";

    FirebaseFirestore db;
    public FirebaseUser user;

    //Constructor for Context aware calls
    public CommonUtility(Activity con){
        this.context = con;
        // Create Firebase instance
        db = FirebaseFirestore.getInstance();
        user  = FirebaseAuth.getInstance().getCurrentUser();

    }

    //Constructor for Context aware calls
    public CommonUtility(Context con){
        this.con = con;

    }

    //Constructor for non Context aware calls
    public CommonUtility(){
        // Create Firebase instance
        db = FirebaseFirestore.getInstance();
        user  = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void alert(String text){
        if(this.SHOW_POPUP){
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }
    }

    //Fire base Auth user Getter
    public  FirebaseUser getCurrentUser(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        return this.user;
    }

    //Firebase Firestore user Getter
    public  FirebaseFirestore getDireStoreInstance(){
        return this.db;
    }



    public void registerAuthListener(){
        FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in

                } else {
                    // User is signed out
                    Intent i = new Intent(context, SplashActivity.class);
                    context.startActivity(i);
                }
                // ...
            }
        };
        FirebaseAuth.getInstance().addAuthStateListener(authListener);
    }



    // DATABASE HELPER FUNCTION
    public void saveSensorDataInFirestore(String Collection, String Document, Map<String, Object> data, String SubCollection){
        Log.d("DEBUG", "SAVING Document: "+Document+" Collection: "+Collection);

            db = FirebaseFirestore.getInstance();
            db.collection(Collection).document(Document).collection(SubCollection)
                    .add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d("DEBUG", "DocumentSnapshot successfully written for Document: "+Document+" Collection: "+SubCollection);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("DEBUG", "Error writing document"+e.getMessage());
                }
            });
    }

    // DATABASE HELPER FUNCTION
    public void saveInFirestore(String Collection, String Document, Map<String, Object> data, String SubCollection){
        Log.d("DEBUG", "SAVING Document: "+Document+" Collection: "+Collection);

        if(SubCollection != null){
            db = FirebaseFirestore.getInstance();
            db.collection(Collection).document(Document).collection(this.EPASS_COLLECTION)
                    .add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d("DEBUG", "DocumentSnapshot successfully written for Document: "+Document+" Collection: "+SubCollection);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("DEBUG", "Error writing document"+e.getMessage());
                }
            });

        }else{
            db = FirebaseFirestore.getInstance();
            db.collection(Collection).document(Document)
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("DEBUG", "DocumentSnapshot successfully written for Document: "+Document+" Collection: "+Collection);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("DEBUG", "Error writing document"+e.getMessage());
                        }
                    });
        }

    }


    public void getFromFirestore(String Collection, String Document){


        DocumentReference docRef = db.collection(Collection).document(Document);

        // Source can be CACHE, SERVER, or DEFAULT.
        Source source = Source.CACHE;

        // Get the document, forcing the SDK to use the offline cache
        docRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    try{
                        dataUpdateCallback.updateMyText( task);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

            }
        });
    }

    public void getSubCollectionFromFirestore(String Collection, String Document, String SubCollection){

        Source source = Source.CACHE;

        db.collection(Collection).document(Document).collection(SubCollection).get(source)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            queryDataUpdateCallback.updateQuerySnapshot( task);

                        } else {
                            Log.d("DEBUG", "Error getting documents: ", task.getException());
                        }
                    }
                });


    }

    public void registerFirestoreDataChangeListener(String Collection, String Document, String SubCollection){
        db.collection(Collection).document(Document).collection(SubCollection)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("DEBUG", "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.MODIFIED) {
                                Log.d("DEBUG", "New city: " + dc.getDocument().getData());
                            }
                        }

                    }
                });
    }

    //Constructor with Interface
    public CommonUtility(DataUpdateCallback callback) {
        this.dataUpdateCallback = callback;
        // Create Firebase instance
        db = FirebaseFirestore.getInstance();
        user  = FirebaseAuth.getInstance().getCurrentUser();
    }

    DataUpdateCallback dataUpdateCallback = null;
    public interface DataUpdateCallback {
        // Declaration of the template function for the interface
        public void updateMyText(Task<DocumentSnapshot> task);


    }

    //Constructor with Interface
    public CommonUtility(QueryDataUpdateCallback callback) {
        this.queryDataUpdateCallback = callback;
        // Create Firebase instance
        db = FirebaseFirestore.getInstance();
        user  = FirebaseAuth.getInstance().getCurrentUser();
    }

    QueryDataUpdateCallback queryDataUpdateCallback = null;
    public interface QueryDataUpdateCallback {
        // Declaration of the template function for the interface
        void updateQuerySnapshot(Task<QuerySnapshot> task);
    }

    //Helper function to find index of elemenyt in array
    public static int findIndex(String arr[], String t)
    {

        // if array is Null
        if (arr == null) {
            return -1;
        }

        // find length of array
        int len = arr.length;
        int i = 0;

        // traverse in the array
        while (i < len) {

            // if the i-th element is t
            // then return the index
            Log.d("DEBUG",arr[i]+"--"+t);
            if (arr[i].equals(t)) {
                return i;
            }
            else {
                i = i + 1;
            }
        }
        return -1;
    }


}
