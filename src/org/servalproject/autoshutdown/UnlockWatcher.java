package org.servalproject.autoshutdown;

import java.io.DataOutputStream;
import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.util.Log;

public class UnlockWatcher extends BroadcastReceiver {
	static private TurnOff to = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v("RB", "Hit !");

		if (intent.toString().contains("BOOT")) {

			SharedPreferences settings = context.getSharedPreferences(
					ASActivity.PREFS_NAME, 0);
			boolean enabled = settings.getBoolean(ASActivity.ENABLED_NAME,
					false);

			if (enabled) {
				int timer = settings.getInt(ASActivity.TIMER_NAME, 5);

				to = new TurnOff("goodbye", timer, context);
			}
		} else if (to != null) { // means unlock
			Log.v("RB", "Get the unlock");
			to.invalidate();
			to = null;
		} else {
			Log.v("RB", "Lost in translation");
		}
	}

	class TurnOff extends Thread {
		private boolean on = true;
		private int timer;
		private Context context;

		public TurnOff(String name, int timer, Context context) {
			super(name);
			this.timer = timer;
			this.context = context;
			start();
		}

		public void invalidate() {
			on = false;
			Log.v("RB", "Defuse");
		}

		public void run() {
			try {
				Log.v("RB", "Start countdown (" + 60 * timer + 1 + "s)");
				for (int i = 0; i < timer; i++) {
					if (!on)
						break;
					
					// Beep
					MediaPlayer player = MediaPlayer.create(this.context,
							Settings.System.DEFAULT_ALARM_ALERT_URI);
					player.start();
					
					// Wait 1 minute
					Thread.sleep(60000);
				}
				Log.v("RB", "Countdown done !!");

				if (on) {
					Log.v("RB", "turn off");
					shutdown();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		private void shutdown() {
			try {
				Process process = Runtime.getRuntime().exec("su");
				DataOutputStream out = new DataOutputStream(
						process.getOutputStream());
				out.writeBytes("reboot -p\n");
				out.writeBytes("exit\n");
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
