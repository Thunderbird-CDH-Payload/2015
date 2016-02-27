package com.example.aschere.cdhprototype2;

import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
	public static TextView receivedDataDisplayer;
	protected static ArrayList<byte[]> magicNumber;
	protected static CaptureCamera cameraHandler = new CaptureCamera();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		String TAG = "MainActivityOnCreate";
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		receivedDataDisplayer = (TextView) findViewById(R.id.receivedTextView);

		Button sendHelloWorld = (Button) findViewById(R.id.sendHelloWorldButton);
		sendHelloWorld.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String TAG = "ButtonOnClick";
				byte[] helloWorldArray = new byte[]{'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd', '!'};
				System.out.println("Hello World Array " + Arrays.toString(helloWorldArray));
				//sendDataToArduino(helloWorldArray);
				takeCameraImage();
				UsbSerialPort arduinoPort = openConnection();
				if (arduinoPort == null)
				{
					return;
				}
				try
				{
					arduinoPort.write(helloWorldArray, 100);
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		});

		byte[] iAmReadySignal = {0x00, 0x11, 0x22};
		// this signal will be sent to the arduino indicating that the android's ready and parents ain't home
		//sendDataToArduino(iAmReadySignal);
		this.onResume();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		//sendDataToArduino(new byte[2]); //should be sending a magic number indicating phone is available
	}

	@Override
	protected void onPause()
	{

		super.onPause();
	}

	/**
	 * Opens a connection with whatever USB device is attached.
	 *
	 * @return port a UsbSerialPort that can be written/read to/from
	 */
	public UsbSerialPort openConnection()
	{
		String TAG = "USBAttachedReceiver";
		// Find all available drivers from attached devices.
		UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
		List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
		if (availableDrivers.isEmpty())
		{
			Log.e(TAG, "There are no available drivers!");
			return null;
		}

		for (int j = 0; j < availableDrivers.size(); j++)
		{
			UsbSerialDriver driver = availableDrivers.get(j);
			System.out.println("Driver " + j);
			Log.i(TAG, "Driver " + j);
			UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
			if (connection == null)
			{
				// You probably need to call UsbManager.requestPermission(driver.getDevice(), ..)
				Log.e(TAG, "Connection null!");
				return null;
			}
			for (int i = 0; i < driver.getPorts().size(); i++)
			{
				System.out.println("Driver " + j + " port " + i);
				Log.i(TAG, i + " " + driver.getPorts().get(i).getSerial());
			}

		}
		return availableDrivers.get(0).getPorts().get(0);
	}

	/*public boolean sendDataToArduino(byte[] byteArray)
	{
		Intent sendingIntent = new Intent("primavera.arduino.intent.action.SEND_DATA"); //new intent for starting
		// communication
		sendingIntent.putExtra("primavera.arduino.intent.extra.DATA", byteArray);
		this.getApplicationContext().sendBroadcast(sendingIntent);
		return true;
	}*/

	public byte[] takeCameraImage()
	{
		String TAG = "MainActivity:takeCameraImage";

		//take picture
		oldCameraAPI oAPI = new oldCameraAPI(this.getApplicationContext());
		return oAPI.captureImage(this.getApplicationContext());
		/*try
		{
			//return cameraHandler.captureImage((CameraManager) getSystemService(Context.CAMERA_SERVICE));
		}
		catch (CameraAccessException e)
		{
			Log.e("Main:takeCameraImage", "Camera access exception: " + e.getLocalizedMessage());
		}

		return null;*/
	}


}
