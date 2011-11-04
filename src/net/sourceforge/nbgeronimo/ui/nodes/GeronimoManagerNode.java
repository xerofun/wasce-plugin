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

package net.sourceforge.nbgeronimo.ui.nodes;

import java.awt.Image;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import javax.enterprise.deploy.spi.DeploymentManager;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import net.sourceforge.nbgeronimo.GeronimoDeploymentFactory;
import net.sourceforge.nbgeronimo.GeronimoDeploymentManager;
import net.sourceforge.nbgeronimo.ui.nodes.editors.GeronimoPasswordEditor;
import org.openide.util.Lookup;

/**
 *
 * @author John Platts
 */
public class GeronimoManagerNode extends AbstractNode implements Node.Cookie {
    private GeronimoDeploymentManager deploymentManager ;
    
    private static final String DISPLAY_NAME  = "displayName" ;
    private static final String URL = "url" ;
    private static final String USERNAME = "username" ;
    private static final String PASSWORD = "password" ;
    private static final String SERVER_ROOT = "serverRoot" ;
    
    private static final String GERONIMO_ICON = "org/tempuri/nbgeronimo/" +
            "resources/littleG.gif";
    
    public GeronimoManagerNode(Children children, Lookup lookup) {
        super(children) ;
        
        this.deploymentManager = (GeronimoDeploymentManager) lookup.lookup(
                DeploymentManager.class) ;
        
        //getCookieSet().add(this) ;
    }
    
    @Override
    public Image getIcon(int type) {
        return Utilities.loadImage(GERONIMO_ICON) ;
    }
    
    @Override
    public Image getOpenedIcon(int type) {
        return Utilities.loadImage(GERONIMO_ICON) ;
    }
    
    @Override
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet() ;
        
        Sheet.Set properties = sheet.get(Sheet.PROPERTIES) ;
        if(properties == null) {
            properties = Sheet.createPropertiesSet() ;
            sheet.put(properties) ;
        }
        
        Node.Property property ;
        
        property = new PropertySupport.ReadWrite(
                DISPLAY_NAME,
                String.class,
                NbBundle.getMessage(GeronimoManagerNode.class,
                "PROP_displayName"),
                NbBundle.getMessage(GeronimoManagerNode.class,
                "HINT_displayName")) {

            @Override
            public Object getValue() throws IllegalAccessException, InvocationTargetException {
                return deploymentManager.getInstanceProperties().
                        getProperty(
                        InstanceProperties.DISPLAY_NAME_ATTR) ;
            }

            @Override
            public void setValue(Object value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                deploymentManager.getInstanceProperties().
                        setProperty(
                        InstanceProperties.DISPLAY_NAME_ATTR,
                        (String) value) ;
            }
        };
        properties.put(property) ;
        
        property = new PropertySupport.ReadOnly(
                URL,
                String.class,
                NbBundle.getMessage(GeronimoManagerNode.class,
                "PROP_url"),
                NbBundle.getMessage(GeronimoManagerNode.class,
                "HINT_url")) {

            @Override
            public Object getValue() throws IllegalAccessException, InvocationTargetException {
                return deploymentManager.getURI() ;
            }
        };
        properties.put(property) ;
        
        property = new PropertySupport.ReadWrite(
                USERNAME,
                String.class,
                NbBundle.getMessage(GeronimoManagerNode.class,
                "PROP_username"),
                NbBundle.getMessage(GeronimoManagerNode.class,
                "HINT_username")) {

            @Override
            public Object getValue() throws IllegalAccessException, InvocationTargetException {
                return deploymentManager.getInstanceProperties().
                        getProperty(
                        InstanceProperties.USERNAME_ATTR) ;
            }
            
            @Override
            public void setValue(Object value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                deploymentManager.getInstanceProperties().
                        setProperty(
                        InstanceProperties.USERNAME_ATTR,
                        (String) value) ;
            }
        };
        properties.put(property) ;
        
        property = new PropertySupport.ReadWrite(
                PASSWORD,
                String.class,
                NbBundle.getMessage(GeronimoManagerNode.class,
                "PROP_password"),
                NbBundle.getMessage(GeronimoManagerNode.class,
                "HINT_password")) {

            @Override
            public Object getValue() throws IllegalAccessException, InvocationTargetException {
                return deploymentManager.getInstanceProperties().
                        getProperty(
                        InstanceProperties.PASSWORD_ATTR) ;
            }
            
            @Override
            public void setValue(Object value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                deploymentManager.getInstanceProperties().
                        setProperty(
                        InstanceProperties.PASSWORD_ATTR,
                        (String) value) ;
            }
            
            @Override
            public PropertyEditor getPropertyEditor() {
                return new GeronimoPasswordEditor() ;
            }
        };
        properties.put(property) ;
        
        property = new PropertySupport.ReadWrite(
                PASSWORD,
                String.class,
                NbBundle.getMessage(GeronimoManagerNode.class,
                "PROP_serverRoot"),
                NbBundle.getMessage(GeronimoManagerNode.class,
                "HINT_serverRoot")) {

            @Override
            public Object getValue() throws IllegalAccessException, InvocationTargetException {
                return deploymentManager.getInstanceProperties().
                        getProperty(
                        GeronimoDeploymentFactory.SERVER_ROOT_ATTR) ;
            }
            
            @Override
            public void setValue(Object value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                deploymentManager.getInstanceProperties().
                        setProperty(
                        GeronimoDeploymentFactory.SERVER_ROOT_ATTR,
                        (String) value) ;
            }
            
            @Override
            public PropertyEditor getPropertyEditor() {
                return new GeronimoPasswordEditor() ;
            }
        };
        properties.put(property) ;
        
        return sheet ;
    }
}
