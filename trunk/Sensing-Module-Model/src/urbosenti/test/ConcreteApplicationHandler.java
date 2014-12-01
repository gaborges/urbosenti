/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.test;

import urbosenti.core.device.DeviceManager;
import urbosenti.core.events.ApplicationHandler;
import urbosenti.core.events.Event;

/**
 *
 * @author Guilherme
 */
public class ConcreteApplicationHandler extends ApplicationHandler{

    public ConcreteApplicationHandler(DeviceManager deviceManager) {
        super(deviceManager);
    }

    @Override
    public void newEvent(Event event) {
        System.out.println("New application event: "+event.toString());
    }
    
}
