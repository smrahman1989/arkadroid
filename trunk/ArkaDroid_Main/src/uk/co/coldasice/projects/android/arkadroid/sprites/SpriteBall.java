package uk.co.coldasice.projects.android.arkadroid.sprites;

import uk.co.coldasice.projects.android.arkadroid.controllers.GameRenderer;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class SpriteBall extends Sprite {
	private static final double INITIAL_DXY = 1.5;

	private final int TRAILLENGTH = 5;
	private int latestTrail = 0;
	int[] trailX, trailY;
	public double dx = INITIAL_DXY;
	public double dy = INITIAL_DXY;
	
	public SpriteBall(Drawable drawable, GameRenderer renderer) {
		super(drawable, renderer);
		// this probably isn't a brick... let's display movement trails.
		trailX = new int[TRAILLENGTH];
		trailY = new int[TRAILLENGTH];
	}

	public void resetTrails() {
		for (int i=0; i<TRAILLENGTH; i++) {
			trailX[i] = (int)x;
			trailY[i] = (int)y;
		}
	}
	
	public void updateTrails() {
		if(++latestTrail==TRAILLENGTH)latestTrail=0;
		trailX[latestTrail] = (int)x;
		trailY[latestTrail] = (int)y;
	}

	public double speed() {
		return Math.sqrt((dx * dx) + (dy * dy));
	}
	
	@Override
	protected void preDraw(Canvas canv) {
		for (int i=latestTrail,j=0;j<TRAILLENGTH;i=i>0?i-1:TRAILLENGTH-1,j++) {
			this.drawable.setBounds(trailX[i], trailY[i], (trailX[i] + (int)w), (trailY[i] + (int)h));
			int alpha = (int)(255d * (TRAILLENGTH - (j+1)) / TRAILLENGTH);
			this.drawable.setAlpha(alpha);
			this.drawable.draw(canv);
		}
	}

	@Override
	protected void postDraw(Canvas canv) {
		// nothing here...
	}

	public void reset() {
		resetTrails();
		dx = INITIAL_DXY;
		dy = INITIAL_DXY;
	}

}
