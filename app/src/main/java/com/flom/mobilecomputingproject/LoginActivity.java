package com.flom.mobilecomputingproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Calendar;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    private ConstraintLayout constraintLayout;
    private TextView tvTimeMsg;

    private Button button_login; // Button used to launch the "openMainMenu()" method.
    private Button button_createAccount; // Button used to launch the "openCreateAccountMenu()" method.
    private EditText TextIL_username; // Button used to launch the "openMainMenu()" method.
    private EditText TextIL_password; // Button used to launch the "openMainMenu()" method.
    private Switch switch_autofill;

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

        constraintLayout = findViewById(R.id.containerlogin);
        tvTimeMsg = findViewById(R.id.tv_time_login_msg);

        setBackground();

        button_login = findViewById(R.id.btn_login_signin);
        button_createAccount = findViewById(R.id.btn_login_signup);
        TextIL_username = findViewById(R.id.ET_login_username);
        TextIL_password = findViewById(R.id.ET_login_password);
        switch_autofill = findViewById(R.id.switch_autofill);

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
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.yes, (arg0, arg1) -> {
                    setResult(RESULT_OK, new Intent().putExtra("EXIT", true));
                    finish();
                }).create().show();
    }



    public void setBackground () {
        Calendar c = Calendar.getInstance();

        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 6 && timeOfDay < 10) {
            //morning
            constraintLayout.setBackground(getDrawable(R.drawable.good_morning_img));
            tvTimeMsg.setText("Good Morning !");
            tvTimeMsg.setTextColor(getResources().getColor(R.color.white));
        } else if (timeOfDay >= 10 && timeOfDay < 18) {
            // afternoon
            constraintLayout.setBackground(getDrawable(R.drawable.good_day_img));
            tvTimeMsg.setText("Good Day !");
            tvTimeMsg.setTextColor(getResources().getColor(R.color.black));
        } else {
            //night
            constraintLayout.setBackground(getDrawable(R.drawable.good_night_img));
            tvTimeMsg.setText("Good Night !");
            tvTimeMsg.setTextColor(getResources().getColor(R.color.white));
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