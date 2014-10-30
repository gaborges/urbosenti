/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.communication;

import java.io.StringWriter;
import java.util.Date;
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

/**
 *
 * @author Guilherme
 */
public class MessageWrapper {
    private int id; // id of in te local storage system
    private Message message;
    private int timeout;
    private String envelopedMessage;
    private Date createdTime;
    private Date sentTime;
    private boolean checked; // Se foi checada pela aplicação
    private boolean sent;
    
    // Critérios utilizados para avaliação ao enviar a mensagem
    private int size; // number of characters
    private int responseTime; // milliseconds
        
    private CommunicationInterface usedCommunicationInterface;
    
    

    public MessageWrapper(Message message) {
        this.message = message;
        this.checked = false;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked() {
        this.checked = true;
    }
    
    public void setUnChecked() {
        this.checked = false;
    }
    
    public int getSize() {
        return size;
    }

    public double getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(int responseTime) {
        this.responseTime = responseTime;
    }

    public CommunicationInterface getUsedCommunicationInterface() {
        return usedCommunicationInterface;
    }

    public void setUsedCommunicationInterface(CommunicationInterface usedCommunicationInterface) {
        this.usedCommunicationInterface = usedCommunicationInterface;
    }    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getEnvelopedMessage() {
        return envelopedMessage;
    }

    public void setEnvelopedMessage(String envelopedMessage) {
        this.envelopedMessage = envelopedMessage;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public Date getSentTime() {
        return sentTime;
    }

    public void setSentTime(Date sentTime) {
        this.sentTime = sentTime;
    }

    void build() throws ParserConfigurationException, TransformerConfigurationException, TransformerException {
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
            
            this.envelopedMessage =  stw.getBuffer().toString();
            this.size = this.envelopedMessage.length();
            this.createdTime = new Date();
    }
    
    public static MessageWrapper createAndBuild(Message m) throws ParserConfigurationException, TransformerConfigurationException, TransformerException{
        MessageWrapper messageWrapper = new MessageWrapper(m);
        messageWrapper.build();
        return messageWrapper;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
}
