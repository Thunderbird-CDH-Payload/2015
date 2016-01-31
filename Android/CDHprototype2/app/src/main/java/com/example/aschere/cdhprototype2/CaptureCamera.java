package com.example.aschere.cdhprototype2;

import android.graphics.ImageFormat;
import android.hardware.camera2.*;
import android.media.ImageReader;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Created by aschere on 1/23/2016.
 * This class handles camera and camera tasks
 * This class creates RAW images
 */
public class CaptureCamera
{
	protected byte[] rawImage = null;
	protected CameraManager cameraManager = null;
	protected String mainCamera = null;

	protected CaptureRequest.Builder captureBuilder;
	protected CameraCaptureSession cameraCaptureSession;
	protected CameraCharacteristics cameraCharacteristics;
	protected CameraDevice cameraDevice;
	protected ImageReader imageReader;

	private void setCameraDevice(CameraManager manglement)
	{
		String TAG = "setCameraDevice";
		String[] rawCameraList;

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
					assert capabilities != null;
					for (int capabilitiesIntegers: capabilities)
					{
						if(capabilitiesIntegers == CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_RAW)
						{
							mainCamera = cameraId;
							cameraCharacteristics = manglement.getCameraCharacteristics(mainCamera);
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

	private final CameraDevice.StateCallback cameraCallback = new CameraDevice.StateCallback()
	{
		String TAG = "CameraStateCallback";

		@Override
		public void onOpened(@NonNull CameraDevice camera)
		{
			Log.i(TAG, "Camera opened! Camera is " + camera.getId());
			cameraDevice = camera;
		}

		@Override
		public void onDisconnected(@NonNull CameraDevice camera)
		{
			Log.w(TAG, "Camera disconnected!");
		}

		@Override
		public void onError(@NonNull CameraDevice camera, int error)
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

		//Open the camera
		Log.i(TAG, "Trying to open camera...");
		manglement.openCamera(mainCamera, cameraCallback, null);
		//Log.i(TAG, "Camera opened!");

		if(!Objects.equals(cameraDevice.getId(), mainCamera))
		{ //there is no main camera with necessary capabilities
			Log.e(TAG, "No main camera to capture images with!");
			return null;
		}

		//TODO: Capture Image
		captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
		//Auto-focus (code from android-Camera2Raw)
		captureBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
		//Auto-Exposure (ditto)
		captureBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_START);

		int width;
		int height;

		try
		{ //attempt to obtain the width and height of the camera result
			width = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE).getWidth();
			height = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE).getHeight();
		}
		catch(NullPointerException e)
		{
			Log.e(TAG, "Failed to obtain width or height or camera!" + e.getLocalizedMessage());
			return null;
		}

		//Create receiving surface
		imageReader = ImageReader.newInstance(width, height, ImageFormat.RAW_SENSOR, 2);
		//cameraCaptureSession.prepare(imageReader.getSurface());
		captureBuilder.addTarget(imageReader.getSurface());

		//Create the request
		CaptureRequest theRequest = captureBuilder.build();

		//Capture
		cameraCaptureSession.capture(theRequest, captureCallback, new Handler());
		//CameraDevice rawCamera =

		//Save image

		return null;
	}

