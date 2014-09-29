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
        
    public void run(){ // Only a representative method
    
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
"                       <addess>192.168.0.2</addess>\n" +
" 			<layer>system</layer>\n" +
"               </target>\n" +
"               <subject>social interaction<subject>\n" +
"               <contentType>text/xml</contentType>\n" +
"	</header>\n" +
"	<content>" +
"		… message according the subject" +
"       </content>";
        this.communicationManager.newPushMessage(message);
    }
}
