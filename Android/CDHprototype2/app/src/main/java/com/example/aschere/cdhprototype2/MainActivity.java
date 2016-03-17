package com.example.aschere.cdhprototype2;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import net.bozho.easycamera.DefaultEasyCamera;
import net.bozho.easycamera.EasyCamera;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
	public static TextView receivedDataDisplayer;
	protected static ArrayList<byte[]> magicNumber;
	protected static CaptureCamera cameraHandler;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	List<String> rawImages = null;

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
				rawImages.add(takeCameraImage());
				/*UsbSerialPort arduinoPort = openConnection();
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
				}*/
				for (String imageName : rawImages)
				{
					if(imageName != null)
					{
						Log.w("Filename", imageName);
					}
				}
			}
		});

		byte[] iAmReadySignal = {0x00, 0x11, 0x22};
		// this signal will be sent to the arduino indicating that the android's ready and parents ain't home
		rawImages = new ArrayList<>();
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

	/*public String takeCameraImage()
	{
		String TAG = "MainActivity:takeCameraImage";

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
				PackageManager.PERMISSION_GRANTED)
		{
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			Log.e(TAG, "Camera permission not granted!");
		}

		//take picture
		if (!checkCameraHardware(this.getApplicationContext()))
		{
			Log.e(TAG, "No camera on this device!");
		}

		CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
		Camera2RawFragment rawCam = Camera2RawFragment.newInstance(manager, this.getApplicationContext());
		//cameraHandler = new CaptureCamera(this.getApplicationContext(), manager);
		//SurfaceView view = new SurfaceView(this.getApplicationContext());

		try
		{
			return rawCam.takePicture();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}*/

	/*public byte[] takeCameraImage()
	{
		String TAG = "MainActivity:takeCameraImage";

		RxCameraConfig config = RxCameraConfigChooser.obtain().
				useBackCamera().
				setAutoFocus(true).
				setPreferPreviewFrameRate(0, 0).
				setPreferPreviewSize(new Point(0, 0)).
				setHandleSurfaceEvent(false).
				get();

		//ImageReader imgReader = ImageReader.newInstance(Camera.getCameraInfo(config.currentCameraId, );)
		final SurfaceView surfaceView = new SurfaceView(null);

		Observable<RxCamera> rxCamera = RxCamera.open(this, config).flatMap(new Func1<RxCamera, Observable<RxCamera>>()
		{
			@Override
			public Observable<RxCamera> call(RxCamera rxCamera)
			{
				//return rxCamera.bindTexture(textureView);
				// or bind a SurfaceView:
				return rxCamera.bindSurface(surfaceView);
			}
		}).flatMap(new Func1<RxCamera, Observable<RxCamera>>()
		{
			@Override
			public Observable<RxCamera> call(RxCamera rxCamera)
			{
				return rxCamera.startPreview();
			}
		});

		RxCamera camera = new RxCamera();

		RxCamera.request().takePictureRequest(true, new Func() {
			@Override
			public void call() {
				Log.i("rxCamera", "Captured!");
			}
		}, 1080, 1920, ImageFormat.RAW_SENSOR).subscribe(new Action1<RxCameraData>()
		{
			@Override
			public void call(RxCameraData rxCameraData)
			{
				return rxCameraData.cameraData;
			}
		});
	}*/

	public Image takeCameraImage()
	{
		String TAG = "MainActivity:takeCameraImage";

		SurfaceView surfaceView = new SurfaceView(null);
		EasyCamera camera = DefaultEasyCamera.open();
		EasyCamera.CameraActions actions = camera.startPreview(surfaceView);
		Camera.PictureCallback callback = new Camera.PictureCallback() {
			/**
			 * Called when image data is available after a picture is taken.
			 * The format of the data depends on the context of the callback
			 * and {@link Camera.Parameters} settings.
			 *
			 * @param data   a byte array of the picture data
			 * @param camera the Camera service object
			 */
			@Override
			public void onPictureTaken(byte[] data, Camera camera)
			{

			}

			public void onPictureTaken(byte[] data, EasyCamera.CameraActions actions) {
				// store picture
			}
		};
		actions.takePicture(EasyCamera.Callbacks.create().withJpegCallback(callback));
	}

	private boolean checkCameraHardware(Context context)
	{
		// this device has a camera
		// no camera on this device
		return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
	}
}