package com.cykiq.studyingalphaquiz.Cloud;

import android.os.AsyncTask;
import android.util.Log;

import com.cykiq.studyingalphaquiz.Data.Topic;
import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arifu on 10/5/2017.
 */

public class GetTopics extends AsyncTask<String, Void, String> {
    private static final String TAG = "GetTags";

    public interface OnPostExecuteListener{
        void onTaskDOne(boolean status,List<Topic> topicList);
    }

    GetTopics.OnPostExecuteListener onPostExecuteListener;




    //response:
    List<Topic> topicList;
    boolean status=false;

    public GetTopics(GetTopics.OnPostExecuteListener onPostExecuteListener) {
        this.onPostExecuteListener = onPostExecuteListener;
    }

    public void getDetails() {
        String result=null, line=null;
        InputStream is = null;



        try {
            HttpClient httpclient = new DefaultHttpClient();//http://arifullahjan.com/api/question.php?difficulty_level="+difficulty_level+"&tags="+tags+"&max="+max
            HttpGet httpGet = new HttpGet("http://arifullahjan.com/api/topics.php");
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


            final JSONObject parentObject = new JSONObject(result);

            JSONArray topicArray = parentObject.getJSONArray("topics");

            Gson gson = new Gson();
            topicList = new ArrayList<Topic>();
            for(int i=0; i<topicArray.length();i++){


                topicList.add(gson.fromJson(topicArray.getJSONObject(i).toString(),Topic.class));
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
        onPostExecuteListener.onTaskDOne(status,topicList);
    }
}
