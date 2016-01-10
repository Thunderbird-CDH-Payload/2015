package com.example.aschere.cdhprototype2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.onResume();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		sendDataToArduino(new byte[2]); //should be sending a magic number indicating phone is available
	}

	public boolean sendDataToArduino(byte[] byteArray)
	{
		Intent sendingIntent = new Intent("primavera.arduino.intent.action.SEND_DATA"); //new intent for starting
		// communication
		sendingIntent.putExtra("primavera.arduino.intent.extra.DATA", byteArray);
		this.getApplicationContext().sendBroadcast(sendingIntent);
	}
}
