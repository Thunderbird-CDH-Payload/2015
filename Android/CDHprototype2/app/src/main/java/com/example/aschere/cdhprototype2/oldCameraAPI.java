package com.example.aschere.cdhprototype2;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Deprecated; Ideally use Camera2 API
 * Provided because Camera2 is complex and ETA is unknown
 * Created by aschere on 16/02/13.
 */
public class oldCameraAPI extends AppCompatActivity
{
	byte[] byteData = null;
	boolean byteDataFilled = false;

	public byte[] captureImage(Context context)
	{
		String TAG = "oldCameraAPI:captureImage";
		if(!checkCameraHardware(context))
		{
			Log.e(TAG, "No camera on this device!");
		}

		Camera theCamera = getCameraInstance();
		if(theCamera!=null)
		{
			theCamera.takePicture(null, null, mPicture);
			theCamera.release();
		}
		while(!byteDataFilled)
		{
			;
		}
		byteDataFilled = false;
		return byteData;
	}

	private boolean checkCameraHardware(Context context) {
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
			// Camera is not available (in use or does not exist)
			Log.e("oldCameraAPI:getCameraInstance", "Camera is in use or doesn't exist");
			return null;
		}
		return c; // returns null if camera is unavailable
	}

	private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			String TAG = "PictureCallback:onPictureTaken";
			File pictureFile = camera.getOutputMesdiaFile(1);
			if (pictureFile == null){
				Log.d(TAG, "Error creating media file, check storage permissions: ");
				return;
			}

			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				fos.close();
			} catch (FileNotFoundException e) {
				Log.d(TAG, "File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d(TAG, "Error accessing file: " + e.getMessage());
			}
			byteData = data;
			byteDataFilled = true;
		}
	};
}
