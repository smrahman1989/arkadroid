package uk.co.coldasice.projects.mobile.lib;

import java.util.Random;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;

public abstract class SimpleGameCanvas extends GameCanvas implements Runnable {

	protected int h;
	protected int w;
	private boolean doubleBuffered;
	private Graphics buffer;
	private Image bufferImg;
	private Thread thread;
	private Random rand = new Random();
	
	/**
	 * Construct!
	 */
	public SimpleGameCanvas() {
		super(true);
		this.w = getWidth();
		this.h = getHeight() - 1;
		this.doubleBuffered = this.isDoubleBuffered();
		if (!doubleBuffered) {
			bufferImg = Image.createImage(w, h+1);
			buffer = bufferImg.getGraphics();
		}
	}

	/** When the game canvas is hidden, stop the thread. */
	protected void hideNotify() {
		thread = null;
	}

	/** Actually do the rendering */
	protected abstract void render (Graphics g);
	
	protected abstract void prepareGraphics(Graphics g);
	
	/** Run this thread  - start gameloop */
	public final void run() {
		Graphics graphics = getGraphics();
		if (doubleBuffered) prepareGraphics(graphics);
		else prepareGraphics(buffer);
		
		while (thread == Thread.currentThread()) {
			
			gameLoopPreRender();
			
			if (!doubleBuffered) {
				render(buffer);
				graphics.drawImage(bufferImg, 0, 0, Graphics.TOP | Graphics.LEFT);
			}
			else render(graphics);
			flushGraphics();

			gameLoopPostRender();
			
			sleep(getSleepTime());
		}
		
		System.out.println("Bork");
	}
	
	public abstract int getSleepTime();
	
	public abstract void gameLoopPreRender();
	public abstract void gameLoopPostRender();

	/** return a random int between 0 and max */
	protected int rand (int max) {
		int r = Math.abs(rand.nextInt());
		return r % max;
	}

	/** simple method to try/catch a sleep - save on code elsewhere */
	private static final void sleep (long millis) {
		if (millis <= 0) return;
		try {
			Thread.sleep(millis);
		}
		catch (InterruptedException e) { }
	}
	
	/** The gamecanvas has been shown */
	protected void showNotify() {
		thread = new Thread(this);
		thread.start();
	}	
}
