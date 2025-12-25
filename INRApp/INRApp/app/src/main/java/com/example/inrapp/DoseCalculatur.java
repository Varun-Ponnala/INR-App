package com.example.inrapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DoseCalculatur extends AppCompatActivity {
    TextView submit;
    EditText currentDose, currentInr;
    RadioGroup targetInr, bleedingInfo;
    DatabaseReference databaseReference;
    FirebaseAuth mauth;


    AlertDialog.Builder builder;
    String recommendation = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dose_calculatur);
        submit = (TextView)findViewById(R.id.submit);
        currentDose = (EditText)findViewById(R.id.dosage);
        currentInr = (EditText)findViewById(R.id.currentInr);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.target_inr, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner

        bleedingInfo = (RadioGroup)findViewById(R.id.bleedingInfo);
        targetInr = (RadioGroup)findViewById(R.id.target_inr);
        mauth = FirebaseAuth.getInstance();
        builder = new AlertDialog.Builder(this);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int target_inr = targetInr.getCheckedRadioButtonId();
                RadioButton t_inr = (RadioButton) findViewById(target_inr);
                String targetInrSelection = t_inr.getText().toString();
                String c_inr = currentInr.getText().toString();
                int bleedingSelection = bleedingInfo.getCheckedRadioButtonId();
                RadioButton ble = (RadioButton) findViewById(bleedingSelection);
                databaseReference = FirebaseDatabase.getInstance().getReference().child(mauth.getCurrentUser().getUid().toString()).child("Info");

                float currentInrValue = Float.parseFloat(c_inr);

                recommendation = getRecommendation(currentInrValue, targetInr);

                databaseReference.child("Target INR").setValue(targetInrSelection);
                databaseReference.child("Current INR").setValue(c_inr);
                databaseReference.child("Warfin Dosage").setValue(currentDose.getText().toString());
                databaseReference.child("Is Bleeding").setValue(ble.getText());
                databaseReference.child("recommendation").setValue(recommendation);
                builder.setMessage(recommendation)
                        .setCancelable(false)
                        .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent i = new Intent(DoseCalculatur.this, MainActivity.class);
                                startActivity(i);
                                finish();

                            }
                        })
                        .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Recommendation");
                alert.show();


            }
        });

        bleedingInfo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                if(checkedId == R.id.seriousBleeding){
                    recommendation = "• Stop warfarin;\n" +
                            "• Reverse the anticoagulant effect with PCC (prothrombin complex concentrate) rather than with fresh frozen plasma;\n" +
                            "• You may also use 5–10 mg of vitamin K1 in a slow IV injection.";
                    builder.setMessage(recommendation)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle("Alert!!!!");
                    alert.show();
                }
            }
        });



    }

    String getRecommendation(float currentInrValue,RadioGroup targetInr){
        String recommendation = "";
        if ((currentInrValue> 0 && currentInrValue <= 2 && targetInr.getCheckedRadioButtonId() == R.id.op2||
                (currentInrValue> 0 && currentInrValue <= 1.5 && targetInr.getCheckedRadioButtonId() == R.id.op1))) {


            recommendation = "Consider increasing maintenance dose by 5–20%.\n" +
                    "Consider a single booster of 1.5–2× the daily maintenance dose.\n" +
                    "Schedule the next appointment in 3–7 days.\n" +
                    "\n";

        }
        else if ((currentInrValue > 2 && currentInrValue <= 2.3 && targetInr.getCheckedRadioButtonId() == R.id.op2||
                (currentInrValue > 1.5 && currentInrValue <= 1.7 && targetInr.getCheckedRadioButtonId() == R.id.op1))) {
            recommendation = "Consider increasing the maintenance dose by 5–15%.\n" +
                    "Consider a single booster of 1.5–2× the daily maintenance dose.\n" +
                    "Schedule the next appointment in 3–7 days.\n" +
                    "\n";

        }
        else if ((currentInrValue > 2.3 && currentInrValue <= 2.4 && targetInr.getCheckedRadioButtonId() == R.id.op2||
                (currentInrValue > 1.7 && currentInrValue <= 1.9 && targetInr.getCheckedRadioButtonId() == R.id.op1))) {
            recommendation = "If the two previous INRs were in range, you might consider not making any adjustments to the dose.\n" +
                    "Consider increasing the maintenance dose by 5–10%.\n" +
                    "Consider a single booster of 1.5–2× the daily maintenance dose.\n" +
                    "Schedule the next appointment in 3–7 days. \n" +
                    "\n";

        }
        else if ((currentInrValue > 2.4 && currentInrValue <= 3.5 && targetInr.getCheckedRadioButtonId() == R.id.op2||
                (currentInrValue > 1.9 && currentInrValue <= 3.0 && targetInr.getCheckedRadioButtonId() == R.id.op1))) {
            recommendation = "No Recommendation, Desired range.";

        }
        else if ((currentInrValue > 3.5 && currentInrValue <= 3.7 && targetInr.getCheckedRadioButtonId() == R.id.op2||
                (currentInrValue > 3.0 && currentInrValue <= 3.2 && targetInr.getCheckedRadioButtonId() == R.id.op1))) {
            recommendation = "If the two previous INRs were in range, you might consider not making any adjustments to the dose.\n" +
                    "Consider omitting one dose or decreasing maintenance dose by 5–10%.\n" +
                    "Schedule the next appointment in 3–7 days. \n" +
                    "\n";

        }
        else if ((currentInrValue > 3.7 && currentInrValue <= 4.4 && targetInr.getCheckedRadioButtonId() == R.id.op2||
                (currentInrValue > 3.2 && currentInrValue <= 3.9 && targetInr.getCheckedRadioButtonId() == R.id.op1))) {
            recommendation = "Consider omitting one dose or decreasing maintenance dose by 5–15%.\n" +
                    "Schedule the next appointment in 1–3 days.\n" +
                    "\n";

        }
        else if ((currentInrValue >= 4.5 && targetInr.getCheckedRadioButtonId() == R.id.op2||
                currentInrValue >= 4.0 && targetInr.getCheckedRadioButtonId() == R.id.op1)) {
            recommendation = "Hold warfarin or decrease maintenance dose by 5–20%.\n" +
                    "Schedule the next appointment in 1 day.\n";


        }
        return recommendation;
    }



}
