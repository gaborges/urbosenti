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
import urbosenti.core.events.Action;
import urbosenti.core.events.ApplicationEvent;
import urbosenti.core.events.Event;
import urbosenti.core.events.EventManager;
import urbosenti.core.events.SystemEvent;

/**
 *
 * @author Guilherme
 */
public class CommunicationManager extends ComponentManager {

    public CommunicationManager(EventManager eventManager) {
        super(eventManager);
    }

    @Override
    protected void onCreate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void applyAction(Action action) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void startPushReceiverService() {
        // Create a Service to receive Push Messages in text format
    }

    public void stopPushReceiverService() {
        // stop the service
    }

    protected void newPushMessage(String bruteMessage) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Criar o documento e com verte a String em DOC
            Document doc = builder.parse(new InputSource(new StringReader(bruteMessage)));

            MessageHeader msgHeader = new MessageHeader();
            Message msg = new Message();
            Element response = doc.getDocumentElement();
            Element header = (Element) response.getElementsByTagName("header").item(0);
            msgHeader.setSenderUid(((Element) header.getElementsByTagName("origin").item(0)).getElementsByTagName("uid").item(0).getTextContent());
            msgHeader.setSenderName(((Element) header.getElementsByTagName("origin").item(0)).getElementsByTagName("name").item(0).getTextContent());
            msgHeader.setSenderAddress(((Element) header.getElementsByTagName("origin").item(0)).getElementsByTagName("address").item(0).getTextContent());
            msgHeader.setSenderLayer(((Element) header.getElementsByTagName("origin").item(0)).getElementsByTagName("layer").item(0).getTextContent());
            msgHeader.setSenderName(((Element) header.getElementsByTagName("target").item(0)).getElementsByTagName("uid").item(0).getTextContent());
            msgHeader.setSenderAddress(((Element) header.getElementsByTagName("target").item(0)).getElementsByTagName("address").item(0).getTextContent());
            msgHeader.setSenderLayer(((Element) header.getElementsByTagName("target").item(0)).getElementsByTagName("layer").item(0).getTextContent());
            msgHeader.setContentType( header.getElementsByTagName("contentType").item(0).getTextContent());
            msgHeader.setSubject(header.getElementsByTagName("subject").item(0).getTextContent());

            msg.setContent(response.getElementsByTagName("conteudo").item(0).getTextContent());
            msg.setHeader(msgHeader);

            Agent origin = new Agent();
            origin.setAddress(msgHeader.getSenderAddress());
            origin.setDescription(msgHeader.getSenderName());
            origin.setLayer(msgHeader.getSenderLayer());
            origin.setUid(msgHeader.getSenderUid());
            
            Event event = null;
            if(msg.getHeader().getTargetLayer().equals("system")){ // if the target is the system
                event = new SystemEvent(origin);
            } else if (msg.getHeader().getTargetLayer().equals("application")){ // if the target is the application
                event = new ApplicationEvent(origin);
            }
            if(event != null){ // Event: new Message
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
