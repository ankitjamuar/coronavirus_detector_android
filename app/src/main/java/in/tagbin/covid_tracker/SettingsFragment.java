package in.tagbin.covid_tracker;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Switch locationTrackingSwitch;
    Switch dataSyncSwitch;
    CommonUtility util;
    boolean isUIUpdated = false;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_settings, container, false);


        util = new CommonUtility(new CommonUtility.DataUpdateCallback() {
            @Override
            public void updateMyText(Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                Log.d("DEBUG", "Cached document data: " + document.get(util.USER_CONTACT_SYNC_BOOLEAN));
                Log.d("DEBUG", "Cached document data: " + document.get(util.USER_LOCATION_TRACKING_BOOLEAN));

                try{
                    if("TRUE".equals(document.get(util.USER_CONTACT_SYNC_BOOLEAN).toString())){
                        dataSyncSwitch.setChecked(true);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                try{
                    if("TRUE".equals(document.get(util.USER_LOCATION_TRACKING_BOOLEAN).toString())){
                        locationTrackingSwitch.setChecked(true);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        });

        //get data and populate UI
        //TODO this fire onChange event and again push it to save data to firestore
        util.getFromFirestore(util.USER_COLLECTION,util.user.getPhoneNumber());

        locationTrackingSwitch = root.findViewById(R.id.location_tracking_switch);
        dataSyncSwitch = root.findViewById(R.id.contact_sync_switch);

        locationTrackingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("DEBUG", "Cached document data: "+isChecked);
                Map<String, Object> profile = new HashMap<>();
                if(isChecked){
                    profile.put(util.USER_LOCATION_TRACKING_BOOLEAN,"TRUE" );
                    util.saveInFirestore(util.USER_COLLECTION,util.user.getPhoneNumber(),profile,null);
                }else{
                    profile.put(util.USER_LOCATION_TRACKING_BOOLEAN,"FALSE" );
                    util.saveInFirestore(util.USER_COLLECTION,util.user.getPhoneNumber(),profile,null);

                }
            }
        });

        dataSyncSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Map<String, Object> profile = new HashMap<>();

                if(isChecked){
                    profile.put(util.USER_CONTACT_SYNC_BOOLEAN,"TRUE" );
                    util.saveInFirestore(util.USER_COLLECTION,util.user.getPhoneNumber(),profile, null);
                }else{
                    profile.put(util.USER_CONTACT_SYNC_BOOLEAN,"FALSE" );
                    util.saveInFirestore(util.USER_COLLECTION,util.user.getPhoneNumber(),profile,null);

                }
            }
        });

        return root;
    }

}
