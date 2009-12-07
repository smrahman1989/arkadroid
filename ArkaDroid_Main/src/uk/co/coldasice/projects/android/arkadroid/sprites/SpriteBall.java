package uk.co.coldasice.projects.android.arkadroid.sprites;

import uk.co.coldasice.projects.android.arkadroid.controllers.GameRenderer;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class SpriteBall extends Sprite {
	/** how many milliseconds between trail paints */ 
	private static final long trailSaveInterval = 50;
	private static final int INITIAL_DXY = 1;

	private final int TRAILLENGTH = 5;
	int[] trailX, trailY;
	private long lastTrailSave;
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
		if (System.currentTimeMillis() - lastTrailSave > trailSaveInterval) {
			for (int i=TRAILLENGTH-1; i>0; i--) {
				trailX[i] = trailX[i-1];
				trailY[i] = trailY[i-1];
			}
			trailX[0] = (int)x;
			trailY[0] = (int)y;
			lastTrailSave = System.currentTimeMillis();
		}
	}

	public double speed() {
		return Math.sqrt((dx * dx) + (dy * dy));
	}
	
	@Override
	protected void preDraw(Canvas canv) {
		for (int i=TRAILLENGTH-1; i>=0; i--) { 
			this.drawable.setBounds(trailX[i], trailY[i], (trailX[i] + (int)w), (trailY[i] + (int)h));
			int alpha = (int)(255d * (TRAILLENGTH - (i+1)) / TRAILLENGTH);
			this.drawable.setAlpha(alpha);
			this.drawable.draw(canv);
			Log.v("Sprite.draw()", "drawing trail. alpha: " + alpha + ", bounds: " + trailX[i] +","+ trailY[i]+","+ (trailX[i] + (int)w)+","+ (trailY[i] + (int)h));
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
