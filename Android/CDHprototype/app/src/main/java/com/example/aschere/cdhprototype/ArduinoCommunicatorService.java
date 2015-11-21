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
 *
 * Note: Code is modified.
 */

package com.example.aschere.cdhprototype;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.*;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;

public class ArduinoCommunicatorService extends Service
{

	final static String DATA_RECEIVED_INTENT = "aschere.cdhprototype.intent.action.DATA_RECEIVED";
	final static String SEND_DATA_INTENT = "aschere.cdhprototype.intent.action.SEND_DATA";
	final static String DATA_SENT_INTERNAL_INTENT = "aschere.cdhprototype.internal.intent.action.DATA_SENT";
	final static String DATA_EXTRA = "aschere.cdhprototype.intent.extra.DATA";
	private boolean mIsRunning = false;
	private SenderThread mSenderThread;
	BroadcastReceiver mReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			final String action = intent.getAction();
			//onreceive

			if (SEND_DATA_INTENT.equals(action))
			{
				final byte[] dataToSend = intent.getByteArrayExtra(DATA_EXTRA);
				if (dataToSend == null)
				{ //no DATA_EXTRA extra in intent R.string.no_extra_in_intent
					return;
				}

				mSenderThread.mHandler.obtainMessage(10, dataToSend).sendToTarget();
			}
			else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action))
			{ //R.string.device_detaches
				mSenderThread.mHandler.sendEmptyMessage(11);
				stopSelf();
			}
		}
	};
	private volatile UsbDevice mUsbDevice = null;
	private volatile UsbDeviceConnection mUsbConnection = null;
	private volatile UsbEndpoint mInUsbEndpoint = null;
	private volatile UsbEndpoint mOutUsbEndpoint = null;

	@Override
	public IBinder onBind(Intent arg0)
	{
		return null;
	}

	@Override
	public void onCreate()
	{ //onCreate
		super.onCreate();
		IntentFilter filter = new IntentFilter();
		filter.addAction(SEND_DATA_INTENT);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		registerReceiver(mReceiver, filter);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{ //onStartCommand intent flags startId

		if (mIsRunning)
		{ //service already running
			return Service.START_REDELIVER_INTENT;
		}

		mIsRunning = true;

		if (!intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false))
		{ //R.string.permission_denied
			stopSelf();
			return Service.START_REDELIVER_INTENT;
		}

		//permission granted
		mUsbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
		if (!initDevice())
		{ //init of device failed
			stopSelf();
			return Service.START_REDELIVER_INTENT;
		}

		//R.string.receiving
		startReceiverThread();
		startSenderThread();

		return Service.START_REDELIVER_INTENT;
	}

	@Override
	public void onDestroy()
	{ //onDestroy
		super.onDestroy();
		unregisterReceiver(mReceiver);
		mUsbDevice = null;
		if (mUsbConnection != null)
		{
			mUsbConnection.close();
		}
	}

	private byte[] getLineEncoding(int baudRate)
	{
		final byte[] lineEncodingRequest = {(byte) 0x80, 0x25, 0x00, 0x00, 0x00, 0x00, 0x08};
		switch (baudRate)
		{
			case 14400:
				lineEncodingRequest[0] = 0x40;
				lineEncodingRequest[1] = 0x38;
				break;

			case 19200:
				lineEncodingRequest[0] = 0x00;
				lineEncodingRequest[1] = 0x4B;
				break;
		}

		return lineEncodingRequest;
	}

	private boolean initDevice()
	{
		UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
		mUsbConnection = usbManager.openDevice(mUsbDevice);
		if (mUsbConnection == null)
		{ //opening usb device failed! R.string.opening_device_failed
			return false;
		}
		UsbInterface usbInterface = mUsbDevice.getInterface(1);
		if (!mUsbConnection.claimInterface(usbInterface, true))
		{ //R.string.claimning_interface_failed
			mUsbConnection.close();
			return false;
		}

		// Arduino USB serial converter setup
		// Set control line state
		mUsbConnection.controlTransfer(0x21, 0x22, 0, 0, null, 0, 0);
		// Set line encoding.
		mUsbConnection.controlTransfer(0x21, 0x20, 0, 0, getLineEncoding(9600), 7, 0);

		for (int i = 0; i < usbInterface.getEndpointCount(); i++)
		{
			if (usbInterface.getEndpoint(i).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK)
			{
				if (usbInterface.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_IN)
				{
					mInUsbEndpoint = usbInterface.getEndpoint(i);
				}
				else if (usbInterface.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_OUT)
				{
					mOutUsbEndpoint = usbInterface.getEndpoint(i);
				}
			}
		}

		if (mInUsbEndpoint == null)
		{ //R.string.no_in_endpoint_found
			mUsbConnection.close();
			return false;
		}

		if (mOutUsbEndpoint == null)
		{ //R.string.no_out_endpoint_found
			mUsbConnection.close();
			return false;
		}

		return true;
	}

	private void startReceiverThread()
	{
		new Thread("arduino_receiver")
		{
			public void run()
			{
				byte[] inBuffer = new byte[4096];
				while (mUsbDevice != null)
				{ //calling bulkTransfer in
					final int len = mUsbConnection.bulkTransfer(mInUsbEndpoint, inBuffer, inBuffer.length, 0);
					if (len > 0)
					{
						Intent intent = new Intent(DATA_RECEIVED_INTENT);
						byte[] buffer = new byte[len];
						System.arraycopy(inBuffer, 0, buffer, 0, len);
						intent.putExtra(DATA_EXTRA, buffer);
						LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
					}
					else
					{ //zero data read!
					}
				}

				//receiver thread stopped
			}
		}.start();
	}

	private void startSenderThread()
	{
		mSenderThread = new SenderThread("arduino_sender");
		mSenderThread.start();
	}

	private class SenderThread extends Thread
	{
		public Handler mHandler;

		public SenderThread(String string)
		{
			super(string);
		}

		public void run()
		{

			Looper.prepare();

			mHandler = new Handler()
			{
				public void handleMessage(Message msg)
				{ //handleMessage msg.what
					if (msg.what == 10)
					{
						final byte[] dataToSend = (byte[]) msg.obj;

						//calling bulkTransfer out
						final int len = mUsbConnection.bulkTransfer(mOutUsbEndpoint, dataToSend, dataToSend.length, 0);
						//length of (dataToSend.length) sent
						Intent sendIntent = new Intent(DATA_SENT_INTERNAL_INTENT);
						sendIntent.putExtra(DATA_EXTRA, dataToSend);
						LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(sendIntent);
					}
					else if (msg.what == 11)
					{
						Looper.myLooper().quit();
					}
				}
			};

			Looper.loop();
			//sender thread stopped
		}
	}
}
