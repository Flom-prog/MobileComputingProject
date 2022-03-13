package com.flom.mobilecomputingproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executor;


public class LoginActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    private ConstraintLayout constraintLayout;
    private TextView tvTimeMsg;

    private ImageView profileLoginPicture;

    private Button button_login; // Button used to launch the "openMainMenu()" method.
    private Button button_createAccount; // Button used to launch the "openCreateAccountMenu()" method.
    private EditText TextIL_username; // Button used to launch the "openMainMenu()" method.
    private EditText TextIL_password; // Button used to launch the "openMainMenu()" method.
    private Switch switch_autofill;


    private ImageButton button_biometric;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private Executor executor;


    private String myLanguage = "en";
    private ImageButton language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Status bar transparent
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        if (Build.VERSION.SDK_INT >= 19) getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_login);

        preferences = PreferenceManager.getDefaultSharedPreferences(this); //Initializes the SharedPreferences

        language = findViewById(R.id.language);


        constraintLayout = findViewById(R.id.containerlogin);
        tvTimeMsg = findViewById(R.id.tv_time_login_msg);

        profileLoginPicture = findViewById(R.id.profileLoginPicture);

        setBackground();

        button_login = findViewById(R.id.btn_login_signin);
        button_createAccount = findViewById(R.id.btn_login_signup);
        TextIL_username = findViewById(R.id.ET_login_username);
        TextIL_password = findViewById(R.id.ET_login_password);
        switch_autofill = findViewById(R.id.switch_autofill);

        button_biometric = findViewById(R.id.btn_login_biometric_signin);


        String mImageUri = preferences.getString("image", null);
        if (mImageUri != null) profileLoginPicture.setImageURI(Uri.parse(mImageUri));

        if (preferences.getString("username", "").equals("") && preferences.getString("password", "").equals("")) button_biometric.setVisibility(View.GONE);
        else button_biometric.setVisibility(View.VISIBLE);

        if (preferences.getBoolean("autofill", false)) {
            switch_autofill.setChecked(true);
            TextIL_username.setText(preferences.getString("username", "")); //Gets the username from the SharedPreferences and put it in the user name space
        } else {
            switch_autofill.setChecked(false);
        }

        // Set a listener on the Log-In button
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log_in();
            }
        });

        // Set a listener on the Create Account button
        button_createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateAccountMenu();
            }
        });
        // Set a listener on the Autofill switch
        switch_autofill.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeAutofillPolicy(isChecked);
            }
        });


        executor = ContextCompat.getMainExecutor(LoginActivity.this);

        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);

                Toast.makeText(LoginActivity.this, getString(R.string.authentication_error) + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                //Toast.makeText(LoginActivity.this, "Authentication succeed", Toast.LENGTH_SHORT).show();
                openMainMenu();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();

                Toast.makeText(LoginActivity.this, R.string.authentication_failed, Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.biometric_authentication))
                .setSubtitle(getString(R.string.login_using_fingerprint))
                .setNegativeButtonText(getString(R.string.use_password))
                .build();


        button_biometric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                biometricPrompt.authenticate(promptInfo);
            }
        });




        Resources res = getResources();
        Configuration conf = res.getConfiguration();
        setLocale(conf.locale.toString().split("_")[0], false);

        language.setOnClickListener(view -> {
            if (myLanguage.equals("fr")) setLocale("en", true);
            else setLocale("fr", true);
        });
    }


    public void setLocale(String lang, boolean reload) {
        switch (lang) {
            case "fr":
                language.setBackgroundResource(R.drawable.fr);
                break;
            case "en":
                language.setBackgroundResource(R.drawable.en);
                break;
        }
        myLanguage = lang;

        if (reload) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("language", lang);
            editor.apply();
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = new Locale(lang);
            res.updateConfiguration(conf, dm);
            Intent refresh = new Intent(this, LoginActivity.class);
            finish();
            startActivity(refresh);
        }
    }


    private void changeAutofillPolicy(Boolean policy) {
        SharedPreferences.Editor editor = preferences.edit(); //Initializes the SharedPreferences' editor

        //Saves the autofill policy in the SharedPreferences
        editor.putBoolean("autofill", policy);

        editor.apply(); //Applies the changes
    }

    /**
     * Method used to open check the log-in credentials and opens the Main Menu if they are correct.
     */
    private void log_in() {
        String username = TextIL_username.getText().toString(); //Gets the username typed by the user in the EditText
        String password = TextIL_password.getText().toString(); //Gets the password typed by the user in the EditText

        String user_username = preferences.getString("username", ""); //Gets the username from the SharedPreferences
        String user_pass = preferences.getString("password", ""); //Gets the password from the SharedPreferences

        // Compares the log-in credentials entered with the ones saved
        if ((username.equals(user_username)) && (!username.equals(""))) { //if the username is correct
            if (password.equals(user_pass) && (!password.equals(""))) { //if the password is correct
                openMainMenu();
            } else { //If the password isn't correct
                TextIL_password.setError(getString(R.string.wrong_pass_error)); //Shows an error message indicating that the password isn't correct
            }
        } else { //If the username isn't correct
            TextIL_username.setError(getString(R.string.wrong_username_error)); //Shows an error message indicating that the username isn't correct
        }
    }

    /**
     * Method used to open the Main Menu.
     */
    private void openMainMenu() {
        Intent intent = new Intent(this, MainMenuActivity.class); //Prepares a new activity
        startActivity(intent); //Opens the new activity
    }

    /**
     * Method used to open the CreateAccount Menu.
     */
    private void openCreateAccountMenu() {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
    }

    /**
     * Method uses to open an Alert Dialog when back button is pressed
     */
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.exit))
                .setMessage(getString(R.string.exit_msg))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.popup_message_choice_yes, (arg0, arg1) -> {
                    setResult(RESULT_OK, new Intent().putExtra("EXIT", true));
                    finishAffinity();
                }).create().show();
    }



    public void setBackground () {
        Calendar c = Calendar.getInstance();

        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 6 && timeOfDay < 10) {
            //morning
            constraintLayout.setBackground(getDrawable(R.drawable.good_morning_img));
            tvTimeMsg.setText(R.string.good_morning);
            tvTimeMsg.setTextColor(getResources().getColor(R.color.grey_200));
        } else if (timeOfDay >= 10 && timeOfDay < 20) {
            // afternoon
            constraintLayout.setBackground(getDrawable(R.drawable.good_day_img));
            tvTimeMsg.setText(R.string.good_day);
            tvTimeMsg.setTextColor(getResources().getColor(R.color.grey_800));
        } else {
            //night
            constraintLayout.setBackground(getDrawable(R.drawable.good_night_img));
            tvTimeMsg.setText(R.string.good_night);
            tvTimeMsg.setTextColor(getResources().getColor(R.color.grey_200));
        }
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}