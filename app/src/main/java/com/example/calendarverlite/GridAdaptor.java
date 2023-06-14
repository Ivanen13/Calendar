package com.example.calendarverlite;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GridAdaptor extends ArrayAdapter {
    private ArrayList<Date> dates;
    private Calendar currentDate ;
    private LayoutInflater inflater;

    private View view;

    public GridAdaptor(@NonNull Context context, ArrayList<Date> dates, Calendar currentDate ) {
        super(context, R.layout.event);

        this.dates = dates;
        this.currentDate = currentDate;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return dates.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return dates.get(position);
    }

    @Override
    public int getPosition(@Nullable Object item) {
        return dates.indexOf(item);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Date monthDate = dates.get(position);
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(monthDate);
        int dayOfMonth = dateCalendar.get(Calendar.DAY_OF_MONTH);
        int displayMonth = dateCalendar.get(Calendar.MONTH) +1;
        int displayYear = dateCalendar.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH) +1;
        int currentYear = currentDate.get(Calendar.YEAR);

        this.view = convertView;
        if(view == null) {
            this.view = inflater.inflate(R.layout.single_cell, parent, false);
        }

        if(displayMonth == currentMonth && displayYear == currentYear) {
            this.view.setBackgroundColor(getContext().getResources().getColor(R.color.green));
        } else {
            this.view.setBackgroundColor(getContext().getResources().getColor(R.color.grey));
        }

        TextView numberOfDay = view.findViewById(R.id.days);
        numberOfDay.setText(String.valueOf(dayOfMonth));

        return this.view;
    }
}
