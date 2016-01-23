package com.example.aschere.cdhprototype2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by aschere on 1/9/2016.
 * Other teams, place the 0x?? identifying your instructions
 */
public class DataFromArduinoReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		byte[] receivedData = intent.getByteArrayExtra("primavera.arduino.intent.extra.DATA");
		/*switch (receivedData[0]) //depending on the data header, do:
		{
			case 0x00:
				break; //something
			case 0x01:
				break; //some other thing
			case 0x02:
				break;
		}*/
		displayReceivedData(receivedData);
	}

	protected void displayReceivedData(byte[] dataReceived)
	{
		String receivedInBytes = "";
		for (byte aDataReceived : dataReceived)
		{
			receivedInBytes = receivedInBytes + aDataReceived;
		}

		MainActivity.receivedDataDisplayer.setText(receivedInBytes);
	}
}
