/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.adaptation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import urbosenti.context.ContextManager;
import urbosenti.core.communication.Message;
import urbosenti.core.device.ComponentManager;
import urbosenti.core.device.DeviceManager;
import urbosenti.core.events.Action;
import urbosenti.core.events.AsynchronouslyManageableComponent;
import urbosenti.core.events.Event;
import urbosenti.core.events.EventManager;
import urbosenti.user.UserManager;

/**
 *
 * @author Guilherme
 */
public class AdaptationManager extends ComponentManager implements Runnable, AsynchronouslyManageableComponent {

    /**
     *
     * @param This method receives a event from the Event Manager to process the
     * event applying changes directly in the component.
     */
    //private LocalKnowledge localKnowledge
    private Queue<Event> availableEvents;
    private DeviceManager deviceManager;
    private ContextManager contextManager;
    private UserManager userManager;
    private boolean running;
    private Boolean flag;
    private Boolean monitor;

    public AdaptationManager(DeviceManager deviceManager) {
        super(deviceManager);
        this.deviceManager = null;
        this.contextManager = null;
        this.userManager = null;
        this.running = true;
        this.availableEvents = new LinkedList<Event>();
        this.flag = true;
        this.monitor = true;
    }
    
    public boolean discovery(DeviceManager deviceManager) {
        if(running) return false;
        this.deviceManager = deviceManager;
        
        // descobre o modelo
        
        return true;
    }
    
    public boolean discovery(DeviceManager deviceManager, ContextManager contextManager, UserManager userManager) {
        if(running) return false;
        this.deviceManager = deviceManager;
        this.contextManager = contextManager;
        this.userManager = userManager;
        
        // descobre o modelo
        // Device
            // Componentes em funcionamento
            // sensores e possíveis atuadores de cada componentes
            // Políticas e estratédias (funcionalidades/comportamentos), define restrições
        // User
            // Preferências do usuário, para privacidade, etc... -- Podem ser alterados on the fly
            // Restrições do usuário
        // Context
            // Descoberta de funções de contexto
                // Predição de contextos
                // Gerar conhecimento
                // Modelos de aprendizagem
                // Inferência
            // Apoio a descoberta e identificação de novos recursos
            // Possibilita gatinhos de eventos dinâmicos, como tempo etc...
        
        return true;
    }
    
    public  boolean discovery(DeviceManager deviceManager, UserManager userManager) {
        if(running) return false;
        this.deviceManager = deviceManager;
        this.userManager = userManager;
        
        // descobre o modelo
        
        return true;
    }
    
    public  boolean discovery(DeviceManager deviceManager, ContextManager contextManager) {
        if(running) return false;
        this.deviceManager = deviceManager;
        this.contextManager = contextManager;
        
        // descobre o modelo
        
        return true;
    }

    public synchronized void newEvent(Event event) {
        /*
         add to queue
         wake up the Adaptation Mechanism
         */
        this.availableEvents.add(event);
        notifyAll();
    }

    public synchronized boolean start() {
        this.running = true;
        notifyAll();
        return true;
    }

    public synchronized boolean stop() {
        this.running = false;
        notifyAll();
        return true;
    }

    @Override
    public void run() {
        /* At first, the adaptation manager discovers the environment to rum*/
        this.discovery(deviceManager, contextManager, userManager);
        /* It begin the monitoring process of events */
        try {
            monitoring();
        } catch (InterruptedException ex) {
            Logger.getLogger(AdaptationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void monitoring() throws InterruptedException {
        while (running) {
            /* Monitoring */
            Event event;     
            synchronized (flag) {
                event = availableEvents.poll();
                if (event == null) {
                    System.out.println("Esperando;;;");
                    wait();
                }
           }

            if (event != null) {
                switch (event.getOriginType()) {
                    case Event.INTERATION_EVENT:
                        
                        //InteractionEvent;
                        /* Analysis -- Diagnosis */
                        /* Planning -- Plan */
                        break;
                    case Event.INTERNAL_COMPONENT_EVENT:
                        //ContextEvent
                        /* Analysis -- Diagnosis */
                        /* Planning -- Plan */
                        break;
                }
                /* Execute */
                System.out.println("Event: "+event.toString());
                System.out.println("Message: "+ event.getValue().toString() );
            }
        }
    }

    @Override
    public void onCreate() {
        // Carregar dados e configurações que serão utilizados para execução em memória
        // Preparar configurações inicias para execução
        // Para tanto utilizar o DataManager para acesso aos dados.
        System.out.println("Activating: " + getClass());
    }

    @Override
    public void applyAction(Action action) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
