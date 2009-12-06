package uk.co.coldasice.projects.android.arkadroid;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.Log;

public class Sprite {
	/** how many milliseconds between trail paints */ 
	private static final long trailSaveInterval = 50;

	private double h;
	private double w;
	private Drawable drawable;
	private ShapeDrawable colour;
	private double y;
	private double x;
	private ArkaDroidGameThread parent;
	Rect rect = new Rect();
	private boolean killed = false;
	double[] trailX, trailY;
	private long lastTrailSave;
	
	public Sprite(Drawable drawable, ArkaDroidGameThread parent, int brickColour) {
		this.drawable = drawable;
		this.w = drawable.getIntrinsicWidth();
		this.h = drawable.getIntrinsicHeight();
		this.x = 0;
		this.y = 0;
		this.parent = parent;
		if(brickColour!=-1){
			colour = new ShapeDrawable(new RectShape());
			colour.getPaint().setColor(0x66000000+brickColour);
		}
		else {
			// this probably isn't a brick... let's display movement trails.
			trailX = new double[5];
			trailY = new double[5];
		}
	}
	
	public void resetTrails() {
		Log.d("resetTrails()", "reset trails");
		if (trailX != null && trailY != null) {
			for (int i=0; i<trailX.length; i++) {
				trailX[i] = x;
				trailY[i] = y;
			}
		}
	}

	public void updateTrails() {
		if (trailX != null && trailY != null) {
			if (System.currentTimeMillis() - lastTrailSave > trailSaveInterval) {
				for (int i=trailX.length-1; i>0; i--) {
					trailX[i] = trailX[i-1];
					trailY[i] = trailY[i-1];
				}
				trailX[0] = x;
				trailY[0] = y;
				lastTrailSave = System.currentTimeMillis();
			}
		}
	}
	
	public double getW() {
		return w;
	}

	public double getH() {
		return h;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public void draw (Canvas canv) {
		if (this.killed) return;
		if (this.trailX != null && this.trailY != null) {
			Log.v("Sprite.draw()", "starting trails");
			for (int i=trailX.length-1; i>=0; i--) { 
				Rect rect = new Rect((int)trailX[i], (int)trailY[i], (int)(trailX[i] + w), (int)(trailY[i] + h));
				this.drawable.setBounds(rect);
				int alpha = (int)(255d * (trailX.length - (i+1)) / trailX.length);
				this.drawable.setAlpha(alpha);
				this.drawable.draw(canv);
				Log.v("Sprite.draw()", "drawing trail. alpha: " + alpha + ", bounds: " + rect);
			}
		}
		this.drawable.setBounds((int)x, (int)y, (int)(x+w), (int)(y+h));
		this.drawable.setAlpha(255);
		this.drawable.draw(canv);
		if(colour!=null){
			this.colour.setBounds((int)x+3, (int)y+2, (int)(x+w)-3, (int)(y+h)-3);
			this.colour.draw(canv);
		}
	}
	
	public void setX(double x) {
		if (x < 0) this.x = 0;
		else if (x + w > this.parent.getW()) this.setXEdge(this.parent.getW()-1);
		else this.x = x;
	}
	
	public void setY (double y) {
		if (y < 0) this.y = 0;
		else if (y + h > this.parent.getH()) this.setYEdge(this.parent.getH()-1);
		else this.y = y;
	}

	public void setXEdge(double x) {
		this.setX(x - w);
	}
	
	public void setYEdge(double y) {
		this.setY(y - h);
	}
	
	public void setXMiddle(double newX) {
		this.x = newX - (w / 2);
	}
	
	public void setYMiddle(double newY) {
		this.y = newY - (h / 2);
	}
	
	public void setXY (double x, double y) {
		this.setX(x);
		this.setY(y);
	}
	
	public void setXYEdge(double x, double y) {
		this.setXEdge(x);
		this.setYEdge(y);
	}
	
	public void setXYMiddle (double x, double y) {
		this.setXMiddle(x);
		this.setYMiddle(y);
	}
	
	public boolean collidesWith(Sprite sp) {
		return getBounds().intersect(sp.getBounds());
	}

	Rect getBounds() {
		rect.set((int)x, (int)y, (int)(x+w), (int)(y+h));
		return rect;
	}
	
	public void kill() {
		killed = true;
	}
	
	public void unkill() {
		killed  = false;
	}
	
	public boolean dead() {
		return killed;
	}

	public double getMidX() {
		return x + (w/2);
	}
	
}
