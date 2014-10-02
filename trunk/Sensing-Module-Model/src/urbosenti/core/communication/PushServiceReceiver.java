/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.communication;

/**
 *
 * @author Guilherme
 */
public class PushServiceReceiver {
    
    private CommunicationManager communicationManager;

    public PushServiceReceiver(CommunicationManager communicationManager) {
        this.communicationManager = communicationManager;
    }
        
    private void run(){ // Only a representative method - If i don't make a mistake, this method will be private ou protected
    
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
        this.communicationManager.newPushMessage(message);
    }
    
    public void startPushReceiverService() {
        // Create a Service to receive Push Messages in text format
        run();
    }

    public void stopPushReceiverService() {
        // stop the service
    }
}
