package com.flom.mobilecomputingproject.database;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flom.mobilecomputingproject.R;
import com.flom.mobilecomputingproject.model.Reminder;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Reminder> dataholder;     //array list to hold the reminders

    public RecyclerViewAdapter(Context context, ArrayList<Reminder> dataholder) {
        this.context = context;
        this.dataholder = dataholder;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.reminder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.message.setText(dataholder.get(position).getMessage());     //Binds the single reminder objects to recycler view
        holder.reminder_time.setText(dataholder.get(position).getReminder_time());
        holder.creation_time.setText(dataholder.get(position).getCreation_time());
    }

    @Override
    public int getItemCount() {
        return dataholder.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView message, reminder_time, creation_time;

        public ViewHolder(@NonNull View itemview) {
            super(itemview);

            message = (TextView) itemView.findViewById(R.id.txtMessage);   //holds the reference of the materials to show data in recyclerview
            reminder_time = (TextView) itemView.findViewById(R.id.txtReminder_time);
            creation_time = (TextView) itemView.findViewById(R.id.txtCreation_time);
        }
    }
}
