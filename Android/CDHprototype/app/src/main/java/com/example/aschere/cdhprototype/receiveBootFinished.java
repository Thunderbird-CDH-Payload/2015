package com.example.aschere.cdhprototype;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by aschere on 11/7/2015.
 */
public class receiveBootFinished extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		Intent i = new Intent(context, MainActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		context.startActivity(i); //call mainactivity.class
	}
}
