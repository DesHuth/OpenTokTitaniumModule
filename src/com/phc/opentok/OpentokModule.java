package com.phc.opentok;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollObject;
import org.appcelerator.kroll.annotations.Kroll;

import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.util.TiActivityResultHandler;
import org.appcelerator.titanium.util.TiActivitySupport;
import org.appcelerator.titanium.util.TiIntentWrapper;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiConfig;

import android.app.Activity;
import android.content.Intent;

import com.phc.opentok.OpentokModuleActivity;

@Kroll.module(name="Opentok", id="com.phc.opentok")
public class OpentokModule extends KrollModule
{
	private static final String LCAT = "OpentokModule";
	protected static final int UNKNOWN_ERROR = 0;

	public OpentokModule()
	{
		super();
	}

	@Kroll.onAppCreate
	public static void onAppCreate(TiApplication app)
	{
		Log.d(LCAT, "inside onAppCreate");
	}

	@Kroll.method
	public void start(KrollDict options, String apiKey, String token, String sessionId)
	{
		Log.d(LCAT, "Start called");
		final KrollFunction successCallback = getCallback(options, "success");
		final KrollFunction cancelCallback = getCallback(options, "cancel");
		final KrollFunction errorCallback = getCallback(options, "error");

		final Activity activity = TiApplication.getAppCurrentActivity();
		final TiActivitySupport activitySupport = (TiActivitySupport) activity;
		final Intent intent = new Intent(activity, OpentokModuleActivity.class);
		intent.putExtra("apiKey", apiKey);
		intent.putExtra("token", token);
		intent.putExtra("sessionId", sessionId);
		final TiIntentWrapper videosIntentWrapper = new TiIntentWrapper(intent);
		videosIntentWrapper.setWindowId(TiIntentWrapper.createActivityName("VIDEOS"));
		
		VideosResultHandler resultHandler = new VideosResultHandler();
		resultHandler.successCallback = successCallback;
		resultHandler.cancelCallback = cancelCallback;
		resultHandler.errorCallback = errorCallback;
		resultHandler.activitySupport = activitySupport;
		resultHandler.videosIntent = videosIntentWrapper.getIntent();
		
		activity.runOnUiThread(resultHandler);
	}

	private KrollDict getDictForResult(final String result) {
		final KrollDict dict = new KrollDict();
		dict.put("barcode", result);
		return dict;
	}

	private KrollFunction getCallback(final KrollDict options, final String name) {
		if (options.containsKey(name)) {
			return (KrollFunction) options.get(name);
		} else {
			Log.e(LCAT, "Callback not found: " + name);
			return null;
		}
	}

	protected class VideosResultHandler implements TiActivityResultHandler,
	Runnable {

		protected int code;
		protected KrollFunction successCallback, cancelCallback, errorCallback;
		protected TiActivitySupport activitySupport;
		protected Intent videosIntent;

		public void run() {
			code = activitySupport.getUniqueResultCode();
			activitySupport.launchActivityForResult(videosIntent, code, this);
		}

		public void onError(Activity activity, int requestCode, Exception e) {
			String msg = "Problem with scanner; " + e.getMessage();
			Log.e(LCAT, "error: " + msg); 
			if (errorCallback != null) {
				errorCallback
					.callAsync((KrollObject)errorCallback,createErrorResponse(UNKNOWN_ERROR, msg));
			}
		}

		public void onResult(Activity activity, int requestCode,
			int resultCode, Intent data) {
			Log.d(LCAT, "onResult() called");

			if (resultCode == Activity.RESULT_CANCELED) {
				Log.d(LCAT, "Videos canceled");
				if (cancelCallback != null) {
					cancelCallback.callAsync((KrollObject)cancelCallback,new KrollDict());
				}
			} else {
				Log.d(LCAT, "Videos successful");
				String result = data
				.getStringExtra(OpentokModuleActivity.EXTRA_RESULT);
				Log.d(LCAT, "Videos result: " + result);
				successCallback.callAsync((KrollObject)successCallback,getDictForResult(result));
			}
		}
	}
}
