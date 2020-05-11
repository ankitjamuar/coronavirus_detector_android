package in.tagbin.covid_tracker.ui.profile;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.tagbin.covid_tracker.CommonUtility;
import in.tagbin.covid_tracker.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link profileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class profileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ImageView submit;
    EditText fullname;
    EditText address;
    EditText mobile;
    RadioGroup gender;
    Spinner district;
    Spinner tehsil;
    String selectedGender;
    String selected_district;
    String selected_tehsil;
    RadioButton male;
    RadioButton female;

    String[] district_names;
    HashMap<String, String[]> district_tehsil;


    CommonUtility util;

    public profileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment profileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static profileFragment newInstance(String param1, String param2) {
        profileFragment fragment = new profileFragment();
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
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        fullname = root.findViewById(R.id.full_name);
        address = root.findViewById(R.id.address_input);
        mobile = root.findViewById(R.id.mobile);
        gender = root.findViewById(R.id.gender_radio_group);
        district = root.findViewById(R.id.district_spinner);
        tehsil = root.findViewById(R.id.tehsil_spinner);
        submit = root.findViewById(R.id.save_profile);
        male = root.findViewById(R.id.radioMale);
        female = root.findViewById(R.id.radioFemale);

        //Update UI
        util = new CommonUtility(new CommonUtility.DataUpdateCallback() {
            @Override
            public void updateMyText(Task<DocumentSnapshot> task) {
                try{
                    DocumentSnapshot document = task.getResult();
                    if(document.contains(util.USER_FULL_NAME)){
                        fullname.setText(document.get(util.USER_FULL_NAME).toString());
                    }
                    if(document.contains(util.USER_MOBILE)){
                        mobile.setText(document.get(util.USER_MOBILE).toString());
                    }

                    if(document.contains(util.USER_ADDRESS)) {
                        address.setText(document.get(util.USER_ADDRESS).toString());
                    }

                    if(document.contains(util.USER_GENDER)) {

                        if("Male".equals(document.get(util.USER_GENDER).toString())){
                            male.setChecked(true);
                        }
                        if ("Female".equals(document.get(util.USER_GENDER).toString())) {
                            female.setChecked(true);
                        }
                    }

                    if(document.contains(util.USER_DISTRICT)) {
                        //Selet Selected District
                        district.setSelection(
                                util.findIndex(
                                        district_names,
                                        document.get(util.USER_DISTRICT).toString()
                                )
                        );
                    }

                    if(document.contains(util.USER_TEHSIL)) {

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Set selected Tehsil
                                int tehsil_id  = util.findIndex(
                                        district_tehsil.get(document.get(util.USER_DISTRICT)),
                                        document.get(util.USER_TEHSIL).toString()
                                );
                                tehsil.setSelection( tehsil_id );
                            }
                        }, 1000);


                    }


                    Log.d("DEBUG",document.get(util.USER_GENDER).toString());


                }catch(Exception e){
                    Log.d("DEBUG","ERROR"+e.getMessage());
                }

            }
        });

        //GEt Profile Data on Load
        util.getFromFirestore(util.USER_COLLECTION,util.user.getPhoneNumber());

        //SET mobile form login info
        mobile.setText(util.getCurrentUser().getPhoneNumber());


        district_tehsil = new HashMap<>();
        String[] Shimla = { "Shimla Urban","Shimla Rural", "Sooni", "Theog", "Kotkhai", "Rampur", "Kumarsain", "Chopal", "Rohru", "Jubbal", "Chidgaon", "Dodra Kewar"};
        String[] Kangra = {"Nurpur", "Indora", "Jwali", "Kangra", "Palampur", "Badoh", "Kasba Kotla", "Jaswan", "Dehra Gopipur", "Khundiyan", "Jaisinghpur", "Baijnath", "Fatehpur","Dharamshala", "Shahpur"};
        String[] Sirampur = {"Nahan","Shilai","Paonta Sahib","Pacchad","Rajgarh"};
        String[] Solan = {"Solan","Kasauli","Nalagarh","Arki","Kandaghat"};
        String[] Mandi = {"Mandi ","Chachyot ","Thunag ","Karsog ","Jogindernagar ","Paddhar ","Ladbhadol ","Sundernagar ","Sarkaghat"};
        String[] Chamba = {"Chamba","Churah","Pangi","Bharmaur","Bhatiath","Salooni","Dhalousie"};
        String[] Bilaspur = {"Bilaspur Sadar","Ghumarwin","Jhandutta"};
        String[] Kullu = {"Kullu ","Nirmand ","Banjar ","Manali ","Anni"};
        String[] Hamirpur = {"Hamirpur","Badsar","Bhoranj","Nadaun","Sujanpur"};
        String[] Kinnaur = {"Nichar","Kalpa","Pooh","Sangla","Moorang"};
        String[] Lahaul_Spiti = {"Lahaul","Spiti"};
        String[] Una = {"Una","Amb"};


        district_tehsil.put("Shimla",Shimla);
        district_tehsil.put("Kangra",Kangra);
        district_tehsil.put("Sirampur",Sirampur);
        district_tehsil.put("Solan",Solan);
        district_tehsil.put("Mandi",Mandi);
        district_tehsil.put("Chamba",Chamba);
        district_tehsil.put("Bilaspur",Bilaspur);
        district_tehsil.put("Kullu",Kullu);
        district_tehsil.put("Hamirpur",Hamirpur);
        district_tehsil.put("Kinnaur",Kinnaur);
        district_tehsil.put("Lahaul Spiti",Lahaul_Spiti);
        district_tehsil.put("Una",Una);


        district_names = new String[]{"Shimla", "Kangra", "Sirampur", "Solan", "Mandi", "Chamba", "Bilaspur", "Kullu", "Hamirpur", "Kinnaur", "Lahaul_Spiti", "Una"};

        ArrayAdapter district_names_AA = new ArrayAdapter(getContext(),R.layout.spinner_list,district_names);
        district_names_AA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        district.setAdapter(district_names_AA);

        district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_district = district_names[position];

                //Set Value of Tehsil
                ArrayAdapter tehsil_names_AA = new ArrayAdapter(getContext(),R.layout.spinner_list,district_tehsil.get(selected_district));
                tehsil_names_AA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                tehsil.setAdapter(tehsil_names_AA);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //For Teshil
        tehsil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try{
                    selected_tehsil = district_tehsil.get(selected_district)[position];
                }catch(Exception e){
                    Log.d("DEBUG",e.getMessage());
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        // Set Array Adapter of District
//        ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,district_names);
//        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        district.setAdapter(aa);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get the checked Radio Button ID from Radio Grou[
                int selectedRadioButtonID = gender.getCheckedRadioButtonId();
                Log.d("DEBUG","SELECTED RADIO"+selectedRadioButtonID);

                // If nothing is selected from Radio Group, then it return -1
                if (selectedRadioButtonID != -1) {

                    RadioButton selectedRadioButton = (RadioButton) root.findViewById(selectedRadioButtonID);
                    selectedGender = selectedRadioButton.getText().toString();

                }else{
                    selectedGender = "Male";
                }


                //TODO get only changed value
                Map<String, Object> profile = new HashMap<>();
                profile.put(util.USER_FULL_NAME,fullname.getText().toString());
                profile.put(util.USER_ADDRESS,address.getText().toString());
                profile.put(util.USER_GENDER,selectedGender);
                profile.put(util.USER_DISTRICT,selected_district);
                profile.put(util.USER_TEHSIL,selected_tehsil);
                profile.put(util.USER_MOBILE,mobile.getText().toString());

                util.saveInFirestore(util.USER_COLLECTION,util.user.getPhoneNumber(),profile,null);

                //TODO fetch is data is saged thrught interface
                Toast.makeText(getContext(),"Data Saaved",Toast.LENGTH_LONG).show();

            }
        });



        //submit.set
        return root;
    }

    private StringBuilder log = new StringBuilder();







}
