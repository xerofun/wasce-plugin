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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.deploy.model.DeployableObject;
import javax.enterprise.deploy.shared.DConfigBeanVersionType;
import javax.enterprise.deploy.shared.ModuleType;
import javax.enterprise.deploy.spi.DeploymentConfiguration;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.exceptions.DConfigBeanVersionUnsupportedException;
import javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException;
import javax.enterprise.deploy.spi.exceptions.InvalidModuleException;
import javax.enterprise.deploy.spi.exceptions.TargetException;
import javax.enterprise.deploy.spi.factories.DeploymentFactory;
import javax.enterprise.deploy.spi.status.ProgressObject;
import net.sourceforge.nbgeronimo.config.GeronimoDeploymentConfiguration;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.openide.util.NbBundle;

/**
 *
 * @author John Platts
 */
public class GeronimoDeploymentManager implements DeploymentManager {

    private GeronimoClassLoader loader;
    private DeploymentFactory factory;
    private DeploymentManager dm;
    private InstanceProperties instanceProperties;
    private String uri;
    private String username;
    private String password;
    private boolean isConnected = false;

    public GeronimoDeploymentManager(String uri, String username, String password) {
        this.uri = uri;
        this.username = username;
        this.password = password;
    }

    public GeronimoDeploymentManager(String uri) {
        this(uri, null, null);
    }

    private void parseUri() {
        if (GeronimoUtils.isValidGeronimoURI(uri)) {
            int uriLength = uri.length();

            String host;
            String port;
            if (uriLength > (GeronimoDeploymentFactory.GERONIMO_URI_START_LENGTH +
                    GeronimoDeploymentFactory.GERONIMO_URI_END_LENGTH)) {
                int hostPortSubstringLength =
                        uriLength -
                        (GeronimoDeploymentFactory.GERONIMO_URI_START_LENGTH +
                        GeronimoDeploymentFactory.GERONIMO_URI_END_LENGTH);
                String hostPortSubstring =
                        uri.substring(GeronimoDeploymentFactory.GERONIMO_URI_START_LENGTH,
                        GeronimoDeploymentFactory.GERONIMO_URI_START_LENGTH +
                        hostPortSubstringLength);

                assert (hostPortSubstring.length() == hostPortSubstringLength) :
                        "hostPortSubstring.length() is not equal to hostPortSubstringLength";

                int indexOfColon = hostPortSubstring.indexOf(':');

                if (indexOfColon != (-1)) {
                    host = hostPortSubstring.substring(2, indexOfColon);
                    port = hostPortSubstring.substring(indexOfColon + 1);
                } else {
                    host = hostPortSubstring.substring(2);
                    port = "1099";
                }
            } else {
                host = "localhost";
                port = "1099";
            }

            getInstanceProperties().setProperty(
                    GeronimoDeploymentFactory.HOST_ATTR, host);
            getInstanceProperties().setProperty(
                    GeronimoDeploymentFactory.PORT_ATTR, port);
        }
    }

    public InstanceProperties getInstanceProperties() {
        if (instanceProperties == null) {
            instanceProperties =
                    InstanceProperties.getInstanceProperties(uri);

            if (instanceProperties != null) {
                parseUri();
            }
        }

        return instanceProperties;
    }

    public String getURI() {
        return uri;
    }

    public String getHost() {
         return getInstanceProperties().getProperty(
                GeronimoDeploymentFactory.HOST_ATTR);
    }

    public String getPassword() {
        return getInstanceProperties().getProperty(
                GeronimoDeploymentFactory.PASSWORD_ATTR);
    }

    public String getUsername() {
        return getInstanceProperties().getProperty(
                GeronimoDeploymentFactory.USERNAME_ATTR);
    }

    public String getPort() {
        return getInstanceProperties().getProperty(
                GeronimoDeploymentFactory.PORT_ATTR);
    }

    public String getServerRoot() {
        return getInstanceProperties().getProperty(
                GeronimoDeploymentFactory.SERVER_ROOT_ATTR);
    }

    public String getServerName() {
        return getInstanceProperties().getProperty(
                GeronimoDeploymentFactory.SERVER_NAME_ATTR);
    }

    public void setServerRoot(String serverRoot) {
        if (getInstanceProperties() != null) {
            getInstanceProperties().setProperty(
                    GeronimoDeploymentFactory.SERVER_ROOT_ATTR,
                    serverRoot);
        }
    }

    public void setHost(String host) {
        if (getInstanceProperties() != null) {
            getInstanceProperties().setProperty(
                    GeronimoDeploymentFactory.HOST_ATTR,
                    host);
        }
    }

    public void setPort(String port) {
        if (getInstanceProperties() != null) {
            getInstanceProperties().setProperty(
                    GeronimoDeploymentFactory.PORT_ATTR,
                    port);
        }
    }

