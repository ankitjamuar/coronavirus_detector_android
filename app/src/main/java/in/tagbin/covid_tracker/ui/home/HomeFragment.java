package in.tagbin.covid_tracker.ui.home;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;

import in.tagbin.covid_tracker.CommonUtility;
import in.tagbin.covid_tracker.R;
import in.tagbin.covid_tracker.ui.add_pass.AddViewPageFragment;
import in.tagbin.covid_tracker.ui.faq.FaqFragment;
import in.tagbin.covid_tracker.ui.gallery.GalleryFragment;
import in.tagbin.covid_tracker.ui.slideshow.SlideshowFragment;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private GoogleMap mMap;
    private HashMap<String, Marker> mMarkers = new HashMap<>();
    private View diagnose;
    private View epass;
    private View faq;
    private View report;
    CommonUtility util;
    ListenerRegistration registration;
    TextView pass_text;
    TextView active_text;
    TextView cured_text;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        diagnose = root.findViewById(R.id.self_diagnose);
        epass= root.findViewById(R.id.view_epass);
        faq= root.findViewById(R.id.view_faq);
        report= root.findViewById(R.id.view_repots);
        pass_text= root.findViewById(R.id.pass_text);
        active_text= root.findViewById(R.id.active_text);
        cured_text= root.findViewById(R.id.cured_text);


        util = new CommonUtility(new CommonUtility.DataUpdateCallback() {
            @Override
            public void updateMyText(Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                Log.d("DEBUG", "Cached document data: " + document.get(util.USER_CONTACT_SYNC_BOOLEAN));
                Log.d("DEBUG", "Cached document data: " + document.get(util.USER_LOCATION_TRACKING_BOOLEAN));

            }
        });
        util.getFromFirestore(util.COVID_STATS,"data");
        setStatChangeListener();

        diagnose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment diagnoseFragment = new GalleryFragment();
                navigateFragment(diagnoseFragment);

            }
        });
        epass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment epassFragment = new AddViewPageFragment();
                navigateFragment(epassFragment);

            }
        });
        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment faqFragment = new FaqFragment();
                navigateFragment(faqFragment);

            }
        });
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Coming Soon",Toast.LENGTH_LONG).show();

            }
        });


//        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
        return root;
    }

    public void navigateFragment(Fragment frag){
        FragmentTransaction transaction =  getFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, frag ); // give your fragment container id in first parameter
        transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
        transaction.commit();
    }

    private void setStatChangeListener(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection(util.COVID_STATS).document("data");
        registration = docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("DEBUG", "Listen failed.", e);
                    return;
                }
                try {
                    if (snapshot != null && snapshot.exists()) {
                        if (snapshot.contains(util.COVID_STATS_INDIA_CASE_TOTAL)) {
                            pass_text.setText(snapshot.get(util.COVID_STATS_INDIA_CASE_TOTAL).toString());
                        }
                        if (snapshot.contains(util.COVID_STATS_INDIA_DEATH_TOTAL)) {
                            active_text.setText(snapshot.get(util.COVID_STATS_INDIA_DEATH_TOTAL).toString());
                        }
                        if (snapshot.contains(util.COVID_STATS_INDIA_CURED_TOTAL)) {
                            cured_text.setText(snapshot.get(util.COVID_STATS_INDIA_CURED_TOTAL).toString());
                        }
                    } else {
                        Log.d("DEBUG", "Current data: null");
                    }
                }catch(Exception ex){
                    Log.d("DEBUG", "ERROR:" +ex.getMessage());
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        registration.remove();
    }
}
