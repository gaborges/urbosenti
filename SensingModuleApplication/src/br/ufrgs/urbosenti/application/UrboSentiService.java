package br.ufrgs.urbosenti.application;

import java.io.IOException;
import java.sql.SQLException;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import br.ufrgs.urbosenti.AndroidOperationalSystemDiscovery;
import br.ufrgs.urbosenti.android.AndroidGeneralCommunicationInterface;
import br.ufrgs.urbosenti.android.SQLiteAndroidDatabaseHelper;
import urbosenti.core.communication.Message;
import urbosenti.core.communication.PushServiceReceiver;
import urbosenti.core.communication.receivers.SocketPushServiceReceiver;
import urbosenti.core.device.DeviceManager;

public class UrboSentiService extends Service {

	private DeviceManager deviceManager;

	public UrboSentiService() {

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		deviceManager = new DeviceManager(); // Objetos Core j� est�o
		// incanciados internamente
		deviceManager.enableAdaptationComponent(); // Habilita componente de
		// adapta��o
		// Adicionar as interfaces de comunica��o suportadas --- Inicialmente
		// manual. Ap�s adicionar um processo autom�tico
		deviceManager.addSupportedCommunicationInterface(new AndroidGeneralCommunicationInterface(getBaseContext()));
		ConcreteApplicationHandler handler = new ConcreteApplicationHandler(deviceManager);
		deviceManager.getEventManager().subscribe(handler);
		PushServiceReceiver teste = new SocketPushServiceReceiver(deviceManager.getCommunicationManager());
		deviceManager.addSupportedInputCommunicationInterface(teste);
		deviceManager.setOSDiscovery(new AndroidOperationalSystemDiscovery(getBaseContext()));
		Log.d("LOG", "adicionou comunica��o e o Application Handle");
		try {
			deviceManager.setDeviceKnowledgeRepresentationModel(getAssets().open("deviceKnowledgeModel.xml"),
					"xmlInputStream");
			Log.d("LOG", "Adicionou o conhecimento ");
			SQLiteAndroidDatabaseHelper dbHelper = new SQLiteAndroidDatabaseHelper(deviceManager.getDataManager(),
					getBaseContext());
			Log.d("LOG", "Criou o DB Handler ");
			deviceManager.getDataManager().setUrboSentiDatabaseHelper(dbHelper);
			// SQLiteDatabase db = (SQLiteDatabase)
			// dbHelper.openDatabaseConnection();
			// Log.d("LOG", "Abriu a conex�o ");
			// dbHelper.createDatabase();
			// Log.d("LOG", "Tentou criar o DB ");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("Error", "IO: " + e.getMessage());
		}
		deviceManager.onCreate();
		Log.d("DEBUG", "onCreateCompletado");
		try {
			/**
			 * *** Processo de inicializa��o dos servi�os ****
			 */
			deviceManager.startUrboSentiServices();
		} catch (IOException ex) {
			Log.d("Error", ex.getLocalizedMessage());
			System.exit(-1);
		} catch (SQLException ex) {
			Log.d("Error", ex.getMessage());
		}

		Log.d("DEBUG", "inicializa��o dos servi�os da urbosenti completado");

		try {
			Message m = new Message();
			m.setContent("oiiiiii");

			// Envia o relato
			deviceManager.getCommunicationManager().addReportToSend(m);
			m = new Message();
			m.setContent("oiiiiii");

			// Envia o relato
			deviceManager.getCommunicationManager().addReportToSend(m);
			Log.d("DEBUG", "Relato enviado");
			m = new Message();
			m.setContent("oiiiiii");

			// Envia o relato
			deviceManager.getCommunicationManager().addReportToSend(m);
			m = new Message();
			m.setContent("oiiiiii");

			// Envia o relato
			deviceManager.getCommunicationManager().addReportToSend(m);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		deviceManager.stopUrboSentiServices();
		Log.d("DEBUG", "Servi�os parados");
	}
}
