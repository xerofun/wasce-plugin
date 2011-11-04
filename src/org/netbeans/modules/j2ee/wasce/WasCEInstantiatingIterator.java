/*
 * WasCEInstantiatingIterator.java
 *
 * Created on June 11, 2005, 2:17 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.netbeans.modules.j2ee.wasce;

import java.awt.Component;
import java.awt.Label;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceCreationException;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;


/**
 *
 * @author Administrator
 */
public class WasCEInstantiatingIterator implements WizardDescriptor.InstantiatingIterator {
    
    private final static String PROP_DISPLAY_NAME = "ServInstWizard_displayName"; // NOI18N

        
    private WasCEWizardPanel panel;
    private WizardDescriptor wizard;
    
    public void removeChangeListener(ChangeListener l) {
    }

    public void addChangeListener(ChangeListener l) {
    }

    public void uninitialize(WizardDescriptor wizard) {
    }

    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
    }

    public void previousPanel() {
    }

    public void nextPanel() {
    }

    public String name() {
        return NbBundle.getMessage(WasCEInstantiatingIterator.class, "MSG_InstallerName");
    }

    public Set instantiate() throws InstanceCreationException  {
           Set result = new HashSet();
           Map<String, String> s= new HashMap<String, String>();
           s.put("serverRoot", current().getComponent().getPath());
           InstanceProperties ip =GeronimoURIManager.createInstanceProperties(current().getComponent().getHost(), current().getComponent().getPort(), current().getComponent().getUser(), current().getComponent().getPwd(), getDisplayName(),s);
           result.add(ip);
           return result;
         
    }

    public boolean hasPrevious() {
        return false;
    }

    public boolean hasNext() {
        return false;
    }

    public WasCEWizardPanel current() {
        if (panel == null) {
                panel = new WasCEWizardPanel();                
            }
            return panel;

    }
    
    private String getDisplayName() {
        return (String)wizard.getProperty(PROP_DISPLAY_NAME);
    }
    
    

}