package com.example.inrapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class DoceCalciFragment extends Fragment {

    public DoceCalciFragment() {
        // Required empty public constructor
    }

    TextView submit;
    EditText currentDose, currentInr;
    RadioGroup targetInr, bleedingInfo;
    DatabaseReference databaseReference;
    FirebaseAuth mauth;

    AlertDialog.Builder builder;
    String recommendation = "";
    DatabaseReference mref;RadioButton ble;
    RadioGroup.OnCheckedChangeListener bleedingInfo_listen;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_doce_calci, container, false);
        submit = (TextView)view.findViewById(R.id.submit);
        currentDose = (EditText)view.findViewById(R.id.dosage);
        currentInr = (EditText)view.findViewById(R.id.currentInr);
        targetInr = (RadioGroup)view.findViewById(R.id.target_inr);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.target_inr, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner

        bleedingInfo = (RadioGroup)view.findViewById(R.id.bleedingInfo);
        mauth = FirebaseAuth.getInstance();
        builder = new AlertDialog.Builder(getActivity());
        mref = FirebaseDatabase.getInstance().getReference().child(Objects.requireNonNull(mauth.getUid())).child("Info");

        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentInr.setText(Objects.requireNonNull(snapshot.child("Current INR").getValue()).toString());
                currentDose.setText(Objects.requireNonNull(snapshot.child("Warfin Dosage").getValue()).toString());
                String isBle = Objects.requireNonNull(snapshot.child("Is Bleeding").getValue()).toString();
                String targetinr_value = Objects.requireNonNull(snapshot.child("Target INR").getValue()).toString();
                if(isBle.trim().equals("No Bleeding")) {
                    bleedingInfo.setOnCheckedChangeListener(null);
                    bleedingInfo.check(R.id.bleeding);
                    bleedingInfo.setOnCheckedChangeListener(bleedingInfo_listen);
                }
                else if(isBle.trim().equals("Serious Bleeding")) {
                    bleedingInfo.setOnCheckedChangeListener(null);
                    bleedingInfo.check(R.id.seriousBleeding);
                    bleedingInfo.setOnCheckedChangeListener(bleedingInfo_listen);
                }
                if(targetinr_value.trim().equals("2.0-3.0")) {

                    targetInr.check(R.id.op1);

                }
                else if(targetinr_value.trim().equals("2.5-3.5")) {

                    targetInr.check(R.id.op2);

                }
                recommendation = Objects.requireNonNull(snapshot.child("recommendation").getValue()).toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int target_inr = targetInr.getCheckedRadioButtonId();
                RadioButton t_inr = (RadioButton) view.findViewById(target_inr);
                String targetInrSelection = t_inr.getText().toString();

                databaseReference = FirebaseDatabase.getInstance().getReference().child(mauth.getCurrentUser().getUid().toString()).child("Info");

                databaseReference.child("Target INR").setValue(targetInrSelection);
                databaseReference.child("Current INR").setValue(currentInr.getText().toString());
                databaseReference.child("Warfin Dosage").setValue(currentDose.getText().toString());

                int bleedingSelection = bleedingInfo.getCheckedRadioButtonId();
                ble = (RadioButton) view.findViewById(bleedingSelection);
                databaseReference.child("Is Bleeding").setValue(ble.getText());
                String c_inr = currentInr.getText().toString();
                float currentInrValue = Float.parseFloat(c_inr);
                recommendation = getRecommendation(currentInrValue, targetInr);
                databaseReference.child("recommendation").setValue(recommendation).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Data Successfully Updated", Toast.LENGTH_SHORT).show();
                        builder.setMessage(recommendation)
                                .setCancelable(false)
                                .setPositiveButton("Noted.", new DialogInterface.OnClickListener() {
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

            }
        });

        bleedingInfo_listen = new RadioGroup.OnCheckedChangeListener()
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
        };



        return view;
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
