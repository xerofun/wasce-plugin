/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. Portions Copyright 2007 John Platts. All Rights
 * Reserved.
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

/* This file was copied from the NetBeans 6.0 source code and adapted
 * for use in nbgeronimo by John Platts. */

package net.sourceforge.nbgeronimo.j2ee;

import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceCreationException;
import javax.enterprise.deploy.spi.DeploymentManager;
import org.netbeans.modules.j2ee.wasce.GeronimoDeploymentManager;
import org.netbeans.modules.j2ee.wasce.GeronimoURIManager;

/**
 *
 * @author dlm198383
 */
public class DeploymentManagerProperties {

    /**
     * Username property, its value is used by the deployment manager.
     */
    public static final String USERNAME_ATTR = "username"; //NOI18N
    
    /**
     * Password property, its value is used by the deployment manager.
     */
    public static final String PASSWORD_ATTR = "password"; //NOI18N
    
    
    /**
     * Display name property, its value is used by IDE to represent server instance.
     */
    public static final String DISPLAY_NAME_ATTR = "displayName"; //NOI18N
    
    /**
     * Location of the app server instance property, its value is used by IDE to represent server instance.
     */
    public static final String LOCATION_ATTR = "LOCATION"; //NOI1
    
    /**
     * Port property, its value is used by the deployment manager.
     */
    public static final String PORT_ATTR = "port";
    
    
    private InstanceProperties instanceProperties;
    private GeronimoDeploymentManager GDM;
    /** Creates a new instance of DeploymentManagerProperties */
    public DeploymentManagerProperties(DeploymentManager dm) {
        
        GDM = (GeronimoDeploymentManager)dm;
        
        instanceProperties = GeronimoURIManager.getInstanceProperties(GDM.getHost(),GDM.getPort());
        
        if (instanceProperties==null){
            try {
                
                instanceProperties = GeronimoURIManager.createInstanceProperties(
                        GDM.getHost(),
                        GDM.getPort(),
                        GDM.getUsername(),
                        GDM.getPassword(),
                        GDM.getHost()+":"+GDM.getPort());
            } catch (InstanceCreationException e){
                
            }
        }
    }
    
    /**
     * Getter for property location. can be null if the dm is remote
     * @return Value of property location.
     */
    
    
    /**
     * Getter for property password. can be null if the DM is a disconnected DM
     * @return Value of property password.
     */
    public java.lang.String getPassword() {
        if (instanceProperties==null)
            return null;
        return instanceProperties.getProperty(InstanceProperties.PASSWORD_ATTR) ;
    }
    
    /**
     * Setter for property password.
     * @param password New value of property password.
     */
    public void setPassword(java.lang.String password) {
        instanceProperties.setProperty(InstanceProperties.PASSWORD_ATTR, password);
        
    }
    
    
    /**
     * Getter for property UserName. can be null for a disconnected DM.
     * @return Value of property UserName.
     */
    public java.lang.String getUserName() {
        if (instanceProperties==null)
            return null;
        return instanceProperties.getProperty(InstanceProperties.USERNAME_ATTR) ;
    }
    
    /**
     * Setter for property UserName.
     * @param UserName New value of property UserName.
     */
    public void setUserName(java.lang.String UserName) {
        instanceProperties.setProperty(InstanceProperties.USERNAME_ATTR, UserName);
        
    }
    /**
     * Ask the server instance to reset cached deployment manager, J2EE
     * management objects and refresh it UI elements.
     */
    public  void refreshServerInstance(){
        instanceProperties.refreshServerInstance();
    }
    
    public String getPort() {
        if (instanceProperties==null)
            return "1099";
        return instanceProperties.getProperty(PORT_ATTR) ;
    }
    
    public void setPort(String port) {
        if(port.trim().matches("[0-9]+"))
        {
        instanceProperties.setProperty(PORT_ATTR, port);
        GDM.setPort(port);
        }
    }
    
    public String getDisplayName() {
        if (instanceProperties == null) {
            return null;
        }
        return instanceProperties.getProperty(InstanceProperties.DISPLAY_NAME_ATTR);
    }
    
    public String getUrl() {
        if (instanceProperties == null) {
            return null;
        }
        return instanceProperties.getProperty(InstanceProperties.URL_ATTR);
    }
    
    public InstanceProperties getInstanceProperties() {
        return instanceProperties;
    }
    
    public String getServerRoot() {
        return GDM.getServerRoot();
    }
    public void setServerRoot(String serverRoot) {
        GDM.setServerRoot(serverRoot);
    }
    public void setHost(String host) {
        GDM.setHost(host);
    }
    public void setServerName(String name) {
        GDM.setServerName(name);
    }    
    
}
