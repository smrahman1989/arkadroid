package uk.co.coldasice.projects.android.arkadroid.sprites;

import uk.co.coldasice.projects.android.arkadroid.controllers.GameRenderer;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class SpritePaddle extends Sprite {
	
	public double paddleDx_mag = 0;
	public boolean rightPressed = false;
	public boolean leftPressed = false;
	
	public SpritePaddle(Drawable drawable, GameRenderer renderer) {
		super(drawable, renderer);
	}
	
	public void setSpeed(double speed) {
		paddleDx_mag = speed;
	}
	
	public void setRightPressed(boolean pressed) {
		rightPressed = pressed;
	}
	
	public void setLeftPressed(boolean pressed) {
		leftPressed = pressed;
	}

	@Override
	protected void postDraw(Canvas canv) { }

	@Override
	protected void preDraw(Canvas canv) { }

	public void update(double timediff) {
		if(leftPressed && !rightPressed){
			setX(x - (paddleDx_mag*timediff));
		}else if(rightPressed && !leftPressed){
			setX(x + (paddleDx_mag*timediff));
		}		
	}
}
