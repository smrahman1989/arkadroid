package uk.co.coldasice.projects.android.arkadroid;

import java.util.ArrayList;
import java.util.Random;

import uk.co.coldasice.projects.android.ArkaDroid.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class ArkaDroidGameThread extends Thread {

	enum State {
		paused, running
	}
	
	public enum Moving {
		NO, LEFT, RIGHT
	}

	private static final int MAX_LIVES = 5;
	
	private boolean mRun = true;
	private SurfaceHolder holder;
	private Context context;
	private Bitmap imgBackground;
	private int w;
	private int h;
	private Sprite spriteBall;
	private Sprite spritePaddle;
	private double paddleDx_mag = 0;
	private double ballDx = 3;
	private double ballDy = 3;
	State state = State.paused;
	private Moving moving = Moving.NO;
	ArrayList<Sprite> bricks;
	private String infoText = "";
	private int livesLeft = MAX_LIVES + 1;

	private boolean winner = false;

	private boolean resetSafe;
	
	private long lastupdate;
	private final double PHYSICS_SPEED = 13.0;
	private final double PADDLE_SPEED = 4.0;
	
	private final Random random = new Random();
	
	public ArkaDroidGameThread(SurfaceHolder holder, Context context) {
		this.holder = holder;
		this.context = context;
		DisplayMetrics disp = this.context.getResources().getDisplayMetrics();
		this.w = disp.widthPixels;
		this.h = disp.heightPixels;
		setupImages();
	}
	
	private void setupImages() {
		Resources r = this.context.getResources();
		spriteBall = new Sprite(r.getDrawable(R.drawable.ball), this, -1);
		spritePaddle = new Sprite(r.getDrawable(R.drawable.paddle), this, -1);
		spritePaddle.setXMiddle(w/2);
		spriteBall.setXYMiddle(w/2, 150);
		imgBackground = BitmapFactory.decodeResource(r, R.drawable.texture);
		bricks = new ArrayList<Sprite>();
		Drawable brickImg = r.getDrawable(R.drawable.brick);
		int paddingTop = 40;
		int paddingSides = 60;
		int howManyBricks = (w-paddingSides) / brickImg.getIntrinsicWidth();
		int brickOffsetX = (w - (howManyBricks * brickImg.getIntrinsicWidth())) / 2;
		for (int j=0; j<5; j++) {
			for (int i=0; i < howManyBricks; i++) {
				Sprite brick = new Sprite(brickImg, this, random.nextInt(0x00FFFFFF));
				brick.setX(i * brick.getW() + brickOffsetX);
				brick.setY((j * brick.getH()) + paddingTop);
				bricks.add(brick);
			}
		} 
	}

	@Override
	public void run() {
		Canvas canv;
		resetSafe = true;
		reset();
		while (mRun) {
			canv = null;
			try {
				canv = this.holder.lockCanvas();
				synchronized (holder) {
					if (state == State.running) {
						resetSafe = true;
						updateGame();
					}
					else if (state == State.paused) reset();
					render(canv);
				}
			}
			finally {
				if (canv != null) holder.unlockCanvasAndPost(canv);
			}
			//try { Thread.sleep(10); } catch (InterruptedException e) {	}
		}
	}

	private void render(Canvas canv) {
		canv.drawBitmap(imgBackground, 0, 0, null);
		for (Sprite sp: bricks) sp.draw(canv);
		spriteBall.draw(canv);
		spritePaddle.draw(canv);
		
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setAntiAlias(true);
		paint.setTextSize(20);
		paint.setStrokeWidth(1);
		paint.setColor(Color.WHITE);
		canv.drawText(infoText, 10, 20, paint);
	}

	private void reset() {
		if (!resetSafe) return;
		boolean fullReset = false;
		if (state != State.paused) livesLeft--;
		if (livesLeft <= 0) {
			fullReset = true;
			infoText = "You died";
			livesLeft = MAX_LIVES;
		}
		else {
			if (winner) {
				fullReset = true;
				infoText = "YOU WON!";
				livesLeft = MAX_LIVES;
			}
			else infoText = "Lives left: " + livesLeft;
			
		}
		spritePaddle.setYEdge(h-5);
		spritePaddle.setXMiddle(w/2);
		spriteBall.setXYMiddle(w/2, 150);
		if (fullReset) {
			for (Sprite sp: bricks) {
				sp.unkill();
			}
		}
		ballDx = 1;
		ballDy = 1;
		lastupdate = System.currentTimeMillis();
	}

	private void updateGame() {
		
		double timediff = (System.currentTimeMillis() - (double)lastupdate) / PHYSICS_SPEED;
		lastupdate = System.currentTimeMillis();
		
		if (spriteBall.collidesWith(spritePaddle)) {
			double ballMidx = spriteBall.getMidX();
			double paddleMidx = spritePaddle.getMidX();
			
			// get the diff between the mid points
			double diff = paddleMidx - ballMidx;
			if (paddleMidx - ballMidx == 0) {
				 ballDy *= -1;
			}
			else {
				// if it's <0, that means spin the ball left a bit
				double maxSpin = spritePaddle.getW() / 2;
				double spin = diff / maxSpin;
				// get the original ball speed
				double speed = Math.sqrt((ballDx * ballDx) + (ballDy * ballDy));
				double shootAngle = 90 - (90 * spin);
				double new_ballDy = Math.sin(Math.toRadians(shootAngle)) * speed;
				double new_ballDx = 0 - Math.cos(Math.toRadians(shootAngle)) * speed;
				
				ballDx = new_ballDx;
				if (new_ballDy > 0) new_ballDy = -new_ballDy;
				ballDy = new_ballDy;
				// bump up 1 pix to stop recollision
				spriteBall.setY(spriteBall.getY() - 1);
			}
			// speed up the ball a bit
			double ballDiff = (0.2 * timediff);
			if (ballDx < 0) ballDx -= ballDiff;
			else ballDx += ballDiff;
			if (ballDy < 0) ballDy -= ballDiff;
			else ballDy += ballDiff;
		}
		
		boolean allDead = true;
		for (Sprite brick: bricks) {
			if (brick.dead()) continue;
			allDead = false;
			if (brick.collidesWith(spriteBall)) {
				// don't change the format of this; it's used in the j2se experiments bit
				 Log.d("updateGame() - deadbrick", "ballPos: " + spriteBall.getBounds() + ", brickPos: " + brick.getBounds() + ", ballDirection: " + ballDx + ", " + ballDy);
				ballDy *= -1;
				brick.kill();
				break;
			}
		}
		
		if (allDead) {
			winner = true;
			pause();
		}
		
		if (ballDx > 0 && (spriteBall.getX() + spriteBall.getW() + ballDx >= w)) ballDx *= -1;
		else if (ballDx < 0 && (spriteBall.getX() + ballDx <= 0)) ballDx *= -1;
		
		if (ballDy < 0 && (spriteBall.getY() + ballDy <= 0)) ballDy *= -1;
		// ball died?
		if (ballDy > 0 && (spriteBall.getY() + spriteBall.getH() > spritePaddle.getY() + 2)) {
			pause();
		}

		spriteBall.setX(spriteBall.getX() + (ballDx*timediff));
		spriteBall.setY(spriteBall.getY() + (ballDy*timediff));
		// Log.d("ArkaDroidGameThread.updateGame()", "paddleDirection: " + paddleDirection + ", paddleDx_mag: " + paddleDx_mag);
		
		if(moving==Moving.LEFT){
			spritePaddle.setX(spritePaddle.getX() - (paddleDx_mag*timediff));
			//paddleDx_mag = Math.max(0, paddleDx_mag - 1);
		}else if(moving==Moving.RIGHT){
			spritePaddle.setX(spritePaddle.getX() + (paddleDx_mag*timediff));
			//paddleDx_mag = Math.min(0, paddleDx_mag + 1);
		}
	}

	public void pause() {
		state = State.paused;
	}

	public void gameStop() {
		state = State.paused;
		mRun = false;
	}

	public void setRunning() {
		mRun  = true;
	}
	
	public void gameGo() {
		state = State.running;
		reset();
	}

	public void setSize(int width, int height) {
		synchronized (holder) {
			this.w = width;
			this.h = height;
			imgBackground = Bitmap.createScaledBitmap(imgBackground, w, h, true);
		}
	}

	public int getW() {
		synchronized (holder) {
			return w;	
		}
	}
	
	public int getH() {
		synchronized (holder) {
			return h;	
		}
	}
	
	public void MovePaddle(Moving moving, double speed) {
		synchronized (holder) {
			this.moving = moving;
			paddleDx_mag = speed;
			//Log.d("MovePaddle", moving+" "+speed);
		}
	}
	
	boolean keyUp(int keyCode, KeyEvent msg) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
				|| keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			MovePaddle(Moving.NO, 0);
			return true;
		}
		return false;
	}

	public boolean keyDown(int keyCode, KeyEvent msg) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			MovePaddle(Moving.LEFT, PADDLE_SPEED);
			return true;
		}
		else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			MovePaddle(Moving.RIGHT, PADDLE_SPEED);
			return true;
		}
		
		return false;
	}

	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int where = (int)event.getX();
		switch (action) {
			case MotionEvent.ACTION_DOWN: {
				if (where < w/3) MovePaddle(Moving.LEFT, PADDLE_SPEED);
				else if (where > (w - w/3)) MovePaddle(Moving.RIGHT, PADDLE_SPEED);
				return true;
			}
			case MotionEvent.ACTION_UP: {
				MovePaddle(Moving.NO, 0);
				return true;
			}
		}
		return false;
	}
	
}
