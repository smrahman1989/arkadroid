package uk.co.coldasice.projects.android.arkadroid.sprites;

import uk.co.coldasice.projects.android.arkadroid.controllers.GameRenderer;
import android.graphics.drawable.Drawable;

public class SpritePaddle extends Sprite_Trail {
	
	private static final double SPEED_DECEL = 1.0;
	private static final double SPEED_ACCEL = 0.5;
	
	private double topSpeed = 4.0;
	private double currentSpeedLeft = 0;
	private double currentSpeedRight = 0;
	public boolean rightPressed = false;
	public boolean leftPressed = false;
	
	public SpritePaddle(Drawable drawable, GameRenderer renderer) {
		super(drawable, renderer);
	}
	
	public void setRightPressed(boolean pressed) {
		rightPressed = pressed;
	}
	
	public void setLeftPressed(boolean pressed) {
		leftPressed = pressed;
	}
	
	public void setSpeed(double speed){
		topSpeed = speed;
	}

	public void update(double timediff) {

		// left pressed?
		if(leftPressed && !rightPressed){
			currentSpeedLeft = adjustPressedSpeed(currentSpeedLeft);
			currentSpeedRight = Math.max(0, currentSpeedRight-SPEED_DECEL);
		}
		// right pressed?
		else if(rightPressed && !leftPressed){
			currentSpeedRight = adjustPressedSpeed(currentSpeedRight);
			currentSpeedLeft = Math.max(0, currentSpeedLeft-SPEED_DECEL);
		}
		// nothing pressed, or both pressed? decelerate the ball towards 0,
		// the move the ball at the new speed in the original direction
		// it was going
		else  {
			// slow down the paddle 
			currentSpeedLeft = Math.max(0, currentSpeedLeft-SPEED_DECEL);
			currentSpeedRight = Math.max(0, currentSpeedRight-SPEED_DECEL);
		}
		setX(x + ((currentSpeedRight-currentSpeedLeft)*timediff));
	}
	
	private double adjustPressedSpeed(double currentSpeed){
		if (currentSpeed < topSpeed) {
			return Math.min(topSpeed,currentSpeed+SPEED_ACCEL);
		}
		if(currentSpeed > topSpeed) return Math.max(0,currentSpeed-SPEED_DECEL);
		return currentSpeed;
		
	}
	
	public void reset() {
		super.reset();
	}

	public double getPaddleSpeed() {
		return currentSpeedRight-currentSpeedLeft;
	}
}
