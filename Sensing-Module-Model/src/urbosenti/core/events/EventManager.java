/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.events;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import urbosenti.adaptation.AdaptationManager;
import urbosenti.core.device.ComponentManager;
import urbosenti.core.device.DeviceManager;
import urbosenti.core.device.model.FeedbackAnswer;
import urbosenti.core.events.timer.EventTimer;
import urbosenti.core.events.timer.EventTimerFactory;
import urbosenti.core.events.timer.TriggerRequest;
import urbosenti.core.events.timer.UrboSentiEventTimerFactory;

/**
 *
 * @author Guilherme
 */
public class EventManager extends ComponentManager {

    /**
     * int EVENT_TIME_TRIGGER_ACHIEVED = 1;
     *
     * <ul><li>id: 1</li>
     * <li>evento: Evento agendado</li>
     * <li>parâmetros: Evento definido por quem agendou; Gatilho de
     * tempo</li></ul>
     */
    public static final int EVENT_TIME_TRIGGER_ACHIEVED = 1;
    public static final int METHOD_ONLY_INTERVAL = 1;
    public static final int METHOD_ONLY_DATE = 2;
    public static final int METHOD_DATE_PLUS_REPEATED_INTERVALS = 3;
    public static final long DEFAULT_SYNCHRONOUS_EVENT_TIMEOUT = 20000;

    //private Timer timer;
    private final List<EventTimer> eventTimerWorkers;
    private boolean enableSystemHandler;
    private AdaptationManager systemHandler;
    private List<ApplicationHandler> applicationHandlers;
    // Fábrica responsável por criar os objetos
    private EventTimerFactory eventTimerFactory;

    public EventManager(DeviceManager deviceManager, AdaptationManager systemHandler) {
        this(deviceManager);
        this.systemHandler = systemHandler;
        this.enableSystemHandler = true;
    }

    public EventManager(DeviceManager deviceManager) {
        super(deviceManager, urbosenti.core.data.dao.EventDAO.COMPONENT_ID);
        this.systemHandler = null;
        this.enableSystemHandler = false;
        this.applicationHandlers = new ArrayList();
        this.eventTimerWorkers = new ArrayList();
        this.eventTimerFactory = new UrboSentiEventTimerFactory();
    }

    @Override
    public void onCreate() {
        System.out.println("Activating: " + getClass());
    }

    public void newEvent(Event event) {
        /* if is a application event foward to the Application Handler*/
        switch (event.getOriginType()) {
            case Event.APPLICATION_EVENT:
                for (ApplicationHandler ah : applicationHandlers) {
                    ah.newEvent(event);
                }
                break;
            /* if is a system event foward to the SystemHandler*/
            case Event.SYSTEM_EVENT:
                if (systemHandler != null && enableSystemHandler) { /* if the SystemHandler is inactive the message is sent to the Application Handler*/

                    systemHandler.newEvent(event);
                } else {
                    for (ApplicationHandler ah : applicationHandlers) {
                        ah.newEvent(event);
                    }
                }
                break;
        }
    }

    /**
     * Retorna uma ação para o evento síncrono.
     *
     * @param event
     * @return Retorna uma ação, se retornal null então o tempo foi expirado.
     * Utiliza o tempo padrão de 20s para limite da resposta;
     */
    public Action newSynchronousEvent(Event event) {
        return this.newSynchronousEvent(event, DEFAULT_SYNCHRONOUS_EVENT_TIMEOUT);
    }

    /**
     * Retorna uma ação para o evento síncrono.
     *
     * @param event
     * @param timeout
     * @return Retorna uma ação, se retornal null então o tempo foi expirado.
     */
    public Action newSynchronousEvent(Event event, long timeout) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setSystemHandler(AdaptationManager systemHandler) {
        this.systemHandler = systemHandler;
    }

    public void enableSystemHandler() {
        this.enableSystemHandler = true;
    }

    public void disableSystemHandler() {
        this.enableSystemHandler = true;
    }

    public void subscribe(ApplicationHandler applicationHandler) {
        applicationHandlers.add(applicationHandler);
    }

    public void unsubscribe(ApplicationHandler applicationHandler) {
        applicationHandlers.remove(applicationHandler);
    }

