package com.example.inrapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {

    public Home() {
        // Required empty public constructor
    }

    private TextView time,treatment_range, warfarin_indication,recommendation;
    private DatabaseReference mref;
    private FirebaseAuth mauth;
    EditText timeSelection;
    String is_reminded;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        time = view.findViewById(R.id.time);
        treatment_range = view.findViewById(R.id.treatment_range);
        warfarin_indication = view.findViewById(R.id.warfarin_indication);
        recommendation = view.findViewById(R.id.recommendation);
        timeSelection = view.findViewById(R.id.timeSelection);
        mauth = FirebaseAuth.getInstance();
        mref = FirebaseDatabase.getInstance().getReference().child(Objects.requireNonNull(mauth.getUid())).child("Info");
        timeSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Time
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        time.setText( selectedHour + ":" + selectedMinute);



                        Calendar calNow = Calendar.getInstance();
                        Calendar calSet = (Calendar) calNow.clone();

                        calSet.set(Calendar.HOUR_OF_DAY, selectedHour);
                        calSet.set(Calendar.MINUTE, selectedMinute);
                        calSet.set(Calendar.SECOND, 0);
                        calSet.set(Calendar.MILLISECOND, 0);

                        if (calSet.compareTo(calNow) <= 0) {
                            // Today Set time passed, count to tomorrow
                            calSet.add(Calendar.DATE, 1);
                        }

                        if(is_reminded.trim().contentEquals("Yes")){
                            Toast.makeText(getActivity(), "Remainder Updated !!!!", Toast.LENGTH_SHORT).show();
                            cancelAlarm();
                            setAlarm(calSet);
                        }

                        mref.child("Warfine Dosage Time").setValue(time.getText().toString());

                        Toast.makeText(getActivity(), "Warfine Dosage Time Updated Successfully !!!!", Toast.LENGTH_SHORT).show();
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });


        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                is_reminded = snapshot.child("To_be_reminded").getValue().toString();
                treatment_range.setText(Objects.requireNonNull(snapshot.child("Target INR").getValue()).toString());
                warfarin_indication.setText(Objects.requireNonNull(snapshot.child("Warfarin Indication").getValue()).toString());
                recommendation.setText(Objects.requireNonNull(snapshot.child("recommendation").getValue()).toString());
                time.setText(Objects.requireNonNull(snapshot.child("Warfine Dosage Time").getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return view;
    }

    void cancelAlarm(){
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(getActivity().getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getActivity().getApplicationContext(), 1, myIntent, 0);

        alarmManager.cancel(pendingIntent);
    }

    private void setAlarm(Calendar targetCal) {



        Intent intent = new Intent(getActivity().getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getActivity().getApplicationContext(), 1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(),
                pendingIntent);

    }
}
