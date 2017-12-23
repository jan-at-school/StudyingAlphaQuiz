package com.cykiq.studyingalphaquiz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adroitandroid.chipcloud.ChipCloud;
import com.adroitandroid.chipcloud.ChipListener;
import com.cykiq.studyingalphaquiz.Cloud.GetQuestion;
import com.cykiq.studyingalphaquiz.Cloud.GetTopics;
import com.cykiq.studyingalphaquiz.Data.Question;
import com.cykiq.studyingalphaquiz.Data.Topic;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnCancelListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.squareup.picasso.Picasso;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.mrapp.android.bottomsheet.BottomSheet;

public class QuizActivity extends AppCompatActivity {


    private static final String TAG = "QuizActivity";
    public static List<Question> quizQuestionList=new ArrayList<Question>();
    ViewPager viewPager;


    TextView tvQAnswer;

    TextView tvHDifficulty;
    TextView tvHQuestionsSolved;
    TextView tvHTopic;
    TextView tvHEnd;
    ImageView image;
    LinearLayout options[]  = new LinearLayout[3];

    TextView tvQBody;

    List<Question> questionList;
    boolean topicsSelected[];

    public Question currentQuizQuestion;
    int questionsSolved=0;
    Lorem lorem = LoremIpsum.getInstance();

    Context mContext;
    private boolean mShowingAnswer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        mContext = this;




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);






        tvHDifficulty = (TextView) findViewById(R.id.difficulty);
        showDifficultyLevel(MySharedPreferences.getDifficulyLevel(getApplicationContext()));
        tvHQuestionsSolved = (TextView)findViewById(R.id.questions_solved);
        tvHTopic = (TextView) findViewById(R.id.topic);
        tvHEnd = (TextView) findViewById(R.id.end_test);
        tvQBody = (TextView) findViewById(R.id.questionTextView);
        tvQAnswer = (TextView) findViewById(R.id.answer_textview);
        image = (ImageView)findViewById(R.id.image_attached);

        options[0] = (LinearLayout) findViewById(R.id.option_A);
        options[1] = (LinearLayout) findViewById(R.id.option_B);
        options[2] = (LinearLayout) findViewById(R.id.option_C);


        tvHTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    selectTopics();
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        });


        tvHEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("End test?")
                        .setCancelText("No")
                        .setConfirmText("Yes")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                finish();
                            }
                        })
                        .show();
            }
        });

        tvHDifficulty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheet.Builder builder = new BottomSheet.Builder(mContext);
                builder.setTitle("Difficulty Level");
                builder.addItem(0,"Easy");
                builder.addItem(1,"Medium");
                builder.addItem(2,"Hard");


                BottomSheet bottomSheet = builder.create();
                bottomSheet.show();
                bottomSheet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        showDifficultyLevel((int)id);
                    }
                });
            }
        });



        options[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionChosen(0);
            }
        });
        options[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionChosen(1);
            }
        });
        options[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionChosen(2);
            }
        });




        try{
            selectTopicAll();
            getNextQuestion();
        }
        catch (Exception e){
            e.printStackTrace();
        }



    }




