package com.xyz.neelpatel.githubprofile;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.neelpatel.githubprofile.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RepositoryActivity extends AppCompatActivity {

    ProgressDialog pd;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repository);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            url = extras.getString("url");
        }
        //Toast.makeText(RepositoryActivity.this,url,Toast.LENGTH_SHORT).show();

        new JsonTask().execute(url);

    }

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(RepositoryActivity.this);
            pd.setMessage("Fetching ...");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return "User does not exists";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONArray jsonresult = null;

            LinearLayout ll = (LinearLayout) findViewById(R.id.linearlayout);

            if (pd.isShowing()) {
                pd.dismiss();
            }
            try {
                jsonresult = new JSONArray(result);
                Log.d("My App", jsonresult.toString());
            } catch (Throwable t) {
                Log.e("My App", "Could not parse malformed JSON: \"" + result + "\"");
            }
            //Toast.makeText(RepositoryActivity.this,jsonresult.toString(),Toast.LENGTH_SHORT).show();
            TextView t[] = new TextView[jsonresult.length()];

            for(int i=0;i<jsonresult.length();i++){
                t[i] = new TextView(RepositoryActivity.this);
                try {
                    t[i].setText("Name : " + jsonresult.getJSONObject(i).getString("name") + "\n\n" + "Description : " + jsonresult.getJSONObject(i).getString("description" ) + "\n\n" + "Language : " + jsonresult.getJSONObject(i).getString("language") + "\n\n" + "Fork count : " + jsonresult.getJSONObject(i).getString("forks_count") + "\n\n" + "Stars count : " + jsonresult.getJSONObject(i).getString("stargazers_count") + "\n\n" + "Default Branch : " + jsonresult.getJSONObject(i).getString("default_branch") + "\n\n" + "Created At : " + jsonresult.getJSONObject(i).getString("created_at")+ "\n\n" + "Last Push : " + jsonresult.getJSONObject(i).getString("pushed_at"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                layoutParams.setMargins(30,30,30,30);
                t[i].setLayoutParams(layoutParams);
                t[i].setBackgroundColor(Color.WHITE);
                t[i].setPadding(40, 40, 40, 40);
                t[i].setElevation(10);
                t[i].setTextSize(20);
                ll.addView(t[i]);
            }
        }

    }
}
