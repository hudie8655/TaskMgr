package com.taskmgr.util;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class HorizontalSlide extends ViewGroup {
	private Scroller scroll;
	private VelocityTracker velocityTrack;
	private int curScreen = 0;
	private final int OFFSET = 600;
	private float lastPos = 0;
	private static ChangeViewListener changeListener;
	private static HorizontalSlide instance;

	public static HorizontalSlide getInstance() {
		return instance;
	}

	public static void setChangeTitleListener(ChangeViewListener listener) {
		changeListener = listener;
	}

	private void init(Context context) {
		scroll = new Scroller(context);
		instance = this;
		// Log.e("","initailization horizontal scroll");
	}

	public HorizontalSlide(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public HorizontalSlide(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public HorizontalSlide(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = getChildAt(curScreen).getWidth();
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
			// Log.e("","onmeasure in for loop");
		}
		scrollTo(curScreen * width, 0);
		// Log.e("", "onmeasure " +curScreen * width);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		int start = 0;
		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			v.layout(start, 0, start + getMeasuredWidth(), getMeasuredHeight());
//			Log.e("", "onlayout width "+getMeasuredWidth() +"  height "+getMeasuredHeight());
			start += getWidth();
		}
		// Log.e("", "onlayout");
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		int action = event.getAction();
		float pos = event.getX();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			// Log.e("", "key down");
			if (!scroll.isFinished()) {
				scroll.abortAnimation();
			}
			if (velocityTrack == null) {
				velocityTrack = VelocityTracker.obtain();
				velocityTrack.addMovement(event);
			}
			lastPos = pos;
			break;
		case MotionEvent.ACTION_MOVE:
			int distance = (int) (lastPos - pos);
			// Log.e("", "key move");
			if (velocityTrack != null) {
				velocityTrack.addMovement(event);
			}
			if (isCanMove(distance)) {
				scrollBy(distance, 0);
			}
			lastPos = pos;
			break;
		case MotionEvent.ACTION_UP:
			// Log.e("", "key up");

			if (velocityTrack != null) {
				velocityTrack.computeCurrentVelocity(1000);
				float velo = velocityTrack.getXVelocity();
				if (velo > OFFSET) {
					gotoScreen(curScreen - 1);
				} else if (velo < -OFFSET) {
					gotoScreen(curScreen + 1);
				} else {
					keepScreen();
				}
				velocityTrack.recycle();
				velocityTrack = null;
			}
			break;
		}
		return true;
	}

	private void keepScreen() {
		// TODO Auto-generated method stub
		int screen = (getScrollX() + getWidth() / 2) / getWidth();
		gotoScreen(screen);
	}

	public void gotoScreen(int i) {
		// TODO Auto-generated method stub
		int screen = (i > getChildCount() - 1) ? getChildCount() - 1 : i;
		if (screen < 0)
			screen = 0;
		int distance = screen * getWidth() - getScrollX();

		scroll.startScroll(getScrollX(), 0, distance, 0);
		invalidate();
		changeListener.changeTitleState(screen);
		curScreen = screen;
		// Log.e("", "scroll screen to " + screen);
	}

	private boolean isCanMove(int distance) {
		if (getScrollX() <= 0 && distance < 0) {
			return false;
		}
		if (getScrollX() >= (getChildCount() - 1) * getWidth() && distance > 0) {
			return false;
		}
		return true;
	}

	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		if (scroll.computeScrollOffset()) {
			scrollTo(scroll.getCurrX(), scroll.getCurrY());
			invalidate();
		}
		super.computeScroll();
	}
}
