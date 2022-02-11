package com.flom.mobilecomputingproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.flom.mobilecomputingproject.database.DatabaseManager;
import com.flom.mobilecomputingproject.model.Reminder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReminderEditorActivity extends AppCompatActivity {

    private TextView datetextview;
    private ImageButton selectDate;

    private EditText messageEditText;
    private Button submit;

    private int reminder_id;
    private String reminder_time_textview;

    private DatabaseManager myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_editor);

        datetextview = findViewById(R.id.date);
        selectDate = findViewById(R.id.selectDate);
        messageEditText = findViewById(R.id.message);
        submit = findViewById(R.id.addButton);

        myDB = new DatabaseManager(ReminderEditorActivity.this);

        Cursor cursor = myDB.readOneReminder(getIntent().getIntExtra("reminder_edit", -1));
        cursor.moveToFirst();

        reminder_id = cursor.getInt(0);

        messageEditText.setText(cursor.getString(1));
        datetextview.setText(cursor.getString(2));


        Calendar newCalender = Calendar.getInstance();

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(ReminderEditorActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        Calendar newTime = Calendar.getInstance();

                        TimePickerDialog time = new TimePickerDialog(ReminderEditorActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                                else Toast.makeText(ReminderEditorActivity.this, R.string.invalid_time, Toast.LENGTH_SHORT).show();
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

                if (message.isEmpty()) {
                    messageEditText.setError(getString(R.string.add_message_before_validate));
                } else {
                    processupdate(message);
                    openMainMenu();
                }
            }
        });
    }

    private void processupdate(String message) {
        myDB.updateReminder(reminder_id, message, reminder_time_textview);     //inserts the data into sql lite database
    }

    /**
     * Method used to return to the Main menu page.
     */
    private void openMainMenu() {
        Intent intent = new Intent(ReminderEditorActivity.this, MainMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}