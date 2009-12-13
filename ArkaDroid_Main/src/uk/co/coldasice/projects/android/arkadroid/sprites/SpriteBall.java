package uk.co.coldasice.projects.android.arkadroid.sprites;

import uk.co.coldasice.projects.android.arkadroid.controllers.GameRenderer;
import android.graphics.drawable.Drawable;

public class SpriteBall extends Sprite_Trail {
	private static final double INITIAL_DXY = 1.5;
	public static final double MAX_SPEED = 7;

	public double dx = INITIAL_DXY;
	public double dy = INITIAL_DXY;
	
	public SpriteBall(Drawable drawable, GameRenderer renderer) {
		super(drawable, renderer);
		reset();
	}

	public double speed() {
		return speed(dx,dy);
	}
	
	public static double speed(double _dx, double _dy) {
		return Math.sqrt((_dx * _dx) + (_dy * _dy));
	}
	
	public void reset() {
		super.reset();
		dx = INITIAL_DXY;
		dy = INITIAL_DXY;
		setXYMiddle(renderer.w/2,renderer.h/2);
	}
	
}
