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

public class ListViewAdapterAddNegation extends ArrayAdapter<String> {

    private Context context;
    private custButtonListener customListener;

    public ListViewAdapterAddNegation(Context context, ArrayList<String> workoutItems) {
        super(context, R.layout.calorie_list_item, workoutItems);
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
        TextView workout_details;
        Button button;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.calorie_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.workout_name = (TextView) convertView.findViewById(R.id.calorie_list_name);
            viewHolder.workout_details = (TextView) convertView.findViewById(R.id.calorie_list_details);
            viewHolder.button = (Button) convertView.findViewById(R.id.addCalorieNegationButton);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        final String nameTemp = getItem(position);
        ArrayList<String> arrayList = new ArrayList<>();
        for (String str: nameTemp.split(":", 2)){
            arrayList.add(str);
        }
        viewHolder.workout_name.setText(arrayList.get(0));
        viewHolder.workout_details.setText(arrayList.get(1));

        viewHolder.button.setOnClickListener(new View.OnClickListener() {
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