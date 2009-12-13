package uk.co.coldasice.projects.android.arkadroid;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Rendering panel
 * @author seb
 *
 */
public class ArkaDroidView extends SurfaceView implements SurfaceHolder.Callback {

	private ArkaDroidGameThread gameThread;

	public ArkaDroidView(Context context, AttributeSet attrs) {
		super(context, attrs);
		SurfaceHolder holder = this.getHolder();
		holder.addCallback(this);
		
		gameThread = new ArkaDroidGameThread(holder, context);
		
		this.setFocusable(true);
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus); 
		if (!hasWindowFocus) gameThread.pause();
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		gameThread.setSize(width, height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		gameThread.setRunning();
		gameThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean stopped = false;
		gameThread.gameStop();
		while (!stopped) {
			try {
				gameThread.join();
				stopped = true;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return gameThread.keyDown(keyCode, event);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return gameThread.keyUp(keyCode, event);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean handled = gameThread.onTouchEvent(event);
		try { Thread.sleep(20); } catch(Exception e) {}
		return handled;
	}
	
	public ArkaDroidGameThread getGameThread() {
		return gameThread;
	}

}
