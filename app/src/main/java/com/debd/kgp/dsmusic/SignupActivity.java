package com.debd.kgp.dsmusic;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.debd.kgp.dsmusic.dialog.MessageDialog;
import com.debd.kgp.dsmusic.dialog.ExitDialog;
import com.debd.kgp.dsmusic.dialog.ExitDialogListener;
import com.debd.kgp.dsmusic.model.ServerConfiguration;
import com.debd.kgp.dsmusic.model.UserCredential;
import com.debd.kgp.dsmusic.restClient.ClientAPI;
import com.debd.kgp.dsmusic.model.restAPIModel.Error;
import com.debd.kgp.dsmusic.model.restAPIModel.User;
import com.debd.kgp.dsmusic.utils.Constants;
import com.debd.kgp.dsmusic.utils.sharedPreferences.ServerConfigurationPreference;
import com.debd.kgp.dsmusic.utils.sharedPreferences.UserCredentialPreference;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener,
        ExitDialogListener, PopupMenu.OnMenuItemClickListener, MessageDialog.OnMessageDialogDismissedListener {

    private EditText edtFName, edtLName, edtUsername, edtPassword, edtEmail, edtPhone;
    private Button btnSignup;
    private ImageView passwordVisibility, btnMenu;
    private boolean isPasswordVisible;
    private ServerConfigurationPreference serverConfigurationPreference;
    private ServerConfiguration serverConfiguration;

    private UserCredentialPreference userCredentialPreference;
    private UserCredential userCredential;

    private Retrofit retrofit;

    @Override
    public void onBackPressed() {
        ExitDialog dialog = new ExitDialog(this);
        dialog.setRetainInstance(true);
        dialog.show(getSupportFragmentManager(), "Exit");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initUIComponents();
        serverConfigurationPreference = new ServerConfigurationPreference(this);
        serverConfiguration = serverConfigurationPreference.getServerConfiguration();

        userCredentialPreference = new UserCredentialPreference(this);

    }

    private void initUIComponents() {
        edtFName = (EditText)findViewById(R.id.edt_fname);
        edtLName = (EditText)findViewById(R.id.edt_lname);
        edtUsername = (EditText)findViewById(R.id.edt_username);
        edtPassword = (EditText)findViewById(R.id.edt_password);
        edtPassword.setTypeface(Typeface.DEFAULT_BOLD);
        edtEmail = (EditText)findViewById(R.id.edt_email);
        edtPhone = (EditText)findViewById(R.id.edt_phone);

        btnSignup = (Button)findViewById(R.id.btn_signup);
        btnSignup.setOnClickListener(this);

        passwordVisibility = (ImageView)findViewById(R.id.password_visibility);
        passwordVisibility.setOnClickListener(this);

        btnMenu = (ImageView)findViewById(R.id.btn_menu);
        btnMenu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.password_visibility :
                if(isPasswordVisible) {
                    isPasswordVisible = false;
                    edtPassword.setTransformationMethod(new PasswordTransformationMethod());
                    passwordVisibility.setImageResource(R.drawable.visibility_off);
                } else {
                    isPasswordVisible = true;
                    edtPassword.setTransformationMethod(null);
                    passwordVisibility.setImageResource(R.drawable.visibity_on);
                }
                break;
            case R.id.btn_signup :
                boolean validate = validateData();
                if(validate) {
                    //if validate signup and on success saveData and go ahead
                    String fname = edtFName.getText().toString().trim();
                    String lname = edtLName.getText().toString().trim();
                    String uname = edtUsername.getText().toString().trim();
                    String pass = edtPassword.getText().toString().trim();
                    long phn = Long.parseLong(edtPhone.getText().toString().trim());
                    String email = edtEmail.getText().toString().trim();

                    User user = new User(phn, uname, pass, fname, lname, email);
                    SignupAsync signupAsync = new SignupAsync(this);
                    signupAsync.execute(user);
                }
                break;
            case R.id.btn_menu :
                PopupMenu popupMenu = new PopupMenu(this, btnMenu);
                popupMenu.getMenuInflater().inflate(R.menu.signup_option_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.show();
                break;
        }
    }

    private class SignupAsync extends AsyncTask<User, Void, Response<User>> {
        private ProgressDialog progressDialog;
        private Context context;
        public SignupAsync(Context context) {
            this.context = context;
            progressDialog = new ProgressDialog(context);
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle("Signing in");
            progressDialog.setMessage("please wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Response<User> response) {
            progressDialog.dismiss();
            if (response == null) {
                //error, cant reach server
                MessageDialog messageDialog = new MessageDialog();
                messageDialog.setRetainInstance(true);
                messageDialog.setTitle("Signup failed");
                messageDialog.setDescription(
                        "\nError, can't reach server"+
                                "\nCheck your network connection");
                messageDialog.show(getSupportFragmentManager(), "error");
            } else {
                //connection successful, handle response.

                if(response.isSuccessful()) {
                    // successful response
                    if(response.code() == 201) {
                        // signup successful
                        String fname = null;
                        if(response.body() != null) {
                            fname = response.body().getfName();
                        }
                        MessageDialog messageDialog = new MessageDialog();
                        messageDialog.setRetainInstance(true);
                        messageDialog.setOnMessageDialogDismissedListener("signupSuccessful", SignupActivity.this);
                        messageDialog.setTitle("Account created");
                        messageDialog.setDescription("Welcome"+(fname!= null?(" "+fname):null)+
                                "\nyour account has been created successfully"+
                                        "\nEnjoy listening");
                        messageDialog.show(getSupportFragmentManager(), "success");
                    } else {
                        Toast.makeText(SignupActivity.this, "Code : "+response.code(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    //error response
                    Error serverError = parseError(response.errorBody());
                    String errorTitle = "Signup Failed";
                    String errorDesc = "Unknown Error";

                    if(serverError != null) {
                        errorDesc = serverError.getDescription()
                                +"\n"+serverError.getResponseMessage()
                                +"\n"+serverError.getSuggession();
                    }
                    MessageDialog signupErrorDialog = new MessageDialog();
                    signupErrorDialog.setRetainInstance(true);
                    signupErrorDialog.setTitle(errorTitle);
                    signupErrorDialog.setDescription(errorDesc);
                    signupErrorDialog.show(getSupportFragmentManager(), "error");


                }


            }

        }

        private Error parseError(ResponseBody responseBody) {
            Converter<ResponseBody, Error> errorConverter =
                    SignupActivity.this
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
        protected Response<User> doInBackground(User... params) {
            User user = params[0];
            if(user == null) {
                return  null;
            }
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl("http://"+serverConfiguration.getURL()+":"+serverConfiguration.getPort()+"/DSMusicServer/api/")
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()));

            retrofit = builder.build();
            ClientAPI clientAPI = retrofit.create(ClientAPI.class);
            Call<User> call = clientAPI.signup(user);
            Response<User> response = null;
            try {
                response = call.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
    }


    private void saveData() {
        userCredentialPreference.saveUserCredential(new UserCredential(
                edtUsername.getText().toString().trim(),
                edtPassword.getText().toString().trim(),
                false,
                true
        ));
    }

    private boolean validateData() {
        boolean status = false;

        try {

            if(edtFName.getText().toString().trim().length()<1) {
                edtFName.requestFocus();
                throw new Exception("Provide First Name");
            }
            if(edtFName.getText().toString().trim().length()>20) {
                edtFName.requestFocus();
                throw new Exception("First Name should be maximum of 20 characters");
            }
            if(edtLName.getText().toString().trim().length()<1) {
                edtLName.requestFocus();
                throw new Exception("Provide Last Name");
            }
            if(edtLName.getText().toString().trim().length()>20) {
                edtLName.requestFocus();
                throw new Exception("Last Name should be maximum of 20 characters");
            }
            if(edtUsername.getText().toString().trim().length()<1) {
                edtUsername.requestFocus();
                throw new Exception("Provide username");
            }
            if(edtUsername.getText().toString().trim().length()>20) {
                edtUsername.requestFocus();
                throw new Exception("Username should be maximum of 20 characters");
            }
            if(edtPassword.getText().toString().trim().length()<1) {
                edtPassword.requestFocus();
                throw new Exception("Provide password");
            }
            if(!validatePassword(edtPassword.getText().toString().trim())) {
                edtPassword.requestFocus();
                throw new Exception("Password must have \n" +
                        "    At least one upper case English letter,\n" +
                        "    At least one lower case English letter,\n" +
                        "    At least one digit,\n" +
                        "    At least one special character, # ? ! @ $ % ^ & * -\n" +
                        "    Minimum eight in length");
            }
            if(edtEmail.getText().toString().trim().length()<1) {
                edtEmail.requestFocus();
                throw new Exception("Provide email");
            }
            if (!validateEmail(edtEmail.getText().toString().trim())){
                edtEmail.requestFocus();
                throw new Exception("Not a valid email");
            }
            try {
                long phn = Long.parseLong(edtPhone.getText().toString().trim());
                if(Long.toString(phn).length() > 12 || Long.toString(phn).length() < 10) {
                    throw new Exception("Enter valid phone number");
                }
            }catch (Exception e){
                edtPhone.requestFocus();
                throw new Exception("Enter valid phone number");
            }

            status = true;

        }catch (NumberFormatException e) {
            status = false;
            showError(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            status = false;
            showError(e.getMessage());
            e.printStackTrace();
        }

        return status;
    }

    private void showError(String message) {
        MessageDialog messageDialog = new MessageDialog();
        messageDialog.setRetainInstance(true);
        messageDialog.setTitle("ERROR");
        messageDialog.setDescription(message);
        messageDialog.show(getSupportFragmentManager(), "Error");
    }

    private boolean validateEmail(String email) {
        String patternString = "^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}$";
        boolean success = false;

        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(email);
        success = matcher.matches();

        return success;

    }
    private boolean validatePassword(String pass) {
        String patternString = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";
        boolean success = false;

        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(pass);
        success = matcher.matches();

        return success;

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
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login :
                finish();
                startActivity(new Intent(this, LoginActivity.class));
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
        if(key.equals("signupSuccessful")) {
            saveData();
            SignupActivity.this.finish();
            startActivity(new Intent(SignupActivity.this, ViewProfileActivity.class));
        }
    }
}
