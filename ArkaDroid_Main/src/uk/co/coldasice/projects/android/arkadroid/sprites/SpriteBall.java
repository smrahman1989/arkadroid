package uk.co.coldasice.projects.android.arkadroid.sprites;

import uk.co.coldasice.projects.android.arkadroid.controllers.GameRenderer;
import android.graphics.drawable.Drawable;

public class SpriteBall extends Sprite_Trail {
	private static final double INITIAL_DXY = 1.5;

	public double dx = INITIAL_DXY;
	public double dy = INITIAL_DXY;
	
	public SpriteBall(Drawable drawable, GameRenderer renderer) {
		super(drawable, renderer);
	}

	public double speed() {
		return Math.sqrt((dx * dx) + (dy * dy));
	}
	
	@Override
	public void reset() {
		super.reset();
		dx = INITIAL_DXY;
		dy = INITIAL_DXY;
	}
	
}
