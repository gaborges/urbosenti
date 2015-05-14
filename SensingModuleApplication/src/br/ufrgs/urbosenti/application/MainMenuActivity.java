package br.ufrgs.urbosenti.application;

import br.ufrgs.urbosenti.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainMenuActivity extends Activity {

	private Button btnApplicationEnvironment;
	private Button btnExit;
	private Button btnAbout;
	public static final int EXIT = 1;
	public static final int PRIVACY_TERM_RC = 654;
	public boolean usePrivacyTermHasAccepted;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Add the activity's layout
		setContentView(R.layout.activity_main_menu);
		// match both objects and views
		btnApplicationEnvironment = (Button) findViewById(R.id.btnApplicationEnvironment);
		btnExit = (Button) findViewById(R.id.btnExit);
		btnAbout = (Button) findViewById(R.id.btnAbout);
		// Exit the aplication
		btnExit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// access the information about the application and the project
		btnAbout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getBaseContext(),AboutActivity.class));
			}
		});
		// access the application environment
		btnApplicationEnvironment.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getBaseContext(),ApplicationEnvironmentActivity.class));
			}
		});
		
		// check if the user have accepted the use and privacy term.
		usePrivacyTermHasAccepted = false;
		// ************** make after ***************** if is not accepted, then open the term view. Otherwise do nothing.
		if(!usePrivacyTermHasAccepted){
			startActivityForResult(new Intent(getBaseContext(),UsePrivacyTermActivity.class),PRIVACY_TERM_RC);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu){
		super.onCreateOptionsMenu(menu);
		menu.add(0,EXIT,0,"Exit");
		return true;
	}
    
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item){

		switch (item.getItemId()) {
		case EXIT:
			finish();
			return true;
		}
		return false;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intentRetornada) {
		// Código de requisição retornado da tela de IMC
		if (requestCode == PRIVACY_TERM_RC) {
			// Código de resultado retornado
			if (resultCode == RESULT_OK) {
				// If was accepted the term show a Toast else exit the application
				if(intentRetornada.getIntExtra(UsePrivacyTermActivity.ANSWER_KEY, UsePrivacyTermActivity.ANSWER_REJECTED)
						== UsePrivacyTermActivity.ANSWER_ACCEPTED){
					// Toast
					Toast.makeText(getBaseContext(), "Thank you for accepting. Now, you could go to application environment and start the service.", Toast.LENGTH_LONG).show();
					// set the flag as true
					this.usePrivacyTermHasAccepted = true;
					// Save on the data base that. Or maybe when the server is stated
				} else {
					// finish the application
					finish();
				}
			}
		}
	}
}