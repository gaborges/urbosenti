/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.communication;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
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
public class CommunicationManager extends ComponentManager implements AsynchronouslyManageableComponent {

    private Queue<MessageWrapper> normalMessageQueue;
    private Queue<MessageWrapper> priorityMessageQueue;
    
    private double mobileDataQuota; // em bytes
    private double usedMobileData; // em bytes
    private double mobileDataPriorityQuota; // em bytes
    
    private int mobileDataPolicy;  // Política de uso de dados móveis
    private int messagingPolicy;   // Política de envio de mensagem
    private int messageStoragePolicy; // Política de armazenamento de mensagem
    private int reconnectionPolicy; // Política de reconexão
    private int uploadMessagingPolicy; // política de Upload periódico de Mensagens
    
    private CommunicationInterface currentCommunicationInterface;
    
    private List<CommunicationInterface> communicationInterfaces;
    // interfaces[]
    // tecnology
    public CommunicationManager(DeviceManager deviceManager) {
        super(deviceManager);
        this.normalMessageQueue = new LinkedList<MessageWrapper>();
        this.priorityMessageQueue = new LinkedList<MessageWrapper>();
    }
    
    public boolean verifyConnectStatus(){
        // Verifica se a principal está conectada
        if(currentCommunicationInterface.getStatus() == CommunicationInterface.STATUS_CONNECTED) return true;
        else{
            // gera o evento de desconexão da interface atual
            for(CommunicationInterface ci :communicationInterfaces){
                if(ci.getStatus() == CommunicationInterface.STATUS_CONNECTED){
                    currentCommunicationInterface = ci;
                    return true;
                }
            }
            // gera o evento que nenhuma interface está disponível
        }
        return false;
    }

    @Override
    public void onCreate() {
        // Carregar dados e configurações que serão utilizados para execução em memória
        // Preparar configurações inicias para execução
        // Para tanto utilizar o DataManager para acesso aos dados.
        System.out.println("Activating: " + getClass());
        this.communicationInterfaces = super.getDeviceManager().getDataManager().getCommunicationDAO().getAvailableInterfaces();
        
//        
//        this.mobileDataPolicy = 1; // sem mobilidade - Default
//        this.messagingPolicy = 1;  // Se não der certo avisa a origem da mensagem
//        this.messageStoragePolicy = 2; // Política de armazenamento de mensagem - Padrão: Apagar todas que foram enviadas com sucesso e armazenar as que não foram enviadas. 
//        this.reconnectionPolicy = 1;   // Política de reconexão: Padrão - Tentativa em intervalos fixos. Pode ser definido pela aplicação. O padrão é uma nova tentativa a cada 60 segundos
//        this.uploadMessagingPolicy = 2; //  política de Upload periódico de Mensagens: Sempre que há um relato novo tenta fazer o upload, caso exista conexão, senão espera reconexão. Padrão.
//        
        this.mobileDataPolicy = super.getDeviceManager().getDataManager().getCommunicationDAO().getCurrentPreferentialPolicy(CommunicationDAO.MOBILE_DATA_POLICY); // sem mobilidade - Default
        this.messagingPolicy = super.getDeviceManager().getDataManager().getCommunicationDAO().getCurrentPreferentialPolicy(CommunicationDAO.MESSAGING_POLICY);  // Se não der certo avisa a origem da mensagem
        this.messageStoragePolicy = super.getDeviceManager().getDataManager().getCommunicationDAO().getCurrentPreferentialPolicy(CommunicationDAO.MESSAGE_STORAGE_POLICY); // Política de armazenamento de mensagem - Padrão: Apagar todas que foram enviadas com sucesso e armazenar as que não foram enviadas. 
        this.reconnectionPolicy = super.getDeviceManager().getDataManager().getCommunicationDAO().getCurrentPreferentialPolicy(CommunicationDAO.RECONNECTION_POLICY);   // Política de reconexão: Padrão - Tentativa em intervalos fixos. Pode ser definido pela aplicação. O padrão é uma nova tentativa a cada 60 segundos
        this.uploadMessagingPolicy = super.getDeviceManager().getDataManager().getCommunicationDAO().getCurrentPreferentialPolicy(CommunicationDAO.UPLOAD_MESSAGING_POLICY); //  política de Upload periódico de Mensagens: Sempre que há um relato novo tenta fazer o upload, caso exista conexão, senão espera reconexão. Padrão.
        
        // Setar dinamica a política de mobile data police aqui.
        // se há politica deve ser setado através das configurações:
        this.mobileDataQuota = 1000;
        this.usedMobileData = 0;
        this.mobileDataPriorityQuota = 2000;
        // Setar dinamicamente a politica de mensagens   
    }

