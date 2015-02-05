package br.ufrgs.urbosenti;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	public static final int NOVO = 0;
	public static final int SAIR = 1;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button buttonOk = (Button) findViewById(R.id.btnLogin);
		buttonOk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//dd;
			}
		});
	}

	 @Override
		public boolean onCreateOptionsMenu(android.view.Menu menu){
			super.onCreateOptionsMenu(menu);
			menu.add(0,NOVO,0,"Novo");
			menu.add(0,SAIR,0,"Voltar");
			return true;
		}
	    
		@Override
		public boolean onMenuItemSelected(int featureId, MenuItem item){

			switch (item.getItemId()) {
			case NOVO:
				//Intent it = new Intent(getBaseContext(), PessoasEditActivity.class);
				//it.putExtra("acao","I");
				//startActivity(it);	
				return true;
			case SAIR:
				finish();
				return true;
			}
			return false;
		}
}
