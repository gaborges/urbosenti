/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urbosenti.adaptation;

import java.util.ArrayList;

/**
 *
 * @author Guilherme
 */
public class Diagnosis {
    public static final int DIAGNOSIS_NO_ADAPTATION_NEEDED = 0;
    private final ArrayList<Integer> changes;

    public Diagnosis() {
        this.changes = new ArrayList<Integer>();
    }
    
    public void addChange(Integer changeId){
        this.changes.add(changeId);
    }
    
    public ArrayList<Integer> getChanges(){
        return this.changes;
    }
}
