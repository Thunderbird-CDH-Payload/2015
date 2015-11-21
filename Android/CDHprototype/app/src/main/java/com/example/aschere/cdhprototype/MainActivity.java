package com.example.aschere.cdhprototype;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //TODO: Everything

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView bView = (TextView)findViewById(R.id.beerView);
        for(int i = 99; i >= 1; i--)
        {
            if(i >= 3)
            {
                String textedI = Integer.toString(i);
                bView.append(textedI);
                bView.append(" cubesats in LEO, ");
                bView.append(textedI);
                bView.append("cubesats in LEO.\n");
                bView.append("One cubesat de-orbited, ");
                textedI = Integer.toString(i-1);
                bView.append(textedI);
                bView.append(" cubesats left!\n");
            }
            else if(i == 2)
            {
                String textedI = Integer.toString(i);
                bView.append(textedI);
                bView.append(" cubesats in LEO, ");
                bView.append(textedI);
                bView.append("cubesats in LEO.\n");
                bView.append("One cubesat de-orbited, ");
                bView.append("One cubesat left!\n");
            }
            else if(i == 1)
            {
                bView.append("One cubesat in LEO, ");
                bView.append("one cubesat in LEO.\n");
                bView.append("One cubesat de-orbited, ");
                bView.append("one cubesat left!\n");
            }
        }
    }
}
