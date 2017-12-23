package com.cykiq.studyingalphaquiz.Cloud;

import android.os.AsyncTask;
import android.util.Log;

import com.cykiq.studyingalphaquiz.Data.User;
import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by arifu on 8/21/2017.
 */

public class SignIn extends AsyncTask<String, Void, String> {
    private static final String TAG = "SignIn";

    String email;
    String password;


    //outputs
    User user;
    boolean successStatus=false;
    String message = "Failed";


    public interface OnPostExecuteListener{
        void onTaskDOne(boolean status, String message , User user);
    }

    SignIn.OnPostExecuteListener onPostExecuteListener;

    public SignIn(String email,String password, SignIn.OnPostExecuteListener onPostExecuteListener) {
        this.onPostExecuteListener = onPostExecuteListener;
        this.email=email;
        this.password=password;
    }

    public void getDetails() {
        String result=null, line=null;
        InputStream is = null;
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("email", email));
        nameValuePairs.add(new BasicNameValuePair("password", password));


        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://arifullahjan.com/api/signIn.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            Log.d("pass 1", "connection success ");
        } catch (Exception e) {
            Log.d("Fail 1", e.toString());
            Log.d("Status: ", "Invalid IP Address");
        }



        try {
            BufferedReader reader = new BufferedReader
                    (new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
            Log.d(TAG,result);
            Log.d("pass 2", "connection success ");
        } catch (Exception e) {
            Log.d("Fail 2", e.toString());
        }

        try {

            final JSONObject responseObject = new JSONObject(result);
            message = responseObject.getString("message");
            if (Integer.parseInt(responseObject.getString("success_status")) == 1){
                successStatus=true;
                JSONObject userObject = responseObject.getJSONObject("user");
                Gson gson = new Gson();
                user = gson.fromJson(userObject.toString(),User.class);

            }
        } catch (Exception e) {
            Log.d("Fail 3", e.toString());
        }
    }



    @Override
    protected String doInBackground(String... params) {
        getDetails();
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(onPostExecuteListener==null)return;
        onPostExecuteListener.onTaskDOne(successStatus,message,user);
    }
}
