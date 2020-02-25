package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;

public class ComposeActivity extends AppCompatActivity {
    public static final int MAX_TWEET_LENGTH = 280;
    EditText etCompose;
    Button btnTweet;
    TextView tvDisplay;
    String TAG = "ComposeActivity";

    TwitterClient client;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate start...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        Log.i(TAG, "Content View Set");

        client = TwitterApplication.getRestClient(this);

        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);
        tvDisplay = findViewById(R.id.tvDisplay);
        tvDisplay.setText(etCompose.getText().length() + "/" + MAX_TWEET_LENGTH);
        btnTweet.setEnabled(false);


        //Set Click listener on button
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String tweetContent = etCompose.getText().toString();
                if(tweetContent.isEmpty()){
                    Toast.makeText(ComposeActivity.this, "Sorry, your Tweet cannot be empty!", LENGTH_LONG).show();
                    return;
                }
                else if(tweetContent.length() > MAX_TWEET_LENGTH){
                    Toast.makeText(ComposeActivity.this, "Sorry, your Tweet cannot be this long!!", LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(ComposeActivity.this, "Tweet sent!", LENGTH_LONG).show();
                //Make Api call to twitter to publish tweet
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "publishTweet success!");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "published tweet says: " + tweet.body);

                            //start the onSubmit callback to autoclose the activity and send the tweet to the timeline
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK, intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "publish tweet Failure", throwable);

                    }
                });

            }
        });

        EditText etValue = (EditText) findViewById(R.id.etCompose);
        etValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Fires right as the text is being changed (even supplies the range of text)
                if(etCompose.getText().length() >= MAX_TWEET_LENGTH){
                    tvDisplay.setTextColor(Color.RED);
                    btnTweet.setEnabled(false);
                }
                else
                {
                    tvDisplay.setTextColor(Color.GRAY);
                    btnTweet.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if(etCompose.getText().length() >= MAX_TWEET_LENGTH){
                    tvDisplay.setTextColor(Color.RED);
                    btnTweet.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Fires right after the text has changed
                tvDisplay.setText(etCompose.getText().length() + "/" + MAX_TWEET_LENGTH);
                if(etCompose.getText().length() >= MAX_TWEET_LENGTH){
                    tvDisplay.setTextColor(Color.RED);
                    btnTweet.setEnabled(false);
                }
                else if(etCompose.getText().length() == 0){
                    btnTweet.setEnabled(false);
                }
            }
        });
        Log.i(TAG, "onCreate End...");
    }
}