//    void toggleAnswer(){
//
//
//
//        if(((LinearLayout)findViewById(R.id.answer_layout)).getVisibility()==View.VISIBLE){
//            ViewGroup.LayoutParams paramsVisible = ((CardView)findViewById(R.id.card_view_answer)).getLayoutParams();
//            ViewGroup.LayoutParams paramsGone = ((CardView)findViewById(R.id.card_view_question)).getLayoutParams();
//            ((CardView)findViewById(R.id.card_view_answer)).setLayoutParams(paramsGone);
//            ((CardView)findViewById(R.id.card_view_question)).setLayoutParams(paramsVisible);
//            ((LinearLayout)findViewById(R.id.answer_layout)).setVisibility(View.GONE);
//            ((LinearLayout)findViewById(R.id.question_layout)).setVisibility(View.VISIBLE);
//
//        }
//        else{
//            ViewGroup.LayoutParams paramsVisible = ((CardView)findViewById(R.id.card_view_question)).getLayoutParams();
//            ViewGroup.LayoutParams paramsGone = ((CardView)findViewById(R.id.card_view_answer)).getLayoutParams();
//            ((CardView)findViewById(R.id.card_view_question)).setLayoutParams(paramsGone);
//            ((CardView)findViewById(R.id.card_view_answer)).setLayoutParams(paramsVisible);
//            ((LinearLayout)findViewById(R.id.answer_layout)).setVisibility(View.VISIBLE);
//            ((LinearLayout)findViewById(R.id.question_layout)).setVisibility(View.GONE);
//        }
//
//
//
//    }




    void showQuestion(Question quizQuestion){
        if(quizQuestion ==null)return;

        tvQBody.setText(quizQuestion.body);
        tvQAnswer.setText(quizQuestion.answer_discription);
        tvQAnswer.setVisibility(View.GONE);


        for (int i =0; i< options.length;i++){
            options[i].removeAllViews();
            TextView tv = new TextView(this);
            tv.setId(i);
            int padding = (int)dpToPixels(5,getApplicationContext());
            tv.setPadding(padding,padding,padding,padding);
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setText(quizQuestion.options[i]);
            tv.setTextColor(Color.BLACK);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1.0f
            );
            tv.setLayoutParams(param);
            options[i].addView(tv);
            options[i].setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
        }
        tvHQuestionsSolved.setText("\uD83C\uDFC1 "+questionsSolved+"");
        //tvHTopic.setText(quizQuestion.topic);
        try{
            if(quizQuestion.img_url==null){
                image.setVisibility(View.GONE);
            }
            else{
                image.setVisibility(View.VISIBLE);
                quizQuestion.img_url.replace("\\","");
                Picasso.with(getApplicationContext()).load("http://www."+quizQuestion.img_url).into(image);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }


        ((ScrollView)findViewById(R.id.scroll_view)).fullScroll(ScrollView.FOCUS_UP);




















    }



    void showDifficultyLevel(int level){
        if(level==0){
            tvHDifficulty.setText("Easy");
            tvHDifficulty.setTextColor(getApplicationContext().getResources().getColor(R.color.light_green));

        }
        else if(level==1){
            tvHDifficulty.setText("Medium");
            tvHDifficulty.setTextColor(getApplicationContext().getResources().getColor(R.color.light_lime));

        }
        else if(level==2){
            tvHDifficulty.setText("Hard");
            tvHDifficulty.setTextColor(getApplicationContext().getResources().getColor(R.color.light_red));

        }
        MySharedPreferences.setDifficultyLevel(getApplicationContext(),level);

    }


    public void getNextQuestion(){

        String tags = "0";
        for(int i=0; i<topicsSelected.length;i++){
            if(topicsSelected[i])
                tags+=","+i;
        }


        new GetQuestion(MySharedPreferences.getDifficulyLevel(getApplicationContext()), tags, 1, new GetQuestion.OnPostExecuteListener() {
            @Override
            public void onTaskDOne(List<Question> questionList) {
                try{
                    for(int i=0; i<questionList.size();i++){
                        Log.d(TAG,"question loaded");
                        currentQuizQuestion = questionList.get(i);

                    }
                    showQuestion(currentQuizQuestion);
                }
                catch (Exception e){
                    e.printStackTrace();
                }


            }
        }).execute("");
    }

    public Question nextQuestionDummy(){
        return null;
        //return new Question(quizQuestionList.size(),lorem.getParagraphs(1,3),new String[]{lorem.getWords(1,30),lorem.getWords(1,30),lorem.getWords(1,30)},((int)(Math.random()*457))%3,lorem.getParagraphs(1,4),"CFA-L1",MySharedPreferences.getDifficulyLevel(getApplicationContext()),new int[]{(int)(Math.random()*50000),(int)(Math.random()*50000),(int)(Math.random()*50000)});
    }


    void onOptionChosen(int choice){

        try{
            if(currentQuizQuestion.choice<0){//means no option is choosem
                questionsSolved++;
                currentQuizQuestion.choice = choice;

                View overlayButtons = (View) getLayoutInflater().inflate(R.layout.option_choosen_layout, null);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                overlayButtons.setLayoutParams(layoutParams);
                options[choice].addView(overlayButtons);


                YoYo.with(Techniques.Landing)
                        .duration(200)
                        .repeat(0)
                        .playOn(overlayButtons);


                if(currentQuizQuestion.correct == choice){
                    options[choice].setBackgroundColor(getApplicationContext().getResources().getColor(R.color.answerChoiceCorrect));
                    YoYo.with(Techniques.Landing)
                            .duration(300)
                            .repeat(0)
                            .playOn(options[choice]);
                }
                else {
                    options[choice].setBackgroundColor(getApplicationContext().getResources().getColor(R.color.answerChoiceWrong));
                    YoYo.with(Techniques.Shake)
                            .duration(100)
                            .repeat(0)
                            .playOn(options[choice]);
                }

                ((Button)overlayButtons.findViewById(R.id.next)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        getNextQuestion();
                        v.setOnClickListener(null);
                        ((Button)v).setText("...");
                        YoYo.with(Techniques.Landing)
                                .duration(1000)
                                .repeat(99)
                                .playOn(v);

                    }
                });
                ((Button)overlayButtons.findViewById(R.id.show_answer)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvQAnswer.setVisibility(View.VISIBLE);
//                        ((ScrollView)findViewById(R.id.scroll_view)).post(new Runnable() {
//                            @Override
//                            public void run() {
//                                ((ScrollView)findViewById(R.id.scroll_view)).scrollTo(0, ((ScrollView)findViewById(R.id.scroll_view)).getBottom());
//                            }
//                        });
                        ((ScrollView)findViewById(R.id.scroll_view)).fullScroll(ScrollView.FOCUS_DOWN);
                        //((ScrollView)findViewById(R.id.scroll_view)).scrollTo(0, ((ScrollView)findViewById(R.id.scroll_view)).getBottom());
                    }
                });



