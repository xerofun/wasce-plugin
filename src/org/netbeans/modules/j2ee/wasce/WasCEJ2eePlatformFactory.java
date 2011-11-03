/*
 * WasCEJ2eePlatformFactory.java
 *
 * Created on June 11, 2005, 2:14 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.netbeans.modules.j2ee.wasce;

import javax.enterprise.deploy.spi.DeploymentManager;
import org.netbeans.modules.j2ee.deployment.plugins.spi.J2eePlatformFactory;
import org.netbeans.modules.j2ee.deployment.plugins.spi.J2eePlatformImpl;

/**
 *
 * @author Administrator
 */
public class WasCEJ2eePlatformFactory extends J2eePlatformFactory {
    public J2eePlatformImpl getJ2eePlatformImpl(DeploymentManager dm) {
        return new WasCEJ2eePlatformImpl();
    }
}