	protected boolean dumpRawFile(TotalCaptureResult result)
	{
		String TAG = "dumpRawFile";
		File rawFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				imageNameGenerator());
		DngCreator rawDumper = new DngCreator(cameraCharacteristics, result);
		try
		{
			FileOutputStream outputStream = new FileOutputStream(rawFile);
			rawDumper.writeImage(outputStream, imageReader.acquireLatestImage());
			return true;
		} catch (FileNotFoundException e)
		{
			Log.e(TAG, "File not found! " + e.getLocalizedMessage());
			e.printStackTrace();
			return false;
		} catch (IOException e)
		{
			Log.e(TAG, "IOException! " + e.getLocalizedMessage());
			e.printStackTrace();
			return false;
		}
	}

	protected CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback()
	{
		String TAG = "CaptureCallback";

		/**
		 * This method is called when an image capture makes partial forward progress; some
		 * (but not all) results from an image capture are available.
		 * <p/>
		 * <p>The result provided here will contain some subset of the fields of
		 * a full result. Multiple {@link #onCaptureProgressed} calls may happen per
		 * capture; a given result field will only be present in one partial
		 * capture at most. The final {@link #onCaptureCompleted} call will always
		 * contain all the fields (in particular, the union of all the fields of all
		 * the partial results composing the total result).</p>
		 * <p/>
		 * <p>For each request, some result data might be available earlier than others. The typical
		 * delay between each partial result (per request) is a single frame interval.
		 * For performance-oriented use-cases, applications should query the metadata they need
		 * to make forward progress from the partial results and avoid waiting for the completed
		 * result.</p>
		 * <p/>
		 * <p>Each request will generate at least {@code 1} partial results, and at most
		 * {@link CameraCharacteristics#REQUEST_PARTIAL_RESULT_COUNT} partial results.</p>
		 * <p/>
		 * <p>Depending on the request settings, the number of partial results per request
		 * will vary, although typically the partial count could be the same as long as the
		 * camera device subsystems enabled stay the same.</p>
		 * <p/>
		 * <p>The default implementation of this method does nothing.</p>
		 *
		 * @param session       the session returned by {@link CameraDevice#createCaptureSession}
		 * @param request       The request that was given to the CameraDevice
		 * @param partialResult The partial output metadata from the capture, which
		 *                      includes a subset of the {@link TotalCaptureResult} fields.
		 */
		@Override
		public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult)
		{
			Log.i(TAG, "Capture progressed");
			super.onCaptureProgressed(session, request, partialResult);
		}

		/**
		 * This method is called when an image capture has fully completed and all the
		 * result metadata is available.
		 * <p/>
		 * <p>This callback will always fire after the last {@link #onCaptureProgressed};
		 * in other words, no more partial results will be delivered once the completed result
		 * is available.</p>
		 * <p/>
		 * <p>For performance-intensive use-cases where latency is a factor, consider
		 * using {@link #onCaptureProgressed} instead.</p>
		 * <p/>
		 * <p>The default implementation of this method does nothing.</p>
		 *
		 * @param session the session returned by {@link CameraDevice#createCaptureSession}
		 * @param request The request that was given to the CameraDevice
		 * @param result  The total output metadata from the capture, including the
		 *                final capture parameters and the state of the camera system during
		 *                capture.
		 */
		@Override
		public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result)
		{
			dumpRawFile(result);
			super.onCaptureCompleted(session, request, result);
		}

		/**
		 * This method is called instead of {@link #onCaptureCompleted} when the
		 * camera device failed to produce a {@link CaptureResult} for the
		 * request.
		 * <p/>
		 * <p>Other requests are unaffected, and some or all image buffers from
		 * the capture may have been pushed to their respective output
		 * streams.</p>
		 * <p/>
		 * <p>The default implementation of this method does nothing.</p>
		 *
		 * @param session The session returned by {@link CameraDevice#createCaptureSession}
		 * @param request The request that was given to the CameraDevice
		 * @param failure The output failure from the capture, including the failure reason
		 *                and the frame number.
		 */
		@Override
		public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure)
		{
			Log.e(TAG, "Capture Failed!" + failure.getReason());
			super.onCaptureFailed(session, request, failure);
		}

		/**
		 * This method is called independently of the others in CaptureCallback,
		 * when a capture sequence finishes and all {@link CaptureResult}
		 * or {@link CaptureFailure} for it have been returned via this listener.
		 * <p/>
		 * <p>In total, there will be at least one result/failure returned by this listener
		 * before this callback is invoked. If the capture sequence is aborted before any
		 * requests have been processed, {@link #onCaptureSequenceAborted} is invoked instead.</p>
		 * <p/>
		 * <p>The default implementation does nothing.</p>
		 *
		 * @param session     The session returned by {@link CameraDevice#createCaptureSession}
		 * @param sequenceId  A sequence ID returned by the {capture} family of functions.
		 * @param frameNumber The last frame number (returned by {@link CaptureResult#getFrameNumber}
		 *                    or {@link CaptureFailure#getFrameNumber}) in the capture sequence.
		 * @see CaptureResult#getFrameNumber()
		 * @see CaptureFailure#getFrameNumber()
		 * @see CaptureResult#getSequenceId()
		 * @see CaptureFailure#getSequenceId()
		 * @see #onCaptureSequenceAborted
		 */
		@Override
		public void onCaptureSequenceCompleted(CameraCaptureSession session, int sequenceId, long frameNumber)
		{
			super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
		}

		/**
		 * This method is called independently of the others in CaptureCallback,
		 * when a capture sequence aborts before any {@link CaptureResult}
		 * or {@link CaptureFailure} for it have been returned via this listener.
		 * <p/>
		 * <p>Due to the asynchronous nature of the camera device, not all submitted captures
		 * are immediately processed. It is possible to clear out the pending requests
		 * by a variety of operations such as {@link CameraCaptureSession#stopRepeating} or
		 * {@link CameraCaptureSession#abortCaptures}. When such an event happens,
		 * {@link #onCaptureSequenceCompleted} will not be called.</p>
		 * <p/>
		 * <p>The default implementation does nothing.</p>
		 *
		 * @param session    The session returned by {@link CameraDevice#createCaptureSession}
		 * @param sequenceId A sequence ID returned by the {capture} family of functions.
		 * @see CaptureResult#getFrameNumber()
		 * @see CaptureFailure#getFrameNumber()
		 * @see CaptureResult#getSequenceId()
		 * @see CaptureFailure#getSequenceId()
		 * @see #onCaptureSequenceCompleted
		 */
		@Override
		public void onCaptureSequenceAborted(CameraCaptureSession session, int sequenceId)
		{
			Log.w(TAG, "Capture aborted!");
			super.onCaptureSequenceAborted(session, sequenceId);
		}
	};
}
