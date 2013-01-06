package com.phc.opentok;

import android.content.Context;
import android.graphics.Color;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import com.phc.opentok.MainView;

public class VideosView extends RelativeLayout {

	private final SurfaceView publisherView;
	private final SurfaceView subscriberView;
	private final MainView mainView;

	public VideosView(final Context context) {
		super(context);

		publisherView = new SurfaceView(getContext());
		subscriberView = new SurfaceView(getContext());

		android.widget.RelativeLayout.LayoutParams params1 = new android.widget.RelativeLayout.LayoutParams(640, 480);
        params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params1.addRule(RelativeLayout.CENTER_VERTICAL);
	    publisherView.setLayoutParams(params1);
		android.widget.RelativeLayout.LayoutParams params2 = new android.widget.RelativeLayout.LayoutParams(640, 480);
        params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params2.addRule(RelativeLayout.CENTER_VERTICAL);
	    subscriberView.setLayoutParams(params2);
		addView(publisherView);
		addView(subscriberView);

		mainView = new MainView(getContext());
		LayoutParams params3 = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		mainView.setLayoutParams(params3);
		mainView.setBackgroundColor(Color.TRANSPARENT);
		addView(mainView);
	}

	public SurfaceView getPublisherView() {
		return publisherView;
	}

	public SurfaceView getSubscriberView() {
		return subscriberView;
	}

	public MainView getMainView() {
		return mainView;
	}

}