    public void setPassword(String password) {
        if (getInstanceProperties() != null) {
            getInstanceProperties().setProperty(
                    GeronimoDeploymentFactory.PASSWORD_ATTR,
                    password);
        }
    }

    public void setUsername(String username) {
        if (getInstanceProperties() != null) {
            getInstanceProperties().setProperty(
                    GeronimoDeploymentFactory.USERNAME_ATTR,
                    username);
        }
    }

    public void setServerName(String serverName) {
        if (getInstanceProperties() != null) {
            getInstanceProperties().setProperty(
                    GeronimoDeploymentFactory.SERVER_NAME_ATTR,
                    serverName);
        }
    }

    private void loadDeploymentFactory() {
        if (factory == null) {
            String serverRoot = getServerRoot();
            try {
                factory = GeronimoUtils.getGeronimoDeploymentFactory(serverRoot);
            } catch (IOException e) {
                Logger.getLogger("global").log(Level.SEVERE, null, e);
            } catch (ClassNotFoundException e) {
            } catch (InstantiationException e) {
                Logger.getLogger("global").log(Level.SEVERE, null, e);
            } catch (IllegalAccessException e) {
                Logger.getLogger("global").log(Level.SEVERE, null, e);
            }
        }
    }

    private void updateDeploymentManager() {
        loadDeploymentFactory();

        loader.updateLoader();

        try {
            if (dm != null) {
                dm.release();
                dm = null;
            }

            if (factory != null) {
                dm = factory.getDeploymentManager(uri, username, password);

                isConnected = true;
            }
        } catch (DeploymentManagerCreationException e) {
            try {
                isConnected = false;

                dm = factory.getDisconnectedDeploymentManager(uri);
            } catch (DeploymentManagerCreationException ex) {
                Logger.getLogger("global").log(Level.SEVERE, null, ex);
            }
        } finally {
            loader.restoreLoader();
        }
    }

    public boolean getIsConnected() {
        return isConnected;
    }

    public String getServerTitleMessage() {
        return ("[" +
                getServerName() + ", " +
                getHost() + ":" + getPort() +
                "]");
    }

    public Target[] getTargets() throws IllegalStateException {
        updateDeploymentManager();

        if (!isConnected) {
            throw new IllegalStateException(NbBundle.getMessage(
                    GeronimoDeploymentManager.class, "ERR_illegalState"));
        }

        loader.updateLoader();

        try {
            return dm.getTargets();
        } finally {
            loader.restoreLoader();
        }
    }

    public TargetModuleID[] getRunningModules(ModuleType moduleType, Target[] target) throws TargetException, IllegalStateException {
        updateDeploymentManager();

        if (!isConnected) {
            throw new IllegalStateException(NbBundle.getMessage(
                    GeronimoDeploymentManager.class, "ERR_illegalState"));
        }
        
        loader.updateLoader();
        try
        {
            return dm.getRunningModules(moduleType, target);
        }
        finally
        {
            loader.restoreLoader();
        }
    }

    public TargetModuleID[] getNonRunningModules(ModuleType moduleType, Target[] target) throws TargetException, IllegalStateException {
        updateDeploymentManager();

        if (!isConnected) {
            throw new IllegalStateException(NbBundle.getMessage(
                    GeronimoDeploymentManager.class, "ERR_illegalState"));
        }
        
        loader.updateLoader();
        try
        {
            return dm.getNonRunningModules(moduleType, target);
        }
        finally
        {
            loader.restoreLoader();
        }
    }

    public TargetModuleID[] getAvailableModules(ModuleType moduleType, Target[] target) throws TargetException, IllegalStateException {
        updateDeploymentManager();

        if (!isConnected) {
            throw new IllegalStateException(NbBundle.getMessage(
                    GeronimoDeploymentManager.class, "ERR_illegalState"));
        }
        
        loader.updateLoader();
        try
        {
            return dm.getAvailableModules(moduleType, target);
        }
        finally
        {
            loader.restoreLoader();
        }
    }

    public DeploymentConfiguration createConfiguration(DeployableObject deployableObject) throws InvalidModuleException {
        updateDeploymentManager() ;
        
        DeploymentConfiguration deploymentConfig ;
        loader.updateLoader();
        try
        {
            deploymentConfig =
                    dm.createConfiguration(deployableObject);
        }
        finally
        {
            loader.restoreLoader();
        }
        
        return new GeronimoDeploymentConfiguration(this, deploymentConfig) ;
    }

    public ProgressObject distribute(Target[] target, File file, File file2) throws IllegalStateException {
        updateDeploymentManager();

        if (!isConnected) {
            throw new IllegalStateException(NbBundle.getMessage(
                    GeronimoDeploymentManager.class, "ERR_illegalState"));
        }

        loader.updateLoader();
        try {
            return dm.distribute(target, file, file2);
        } finally {
            loader.restoreLoader();
        }
    }

