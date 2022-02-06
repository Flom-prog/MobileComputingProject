package com.flom.mobilecomputingproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.flom.mobilecomputingproject.database.DatabaseManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddReminderActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    private TextView datetextview;
    private ImageButton selectDate;

    private EditText messageEditText;
    private Button submit;

    private Boolean isDateSet;

    private String reminder_time_textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        preferences = PreferenceManager.getDefaultSharedPreferences(this); //Initializes the SharedPreferences

        datetextview = findViewById(R.id.date);
        selectDate = findViewById(R.id.selectDate);
        messageEditText = findViewById(R.id.message);
        submit = findViewById(R.id.addButton);

        isDateSet = false;

        Calendar newCalender = Calendar.getInstance();

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDateSet = true;

                DatePickerDialog dialog = new DatePickerDialog(AddReminderActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        Calendar newTime = Calendar.getInstance();

                        TimePickerDialog time = new TimePickerDialog(AddReminderActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                newDate.set(year, month, dayOfMonth, hourOfDay, minute,0);

                                Calendar tem = Calendar.getInstance();

                                if (newDate.getTimeInMillis() - tem.getTimeInMillis() > 0) {
                                    datetextview.setError(null);
                                    datetextview.setTextColor(Color.BLACK);

                                    int newMonth = month + 1;

                                    String dayOfMonthModified, monthModified, hourOfDayModified, minuteModified;

                                    if (dayOfMonth < 10) {
                                        dayOfMonthModified = "0" + dayOfMonth;
                                    } else dayOfMonthModified = String.valueOf(dayOfMonth);
                                    if (newMonth < 10) {
                                        monthModified = "0" + newMonth;
                                    } else monthModified = String.valueOf(newMonth);
                                    if (hourOfDay < 10) {
                                        hourOfDayModified = "0" + hourOfDay;
                                    } else hourOfDayModified = String.valueOf(hourOfDay);
                                    if (minute < 10) {
                                        minuteModified = "0" + minute;
                                    } else minuteModified = String.valueOf(minute);

                                    reminder_time_textview = year + "-" + monthModified + "-" + dayOfMonthModified + " " + hourOfDayModified + ":" + minuteModified + ":00";
                                    datetextview.setText(year + "-" + monthModified + "-" + dayOfMonthModified + "\n" + hourOfDayModified + ":" + minuteModified);
                                }
                                else Toast.makeText(AddReminderActivity.this, R.string.invalid_time, Toast.LENGTH_SHORT).show();
                            }
                        }, newTime.get(Calendar.HOUR_OF_DAY), newTime.get(Calendar.MINUTE),true);

                        time.show();
                    }
                }, newCalender.get(Calendar.YEAR), newCalender.get(Calendar.MONTH),newCalender.get(Calendar.DAY_OF_MONTH));

                dialog.getDatePicker().setMinDate(System.currentTimeMillis());
                dialog.show();
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString().trim();

                if (!isDateSet) {
                    datetextview.setError("");
                    datetextview.setTextColor(Color.RED);
                    datetextview.setText(R.string.add_date_before_validate);
                } else if (message.isEmpty()) {
                    messageEditText.setError(getString(R.string.add_message_before_validate));
                } else {
                    processinsert(message);
                    openMainMenu();
                }
            }
        });
    }

    private void processinsert(String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String creation_time = sdf.format(new Date());

        DatabaseManager myDB = new DatabaseManager(AddReminderActivity.this);
        myDB.addReminder(message, reminder_time_textview, creation_time);     //inserts the data into sql lite database

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("TotalNumberOfReminders", preferences.getInt("TotalNumberOfReminders", 0) + 1);
        editor.apply(); //Applies the changes
    }

    /**
     * Method used to return to the Main menu page.
     */
    private void openMainMenu() {
        Intent intent = new Intent(AddReminderActivity.this, MainMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}