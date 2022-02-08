package com.flom.mobilecomputingproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import android.app.AlertDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.database.Cursor;
        import android.os.Bundle;
        import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
        import android.view.animation.AnimationUtils;
        import android.view.animation.LayoutAnimationController;
        import android.widget.ImageButton;
        import android.widget.TextView;

        import com.flom.mobilecomputingproject.database.DatabaseManager;
        import com.flom.mobilecomputingproject.database.RecyclerViewAdapter;
        import com.flom.mobilecomputingproject.model.Reminder;
        import com.flom.mobilecomputingproject.model.ReminderEnum;
        import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MainMenuActivity extends AppCompatActivity implements ReminderItemTouchHelper.RecyclerItemTouchHelperListener {

    private SharedPreferences preferences;

    private ImageButton button_profile, reminders_trash_button, reminders_settings_button, reminders_order_button;

    private final static int EXIT_CODE = 100; // code used to finish this activity

    private TextView remindersCounter, noReminderHint, reminders_text_view;

    private FloatingActionButton addReminder;
    private RecyclerView recyclerView;
    private ArrayList<Reminder> dataholder;     //Array list to add reminders and display in recyclerview
    private RecyclerViewAdapter adapter;
    private DatabaseManager myDB;

    private ReminderEnum[] reminderEnums;
    private int checkedItemMode;

    private int idOfItemRemoved;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        preferences = PreferenceManager.getDefaultSharedPreferences(this); //Initializes the SharedPreferences

        addReminder = findViewById(R.id.floating_action_button);

        recyclerView = findViewById(R.id.recyclerview);

        button_profile = findViewById(R.id.profile_button);
        reminders_trash_button = findViewById(R.id.reminders_trash_button);
        reminders_settings_button = findViewById(R.id.reminders_settings_button);
        reminders_order_button = findViewById(R.id.reminders_order_button);

        remindersCounter = findViewById(R.id.reminders_counter);
        noReminderHint = findViewById(R.id.no_reminders_hint);
        reminders_text_view = findViewById(R.id.reminders_text_view);

        reminderEnums = ReminderEnum.values();


        addReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddReminder();
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
            String reminder_time = dataholder.get(position).getReminder_time();
            String creation_time = dataholder.get(position).getCreation_time();

            // remove the item from recycler view
            myDB.deleteReminder(idOfItemRemoved);
            reloadAll();

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar.make(recyclerView, "\"" + message + getString(R.string.reminders_was_deleted), Snackbar.LENGTH_LONG);
            snackbar.setAction(getString(R.string.undo), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // undo is selected, restore the deleted item
                    myDB.addReminder(message, reminder_time, creation_time);
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

        Cursor cursor = myDB.readAllReminders(reminderEnums[preferences.getInt("ReminderDisplayMode", 0)].toString(), preferences.getString("ReminderDisplayOrder", "ASC"));

        while (cursor.moveToNext()) {
            Reminder reminder = new Reminder(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
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