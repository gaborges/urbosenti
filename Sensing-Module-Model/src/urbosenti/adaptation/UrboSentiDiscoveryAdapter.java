/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.adaptation;

import urbosenti.context.ContextManager;
import urbosenti.core.device.BaseComponentManager;
import urbosenti.core.device.DeviceManager;
import urbosenti.core.device.model.Device;
import urbosenti.user.UserManager;

/**
 *
 * @author Guilherme
 */
public class UrboSentiDiscoveryAdapter extends DiscoveryAdapter {
    private DeviceManager deviceManager;
    private ContextManager contextManager;
    private UserManager userManager;

    @Override
    public Device discovery(BaseComponentManager baseComponentManager) {
        
        this.deviceManager = (DeviceManager) baseComponentManager;
        this.contextManager = deviceManager.getContextManager();
        this.userManager = deviceManager.getUserManager();
        
        // descobre o modelo
        // Device
            // Componentes em funcionamento
            // sensores e possíveis atuadores de cada componentes
            // Políticas e estratédias (funcionalidades/comportamentos), define restrições
            // Events, communication, data
        // User, se ativo
            // Preferências do usuário, para privacidade, etc... -- Podem ser alterados on the fly
            // Restrições do usuário
        // Context, se ativo
            // Descoberta de funções de contexto
                // Predição de contextos
                // Gerar conhecimento
                // Modelos de aprendizagem
                // Inferência
            // Apoio a descoberta e identificação de novos recursos
            // Possibilita gatinhos de eventos dinâmicos, como tempo etc...
        return null;
    }
    
}
