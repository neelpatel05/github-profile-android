package com.xyz.neelpatel.githubprofile;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.neelpatel.githubprofile.R;

public class MainActivity extends AppCompatActivity {

    EditText username;
    Button fetchprofile;
    String username1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fetchprofile = (Button) findViewById(R.id.fetchprofile);

        fetchprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = (EditText) findViewById(R.id.username);
                username1 = username.getText().toString();
                if(username1.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Error");
                    builder.setMessage("Username cannot be empty");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    builder.setCancelable(false);
                    builder.show();
                } else if(!isNetworkConnected()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Error");
                    builder.setMessage("Please connect to Internet");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    builder.setCancelable(false);
                    builder.show();
                }
                else {
                    Intent newintent = new Intent(v.getContext(),DisplayActivity.class);
                    newintent.putExtra("username",username1);
                    startActivity(newintent);
                }
            }
        });

    }

    private boolean isNetworkConnected() {
        getApplicationContext();
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        assert cm != null;
        return cm.getActiveNetworkInfo() != null;
    }


}
