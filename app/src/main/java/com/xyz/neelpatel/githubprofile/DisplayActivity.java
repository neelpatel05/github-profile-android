package com.xyz.neelpatel.githubprofile;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.neelpatel.githubprofile.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DisplayActivity extends AppCompatActivity {

    ProgressDialog pd;
    String username;
    String url;

    TextView profile_name,profile_bio,profile_location,profile_repos,profile_followers,profile_following,profile_events,profile_organizations;
    ImageView profile_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        profile_name = (TextView) findViewById(R.id.profile_name);
        profile_bio = (TextView) findViewById(R.id.profile_bio);
        profile_location = (TextView) findViewById(R.id.profile_location);
        profile_repos = (TextView) findViewById(R.id.profile_repos);
        profile_followers = (TextView) findViewById(R.id.profile_followers);
        profile_following = (TextView) findViewById(R.id.profile_following);
        profile_events = (TextView) findViewById(R.id.profile_events);
        profile_organizations = (TextView) findViewById(R.id.profile_oraganizations);

        profile_photo = (ImageView) findViewById(R.id.profile_photo);


        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            username = extras.getString("username");
        }

        url = "https://api.github.com/users/"+username;
        new JsonTask().execute(url);


    }

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(DisplayActivity.this);
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
                    buffer.append(line+"\n");
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
            JSONObject jsonresult = null;
            if (pd.isShowing()) {
                pd.dismiss();
            }
            if (result.equals("User does not exists")) {

                AlertDialog.Builder builder = new AlertDialog.Builder(DisplayActivity.this);
                builder.setTitle("Error");
                builder.setMessage("User does not exists");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent newintent = new Intent(DisplayActivity.this,MainActivity.class);
                        startActivity(newintent);
                    }
                });
                builder.setCancelable(false);
                builder.show();

            } else {
                try {

                    jsonresult = new JSONObject(result);
                    Log.d("My App", jsonresult.toString());

                } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + result + "\"");
                }
                try {
                    String x, y, z;
                    x = jsonresult.getString("name");
                    y = jsonresult.getString("bio");
                    z = jsonresult.getString("location");
                    if (x == null) {
                        x = "Not Available";
                        profile_name.setText(x);
                    } else {
                        profile_name.setText(x);
                    }

                    if (y == null) {
                        y = "Not Available";
                        profile_bio.setText(y);
                    } else {
                        profile_bio.setText(y);
                    }

                    if (z == null) {
                        z = "Not Available";
                        profile_location.setText(z);
                    } else {
                        profile_location.setText(z);
                    }

                    profile_repos.setText("Repository | " + jsonresult.getString("public_repos"));
                    profile_followers.setText("Followers | " + jsonresult.getString("followers"));
                    profile_following.setText("Following | " + jsonresult.getString("following"));

                    new ImageRequest().execute(jsonresult.getString("avatar_url"));

                    profile_repos.setAnimation(AnimationUtils.loadAnimation(DisplayActivity.this,android.R.anim.slide_in_left));
                    profile_followers.setAnimation(AnimationUtils.loadAnimation(DisplayActivity.this,android.R.anim.slide_in_left));
                    profile_following.setAnimation(AnimationUtils.loadAnimation(DisplayActivity.this,android.R.anim.slide_in_left));
                    profile_events.setAnimation(AnimationUtils.loadAnimation(DisplayActivity.this,android.R.anim.slide_in_left));
                    profile_organizations.setAnimation(AnimationUtils.loadAnimation(DisplayActivity.this,android.R.anim.slide_in_left));

                    //TextView OnClicks
                    final JSONObject finalJsonresult = jsonresult;
                    profile_repos.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent newintent = new Intent(DisplayActivity.this,RepositoryActivity.class);
                            try {
                                newintent.putExtra("url", finalJsonresult.getString("repos_url"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            startActivity(newintent);
                        }
                    });

                    profile_followers.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent newintent = new Intent(DisplayActivity.this,FollowersActivity.class);
                            try {
                                newintent.putExtra("url", finalJsonresult.getString("followers_url"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            startActivity(newintent);
                        }
                    });

                    profile_following.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent newintent = new Intent(DisplayActivity.this,FollowingActivity.class);
                            newintent.putExtra("url", "https://api.github.com/users/" +username+"/following");
                            startActivity(newintent);
                        }
                    });

                    profile_events.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent newintent = new Intent(DisplayActivity.this,EventsActivity.class);
                            newintent.putExtra("url", "https://api.github.com/users/" + username+"/events");
                            startActivity(newintent);
                        }
                    });

                    profile_organizations.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent newintent = new Intent(DisplayActivity.this,OrganizationsActivity.class);
                            newintent.putExtra("url", "https://api.github.com/users/" + username+"/orgs");
                            startActivity(newintent);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class ImageRequest extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            }catch (Exception e){
                //Log.d(TAG,e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            ImageView imageView = (ImageView) findViewById(R.id.profile_photo);
            imageView.setImageBitmap(result);
        }
    }
}


