package com.debd.kgp.dsmusic.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.debd.kgp.dsmusic.R;
import com.debd.kgp.dsmusic.dialog.ExitDialog;
import com.debd.kgp.dsmusic.dialog.ExitDialogListener;
import com.debd.kgp.dsmusic.dialog.MessageDialog;
import com.debd.kgp.dsmusic.model.ServerConfiguration;
import com.debd.kgp.dsmusic.model.UserCredential;
import com.debd.kgp.dsmusic.restClient.ClientAPI;
import com.debd.kgp.dsmusic.model.restAPIModel.Error;
import com.debd.kgp.dsmusic.utils.sharedPreferences.ServerConfigurationPreference;
import com.debd.kgp.dsmusic.utils.sharedPreferences.UserCredentialPreference;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        ExitDialogListener, PopupMenu.OnMenuItemClickListener, MessageDialog.OnMessageDialogDismissedListener{


    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private ImageView chk_remember, passwordVisibity, btnMenu;
    private TextView signup;
    private LinearLayout chk_remember_panel;

    private boolean doRemember = false, isPasswordVisible = false;


    private UserCredentialPreference userCredentialPreference;
    private UserCredential userCredential;

    private ServerConfigurationPreference serverConfigurationPreference;
    private ServerConfiguration serverConfiguration;

    private Retrofit retrofit;

    private ProgressDialog progressDialog;
    private Handler handler;

    @Override
    public void onBackPressed() {
        ExitDialog dialog = new ExitDialog(this);
        dialog.setRetainInstance(true);
        dialog.show(getSupportFragmentManager(), "Exit");
    }

    @Override
    public void onExit(boolean status) {
        if(status) {
            finish();
            System.exit(0);
            return;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initUIComponents();

        userCredentialPreference = new UserCredentialPreference(this);
        userCredential = userCredentialPreference.getUserCredential();

        serverConfigurationPreference = new ServerConfigurationPreference(this);
        serverConfiguration = serverConfigurationPreference.getServerConfiguration();
        displayUserCred();

        progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        handler = new Handler();

    }

    private void displayUserCred() {
        if(userCredential != null && userCredential.getRememberStatus()) {
            edtUsername.setText(userCredential.getUsername());
            edtPassword.setText(userCredential.getPassword());
            doRemember = true;
            chk_remember.setImageResource(R.drawable.checkbox_checked);
        }
    }

    private void initUIComponents() {
        edtUsername = (EditText)findViewById(R.id.edt_username);
        edtPassword = (EditText)findViewById(R.id.edt_password);
        edtPassword.setTypeface(Typeface.DEFAULT_BOLD);

        btnLogin = (Button)findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);

        chk_remember = (ImageView)findViewById(R.id.chk_remember);

        chk_remember_panel = (LinearLayout)findViewById(R.id.chk_remember_panel);
        chk_remember_panel.setOnClickListener(this);

        passwordVisibity = (ImageView)findViewById(R.id.password_visibility);
        passwordVisibity.setOnClickListener(this);

        btnMenu = (ImageView)findViewById(R.id.btn_menu);
        btnMenu.setOnClickListener(this);

        signup = (TextView)findViewById(R.id.txt_signup_here);
        signup.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login :
                //login here , if successful, saveCred and go ahead. else show error message
                LoginAsync loginAsync = new LoginAsync(this);
                loginAsync.execute(new String[]{edtUsername.getText().toString().trim(),
                        edtPassword.getText().toString().trim()});
                break;
            case R.id.chk_remember_panel :
                if(doRemember) {
                    doRemember = false;
                    chk_remember.setImageResource(R.drawable.checkbox_unchecked);
                } else {
                    doRemember = true;
                    chk_remember.setImageResource(R.drawable.checkbox_checked);
                }
                break;
            case R.id.password_visibility :
                if(isPasswordVisible) {
                    isPasswordVisible = false;
                    edtPassword.setTransformationMethod(new PasswordTransformationMethod());
                    passwordVisibity.setImageResource(R.drawable.visibility_off);
                } else {
                    isPasswordVisible = true;
                    edtPassword.setTransformationMethod(null);
                    passwordVisibity.setImageResource(R.drawable.visibity_on);
                }
                break;
            case R.id.txt_signup_here :
                Intent intent = new Intent(this, SignupActivity.class);
                finish();
                startActivity(intent);
                break;
            case R.id.btn_menu :
                PopupMenu popupMenu = new PopupMenu(this, btnMenu);
                popupMenu.getMenuInflater().inflate(R.menu.login_option_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.show();
                break;
        }
    }

    private class LoginAsync extends AsyncTask<String, Void, Response<String>> {
        private Context context;
        private ProgressDialog progressDialog;
        public LoginAsync(Context context) {
            this.context = context;
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Loggin in");
            progressDialog.setMessage("please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Response<String> response) {
            progressDialog.dismiss();
            if(response != null) {
                if(response.isSuccessful()) {
                    if(response.code() == 200) {
                        // login successful
                        // show success message and go to home
                        MessageDialog messageDialog = new MessageDialog();
                        messageDialog.setRetainInstance(true);
                        messageDialog.setOnMessageDialogDismissedListener("loginSuccessful", LoginActivity.this);
                        messageDialog.setTitle("Login Successful");
                        messageDialog.setDescription("You have successfully logged in\nWelcome "+response.body().toString());
                        messageDialog.show(getSupportFragmentManager(), "logged_in");

                        saveCredPreference(new UserCredential(
                                edtUsername.getText().toString().trim(),
                                edtPassword.getText().toString().trim(),
                                doRemember,
                                true
                        ));
                    }
                } else {
                    //some error happen on server side
                    Error serverError = parseError(response.errorBody());
                    String errorTitle = "Login Failed";
                    String errorDesc = "Unknown Error";

                    if(serverError != null) {
                        errorDesc = serverError.getDescription()
                                +"\n"+serverError.getResponseMessage()
                                +"\n"+serverError.getSuggession();
                    }
                    MessageDialog authErrorDialog = new MessageDialog();
                    authErrorDialog.setRetainInstance(true);
                    authErrorDialog.setOnMessageDialogDismissedListener("errorDialog", LoginActivity.this);
                    authErrorDialog.setTitle(errorTitle);
                    authErrorDialog.setDescription(errorDesc);
                    authErrorDialog.show(getSupportFragmentManager(), "error");

                }
            } else {
                MessageDialog messageDialog = new MessageDialog();
                messageDialog.setRetainInstance(true);
                messageDialog.setOnMessageDialogDismissedListener("errorDialog", LoginActivity.this);
                messageDialog.setTitle("Login failed");
                messageDialog.setDescription(
                        "\nFailed to connect to server"+
                                "\nCheck your network connection");
                messageDialog.show(getSupportFragmentManager(), "error");
            }
        }

        private Error parseError(ResponseBody responseBody) {
            Converter<ResponseBody, Error> errorConverter =
                    LoginActivity.this
                            .retrofit
                            .responseBodyConverter(Error.class, new Annotation[0]);
            Error error = null;
            try {
                error = errorConverter.convert(responseBody);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return error;
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }

        @Override
        protected Response<String> doInBackground(String... params) {

            String authHeader = null;
            String uname = params[0];
            String pass = params[1];

            if(uname.length() >= 1 && pass.length() >= 1) {
                String base = uname+":"+pass;
                authHeader = "Basic "+ Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
            }
            //login user to server
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl("http://"+serverConfiguration.getURL()+":"+serverConfiguration.getPort()+"/DSMusicServer/api/")
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()));

            retrofit = builder.build();
            ClientAPI clientAPI = retrofit.create(ClientAPI.class);
            Call<String> call = clientAPI.login(uname, authHeader);

            Response<String> res = null;

            try {
                res = call.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return res;

        }
    }

    /*
    private void login() {

        //progressDialog.show(this, "Logging you in", "please wait...", true, false);

        String authHeader = null;
        String uname = edtUsername.getText().toString().trim();
        String pass = edtPassword.getText().toString().trim();

        if(uname.length() >= 1 && pass.length() >= 1) {
            String base = uname+":"+pass;
            authHeader = "Basic "+ Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
        }

        *//*Toast.makeText(this, "Connection to "+"http://"+serverConfiguration.getURL()+":"+serverConfiguration.getPort()+"/DSMusicServer/api/", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Auth Header : "+authHeader, Toast.LENGTH_SHORT).show();*//*
        //login user to server
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://"+serverConfiguration.getURL()+":"+serverConfiguration.getPort()+"/DSMusicServer/api/")
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()));

        retrofit = builder.build();
        ClientAPI clientAPI = retrofit.create(ClientAPI.class);
        Call<String> call = clientAPI.login(edtUsername.getText().toString().trim(), authHeader);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                //Toast.makeText(LoginActivity.this, "Status Code : "+response.code(), Toast.LENGTH_SHORT).show();


            }

            private Error parseError(ResponseBody responseBody) {

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

                t.printStackTrace();
                MessageDialog messageDialog = new MessageDialog();
                messageDialog.setRetainInstance(true);
                messageDialog.setOnMessageDialogDismissedListener("errorDialog", LoginActivity.this);
                messageDialog.setTitle("Login failed");
                messageDialog.setDescription(t.getMessage()+
                        "\nFailed to connect to server"+
                "\nCheck your network connection");
                messageDialog.show(getSupportFragmentManager(), "error");
            }
        });


    }*/

    private void saveCredPreference(UserCredential credential) {
        userCredentialPreference.saveUserCredential(credential);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signup :
                finish();
                startActivity(new Intent(this, SignupActivity.class));
                break;
            case R.id.exit :
                onBackPressed();
                break;
            case R.id.server_config :
                finish();
                startActivity(new Intent(this, ServerInfoActivity.class));
                break;
        }
        return true;
    }

    @Override
    public void dialogDismissed(String key) {
        if(key.equals("loginSuccessful")) {
            LoginActivity.this.finish();
            startActivity(new Intent(LoginActivity.this, ViewProfileActivity.class));
        }
        if ((key.equals("errorDialog"))) {
            //progressDialog.dismiss();
        }
    }
}
