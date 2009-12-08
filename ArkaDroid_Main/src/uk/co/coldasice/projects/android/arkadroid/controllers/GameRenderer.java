package uk.co.coldasice.projects.android.arkadroid.controllers;

import java.text.DecimalFormat;

import uk.co.coldasice.projects.android.ArkaDroid.R;
import uk.co.coldasice.projects.android.arkadroid.sprites.Sprite;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;

public class GameRenderer {
	
	private Paint paint = new Paint();
	private Bitmap imgBackground;
	private GameState gameState;
	private int w;
	private int h;
	private GameLoop gameLoop;
	
	private static final DecimalFormat df = new DecimalFormat("0.000");
		
	public GameRenderer(GameState gameState, GameLoop gameloop, Resources r) {
		this.gameState = gameState;
		this.gameLoop = gameloop;
		paint.setStyle(Paint.Style.FILL);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(1);
		paint.setColor(Color.WHITE);
		imgBackground = BitmapFactory.decodeResource(r, R.drawable.texture);
		DisplayMetrics disp = r.getDisplayMetrics();
		this.w = disp.widthPixels;
		this.h = disp.heightPixels;
	}
	
	public void reset() {
		
	}

	public void render(Canvas canv) {

		canv.drawBitmap(imgBackground, 0, 0, null);
		for (Sprite sp: gameState.bricks) sp.draw(canv);
		gameState.ball.draw(canv);
		gameState.paddle.draw(canv);
		
		if (gameState.isPaused()) {
			paint.setTextSize(25);
			canv.drawText("Press any key to start", 40, h/2, paint);
		}
		else {
			paint.setTextSize(12);
			canv.drawText(gameState.infoText, 10, 14, paint);
			canv.drawText("Speed: " + df.format(gameState.ball.speed()), 80, 14, paint);
			canv.drawText("Score: " + (gameState.currentScore + gameState.getScoreToAdd()), 10, 28, paint);
			canv.drawText("Timediff: " + df.format(gameLoop.timediff), 80, 28, paint);
			canv.drawText("Paddle: " + df.format(gameState.paddle.getPaddleSpeed()) 
					+ " L:"+gameState.paddle.leftPressed
					+ " R:"+gameState.paddle.rightPressed, 160, 14, paint);
		}
		
	}

	public void setSize(int width, int height) {
		this.w = width;
		this.h = height;
		imgBackground = Bitmap.createScaledBitmap(imgBackground, w, h, true);
	}

	public int getW() {
		return w;
	}

	public int getH() {
		return h;
	}

}
