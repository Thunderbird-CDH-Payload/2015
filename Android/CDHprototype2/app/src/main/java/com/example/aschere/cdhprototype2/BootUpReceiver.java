package com.example.aschere.cdhprototype2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by aschere on 1/9/2016.
 */
public class BootUpReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		//Code copied from http://www.khurramitdeveloper.blogspot.ca/2013/06/start-activity-or-service-on-boot.html
		// TODO: This method is called when the BroadcastReceiver is receiving

		// Start Service On Boot Start Up
		Intent service = new Intent(context, DataFromArduinoReceiver.class);
		context.startService(service);

		//Start App On Boot Start Up
		Intent App = new Intent(context, MainActivity.class);
		App.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(App);
	}

}
