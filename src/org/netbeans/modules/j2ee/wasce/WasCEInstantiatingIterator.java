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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import net.sourceforge.nbgeronimo.GeronimoURIManager;
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
           String displayName = getDisplayName();
           
//           String url         =  "deployer:geronimo:jmx:rmi:///jndi/rmi://" +current().getComponent().getHost() + ":" + current().getComponent().getPort() + "/JMXConnector" ;// NOI18N
//           String username    = current().getComponent().getUser(); // NOI18N
//           String password    = current().getComponent().getPwd(); // NOI18N
//           String httpportnumber=current().getComponent().getPort();
           
           //try {
              System.out.println("Instance property");
               InstanceProperties ip =GeronimoURIManager.createInstanceProperties(current().getComponent().getHost(), current().getComponent().getPort(), current().getComponent().getUser(), current().getComponent().getPwd(), displayName);
               System.out.println("Sem erro");
               result.add(ip);
//           } catch (Exception ex) {
//               System.out.println("teste "+ex.getMessage());
//               DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
//                        NbBundle.getMessage(WasCEInstantiatingIterator.class, "MSG_CreateFailed", displayName),
//                        NotifyDescriptor.ERROR_MESSAGE));
//           }
           return result;
//         Set result = new HashSet();       
//           String displayName = getDisplayName();
//           String url         = "deployer:myserver:localhost:808"; // NOI18N
//           String username    = "username"; // NOI18N
//           String password    = "password"; // NOI18N
//          try {
//               InstanceProperties ip = InstanceProperties.createInstanceProperties(
//                       url, username, password, displayName);
//            boolean add = result.add(ip);
//           } catch (Exception ex) {
//               DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
//                        NbBundle.getMessage(WasCEInstantiatingIterator.class, "MSG_CreateFailed", displayName),
//                        NotifyDescriptor.ERROR_MESSAGE));
//           }
//           return result;
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