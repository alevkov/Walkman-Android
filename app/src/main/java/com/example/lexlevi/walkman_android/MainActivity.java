package com.example.lexlevi.walkman_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private Button main_button_calculator;
    private Button main_button_login;
    private Button main_button_load;
    private TextView main_textView1;
    private TextView main_textView2;
    private TextView main_textView3;

    protected int NumberTimesPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        NumberTimesPressed = 0;

        main_button_calculator = (Button) findViewById(R.id.main_button_calculator);
        main_button_login = (Button) findViewById(R.id.main_button_login);
        main_button_load = (Button) findViewById(R.id.main_button_load);
        main_textView1 = (TextView) findViewById(R.id.main_textView1);
        main_textView2 = (TextView) findViewById(R.id.main_textView2);
        main_textView3 = (TextView) findViewById(R.id.main_textView3);

        main_button_calculator.setOnClickListener(this);
        main_button_login.setOnClickListener(this);
        main_button_load.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (main_button_calculator.isPressed()) {
            Intent i = new Intent(this, CalculatorActivity.class);
            this.startActivity(i);

        } else if (main_button_login.isPressed()) {
            Intent i = new Intent(this, WebActivity.class);
            this.startActivity(i);
        }
        else if (main_button_load.isPressed()) {
            NumberTimesPressed++;
            String result = "Clicked: " + NumberTimesPressed;
            main_textView3.setText(result);
            String arr[] = getResources().getStringArray(R.array.history_array);
            main_textView3.append(arr.toString());
        }
    }


/*
getString(R.string.app_name);

And, you can get string-array using

String arr[] = getResources().getStringArray(R.array.planet);
for (int i = 0; i < arr.length; i++) {
        Toast.makeText(getBaseContext(),arr[i], Toast.LENGTH_LONG).show();
}

 */

}