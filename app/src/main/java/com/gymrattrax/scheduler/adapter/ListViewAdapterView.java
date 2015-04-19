package com.gymrattrax.scheduler.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gymrattrax.scheduler.R;

import java.util.ArrayList;

public class ListViewAdapterView extends ArrayAdapter<String> {

    private ArrayList<String> item = new ArrayList<>();
    private Context context;

    public ListViewAdapterView(Context context, ArrayList<String> workoutItems) {
        super(context, R.layout.edit_list_item, workoutItems);
        this.item = workoutItems;
        this.context = context;
    }

    public interface custButtonListener {
        public void onButtonClickListener(int position, String value);
    }

    //    items in each row in listView
    public class ViewHolder {
        TextView workout_name;
        TextView workout_date;
        TextView workout_details;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.view_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.workout_name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.workout_date = (TextView) convertView.findViewById(R.id.date);
            viewHolder.workout_details = (TextView) convertView.findViewById(R.id.details);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final String nameTemp = getItem(position);
        ArrayList<String> arrayList = new ArrayList<>();
        for (String str: nameTemp.split("!", 3)){
            arrayList.add(str);
        }

        viewHolder.workout_name.setText(arrayList.get(0));
        viewHolder.workout_date.setText(arrayList.get(1));
        viewHolder.workout_details.setText(arrayList.get(2));

        return convertView;
    }
}

