package com.example.aschere.cdhprototype2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by aschere on 1/9/2016.
 */
public class DataFromArduinoReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		byte[] receivedData = intent.getByteArrayExtra("primavera.arduino.intent.extra.DATA");
		switch (receivedData[0]) //depending on the data header, do:
		{
			case 0:
				break; //something
			case 1:
				break; //some other thing
		}
	}
}
