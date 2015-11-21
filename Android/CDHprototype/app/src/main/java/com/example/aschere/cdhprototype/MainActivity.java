package com.example.aschere.cdhprototype;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.example.aschere.cdhprototype.exceptions.InvalidVoterIdException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity
{

	//TODO: Everything

	private static final int ARDUINO_USB_VENDOR_ID = 0x2341; //might need to change to Chinese id
	private static final int fun_id = 0x000;
	private Boolean mIsReceiving;
	private Boolean mDataListChanged = false;
	private ArrayList<ByteArray> mTransferredDataList = new ArrayList<ByteArray>();
	private UsbDevice usbDevice;
	BroadcastReceiver mReceiver = new BroadcastReceiver()
	{

		private void handleTransferredData(Intent intent, boolean receiving)
		{
			if (mIsReceiving == null || mIsReceiving != receiving)
			{
				mIsReceiving = receiving;
				mTransferredDataList.add(new ByteArray());
				mDataListChanged = true; //changed, process it!
			}

			final byte[] newTransferredData = intent.getByteArrayExtra(ArduinoCommunicatorService.DATA_EXTRA);

			ByteArray transferredData = mTransferredDataList.get(mTransferredDataList.size() - 1);
			transferredData.add(newTransferredData);
			mTransferredDataList.set(mTransferredDataList.size() - 1, transferredData);
			//mDataAdapter.notifyDataSetChanged();
		}

		@Override
		public void onReceive(Context context, Intent intent)
		{
			final String action = intent.getAction();

			if (ArduinoCommunicatorService.DATA_RECEIVED_INTENT.equals(action))
			{
				handleTransferredData(intent, true);
			}
			else if (ArduinoCommunicatorService.DATA_SENT_INTERNAL_INTENT.equals(action))
			{
				handleTransferredData(intent, false);
			}
		}
	};
	private ArrayList<ByteArray> mDataReceived = new ArrayList<>();

	private void findDevice() //throws UsbDeviceNotFound
	{
		UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
		this.usbDevice = null;
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

		if (usbDevice != null)
		{
			Intent startIntent = new Intent(getApplicationContext(), ArduinoCommunicatorService.class);
			PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, startIntent, 0);
			usbManager.requestPermission(usbDevice, pendingIntent);
			//startIntent.putExtra(usbDevice);
		}
		else
		{

			//throw new UsbDeviceNotFound(getString(R.string.no_device_found)); //no device connected!
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

		//mDataAdapter = new ArrayAdapter<ByteArray>(this, android.R.layout.simple_list_item_1, mTransferredDataList);

		this.findDevice();
		this.main();
	}

	public void main()
	{
		ByteArray byteArrayToSend = new ByteArray();

		byteArrayToSend.add((byte) 't');
		byteArrayToSend.add((byte) 'e');
		byteArrayToSend.add((byte) 's');
		byteArrayToSend.add((byte) 't');

		this.sendData(new ByteArray(negotiationCode.SENDING_START.code()));
		this.sendData(byteArrayToSend);
		this.sendData(new ByteArray(negotiationCode.SENDING_END.code()));

		//TODO: demanding data from arduino
		//to receive something
		ByteArray byteArrayToReceive = this.receiveData(negotiationCode.RECEIVING.code());
		if (byteArrayToReceive != byteArrayToSend)
		{
			System.err.println("Test data received != test data sent, check connections");
			//we know connection is not ok
			//throw exception
		}

		while (true)
		{
			//TODO: ping arduino

			//TODO: ask them fun id and verify
			if (receiveData(negotiationCode.GET_FUN_ID.code()).get(0) == fun_id)
			{
				//TODO: switch case
				//Here is where the magic happens. For each case, do something
				int var = 0;
				switch (var)
				{
					case 0:
						break;
					default:
						break;
				}

			}
			//this.sendData(funIsDoneCode);
		}
	}

	private int translateNegotiationCodeToSwitchCode(byte[] negotationCode)
	{
		//TODO: translate byte array negotiation code to int for switch case
		return 0;
	}

	@Override
	protected void onResume() //an analogue of arduinos' loop?
	{
		//TODO: actual stuff to send
		//to send something
		super.onResume();
		this.main();
	}

	/**
	 * Call this to send a byte array to the arduino device
	 *
	 * @param byteArrayToSend a byte array containing the data
	 * @return true if successful, false otherwise
	 */
	private boolean sendData(ByteArray byteArrayToSend)
	{
		try
		{
			Intent sendIntent = new Intent(getApplicationContext(), ArduinoCommunicatorService.class);
			sendIntent.setClass(getApplicationContext(), ArduinoCommunicatorService.class);
			sendIntent.setAction("aschere.cdhprototype.internal.intent.action.DATA_SENT");
			sendIntent.putExtra("dataToSend", byteArrayToSend.toArray()); //put stuff to send to intent
			startActivity(sendIntent); //call ArduinoCommunicatorService to send our stuff
			return true;
		} catch (Exception e)
		{
			return false;
		}
	}

	/* //No GUI necessary
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		ByteArray transferedData = mTransferredDataList.get(position);
		transferedData.toggleCoding();
		mTransferredDataList.set(position, transferedData);
		mDataAdapter.notifyDataSetChanged();
	}
    */

	public ByteArray getData(byte[] negotiationCode)
	{
		return new ByteArray();
	}

	/**
	 * Informs the arduino we want to get a data and tells them to send it
	 * Depending on the negotiationCode, we can request different data
	 * negotiationCodes have to be hard-coded in the arduinos' code
	 *
	 * @param negotiationCode the signal sent to the arduino in order to demand data
	 *                        no, it's not a demand, it is a 'strong request'
	 * @return
	 */
	private ByteArray receiveData(byte[] negotiationCode)
	{
		ByteArray incomingBytes;
		try
		{
			this.sendData(new ByteArray(negotiationCode)); //send a code to arduino indicating we want to receive data
			//code to be coded by arduino team

			mIsReceiving = true;

			//receive "aschere.cdhprototype.intent.action.DATA_RECEIVED" intent
			while (!mDataListChanged)
			{
				wait();
			}
			incomingBytes = mTransferredDataList.get(mTransferredDataList.size() - 1); //get most recent change
			this.mDataListChanged = false;
			return incomingBytes;
		} catch (Exception e)
		{
			return null;
		}
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

	public boolean getConfidenceOnDevice(int voteId, deviceVotes oneVoter, deviceVotes otherVoter) throws
			InvalidVoterIdException
	{
		switch (voteId)
		{
			case 1:
				return oneVoter.getVote(1) || otherVoter.getVote(1);
			case 2:
				return oneVoter.getVote(2) || otherVoter.getVote(2);
			case 3:
				return oneVoter.getVote(3) || otherVoter.getVote(3);
			default:
				throw new InvalidVoterIdException("Invalid voter ID!");
		}
	}
}