    @Override
    public void applyAction(Action action) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void sendMessage(Message message) {

        if (message.getSender() == null) {
            message.setSender(new Agent());
            message.getSender().setLayer("application");
            message.getSender().setUid(getDeviceManager().getUID());
            message.getSender().setDescription("Sensing Module");
        }
        MessageWrapper messageWrapper = new MessageWrapper(message);
        try {
            // gerar mensagem em XML
            messageWrapper.build();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        // verifica se alguma conexão estão conectada
        if(verifyConnectStatus()){
            
            // Política de dados móveis, se a interface usa dados móveis
            if(this.currentCommunicationInterface.isUsesMobileData()){
                switch(mobileDataPolicy){
                    case 1: // Sem mobilidade. Configuração default. Nenhuma ação.
                        break;
                    case 2: // Fazer o uso sempre que possível. Nenhuma ação adicional.
                        break;
                    case 3: // Somente fazer uso com mensagens de alta prioridade.
                         if(message.getPriority() != Message.PREFERENTIAL_PRIORITY){ // se não for prioritária
                             // politica de armazenamento
                             // adiciona na fila para upload
                             // gera o evento para a aplicação e a adaptação
                             return;
                         }                        
                        break;
                    case 4: // Utiliza cota por ciclo de uso: Até X todos os tipos de mensagens, após até Y somente de alta prioridade.
                        if(message.getPriority() == Message.PREFERENTIAL_PRIORITY){
                            if((messageWrapper.getEnvelopedMessage().length() * Character.SIZE) + usedMobileData >= mobileDataPriorityQuota){
                                // politica de armazenamento
                                // fila para upload se salvo
                                // gera o evento (aplicação e sistema)
                                return;
                            }
                        }else{
                            if((messageWrapper.getEnvelopedMessage().length() * Character.SIZE) + usedMobileData >= mobileDataQuota){
                                // politica de armazenamento
                                // fila para upload se salvo
                                // gera o evento (aplicação e sistema)
                                return;
                            }
                        }
                        break;
                    case 5: // Utiliza cota por ciclo de uso: Até X todos os tipos de mensagens para mensagens de alta prioridade o uso é liberado.
                        if((messageWrapper.getEnvelopedMessage().length() * Character.SIZE) + usedMobileData >= mobileDataQuota){
                                // politica de armazenamento
                                // fila para upload se salvo
                                // gera o evento (aplicação e sistema)
                                return;
                        }
                        break;
                    case 6: // Não utilizar dados móveis.
                        // politica de armazenamento
                        // fila para upload se salvo
                        // gera o evento (aplicação e sistema)
                        return;
                }
            }
            

            // fazer a mensagem para envio via HTTP
            try {
                // Enviar a mensagem
                DeliveryMessagingService service = new DeliveryMessagingService(this);
                boolean res = service.sendHttpMessage(
                        message.getTarget().getAddress(),
                        message.getContentType(),
                        messageWrapper.getEnvelopedMessage());

                if(res){
                    // evento mensagem entregue
                    // Politica de armazenamento de mensagens
                } else {
                    // evento mensagem não entregue. Motivo? 
                    // Politica de armazenamento de mensagens
                    // adicionar na fila de envio
                }


            } catch (MalformedURLException ex) {
                Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (RuntimeException ex) {
            }
        } else {
            // storage policy
            // add to the list
        }
    }

    public String sendMessageWithResponse(Message message) {
        try {
            if (message.getSender() == null) {
                message.setSender(new Agent());
                message.getSender().setLayer("application");
                message.getSender().setUid(getDeviceManager().getUID());
                message.getSender().setDescription("Sensing Module");
            }
            // fazer a mensagem para envio via HTTP


            // gerar mensagem em XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            // Criar o documento e com verte a String em DOC
            Document doc = builder.newDocument();
            Element root = doc.createElement("message"),
                    content = doc.createElement("conteudo"),
                    header = doc.createElement("header"),
                    origin = doc.createElement("origin"),
                    target = doc.createElement("target"),
                    layer = doc.createElement("layer"),
                    uid = doc.createElement("uid"),
                    address = doc.createElement("address"),
                    name = doc.createElement("name");

            // origin
            uid.setTextContent(message.getSender().getUid());
            name.setTextContent(message.getSender().getDescription());
            layer.setTextContent(message.getSender().getLayer());
            origin.appendChild(uid);
            origin.appendChild(name);
            origin.appendChild(layer);

            // target
            layer = doc.createElement("layer");
            uid = doc.createElement("uid");
            name = doc.createElement("name");
            uid.setTextContent(message.getTarget().getUid());
            name.setTextContent(message.getTarget().getDescription());
            layer.setTextContent(message.getTarget().getLayer());
            address.setTextContent(message.getTarget().getAddress());
            target.appendChild(uid);
            target.appendChild(name);
            target.appendChild(layer);
            target.appendChild(address);

            // header
            Element subject = doc.createElement("subject"),
                    contentType = doc.createElement("contentType");
            contentType.setTextContent(message.getContentType());
            subject.setTextContent(message.getSubject());
            header.appendChild(subject);
            header.appendChild(contentType);
            header.appendChild(origin);
            header.appendChild(target);

            // content
            content.setTextContent(message.getContent());

            root.appendChild(header);
            root.appendChild(content);
            doc.appendChild(root);

            // Converter Documento para STRING
            StringWriter stw = new StringWriter();
            Transformer serializer = TransformerFactory.newInstance().newTransformer();
            serializer.transform(new DOMSource(doc), new StreamResult(stw));

            // Enviar a mensagem
            DeliveryMessagingService service = new DeliveryMessagingService(this);
            return service.sendHttpMessageReturn(message.getTarget().getAddress(), message.getContentType(), stw.getBuffer().toString());

            // modelar métodos de conexão e comunicação
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String sendMessageWithResponse(Message message, int timeout) {
        // fazer a mensagem para envio via HTTP
        return null;
    }

    public void sendReport(Message message) {
    }

    protected void newPushMessage(String bruteMessage) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Criar o documento e com verte a String em DOC
            Document doc = builder.parse(new InputSource(new StringReader(bruteMessage)));

//            MessageHeader msgHeader = new MessageHeader();
            Message msg = new Message();
            msg.setSender(new Agent());
            msg.setTarget(new Agent());
            Element response = doc.getDocumentElement();
            Element header = (Element) response.getElementsByTagName("header").item(0);
//            msgHeader.setSenderUid(((Element) header.getElementsByTagName("origin").item(0)).getElementsByTagName("uid").item(0).getTextContent());
//            msgHeader.setSenderName(((Element) header.getElementsByTagName("origin").item(0)).getElementsByTagName("name").item(0).getTextContent());
//            msgHeader.setSenderAddress(((Element) header.getElementsByTagName("origin").item(0)).getElementsByTagName("address").item(0).getTextContent());
//            msgHeader.setSenderLayer(((Element) header.getElementsByTagName("origin").item(0)).getElementsByTagName("layer").item(0).getTextContent());
//            msgHeader.setTargetUid(((Element) header.getElementsByTagName("target").item(0)).getElementsByTagName("uid").item(0).getTextContent());
//            msgHeader.setTargetAddress(((Element) header.getElementsByTagName("target").item(0)).getElementsByTagName("address").item(0).getTextContent());
//            msgHeader.setTargetLayer(((Element) header.getElementsByTagName("target").item(0)).getElementsByTagName("layer").item(0).getTextContent());
//            msgHeader.setContentType( header.getElementsByTagName("contentType").item(0).getTextContent());
//            msgHeader.setSubject(header.getElementsByTagName("subject").item(0).getTextContent());

            msg.getSender().setUid(((Element) header.getElementsByTagName("origin").item(0)).getElementsByTagName("uid").item(0).getTextContent());
            msg.getSender().setDescription(((Element) header.getElementsByTagName("origin").item(0)).getElementsByTagName("name").item(0).getTextContent());
            msg.getSender().setAddress(((Element) header.getElementsByTagName("origin").item(0)).getElementsByTagName("address").item(0).getTextContent());
            msg.getSender().setLayer(((Element) header.getElementsByTagName("origin").item(0)).getElementsByTagName("layer").item(0).getTextContent());
            msg.getTarget().setUid(((Element) header.getElementsByTagName("target").item(0)).getElementsByTagName("uid").item(0).getTextContent());
            msg.getTarget().setAddress(((Element) header.getElementsByTagName("target").item(0)).getElementsByTagName("address").item(0).getTextContent());
            msg.getTarget().setLayer(((Element) header.getElementsByTagName("target").item(0)).getElementsByTagName("layer").item(0).getTextContent());
            msg.setContentType(header.getElementsByTagName("contentType").item(0).getTextContent());
            msg.setSubject(header.getElementsByTagName("subject").item(0).getTextContent());

            if (header.getElementsByTagName("priority").getLength() > 0) {
                if (header.getElementsByTagName("priority").item(0).getTextContent().equals("preferential")) {
                    msg.setPreferentialPriority();
                } else {
                    msg.setNormalPriority();
                }
            }

            msg.setContent(response.getElementsByTagName("content").item(0).getTextContent());
//            msg.setHeader(msgHeader);
//
//            Agent origin = new Agent();
//            origin.setAddress(msgHeader.getSenderAddress());
//            origin.setDescription(msgHeader.getSenderName());
//            origin.setLayer(msgHeader.getSenderLayer());
//            origin.setUid(msgHeader.getSenderUid());

            System.out.println("Layer: " + msg.getTarget().getLayer());
            Event event = null;
            if (msg.getTarget().getLayer().equals("system")) { // if the target is the system
                event = new SystemEvent(msg.getSender());
            } else if (msg.getTarget().getLayer().equals("application")) { // if the target is the application
                event = new ApplicationEvent(msg.getSender());
            }
            if (event != null) { // Event: new Message
                event.setId(1);
                event.setName("new message");
                event.setTime(new Date());
                event.setValue(msg);
                getEventManager().newEvent(event);
            } else {
                // do one error message -- create it!
            }
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CommunicationManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    protected void storagePolice(MessageWrapper messageWrapper){
    
    }
    
    private synchronized void addMessage(MessageWrapper messageWrapper){
        if(messageWrapper.getMessage().getPriority() == Message.PREFERENTIAL_PRIORITY){
            this.priorityMessageQueue.add(messageWrapper);
        } else {
            this.normalMessageQueue.add(messageWrapper);
        }
        notifyAll();
    }
    
    public synchronized MessageWrapper getNormalMessage() throws InterruptedException{
        MessageWrapper mw;
        while(true){
            mw = this.normalMessageQueue.peek();
            if(mw == null) wait();
            else break;
        }
        return  mw;
    }
    
    public synchronized MessageWrapper getPriorityMessage() throws InterruptedException{
        MessageWrapper mw;
        while(true){
            mw = this.priorityMessageQueue.peek();
            if(mw == null) wait();
            else break;
        }
        return  mw;
    }
    
    public synchronized MessageWrapper pollNormalMessage() throws InterruptedException{
        MessageWrapper mw;
        while(true){
            mw = this.normalMessageQueue.poll();
            if(mw == null) wait();
            else break;
        }
        return  mw;
    }
    
    public synchronized MessageWrapper pollPriorityMessage() throws InterruptedException{
        MessageWrapper mw;
        while(true){
            mw = this.priorityMessageQueue.poll();
            if(mw == null) wait();
            else break;
        }
        return  mw;
    }
    
    
    public synchronized MessageWrapper pollMessage() throws InterruptedException{
        MessageWrapper mw;
        while(true){
            mw = this.priorityMessageQueue.poll();
            if(mw == null) wait();
            else break;
        }
        return  mw;
    }
    
    
}
