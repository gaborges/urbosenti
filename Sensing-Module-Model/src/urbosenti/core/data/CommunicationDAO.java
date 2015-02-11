/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import urbosenti.core.communication.CommunicationInterface;
import urbosenti.core.communication.MessageWrapper;

/**
 *
 * @author Guilherme
 */
public class CommunicationDAO {
    
    public final static int  MOBILE_DATA_POLICY = 1;
    public final static int  MESSAGE_STORAGE_POLICY = 2;
    public final static int  RECONNECTION_POLICY = 3;
    public final static int  UPLOAD_MESSAGING_POLICY = 4;
    
    // Variáveis temporárias        
    private int mobileDataPolicy;  // Política de uso de dados móveis
    private int messageStoragePolicy; // Política de armazenamento de mensagem
    private int reconnectionPolicy; // Política de reconexão
    private int uploadMessagingPolicy; // política de Upload periódico de Mensagens

    public CommunicationDAO() {
        this.mobileDataPolicy = 1; // sem mobilidade - Default
        this.messageStoragePolicy = 2; // Política de armazenamento de mensagem - Padrão: Apagar todas que foram enviadas com sucesso e armazenar as que não foram enviadas. 
        this.reconnectionPolicy = 1;   // Política de reconexão: Padrão - Tentativa em intervalos fixos. Pode ser definido pela aplicação. O padrão é uma nova tentativa a cada 60 segundos
        this.uploadMessagingPolicy = 2; //  política de Upload periódico de Mensagens: Sempre que há um relato novo tenta fazer o upload, caso exista conexão, senão espera reconexão. Padrão.
        this.availableCommunicationInterfaces = new ArrayList<CommunicationInterface>();
        countIdMessages = 0;
        storedMessages = new ArrayList<MessageWrapper>();
    }
    
    private final List<CommunicationInterface> availableCommunicationInterfaces;
    
    // gambiarras sem banco
    private int countIdMessages;
    private final List<MessageWrapper> storedMessages;
    
    public void insertReport(MessageWrapper mw){
        this.countIdMessages++;
        mw.setId(countIdMessages);
        storedMessages.add(mw);
    }
    
    public void removeReport(MessageWrapper mw){
        int i = 0;
        for(;i<storedMessages.size();i++){
            if(storedMessages.get(i).getId() == mw.getId()){
                storedMessages.remove(i);
                break;
            }
        }
    }
    
    public void removeReport(int reportId){
        int i = 0;
        for(;i<storedMessages.size();i++){
            if(storedMessages.get(i).getId() == reportId){
                storedMessages.remove(i);
                break;
            }
        }
    }
    
    public void updateReport(MessageWrapper mw){
        int i = 0;
        for(;i<storedMessages.size();i++){
            if(storedMessages.get(i).getId() ==  mw.getId()){
                storedMessages.set(i, mw);
                break;
            }
        }
    }
    
    public MessageWrapper getReport(int id){
        int i = 0;
        for(;i<storedMessages.size();i++){
            if(storedMessages.get(i).getId() == id){
                return storedMessages.get(i);
            }
        }
        return null;
    }
    
    public int reportsStoredCount(){
        return storedMessages.size();
    }
    /**
     * @param timeToExpire will be used to say from the creation date plus the time to expire is bigger than the current date. 
     */
    public void removeAllExpiredReports(Date timeToExpire){
        int i = 0;
        for(;i<storedMessages.size();i++){
            if(((new Date()).getTime() + timeToExpire.getTime()) > storedMessages.get(i).getCreatedTime().getTime()){
                storedMessages.remove(i);
            }
        }
    }
    
    public int getCurrentPreferentialPolicy (int policyId){
        switch(policyId){
            case MOBILE_DATA_POLICY:
                return mobileDataPolicy;
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
    
    public List<CommunicationInterface> getAvailableInterfaces() throws IOException{
        /*
         * POde consultar no banco se há alguma existente, se existe instancia o objeto.
         */
        //        
//        // Poderia fazer uma busca no banco        
//        CommunicationInterface ci = new WiredCommunicationInterface(); // this is available
//        if(ci.isAvailable()) list.add(ci);
//        ci = new MobileDataCommunicationInterface(); // testar mais tarde
//        if(ci.isAvailable()) list.add(ci); 
//        ci = new DTNCommunicationInterface();
//        if(ci.isAvailable()) list.add(ci); 
//        ci = new WirelessCommunicationInterface();
//        if(ci.isAvailable()) list.add(ci); 
////        ci = new CommunicationInterface(); // Mais tarde
////        ci.setId(3);
////        ci.setName("Delay Tolerant Network (DTN) Interface");
////        ci.setUsesMobileData(false);
////        ci.setStatus(CommunicationInterface.STATUS_AVAILABLE);
////        ci = new CommunicationInterface(); // Mais tarde
////        ci.setId(4);
////        ci.setName("Wireless Interface Mobile Data Enabled");
////        ci.setUsesMobileData(true);
////        ci.setStatus(CommunicationInterface.STATUS_UNAVAILABLE);
        return availableCommunicationInterfaces;
    }   

    
    /**
     *  adiciona uma nova interface de comunicação. Se ela existe ela é setada como suportada, se não existe ela é adicionada.
     *  No caso em questão todas já existem. Adicionar a nova possíbilidade em futuras implementações.;
     * @param ci
     */
    protected void addAvailableCommunicationInterface(CommunicationInterface ci) {
        ci.setStatus(CommunicationInterface.STATUS_AVAILABLE);
        /*
         * Persiste no banco de dados. Se já existe somente atualiza.
         */
        availableCommunicationInterfaces.add(ci);
    }
    
    
    
}
