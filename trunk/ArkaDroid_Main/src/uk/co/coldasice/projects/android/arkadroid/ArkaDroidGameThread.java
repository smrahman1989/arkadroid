package uk.co.coldasice.projects.android.arkadroid;

import uk.co.coldasice.projects.android.arkadroid.controllers.GameLoop;
import uk.co.coldasice.projects.android.arkadroid.controllers.GameRenderer;
import uk.co.coldasice.projects.android.arkadroid.controllers.GameState;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class ArkaDroidGameThread extends Thread {

	private boolean mRun = true;
	private SurfaceHolder holder;

	private boolean resetSafe;

	private long nextRender;
	private final double PADDLE_SPEED = 4.0;
	private final int RENDER_EVERY_MS = 40;
	private final int SLEEP_FOR_MS = 10;

	private GameRenderer renderer;

	private GameLoop gameloop;

	private GameState gameState;
	
	
	public ArkaDroidGameThread(SurfaceHolder holder, Context context) {
		this.holder = holder;
		Resources r = context.getResources();
		this.gameState = new GameState();
		this.gameloop = new GameLoop(gameState);
		this.renderer = new GameRenderer(gameState, gameloop, r);
		gameState.init(r, renderer);
	}

	@Override
	public void run() {
		Canvas canv;
		resetSafe = true;
		reset();
		while (mRun) {
			canv = null;
			try {
				if (gameloop.lastupdate > nextRender) canv = this.holder.lockCanvas();
				synchronized (holder) {
					if (gameState.isRunning()) {
						resetSafe = true;
						gameloop.updateGame(renderer);
					}
					else if (gameState.isPaused()) reset();
					if (canv != null){
						renderer.render(canv);
						gameState.ball.updateTrails();
						nextRender = gameloop.lastupdate + RENDER_EVERY_MS;
					}
				}
			}
			finally {
				if (canv != null) holder.unlockCanvasAndPost(canv);
			}
			try { Thread.sleep(SLEEP_FOR_MS); } catch (InterruptedException e) {	}
		}
	}
	

	private void reset() {
		if (!resetSafe) return;
		gameState.reset();
		renderer.reset();
		gameloop.reset();
	}

	public void pause() {
		gameState.setPaused();
	}

	public void gameStop() {
		gameState.setPaused();
		mRun = false;
	}

	public void setRunning() {
		mRun  = true;
	}
	
	public void gameGo() {
		gameState.setRunning();
		resetSafe = true;
		reset();
	}

	public void setSize(int width, int height) {
		synchronized (holder) {
			renderer.setSize(width, height);	
		}
	}

	public int getW() {
		synchronized (holder) {
			return renderer.getW();
		}
	}
	
	public int getH() {
		synchronized (holder) {
			return renderer.getH();
		}
	}
	
	public void changedLeft(double speed, boolean pressed) {
		synchronized (holder) {
			this.gameState.paddle.setSpeed(speed);
			this.gameState.paddle.setLeftPressed(pressed);
		}
	}
	
	public void changedRight(double speed, boolean pressed) {
		synchronized (holder) {
			this.gameState.paddle.setSpeed(speed);
			this.gameState.paddle.setRightPressed(pressed);
		}
	}
	
	boolean keyUp(int keyCode, KeyEvent msg) {
		boolean handled = false;
		if (gameState.isPaused()) {
			gameGo();
			handled = true;
		}
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
			changedLeft(PADDLE_SPEED,false);
			handled = true;
		}
		else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
			changedRight(PADDLE_SPEED,false);
			handled = true;
		}
		return handled;
	}

	public boolean keyDown(int keyCode, KeyEvent msg) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			changedLeft(PADDLE_SPEED,true);
			return true;
		}
		else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			changedRight(PADDLE_SPEED,true);
			return true;
		}
		
		return false;
	}

	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int width = getW();
		switch (action) {
			case MotionEvent.ACTION_DOWN: {
				int where = (int)event.getX();
				if (where < width/3) changedLeft(PADDLE_SPEED,true);
				else if (where > (width - width/3)) changedRight(PADDLE_SPEED,true);
				return true;
			}
			case MotionEvent.ACTION_UP: {
				changedLeft(PADDLE_SPEED,false);
				changedRight(PADDLE_SPEED,false);
				return true;
			}
			case MotionEvent.ACTION_MOVE: {
				changedLeft(PADDLE_SPEED,false);
				changedRight(PADDLE_SPEED,false);
				int where = (int)event.getX();
				if (where < width/3) changedLeft(PADDLE_SPEED,true);
				else if (where > (width - width/3)) changedRight(PADDLE_SPEED,true);
				return true;
			}
		}
		return false;
	}
	
}
