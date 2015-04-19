package com.gymrattrax.scheduler.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;


import com.gymrattrax.scheduler.R;

import java.util.ArrayList;

import static android.view.View.OnClickListener;

public class ListViewAdapterAdd extends ArrayAdapter<String> {

    private ArrayList<String> item = new ArrayList<>();
    private Context context;
    private custButtonListener customListener;

    public ListViewAdapterAdd(Context context, ArrayList<String> workoutItems) {
        super(context, R.layout.add_list_item, workoutItems);
        this.item = workoutItems;
        this.context = context;
    }

    public interface custButtonListener {
        public void onButtonClickListener(int position, String value);
    }

    public void setCustButtonListener(custButtonListener listener) {
        this.customListener = listener;
    }

    //    items in each row in listView
    public class ViewHolder {
        TextView workout_name;
        Button button;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.add_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.workout_name = (TextView) convertView.findViewById(R.id.list_name);
            viewHolder.button = (Button) convertView.findViewById(R.id.addWorkoutToScheduleButton);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final String temp1 = getItem(position);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(temp1);

        viewHolder.workout_name.setText(arrayList.get(0));

        viewHolder.button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customListener != null) {
                    String text = viewHolder.workout_name.getText().toString();
                    customListener.onButtonClickListener(position, text);
                }
            }
        });
        return convertView;
    }
}

