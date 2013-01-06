package com.phc.opentok;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import org.appcelerator.kroll.common.Log;

import com.opentok.Publisher;
import com.opentok.Session;
import com.opentok.Stream;
import com.opentok.Subscriber;
import com.phc.opentok.VideosView;

public class OpentokModuleActivity extends Activity implements Publisher.Listener, Session.Listener, Callback {
	ExecutorService executor;
	SurfaceView publisherView;
	SurfaceView subscriberView;
	Camera camera;
	Publisher publisher;
	Subscriber subscriber;
	static String apiKey;
	static String token;
	static String sessionId;
	private Session session;
	private WakeLock wakeLock;
	private VideosView videosView;

	public static final String EXTRA_RESULT = "videosResult";
	private static final String LCAT = "OpentokModuleActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras(); 
		if(extras !=null)
		{
			apiKey = extras.getString("apiKey");
			token = extras.getString("token");
			sessionId = extras.getString("sessionId");
		}
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		videosView = new VideosView(this);
		publisherView = videosView.getPublisherView();
		subscriberView = videosView.getSubscriberView();

		// Although this call is deprecated, Camera preview still seems to require it :-\
		publisherView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// SurfaceHolders are not initially available, so we'll wait to create the publisher
		publisherView.getHolder().addCallback(this);
		
		setContentView(videosView);

		// A simple executor will allow us to perform tasks asynchronously.
		executor = Executors.newCachedThreadPool();
	}

	@Override
	public void onStop() {
		super.onStop();

		// Release the camera when the application is being destroyed, lest we can't acquire it again later.
		if (null != camera) camera.release();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	/**
	 * Invoked when Our Publisher's rendering surface comes available.
	 */
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		if (publisher == null) {
			executor.submit(new Runnable() {

				@Override
				public void run() {
					try {
						// This usually maps to the front camera.
						Class<?> cameraClass = Class.forName("android.hardware.Camera");
						Method cameraOpenMethod = cameraClass.getMethod("open", Integer.TYPE);
						camera = (Camera) cameraOpenMethod.invoke(null, 1);
						camera.setPreviewDisplay(publisherView.getHolder());
						// Note: preview will continue even if we fail to connect.
						camera.startPreview();

						// Since our Publisher is ready, go ahead and prepare session instance and connect.
						session = Session.newInstance(getApplicationContext(), 
								sessionId,
								token,
								apiKey,
								OpentokModuleActivity.this);
						session.connect();

					} catch (Throwable t) {
						t.printStackTrace();
					}

				}});
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSessionConnected() {
		executor.submit(new Runnable() {

			@Override
			public void run() {
				// Session is ready to publish. Create Publisher instance from our rendering surface and camera, then connect.
				publisher = session.createPublisher(camera, publisherView.getHolder());
				publisher.connect();
			}});
	}

	@Override
	public void onSessionDidReceiveStream(final Stream stream) {
		executor.submit(new Runnable() {

			@Override
			public void run() {
				if (publisher.getStreamId().equals(stream.getStreamId())) {
					// May do something with this later
				} else {
					subscriber = session.createSubscriber(subscriberView, stream);
					subscriber.connect();
				}
			}});
	}

	@Override
	public void onPublisherStreamingStarted() {
		Log.i(LCAT, "publisher is streaming!");
	}

	@Override
	public void onPublisherFailed() {
		Log.e(LCAT, "publisher failed!");
	}

	@Override
	public void onSessionDidDropStream(Stream stream) {
		Log.i(LCAT, String.format("stream %d dropped", stream.toString()));
	}

	@Override
	public void onSessionDisconnected() {
		Log.i(LCAT, "session disconnected");	
	}

	@Override
	public void onPublisherDisconnected() {
		Log.i(LCAT, "publisher disconnected");	

	}

	@Override
	public void onSessionError(Exception arg0) {
		Log.e(LCAT, "session failed!");	
	}

}
