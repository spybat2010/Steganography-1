package rusk.com.steganography;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Random;

public class GenerateRE extends AppCompatActivity {

    private int mYear, mMonth, mDay, mHour, mMinute;
    private String TAG = "";
    private String userKey = "didnotget";

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_re);


        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            userKey = extras.getString("user_key");
        }

        final TextView reToSend = (TextView) findViewById(R.id.re_to_send);
        final TextView setDate = (TextView) findViewById(R.id.setDate);
        final TextView setTime = (TextView) findViewById(R.id.setTime);
        final TextView generateToken = (TextView) findViewById(R.id.generateToken);
        final Button share = (Button) findViewById(R.id.share);


        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                share.setEnabled(false);

                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(GenerateRE.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                if((hourOfDay <= (c.get(Calendar.HOUR_OF_DAY)))&&
                                        (minute <= (c.get(Calendar.MINUTE)))){
                                    Toast.makeText(GenerateRE.this, "Past time not allowed !",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    setTime.setText(hourOfDay + ":" + minute);
                                }

                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();

            }
        });


        setDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(GenerateRE.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                setDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);


                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();

            }
        });


        Log.d(TAG, "onCreate: setting date and key " + setDate.getText().toString());

        generateToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (setDate.getText().toString().contains("-") && setTime.getText().toString().contains(":")){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            GenerateRE.this);
                    alertDialogBuilder.setTitle("Confirm Validation Time");

                    alertDialogBuilder
                            .setMessage("You will not be able to change validation after generating token !")
                            .setCancelable(false)
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    setDate.setOnClickListener(null);
                                    setTime.setOnClickListener(null);

                                    // Generate random id, for example V8M324
//                                    char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
//                                    Random rnd = new Random();
//                                    StringBuilder sb = new StringBuilder(6);
//                                    for (int i = 0; i < 6; i++)
//                                        sb.append(chars[rnd.nextInt(chars.length)]);
//
//                                    String randomRe = sb.toString();
//                                    reToSend.setText(randomRe);
                                    reToSend.setText(userKey);
                                    share.setEnabled(true);
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();



                } else {
                    Toast.makeText(GenerateRE.this, "Set Validation time before generating user key", Toast.LENGTH_LONG).show();
                }


            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "User Key is " + reToSend.getText().toString());
                startActivity(Intent.createChooser(shareIntent, "Share Via"));

            }
        });

    }
}
