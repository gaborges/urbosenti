/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.communication;

import java.io.IOException;
import java.io.StringReader;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import urbosenti.core.data.CommunicationDAO;
import urbosenti.core.device.Agent;
import urbosenti.core.device.ComponentManager;
import urbosenti.core.device.DeviceManager;
import urbosenti.core.events.Action;
import urbosenti.core.events.ApplicationEvent;
import urbosenti.core.events.AsynchronouslyManageableComponent;
import urbosenti.core.events.Event;
import urbosenti.core.events.SystemEvent;

/**
 *
 * @author Guilherme
 */
public class CommunicationManager extends ComponentManager implements AsynchronouslyManageableComponent, Runnable {

    /**
     * int EVENT_INTERFACE_DISCONNECTION = 1; </ br>
     *
     * <ul><li>id: 1</li>
     * <li>evento: Interface Desconectada</li>
     * <li>parâmetros: Interface desconectada</li></ul>
     *
     */
    private static final int EVENT_INTERFACE_DISCONNECTION = 1;
    /**
     * int EVENT_MESSAGE_DELIVERED = 2;
     *
     * <ul><li>id: 2</li>
     * <li>evento: Mensagem Entregue</li>
     * <li>parâmetros: Mensagem Wrapper; Destinatário; Interface;</li></ul>
     *
     */
    private static final int EVENT_MESSAGE_DELIVERED = 2;
    /**
     * int EVENT_MESSAGE_NOT_DELIVERED = 3;
     *
     * <ul><li>id: 3</li>
     * <li>evento: Mensagem não entregue</li>
     * <li>parâmetros: Mensagem Wrapper; Destinatário;</li></ul>
     *
     */
    private static final int EVENT_MESSAGE_NOT_DELIVERED = 3;
    /**
     * int EVENT_MESSAGE_RECEIVED = 4;
     *
     * <ul><li>id: 4</li>
     * <li>evento: Mensagem recebida</li>
     * <li>parâmetros: Origem; Mensagem</li></ul>
     *
     */
    private static final int EVENT_MESSAGE_RECEIVED = 4;
    /**
     * int EVENT_MESSAGE_RECEIVED_INVALID_FORMAT = 5;
     *
     * <ul><li>id: 5</li>
     * <li>evento: Mensagem recebida em formato inválido</li>
     * <li>parâmetros: Origem; Mensagem Bruta</li></ul>
     *
     */
    private static final int EVENT_MESSAGE_RECEIVED_INVALID_FORMAT = 5;
    /**
     * int EVENT_ADDRESS_NOT_REACHABLE = 6;
     *
     * <ul><li>id: 6</li>
     * <li>evento: Endereço não acessível. Identificado por timeout.</li>
     * <li>parâmetros: Mensagem Wrapper; Destinatário; Interface</li></ul>
     *
     */
    private static final int EVENT_ADDRESS_NOT_REACHABLE = 6;
    /**
     * int EVENT_INTERFACE_DISCONNECTION = 7; </ br>
     *
     * <ul><li>id: 9</li>
     * <li>evento: desconexão geral do dispositivo </li>
     * <li>parâmetros: nenhum.</li></ul>
     *
     */
    private static final int EVENT_DISCONNECTION = 7;
    /**
     * int EVENT_RESTORED_CONNECTION = 8;
     *
     * <ul><li>id: 8</li>
     * <li>evento: Conexão Reestabelecida</li>
     * <li>parâmetros: Interface</li></ul>
     *
     */
    private static final int EVENT_RESTORED_CONNECTION = 8;
    /**
     * int EVENT_REPORT_AWAITING_APPROVAL = 9;
     *
     * <ul><li>id: 9</li>
     * <li>evento: Relato Esperando Aprovação </li>
     * <li>parâmetros: Mensagem</li></ul>
     *
     */
    private static final int EVENT_REPORT_AWAITING_APPROVAL = 9;
    /**
     * int EVENT_MESSAGE_STORED = 10;
     *
     * <ul><li>id: 10</li>
     * <li>evento: Mensagem armazenada </li>
     * <li>parâmetros: Mensagem Wrapper</li></ul>
     *
     */
    private static final int EVENT_MESSAGE_STORED = 10;

    private int countPriorityMessage;
    private int countNormalMessage;
    private int limitPriorityMessage;
    private int limitNormalMessage;
    private List<MessageWrapper> messagesNotChecked;
    private final Queue<MessageWrapper> normalMessageQueue;
    private final Queue<MessageWrapper> priorityMessageQueue;
    private double mobileDataQuota; // em bytes
    private double usedMobileData; // em bytes
    private double mobileDataPriorityQuota; // em bytes
    /**
     * <h3>Política de uso de dados móveis</h3>
     * <ul><li>Políticas:</li>
     * <li>1 = Sem mobilidade. Configuração default.</li>
     * <li>2 = Fazer o uso sempre que possível.</li>
     * <li>3 = Somente fazer uso com relatos de alta prioridade.</li>
     * <li>4 = Utiliza cota por ciclo de uso: Até X todos os tipos de mensagens,
     * após até Y somente de alta prioridade.</li>
     * <li>5 = Utiliza cota por ciclo de uso: Até X todos os tipos de mensagens
     * para mensagens de alta prioridade o uso é liberado.</li>
     * <li>6 = Não utilizar dados móveis.</li></ul>
     */
    private int mobileDataPolicy;
    /**
     * <h3>Política de Upload periódico de Mensagens</h3>
     * <ul><li>Políticas:</li>
     * <li>1 = Sempre que há um relato novo tenta fazer o upload, caso exista
     * conexão, senão espera reconexão. Padrão.</li>
     * <li>2 = Em intervalos fixos. Pode ser definido pela aplicação. Intervalo
     * inicial padrão a cada 15 segundos. Se não há conexão as mensagens são
     * armazenadas.</li>
     * <li>3 = Exige confirmação da aplicação para upload dos relatos. Enquanto
     * não confirmada comportamento na política 1.</li>
     * <li>4 = Adaptativo. O componente de adaptação irá atribuir dinamicamente
     * novos intervalos.</li></ul>
     */
    private int uploadMessagingPolicy;
    /**
     * <h3>Política de armazenamento de mensagem</h3>
     * <ul><li>Políticas:</li>
     * <li>1 = Não armazenar nenhuma.</li>
     * <li>2 = Apagar todas que foram enviadas com sucesso e armazenar as que
     * não foram enviadas. Opção padrão.</li>
     * <li>3 = Armazenar todas e deixar a aplicação decidir quais apagar.</li>
     * <li>4 = Dinâmico (Exige componente de adaptação). Dá poder ao mecanismo
     * decidir quando apagar uma mensagem armazenada. O usuário pode especificar
     * uma quantidade ou um tempo.</li></ul>
     */
    private int messageStoragePolicy;
    /**
     * <h3>Política de armazenamento de mensagem</h3>
     * <ul><li>Políticas:</li>
     * <li>1 = Tentativa em intervalos fixos. Pode ser definido pela aplicação.
     * O padrão é nova tentativa a cada 60 segundos.</li>
     * <li>2 = Adaptativo. Permite o componente de adaptação reconfigure
     * dinamicamente o tempo de reconexão.</li></ul>
     */
    private int reconnectionPolicy; // Política de reconexão
    private Integer uploadInterval; // Intervalo de upload
    private Integer reconnectionAttemptInterval;
    private CommunicationInterface currentCommunicationInterface;
    private List<CommunicationInterface> communicationInterfaces;
    private ReconnectionService reconnectionService;
    private Agent uploadServer;
    private boolean running; // indica se o servidor está rodando ou não

