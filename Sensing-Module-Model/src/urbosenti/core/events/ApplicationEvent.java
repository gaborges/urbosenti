/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.core.events;

import urbosenti.core.device.model.Agent;
import urbosenti.core.device.ComponentManager;
import urbosenti.core.device.model.Instance;

/**
 *
 * @author Guilherme
 */
public class ApplicationEvent extends Event {

    private Instance effectingInstance;

    public ApplicationEvent(Object origin) {
        super(origin, Event.APPLICATION_EVENT);
        effectingInstance = null;
    }

    public ApplicationEvent(Agent origin) {
        super(origin, Event.APPLICATION_EVENT);
        effectingInstance = null;
    }

    public ApplicationEvent(ComponentManager origin) {
        super(origin, Event.APPLICATION_EVENT);
        effectingInstance = null;
    }

    /**
     * Retorna a instância que está agindo sobre as variáveis e realizando o
     * efeito. Se não for adicionado o evento é considerado geral da aplicação,
     * isto é independe da instância de usuário afetando esse evento. Caso
     * exista alguma instância agindo neste evento ela deve ser adicionada.
     *
     * @return retorna Null se a instância não foi atribuída.
     */
    public Instance getEffectingInstance() {
        return effectingInstance;
    }

    public void setEffectingInstance(Instance effectingInstance) {
        this.effectingInstance = effectingInstance;
    }

}