//                for(int i=0; i<options.length;i++){
//                    if(i==choice){
//                        if(currentQuizQuestion.correctAnswer == choice){
//
//                            options[i].setBackgroundColor(getApplicationContext().getResources().getColor(R.color.answerChoiceCorrect));
//                        }
//                        else {
//
//                            options[i].setBackgroundColor(getApplicationContext().getResources().getColor(R.color.answerChoiceWrong));
//
//                        }
//                    }
//                    else {
//                        options[i].setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
//                    }
//
//                }


            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void selectTopics() {


        final View view = getLayoutInflater().inflate(R.layout.dialog_select_topics, null);

        try{
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int height = metrics.heightPixels;
            int width = metrics.widthPixels;
            //view.setMinimumWidth((int)(width * 0.9f));


            final DialogPlus dialog = DialogPlus.newDialog(this)
                    .setContentHolder(new ViewHolder(view))
                    //.setFooter(R.layout.footer)
                    .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                    .setGravity(Gravity.TOP)
                    .setExpanded(true)//, (int) (height * 0.3f))
                    .setCancelable(true)
                    .setOnCancelListener(new OnCancelListener() {
                        @Override
                        public void onCancel(DialogPlus dialog) {
                            showTvTopics();
                        }
                    })
                    .create();
            dialog.show();
            ((ImageButton)view.findViewById(R.id.select_topic_done)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    showTvTopics();
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }


        final ChipCloud chipCloud = (ChipCloud) findViewById(R.id.chip_cloud);

        new GetTopics(new GetTopics.OnPostExecuteListener() {
            @Override
            public void onTaskDOne(boolean status, List<Topic> topicList) {
                try{
                    if(status){

                        String labels[] = new String[topicList.size()];
                        topicsSelected = new boolean[topicList.size()];
                        for(int i=0; i<topicList.size();i++){
                            labels[i]=topicList.get(i).name;
                        }

                        showChipCloud(labels,chipCloud);


                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        }).execute("");












    }
    void showChipCloud(String[] labels,ChipCloud chipCloud){
        new ChipCloud.Configure()
                .chipCloud(chipCloud)
                .selectTransitionMS(150)
                .deselectTransitionMS(150)
                .labels(labels)
                .mode(ChipCloud.Mode.MULTI)
                .allCaps(false)
                .gravity(ChipCloud.Gravity.CENTER)
                .textSize(getResources().getDimensionPixelSize(R.dimen.default_textsize))
                .verticalSpacing(getResources().getDimensionPixelSize(R.dimen.vertical_spacing))
                .minHorizontalSpacing(getResources().getDimensionPixelSize(R.dimen.min_horizontal_spacing))
                .chipListener(new ChipListener() {
                    @Override
                    public void chipSelected(int index) {
                        topicsSelected[index] = true;
                    }
                    @Override
                    public void chipDeselected(int index) {
                        topicsSelected[index] = false;
                    }
                })
                .build();

    }

    private void showTvTopics() {
        if(topicsSelected.length==0){
            selectTopicAll();
        }
        else{
            int temp=0;
            for(int i=0; i<topicsSelected.length;i++){
                if(topicsSelected[i]){
                    temp++;
                }
            }
            if(temp==0){
                selectTopicAll();
            }
            else {
                tvHTopic.setText("topics: "+temp);
            }
        }

    }

    private void selectTopicAll(){
        topicsSelected = new boolean[1];
        topicsSelected[0] = true;
        tvHTopic.setText("All");
    }


    /**
     * Change value in dp to pixels
     * @param dp
     * @param context
     * @return
     */
    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }


}
