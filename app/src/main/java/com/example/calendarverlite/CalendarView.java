package com.example.calendarverlite;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarView extends LinearLayout {
    private ImageButton previous;
    private ImageButton next;
    private TextView currentDate;
    private GridView days;
    private Calendar calendar;
    private Context context;
    private AlertDialog alertDialog;
    private GridAdaptor gridAdaptor;
    private SimpleDateFormat dateFormat  =new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    private ArrayList<Date> dates;

    public CalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.dates = new ArrayList<>();
        this.context = context;
        this.calendar = Calendar.getInstance(Locale.ENGLISH);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calendar, this);
        this.next = view.findViewById(R.id.nextButn);
        this.previous = view.findViewById(R.id.prevButn);
        this.currentDate = view.findViewById(R.id.currentDate);
        this.days = view.findViewById(R.id.gridView);

        SetUpCalendar();

        previous.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, -1);
                SetUpCalendar();
            }
        });

        next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, 1);
                SetUpCalendar();
            }
        });

        this.days.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                View eventView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event, null);
                EditText eventName = eventView.findViewById(R.id.editText);
                TextView eventTime = eventView.findViewById(R.id.eventTime);
                ImageButton setTime = eventView.findViewById(R.id.setTime);
                Button saveEvent = eventView.findViewById(R.id.save);

                setTime.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar1 =  Calendar.getInstance();
                        int minutes = calendar1.get(Calendar.MINUTE);
                        int hours = calendar1.get(Calendar.HOUR_OF_DAY);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(eventView.getContext(), androidx.appcompat.R.style.Theme_AppCompat_Dialog,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        Calendar calendar2 = Calendar.getInstance();
                                        calendar2.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        calendar2.set(Calendar.MINUTE, minute);
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("K:mm a", Locale.ENGLISH);
                                        String time = simpleDateFormat.format(calendar2.getTime());
                                        eventTime.setText(time);
                                    }
                                }, hours, minutes, false);
                        timePickerDialog.show();
                    }
                });

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String channelId = "id";
                    CharSequence channelName = "Channel";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;

                    NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
                    NotificationManager notificationManager = getSystemService(context,NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }

                saveEvent.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "id")
                                .setSmallIcon(R.drawable.baseline_message_24)
                                .setContentTitle(eventName.getText())
                                .setContentText(eventTime.getText())
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(1, builder.build());

                        alertDialog.dismiss();
                    }
                });
                builder.setView(eventView);
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    private void SetUpCalendar() {
        this.dates.clear();
        String currentDate = dateFormat.format(calendar.getTime());
        this.currentDate.setText(currentDate);
        Calendar month = (Calendar) calendar.clone();
        month.set(Calendar.DAY_OF_MONTH,1);
        month.add(Calendar.DAY_OF_MONTH, -(month.get(Calendar.DAY_OF_WEEK)-2));

        while (dates.size() < 42) {
            dates.add(month.getTime());
            month.add(Calendar.DAY_OF_MONTH, 1);
        }

        gridAdaptor = new GridAdaptor(context, dates, calendar);
        days.setAdapter(gridAdaptor);
    }
}
