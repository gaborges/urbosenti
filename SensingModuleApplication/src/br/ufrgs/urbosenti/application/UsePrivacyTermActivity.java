package br.ufrgs.urbosenti.application;

import br.ufrgs.urbosenti.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class UsePrivacyTermActivity extends Activity {

	public static final int BACK = 1;
	public static final int ANSWER_ACCEPTED = 1;
	public static final int ANSWER_REJECTED = 0;
	public static final String ANSWER_KEY = "anwer";
	private Button btnAccept;
	private Button btnReject;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set the activity's layout
		setContentView(R.layout.activity_privacy_term);
		btnAccept = (Button) findViewById(R.id.btnAccept);
		btnReject = (Button) findViewById(R.id.btnReject);
		// action when the button accept is clicked
		btnAccept.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it = new Intent();
				it.putExtra(ANSWER_KEY, ANSWER_ACCEPTED);
				setResult(RESULT_OK,it);
				finish();
			}
		});
		// action when the button reject is clicked
		btnReject.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it = new Intent();
				it.putExtra(ANSWER_KEY, ANSWER_REJECTED);
				setResult(RESULT_OK,it);
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
			// If back is rejected
			Intent it = new Intent();
			it.putExtra(ANSWER_KEY, ANSWER_REJECTED);
			setResult(RESULT_OK,it);
			finish();
			return true;
		}
		return false;
	}

}
