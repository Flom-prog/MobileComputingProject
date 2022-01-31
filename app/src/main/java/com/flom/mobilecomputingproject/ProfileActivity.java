package com.flom.mobilecomputingproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

public class ProfileActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    private Button saveDataButton, signOutButton, deleteButton;

    private TextView usernameEditText, passwordEditText;

    private View test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        saveDataButton = findViewById(R.id.saveDataButton);
        signOutButton = findViewById(R.id.signOutButton);
        deleteButton = findViewById(R.id.deleteButton);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        test = findViewById(R.id.test);


        preferences = PreferenceManager.getDefaultSharedPreferences(this); //Initializes the SharedPreferences


        usernameEditText.setText(preferences.getString("username", ""));
        passwordEditText.setText(preferences.getString("password", ""));


        saveDataButton.setOnClickListener(view -> {
            saveData();
        });

        // Sign out button
        signOutButton.setOnClickListener(view -> {
            openLogInMenu();
        });

        // Delete button
        deleteButton.setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.popup_message_confirmation_delete_account)
                    .setPositiveButton(R.string.popup_message_choice_yes, (dialogInterface, i) ->
                            deleteAccount()
                    )
                    .setNegativeButton(R.string.popup_message_choice_no, null)
                    .show();

        });
    }

    private void saveData() {
        SharedPreferences.Editor editor = preferences.edit(); //Initializes the SharedPreferences' editor

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        //Saves each field in the SharedPreferences
        if (!username.equals(preferences.getString("username", ""))) editor.putString("username", username);
        if (!password.equals(preferences.getString("password", ""))) editor.putString("password", password);

        editor.apply(); //Applies the changes

        Snackbar.make(test, "Données mise à jour", Snackbar.LENGTH_SHORT).show();
    }

    private void deleteAccount() {
        SharedPreferences.Editor editor = preferences.edit(); //Initializes the SharedPreferences' editor

        //Saves each field in the SharedPreferences
        editor.putString("username", "");
        editor.putString("password", "");

        editor.apply(); //Applies the changes

        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * This method is used to alert the user that he's about to leave the app.
     */
    private void openLogInMenu() {
        AlertDialog alerte = new AlertDialog.Builder(this).create();
        alerte.setTitle(getString(R.string.exit));
        alerte.setMessage("\n"+ getString(R.string.exit_msg));

        alerte.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        alerte.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alerte.show();
    }

    @Override
    public void onBackPressed() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (!username.equals(preferences.getString("username", "")) || !password.equals(preferences.getString("password", ""))) {
            AlertDialog alerte = new AlertDialog.Builder(this).create();
            alerte.setTitle(getString(R.string.exit));
            alerte.setMessage("\nÊtes-vous sûr de vouloir quitter sans avoir enregistré ?");

            alerte.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alerte.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            alerte.show();
        } else super.onBackPressed();
    }
}