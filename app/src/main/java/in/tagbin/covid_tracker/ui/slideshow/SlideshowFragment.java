package in.tagbin.covid_tracker.ui.slideshow;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ramotion.expandingcollection.ECBackgroundSwitcherView;
import com.ramotion.expandingcollection.ECCardData;
import com.ramotion.expandingcollection.ECPagerView;
import com.ramotion.expandingcollection.ECPagerViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

import in.tagbin.covid_tracker.APIUtility;
import in.tagbin.covid_tracker.CommonUtility;
import in.tagbin.covid_tracker.DataReaderContract;
import in.tagbin.covid_tracker.R;
import in.tagbin.covid_tracker.ui.faq.FaqFragment;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;
    private ECPagerView ecPagerView;
    public PassReaderDbHelper dbHelper;
    CommonUtility util;

    public ECPagerViewAdapter ecPagerViewAdapter;

    private static List<String> createItemsList(String cardName) {
        ArrayList<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(
                cardName
        ));
        return list;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        List<ECCardData> dataset = new ArrayList<>();

        util = new CommonUtility(new CommonUtility.QueryDataUpdateCallback() {
            @Override
            public void updateQuerySnapshot(Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d("DEBUG", document.getId() + " => " + document.getData());

                    String date = document.contains(util.EPASS_START_DATE)?document.get(util.EPASS_START_DATE).toString():"";
                    String time = document.contains(util.EPASS_START_TIME)?document.get(util.EPASS_START_TIME).toString():"";
                    String status = document.contains(util.EPASS_STATUS)?document.get(util.EPASS_STATUS).toString():"PENDING";
                    String category = document.contains(util.EPASS_CATEGORY)?document.get(util.EPASS_CATEGORY).toString():"EMERGENCY";

                    dataset.add(new CardDataImpl(
                            document.contains(util.EPASS_REASON)?document.get(util.EPASS_REASON).toString():"",
                            date+" "+time,
                            status,
                            category,
                            category,
                            category=="EMERGENCY"?R.drawable.emergency_pass:R.drawable.normal_pass,
                            category=="EMERGENCY"?R.drawable.emergency_pass:R.drawable.normal_pass,
                            createItemsList(status)
                    ));

                    // Implement pager adapter and attach it to pager view
                    ecPagerViewAdapter = new ECPagerViewAdapter(getContext(), dataset) {
                        @Override
                        public void instantiateCard(LayoutInflater inflaterService, ViewGroup head, final ListView list, ECCardData data) {
                            // Data object for current card
                            CardDataImpl cardData = (CardDataImpl) data;

                            // Set adapter and items to current card content list
                            final List<String> listItems = cardData.getListItems();
                            final CardListItemAdapter listItemAdapter = new CardListItemAdapter(getContext(), listItems);
                            list.setAdapter(listItemAdapter);

                            // Also some visual tuning can be done here
                            list.setBackgroundColor(Color.WHITE);

                            // Here we can create elements for head view or inflate layout from xml using inflater service
                            TextView cardTitle = new TextView(getContext());
                            cardTitle.setText(cardData.getCardTitle());
                            cardTitle.setTextSize(COMPLEX_UNIT_DIP, 20);
                            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.WRAP_CONTENT,
                                    FrameLayout.LayoutParams.MATCH_PARENT
                            );
                            layoutParams.gravity = Gravity.CENTER;

                            //head.addView(cardTitle, layoutParams);

                            // Inflate main header layout and attach it to header root view
                            inflaterService.inflate(R.layout.simple_head, head);

                            // Set header data from data object
                            TextView title = (TextView) head.findViewById(R.id.title);
                            title.setText(cardData.getCardTitle());

                            ImageView avatar = (ImageView) head.findViewById(R.id.avatar);
                            Log.d("DEBUG", "Card Status WAS"+cardData.getCardStatus());
                            if( "APPROVED".equals(cardData.getCardStatus()) ){
                                avatar.setImageResource(R.drawable.approved);
                            }else if("REJECTED".equals(cardData.getCardStatus())){
                                avatar.setImageResource(R.drawable.rejected);
                            }else if("PENDING".equals(cardData.getCardStatus()) ){
                                avatar.setImageResource(R.drawable.pending);
                            }

//
                            TextView name = (TextView) head.findViewById(R.id.name);
                            name.setText("Valid for: "+ cardData.getCardTimeSlot());

                            TextView message = (TextView) head.findViewById(R.id.message);
                            message.setText("Reason:" +cardData.getCardReason());

        //                TextView viewsCount = (TextView) head.findViewById(R.id.socialViewsCount);
        //                viewsCount.setText(" " + cardData.getPersonViewsCount());
        //                TextView likesCount = (TextView) head.findViewById(R.id.socialLikesCount);
        //                likesCount.setText(" " + cardData.getPersonLikesCount());
        //                TextView commentsCount = (TextView) head.findViewById(R.id.socialCommentsCount);
        //                commentsCount.setText(" " + cardData.getPersonCommentsCount());

                            // Card toggling by click on head element
                            head.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(final View v) {
                                    ecPagerView.toggle();
                                }
                            });
                        }
                    };
                    ecPagerView.setPagerViewAdapter(ecPagerViewAdapter);

                    // Add background switcher to pager view
                    ecPagerView.setBackgroundSwitcherView((ECBackgroundSwitcherView) root.findViewById(R.id.ec_bg_switcher_element));


                }
            }
        });

        //Get Passes from Server
        util.getSubCollectionFromFirestore(util.USER_COLLECTION, util.user.getPhoneNumber(),util.EPASS_COLLECTION);

        //Register to receive online change
        util.registerFirestoreDataChangeListener(util.USER_COLLECTION, util.user.getPhoneNumber(),util.EPASS_COLLECTION);

        // Get pager from layout
        ecPagerView = (ECPagerView) root.findViewById(R.id.ec_pager_element);






//        if(category == "EMERGENCY"){
//            dataset.add(new CardDataImpl(reason,date+" "+ time,status,category,category, R.drawable.emergency_pass, R.drawable.emergency_pass, createItemsList(status)));
//        }else{
//            dataset.add(new CardDataImpl(reason,date+" "+ time,status,category,category, R.drawable.normal_pass, R.drawable.normal_pass, createItemsList(status)));
//        }



        // Generate example dataset
        //List<ECCardData> dataset = CardDataImpl.generateExampleData();



        // Directly modifying dataset
        //dataset.remove(2);
        //ecPagerViewAdapter.notifyDataSetChanged();
        return root;
    }

    public String getToken(){
        SharedPreferences sharedpreferences;
        sharedpreferences = getActivity().getSharedPreferences(APIUtility.otherStorage, Context.MODE_PRIVATE);
        String token = sharedpreferences.getString("token", null);
        if( null ==  token ){
            //TODO Log the user out
            Toast.makeText(getContext(),"Token Unavailable",Toast.LENGTH_LONG).show();
            return "NO";
        }else{
            return token;
        }

    }
}
