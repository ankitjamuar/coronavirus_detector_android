package in.tagbin.covid_tracker.ui.add_pass;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import in.tagbin.covid_tracker.CommonUtility;
import in.tagbin.covid_tracker.R;

import static android.app.Activity.RESULT_OK;

public class AddPassFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 22;
    private AddPassViewModel mViewModel;
    public RadioButton emergency;
    public RadioButton esp;
    public EditText from_place;
    public EditText to_place;
    public TextView timeSlot;
    public TextView reason;
    public TextView document;
    public TextView start_time;
    public TextView end_time;
    public Calendar dateStart;
    public Calendar dateEnd;
    public TextView select_date_title;
    public EditText vechileNumber;
    private ImageView submit;
    private ImageView upload;
    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;
    public String documentUrl = "";



    public static AddPassFragment newInstance() {
        return new AddPassFragment();
    }
    public Calendar currentDate = null;
    public CommonUtility util;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.add_pass_fragment, container, false);
        util = new CommonUtility(getActivity());
        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        emergency = root.findViewById(R.id.emergency);
        esp = root.findViewById(R.id.esp);
        from_place = root.findViewById(R.id.from);
        to_place = root.findViewById(R.id.to);
        select_date_title = root.findViewById(R.id.select_date_title);
        start_time = root.findViewById(R.id.start_time);
        end_time = root.findViewById(R.id.end_time);
        reason = root.findViewById(R.id.reason_text);
        //document = root.findViewById(R.id.upload_document_name);
        submit = root.findViewById(R.id.submit);
        upload = root.findViewById(R.id.upload_button);
        vechileNumber = root.findViewById(R.id.vechile_number);

        currentDate = Calendar.getInstance();
        dateStart = Calendar.getInstance();
        dateEnd = Calendar.getInstance();

        select_date_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
            }
        });

        start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        dateStart.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        dateStart.set(Calendar.MINUTE, minute);
                        String AM_PM ;
                        if(hourOfDay < 12) {
                            AM_PM = "AM";
                        } else {
                            AM_PM = "PM";
                        }
                        start_time.setText(hourOfDay + " : " + minute + " " + AM_PM );

                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false);
                dialog.setTitle("Time to come back");
                dialog.show();
            }
        });

        end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        dateEnd.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        dateEnd.set(Calendar.MINUTE, minute);
                        String AM_PM ;
                        if(hourOfDay < 12) {
                            AM_PM = "AM";
                        } else {
                            AM_PM = "PM";
                        }
                        end_time.setText(hourOfDay + " : " + minute + " " + AM_PM );
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false);
                dialog.setTitle("Time to come back");
                dialog.show();
            }
        });

        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                esp.setChecked(false);
            }
        });
        esp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emergency.setChecked(false);
            }
        });

        //Upload Button
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentValues values = new ContentValues();
                Log.d("DEBUG", "-" + reason.getText().toString() + "-" + esp.getText().toString());
                String category = "";
                if (esp.isChecked()) {
                    category = esp.getText().toString();

                } else {
                    category = emergency.getText().toString();

                }
                if (from_place.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please select From", Toast.LENGTH_LONG).show();
                    return;
                }
                if (to_place.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please select To", Toast.LENGTH_LONG).show();
                    return;
                }
                if (start_time.getText().toString().equals("Start Time")) {
                    Toast.makeText(getContext(), "Please select start time", Toast.LENGTH_LONG).show();
                    return;
                }
                if (end_time.getText().toString().equals("End Time")) {
                    Toast.makeText(getContext(), "Please select end time", Toast.LENGTH_LONG).show();
                    return;
                }
                if (select_date_title.getText().toString().equals("Select Date")) {
                    Toast.makeText(getContext(), "Please select Date", Toast.LENGTH_LONG).show();
                    return;
                }
                if (reason.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please enter reason", Toast.LENGTH_LONG).show();
                    return;
                }

                Map<String, Object> epass = new HashMap<>();
                epass.put(util.EPASS_CATEGORY, category);
                epass.put(util.EPASS_CATEGORY_TYPE, "");
                epass.put(util.EPASS_FROM, from_place.getText().toString());
                epass.put(util.EPASS_TO, to_place.getText().toString());
                epass.put(util.EPASS_START_DATE, select_date_title.getText().toString());
                epass.put(util.EPASS_START_TIME, start_time.getText().toString());
                epass.put(util.EPASS_END_TIME, start_time.getText().toString());
                epass.put(util.EPASS_REASON, reason.getText().toString());
                epass.put(util.EPASS_VECHILE_NUMBER, "");
                epass.put(util.EPASS_STATUS, "PENDING");
                epass.put(util.EPASS_DOCUMENT, documentUrl);
                epass.put(util.EPASS_VECHILE_NUMBER, vechileNumber.getText().toString());

                util.saveInFirestore(util.USER_COLLECTION, util.user.getPhoneNumber(), epass,util.EPASS_COLLECTION);
                Toast.makeText(getContext(), "E-PASS Requested.", Toast.LENGTH_LONG).show();
                clearForm();
                documentUrl = "";
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AddPassViewModel.class);
        // TODO: Use the ViewModel

    }

    public void selectDate() {

        new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateStart.set(year, monthOfYear, dayOfMonth);
                select_date_title.setText(dayOfMonth+"/"+monthOfYear+"/"+year);

            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    public void clearForm(){
//        emergency =
//        esp =
        from_place.setText("");
        to_place.setText("");
        select_date_title.setText("Select Date");
        start_time.setText("Start Time");
        end_time .setText("End Time");
        reason.setText(" ");
        //document.setText("");

    }

    private void SelectImage()
    {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {

              uploadImage();
            }

            catch (Exception e) {
                // Log the exception
               Log.d("DEBUG","Error while Uploading "+e.getMessage());
            }
        }
    }

    // UploadImage method
    private void uploadImage()
    {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "images/"
                                    + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    documentUrl = taskSnapshot.getUploadSessionUri().toString();

                                    Toast
                                            .makeText(getContext(),
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(getContext(),
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                    Log.d("DEBUG",progress+"");
                                }
                            });
        }
    }


}
