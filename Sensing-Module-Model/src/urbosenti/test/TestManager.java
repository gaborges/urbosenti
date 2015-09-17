/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.test;

import urbosenti.core.device.ComponentManager;
import urbosenti.core.device.model.FeedbackAnswer;
import urbosenti.core.events.Action;

/**
 *
 * @author Guilherme
 */
public class TestManager extends ComponentManager{

    
    @Override
    public void onCreate() {
        
    }

    @Override
    public FeedbackAnswer applyAction(Action action) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
