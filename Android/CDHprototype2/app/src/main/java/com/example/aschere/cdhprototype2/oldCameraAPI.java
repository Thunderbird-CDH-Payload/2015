package com.example.aschere.cdhprototype2;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Deprecated; Ideally use Camera2 API
 * Provided because Camera2 is complex and ETA is unknown
 * Created by aschere on 16/02/13.
 */
public class oldCameraAPI extends AppCompatActivity
{
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	protected Context mainContext;
	byte[] byteData = null;
	boolean byteDataFilled = false;

	private Camera.PictureCallback jpegCallback = new Camera.PictureCallback()
	{
		@Override
		public void onPictureTaken(byte[] data, Camera camera)
		{
			//byteDataFilled = true;
		}
	};
	private Camera.PictureCallback mPicture = new Camera.PictureCallback()
	{

		@Override
		public void onPictureTaken(byte[] data, Camera camera)
		{
			/*String TAG = "PictureCallback:onPictureTaken";
			File pictureFile = getOutputMediaFile(1);
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
			}*/
			byteData = data;
			byteDataFilled = true;
			camera.release();
		}
	};

	public oldCameraAPI(Context appContext)
	{
		mainContext = appContext;
	}

	/**
	 * A safe way to get an instance of the Camera object.
	 */
	public static Camera getCameraInstance()
	{
		Camera c = null;
		try
		{
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e)
		{
			// Camera is not available (in use or does not exist)
			Log.e("oldCameraAPI:gCI", "Camera is in use or doesn't exist");
			return null;
		}
		return c; // returns null if camera is unavailable
	}

	/**
	 * Create a File for saving an image or video
	 */
	private static File getOutputMediaFile(int type)
	{
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), "MyCameraApp");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists())
		{
			if (!mediaStorageDir.mkdirs())
			{
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE)
		{
			mediaFile = new File(mediaStorageDir.getPath() + File.separator +
					"IMG_" + timeStamp + ".jpg");
		}
		else if (type == MEDIA_TYPE_VIDEO)
		{
			mediaFile = new File(mediaStorageDir.getPath() + File.separator +
					"VID_" + timeStamp + ".mp4");
		}
		else
		{
			return null;
		}

		return mediaFile;
	}

	public byte[] captureImage(Context context)
	{
		String TAG = "oldCameraAPI:captureImage";
		byteData=null;
		if (!checkCameraHardware(context))
		{
			Log.e(TAG, "No camera on this device!");
		}

		Camera theCamera = getCameraInstance();
		if (theCamera != null)
		{
			try
			{
				SurfaceView view = new SurfaceView(mainContext);
				theCamera.setPreviewDisplay(view.getHolder());
				theCamera.startPreview();
				Log.w(TAG, "Preview started");
				theCamera.takePicture(null, mPicture, null, null);
				Log.w(TAG, "Picture taken");
				theCamera.release();
				Log.w(TAG, "Camera released");
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		while (!byteDataFilled)
		{
			int x = 0;
		}
		byteDataFilled = false;
		return byteData;
	}

	private boolean checkCameraHardware(Context context)
	{
		// this device has a camera
		// no camera on this device
		return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
	}
}
