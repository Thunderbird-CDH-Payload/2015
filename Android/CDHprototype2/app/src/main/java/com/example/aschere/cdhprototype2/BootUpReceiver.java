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
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context, MainActivity.class);  //MyActivity can be anything which you want to start on bootup...
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}

}
