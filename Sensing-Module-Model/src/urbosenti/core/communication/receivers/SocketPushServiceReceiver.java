/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.communication.receivers;

import urbosenti.core.communication.CommunicationManager;
import urbosenti.core.communication.PushServiceReceiver;
import urbosenti.core.device.model.Agent;

/**
 *
 * @author Guilherme
 */
public class SocketPushServiceReceiver extends PushServiceReceiver{

    public SocketPushServiceReceiver(CommunicationManager communicationManager) {
        super(communicationManager);
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
      
          // When have a new message
        String message = "<message>\n" +
"	<header>\n" +
"		<origin>\n" +
"			<uid>11XYZ</uid>\n" +
"			<name>Backend Module</name>\n" +
"			<address>192.168.0.1</address>\n" +
"			<layer>system</layer>\n" +
"               </origin>\n" +
"               <target>\n" +
"			<uid>22XYZ</uid>\n" +
"                       <address>192.168.0.2</address>\n" +
" 			<layer>system</layer>\n" +
"               </target>\n" +
"               <subject>social interaction</subject>\n" +
"               <contentType>text/xml</contentType>\n" +
"	</header>\n" +
"	<content>" +
"		… message according the subject" +
"       </content>" +
"     </message>";
        Agent origin = new Agent();
        origin.setServiceAddress("http://exemplo:8084/TestServer/webresources/test/return");
        super.communicationManager.newPushMessage(origin,message);
    }
    
    
    
}
