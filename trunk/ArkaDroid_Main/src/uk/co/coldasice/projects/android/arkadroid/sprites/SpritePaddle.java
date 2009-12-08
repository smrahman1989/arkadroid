package uk.co.coldasice.projects.android.arkadroid.sprites;

import uk.co.coldasice.projects.android.arkadroid.controllers.GameRenderer;
import android.graphics.drawable.Drawable;

public class SpritePaddle extends Sprite_Trail {
	
	private static final int LASTPRESSED_LEFT = -1;
	private static final int LASTPRESSED_RIGHT = 1;
	private static final int LASTPRESSED_NONE = 0;
	private static final double SPEED_DECEL = 0.1;
	private static final double SPEED_ACCEL = 0.1;
	
	private double currentSpeed = 0;
	private double desiredSpeed = 0;
	public boolean rightPressed = false;
	public boolean leftPressed = false;
	private int lastPressed = LASTPRESSED_NONE;
	
	public SpritePaddle(Drawable drawable, GameRenderer renderer) {
		super(drawable, renderer);
	}
	
	public void setSpeed(double speed) {
		desiredSpeed = speed;
		// currentSpeed = 0;
	}
	
	public void setRightPressed(boolean pressed) {
		rightPressed = pressed;
	}
	
	public void setLeftPressed(boolean pressed) {
		leftPressed = pressed;
	}

	public void update(double timediff) {
		// accelerate the ball towards desired speed, if just one direction is pressed
		if (leftPressed ^ rightPressed && desiredSpeed > currentSpeed) {
			currentSpeed += (desiredSpeed - currentSpeed) * SPEED_ACCEL;
		}
		// left pressed?
		if(leftPressed && !rightPressed){
			setX(x - (currentSpeed*timediff));
			lastPressed = LASTPRESSED_LEFT;
		}
		// right pressed?
		else if(rightPressed && !leftPressed){
			setX(x + (currentSpeed*timediff));
			lastPressed = LASTPRESSED_RIGHT;
		}
		// nothing pressed, or both pressed? decelerate the ball towards 0,
		// the move the ball at the new speed in the original direction
		// it was going
		else  {
			// slow down the paddle 
			if (currentSpeed > 0) {
				currentSpeed -= currentSpeed * SPEED_DECEL;
			}
			setX(x + lastPressed * (currentSpeed*timediff));
		}
	}
	
	public void reset() {
		super.reset();
		lastPressed = LASTPRESSED_NONE;
	}

	public double getPaddleSpeed() {
		return currentSpeed;
	}
}
