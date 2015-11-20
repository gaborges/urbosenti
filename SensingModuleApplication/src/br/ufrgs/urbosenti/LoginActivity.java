package br.ufrgs.urbosenti;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import br.ufrgs.urbosenti.android.SQLiteAndroidDatabaseHelper;
import br.ufrgs.urbosenti.application.ConcreteApplicationHandler;
import br.ufrgs.urbosenti.application.MainMenuActivity;
import urbosenti.core.communication.interfaces.WiredCommunicationInterface;
import urbosenti.core.device.DeviceManager;

public class LoginActivity extends Activity {

	public static final int NOVO = 0;
	public static final int SAIR = 1;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		Button buttonOk = (Button) findViewById(R.id.btnLogin);
		InputStream io;
		try {
			DeviceManager deviceManager = new DeviceManager(); // Objetos Core já estão incanciados internamente
	        //deviceManager.enableAdaptationComponent(); // Habilita componente de adaptação
	       
	        // Adicionar as interfaces de comunicação suportadas --- Inicialmente manual. Após adicionar um processo automático
	        deviceManager.addSupportedCommunicationInterface(new WiredCommunicationInterface());
	        ConcreteApplicationHandler handler = new ConcreteApplicationHandler(deviceManager);
	        Log.d("LOG", "adicionou comunicação e o Application Handle");
						
			deviceManager.setDeviceKnowledgeRepresentationModel(getAssets().open("deviceKnowledgeModel.xml"), "xmlInputStream");
			Log.d("LOG", "Adicionou o conhecimento ");
			SQLiteAndroidDatabaseHelper dbHelper = new SQLiteAndroidDatabaseHelper(
					deviceManager.getDataManager(), 
					getBaseContext());

			
			//dbHelper.createDatabase();
			// Log.d("LOG", "Criou o DB Handler ");
			 //deviceManager.getDataManager().setUrboSentiDatabaseHelper(dbHelper);
			 Log.d("LOG", "Colocou lá ");
			 dbHelper.createDatabase();
				io = getAssets().open("deviceKnowledgeModel.xml");
				dbHelper.loadingGeneralDefinitions(io);
				dbHelper.loadingAgentModels(io);
				dbHelper.loadingDevice(getAssets().open("deviceKnowledgeModel.xml"));

				 Log.d("LOG", "Leu ao menos ");
;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d("ERRO",e.getMessage());
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			Log.d("ERRO",e.getMessage());
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			Log.d("ERRO",e.getMessage());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.d("ERRO",e.getMessage());
		}
		buttonOk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getBaseContext(),MainMenuActivity.class));
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
