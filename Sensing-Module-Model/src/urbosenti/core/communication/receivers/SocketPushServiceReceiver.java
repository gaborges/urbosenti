/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.communication.receivers;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import urbosenti.core.communication.CommunicationManager;
import urbosenti.core.communication.PushServiceReceiver;

/**
 *
 * @author Guilherme
 */
public class SocketPushServiceReceiver extends PushServiceReceiver {

    private Integer port;
    private ServerSocket serverSocket;

    public SocketPushServiceReceiver(CommunicationManager communicationManager) {
        super(communicationManager);
        super.setId(1);
        super.setDescription("Socket Input Interface");
        this.port = 556677;
    }

    @Override
    public void stop() {
        super.stop(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        try {            
            // When have a new message
            String message
                    = "<message>\n"
                    + "	<header>\n"
                    + "		<origin>\n"
                    + "			<uid>11XYZ</uid>\n"
                    + "			<layer>2</layer>\n"
                    + "               </origin>\n"
                    + "               <target>\n"
                    + "			<uid>22XYZ</uid>\n"
                    + " 			<layer>2</layer>\n"
                    + "               </target>\n"
                    + "               <priority>1</priority>\n"
                    + "               <subject>4</subject>\n"
                    + "               <contentType>text/xml</contentType>\n"
                    + "               <contentSize>29</contentSize>\n"
                    + "               <anonymousUpload>false</anonymousUpload>\n"
                    + "	</header>\n"
                    + "	<content>message according the subject</content>\n"
                    + "</message>";
            super.communicationManager.newPushMessage("http://exemplo:8084/TestServer/webresources/test/return", message);
            while (this.getStatus() == STATUS_LISTENING) {
                if(!serverSocket.isBound()){
                    serverSocket = new ServerSocket(port);
                    this.getInterfaceConfigurations().put("ipv4Address",this.serverSocket.getInetAddress().getHostAddress());
                    super.communicationManager.updateInputCommunicationInterfaceConfiguration(this,this.getInterfaceConfigurations());
                }
                try (Socket accept = this.serverSocket.accept()) {
                    DataInputStream dataInputStream = new DataInputStream(accept.getInputStream());
                    message = dataInputStream.readUTF();
                    super.communicationManager.newPushMessage(accept.getInetAddress().getHostAddress(), message);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(SocketPushServiceReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void addressDiscovery() throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.getInterfaceConfigurations().put("ipv4Address",this.serverSocket.getInetAddress().getHostAddress());
        this.getInterfaceConfigurations().put("port", port.toString());
    }

}
