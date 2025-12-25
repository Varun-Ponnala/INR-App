package com.example.inrapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class WelcomeActivity extends AppCompatActivity {
    TextView submit;
    EditText time;
    RadioGroup remind;
    DatabaseReference mref2;
    FirebaseAuth mauth;
    private RadioButton remainderInfo;
    private int mHour, mMinute;
    Spinner dropDown;
    Calendar calSet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        submit = (TextView)findViewById(R.id.submit);
        time = (EditText) findViewById(R.id.date);
        remind = (RadioGroup) findViewById(R.id.remind);
        mauth = FirebaseAuth.getInstance();

        dropDown = (Spinner) findViewById(R.id.indication);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.indication, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        dropDown.setAdapter(adapter);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Time
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(WelcomeActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        time.setText( selectedHour + ":" + selectedMinute);
                        Calendar calNow = Calendar.getInstance();
                        calSet = (Calendar) calNow.clone();

                        calSet.set(Calendar.HOUR_OF_DAY, selectedHour);
                        calSet.set(Calendar.MINUTE, selectedMinute);
                        calSet.set(Calendar.SECOND, 0);
                        calSet.set(Calendar.MILLISECOND, 0);

                        if (calSet.compareTo(calNow) <= 0) {
                            // Today Set time passed, count to tomorrow
                            calSet.add(Calendar.DATE, 1);
                        }


                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = remind.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                remainderInfo = (RadioButton) findViewById(selectedId);
                String is_reminded = remainderInfo.getText().toString();
                if(is_reminded.trim().contentEquals("Yes")){
                    setAlarm(calSet);
                }
                mref2 = FirebaseDatabase.getInstance().getReference().child(mauth.getCurrentUser().getUid().toString()).child("Info");
                mref2.child("Warfine Dosage Time").setValue(time.getText().toString());
                mref2.child("To_be_reminded").setValue(is_reminded);
                mref2.child("Warfarin Indication").setValue(dropDown.getSelectedItem().toString());

                Intent i = new Intent(WelcomeActivity.this, InfoActivity.class);
                startActivity(i);
                finish();
            }
        });





    }
    private void setAlarm(Calendar targetCal) {



        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getBaseContext(), 1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(),
                pendingIntent);

    }


}