    /**
     * Ações disponibilizadas por esse componente:
     * <ul>
     * <li>1 - Adicionar um gatilho temporal de evento</li>
     * <li>2 - Cancelar gatilho</li>
     * </ul>
     *
     * @param action contém objeto ação.
     * @return
     *
     */
    @Override
    public synchronized FeedbackAnswer applyAction(Action action) {
        TriggerRequest tr;
        switch (action.getId()) {
            case 1: // 1 - Adicionar um gatilho de tempo
                // Parâmetros:
                // Evento
                tr = new TriggerRequest();
                tr.setEvent((Event) action.getParameters().get("event"));
                // Intervalo de tempo
                tr.setInterval((Long) action.getParameters().get("time"));
                // Data do gatilho
                tr.setTime((Date) action.getParameters().get("date"));
                // Modo de operação
                tr.setMethod((Integer) action.getParameters().get("method"));
                // Quem Registrou
                tr.setHandler((Object) action.getParameters().get("handler"));
                // Criar o worker e adicionar na fila --- Colocar um factory aqui, e adicionar as instâncias pela aplicação
                EventTimer timerWorker = this.eventTimerFactory.getEventTimer(tr, this);
                this.eventTimerWorkers.add(timerWorker);
                // Iniciar o contador
                timerWorker.start();
                break;
            case 2: // 2 - Cancelar gatilho
                tr = new TriggerRequest();
                tr.setEvent((Event) action.getParameters().get("event"));
                // Intervalo de tempo
                tr.setInterval((Long) action.getParameters().get("time"));
                // Data do gatilho
                tr.setTime((Date) action.getParameters().get("date"));
                // Modo de operação
                tr.setMethod((Integer) action.getParameters().get("method"));
                // Quem Registrou
                tr.setHandler((Object) action.getParameters().get("handler"));
                // Verificar os workers ativos qual depes possui a requisição a ser cancelada
                boolean found = false;
                for (EventTimer t : eventTimerWorkers) {
                    if (!t.isFinished()
                            && t.equalsTriggerRequest(tr)) {
                        t.cancel();
                        found = true;
                        break;
                    }
                }
//                if(found)System.out.println("Achou :D"); 
//                else System.out.println("Não Achou :'(");
                break;
        }
        return null;
    }

    /**
     * Nesta função os eventos gerados pelo gatilho de tempo, diferentemente dos
     * demais componentes, são enviados diretamente para quem realizou a
     * requisição do gatilho
     * <br><br>Eventos possíveis
     * <ul>
     * <li>EventManager.EVENT_TIME_TRIGGER_ACHIEVED - Evento agendado</li>
     * </ul>
     *
     * @param eventId identificador do evento citado acima
     * @param params Parâmetros oriundo dos objetos do componente <br><br>
     * @see #EVENT_TIME_TRIGGER_ACHIEVED
     */
    public void newInternalEvent(int eventId, Object... params) {
        TriggerRequest tr;
        HashMap<String, Object> values;
        Event event;
        switch (eventId) {
            case EVENT_TIME_TRIGGER_ACHIEVED: // 1 - Evento agendado

                tr = (TriggerRequest) params[0];

                // Parâmetros do Evento
                values = new HashMap();
                values.put("event", tr.getEvent());
                values.put("trigger", tr);

                // O agendador pertence ao sistema
                if (tr.getHandler() instanceof SystemHandler) {

                    // criação do evento
                    event = new SystemEvent(this);
                    event.setId(1);
                    event.setName("Evento agendado");
                    event.setTime(new Date());
                    event.setValue(values);

                    // Envia o evento para o tratador de sistema
                    ((SystemHandler) tr.getHandler()).newEvent(event);

                    // senão o agendador pertence a aplicação
                } else if (tr.getHandler() instanceof ApplicationHandler) {

                    // criação do evento
                    event = new ApplicationEvent(this);
                    event.setId(1);
                    event.setName("Evento agendado");
                    event.setTime(new Date());
                    event.setValue(values);

                    // Envia o evento para o tratador da aplicação
                    ((ApplicationHandler) tr.getHandler()).newEvent(event);
                }
                break;
        }
    }

    /**
     *
     * @param externalEventTimerFactory contem a Fábrica que foi desenvolvida
     * para o sistema operacional nativo
     */
    public void setExternalEventTimer(EventTimerFactory externalEventTimerFactory) {
        this.eventTimerFactory = externalEventTimerFactory;
    }
}
