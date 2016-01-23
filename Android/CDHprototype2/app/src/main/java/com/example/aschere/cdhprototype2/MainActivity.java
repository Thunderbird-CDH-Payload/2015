package com.example.aschere.cdhprototype2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity
{
	protected static ArrayList<byte[]> magicNumber;
	public static TextView receivedDataDisplayer;
	protected static CaptureCamera cameraHandler = new CaptureCamera();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button sendHelloWorld = (Button) findViewById(R.id.sendHelloWorldButton);
		receivedDataDisplayer = (TextView) findViewById(R.id.receivedTextView);
		sendHelloWorld.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				byte[] helloWorldArray = new byte[] {'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd', '!'};
				System.out.println(Arrays.toString(helloWorldArray));
				sendDataToArduino(helloWorldArray);
				takeCameraImage();

			}
		});

		byte[] iAmReadySignal = {0x00, 0x11, 0x22};
		// this signal will be sent to the arduino indicating that the android's ready and parents ain't home
		sendDataToArduino(iAmReadySignal);
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
		return true;
	}

	public byte[] takeCameraImage()
	{
		if(!cameraHandler.checkCameraHardware(this.getApplicationContext()))
		{
			Log.e("MainActivity", "Mission Fail! Can't connect to camera!");
			return null;
		}
		return cameraHandler.captureImage();
	}


}
