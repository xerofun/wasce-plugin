/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.netbeans.modules.j2ee.wasce;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.deploy.spi.factories.DeploymentFactory;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.swing.SwingUtilities;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceCreationException;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 * @version $Rev: 685907 $ $Date: 2008-08-14 11:13:23 -0300 (qui, 14 ago 2008) $
 */
public class GeronimoPluginUtils {

    private static final Logger LOGGER = Logger.getLogger(GeronimoPluginUtils.class.getName());
    private static final String DEPLOYER_JAR_PATH = "/bin/deployer.jar";
    private static final String SERVER_JAR_PATH = "/bin/server.jar";
    private static final String DEPLOY_JSR88_PARTIAL_JAR_PATH = "/repository/org/apache/geronimo/modules/geronimo-deploy-jsr88/";
    private static List<String> serverRequirements = new LinkedList<String>();


    static {
        serverRequirements.add("bin");
        serverRequirements.add("lib");
        serverRequirements.add("repository");
        serverRequirements.add("repository");
        serverRequirements.add("schema");
        serverRequirements.add("var");
        //serverRequirements.add("bin/deploy.jar");
        serverRequirements.add("bin/server.jar");
        serverRequirements.add("bin/deploy.bat");
        
    }
    
     private static Map<String, DeploymentFactory> factories = new HashMap<String, DeploymentFactory>();
    //private static final String DEPLOYMENT_FACTORY_CLASS_NAME = "org.apache.geronimo.deployment.plugin.factories.DeploymentFactoryBootstrapper";
private static final String DEPLOYMENT_FACTORY_CLASS_NAME = "org.apache.geronimo.deployment.plugin.factories.DeploymentFactoryImpl";
    public static DeploymentFactory getFactory(String uri, GeronimoDeploymentManager gdm) {
        DeploymentFactory factory = null;
        try {
            String serverDir = GeronimoPluginUtils.getInstanceProperties(uri).getProperty(GeronimoDeploymentFactory.SERVER_ROOT_ATTR);

            // FIXME: When is it null?
            if (serverDir == null) {
                
                serverDir =  gdm.getServerRoot();
            }

            factory = factories.get(serverDir);
            if (factory == null) {
                try {
                    URLClassLoader loader = GeronimoPluginUtils.getGeronimoClassLoader(serverDir);
                    ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
                    try {
                        Thread.currentThread().setContextClassLoader(loader);
                        factory = (DeploymentFactory) loader.loadClass(DEPLOYMENT_FACTORY_CLASS_NAME).newInstance();
                    } finally {
                        Thread.currentThread().setContextClassLoader(oldCL);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, e.getLocalizedMessage(), e);
                }
                factories.put(serverDir, factory);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, e.getLocalizedMessage(), e);
        }
        assert factory != null : "factory is null";
        return factory;
    }

    public static boolean isValidServerLocation(File candidate) {
        return isValidServerLocation(candidate, serverRequirements);
    }

    private static boolean isValidServerLocation(File candidate, List<String> requirements) {
        if (null == candidate || !candidate.exists() || !candidate.canRead() || !candidate.isDirectory() || !hasRequiredChildren(candidate, requirements)) {
            return false;
        }
        if (null == computeGeronimoVersion(candidate)) {
            return false;
        }
        return true;
    }

    private static boolean hasRequiredChildren(File candidate, final List<String> requiredChildren) {
        if (null == candidate) {
            return false;
        }
        String[] children = candidate.list();
        if (null == children) {
            return false;
        }
        if (null == requiredChildren) {
            return true;
        }
        Iterator<String> iter = requiredChildren.iterator();
        while (iter.hasNext()) {
            String next = iter.next();
            File test = new File(candidate.getPath() + File.separator + next);
            if (!test.exists()) {
                return false;
            }
        }
        return true;
    }

    public static InstanceProperties getInstanceProperties(String uri) {
        return getInstanceProperties(uri, null, null, null);
    }

    public static InstanceProperties getInstanceProperties(String url, String user, String password, String name) {
        InstanceProperties instanceProperties = InstanceProperties.getInstanceProperties(url);
        // FIXME: Might it ever happen that instanceProperties, user, password, name are all null?
        if (instanceProperties == null) {
            try {
                instanceProperties = InstanceProperties.createInstanceProperties(url, user, password, name);
            } catch (InstanceCreationException ice) {
                // it must not happen
                // it's thrown only when instance with same url is already registered, but it's just been checked
                String msg = NbBundle.getMessage(GeronimoPluginUtils.class, "MSG_ExceptionThrownButShouldHaveNot", ice.getClass().getName(), ice.getLocalizedMessage());
                LOGGER.log(Level.WARNING, msg, ice);
            }
        }
        return instanceProperties;
    }

