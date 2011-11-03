/*
 * WasCEStartServer.java
 *
 * Created on June 11, 2005, 2:03 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.netbeans.modules.j2ee.wasce;

import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.status.ProgressObject;
import org.netbeans.modules.j2ee.deployment.plugins.api.ServerDebugInfo;
import org.netbeans.modules.j2ee.deployment.plugins.spi.StartServer;

/**
 *
 * @author Administrator
 */
public class WasCEStartServer extends StartServer {
    
    public ProgressObject startDebugging(Target target) {
        return null;
    }
    
    public boolean isDebuggable(Target target) {
        return false;
    }
    
    public boolean isAlsoTargetServer(Target target) {
        return true;
    }
    
    public ServerDebugInfo getDebugInfo(Target target) {
        return null;
    }
    
    public boolean supportsStartDeploymentManager() {
        return false;
    }
    
    public ProgressObject stopDeploymentManager() {
        return null;
    }
    
    public ProgressObject startDeploymentManager() {
        return null;
    }
    
    public boolean needsStartForTargetList() {
        return false;
    }
    
    public boolean needsStartForConfigure() {
        return false;
    }
    
    public boolean needsStartForAdminConfig() {
        return false;
    }
    
    public boolean isRunning() {
        return false;
    }
}
