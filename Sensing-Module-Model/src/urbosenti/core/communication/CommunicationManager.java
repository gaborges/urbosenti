/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.communication;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
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

    // interfaces[]
        // tecnology
    
    
    public CommunicationManager(DeviceManager deviceManager) {
        super(deviceManager);
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

    public void sendMessage(Message message) {
        // fazer a mensagem para envio via HTTP
        //gerar mensagem em XML
        // modelar métodos de conexão e comunicação
    }

    public String sendMessageWithResponse(Message message) {
        // fazer a mensagem para envio via HTTP
        return null;
    }

    public String sendMessageWithResponse(Message message, int timeout) {
        // fazer a mensagem para envio via HTTP
        return null;
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
                if(header.getElementsByTagName("priority").item(0).getTextContent().equals("preferential"))
                    msg.setPreferentialPriority();
                else 
                    msg.setNormalPriority();
            }

            msg.setContent(response.getElementsByTagName("content").item(0).getTextContent());
//            msg.setHeader(msgHeader);
//
//            Agent origin = new Agent();
//            origin.setAddress(msgHeader.getSenderAddress());
//            origin.setDescription(msgHeader.getSenderName());
//            origin.setLayer(msgHeader.getSenderLayer());
//            origin.setUid(msgHeader.getSenderUid());

            System.out.println("Layer: " + msg.getHeader().getTargetLayer());
            Event event = null;
            if (msg.getHeader().getTargetLayer().equals("system")) { // if the target is the system
                event = new SystemEvent(msg.getSender());
            } else if (msg.getHeader().getTargetLayer().equals("application")) { // if the target is the application
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
}
