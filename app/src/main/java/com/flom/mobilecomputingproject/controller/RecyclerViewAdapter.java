package com.flom.mobilecomputingproject.controller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flom.mobilecomputingproject.AddReminderActivity;
import com.flom.mobilecomputingproject.R;
import com.flom.mobilecomputingproject.ReminderEditorActivity;
import com.flom.mobilecomputingproject.model.Reminder;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Reminder> dataholder;     //array list to hold the reminders

    public RecyclerViewAdapter(Context context, ArrayList<Reminder> dataholder) {
        this.context = context;
        this.dataholder = dataholder;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.reminder, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtReminderId.setText(String.valueOf(dataholder.get(position).getReminder_id()));

        holder.message.setText(dataholder.get(position).getMessage());     //Binds the single reminder objects to recycler view
        holder.picture.setImageURI(Uri.parse(dataholder.get(position).getImage_reminder()));
        holder.reminder_time.setText(dataholder.get(position).getReminder_time());
        holder.creation_time.setText(dataholder.get(position).getCreation_time());
        holder.reminder_seen.setText(dataholder.get(position).getReminder_seen());

        if (dataholder.get(position).getReminder_seen().equals("false")) holder.reminder_time.setTextColor(holder.itemView.getResources().getColor(R.color.red));

        holder.reminder_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ReminderEditorActivity.class);
                intent.putExtra("reminder_edit", Integer.valueOf((String) holder.txtReminderId.getText()));
                context.startActivity(intent);
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return dataholder.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {

        public View reminder_layout, viewBackground;
        private TextView txtReminderId, message, reminder_time, creation_time, reminder_seen;
        private ImageView picture;

        public ViewHolder(@NonNull View itemview) {
            super(itemview);

            reminder_layout = itemview.findViewById(R.id.reminder_layout);
            viewBackground = itemView.findViewById(R.id.view_background);

            txtReminderId = itemview.findViewById(R.id.txtReminderId);
            message = itemview.findViewById(R.id.txtMessage);   //holds the reference of the materials to show data in recyclerview
            picture = itemview.findViewById(R.id.imageReminder);
            reminder_time = itemview.findViewById(R.id.txtReminder_time);
            creation_time = itemview.findViewById(R.id.txtCreation_time);
            reminder_seen = itemview.findViewById(R.id.reminder_seen);
        }
    }
}
