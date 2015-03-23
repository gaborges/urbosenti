/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.test;

import java.io.File;
import java.text.NumberFormat;
import java.util.HashMap;
import urbosenti.core.device.OperatingSystemDiscovery;

/**
 *
 * @author Guilherme
 */
public class DesktopOperationalSystemDiscovery implements OperatingSystemDiscovery{

    @Override
    public HashMap<String, Object> discovery() {
        Runtime runtime = Runtime.getRuntime();        
        HashMap<String,Object> m = new HashMap<String, Object>();
        m.put(OperatingSystemDiscovery.AVAILABLE_STORAGE_SPACE, diskTotalSpace());
        //m.put(OperatingSystemDiscovery.BATTERY_CAPACITY, args); // somente em android
        m.put(OperatingSystemDiscovery.CPU_CORE_COUNT, runtime.availableProcessors());
        //m.put(OperatingSystemDiscovery.CPU_CORE_FREQUENCY, args); não existe como
        m.put(OperatingSystemDiscovery.CPU_MODEL, System.getProperty("os.arch"));
        //m.put(OperatingSystemDiscovery.DEVICE_MODEL, args); // Somente em android
        //m.put(OperatingSystemDiscovery.VIRTUAL_MACHINE_NAME, System.getProperty("java.vm.name"));
        m.put(OperatingSystemDiscovery.NATIVE_OPERATION_SYSTEM, System.getProperty("os.name"));
        m.put(OperatingSystemDiscovery.RAM_AVAILABLE, runtime.maxMemory());
        return m;
    }
    
    public long diskTotalSpace() {
        /* Get a list of all filesystem roots on this system */
        File[] roots = File.listRoots();
        long total = 0;
        /* For each filesystem root, print some info */
        for (File root : roots) {
            total += root.getTotalSpace();
        }
        return total;
    }
}
