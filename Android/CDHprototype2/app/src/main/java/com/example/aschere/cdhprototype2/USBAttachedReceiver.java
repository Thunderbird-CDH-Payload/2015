package com.example.aschere.cdhprototype2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.util.List;

public class USBAttachedReceiver extends BroadcastReceiver
{
	public USBAttachedReceiver()
	{
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		// TODO: This method is called when the BroadcastReceiver is receiving
		// an Intent broadcast.

		String TAG = "USBAttachedReceiver";
		// Find all available drivers from attached devices.
		UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
		List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
		if (availableDrivers.isEmpty())
		{
			return;
		}

		// Open a connection to the first available driver.
		UsbSerialDriver driver = availableDrivers.get(0);
		UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
		if (connection == null)
		{
			// You probably need to call UsbManager.requestPermission(driver.getDevice(), ..)
			return;
		}

		// Read some data! Most have just one port (port 0).
		UsbSerialPort port = driver.getPorts().get(0);
		try
		{
			port.open(connection);
			port.setParameters(115200, UsbSerialPort.DATABITS_8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
			//TODO: feel free to change this parameters to suit the arduino/STM/whatever
			byte buffer[] = new byte[16];
			int numBytesRead = port.read(buffer, 1000);
			Log.d(TAG, "Read " + numBytesRead + " bytes.");
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				port.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
