package in.tagbin.covid_tracker.ui.gallery;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import in.tagbin.covid_tracker.R;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    TextView question;
    public int question_number;
    ImageView nextQuestion;
    RadioGroup answers;
    public boolean case1 = false;
    public int case2 = 0;
    public int case3 = 0;
    CheckBox diabetese;
    CheckBox highBloodPressure;
    CheckBox heartDisease;
    CheckBox kidneyDisease;
    CheckBox lungDisease;
    CheckBox stroke;
    CheckBox reducedImmunity;
    CheckBox noneofTheAbove;
    RadioButton firstButton;
    RadioButton secondButton;
    RadioButton thirdButton;
    Animation animation;
    ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        question = root.findViewById(R.id.diagnose_question);
        nextQuestion = root.findViewById(R.id.next_question_button);
        answers = root.findViewById(R.id.answers_button);
        diabetese= root.findViewById(R.id.diabetese);
        highBloodPressure= root.findViewById(R.id.highBloodPressure);
        heartDisease= root.findViewById(R.id.heartDisease);
        kidneyDisease= root.findViewById(R.id.kidneyDisease);
        lungDisease= root.findViewById(R.id.lungDisease);
        stroke= root.findViewById(R.id.stroke);
        reducedImmunity= root.findViewById(R.id.reducedImmunity);
        noneofTheAbove= root.findViewById(R.id.noneofTheAbove);

        firstButton= root.findViewById(R.id.radio0);
        secondButton= root.findViewById(R.id.radio1);
        thirdButton= root.findViewById(R.id.radio2);

        progressBar  = root.findViewById(R.id.progressBar);



        animation = AnimationUtils.loadAnimation(getContext(),
                R.anim.fade_in);
        question_number = 0;

        final ArrayList<String> questionList = new ArrayList<>();
        questionList.add("For whom you are testing?");
        questionList.add("What is your age?");
        questionList.add("What is your Gender");
        questionList.add("Tell a little about travel history of your or your family members?");
        questionList.add("What is your body temperature?");
        questionList.add("Have you been coughing lately?");
        questionList.add("Do you have any difficulty breathing?");
        questionList.add("Do you have history of any of these?");



        nextQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                question_number++;

                // get selected radio button from radioGroup
                int selectedId =  answers.getCheckedRadioButtonId();

                // find the radio button by returned id
                RadioButton radioButton = (RadioButton) getActivity().findViewById(selectedId);

                Log.d("DEBUG",radioButton.getText().toString()+"");


                String[] answerArray3 = new String[] {};

                printStatus();



                if(question_number == 1){
                    firstButton.setText("<50");
                    secondButton.setText("50-65");
                    thirdButton.setText(">65");
                    progressBar.setProgress(12,true);
                }
                if(question_number == 2){

                    firstButton.setText("Male");
                    secondButton.setText("Female");
                    thirdButton.setText("Other");
                    progressBar.setProgress(24,true);
                }
                if(question_number == 3){



                    firstButton.setText("No Travel History");
                    secondButton.setText("No contact with anyone with Symptoms");
                    thirdButton.setText("History of travel or meeting in affected geographical area in last 14 days\"");
                    progressBar.setProgress(36,true);
                }
                if(question_number == 4){

                    if(thirdButton.isChecked()){
                        case1 = true;
                        Log.d("DEBUG","CASE is True;");
                    }

                    firstButton.setText("96-98.6 F");
                    secondButton.setText("98.6-102 F");
                    thirdButton.setText("> 102 F");
                    progressBar.setProgress(48,true);
                }
                if(question_number == 5){

                    if(secondButton.isChecked() || thirdButton.isChecked()){
                        case2 = case2 + 1;
                    }

                    firstButton.setText("YES");
                    secondButton.setText("NO");
                    thirdButton.setText("DON'T KNOW");
                    progressBar.setProgress(60,true);
                }
                if(question_number == 6){

                    if(firstButton.isChecked() ){
                        case2 = case2 + 1;
                    }

                    firstButton.setText("YES");
                    secondButton.setText("NO");
                    thirdButton.setText("DON'T KNOW");
                    progressBar.setProgress(72,true);

                }
                if(question_number == 7){

                    if(firstButton.isChecked() ){
                        case2 = case2 + 1;
                    }

                    //hide Radio Buttons
                    answers.setVisibility(View.GONE);

                    //Set visibility for checkbox
                    diabetese.setVisibility(View.VISIBLE);
                    highBloodPressure.setVisibility(View.VISIBLE);
                    lungDisease.setVisibility(View.VISIBLE);
                    kidneyDisease.setVisibility(View.VISIBLE);
                    heartDisease.setVisibility(View.VISIBLE);
                    stroke.setVisibility(View.VISIBLE);
                    reducedImmunity.setVisibility(View.VISIBLE);
                    noneofTheAbove.setVisibility(View.VISIBLE);
                    progressBar.setProgress(80,true);


                }

                if(question_number == 8) {
                    progressBar.setProgress(100,true);
                    changeText(question,"Thank you for your answers!",animation,10);

                    //Hide Submit Button
                    nextQuestion.setVisibility(View.INVISIBLE);

                    diabetese.setVisibility(View.INVISIBLE);
                    highBloodPressure.setVisibility(View.INVISIBLE);
                    lungDisease.setVisibility(View.INVISIBLE);
                    kidneyDisease.setVisibility(View.INVISIBLE);
                    heartDisease.setVisibility(View.INVISIBLE);
                    stroke.setVisibility(View.INVISIBLE);
                    reducedImmunity.setVisibility(View.INVISIBLE);
                    noneofTheAbove.setVisibility(View.INVISIBLE);


                    if (diabetese.isChecked()) {
                        case3 = case3 + 1;
                    }
                    if (highBloodPressure.isChecked()) {
                        case3 = case3 + 1;
                    }
                    if (kidneyDisease.isChecked()) {
                        case3 = case3 + 1;
                    }
                    if (lungDisease.isChecked()) {
                        case3 = case3 + 1;
                    }
                    if (heartDisease.isChecked()) {
                        case3 = case3 + 1;
                    }
                    if (stroke.isChecked()) {
                        case3 = case3 + 1;
                    }
                    if (reducedImmunity.isChecked()) {
                        case3 = case3 + 1;
                    }

                    if(case1 && case2 ==0){
                        changeText(question,"You have high risk but no symptoms, you should quarantine yourself for 14 days",animation,10);
                        changeBackgroundColor(root,"#fff17a0a");
                    }
                    else if(case1 && case2 >=1){
                        changeText(question,"You have high risk with symptoms, you should contact helpline right away.\n\n Helpline: 104",animation,10);
                        changeBackgroundColor(root,"#ffff4444");
                    }
                    else if(!case1 && case2 >=2 && case3 >= 1){
                        changeText(question,"You have high risk with symptoms, you should contact helpline right away.\n\n Helpline: 104",animation,10);
                        changeBackgroundColor(root,"#ffff4444");
                    }
                    else if(!case1 && case2 ==1 && case3 >= 1){
                        changeText(question,"You have medium risk, you should contact Physician.",animation,10);
                        changeBackgroundColor(root,"#fff17a0a");
                    }
                    else if(!case1 && case2 >=2 && case3 >= 0){
                        changeText(question,"You have medium risk, you should contact Physician.",animation,10);
                        changeBackgroundColor(root,"#fff17a0a");
                    }else{
                        changeText(question,"You have low risk, you should follow precautions.\n\nPrecautions :\n" +
                                "1. HANDS Wash them often\n" +
                                "2. ELBOW Cough into it\n" +
                                "3. FACE Don't touch it\n" +
                                "4. SPACE Keep safe distance\n" +
                                "5. HOME Stay ",animation,2000);
                        changeBackgroundColor(root,"#99cc00");
                    }

                    Log.d("DEBUG","Case1 Was"+ case1);
                    Log.d("DEBUG","Case2 Was"+ case2);
                    Log.d("DEBUG","Case3 Was"+ case3);

                }

                if(question_number <= 7){
                    changeText(question,questionList.get(question_number),animation,10);
                }

            }
        });

        resetForNextDiagnosis();


        return root;
    }

    public void changeText(final TextView text, final String content, final Animation anim, final int delay){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                text.setText(content);
                text.startAnimation(anim);
                if(delay==6000){
                    //TODO little hack should be removed in production
                    answers.setVisibility(View.VISIBLE);
                    nextQuestion.setVisibility(View.VISIBLE);
                }
            }
        }, delay);

    }

    public void resetForNextDiagnosis(){
        answers.setVisibility(View.INVISIBLE);

        diabetese.setVisibility(View.INVISIBLE);
        highBloodPressure.setVisibility(View.INVISIBLE);
        lungDisease.setVisibility(View.INVISIBLE);
        kidneyDisease.setVisibility(View.INVISIBLE);
        heartDisease.setVisibility(View.INVISIBLE);
        stroke.setVisibility(View.INVISIBLE);
        reducedImmunity.setVisibility(View.INVISIBLE);
        noneofTheAbove.setVisibility(View.INVISIBLE);

        nextQuestion.setVisibility(View.INVISIBLE);


        changeText(question,"Hi There!.",animation,10);
        changeText(question,"Answer few questions to help us diagnose you.",animation,2000);
        changeText(question,"For whom you are testing?",animation,6000);

    }

    public int getIndexofAnswer(String str, String[] TYPES){

        int index = -1;
        for (int i=0;i<TYPES.length;i++) {
            Log.d("DEBUG",str+"-"+i+"-"+TYPES[i]);
            if (TYPES[i].equals(str)) {
                index = i;
                Log.d("DEBUG",i+"MATCHED-"+TYPES[i]);
                break;
            }
        }
        return index;
    }

    public void changeBackgroundColor(View view, String color){
        ConstraintLayout layout = view.findViewById(R.id.bg_layout);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimator.setDuration(325);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                float fractionAnim = (float) valueAnimator.getAnimatedValue();

                layout.setBackgroundColor(ColorUtils.blendARGB(Color.parseColor("#FFFFFF")
                        , Color.parseColor(color)
                        , fractionAnim));
            }
        });
        valueAnimator.start();
    }

    public void printStatus(){
        if(firstButton.isChecked()){
            Log.d("DEBUG","firstButton Checked");
        }else{
            Log.d("DEBUG","firstButton NOT Checked");
        }

        if(secondButton.isChecked()){
            Log.d("DEBUG","secondButton Button Checked");
        }else{
            Log.d("DEBUG","secondButton NOT Checked");
        }

        if(thirdButton.isChecked()){
            Log.d("DEBUG","thirdButton Checked");
        }else{
            Log.d("DEBUG","thirdButtonn NOT Checked");
        }
    }
}
