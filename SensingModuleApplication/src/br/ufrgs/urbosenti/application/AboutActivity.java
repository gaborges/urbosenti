package br.ufrgs.urbosenti.application;

import br.ufrgs.urbosenti.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class AboutActivity extends Activity {

	private Button btnBack;

	public static final int BACK = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set the activity view
		setContentView(R.layout.activity_about);
		btnBack = (Button) findViewById(R.id.btnBack);
		// back to the previous activity
		btnBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu){
		super.onCreateOptionsMenu(menu);
		menu.add(0,BACK,0,"Back");
		return true;
	}
    
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item){

		switch (item.getItemId()) {
		case BACK:
			finish();
			return true;
		}
		return false;
	}

}
