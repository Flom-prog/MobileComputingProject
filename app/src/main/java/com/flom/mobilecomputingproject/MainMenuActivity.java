package com.flom.mobilecomputingproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

public class MainMenuActivity extends AppCompatActivity {

    private FloatingActionButton addReminder;
    private Dialog dialog;

    private RecyclerView recyclerView;

    private ImageButton button_profile; //Button used to launch the "openProfile()" method.

    private final static int EXIT_CODE = 100; // code used to finish this activity

    private Boolean isDateSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        addReminder = findViewById(R.id.floating_action_button);

        recyclerView = findViewById(R.id.recyclerview);

        button_profile = findViewById(R.id.profile_button);

        // Set the animation from here
        /*LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down);
        recyclerView.setLayoutAnimation(layoutAnimationController);*/

        addReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReminder();
            }
        });

        //Set a listener on the Settings button
        button_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile();
            }
        });

        RecyclerViewAdapter adapter = new RecyclerViewAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);
    }



    public void addReminder(){
        dialog = new Dialog(MainMenuActivity.this);
        dialog.setContentView(R.layout.floating_reminder_popup);

        TextView textView = dialog.findViewById(R.id.date);
        ImageButton select = dialog.findViewById(R.id.selectDate);
        Button add = dialog.findViewById(R.id.addButton);
        EditText message = dialog.findViewById(R.id.message);

        isDateSet = false;

        Calendar newCalender = Calendar.getInstance();

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDateSet = true;

                DatePickerDialog dialog = new DatePickerDialog(MainMenuActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        Calendar newTime = Calendar.getInstance();

                        TimePickerDialog time = new TimePickerDialog(MainMenuActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                newDate.set(year, month, dayOfMonth, hourOfDay, minute,0);

                                Calendar tem = Calendar.getInstance();

                                if (newDate.getTimeInMillis() - tem.getTimeInMillis() > 0) {
                                    textView.setError(null);
                                    textView.setTextColor(Color.BLACK);
                                    textView.setText(dayOfMonth + " / " + (month + 1) + " / " + year + "\n" + hourOfDay + ":" + minute);
                                }
                                else Toast.makeText(MainMenuActivity.this, R.string.invalid_time, Toast.LENGTH_SHORT).show();
                            }
                        }, newTime.get(Calendar.HOUR_OF_DAY), newTime.get(Calendar.MINUTE),true);

                        time.show();
                    }
                }, newCalender.get(Calendar.YEAR), newCalender.get(Calendar.MONTH),newCalender.get(Calendar.DAY_OF_MONTH));

                dialog.getDatePicker().setMinDate(System.currentTimeMillis());
                dialog.show();
            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isDateSet) {
                    textView.setError("");
                    textView.setTextColor(Color.RED);
                    textView.setText(R.string.add_date_before_validate);
                }
                else Toast.makeText(MainMenuActivity.this, R.string.reminder_added, Toast.LENGTH_SHORT).show();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }




    /**
     * Method used to launch the openLogInMenu() method when the back button is pressed
     */
    @Override
    public void onBackPressed() {
        openLogInMenu();
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
                finish();
            }
        });
        alerte.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alerte.show();
    }

    /**
     * Method used to open the Settings page.
     */
    private void openProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    /**
     * Method used to finish the MainMenu Activity and disabled the back pressed button
     *
     * @param requestCode the request code of this activity
     * @param resultCode  the result of the Login Activity to finish the MainMenu Activity
     * @param data        the boolean to finish the MainMenu Activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EXIT_CODE) {
            if (resultCode == RESULT_OK) {
                if (data.getBooleanExtra("EXIT", true)) {
                    finish();
                }
            }
        }
    }


    private void runLayoutAnimation(RecyclerView recyclerView) {
        Context context = recyclerView.getContext();

        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);

        recyclerView.setLayoutAnimation(layoutAnimationController);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
}