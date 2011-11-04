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

package net.sourceforge.nbgeronimo.j2ee;

import java.awt.Image;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.j2ee.deployment.common.api.J2eeLibraryTypeProvider;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.plugins.spi.J2eePlatformImpl;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.modules.j2ee.wasce.GeronimoDeploymentManager;
import org.netbeans.spi.project.libraries.LibraryImplementation;

/**
 *
 * @author John Platts
 */
class GeronimoJ2eePlatformImpl extends J2eePlatformImpl {
    
    GeronimoDeploymentManager dm ;
    
    private static final String GERONIMO_ICON = "org/netbeans/modules/j2ee/wasce/resources/server.gif";
    
    private static final Set SUPPORTED_SPEC_VERSIONS ;
    private static final Set SUPPORTED_JAVA_PLATFORM_VERSIONS ;
    private static final Set SUPPORTED_MODULE_TYPES ;
    private static final File[] NO_TOOL_CLASSPATH_ENTRIES ;
    private static final String[] GERONIMO2_JAVAEE_JARS ;
    private static final int GERONIMO2_JAVAEE_JARS_LENGTH ;
    
    static
    {
        SUPPORTED_SPEC_VERSIONS = new HashSet() ;
        SUPPORTED_SPEC_VERSIONS.add(J2eeModule.J2EE_14) ;
        SUPPORTED_SPEC_VERSIONS.add(J2eeModule.JAVA_EE_5) ;
        
        SUPPORTED_JAVA_PLATFORM_VERSIONS = new HashSet();
        SUPPORTED_JAVA_PLATFORM_VERSIONS.add("1.5");
        SUPPORTED_JAVA_PLATFORM_VERSIONS.add("1.6");
        
        SUPPORTED_MODULE_TYPES = new HashSet() ;
        SUPPORTED_MODULE_TYPES.add(J2eeModule.EAR);
        SUPPORTED_MODULE_TYPES.add(J2eeModule.WAR);
        SUPPORTED_MODULE_TYPES.add(J2eeModule.CONN);
        SUPPORTED_MODULE_TYPES.add(J2eeModule.EJB);
        SUPPORTED_MODULE_TYPES.add(J2eeModule.CLIENT);
        
        NO_TOOL_CLASSPATH_ENTRIES = new File[0] ;
        
        GERONIMO2_JAVAEE_JARS = new String[]
        {
            "/repository/jstl/jstl/1.2/jstl-1.2.jar",
            "/repository/org/apache/geronimo/specs/geronimo-activation_1.1_spec/1.0/geronimo-activation_1.1_spec-1.0.jar",
            "/repository/org/apache/geronimo/specs/geronimo-annotation_1.0_spec/1.1/geronimo-annotation_1.0_spec-1.1.jar",
            "/repository/org/apache/geronimo/specs/geronimo-ejb_3.0_spec/1.0/geronimo-ejb_3.0_spec-1.0.jar",
            "/repository/org/apache/geronimo/specs/geronimo-el_1.0_spec/1.0/geronimo-el_1.0_spec-1.0.jar",
            "/repository/org/apache/geronimo/specs/geronimo-javaee-deployment_1.1MR3_spec/1.0/geronimo-javaee-deployment_1.1MR3_spec-1.0.jar",
            "/repository/org/apache/myfaces/core/myfaces-api/1.2.0/myfaces-api-1.2.0.jar",
            "/repository/org/apache/geronimo/specs/geronimo-interceptor_3.0_spec/1.0/geronimo-interceptor_3.0_spec-1.0.jar",
            "/repository/org/apache/geronimo/specs/geronimo-jms_1.1_spec/1.1/geronimo-jms_1.1_spec-1.1.jar",
            "/repository/org/apache/geronimo/specs/geronimo-ws-metadata_2.0_spec/1.1.1/geronimo-ws-metadata_2.0_spec-1.1.1.jar",
            "/repository/org/apache/geronimo/javamail/geronimo-javamail_1.4_mail/1.2/geronimo-javamail_1.4_mail-1.2.jar",
            "/repository/org/apache/geronimo/specs/geronimo-j2ee-management_1.1_spec/1.0/geronimo-j2ee-management_1.1_spec-1.0.jar",
            "/repository/org/apache/geronimo/specs/geronimo-jpa_3.0_spec/1.1/geronimo-jpa_3.0_spec-1.1.jar",
            "/repository/org/apache/geronimo/specs/geronimo-j2ee-connector_1.5_spec/1.1.1/geronimo-j2ee-connector_1.5_spec-1.1.1.jar",
            "/repository/org/apache/geronimo/specs/geronimo-jacc_1.1_spec/1.0/geronimo-jacc_1.1_spec-1.0.jar",
            "/repository/org/apache/geronimo/specs/geronimo-servlet_2.5_spec/1.1/geronimo-servlet_2.5_spec-1.1.jar",
            "/repository/org/apache/geronimo/specs/geronimo-jsp_2.1_spec/1.0/geronimo-jsp_2.1_spec-1.0.jar",
            "/repository/org/apache/geronimo/specs/geronimo-jta_1.1_spec/1.1/geronimo-jta_1.1_spec-1.1.jar",
            "/repository/javax/xml/bind/jaxb-api/2.0/jaxb-api-2.0.jar",
            "/repository/org/apache/geronimo/specs/geronimo-jaxr_1.0_spec/1.1/geronimo-jaxr_1.0_spec-1.1.jar",
            "/repository/org/apache/geronimo/specs/geronimo-jaxrpc_1.1_spec/1.1/geronimo-jaxrpc_1.1_spec-1.1.jar",
            "/repository/org/apache/axis2/axis2-saaj-api/1.3/axis2-saaj-api-1.3.jar",
            "/repository/org/apache/geronimo/specs/geronimo-stax-api_1.0_spec/1.0/geronimo-stax-api_1.0_spec-1.0.jar",
            "/repository/org/apache/axis2/axis2-jaxws-api/1.3/axis2-jaxws-api-1.3.jar"
        } ;
        GERONIMO2_JAVAEE_JARS_LENGTH = GERONIMO2_JAVAEE_JARS.length ;
    }