    public ProgressObject distribute(Target[] target, InputStream stream, InputStream stream2) throws IllegalStateException {
        updateDeploymentManager();

        if (!isConnected) {
            throw new IllegalStateException(NbBundle.getMessage(
                    GeronimoDeploymentManager.class, "ERR_illegalState"));
        }

        loader.updateLoader();
        try {
            return dm.distribute(target, stream, stream2);
        } finally {
            loader.restoreLoader();
        }
    }

    public ProgressObject distribute(Target[] target, ModuleType moduleType, InputStream stream, InputStream stream2) throws IllegalStateException {
        updateDeploymentManager();

        if (!isConnected) {
            throw new IllegalStateException(NbBundle.getMessage(
                    GeronimoDeploymentManager.class, "ERR_illegalState"));
        }

        loader.updateLoader();
        try {
            return dm.distribute(target, moduleType, stream, stream2);
        } finally {
            loader.restoreLoader();
        }
    }

    public ProgressObject start(TargetModuleID[] targetModuleId) throws IllegalStateException {
        updateDeploymentManager();

        if (!isConnected) {
            throw new IllegalStateException(NbBundle.getMessage(
                    GeronimoDeploymentManager.class, "ERR_illegalState"));
        }
        
        loader.updateLoader();
        try
        {
            return dm.start(targetModuleId);
        }
        finally
        {
            loader.restoreLoader();
        }
    }

    public ProgressObject stop(TargetModuleID[] targetModuleId) throws IllegalStateException {
        updateDeploymentManager();

        if (!isConnected) {
            throw new IllegalStateException(NbBundle.getMessage(
                    GeronimoDeploymentManager.class, "ERR_illegalState"));
        }
        
        loader.updateLoader();
        try
        {
            return dm.stop(targetModuleId);
        }
        finally
        {
            loader.restoreLoader();
        }
    }

    public ProgressObject undeploy(TargetModuleID[] targetModuleId) throws IllegalStateException {
        updateDeploymentManager();

        if (!isConnected) {
            throw new IllegalStateException(NbBundle.getMessage(
                    GeronimoDeploymentManager.class, "ERR_illegalState"));
        }
        
        loader.updateLoader();
        try
        {
            return dm.undeploy(targetModuleId);
        }
        finally
        {
            loader.restoreLoader();
        }
    }

    public boolean isRedeploySupported() {
        updateDeploymentManager();
        
        return dm.isRedeploySupported();
    }

    public ProgressObject redeploy(TargetModuleID[] targetModuleId, File file, File file2) throws UnsupportedOperationException, IllegalStateException {
        updateDeploymentManager();

        if (!isConnected) {
            throw new IllegalStateException(NbBundle.getMessage(
                    GeronimoDeploymentManager.class, "ERR_illegalState"));
        }

        loader.updateLoader();
        try {
            return dm.redeploy(targetModuleId, file, file2);
        } finally {
            loader.restoreLoader();
        }
    }

    public ProgressObject redeploy(TargetModuleID[] targetModuleId, InputStream inputStream, InputStream inputStream2)
            throws UnsupportedOperationException, IllegalStateException {
        updateDeploymentManager();

        if (!isConnected) {
            throw new IllegalStateException(NbBundle.getMessage(
                    GeronimoDeploymentManager.class, "ERR_illegalState"));
        }

        loader.updateLoader();
        try {
            return dm.redeploy(targetModuleId, inputStream, inputStream2);
        } finally {
            loader.restoreLoader();
        }
    }

    public void release() {
        if (dm != null) {
            dm.release();
            dm = null;
        }
    }

    public Locale getDefaultLocale() {
        updateDeploymentManager();

        return dm.getDefaultLocale();
    }

    public Locale getCurrentLocale() {
        updateDeploymentManager();

        return dm.getCurrentLocale();
    }

    public void setLocale(Locale locale) throws UnsupportedOperationException {
        updateDeploymentManager();

        dm.setLocale(locale);
    }

    public Locale[] getSupportedLocales() {
        updateDeploymentManager();

        return dm.getSupportedLocales();
    }

    public boolean isLocaleSupported(Locale locale) {
        updateDeploymentManager();

        return dm.isLocaleSupported(locale);
    }

    public DConfigBeanVersionType getDConfigBeanVersion() {
        updateDeploymentManager() ;
        
        return dm.getDConfigBeanVersion();
    }

    public boolean isDConfigBeanVersionSupported(DConfigBeanVersionType dConfigBeanVersionType) {
        updateDeploymentManager();

        return dm.isDConfigBeanVersionSupported(dConfigBeanVersionType);
    }

    public void setDConfigBeanVersion(DConfigBeanVersionType dConfigBeanVersionType)
            throws DConfigBeanVersionUnsupportedException {
        updateDeploymentManager();

        dm.setDConfigBeanVersion(dConfigBeanVersionType);
    }
}
