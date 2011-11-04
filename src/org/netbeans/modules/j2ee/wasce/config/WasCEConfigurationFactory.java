/*
 * WasCEConfigurationFactory.java
 * 
 * Created on Oct 15, 2007, 9:21:25 AM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.j2ee.wasce.config;

import org.netbeans.modules.j2ee.deployment.common.api.ConfigurationException;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.ModuleConfiguration;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.ModuleConfigurationFactory;

/**
 *
 * @author Administrator
 */
public class WasCEConfigurationFactory implements ModuleConfigurationFactory {
    
    public ModuleConfiguration create(J2eeModule j2eeModule) throws ConfigurationException {
//        if (J2eeModule.WAR == j2eeModule.getModuleType()) {
//            return new WarDeploymentConfiguration(j2eeModule);
//        }
        // TODO implement config for EAR and EJB if supported
        return null;
    }

}
