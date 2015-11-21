/*
 * Copyright (C) 2012 Mathias Jeppsson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.aschere.cdhprototype;

import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ArduinoCommunicatorActivity extends ListActivity
{

	private static final int ARDUINO_USB_VENDOR_ID = 0x2341;

	private final static String TAG = "ArduinoCommunicatorActivity";

	private Boolean mIsReceiving;
	private ArrayList<ByteArray> mTransferedDataList = new ArrayList<ByteArray>();
	private ArrayAdapter<ByteArray> mDataAdapter;
	BroadcastReceiver mReceiver = new BroadcastReceiver()
	{

		private void handleTransferedData(Intent intent, boolean receiving)
		{
			if (mIsReceiving == null || mIsReceiving != receiving)
			{
				mIsReceiving = receiving;
				mTransferedDataList.add(new ByteArray());
			}

			final byte[] newTransferedData = intent.getByteArrayExtra(ArduinoCommunicatorService.DATA_EXTRA);

			ByteArray transferedData = mTransferedDataList.get(mTransferedDataList.size() - 1);
			transferedData.add(newTransferedData);
			mTransferedDataList.set(mTransferedDataList.size() - 1, transferedData);
			mDataAdapter.notifyDataSetChanged();
		}

		@Override
		public void onReceive(Context context, Intent intent)
		{
			final String action = intent.getAction();

			if (ArduinoCommunicatorService.DATA_RECEIVED_INTENT.equals(action))
			{
				handleTransferedData(intent, true);
			}
			else if (ArduinoCommunicatorService.DATA_SENT_INTERNAL_INTENT.equals(action))
			{
				handleTransferedData(intent, false);
			}
		}
	};

	private void findDevice()
	{
		UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
		UsbDevice usbDevice = null;
		HashMap<String, UsbDevice> usbDeviceList = usbManager.getDeviceList();
		Iterator<UsbDevice> deviceIterator = usbDeviceList.values().iterator();
		if (deviceIterator.hasNext())
		{
			UsbDevice tempUsbDevice = deviceIterator.next();

			if (tempUsbDevice.getVendorId() == ARDUINO_USB_VENDOR_ID)
			{
				usbDevice = tempUsbDevice;
			}
		}

		if (usbDevice == null)
		{
			Toast.makeText(getBaseContext(), getString(R.string.no_device_found), Toast.LENGTH_LONG).show();
		}
		else
		{
			Intent startIntent = new Intent(getApplicationContext(), ArduinoCommunicatorService.class);
			PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, startIntent, 0);
			usbManager.requestPermission(usbDevice, pendingIntent);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		IntentFilter filter = new IntentFilter();
		filter.addAction(ArduinoCommunicatorService.DATA_RECEIVED_INTENT);
		filter.addAction(ArduinoCommunicatorService.DATA_SENT_INTERNAL_INTENT);
		registerReceiver(mReceiver, filter);

		mDataAdapter = new ArrayAdapter<ByteArray>(this, android.R.layout.simple_list_item_1, mTransferedDataList);
		setListAdapter(mDataAdapter);

		findDevice();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);

		ByteArray transferedData = mTransferedDataList.get(position);
		transferedData.toggleCoding();
		mTransferedDataList.set(position, transferedData);
		mDataAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);

		if (UsbManager.ACTION_USB_DEVICE_ATTACHED.contains(intent.getAction()))
		{
			findDevice();
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.options, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.help:
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse("http://ron.bems.se/arducom/usage.html")));
				return true;
			case R.id.about:
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse("http://ron.bems.se/arducom/primaindex.php")));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
