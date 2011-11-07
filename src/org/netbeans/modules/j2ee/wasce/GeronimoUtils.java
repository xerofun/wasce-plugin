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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import javax.enterprise.deploy.spi.factories.DeploymentFactory;
import org.netbeans.modules.j2ee.wasce.GeronimoDeploymentFactory;
import org.openide.util.Utilities;

/**
 *
 * @author John Platts
 */
/**
 * implementation by Daniel Gomes to work with Websphere 2011
 * @since 2011
 */
public final class GeronimoUtils
{
    private GeronimoUtils() {}

    private static final String DEPLOYMENTFACTORY_KEY =
           "J2EE-DeploymentFactory-Implementation-Class" ;
    //org.apache.geronimo.deployment.plugin.factories.DeploymentFactoryBootstrapper
    //org.apache.geronimo.deployment.plugin.factories.DeploymentFactoryImpl
    
    public static GeronimoClassLoader getGeronimoClassLoader(String serverRoot)
    {
        return GeronimoClassLoader.getInstance(serverRoot) ;
    }
    
    /**
     * Changed by Daniel Gomes 2011
     * alter lib jsr88 to get correct path to wasce
     * @param serverRoot
     * @return 
     */
    public static File getJSR88DeployerFile(String serverRoot)
    {
        return new File(serverRoot + "/jsr88/geronimo-deploy-jsr88-full.jar") ;
    }
    
    public static String getDeploymentFactoryImplClassName(String serverRoot)
            throws IOException
    {
        JarFile jarFile = new JarFile(getJSR88DeployerFile(serverRoot)) ;
        
        try
        {
            Manifest manifestFile = jarFile.getManifest() ;
            Attributes attributes = manifestFile.getMainAttributes();
            return attributes.getValue(DEPLOYMENTFACTORY_KEY);
        }
        finally
        {
           // jarFile.close() ;
        }
    }
    
    private static DeploymentFactory createGeronimoDeploymentFactory(String serverRoot) throws IOException, ClassNotFoundException, InstantiationException,IllegalAccessException    {
        GeronimoClassLoader classLoader =  getGeronimoClassLoader(serverRoot) ;
        
        classLoader.updateLoader() ;
        //try{
            String deploymentFactoryClassName = null ;
            deploymentFactoryClassName =  getDeploymentFactoryImplClassName(serverRoot) ;

            if(deploymentFactoryClassName == null)
            {
                return null ;
            }

            Class deploymentFactory ;
            //deploymentFactory = Class.forName(serverRoot, true, classLoader) ;
            deploymentFactory = Class.forName(deploymentFactoryClassName, true, classLoader) ;

            Object deploymentFactoryInstance = deploymentFactory.newInstance() ;

            if((deploymentFactoryInstance != null) &&
               (deploymentFactoryInstance instanceof DeploymentFactory))
            {
                return (DeploymentFactory) deploymentFactoryInstance ;
            }
            else
            {
                return null ;
            }
        //}
//catch(Exception ex ){
//            ex.printStackTrace();
//            ex.getMessage();
//            return null;
//        }
//        finally
//        {
////            classLoader.restoreLoader();
//        }
    }
    private static Map<String, DeploymentFactory>
            serverRootToDeploymentFactoryHashMap =
            Collections.synchronizedMap(new HashMap<String, DeploymentFactory>()) ;
    
    public static DeploymentFactory getGeronimoDeploymentFactory(String serverRoot) throws IOException, ClassNotFoundException,InstantiationException,IllegalAccessException{
        DeploymentFactory deploymentFactory = serverRootToDeploymentFactoryHashMap.get(serverRoot) ;
        
        if(deploymentFactory == null)
        {
            deploymentFactory = createGeronimoDeploymentFactory(serverRoot) ;
            serverRootToDeploymentFactoryHashMap.put(serverRoot, deploymentFactory) ;
        }
        
        return deploymentFactory ;
    }
    public static boolean isValidGeronimoURI(String uri) {
        // Format of Geronimo URI:
        // deployer:geronimo:jmx:rmi:///jndi/rmi:[//host[:port]]/JMXConnector
        System.out.println("Valor URI from isValidGeronimoURI = "+uri);
        if(!uri.startsWith(GeronimoDeploymentFactory.GERONIMO_URI_START))
        {
            // URI does not begin with deployer:geronimo:jmx:rmi:///jndi/rmi:
            
            // This factory will not handle these URIs
            
            return false ;
        }
        if(!uri.endsWith(GeronimoDeploymentFactory.GERONIMO_URI_END))
        {
            // URI does not end with /JMXConnector
            
            // This factory will not handle these URIs
            
            return false ;
        }
        
        int hostPortSubstringLength = uri.length() -
                (GeronimoDeploymentFactory.GERONIMO_URI_START_LENGTH +
                 GeronimoDeploymentFactory.GERONIMO_URI_END_LENGTH) ;
        
        if(hostPortSubstringLength == 0)
        {
            // deployer:geronimo:jmx:rmi:///jndi/rmi:/JMXConnector is supported
            // by this factory
            
            return true ;
        }
        
        assert (hostPortSubstringLength > 0) :
            "hostPortSubstringLength must be greater than zero." ;
        
        String hostPortSubstring =
                uri.substring(GeronimoDeploymentFactory.GERONIMO_URI_START_LENGTH,
                              GeronimoDeploymentFactory.GERONIMO_URI_START_LENGTH +
                              hostPortSubstringLength) ;
        
        assert (hostPortSubstring.length() == hostPortSubstringLength) :
            "hostPortSubstring.length() must be equal to hostPortSubstringLength" ;
        
        // hostPortSubstring must be in the format //host[:port]
        
        if(!hostPortSubstring.startsWith("//"))
        {
            return false ;
        }
        if(hostPortSubstringLength < 3)
        {
            return false ;
        }
        
        int colonIndex = hostPortSubstring.indexOf(':') ;
        if(colonIndex == (-1))
        {
            return true ;
        }
        else
        {
            if(colonIndex == (hostPortSubstringLength - 1))
            {
                // hostPortSubstring ends with a colon
                
                return false ;
            }
            
            for(int i = colonIndex + 1; i < hostPortSubstringLength; )
            {
                int codept = hostPortSubstring.codePointAt(i) ;
                
                boolean isDecimalDigit = ((codept >= '0') && (codept <= '9')) ;
                if(!isDecimalDigit)
                {
                    // hostPortSubstring contains a character other than a decimal digit
                    return false ;
                }
                
                // Increment i by 1 if codept is less than or equal to 0x0000FFFF
                // and increment i by 2 if codept is greater than 0x0000FFFF
                i += ((codept <= 0x0000FFFF) ? 1 : 2) ;
            }
            
            String portSubstring =
                    hostPortSubstring.substring(colonIndex + 1) ;
            
            int port ;
            try
            {
                // The port must be an integer between 1 and 65535
                
                // Use Integer.parseInt to get the port as an integer
                
                port = Integer.parseInt(portSubstring, 10) ;
            }
            catch(NumberFormatException ex)
            {
                // port is not a integer
                return false ;
            }
            
            // Return true if port is between 1 and 65535
            // and return false otherwise
            return ((port >= 1) && (port <= 65535)) ;
        }
    }
    public static File getGeronimoCommand(String serverRoot)
    {
        String commandExt = Utilities.isWindows() ? ".bat" : ".sh" ;
        return new File(serverRoot, "/bin/geronimo" + commandExt) ;
    }
    public static File getGeronimoLog(String serverRoot)
    {
        return new File(serverRoot, "/var/log/geronimo.log") ;
    }
}
