package uk.co.coldasice.projects.android.arkadroid;

import java.util.ArrayList;

import uk.co.coldasice.projects.android.breakout.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class ArkaDroidGameThread extends Thread {

	enum State {
		paused, running
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
	private boolean touching = false;
	private int lastTouchX;
	ArrayList<Sprite> bricks;
	private String infoText = "";
	private int livesLeft = MAX_LIVES + 1;

	private boolean winner = false;

	private boolean resetSafe;
	
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
		spriteBall = new Sprite(r.getDrawable(R.drawable.ball), this);
		spritePaddle = new Sprite(r.getDrawable(R.drawable.paddle), this);
		spritePaddle.setXMiddle(w/2);
		spriteBall.setXYMiddle(w/2, 150);
		imgBackground = BitmapFactory.decodeResource(r, R.drawable.daft);
		bricks = new ArrayList<Sprite>();
		Drawable brickImg = r.getDrawable(R.drawable.brick);
		int howManyBricks = w / brickImg.getIntrinsicWidth();
		int brickOffsetX = (w - (howManyBricks * brickImg.getIntrinsicWidth())) / 2;
		int brickOffsetY = 25;
		for (int j=0; j<3; j++) {
			for (int i=0; i < howManyBricks; i++) {
				Sprite brick = new Sprite(brickImg, this);
				brick.setX(i * brick.getW() + brickOffsetX);
				brick.setY(j * brick.getH() + brickOffsetY);
				bricks.add(brick);
			}
		} 
	}

	@Override
	public void run() {
		Canvas canv;
		resetSafe = true;
		resetSprites();
		while (mRun) {
			canv = null;
			try {
				canv = this.holder.lockCanvas();
				synchronized (holder) {
					if (state == State.running) {
						resetSafe = true;
						updateGame();
					}
					else if (state == State.paused) resetSprites();
					render(canv);
				}
			}
			finally {
				if (canv != null) holder.unlockCanvasAndPost(canv);
			}
			Thread.yield();
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

	private void resetSprites() {
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
	}

	private void updateGame() {
		
		if (touching) {
			if (lastTouchX < w/3) paddleDx_mag-=1.2;
			else if (lastTouchX > (w - w/3)) paddleDx_mag += 1.2;
		}
		
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
			if (ballDx < 0) ballDx -= 0.2;
			else ballDx += 0.2;
			if (ballDy < 0) ballDy -= 0.2;
			else ballDy += 0.2;
		}
		
		boolean allDead = true;
		for (Sprite brick: bricks) {
			if (brick.dead()) continue;
			allDead = false;
			if (brick.collidesWith(spriteBall)) {
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

		spriteBall.setX(spriteBall.getX() + ballDx);
		spriteBall.setY(spriteBall.getY() + ballDy);
		// Log.d("ArkaDroidGameThread.updateGame()", "paddleDirection: " + paddleDirection + ", paddleDx_mag: " + paddleDx_mag);
		spritePaddle.setX(spritePaddle.getX() + paddleDx_mag);
		if (paddleDx_mag < 0) paddleDx_mag = Math.min(0, paddleDx_mag + 1);
		else if (paddleDx_mag > 0) paddleDx_mag = Math.max(0, paddleDx_mag - 1);
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
		resetSprites();
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
	
	public boolean keyDown(int keyCode, KeyEvent msg) {
		synchronized (holder) {
			if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
				paddleDx_mag-=2;
				return true;
			}
			else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				paddleDx_mag+=2;
				return true;
			}
			
			return false;
		}
	}
	
	boolean keyUp(int keyCode, KeyEvent msg) {
        boolean handled = false;
        synchronized (holder) {
        	if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                        || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                handled = true;
            }
        }
        return handled;
    }

	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		lastTouchX = (int)event.getX();
		switch (action) {
			case MotionEvent.ACTION_DOWN: {
				touching = true;
				return true;
			}
			case MotionEvent.ACTION_UP: {
				touching  = false;
				return true;
			}
		}
		return false;
	}
	
}
