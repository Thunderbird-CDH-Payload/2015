package com.example.aschere.cdhprototype2;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.*;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by aschere on 1/23/2016.
 * This class handles camera and camera tasks
 */
public class CaptureCamera
{
	protected byte[] rawImage = null;
	protected android.hardware.camera2.CameraDevice cameraDevice = null;
	protected CameraManager cameraManager = null;
	protected String mainCamera = null;

	private void setCameraDevice(CameraManager manglement)
	{
		String TAG = "setCameraDevice";
		String[] rawCameraList = null;
		ArrayList<CameraDevice> cameraList = null;

		cameraManager = manglement;
		try
		{
			rawCameraList = cameraManager.getCameraIdList();
			for (String cameraId: rawCameraList)
			{
				CameraCharacteristics camChars = cameraManager.getCameraCharacteristics(cameraId);
				//get camera characteristics

				//get rear-facing camera
				Integer facing = camChars.get(CameraCharacteristics.LENS_FACING);
				if(facing != null && facing == CameraCharacteristics.LENS_FACING_BACK)
				{
					int[] capabilities = camChars.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
					//get RAW-capable camera
					for (int capabilitiesIntegers: capabilities)
					{
						if(capabilitiesIntegers == CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_RAW)
						{
							mainCamera = cameraId;
							return;
						}
					}
					Log.w(TAG, "No camera with RAW capabilities!");
				}
				Log.w(TAG, "No rear-facing camera!");
			}

		}
		catch (Exception e)
		{
			Log.e(TAG, "Failed to obtain list of camera list");
		}

	}

	private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera)
		{
			//receiving raw image
			//store raw bytes in buffer to pass back to caller at MainActivity
			String TAG = "CameraCaptureImage";
			if(data == null)
			{
				Log.e(TAG, "No raw image available");
			}
			rawImage = data;

			//also write a copy to external storage
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

	private final CameraDevice.StateCallback cameraCallback = new CameraDevice.StateCallback()
	{
		String TAG = "CameraStateCallback";

		@Override
		public void onOpened(CameraDevice camera)
		{
			Log.i(TAG, "Camera opened");
		}

		@Override
		public void onDisconnected(CameraDevice camera)
		{
			Log.w(TAG, "Camera disconnected!");
		}

		@Override
		public void onError(CameraDevice camera, int error)
		{
			Log.e(TAG, "Camera error! " + error);
		}
	};

	protected String imageNameGenerator()
	{
		//TODO: create useful unique name
		//this method should have some way of "remembering" the newest file name and increment it
		//OR use timestamp method
		//following code copied from:
		//https://developer.android.com/training/camera/photobasics.html

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		return "JPEG_" + timeStamp + "_" + ".raw";
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

	public byte[] captureImage(CameraManager manglement) throws CameraAccessException, SecurityException
	{
		String TAG = "CaptureImage";
		this.setCameraDevice(manglement);

		if(mainCamera==null)
		{ //there is no main camera with necessary capabilities
			Log.e(TAG, "No main camera to capture images with!");
			return null;
		}

		manglement.openCamera(mainCamera, cameraCallback, new Handler());
		return null;
	}
}
