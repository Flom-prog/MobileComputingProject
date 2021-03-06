package com.flom.mobilecomputingproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class ProfileActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    private Button saveDataButton, signOutButton, deleteButton;
    private TextView usernameEditText, passwordEditText, total_number_of_reminders;
    private ImageButton profilePicture;

    private View test;

    private Uri mImageUri;


    private final static int RESULT_LOAD_IMG = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        preferences = PreferenceManager.getDefaultSharedPreferences(this); //Initializes the SharedPreferences

        profilePicture = findViewById(R.id.profilePicture);
        saveDataButton = findViewById(R.id.saveDataButton);
        signOutButton = findViewById(R.id.signOutButton);
        deleteButton = findViewById(R.id.deleteButton);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        total_number_of_reminders = findViewById(R.id.total_number_of_reminders);

        test = findViewById(R.id.test);


        preferences = PreferenceManager.getDefaultSharedPreferences(this); //Initializes the SharedPreferences

        String mImageUri = preferences.getString("image", null);
        if (mImageUri != null) profilePicture.setImageURI(Uri.parse(mImageUri));

        usernameEditText.setText(preferences.getString("username", ""));
        passwordEditText.setText(preferences.getString("password", ""));


        profilePicture.setOnClickListener(view -> {
            imageSelect();
        });

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

        total_number_of_reminders.setText(String.valueOf(preferences.getInt("TotalNumberOfReminders", 0)));
    }

    private void saveData() {

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (username.length() > 0 && username.length() <= 12) { //if the username is correct
            SharedPreferences.Editor editor = preferences.edit(); //Initializes the SharedPreferences' editor

            //Saves each field in the SharedPreferences
            if (!username.equals(preferences.getString("username", "")) || !password.equals(preferences.getString("password", ""))) {
                editor.putString("username", username);
                editor.putString("password", password);

                editor.apply(); //Applies the changes

                Snackbar.make(test, R.string.data_updated, Snackbar.LENGTH_SHORT).show();
            } else Snackbar.make(test, R.string.no_data_to_update, Snackbar.LENGTH_SHORT).show();
        }
        else { //If the username isn't correct
            if (username.length() < 1) usernameEditText.setError(getString(R.string.create_acc_missing_username)); //Shows an error message indicating that the username isn't correct
            if (username.length() > 12) usernameEditText.setError(getString(R.string.create_acc_too_long_username)); //Shows an error message indicating that the username isn't correct
        }
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
        alerte.setMessage(getString(R.string.return_to_loginmenu));

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
            alerte.setMessage(getString(R.string.sure_exit_before_save));

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


    private void imageSelect() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == RESULT_LOAD_IMG) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a image.
                // The Intent's data Uri identifies which item was selected.
                if (data != null) {

                    // This is the key line item, URI specifies the name of the data
                    mImageUri = data.getData();

                    // Removes Uri Permission so that when you restart the device, it will be allowed to reload.
                    this.grantUriPermission(this.getPackageName(), mImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                    this.getContentResolver().takePersistableUriPermission(mImageUri, takeFlags);

                    // Saves image URI as string to Default Shared Preferences
                    SharedPreferences preferences =
                            PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("image", String.valueOf(mImageUri));
                    editor.apply();

                    // Sets the ImageView with the Image URI
                    profilePicture.setImageURI(mImageUri);
                    profilePicture.invalidate();
                }
            }
        }
    }
}