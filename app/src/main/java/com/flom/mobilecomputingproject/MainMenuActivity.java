package com.flom.mobilecomputingproject;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flom.mobilecomputingproject.controller.RecyclerViewAdapter;
import com.flom.mobilecomputingproject.controller.ReminderItemTouchHelper;
import com.flom.mobilecomputingproject.database.DatabaseManager;
import com.flom.mobilecomputingproject.model.Reminder;
import com.flom.mobilecomputingproject.model.ReminderEnum;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainMenuActivity extends AppCompatActivity implements ReminderItemTouchHelper.RecyclerItemTouchHelperListener {

    private SharedPreferences preferences;

    private ImageButton button_profile, reminders_trash_button, reminders_settings_button, reminders_order_button;

    private final static int EXIT_CODE = 100; // code used to finish this activity

    private TextView remindersCounter, noReminderHint, reminders_text_view;

    private FloatingActionButton addReminder, maps_button;
    private RecyclerView recyclerView;
    private ArrayList<Reminder> dataholder;     //Array list to add reminders and display in recyclerview
    private RecyclerViewAdapter adapter;
    private DatabaseManager myDB;

    private ReminderEnum[] reminderEnums;
    private int checkedItemMode;

    private int idOfItemRemoved;

    private String reminder_seen;

    private Switch switch_show_all_reminders;
    private boolean see_all_reminders;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        preferences = PreferenceManager.getDefaultSharedPreferences(this); //Initializes the SharedPreferences

        addReminder = findViewById(R.id.floating_action_button);
        maps_button = findViewById(R.id.floating_maps_button);

        recyclerView = findViewById(R.id.recyclerview);

        button_profile = findViewById(R.id.profile_button);
        reminders_trash_button = findViewById(R.id.reminders_trash_button);
        reminders_settings_button = findViewById(R.id.reminders_settings_button);
        reminders_order_button = findViewById(R.id.reminders_order_button);

        remindersCounter = findViewById(R.id.reminders_counter);
        noReminderHint = findViewById(R.id.no_reminders_hint);
        reminders_text_view = findViewById(R.id.reminders_text_view);


        switch_show_all_reminders = findViewById(R.id.switch_show_all_reminders);
        see_all_reminders = false;

        switch_show_all_reminders.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                see_all_reminders = isChecked;
                reloadAll();
            }
        });


        reminderEnums = ReminderEnum.values();


        addReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddReminder();
            }
        });

        maps_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMapsActivity();
            }
        });

        //Set a listener on the Settings button
        button_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile();
            }
        });

        reminders_trash_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteAllAlert();
            }
        });

        reminders_settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showReminderDisplayModesSelector();
            }
        });

        if (preferences.getString("ReminderDisplayOrder", "ASC").equals("ASC")) reminders_order_button.setImageDrawable(getDrawable(R.drawable.ic_asc_24));
        else reminders_order_button.setImageDrawable(getDrawable(R.drawable.ic_desc_24));

        reminders_order_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (preferences.getString("ReminderDisplayOrder", "ASC").equals("ASC")) setReminderDisplayOrder("DESC");
                else setReminderDisplayOrder("ASC");
            }
        });


        myDB = new DatabaseManager(MainMenuActivity.this);
        dataholder = new ArrayList<>();

        //Swipe
        ReminderItemTouchHelper noteItemTouchHelper = new ReminderItemTouchHelper(0, ItemTouchHelper.LEFT, this);

        //attaching the touch helper to recycler view
        new ItemTouchHelper(noteItemTouchHelper).attachToRecyclerView(recyclerView);

        reloadAll();
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        if (viewHolder instanceof RecyclerViewAdapter.ViewHolder) {
            // get the removed item name to display it in snack bar
            idOfItemRemoved = dataholder.get(position).getReminder_id();
            String message = dataholder.get(position).getMessage();
            String picture = dataholder.get(position).getImage_reminder();
            String reminder_time = dataholder.get(position).getReminder_time();
            String creation_time = dataholder.get(position).getCreation_time();
            reminder_seen = dataholder.get(position).getReminder_seen();
            double location_x = dataholder.get(position).getLocation_x();
            double location_y = dataholder.get(position).getLocation_y();

            // remove the item from recycler view
            myDB.deleteReminder(idOfItemRemoved);
            NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(idOfItemRemoved);

            reloadAll();

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar.make(recyclerView, "\"" + message + getString(R.string.reminders_was_deleted), Snackbar.LENGTH_LONG);
            snackbar.setAction(getString(R.string.undo), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SimpleDateFormat fmt;
                    if (preferences.getString("language", "fr").equals("fr")) fmt = new SimpleDateFormat("EEEE dd LLLL yyyy - HH:mm", Locale.FRENCH);
                    else fmt = new SimpleDateFormat("EEEE dd LLLL yyyy - HH:mm", Locale.ENGLISH);

                    Date date = null;
                    try {
                        date = fmt.parse(reminder_time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    SimpleDateFormat fmtOut = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String new_reminder_time = fmtOut.format(date);

                    Calendar currentDate = Calendar.getInstance();
                    long timeDiff =  date.getTime() - currentDate.getTimeInMillis();
                    if (timeDiff <= 0) reminder_seen = "true";
                    else reminder_seen = "false";

                    // undo is selected, restore the deleted item
                    myDB.addReminder(message, picture, new_reminder_time, creation_time, reminder_seen, location_x, location_y);
                    reloadAll();
                }
            });
            snackbar.show();
        }
    }


    public void setReminderDisplayOrder(String order) {
        SharedPreferences.Editor editor = preferences.edit(); //Initializes the SharedPreferences' editor
        editor.putString("ReminderDisplayOrder", order);
        editor.apply(); //Applies the changes

        switch (order) {
            case "ASC":
                reminders_order_button.setImageDrawable(getDrawable(R.drawable.ic_asc_24));
                break;
            case "DESC":
                reminders_order_button.setImageDrawable(getDrawable(R.drawable.ic_desc_24));
                break;
        }

        reloadAll();
    }


    private void showDeleteAllAlert() {
        android.app.AlertDialog alerte = new android.app.AlertDialog.Builder(this).create();
        alerte.setTitle(getString(R.string.warning));
        alerte.setMessage(getString(R.string.reminders_delete_all_wrng_msg_1)+"\n\n"+getString(R.string.wrng_continue));

        alerte.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                myDB.deleteAllReminders();
                reloadAll();
            }
        });
        alerte.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alerte.show();
    }


    /**
     * Method used to show the Reminder display modes Selector
     */
    private void showReminderDisplayModesSelector() {
        // setup the alert builder
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.reminders_editor_choose_display_mode));// add a radio button list

        checkedItemMode = preferences.getInt("ReminderDisplayMode", 0);

        String[] ReminderDisplayModesNames = new String[reminderEnums.length];

        for (int i = 0; i < reminderEnums.length; i++) {
            ReminderDisplayModesNames[i] = reminderEnums[i].toString();
        }

        builder.setSingleChoiceItems(ReminderDisplayModesNames, checkedItemMode, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkedItemMode = which;
            }
        });

        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selectedCategory) {
                SharedPreferences.Editor editor = preferences.edit(); //Initializes the SharedPreferences' editor
                editor.putInt("ReminderDisplayMode", checkedItemMode);
                editor.apply(); //Applies the changes

                reloadAll();
            }
        });

        builder.setNegativeButton(getString(R.string.cancel), null);

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }



    public void storeDataInArray() {
        dataholder.clear();

        Cursor cursor = myDB.readAllReminders(reminderEnums[preferences.getInt("ReminderDisplayMode", 0)].toString(), preferences.getString("ReminderDisplayOrder", "ASC"), see_all_reminders);

        while (cursor.moveToNext()) {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = null;
            try {
                if (!cursor.getString(3).isEmpty()) date = fmt.parse(cursor.getString(3));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat fmtOut;
            if (preferences.getString("language", "fr").equals("fr")) fmtOut = new SimpleDateFormat("EEEE dd LLLL yyyy - HH:mm", Locale.FRENCH);
            else fmtOut = new SimpleDateFormat("EEEE dd LLLL yyyy - HH:mm", Locale.ENGLISH);

            String reminder_time;
            if (date != null) reminder_time = fmtOut.format(date);
            else reminder_time = "";

            Reminder reminder = new Reminder(cursor.getInt(0), cursor.getString(1), cursor.getString(2), reminder_time, cursor.getString(4), cursor.getString(5), cursor.getDouble(6), cursor.getDouble(7));
            dataholder.add(reminder);
        }
    }


    private void reloadRecycleView() {
        adapter = new RecyclerViewAdapter(MainMenuActivity.this, dataholder);
        recyclerView.setAdapter(adapter);      //Binds the adapter with recyclerview
        recyclerView.setLayoutManager(new LinearLayoutManager(MainMenuActivity.this));
    }


    private void setupNoteCounter() {
        //Updates the notes_counter TextView with the current amount of notes
        remindersCounter.setText(String.valueOf(dataholder.size()));

        if (dataholder.size() == 1) {
            reminders_text_view.setText(R.string.reminder);
        } else {
            reminders_text_view.setText(R.string.reminders);
        }
    }

    private void setupNoNoteHint() {
        if (dataholder.isEmpty()) noReminderHint.setVisibility(View.VISIBLE);
        else noReminderHint.setVisibility(View.GONE);
    }

    private void reloadAll() {
        storeDataInArray();
        reloadRecycleView();
        setupNoNoteHint();
        setupNoteCounter();

        // Set the animation from here
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down);
        recyclerView.setLayoutAnimation(layoutAnimationController);
        recyclerView.scheduleLayoutAnimation();
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
     * Method used to open the Add Reminder page.
     */
    private void openAddReminder() {
        Intent intent = new Intent(this, AddReminderActivity.class);
        startActivity(intent);
    }

    private void openMapsActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    /**
     * Method used to open the Profile page.
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
}