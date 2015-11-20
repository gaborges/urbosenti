package br.ufrgs.urbosenti.application;

import br.ufrgs.urbosenti.R;
import br.ufrgs.urbosenti.android.SQLiteAndroidDatabaseHelper;
import urbosenti.core.communication.interfaces.WiredCommunicationInterface;
import urbosenti.core.device.DeviceManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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
				// Instanciar componentes -- Device manager e os demais onDemand
		        DeviceManager deviceManager = new DeviceManager(); // Objetos Core já estão incanciados internamente
		        //deviceManager.enableAdaptationComponent(); // Habilita componente de adaptação
		       
		        // Adicionar as interfaces de comunicação suportadas --- Inicialmente manual. Após adicionar um processo automático
		        deviceManager.addSupportedCommunicationInterface(new WiredCommunicationInterface());
		        ConcreteApplicationHandler handler = new ConcreteApplicationHandler(deviceManager);
		        Log.d("LOG", "adicionou comunicação e o Application Handle");
		        try {
		       		deviceManager.setDeviceKnowledgeRepresentationModel(getAssets().open("deviceKnowledgeModel.xml"), "xmlInputStream");
					Log.d("LOG", "Adicionou o conhecimento ");
					SQLiteAndroidDatabaseHelper dbHelper = new SQLiteAndroidDatabaseHelper(
							deviceManager.getDataManager(), 
							getBaseContext());
					 Log.d("LOG", "Criou o DB Handler ");
					 deviceManager.getDataManager().setUrboSentiDatabaseHelper(dbHelper);
					//SQLiteDatabase db = (SQLiteDatabase) dbHelper.openDatabaseConnection();
					//Log.d("LOG", "Abriu a conexão ");
					//dbHelper.createDatabase();
					//Log.d("LOG", "Tentou criar o DB ");
					
					
		        } catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("Error", "IO: "+e.getMessage());
				}
		        // Processo de Descoberta, executa todos os onCreate's de todos os Componentes habilidatos do módudo de sensoriamento
		        deviceManager.onCreate();
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