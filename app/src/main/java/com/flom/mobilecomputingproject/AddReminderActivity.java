package com.flom.mobilecomputingproject;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.flom.mobilecomputingproject.database.DatabaseManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AddReminderActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;
    private EditText messageEditText;
    private ImageView micButton;

    private TextView picturetextview;
    private ImageButton selectPicture;
    private final static int RESULT_LOAD_IMG = 5;
    private Uri mImageUri;

    private TextView datetextview;
    private ImageButton selectDate;

    private Button submit;

    private Boolean isDateSet;

    private String reminder_time_textview;

    private int reminder_id;

    private Switch switch_add_notification, every_day_notification;
    private boolean add_notification, every_day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }

        messageEditText = findViewById(R.id.message);
        micButton = findViewById(R.id.speechtotext);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {
                messageEditText.setText("");
                messageEditText.setHint("Listening...");
            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                micButton.setImageResource(R.drawable.ic_keyboard_voice_white_24);
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                messageEditText.setText(data.get(0));
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        micButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    speechRecognizer.stopListening();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    micButton.setImageResource(R.drawable.ic_keyboard_voice_red_24);
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
                return false;
            }
        });


        preferences = PreferenceManager.getDefaultSharedPreferences(this); //Initializes the SharedPreferences

        picturetextview = findViewById(R.id.picture);
        selectPicture = findViewById(R.id.selectPicture);
        datetextview = findViewById(R.id.date);
        selectDate = findViewById(R.id.selectDate);
        submit = findViewById(R.id.addButton);

        switch_add_notification = findViewById(R.id.switch_add_notification);

        add_notification = true;
        switch_add_notification.setChecked(true);

        switch_add_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                add_notification = isChecked;
            }
        });


        every_day_notification = findViewById(R.id.every_day_notification);

        every_day = false;
        every_day_notification.setChecked(false);

        every_day_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                every_day = isChecked;
            }
        });


        selectPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageSelect();
            }
        });


        isDateSet = false;

        Calendar newCalender = Calendar.getInstance();

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                                    isDateSet = true;
                                    reminder_time_textview = year + "-" + monthModified + "-" + dayOfMonthModified + " " + hourOfDayModified + ":" + minuteModified;
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
                String picture = picturetextview.getText().toString().trim();

                if (!isDateSet) {
                    datetextview.setError("");
                    datetextview.setTextColor(Color.RED);
                    datetextview.setText(R.string.add_date_before_validate);
                } else if (message.isEmpty()) {
                    messageEditText.setError(getString(R.string.add_message_before_validate));
                } else {
                    if (picture.isEmpty()) picture = "";

                    Calendar currentDate = Calendar.getInstance();

                    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Date date = null;
                    try {
                        date = fmt.parse(reminder_time_textview);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    long timeDiff = date.getTime() - currentDate.getTimeInMillis();

                    String reminder_seen;
                    if (timeDiff <= 0) reminder_seen = "true";
                    else reminder_seen = "false";

                    processinsert(message, picture, reminder_seen);


                    if (every_day) {
                        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(NotificationWorker.class, 24, TimeUnit.HOURS).build();

                        WorkManager.getInstance().enqueue(periodicWorkRequest);
                    } else {
                        Constraints constraints = new Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .build();

                        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class).setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                                .setConstraints(constraints)
                                .setInputData(new Data.Builder()
                                        .putString("IMAGE_URI", String.valueOf(mImageUri))
                                        .putInt("ID", reminder_id)
                                        .putBoolean("SHOW_NOTIFICATION", add_notification)
                                        .build())
                                .addTag(message).build();

                        WorkManager.getInstance().enqueue(oneTimeWorkRequest);
                    }


                    openMainMenu();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0 ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
        }
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

                    picturetextview.setText(String.valueOf(mImageUri));
                }
            }
        }
    }


    private void processinsert(String message, String picture, String reminder_seen) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String creation_time = sdf.format(new Date());

        DatabaseManager myDB = new DatabaseManager(AddReminderActivity.this);
        reminder_id = myDB.addReminder(message, picture, reminder_time_textview, getString(R.string.created_on) + creation_time, reminder_seen);     //inserts the data into sql lite database

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