    public CommunicationManager(DeviceManager deviceManager) {
        super(deviceManager);
        this.normalMessageQueue = new LinkedList<MessageWrapper>();
        this.priorityMessageQueue = new LinkedList<MessageWrapper>();
    }

    // if do not setted then when the method onCreate was activated it creates automatically
    public void setReconectionService(ReconnectionService reconnectionService) {
        this.reconnectionService = reconnectionService;
    }

    @Override
    public void onCreate() {
        // Carregar dados e configurações que serão utilizados para execução em memória
        // Preparar configurações inicias para execução
        // Para tanto utilizar o DataManager para acesso aos dados.
        System.out.println("Activating: " + getClass());
        try {
            this.communicationInterfaces = super.getDeviceManager().getDataManager().getCommunicationDAO().getAvailableInterfaces();
        } catch (IOException ex) {
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.currentCommunicationInterface = this.communicationInterfaces.get(0);
//        
//        this.mobileDataPolicy = 1; // sem mobilidade - Default
//        this.messagingPolicy = 1;  // Se não der certo avisa a origem da mensagem
//        this.messageStoragePolicy = 2; // Política de armazenamento de mensagem - Padrão: Apagar todas que foram enviadas com sucesso e armazenar as que não foram enviadas. 
//        this.reconnectionPolicy = 1;   // Política de reconexão: Padrão - Tentativa em intervalos fixos. Pode ser definido pela aplicação. O padrão é uma nova tentativa a cada 60 segundos
//        this.uploadMessagingPolicy = 2; //  política de Upload periódico de Mensagens: Sempre que há um relato novo tenta fazer o upload, caso exista conexão, senão espera reconexão. Padrão.
//        
        this.mobileDataPolicy = super.getDeviceManager().getDataManager().getCommunicationDAO().getCurrentPreferentialPolicy(CommunicationDAO.MOBILE_DATA_POLICY); // sem mobilidade - Default
        this.messageStoragePolicy = super.getDeviceManager().getDataManager().getCommunicationDAO().getCurrentPreferentialPolicy(CommunicationDAO.MESSAGE_STORAGE_POLICY); // Política de armazenamento de mensagem - Padrão: Apagar todas que foram enviadas com sucesso e armazenar as que não foram enviadas. 
        this.reconnectionPolicy = super.getDeviceManager().getDataManager().getCommunicationDAO().getCurrentPreferentialPolicy(CommunicationDAO.RECONNECTION_POLICY);   // Política de reconexão: Padrão - Tentativa em intervalos fixos. Pode ser definido pela aplicação. O padrão é uma nova tentativa a cada 60 segundos
        this.uploadMessagingPolicy = super.getDeviceManager().getDataManager().getCommunicationDAO().getCurrentPreferentialPolicy(CommunicationDAO.UPLOAD_MESSAGING_POLICY); //  política de Upload periódico de Mensagens: Sempre que há um relato novo tenta fazer o upload, caso exista conexão, senão espera reconexão. Padrão.

        // Setar dinamica a política de mobile data police aqui.
        // se há politica deve ser setado através das configurações:
        this.mobileDataQuota = 1000;
        this.usedMobileData = 0;
        this.mobileDataPriorityQuota = 2000;
        // Setar dinamicamente a politica de mensagens   

        // testar se não foi setado os serviços de entraga e reconexão senão criar e iniciar
        // Testar se os serviços foram iniciados e caso não, iniciá-los
        //Contadores do escalonador da fila de reports
        limitNormalMessage = 1;
        limitPriorityMessage = 4;

        // Intervalo para upload do serviço de upload em 15 segundos
        this.uploadInterval = 15000;
        // Intervalo para reconexão em 60 segundos
        this.reconnectionAttemptInterval = 60000;
        if (reconnectionService == null) {
            this.reconnectionService = new ReconnectionService(this, communicationInterfaces);
            this.reconnectionService.setReconnectionTime(reconnectionAttemptInterval);
            this.reconnectionService.setReconnectionMethodOneAtTime();
        }
    }

    @Override
    public synchronized void applyAction(Action action) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @param message has the message to send. This message can be sent to
     * everyone that can understand the message.
     *
     * @return returns true if the message is sent successfully and false if the
     * device haven't connection.
     * @throws java.net.SocketTimeoutException
     * @throws java.net.ConnectException
     *
     *
     */
    public boolean sendMessage(Message message) throws SocketTimeoutException, java.net.ConnectException, IOException {
        // 1 - Recebe a mensagem - Gets the message
        /*
         * Se quem está enviando não foi explicitado, então, por padrão, são preenxidos os dados do envio da aplicação.
         */
        if (message.getSender() == null) {
            message.setSender(new Agent());
            message.getSender().setLayer("application");
            message.getSender().setUid(getDeviceManager().getUID());
            message.getSender().setDescription("Sensing Module");
        }
        MessageWrapper messageWrapper = new MessageWrapper(message);
        try {
            // 2 - Cria o envelope XML da UrboSenti correspondente da mensagem
            messageWrapper.build();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        // 3 - Verifica se alguma interface de comunicação está disponível
        CommunicationInterface ci = this.getCommunicationInterface(); // Método traz a interface de comunicação atual
        // 4 - [Nenhuma disponível]    
        if (ci == null) {
            // Evento desconexão
            this.newInternalEvent(EVENT_DISCONNECTION);
            // Retorna false (ou o erro)
            return false;
        }
        try {
            // 5 - Tenta enviar --- OBS.: Implementar
            ci.sendMessage(this, messageWrapper);
            //[Sucesso]
            // Evento: Mensagem Entregue
            this.newInternalEvent(EVENT_MESSAGE_DELIVERED, messageWrapper, message.getTarget(), currentCommunicationInterface);
            // Returna true para a aplicação
            return true;
        } catch (SocketTimeoutException ex) {
            //[Endereço não acessível]
            // Evento: Endereço não acessível
            // throws host unknown exception
            //[Timeout]
            // Evento: Timeout
            // thows Timeout exception   
            this.newInternalEvent(EVENT_ADDRESS_NOT_REACHABLE, messageWrapper, message.getTarget(), ci);
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } catch (java.net.ConnectException ex) {
            // Evento: Timeout
            // thows Timeout exception   
            this.newInternalEvent(EVENT_ADDRESS_NOT_REACHABLE, messageWrapper, message.getTarget(), ci);
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } catch (IOException ex) {
            //[Erro de IO]
            // Evento: Mensagem não entregue
            // throws Excessção d IO
            this.newInternalEvent(EVENT_MESSAGE_NOT_DELIVERED, messageWrapper, message.getTarget());
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    public String sendMessageWithResponse(Message message) throws SocketTimeoutException, java.net.ConnectException, IOException {

        // 1 - Recebe a mensagem - Gets the message
        /*
         * Se quem está enviando não foi explicitado, então, por padrão, são preenxidos os dados do envio da aplicação.
         */
        if (message.getSender() == null) {
            message.setSender(new Agent());
            message.getSender().setLayer("application");
            message.getSender().setUid(getDeviceManager().getUID());
            message.getSender().setDescription("Sensing Module");
        }
        MessageWrapper messageWrapper = new MessageWrapper(message);
        try {
            // 2 - Cria o envelope XML da UrboSenti correspondente da mensagem
            messageWrapper.build();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        // 3 - Verifica se alguma interface de comunicação está disponível
        CommunicationInterface ci = this.getCommunicationInterface(); // Método traz a interface de comunicação atual
        // 4 - [Nenhuma disponível]    
        if (ci == null) {
            // Evento desconexão
            this.newInternalEvent(EVENT_DISCONNECTION);
            // Retorna false (ou o erro)
            return null;
        }
        try {
            // 5 - Tenta enviar --- OBS.: Implementar
            String response = (String) ci.sendMessageWithResponse(this, messageWrapper);
            // se não conseguir tenta por outro
            // evento de mensagem entregue
            this.newInternalEvent(EVENT_MESSAGE_DELIVERED, messageWrapper, message.getTarget(), currentCommunicationInterface);
            // retorna resultado
            return response;
        } catch (SocketTimeoutException ex) {
            //[Endereço não acessível]
            // Evento: Endereço não acessível
            // throws host unknown exception
            //[Timeout]
            // Evento: Timeout
            // thows Timeout exception   
            this.newInternalEvent(EVENT_ADDRESS_NOT_REACHABLE, messageWrapper, message.getTarget(), ci);
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } catch (java.net.ConnectException ex) {
            // Evento: Timeout
            // thows Timeout exception   
            this.newInternalEvent(EVENT_ADDRESS_NOT_REACHABLE, messageWrapper, message.getTarget(), ci);
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } catch (IOException ex) {
            //[Erro de IO]
            // Evento: Mensagem não entregue
            // throws Excessção d IO
            this.newInternalEvent(EVENT_MESSAGE_NOT_DELIVERED, messageWrapper, message.getTarget());
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    public String sendMessageWithResponse(Message message, int timeout) throws SocketTimeoutException, java.net.ConnectException, IOException {

        // 1 - Recebe a mensagem - Gets the message
        /*
         * Se quem está enviando não foi explicitado, então, por padrão, são preenxidos os dados do envio da aplicação.
         */
        if (message.getSender() == null) {
            message.setSender(new Agent());
            message.getSender().setLayer("application");
            message.getSender().setUid(getDeviceManager().getUID());
            message.getSender().setDescription("Sensing Module");
        }
        MessageWrapper messageWrapper = new MessageWrapper(message);
        messageWrapper.setTimeout(timeout);
        try {
            // 2 - Cria o envelope XML da UrboSenti correspondente da mensagem
            messageWrapper.build();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        // 3 - Verifica se alguma interface de comunicação está disponível
        CommunicationInterface ci = this.getCommunicationInterface(); // Método traz a interface de comunicação atual
        // 4 - [Nenhuma disponível]    
        if (ci == null) {
            // Evento desconexão
            this.newInternalEvent(EVENT_DISCONNECTION);
            // Retorna false (ou o erro)
            return null;
        }
        try {
            // 5 - Tenta enviar --- OBS.: Implementar
            String response = (String) ci.sendMessageWithResponse(this, messageWrapper);
            // se não conseguir tenta por outro
            // evento de mensagem entregue
            this.newInternalEvent(EVENT_MESSAGE_DELIVERED, messageWrapper, message.getTarget(), currentCommunicationInterface);
            // retorna resultado
            return response;
        } catch (SocketTimeoutException ex) {
            //[Endereço não acessível]
            // Evento: Endereço não acessível
            // throws host unknown exception
            //[Timeout]
            // Evento: Timeout
            // thows Timeout exception   
            this.newInternalEvent(EVENT_ADDRESS_NOT_REACHABLE, messageWrapper, message.getTarget(), ci);
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } catch (java.net.ConnectException ex) {
            // Evento: Timeout
            // thows Timeout exception   
            this.newInternalEvent(EVENT_ADDRESS_NOT_REACHABLE, messageWrapper, message.getTarget(), ci);
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } catch (IOException ex) {
            //[Erro de IO]
            // Evento: Mensagem não entregue
            // throws Excessção d IO
            this.newInternalEvent(EVENT_MESSAGE_NOT_DELIVERED, messageWrapper, message.getTarget());
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }
    /**
     * 
     * @param message Adiciona um report para envio ao servidor.
     */
    public void sendReport(Message message) {
        // recebe o report para envio
        // cria envelope
        if (message.getSender() == null) {
            message.setSender(new Agent());
            message.getSender().setLayer("application");
            message.getSender().setUid(getDeviceManager().getUID());
            message.getSender().setDescription("Sensing Module");
        }
        MessageWrapper messageWrapper = new MessageWrapper(message);
        try {
            // Cria o envelope XML da UrboSenti correspondente da mensagem
            messageWrapper.build();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        // adiciona na fila de upload
        this.addReport(messageWrapper);
    }

    protected void newPushMessage(Agent origin, String bruteMessage) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Criar o documento e com verte a String em DOC
            Document doc = builder.parse(new InputSource(new StringReader(bruteMessage)));

            Message msg = new Message();
            msg.setSender(new Agent());
            msg.setTarget(new Agent());
            Element response = doc.getDocumentElement();
            Element header = (Element) response.getElementsByTagName("header").item(0);

            msg.getSender().setUid(((Element) header.getElementsByTagName("origin").item(0)).getElementsByTagName("uid").item(0).getTextContent());
            if (((Element) header.getElementsByTagName("origin").item(0)).getElementsByTagName("name").getLength() > 0) {
                msg.getSender().setDescription(((Element) header.getElementsByTagName("origin").item(0)).getElementsByTagName("name").item(0).getTextContent());
            }
            if (((Element) header.getElementsByTagName("origin").item(0)).getElementsByTagName("address").getLength() > 0) {
                msg.getSender().setAddress(((Element) header.getElementsByTagName("origin").item(0)).getElementsByTagName("address").item(0).getTextContent());
            }
            msg.getSender().setLayer(((Element) header.getElementsByTagName("origin").item(0)).getElementsByTagName("layer").item(0).getTextContent());
            msg.getTarget().setUid(((Element) header.getElementsByTagName("target").item(0)).getElementsByTagName("uid").item(0).getTextContent());
            msg.getTarget().setAddress(((Element) header.getElementsByTagName("target").item(0)).getElementsByTagName("address").item(0).getTextContent());
            msg.getTarget().setLayer(((Element) header.getElementsByTagName("target").item(0)).getElementsByTagName("layer").item(0).getTextContent());
            msg.setContentType(header.getElementsByTagName("contentType").item(0).getTextContent());
            msg.setSubject(header.getElementsByTagName("subject").item(0).getTextContent());
            if (header.getElementsByTagName("anonymousUpload").getLength() > 0) {
                msg.setAnonymousUpload(Boolean.parseBoolean(header.getElementsByTagName("anonymousUpload").item(0).getTextContent()));
            } else {
                msg.setAnonymousUpload(false);
            }

            if (header.getElementsByTagName("priority").getLength() > 0) {
                if (header.getElementsByTagName("priority").item(0).getTextContent().equals("preferential")) {
                    msg.setPreferentialPriority();
                } else {
                    msg.setNormalPriority();
                }
            }

            msg.setContent(response.getElementsByTagName("content").item(0).getTextContent());

            if (origin != null && msg.getSender().getAddress().isEmpty()) {
                msg.getSender().setAddress(origin.getAddress());
            }

            System.out.println("Layer: " + msg.getTarget().getLayer());

            this.newInternalEvent(EVENT_MESSAGE_RECEIVED, msg.getSender(), msg);

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
            this.newInternalEvent(EVENT_MESSAGE_RECEIVED_INVALID_FORMAT, origin, bruteMessage);
        } catch (SAXException ex) {
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
            this.newInternalEvent(EVENT_MESSAGE_RECEIVED_INVALID_FORMAT, origin, bruteMessage);
        } catch (IOException ex) {
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
            this.newInternalEvent(EVENT_MESSAGE_RECEIVED_INVALID_FORMAT, origin, bruteMessage);
        }

    }

    /**
     * @param messageWrapper contém a mensagem e mais informações que podem ser
     * enviadas
     *
     * Políticas: <\br>
     * 1) Não armazenar nenhuma. <\br>
     * 2) Apagar todas que foram enviadas com sucesso e armazenar as que não
     * foram enviadas. Opção padrão. <\br>
     * 3) Armazenar todas e deixar a aplicação decidir quais apagar. <\br>
     * 4) Dinâmico (Exige componente de adaptação). Dá poder ao mecanismo
     * decidir quando apagar uma mensagem armazenada. O usuário pode especificar
     * uma quantidade ou um tempo.
     */
    protected void storagePolice(MessageWrapper messageWrapper) {
        switch (messageStoragePolicy) {
            case 1: // Não armazenar nenhuma
                break;
            case 2: // Apagar todas que foram enviadas com sucesso e armazenar as que não foram enviadas. Opção padrão.
                if (messageWrapper.isSent()) { // Não foi enviada. Armazenar
                    getDeviceManager().getDataManager().getCommunicationDAO().removeMessage(messageWrapper);
                } else { // senão foi enviada. Armazenar
                    if (messageWrapper.getId() > 0) {
                        getDeviceManager().getDataManager().getCommunicationDAO().updateMessage(messageWrapper);
                    } else {
                        getDeviceManager().getDataManager().getCommunicationDAO().insertMessage(messageWrapper);
                    }
                }
                break;
            case 3: // Armazenar todas e deixar a aplicação decidir quais apagar.
                if (messageWrapper.getId() > 0) {
                    getDeviceManager().getDataManager().getCommunicationDAO().updateMessage(messageWrapper);
                } else {
                    getDeviceManager().getDataManager().getCommunicationDAO().insertMessage(messageWrapper);
                }
                break;
            case 4: // Dinâmico (Exige componente de adaptação). Dá poder ao mecanismo decidir quando apagar uma mensagem armazenada. O usuário pode especificar uma quantidade ou um tempo.
                if (messageWrapper.getId() > 0) {
                    getDeviceManager().getDataManager().getCommunicationDAO().updateMessage(messageWrapper);
                } else {
                    getDeviceManager().getDataManager().getCommunicationDAO().insertMessage(messageWrapper);
                    // Gerar evento -- Mensagem armazenada.
                    this.newInternalEvent(EVENT_MESSAGE_STORED, messageWrapper);
                }
                break;
        }
    }

    /**
     * @param messageWrapper Adiciona um novo relato para ser feito o upload. Se
     * política 3 e o relato não foi aprovado ainda ele é adicionado na fila de
     * espera e um evento avisando a aplicação que o relato está esperando sua
     * aprovação.
     */
    private synchronized void addReport(MessageWrapper messageWrapper) {
        // Verifica política de upload de relatos
        // se 3 gera adiciona na fila para aprovação da aplicação e utiliza política de armazenamento
        if (this.uploadMessagingPolicy == 3 && !messageWrapper.isChecked()) {
            this.messagesNotChecked.add(messageWrapper);
            this.storagePolice(messageWrapper); // depois vejo se uso ou não
            this.newInternalEvent(EVENT_REPORT_AWAITING_APPROVAL, messageWrapper.getMessage());
        }
        // Dependendo da prioriade
        if (messageWrapper.getMessage().getPriority() == Message.PREFERENTIAL_PRIORITY) {
            this.priorityMessageQueue.add(messageWrapper);
        } else {
            this.normalMessageQueue.add(messageWrapper);
        }
        notifyAll();
    }

    /**
     * @return retorna a primeira MessageWrapper da fila <b>normal</b> contendo
     * um relato com prioridade <b>normal</b>. Esse relato continua da fila.
     * OBS.: SOmente utilizado pelo método de upload dinâmico, pois tem um loop
     * infinito dentro.
     * @throws InterruptedException
     */
    protected synchronized MessageWrapper getNormalReport() throws InterruptedException {
        MessageWrapper mw;
        while (true) {
            mw = this.normalMessageQueue.peek();
            if (mw == null) {
                wait();
            } else {
                break;
            }
        }
        return mw;
    }

    /**
     * @return retorna a primeira MessageWrapper da fila <b>prioritária</b>
     * contendo um relato com prioridade <b>prioritária</b>. Esse relato
     * continua da fila. OBS.: SOmente utilizado pelo método de upload dinâmico,
     * pois tem um loop infinito dentro.
     * @throws InterruptedException
     */
    protected synchronized MessageWrapper getPriorityMessage() throws InterruptedException {
        MessageWrapper mw;
        while (true) {
            mw = this.priorityMessageQueue.peek();
            if (mw == null) {
                wait();
            } else {
                break;
            }
        }
        return mw;
    }

    /**
     * @return retorna e remove a primeira MessageWrapper da fila <b>normal</b>
     * contendo um relato com prioridade <b>normal</b>. Esse relato é removido
     * da fila. OBS.: SOmente utilizado pelo método de upload dinâmico, pois tem
     * um loop infinito dentro.
     * @throws InterruptedException
     */
    protected synchronized MessageWrapper pollNormalMessage() throws InterruptedException {
        MessageWrapper mw;
        while (true) {
            mw = this.normalMessageQueue.poll();
            if (mw == null) {
                wait();
            } else {
                break;
            }
        }
        return mw;
    }

    /**
     * @return retorna e remove a primeira MessageWrapper da fila
     * <b>prioritária</b> contendo um relato com prioridade <b>prioritária</b>.
     * Esse relato é removido da fila. OBS.: SOmente utilizado pelo método de
     * upload dinâmico, pois tem um loop infinito dentro.
     * @throws InterruptedException
     */
    protected synchronized MessageWrapper pollPriorityMessage() throws InterruptedException {
        MessageWrapper mw;
        while (true) {
            mw = this.priorityMessageQueue.poll();
            if (mw == null) {
                wait();
            } else {
                break;
            }
        }
        return mw;
    }

    /**
     * @return retorna e remove a primeira MessageWrapper de uma das duas filas
     * <b>prioritária</b> ou <b>normal</b> conforme o escalonamento dinâmico
     * pré-configurado no método OnCreate. Esse relato escolhido é removido da
     * fila. OBS.: SOmente utilizado pelo método de upload dinâmico, pois tem um
     * loop infinito dentro.
     * @throws InterruptedException
     */
    protected synchronized MessageWrapper pollMessage() throws InterruptedException {
        MessageWrapper mwp, mwn;
        while (true) {
            /*
             * Fazer um escalonamento dinâmico entre prioridades;
             * // Se a outra fila está vazia faz a atual e zera os contadores
             // obedece os dois limites priorizando os prioritários
             */

            // Pega primeiras mensagens
            mwp = this.priorityMessageQueue.peek();
            mwn = this.normalMessageQueue.peek();
            // Se ambas estão vazias, zera as contagens e espera
            if (mwn == null && mwp == null) {
                countNormalMessage = 0;
                countPriorityMessage = 0;
                wait();
            }
            // Se houver mensagens prioritárias pega elas até o limite
            if (mwp != null && countPriorityMessage < limitPriorityMessage) {
                countPriorityMessage++;
                return this.priorityMessageQueue.poll();
            }
            // se houver mensagens normais depois de atingido o limite das prioritárias então pega as mensagens normais até atingir seu limite.
            // Atingido o limite normal a contagem é zerada de ambas as filas é zerada
            if (mwn != null && countNormalMessage < limitNormalMessage) {
                countNormalMessage++;
            }
            if (countNormalMessage >= limitNormalMessage) {
                countNormalMessage = 0;
                countPriorityMessage = 0;
            }
            return this.normalMessageQueue.poll();
        }
    }

    /**
     * @return retorna a primeira MessageWrapper de uma das duas filas
     * <b>prioritária</b> ou <b>normal</b> conforme o escalonamento dinâmico
     * pré-configurado no método OnCreate. Esse relato escolhido não é remvido
     * da fila. OBS.: SOmente utilizado pelo método de upload dinâmico, pois tem
     * um loop infinito dentro.
     * @throws InterruptedException
     */
    protected synchronized MessageWrapper getMessage() throws InterruptedException {
        MessageWrapper mwp, mwn;
        while (true) {
            /*
             * Fazer um escalonamento dinâmico entre prioridades;
             * // Se a outra fila está vazia faz a atual e zera os contadores
             // obedece os dois limites priorizando os prioritários
             */

            // Pega primeiras mensagens
            mwp = this.priorityMessageQueue.peek();
            mwn = this.normalMessageQueue.peek();
            // Se ambas estão vazias, zera as contagens e espera
            if (mwn == null && mwp == null) {
                countNormalMessage = 0;
                countPriorityMessage = 0;
                wait();
            }
            // Se houver mensagens prioritárias pega elas até o limite
            if (mwp != null && countPriorityMessage < limitPriorityMessage) {
                countPriorityMessage++;
                return mwp;
            }
            // se houver mensagens normais depois de atingido o limite das prioritárias então pega as mensagens normais até atingir seu limite.
            // Atingido o limite normal a contagem é zerada de ambas as filas é zerada
            if (mwn != null && countNormalMessage < limitNormalMessage) {
                countNormalMessage++;
            }
            return mwn;
        }
    }
    /**
     * 
     * @param mw
     * @return remove o report da fila e retorna true se teve sucesso.
     */
    protected synchronized boolean removeMessage(MessageWrapper mw) {
        try {
            this.priorityMessageQueue.remove(mw);
            return true;
        } catch (NoSuchElementException ex) {
        }
        try {
            this.normalMessageQueue.remove(mw);
            return true;
        } catch (NoSuchElementException ex) {
        }
        return false;
    }

    /**
     *
     * @param message - Mensagem aprovada
     * @return return true se a mensagem foi atualizada com sucesso. Caso ela
     * não esteja mais na lista o der algum erro será retornado false
     */
    public synchronized boolean approvalReport(Message message) {
        // primeiro busca pelo tempo;
        for (MessageWrapper temp : messagesNotChecked) {
            if (message.getCreatedTime().getTime() == temp.getMessage().getCreatedTime().getTime()) {
                temp.setChecked();
                messagesNotChecked.remove(temp);
                addReport(temp);
                return true;
            }
        }
        return false;
    }

    /**
     * Função que gera os eventos. Cada evento possui um descritor que define os
     * parâmetros necessários
     *
     * @param eventId
     * @param parameters
     */
    private synchronized void newInternalEvent(int eventId, Object... parameters) {
        Agent agent;
        CommunicationInterface ci;
        MessageWrapper mw;
        Event event = null;
        Message msg;
        HashMap<String, Object> values;
        String bruteMessage;
        switch (eventId) {
            case EVENT_INTERFACE_DISCONNECTION: // 1 - Desconexão - parâmetros: Interface desconectada
                ci = (CommunicationInterface) parameters[0];

                // Adiciona os valores que serão passados para serem tratados
                values = new HashMap<String, Object>();
                values.put("interface", ci);

                // cria o evento
                event = new SystemEvent(this);// Event: new Message
                event.setId(1);
                event.setName("Desconexão");
                event.setTime(new Date());
                event.setValue(values);

                // envia o evento
                getEventManager().newEvent(event);

                break;
            case EVENT_MESSAGE_DELIVERED: // 2 - Mensagem Entregue - parâmetros: Mensagem; Destinatário; Interface;
                mw = (MessageWrapper) parameters[0];
                agent = (Agent) parameters[1];
                ci = (CommunicationInterface) parameters[2];

                // Adiciona os valores que serão passados para serem tratados
                values = new HashMap<String, Object>();
                values.put("messageWrapper", mw);
                values.put("targuet", agent);
                values.put("interface", ci);

                // cria o evento
                event = new SystemEvent(this);// Event: new Message
                event.setId(2);
                event.setName("Mensagem entregue");
                event.setTime(new Date());
                event.setValue(values);

                // envia o evento
                getEventManager().newEvent(event);

                break;
            case EVENT_MESSAGE_NOT_DELIVERED: // 3 - Mensagem Não Entregue - parâmetros: Mensagem; Destinatário;
                mw = (MessageWrapper) parameters[0];
                agent = (Agent) parameters[1];

                // Adiciona os valores que serão passados para serem tratados
                values = new HashMap<String, Object>();
                values.put("messageWrapper", mw);
                values.put("targuet", agent);

                // cria o evento de Sistema
                event = new SystemEvent(this);// Event: new Message
                event.setId(3);
                event.setName("Mensagem não entregue");
                event.setTime(new Date());
                event.setValue(values);

                // Se foi enviado para aplicação avisa também a aplicação
                if (mw.getMessage().getSender().getLayer().equals(Agent.LAYER_APPLICATION)) {
                    // cria o evento de Aplicação
                    event = new ApplicationEvent(this);// Event: new Message
                    event.setId(3);
                    event.setName("Mensagem não entregue");
                    event.setTime(new Date());
                    event.setValue(values);
                }

                // envia o evento
                getEventManager().newEvent(event);
                break;

            case EVENT_MESSAGE_RECEIVED: // 4 - Mensagem Recebida - parâmetros: Origem; Mensagem
                agent = (Agent) parameters[0];
                msg = (Message) parameters[1];

                if (msg != null) {
                    if (msg.getTarget().getLayer().equals(Agent.LAYER_SYSTEM)) { // if the target is the system
                        event = new SystemEvent(this);
                    } else if (msg.getTarget().getLayer().equals(Agent.LAYER_APPLICATION)) { // if the target is the application
                        event = new ApplicationEvent(this);
                    }

                    // Adiciona os valores que serão passados para serem tratados
                    values = new HashMap<String, Object>();
                    values.put("message", msg);
                    values.put("sender", agent);

                    // Event: new Message
                    if (event != null) {
                        event.setId(4);
                        event.setName("new message");
                        event.setTime(new Date());
                        event.setValue(values);

                        // envia o evento
                        getEventManager().newEvent(event);
                    }
                }
                break;

            case EVENT_MESSAGE_RECEIVED_INVALID_FORMAT: // 5 - Mensagem Recebida em Formato Inválido - parâmetros: Origem; Mensagem
                agent = (Agent) parameters[0];
                bruteMessage = (String) parameters[1];

                // Adiciona os valores que serão passados para serem tratados
                values = new HashMap<String, Object>();
                values.put("message", bruteMessage);
                values.put("sender", agent);

                // Event: new Message
                event = new SystemEvent(this);

                event.setId(5);
                event.setName("Message received with invalid format");
                event.setTime(new Date());
                event.setValue(values);

                // envia o evento
                getEventManager().newEvent(event);
                break;
            case EVENT_ADDRESS_NOT_REACHABLE: // 6 - Endereço não acessível - Mensagem; Destinatário; Interface
                agent = (Agent) parameters[1];
                mw = (MessageWrapper) parameters[0];
                ci = (CommunicationInterface) parameters[2];

                // Adiciona os valores que serão passados para serem tratados
                values = new HashMap<String, Object>();
                values.put("messageWrapper", mw);
                values.put("interface", ci);
                values.put("target", agent);

                // Event: new Message
                if (mw.getMessage().getSender().getLayer().equals(Agent.LAYER_SYSTEM)) {
                    event = new SystemEvent(this);
                } else {
                    event = new ApplicationEvent(this);
                }

                event.setId(6);
                event.setName("Address not reachable");
                event.setTime(new Date());
                event.setValue(values);

                // envia o evento
                getEventManager().newEvent(event);
                break;
            case EVENT_DISCONNECTION: // 7 - Desconexão  geral - parâmetros: nenhum

                // Event
                event = new SystemEvent(this);

                event.setId(7);
                event.setName("The device was completely disconnected");
                event.setTime(new Date());

                // envia o evento
                getEventManager().newEvent(event);
                break;
            case EVENT_RESTORED_CONNECTION: // 8 - Conexão Reestabelecida - parâmetros: interface
                ci = (CommunicationInterface) parameters[0];

                // Adiciona os valores que serão passados para serem tratados
                values = new HashMap<String, Object>();
                values.put("interface", ci);

                // Event: new Message
                event = new SystemEvent(this);

                event.setId(8);
                event.setName("The interface connection was restored");
                event.setTime(new Date());
                event.setValue(values);

                // envia o evento
                getEventManager().newEvent(event);
                break;
            case EVENT_REPORT_AWAITING_APPROVAL: // 9 - Relato Esperando Aprovação  - parâmetros: mensagem
                msg = (Message) parameters[0];

                event = new ApplicationEvent(this);

                // Adiciona os valores que serão passados para serem tratados
                values = new HashMap<String, Object>();
                values.put("message", msg);

                event.setId(9);
                event.setName("Report awaiting approval");
                event.setTime(new Date());
                event.setValue(values);

                // envia o evento
                getEventManager().newEvent(event);
                break;
            case EVENT_MESSAGE_STORED:
                mw = (MessageWrapper) parameters[0];

                event = new SystemEvent(this);

                // Adiciona os valores que serão passados para serem tratados
                values = new HashMap<String, Object>();
                values.put("messageWrapper", mw);

                event.setId(10);
                event.setName("Message Stored");
                event.setTime(new Date());
                event.setValue(values);

                // envia o evento
                getEventManager().newEvent(event);
                break;
        }
        // inserir métricas e medidas no evento
    }

    public CommunicationInterface getCurrentCommunicationInterface() {
        return currentCommunicationInterface;
    }

    /**
     * ReconectionService informa que interface conseguiu reconectar-se com sucesso.
     * @param current 
     */
    void notifyReconnection(CommunicationInterface current) {
        this.newInternalEvent(EVENT_RESTORED_CONNECTION, current);
        this.currentCommunicationInterface = current;
    }
    /**
     * ReconectionService informa que interface não conseguiu reconectar-se com sucesso.
     * @param current 
     */
    void notifyReconnectionNotSucceed(CommunicationInterface current) {
        this.newInternalEvent(EVENT_INTERFACE_DISCONNECTION, current);
    }

    /**
     *
     * @return retorna se o serviço de upload automático está em funcionamento 
     */
    public synchronized boolean isUploadServerRunning() {
        return running;
    }

    /**
     * @param status , seta o status que o serviço de upload de arquivos deve estar.
     */
    public synchronized void setUploadServerRunningStatus(boolean status) {
        this.running = status;
    }

    /**
     * Método retorna uma Interface de comunicação disponível
     * (CommunicationInterface). Se nenhuma estiver disponível então retorna
     * null.
     *
     * @return CommunicationInterface or null if no one interface is available
     */
    private CommunicationInterface getCommunicationInterface() {
        // Se há uma interface atual testa se ela possuí conexão
        if (currentCommunicationInterface != null) {
            try {
                if (currentCommunicationInterface.testConnection()) {
                    return currentCommunicationInterface;
                }
            } catch (IOException ex) {
                Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedOperationException ex) {
                Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Testa se outra interface possuí conexão, se sim ela passa a ser a atual e é retornada
        for (CommunicationInterface ci : communicationInterfaces) {
            try {
                if (ci.testConnection()) {
                    currentCommunicationInterface = ci;
                    return ci;
                }
            } catch (IOException ex) {
                Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedOperationException ex) {
                Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return null;
    }

    /**
     * Método utilizado para upload de relatos para um único servidor. Depois
     * fazer um para uploads múltiplos adicionando 2 métodos.
     * <ul><li>public static UploadService createUploadService(Agent Server)
     * throws InterruptException; </li>
     * <li>public synchronized addUploadService(UploadService up);</li>
     * <li> public boolean startUploadService(UploadService up);</li>
     * <li> UploadService up = createUploadService(Agent Server);</li>
     * <li> up.addReport();</li></ul>
     *
     */
    @Override
    public void run() {
        try {
            uploadService(uploadServer);
        } catch (InterruptedException ex) {
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
/**
 * 
 * @param server Adiciona o servidor que a função de upload dinâmico vai reportar.
 */
    public void addUploadServer(Agent server) {
        this.uploadServer = server;
    }

    /**
     * Este método é responsável por executar o processo de upload automático de
     * relatos. Idealmente, no futuro, poderia ser colocado em uma classe
     * separada que faça esse papel , o que permitiria fazer upload para
     * multiplos servidores, onde o run dessa classe inicia os servidores.
     *
     * @param server contém o agente que será usado de servidor.
     *
     */
    private void uploadService(Agent server) throws InterruptedException {
        this.running = true;
        final Integer waitUpload = 0;
        boolean sent;
        while (isUploadServerRunning()) {
            // [Início Loop]
            // Aguarda condição da política de upload ser atendida
            switch (this.uploadMessagingPolicy) {
                case 1: // Sempre que há um relato novo tenta fazer o upload, caso exista conexão, senão espera reconexão. Padrão.
                    break;
                case 2: // Em intervalos fixos. Pode ser definido pela aplicação. Intervalo inicial padrão a cada 15 segundos. Se não há conexão as mensagens são armazenadas.
                    synchronized (waitUpload) {
                        waitUpload.wait(uploadInterval);
                    }
                    break;
                case 3: // Está implementada no método ADDMessage. Exige confirmação da aplicação para upload dos relatos. Enquanto não confirmada comportamento na política 1.  
                    break;
                case 4: // Adaptativo. O componente de adaptação irá atribuir dinamicamente novos intervalos.
                    synchronized (waitUpload) {
                        waitUpload.wait(uploadInterval);
                    }
                    break;
            }
            // Busca uma mensagem da fila para a upload
            // Aguarda condições de dados móveis [Implementar outra hora] - Serviço compartilhado entre os servers
            MessageWrapper mw = this.mobileDataPolicy();
            // Adiciona o servidor visado
            mw.getMessage().setTarget(server);
            // 3 - Verifica se alguma interface de comunicação está disponível
            CommunicationInterface ci = this.getCommunicationInterface(); // Método traz a interface de comunicação atual
            // 4 - [disponível]    
            if (ci != null) {
                try {
                    // Executa função SendMessage
                    ci.sendMessage(this, mw);
                    // Remove a mensagem da fila
                    this.removeMessage(mw);
                    // Evento: Mensagem Entregue
                    this.newInternalEvent(EVENT_MESSAGE_DELIVERED, mw, mw.getMessage().getTarget(), currentCommunicationInterface);
                    // Política de Armazenamento
                    this.storagePolice(mw);
                    sent = true;
                } catch (java.net.ConnectException ex) {
                    // Evento: Timeout
                    // thows Timeout exception   
                    this.newInternalEvent(EVENT_ADDRESS_NOT_REACHABLE, mw, mw.getMessage().getTarget(), currentCommunicationInterface);
                    Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
                    sent = false;
                } catch (SocketTimeoutException ex) {
                    //[Timeout]
                    // Evento: Timeout
                    this.newInternalEvent(EVENT_ADDRESS_NOT_REACHABLE, mw, mw.getMessage().getTarget(), currentCommunicationInterface);
                    Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
                    sent = false;
                } catch (IOException ex) {
                    //[Erro de IO]
                    // Evento: Mensagem não entregue
                    this.newInternalEvent(EVENT_MESSAGE_NOT_DELIVERED, mw, mw.getMessage().getTarget());
                    Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
                    sent = false;
                }
            } else {
                // Evento desconexão
                this.newInternalEvent(EVENT_DISCONNECTION);
                sent = false;
            }

            // [Sem sucesso] [IO Exception] [Timeout]
            // Política de Armazenamento
            // [Aguarda Política de Reconexão]        
            if (!sent) {
                System.out.println("Starting Reconection Service");
                this.reconnectionService.reconectionProcess();
            }
        }
        // [Fim Loop]
    }

    /**
     * Função referente a política de dados móveis. Somente estratégias 1 e 2
     * funcionam, as demais necessitam de implementação
     *
     * @return
     * @throws InterruptedException
     */
    private MessageWrapper mobileDataPolicy() throws InterruptedException {
        // Política de dados móveis, se a interface usa dados móveis
        MessageWrapper mw = null;
        switch (mobileDataPolicy) {
            case 1: // Sem mobilidade. Configuração default. Nenhuma ação.
                mw = this.getMessage();
                break;
            case 2: // Fazer o uso sempre que possível. Nenhuma ação adicional.
                mw = this.getMessage();
                break;
            case 3: // Somente fazer uso com mensagens de alta prioridade.
                if (currentCommunicationInterface.isUsesMobileData()) {
                    mw = this.getPriorityMessage();
                } else {
                    mw = this.getMessage();
                }
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            // break;
            case 4: // Utiliza cota por ciclo de uso: Até X todos os tipos de mensagens, após até Y somente de alta prioridade.
                mw = this.getMessage();
                if (mw.getMessage().getPriority() == Message.PREFERENTIAL_PRIORITY) {
                    if ((mw.getEnvelopedMessage().length() * Character.SIZE) + usedMobileData >= mobileDataPriorityQuota) {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                } else {
                    if ((mw.getEnvelopedMessage().length() * Character.SIZE) + usedMobileData >= mobileDataQuota) {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                }
                break;
            case 5: // Utiliza cota por ciclo de uso: Até X todos os tipos de mensagens para mensagens de alta prioridade o uso é liberado.
                mw = this.getMessage();
                if ((mw.getEnvelopedMessage().length() * Character.SIZE) + usedMobileData >= mobileDataQuota) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
                break;
            case 6: // Não utilizar dados móveis.
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        return mw;
    }
}