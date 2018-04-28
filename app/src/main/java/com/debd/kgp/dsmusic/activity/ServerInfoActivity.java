package com.debd.kgp.dsmusic.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.debd.kgp.dsmusic.BuildConfig;
import com.debd.kgp.dsmusic.R;
import com.debd.kgp.dsmusic.dialog.ExitDialog;
import com.debd.kgp.dsmusic.dialog.ExitDialogListener;
import com.debd.kgp.dsmusic.dialog.MessageDialog;
import com.debd.kgp.dsmusic.model.ServerConfiguration;
import com.debd.kgp.dsmusic.utils.sharedPreferences.ServerConfigurationPreference;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerInfoActivity extends AppCompatActivity implements View.OnClickListener, ExitDialogListener{

    private EditText edtURL, edtPort;
    private Button btnGo;

    private ServerConfigurationPreference serverConfigPreference;
    private ServerConfiguration serverConfiguration;

    private String baseURL;
    private int portNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_info);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initUIComponents();

        serverConfigPreference = new ServerConfigurationPreference(this);
        serverConfiguration = serverConfigPreference.getServerConfiguration();

        displayServerConfig(serverConfiguration);
    }

    private void displayServerConfig(ServerConfiguration configuration) {
        if(configuration != null) {
            if(configuration.getURL() != null) {
                edtURL.setText(configuration.getURL());
            }
            if(configuration.getPort() > 0) {
                edtPort.setText(""+configuration.getPort());
            }
        }
    }

    private void initUIComponents() {
        edtURL = (EditText)findViewById(R.id.edt_base_url);
        edtPort = (EditText)findViewById(R.id.edt_port);
        btnGo = (Button)findViewById(R.id.btn_go);
        btnGo.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        ExitDialog dialog = new ExitDialog(this);
        dialog.setRetainInstance(true);
        dialog.show(getSupportFragmentManager(), "Exit");
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_go :
                if(validateData()) {
                    saveServerConfig();
                    finish();
                    startActivity(new Intent(this, LoginActivity.class));
                }
                break;
        }
    }

    private void saveServerConfig() {

        serverConfiguration = new ServerConfiguration(baseURL, portNumber);
        serverConfigPreference.saveServerConfiguration(serverConfiguration);
    }



    private boolean validateData() {

        boolean success = false;

        try {
            baseURL = edtURL.getText().toString().trim();
            if(baseURL.length() < 1) {
                edtURL.requestFocus();
                throw new Exception("Provide Base URL");
            }
            if(!validateURL(baseURL)) {
                edtURL.requestFocus();
                throw new Exception("Not a valid Base URL \n"+
                "Example : \n" +
                "http://www.google.com \n"+
                "https://www.google.com \n"+
                "http://192.168.0.1 \n"+
                "https://192.168.0.1");
            }
            try {
                if(edtPort.getText().toString().trim().length() < 1) {
                    throw new Exception("Provide port number");
                }
                portNumber = Integer.parseInt(edtPort.getText().toString().trim());

                if(portNumber > 65535)
                    throw new Exception("Not a valid port");
            }catch (Exception e) {
                edtPort.requestFocus();
                throw new Exception("Not a valid port");
            }

            success = true;
        }catch (NumberFormatException e) {
            success = false;
            showError(e.getMessage());
            if(BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }catch (Exception e) {
            success = false;
            showError(e.getMessage());
            if(BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }

        return success;
    }

    private boolean validateURL(String url) {
        String patternString = "^(http:\\/\\/|https:\\/\\/)?(www.)?([a-zA-Z0-9]+).[a-zA-Z0-9]*.[a-z]{3}.?([a-z]+)?$";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(url);
        //return matcher.matches();
        return true;

    }

    private void showError(String message) {
        MessageDialog messageDialog = new MessageDialog();
        messageDialog.setRetainInstance(true);
        messageDialog.setDescription(message);
        messageDialog.show(getSupportFragmentManager(), "Error");
    }

    @Override
    public void onExit(boolean status) {
        if(status) {
            finish();
            System.exit(0);
            return;
        }
    }
}