    public static boolean isPortFree(int port) {
        ServerSocket soc = null;
        try {
            soc = new ServerSocket(port);
        } catch (IOException ioe) {
            return false;
        } finally {
            if (soc != null) {
                try {
                    soc.close();
                } catch (IOException ignored) {
                }
            }
        }
        return true;
    }

    public static void showInformation(final String msg, final String title) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                NotifyDescriptor d = new NotifyDescriptor.Message(msg, NotifyDescriptor.INFORMATION_MESSAGE);
                d.setTitle(title);
                DialogDisplayer.getDefault().notify(d);
            }
        });
    }

    public static MBeanServerConnection getRMIServer(String uri) throws IOException {
        MBeanServerConnection rmiServer = null;
        InstanceProperties ip = InstanceProperties.getInstanceProperties(uri);
        ClassLoader origClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getGeronimoClassLoader(ip.getProperty(GeronimoDeploymentFactory.SERVER_ROOT_ATTR)));
        try {

            Map<String, Object> env = new HashMap<String, Object>();
            String username = ip.getProperty(InstanceProperties.USERNAME_ATTR);
            String password = ip.getProperty(InstanceProperties.PASSWORD_ATTR);
            String[] credentials = new String[]{username, password};
            env.put(JMXConnector.CREDENTIALS, credentials);

            // FIXME: Create the JMXServiceURL based on information provided by user - hostname and port
            JMXServiceURL address = new JMXServiceURL("service:jmx:rmi://localhost/jndi/rmi://localhost:1099/JMXConnector");
            JMXConnector jmxConnector = JMXConnectorFactory.connect(address, env);
            rmiServer = jmxConnector.getMBeanServerConnection();
        } finally {
            Thread.currentThread().setContextClassLoader(origClassLoader);
        }
        return rmiServer;
    }

    // TODO: Move it to GeronimoClassLoader's constructor
    public static URLClassLoader getGeronimoClassLoader(String serverRoot) {
        if (serverRoot == null) {
            throw new NullPointerException("serverRoot must not be null");
        }
        URLClassLoader loader = null;
        try {
            // jacek: Don't really know what it does, but it has not caused any harm so far
            System.setProperty("Xorg.apache.geronimo.gbean.NoProxy", "false");

            List<URL> urlList = new ArrayList<URL>();
//            urlList.add(new File(serverRoot + "/repository/org/apache/geronimo/framework/geronimo-common/2.1.2/geronimo-common-2.1.2.jar").toURI().toURL());
//            urlList.add(new File(serverRoot + "/repository/org/apache/geronimo/framework/geronimo-crypto/2.1.2/geronimo-crypto-2.1.2.jar").toURI().toURL());
//            urlList.add(new File(serverRoot + "/repository/org/apache/geronimo/framework/geronimo-deploy-config/2.1.2/geronimo-deploy-config-2.1.2.jar").toURI().toURL());
//            urlList.add(new File(serverRoot + "/repository/org/apache/geronimo/framework/geronimo-deploy-jsr88/2.1.2/geronimo-deploy-jsr88-2.1.2.jar").toURI().toURL());
//            urlList.add(new File(serverRoot + "/repository/org/apache/geronimo/framework/geronimo-deployment/2.1.2/geronimo-deployment-2.1.2.jar").toURI().toURL());
//            urlList.add(new File(serverRoot + "/repository/org/apache/geronimo/modules/geronimo-j2ee-schema/2.1.2/geronimo-j2ee-schema-2.1.2.jar").toURI().toURL());
//            urlList.add(new File(serverRoot + "/repository/org/apache/geronimo/specs/geronimo-javaee-deployment_1.1MR3_spec/1.0/geronimo-javaee-deployment_1.1MR3_spec-1.0.jar").toURI().toURL());
//            urlList.add(new File(serverRoot + "/lib/geronimo-kernel-2.1.2.jar").toURI().toURL());
//            urlList.add(new File(serverRoot + "/repository/org/apache/geronimo/framework/geronimo-plugin/2.1.2/geronimo-plugin-2.1.2.jar").toURI().toURL());
//            urlList.add(new File(serverRoot + "/repository/org/apache/geronimo/framework/geronimo-system/2.1.2/geronimo-system-2.1.2.jar").toURI().toURL());
//            urlList.add(new File(serverRoot + "/lib/plexus-archiver-1.0-alpha-7.jar").toURI().toURL());
            
            urlList.add(new File(serverRoot + "/jsr88/geronimo-deploy-jsr88-full.jar").toURI().toURL());
            
            loader = new GeronimoClassLoader(urlList.toArray(new URL[urlList.size()]), GeronimoDeploymentFactory.class.getClassLoader());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, e.getLocalizedMessage(), e);
        }
        return loader;
    }

    /**
     * Borrowed from Geronimo Eclipse Plugin (GEP)
     *
     * @param serverRoot root directory of Geronimo instance
     *
     * @return stringified version or empty string when no approach to compute succeeded
     */
    private static String computeGeronimoVersion(File serverRoot) {
        return computeGeronimoVersion(serverRoot.getAbsolutePath());
    }

    static String computeGeronimoVersion(String serverRoot) {
        URL systemjarURL = null;

        File libDir = new File(serverRoot + "/lib");
        if (libDir.exists()) {
            File[] libs = libDir.listFiles();
            for (int i = 0; i < libs.length; i++) {
                if (libs[i].getName().startsWith("geronimo-system")) {
                    try {
                        systemjarURL = libs[i].toURI().toURL();
                        break;
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (systemjarURL == null) {
            File systemDir = new File(serverRoot + "/repository/org/apache/geronimo/modules/geronimo-system");
            if (systemDir.exists() && systemDir.isDirectory() && systemDir.canRead()) {
                List<File> files = scanDirectory(systemDir);
                for (File jarFile : files) {
                    if (jarFile.getName().startsWith("geronimo-system") && jarFile.getName().endsWith("jar")) {
                        try {
                            systemjarURL = jarFile.toURI().toURL();
                            break;
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        if (systemjarURL == null) {
            File systemDir = new File(serverRoot + "/repository/org/apache/geronimo/framework/geronimo-system");
            if (systemDir.exists() && systemDir.isDirectory() && systemDir.canRead()) {
                List<File> files = scanDirectory(systemDir);
                for (File jarFile : files) {
                    if (jarFile.getName().startsWith("geronimo-system") && jarFile.getName().endsWith("jar")) {
                        try {
                            systemjarURL = jarFile.toURI().toURL();
                            break;
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        if (systemjarURL != null) {
            URLClassLoader cl = new URLClassLoader(new URL[]{systemjarURL});
            try {
                Class clazz = cl.loadClass("org.apache.geronimo.system.serverinfo.ServerConstants");
                Method method = clazz.getMethod("getVersion", new Class[]{});
                return (String) method.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private static List<File> scanDirectory(File dir) {
        List<File> files = new ArrayList<File>();
        scanDirectory(dir, files);
        return files;
    }

    private static void scanDirectory(File dir, List<File> files) {
        File[] tmpFiles = dir.listFiles();
        for (int i = 0; i < tmpFiles.length; i++) {
            if (tmpFiles[i].isDirectory()) {
                scanDirectory(tmpFiles[i], files);
            } else {
                files.add(tmpFiles[i]);
            }
        }
    }

    public static class GeronimoClassLoader extends URLClassLoader {

        public GeronimoClassLoader(URL[] urls) throws MalformedURLException, RuntimeException {
            super(urls);
        }

        public GeronimoClassLoader(URL[] urls, ClassLoader parent) throws MalformedURLException, RuntimeException {
            super(urls, parent);
        }

        @Override
        protected PermissionCollection getPermissions(CodeSource cs) {
            Permissions p = new Permissions();
            p.add(new AllPermission());
            return p;
        }

        @Override
        public URL getResource(String name) {
            // FIXME: A hack to shut down the PCL logger while log4j conf files are loaded from the default (unnamed) package
            final String pclName = "org.netbeans.ProxyClassLoader";
            Level oldLevel = Logger.getLogger(pclName).getLevel();
            try {
                if ("log4j.xml".equals(name) || "log4j.properties".equals(name)) {
                    Logger.getLogger(pclName).setLevel(java.util.logging.Level.OFF);
                }
                return super.getResource(name);
            } finally {
                Logger.getLogger(pclName).setLevel(oldLevel);
            }
        }
    }
   
}
