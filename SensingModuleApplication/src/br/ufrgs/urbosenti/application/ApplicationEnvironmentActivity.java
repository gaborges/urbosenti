package br.ufrgs.urbosenti.application;

import br.ufrgs.urbosenti.AndroidOperationalSystemDiscovery;
import br.ufrgs.urbosenti.R;
import br.ufrgs.urbosenti.android.AndroidGeneralCommunicationInterface;
import br.ufrgs.urbosenti.android.SQLiteAndroidDatabaseHelper;
import urbosenti.core.communication.Message;
import urbosenti.core.communication.PushServiceReceiver;
import urbosenti.core.communication.interfaces.WiredCommunicationInterface;
import urbosenti.core.communication.receivers.SocketPushServiceReceiver;
import urbosenti.core.device.DeviceManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ApplicationEnvironmentActivity extends Activity {

	private Button btnBack;
	private Button btnService;
	private TextView txtApplicationUID;
	private TextView txtApplicationStatus;
	private TextView txtRemoteServerStatus;
	private TextView txtServiceStatus;

	public static final int BACK = 1;

	protected void onCreateOld2(Bundle savedInstanceState) {
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
		final Intent intent = new Intent(getBaseContext(), UrboSentiService.class);
		// back to the previous activity
		btnBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				stopService(intent);
				finish();
			}
		});
		// execute or stop the service
		btnService.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Execute the service
				// Instanciar componentes -- Device manager e os demais onDemand

				startService(intent);
			}
		});
	}

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

		// Execute the service
		// Instanciar componentes -- Device manager e os demais onDemand
		final DeviceManager deviceManager = new DeviceManager(); // Objetos Core
																	// já estão
																	// incanciados
																	// internamente
		deviceManager.enableAdaptationComponent(); // Habilita componente de
													// adaptação

		// Adicionar as interfaces de comunicação suportadas --- Inicialmente
		// manual. Após adicionar um processo automático
		deviceManager.addSupportedCommunicationInterface(new AndroidGeneralCommunicationInterface(getBaseContext()));
		ConcreteApplicationHandler handler = new ConcreteApplicationHandler(deviceManager);
		deviceManager.getEventManager().subscribe(handler);
		PushServiceReceiver teste = new SocketPushServiceReceiver(deviceManager.getCommunicationManager());
		deviceManager.addSupportedInputCommunicationInterface(teste);
		deviceManager.setOSDiscovery(new AndroidOperationalSystemDiscovery(getBaseContext()));
		Log.d("LOG", "adicionou comunicação e o Application Handle");
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
			// Log.d("LOG", "Abriu a conexão ");
			// dbHelper.createDatabase();
			// Log.d("LOG", "Tentou criar o DB ");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("Error", "IO: " + e.getMessage());
		}
		// Processo de Descoberta, executa todos os onCreate's de todos os
		// Componentes habilidatos do módudo de sensoriamento

		deviceManager.onCreate();
		Log.d("DEBUG", "onCreateCompletado");

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

				Thread t = new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							/**
							 * *** Processo de inicialização dos serviços ****
							 */
							deviceManager.startUrboSentiServices();
						} catch (IOException ex) {
							Log.d("Error", ex.getLocalizedMessage());
							System.exit(-1);
						} catch (SQLException ex) {
							Log.d("Error", ex.getMessage());
						}
					}
				});
				t.start();

				Log.d("DEBUG", "inicialização dos serviços da urbosenti completado");

				try {
					Message m = new Message();
					m.setContent("oiiiiii");

					// Envia o relato
					deviceManager.getCommunicationManager().addReportToSend(m);
					m = new Message();
					m.setContent("oiiiiii");

					// Envia o relato
					deviceManager.getCommunicationManager().addReportToSend(m);
					m = new Message();
					m.setContent("oiiiiii");

					// Envia o relato
					deviceManager.getCommunicationManager().addReportToSend(m);
					m = new Message();
					m.setContent("oiiiiii");

					// Envia o relato
					deviceManager.getCommunicationManager().addReportToSend(m);

					Log.d("DEBUG", "Relato enviado");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try {
					Thread.sleep(40000);
				} catch (InterruptedException ex) {
					Log.d("Error", ex.getLocalizedMessage());
				}
				deviceManager.stopUrboSentiServices();
				Log.d("DEBUG", "Serviços parados");
			}
		});
	}

	protected void onCreateOld(Bundle savedInstanceState) {
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
				Thread t = new Thread(new Runnable() {

					@Override
					public void run() {
						DeviceManager deviceManager = new DeviceManager(); // Objetos
																			// Core
																			// já
																			// estão
																			// incanciados
																			// internamente
						deviceManager.enableAdaptationComponent(); // Habilita
																	// componente
																	// de
																	// adaptação

						// Adicionar as interfaces de comunicação suportadas ---
						// Inicialmente manual. Após adicionar um processo
						// automático
						deviceManager.addSupportedCommunicationInterface(
								new AndroidGeneralCommunicationInterface(getBaseContext()));
						ConcreteApplicationHandler handler = new ConcreteApplicationHandler(deviceManager);
						deviceManager.getEventManager().subscribe(handler);
						PushServiceReceiver teste = new SocketPushServiceReceiver(
								deviceManager.getCommunicationManager());
						deviceManager.addSupportedInputCommunicationInterface(teste);
						deviceManager.setOSDiscovery(new AndroidOperationalSystemDiscovery(getBaseContext()));
						Log.d("LOG", "adicionou comunicação e o Application Handle");
						try {
							deviceManager.setDeviceKnowledgeRepresentationModel(
									getAssets().open("deviceKnowledgeModel.xml"), "xmlInputStream");
							Log.d("LOG", "Adicionou o conhecimento ");
							SQLiteAndroidDatabaseHelper dbHelper = new SQLiteAndroidDatabaseHelper(
									deviceManager.getDataManager(), getBaseContext());
							Log.d("LOG", "Criou o DB Handler ");
							deviceManager.getDataManager().setUrboSentiDatabaseHelper(dbHelper);
							// SQLiteDatabase db = (SQLiteDatabase)
							// dbHelper.openDatabaseConnection();
							// Log.d("LOG", "Abriu a conexão ");
							// dbHelper.createDatabase();
							// Log.d("LOG", "Tentou criar o DB ");

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Log.d("Error", "IO: " + e.getMessage());
						}
						// Processo de Descoberta, executa todos os onCreate's
						// de todos os Componentes habilidatos do módudo de
						// sensoriamento
						deviceManager.onCreate();
						Log.d("DEBUG", "onCreateCompletado");
						try {
							/**
							 * *** Processo de inicialização dos serviços ****
							 */
							deviceManager.startUrboSentiServices();
						} catch (IOException ex) {
							Log.d("Error", ex.getLocalizedMessage());
							System.exit(-1);
						} catch (SQLException ex) {
							Log.d("Error", ex.getMessage());
						}

						Log.d("DEBUG", "inicialização dos serviços da urbosenti completado");

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

						try {
							Thread.sleep(40000);
						} catch (InterruptedException ex) {
							Log.d("Error", ex.getLocalizedMessage());
						}
						deviceManager.stopUrboSentiServices();
						Log.d("DEBUG", "Serviços parados");
					}
				});
				t.start();
				Toast.makeText(getBaseContext(), "Espera aí que já termina!", Toast.LENGTH_LONG).show();
				try {
					t.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("ERROO", e.getLocalizedMessage());
				}
				Log.d("Debug", "terminou");
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