    public GeronimoJ2eePlatformImpl(GeronimoDeploymentManager dm) {
        this.dm = dm ;
    }
    
    private static URL fileToUrl(File file) throws MalformedURLException {
        URL url = file.toURI().toURL() ;
        
        if(FileUtil.isArchiveFile(url)) {
            url = FileUtil.getArchiveRoot(url);
        }
        
        return url ;
    }
    
    @Override
    public LibraryImplementation[] getLibraries() {
        LibraryImplementation[] libraries = new LibraryImplementation[1] ;
        
        LibraryImplementation library = new J2eeLibraryTypeProvider().
                createLibrary() ;
        
        library.setName(NbBundle.getMessage(GeronimoJ2eePlatformFactory.class,
                "TXT_libraryName")) ;
        
        String serverRoot = dm.getHost() ;
        HashSet<URL> librarySet = new HashSet<URL>() ;
        
        for(int i = 0; i < GERONIMO2_JAVAEE_JARS_LENGTH; i++)
        {
            try
            {
                librarySet.add(fileToUrl(new File(serverRoot,
                          GERONIMO2_JAVAEE_JARS[i])));
            }
            catch(MalformedURLException e)
            {
                Logger.getLogger("global").log(Level.WARNING, null, e) ;
            }
        }
        
        ArrayList<URL> libraryList = new ArrayList<URL>(librarySet) ;
              
        library.setContent(J2eeLibraryTypeProvider.VOLUME_TYPE_CLASSPATH,
                libraryList) ;
        
        libraries[0] = library ;
        
        return libraries ;
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(GeronimoJ2eePlatformImpl.class,
                                   "TXT_platformName"); 
    }

    @Override
    public Image getIcon() {
        return Utilities.loadImage(GERONIMO_ICON) ;
    }

    @Override
    public File[] getPlatformRoots() {
        return new File[] {
            new File(dm.getServerRoot())
        };
    }

    @Override
    public File[] getToolClasspathEntries(String arg0) {
        return NO_TOOL_CLASSPATH_ENTRIES ;
    }

    @Override
    public boolean isToolSupported(String toolName) {
        return false ;
    }

    @Override
    public Set getSupportedSpecVersions() {
        return SUPPORTED_SPEC_VERSIONS ;
    }

    @Override
    public Set getSupportedModuleTypes() {
        return SUPPORTED_MODULE_TYPES;
    }

    @Override
    public Set getSupportedJavaPlatformVersions() {
        return SUPPORTED_JAVA_PLATFORM_VERSIONS;
    }

    @Override
    public JavaPlatform getJavaPlatform() {
        return null;
    }
}
