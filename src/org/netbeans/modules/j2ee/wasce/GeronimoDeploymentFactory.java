/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2007 John Platts. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is nbgeronimo. The Initial Developer of the Original
 * Software is John Platts. Portions Copyright 2007 John Platts.
 * All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.j2ee.wasce;

import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException;
import javax.enterprise.deploy.spi.factories.DeploymentFactory;
import org.openide.util.NbBundle;

/**
 *
 * @author John Platts
 */
public class GeronimoDeploymentFactory implements DeploymentFactory
{
    public static final String SERVER_ROOT_ATTR = "serverRoot";
    public static final String HOST_ATTR = "host";
    public static final String PORT_ATTR = "port";
    public static final String USERNAME_ATTR = "username";
    public static final String PASSWORD_ATTR = "password";
    public static final String SERVER_NAME_ATTR = "serverName";

    public static final String GERONIMO_URI_START =
            "deployer:geronimo:jmx:rmi:///jndi/rmi:" ;
    public static final int GERONIMO_URI_START_LENGTH =
            GERONIMO_URI_START.length() ;
    public static final String GERONIMO_URI_END =
            "/JMXConnector" ;
    public static final int GERONIMO_URI_END_LENGTH =
            GERONIMO_URI_END.length() ;
    
    private GeronimoDeploymentFactory() {}
    
    private static GeronimoDeploymentFactory instance = null ;
    
    public static synchronized GeronimoDeploymentFactory create()
    {
        if(instance == null)
        {
            instance = new GeronimoDeploymentFactory() ;
        }
        
        return instance ;
    }
    
    public boolean handlesURI(String uri) {
        return GeronimoUtils.isValidGeronimoURI(uri) ;
    }

    public DeploymentManager getDeploymentManager(String uri, String username, String password) throws DeploymentManagerCreationException {
        return new GeronimoDeploymentManager(uri, username, password) ;
    }

    public DeploymentManager getDisconnectedDeploymentManager(String uri) throws DeploymentManagerCreationException {
        return new GeronimoDeploymentManager(uri) ;
    }

    public String getDisplayName() {
        return NbBundle.getMessage(GeronimoDeploymentFactory.class,
                "TXT_DisplayName") ;
    }

    public String getProductVersion() {
        return NbBundle.getMessage(GeronimoDeploymentFactory.class,
                "TXT_productVersion") ;
    }

}
