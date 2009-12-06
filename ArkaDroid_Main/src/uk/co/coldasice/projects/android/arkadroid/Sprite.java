package uk.co.coldasice.projects.android.arkadroid;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

public class Sprite {

	private double h;
	private double w;
	private Drawable drawable;
	private ShapeDrawable colour;
	private double y;
	private double x;
	private ArkaDroidGameThread parent;
	Rect rect = new Rect();
	private boolean killed = false;

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
		this.drawable.setBounds((int)x, (int)y, (int)(x+w), (int)(y+h));
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
