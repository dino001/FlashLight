package com.oaktree.flashlight;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends Activity {

	ImageButton btnSwitch;

	private Camera camera;
	private boolean isFlashOn;
	private boolean isResumeFlashOn;
	private boolean hasFlash;
	Parameters params;
	MediaPlayer mp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btnSwitch = (ImageButton) findViewById(R.id.btnSwitch);

		btnSwitch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isFlashOn) {
					turnOffFlash();
				} else {
					turnOnFlash();
				}
			}
		});

		// First check if device is supporting flashlight or not
		hasFlash = getApplicationContext().getPackageManager()
				.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

		if (!hasFlash) {
			// device doesn't support flash
			// Show alert message and close the application
			AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
					.create();
			alert.setTitle("Error");
			alert.setMessage("Sorry, your device doesn't support flash light!");
			alert.setButton(0, "OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// closing the application
					finish();
				}
			});
			alert.show();
			return;
		}

		// get the camera
		getCamera();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// getting camera parameters
	private void getCamera() {
		if (camera == null) {
			try {
				camera = Camera.open();
				params = camera.getParameters();
			} catch (RuntimeException e) {
				Log.e("Camera Error. Failed to Open. Error: ", e.getMessage());
			}
		}
	}

	/*
	 * Turning On flash
	 */
	private void turnOnFlash() {
		if (!isFlashOn) {
			if (camera == null || params == null) {
				return;
			}
			// play sound
			// playSound();

			params = camera.getParameters();
			params.setFlashMode(Parameters.FLASH_MODE_TORCH);
			camera.setParameters(params);
			camera.startPreview();
			isFlashOn = true;

			// changing button/switch image
			toggleButtonImage();
		}

	}

	private void closeApp(){
		finish();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		 // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_close:
	            closeApp();
	            return true;	        
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	/*
	 * Turning Off flash
	 */
	private void turnOffFlash() {
		if (isFlashOn) {
			if (camera == null || params == null) {
				return;
			}
			// play sound
			// playSound();

			params = camera.getParameters();
			params.setFlashMode(Parameters.FLASH_MODE_OFF);
			camera.setParameters(params);
			camera.stopPreview();
			isFlashOn = false;

			// changing button/switch image
			toggleButtonImage();
		}
	}
		
	/*
	 * Toggle switch button images changing image states to on / off
	 */
	private void toggleButtonImage() {		
		if (isFlashOn) {		
			btnSwitch.setBackgroundResource(R.drawable.button_on);			
		} else {			
			btnSwitch.setBackgroundResource(R.drawable.button_off);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		// on pause turn off the flash but keep the flag on
		if (isFlashOn){				
			turnOffFlash();
			isResumeFlashOn = true;			
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		// on resume turn on the flash if the flag is on
		if (hasFlash && isResumeFlashOn){
			turnOnFlash();
			isResumeFlashOn = false;
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		// on starting the app get the camera params
		getCamera();
	}

	@Override
	protected void onStop() {
		super.onStop();

		// on stop release the camera
		if (camera != null) {
			camera.release();
			camera = null;
		}
	}

}
