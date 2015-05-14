package br.ufrgs.urbosenti.application;

import br.ufrgs.urbosenti.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ApplicationEnvironmentActivity extends Activity {

	private Button btnBack;
	private Button btnService;
	private TextView txtApplicationUID;
	private TextView txtApplicationStatus;
	private TextView txtRemoteServerStatus;
	private TextView txtServiceStatus;
	
	public static final int BACK = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set the activity view
		setContentView(R.layout.activity_application_environment);
		// relate the Layout's Element with the object
		btnBack = (Button) findViewById(R.id.btnBack);
		btnService = (Button) findViewById(R.id.btnStartService);
		txtApplicationUID = (TextView) findViewById(R.id.txtApplicationUID);
		txtApplicationStatus = (TextView) findViewById(R.id.txtApplicationStatus);
		txtRemoteServerStatus = (TextView) findViewById(R.id.txtRemoteServerStatus);
		txtServiceStatus = (TextView) findViewById(R.id.txtServiceStatus);
		// back to the previous activity
		btnBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// execute or stop the service
		btnService.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Execute the service
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, BACK, 0, "Back");
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch (item.getItemId()) {
		case BACK:
			finish();
			return true;
		}
		return false;
	}
}