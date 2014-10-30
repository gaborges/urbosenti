/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.data;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import urbosenti.core.communication.CommunicationInterface;
import urbosenti.core.communication.MessageWrapper;

/**
 *
 * @author Guilherme
 */
public class CommunicationDAO {
    
    public final static int  MOBILE_DATA_POLICY = 1;
    public final static int  MESSAGING_POLICY = 2;
    public final static int  MESSAGE_STORAGE_POLICY = 3;
    public final static int  RECONNECTION_POLICY = 4;
    public final static int  UPLOAD_MESSAGING_POLICY = 5;
    
    // Variáveis temporárias        
    private int mobileDataPolicy;  // Política de uso de dados móveis
    private int messagingPolicy;   // Política de envio de mensagem
    private int messageStoragePolicy; // Política de armazenamento de mensagem
    private int reconnectionPolicy; // Política de reconexão
    private int uploadMessagingPolicy; // política de Upload periódico de Mensagens

    public CommunicationDAO() {
        this.mobileDataPolicy = 1; // sem mobilidade - Default
        this.messagingPolicy = 1;  // Se não der certo avisa a origem da mensagem
        this.messageStoragePolicy = 2; // Política de armazenamento de mensagem - Padrão: Apagar todas que foram enviadas com sucesso e armazenar as que não foram enviadas. 
        this.reconnectionPolicy = 1;   // Política de reconexão: Padrão - Tentativa em intervalos fixos. Pode ser definido pela aplicação. O padrão é uma nova tentativa a cada 60 segundos
        this.uploadMessagingPolicy = 2; //  política de Upload periódico de Mensagens: Sempre que há um relato novo tenta fazer o upload, caso exista conexão, senão espera reconexão. Padrão.
    }
    
    public void insertMessage(MessageWrapper mw){
    
    }
    
    public void removeMessage(MessageWrapper mw){
    
    }
    
    public void updateMessage(MessageWrapper mw){
    
    }
    
    public MessageWrapper getMessageWrapper(int id){
        return null;
    }
    
    public int getCurrentPreferentialPolicy (int policyId){
        switch(policyId){
            case MOBILE_DATA_POLICY:
                return mobileDataPolicy;
            case MESSAGING_POLICY:
                return messagingPolicy;
            case MESSAGE_STORAGE_POLICY:
                return messageStoragePolicy;
            case RECONNECTION_POLICY:
                return reconnectionPolicy;
            case UPLOAD_MESSAGING_POLICY:
                return uploadMessagingPolicy;
        }
        return 0;
    }
    
    // POde ter um evento associado para reconfiguração
    public void updatePreferentialPolicy(int policyId, int newValue){
        switch(policyId){
            case MOBILE_DATA_POLICY:
                mobileDataPolicy = newValue;
                break;
            case MESSAGING_POLICY:
                messagingPolicy = newValue;
                break;
            case MESSAGE_STORAGE_POLICY:
                messageStoragePolicy = newValue;
                break;
            case RECONNECTION_POLICY:
                reconnectionPolicy = newValue;
                break;
            case UPLOAD_MESSAGING_POLICY:
                uploadMessagingPolicy = newValue;
                break;
        }
    }
    
    public List<CommunicationInterface> getAvailableInterfaces(){
        List<CommunicationInterface> list = new ArrayList<CommunicationInterface>();
        CommunicationInterface ci = new CommunicationInterface();
//        ci.setId(1);
//        ci.setName("Wired Interface");
//        ci.setUsesMobileData(false);
//        ci.setStatus(CommunicationInterface.STATUS_UNAVAILABLE);
//        list.add(ci);
        ci = new CommunicationInterface();
        ci.setId(2);
        ci.setName("Wireless Interface");
        ci.setUsesMobileData(false);
        ci.setStatus(CommunicationInterface.STATUS_AVAILABLE);
        list.add(ci);
//        ci = new CommunicationInterface(); // Mais tarde
//        ci.setId(3);
//        ci.setName("Delay Tolerant Network (DTN) Interface");
//        ci.setUsesMobileData(false);
//        ci.setStatus(CommunicationInterface.STATUS_AVAILABLE);
//        ci = new CommunicationInterface(); // Mais tarde
//        ci.setId(4);
//        ci.setName("Wireless Interface Mobile Data Enabled");
//        ci.setUsesMobileData(true);
//        ci.setStatus(CommunicationInterface.STATUS_UNAVAILABLE);
        return list;
    }   
    
}
