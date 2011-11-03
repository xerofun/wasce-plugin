/*
 * WarDeploymentConfiguration.java
 *
 * Created on Oct 15, 2007, 9:23:13 AM
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.j2ee.wasce.config;

import java.io.OutputStream;
import org.netbeans.modules.j2ee.deployment.common.api.ConfigurationException;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.ContextRootConfiguration;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.DeploymentPlanConfiguration;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.ModuleConfiguration;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Administrator
 */
public class WarDeploymentConfiguration implements ModuleConfiguration, ContextRootConfiguration, DeploymentPlanConfiguration {

    private final J2eeModule module;

    public WarDeploymentConfiguration(J2eeModule module) {
        this.module = module;
        // TODO server specific deployment descriptor should be created (if neccessary) and loaded
    }

    public J2eeModule getJ2eeModule() {
        return module;
    }

    public Lookup getLookup() {
        return Lookups.fixed(new Object[] {this});
    }

    public void dispose() {

    }

    public String getContextRoot() throws ConfigurationException {
        // TODO implement reading of the context root
        return "/wasce";
        //return "/mypath"; // NOI18N
    }

    public void setContextRoot(String arg0) throws ConfigurationException {
        // TODO implement storing of the context root
    }

    public void save(OutputStream os) throws ConfigurationException {
        // TODO implement storing of the deployment plan
    }

}
