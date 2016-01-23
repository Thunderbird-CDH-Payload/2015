package com.example.aschere.cdhprototype2;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by aschere on 1/23/2016.
 * This class handles camera and camera tasks
 */
public class CaptureCamera
{
	protected static Camera c = null;
	protected byte[] rawImage = null;

	/** Check if this device has a camera */
	public boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		}
		catch (Exception e){
			Log.e("Camera", "Camera is not available, does not exist, or has ragequitted.");
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

	private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera)
		{
			//receiving raw image
			//store raw bytes in buffer to pass back to caller at MainActivity
			rawImage = data;

			//also write a copy to external storage
			String TAG = "CameraCaptureImage";
			File rawFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
					imageNameGenerator());
			if (!isExternalStorageWritable()){
				Log.d(TAG, "Error creating media file, check storage permissions: " );//+ e.getMessage());
				return;
			}

			try {
				FileOutputStream fos = new FileOutputStream(rawFile);
				fos.write(data);
				fos.close();
			} catch (FileNotFoundException e) {
				Log.d(TAG, "File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d(TAG, "Error accessing file: " + e.getMessage());
			}
		}
	};

	protected String imageNameGenerator()
	{
		//TODO: create useful unique name
		//this method should have some way of "remembering" the newest file name and increment it
		int counter = 0;
		return "rawImage" + Integer.toString(counter);
	}
	protected byte[] captureImage()
	{
		rawImage = null; //clear rawImage buffer
		c = getCameraInstance(); //set the instance
		c.takePicture(null, mPicture, null, null); //system call
		c.release(); //let it go
		return rawImage; //return raw image (byte array)
	}

	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state);
	}

	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state) ||
				Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
	}
}
