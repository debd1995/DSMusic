package com.debd.kgp.dsmusic.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.WindowManager;
import android.widget.TextView;

import com.debd.kgp.dsmusic.R;
import com.debd.kgp.dsmusic.model.ServerConfiguration;
import com.debd.kgp.dsmusic.model.UserCredential;
import com.debd.kgp.dsmusic.restClient.ClientAPI;
import com.debd.kgp.dsmusic.utils.sharedPreferences.ServerConfigurationPreference;
import com.debd.kgp.dsmusic.utils.sharedPreferences.UserCredentialPreference;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashActivity extends AppCompatActivity {

    private boolean backKeyPressed = false;
    private Context context;

    private TextView splashText;

    private ServerConfigurationPreference serverConfigurationPreference;
    private ServerConfiguration serverConfiguration;

    private UserCredentialPreference userCredentialPreference;
    private UserCredential userCredential;

    private Runnable backgroundTask;
    private Handler handler;
    private int SPLASH_TIMEOUT = 1000;

    private Retrofit retrofit;

    @Override
    protected void onPause() {
        super.onPause();
        backKeyPressed = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        backKeyPressed = false;
        handler.postDelayed(backgroundTask, SPLASH_TIMEOUT);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        splashText = (TextView)findViewById(R.id.splash_text);
        context = this;

        backgroundTask = new BackgroundTask();
        handler = new Handler();

        serverConfigurationPreference = new ServerConfigurationPreference(this);
        userCredentialPreference = new UserCredentialPreference(this);

    }

    private class BackgroundTask implements Runnable {
        @Override
        public void run() {
            if(!backKeyPressed) {
                serverConfiguration = serverConfigurationPreference.getServerConfiguration();
                //if server configuration is present
                if(serverConfiguration != null) {
                    //  we need to check for user credentials,
                    //      if credential present and user is logged in,
                    //          then authenticate user to server
                    //              if authentication successfull, goto home
                    //              else go to login page
                    //       else
                    //          goto login page
                    //
                    //
                    userCredential = userCredentialPreference.getUserCredential();
                    if(userCredential != null && userCredential.getLoginStatus()) {

                        String authHeader = "";
                        String base = userCredential.getUsername()+":"+userCredential.getPassword();
                        authHeader = "Basic "+ Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);

                        //login user to server
                        Retrofit.Builder builder = new Retrofit.Builder()
                                .baseUrl("http://"+serverConfiguration.getURL()+":"+serverConfiguration.getPort()+"/DSMusicServer/api/")
                                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()));

                        retrofit = builder.build();
                        ClientAPI clientAPI = retrofit.create(ClientAPI.class);
                        Call<String> call = clientAPI.login(userCredential.getUsername(), authHeader);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {

                                if(response != null && response.code() == 200) {
                                    // login successful
                                    SplashActivity.this.finish();
                                    startActivity(new Intent(SplashActivity.this, ViewProfileActivity.class));
                                } else {
                                    SplashActivity.this.finish();
                                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                }
                            }
                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                SplashActivity.this.finish();
                                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                            }
                        });
                    } else {
                        // redirect user to enter login details
                        SplashActivity.this.finish();
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    }

                }else {
                    // if server configuration is not present,
                    // goto Server Info , because then we need it for further communication
                    SplashActivity.this.finish();
                    startActivity(new Intent(SplashActivity.this, ServerInfoActivity.class));
                }

            }
        }
    }
}
