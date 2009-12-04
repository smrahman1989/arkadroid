package uk.co.coldasice.projects.android.arkadroid;

import uk.co.coldasice.projects.android.breakout.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class ArkaDroid extends Activity {

	private static final int MENU_START = 1;
	private ArkaDroidView arkaDroidView;
	private ArkaDroidGameThread gameThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        // turn off the window's title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // tell system to use the layout defined in our XML file
        this.setContentView(R.layout.main);

        // get handles to the LunarView from XML, and its LunarThread
        arkaDroidView = (ArkaDroidView) findViewById(R.id.arkadroid);
        gameThread = arkaDroidView.getGameThread();

        if (savedInstanceState == null) {
            // we were just launched: set up a new game
            // gameThread.setState(LunarThread.STATE_READY);
            Log.w(this.getClass().getName(), "SIS is null");
        } else {
            Log.w(this.getClass().getName(), "sis isn't null. that's odd.");
        }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_START, 0, R.string.menu_start);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
			case MENU_START: gameThread.gameGo(); return true;
		}
		return false;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		arkaDroidView.getGameThread().pause();
	}
	
}
