package org.servalproject.autoshutdown;

import java.io.DataOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class ASActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	public static final String PREFS_NAME = "as_prefs";
	public static final String TIMER_NAME = "timer";
	public static final String ENABLED_NAME = "enabled";

	private SharedPreferences settings;

	private CheckBox check;
	private EditText text;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Loat the preferences
		settings = getSharedPreferences(PREFS_NAME, 2);
		boolean enabled = settings.getBoolean(ENABLED_NAME, false);
		int timer = settings.getInt(TIMER_NAME, 5);

		// Write'em
		check = (CheckBox) findViewById(R.id.autosd_check_enable);
		check.setChecked(enabled);

		text = (EditText) findViewById(R.id.autosd_time);
		text.setText("" + timer);
		
		Button validate = (Button) findViewById(R.id.button1);
		validate.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// Get the preferences
		boolean enabled = check.isChecked();
		int timer = Integer.parseInt(text.getText().toString());

		// Save'em
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(ENABLED_NAME, enabled);
		editor.putInt(TIMER_NAME, timer);
		editor.commit();
		
		// Signal to the user
		Toast.makeText(this.getBaseContext(), R.string.autosd_done, Toast.LENGTH_LONG).show();
		
		// Check for the root access
		try {
			Process process = Runtime.getRuntime().exec("su");
			DataOutputStream out = new DataOutputStream(
					process.getOutputStream());
			out.writeBytes("cat /dev/null\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}
}