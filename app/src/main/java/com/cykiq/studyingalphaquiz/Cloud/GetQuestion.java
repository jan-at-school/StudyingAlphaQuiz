package com.cykiq.studyingalphaquiz.Cloud;

import android.os.AsyncTask;
import android.util.Log;

import com.cykiq.studyingalphaquiz.Data.Question;
import com.cykiq.studyingalphaquiz.Data.User;
import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arifu on 8/21/2017.
 */

public class GetQuestion extends AsyncTask<String, Void, String> {
    private static final String TAG = "GetQuestion";

    public interface OnPostExecuteListener{
        void onTaskDOne(List<Question> questionList);
    }

    GetQuestion.OnPostExecuteListener onPostExecuteListener;


    //parameters
    int difficulty_level;
    String tags;
    int max;


    //response:
    List<Question> questionList;
    boolean status=false;

    public GetQuestion(int difficulty_level,String tags,int max, GetQuestion.OnPostExecuteListener onPostExecuteListener) {
        this.onPostExecuteListener = onPostExecuteListener;
        this.difficulty_level=difficulty_level;
        this.tags=tags;
        this.max=max;
    }

    public void getDetails() {
        String result=null, line=null;
        InputStream is = null;
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("difficulty_level", difficulty_level+""));
        nameValuePairs.add(new BasicNameValuePair("tags", tags+""));
        nameValuePairs.add(new BasicNameValuePair("max", max+""));



        try {
            HttpClient httpclient = new DefaultHttpClient();//http://arifullahjan.com/api/question.php?difficulty_level="+difficulty_level+"&tags="+tags+"&max="+max
            String url = "http://arifullahjan.com/api/question.php?difficulty_level="+difficulty_level+"&tags="+tags+"&max="+max+"&user_id=4";
            Log.d(TAG,url+"");
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpclient.execute(httpGet);
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

            final JSONObject questionObject = new JSONObject(result);
            JSONArray questionArray = questionObject.getJSONArray("questions");

            Gson gson = new Gson();
            questionList = new ArrayList<Question>();
            for(int i=0; i<questionArray.length();i++){


                questionList.add(gson.fromJson(questionArray.getJSONObject(i).toString(),Question.class));
            }


            status = true;



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
        onPostExecuteListener.onTaskDOne(questionList);
    }
}
