/*
 * WasCEOptionalFactory.java
 *
 * Created on June 11, 2005, 2:10 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.netbeans.modules.j2ee.wasce;

import javax.enterprise.deploy.spi.DeploymentManager;
import org.netbeans.modules.j2ee.deployment.plugins.spi.FindJSPServlet;
import org.netbeans.modules.j2ee.deployment.plugins.spi.IncrementalDeployment;
import org.netbeans.modules.j2ee.deployment.plugins.spi.OptionalDeploymentManagerFactory;
import org.netbeans.modules.j2ee.deployment.plugins.spi.StartServer;
import org.openide.WizardDescriptor.InstantiatingIterator;

/**
 *
 * @author Administrator
 */
public class WasCEOptionalFactory extends OptionalDeploymentManagerFactory {
    
    public StartServer getStartServer(DeploymentManager dm) {
        return new WasCEStartServer();
    }
    
    public IncrementalDeployment getIncrementalDeployment(DeploymentManager dm) {
        return null;
    }
    
    public FindJSPServlet getFindJSPServlet(DeploymentManager dm) {
        return null;
    }
    
    public InstantiatingIterator getAddInstanceIterator() {
        
        return new WasCEInstantiatingIterator();
    }
    
}
