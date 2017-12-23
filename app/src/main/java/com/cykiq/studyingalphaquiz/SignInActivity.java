package com.cykiq.studyingalphaquiz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cykiq.studyingalphaquiz.Cloud.SignIn;
import com.cykiq.studyingalphaquiz.Data.User;

import java.util.ArrayList;
import java.util.List;

public class SignInActivity extends AppCompatActivity {


    TextView error_text_view;
    Button login_button;
    ImageView image;

    String email;
    String password;
    User user;
    private ProgressBar progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//            requestWindowFeature(Window.FEATURE_NO_TITLE);
//            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        error_text_view = (TextView) findViewById(R.id.error_text_view);
        login_button = (Button) findViewById(R.id.login_button);
//        progress_layout = (LinearLayout) findViewById(R.id.progress);


        progressDialog = (ProgressBar) findViewById(R.id.progressBarDialog);
        image = (ImageView) findViewById(R.id.image);
        image.startAnimation(AnimationUtils.loadAnimation(SignInActivity.this, android.R.anim.fade_in));




        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String errorMessage = validateOrGet();
                if (errorMessage == null) {
                    error_text_view.setVisibility(View.GONE);
                    progressShow();

                    //communicates with server.. if signin succeeds user object is stored to local db
                    new SignIn(email, password, new SignIn.OnPostExecuteListener() {
                        @Override
                        public void onTaskDOne(boolean status,String password, final User user) {

                            if (status) {//if signin succeeded
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        loginSuccess(user);
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        error_text_view.setVisibility(View.VISIBLE);
                                        error_text_view.startAnimation(AnimationUtils.loadAnimation(SignInActivity.this, android.R.anim.fade_in));
                                        progressHide();

                                    }
                                });
                            }
                        }
                    }).execute("");
                } else {
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private String validateOrGet() {

        String temp = ((EditText)findViewById(R.id.username_edit_text)).getText().toString();

        if (temp.contains("@")) {
            email = temp;
        } else {
            return "Invalid email!";
        }

        temp = ((EditText)findViewById(R.id.password_edit_text)).getText().toString();
        if (temp.length() > 0) {
            password = temp;
        } else {
            return "enter your password";
        }
        return null;
    }


    /*
    * signin succeeded.
    * This is probably the first time so load stations data and save to db.
    * The station data will be updated everytime in the main activity but this makes things fast.
    * */
    private void loginSuccess(User user) {

        DatabaseHandler db = DatabaseHandler.getInstance(getApplicationContext());
        //db.insertUser(user); //TODO: insert user to db

        Toast.makeText(getApplicationContext(),user.toString(),Toast.LENGTH_LONG).show();
        //TODO: store shared preference



        //progressHide();
    }


    private void progressShow() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((EditText)findViewById(R.id.username_edit_text)).setEnabled(false);
                ((EditText)findViewById(R.id.password_edit_text)).setEnabled(false);
                progressDialog.setVisibility(View.VISIBLE);
                progressDialog.startAnimation(AnimationUtils.loadAnimation(SignInActivity.this, android.R.anim.fade_in));
                image.startAnimation(AnimationUtils.loadAnimation(SignInActivity.this, android.R.anim.fade_out));
                image.setVisibility(View.INVISIBLE);
                login_button.setEnabled(false);
                error_text_view.setVisibility(View.INVISIBLE);

            }
        });
    }

    private void progressHide() {
        runOnUiThread(new Runnable() {
            //@Override
            public void run() {
                ((EditText)findViewById(R.id.username_edit_text)).setEnabled(true);
                ((EditText)findViewById(R.id.password_edit_text)).setEnabled(true);
                image.setVisibility(View.VISIBLE);
                image.startAnimation(AnimationUtils.loadAnimation(SignInActivity.this, android.R.anim.fade_in));
                progressDialog.setVisibility(View.GONE);
                login_button.setEnabled(true);
            }
        });
    }


}
