/*
 * WasCEJ2eePlatformImpl.java
 *
 * Created on June 11, 2005, 2:15 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.netbeans.modules.j2ee.wasce;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.plugins.spi.J2eePlatformImpl;
import org.netbeans.spi.project.libraries.LibraryImplementation;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;



/**
 *
 * @author Administrator
 */
public class WasCEJ2eePlatformImpl extends J2eePlatformImpl {
    
    public boolean isToolSupported(String toolName) {
        return false;
    }
    
    public File[] getToolClasspathEntries(String toolName) {
        return new File[0];
    }
    
    public Set getSupportedSpecVersions() {
        Set result = new HashSet();
        result.add(J2eeModule.J2EE_14);
        result.add(J2eeModule.JAVA_EE_5);
        return result;
    }
    
    public java.util.Set getSupportedModuleTypes() {
        Set result = new HashSet();
        result.add(J2eeModule.WAR);
        result.add(J2eeModule.EAR);
        result.add(J2eeModule.EJB);
        return result;
    }
    
    public java.io.File[] getPlatformRoots() {
        return new File[0];
    }
    
    public LibraryImplementation[] getLibraries() {
        return new LibraryImplementation[0];
    }
    
    public java.awt.Image getIcon() {
        return Utilities.loadImage("org/netbeans/modules/j2ee/wasce/resources/server.gif"); // NOI18N
        
    }
    
    public String getDisplayName() {
        return NbBundle.getMessage(WasCEJ2eePlatformImpl.class, "MSG_MyServerPlatform");
    }
    
    public Set getSupportedJavaPlatformVersions() {
        Set versions = new HashSet();
        versions.add("1.4"); // NOI18N
        versions.add("1.5"); // NOI18N
        //versions.add("1.6"); // NOI18N
        return versions;
    }
    
    public JavaPlatform getJavaPlatform() {
        return JavaPlatformManager.getDefault().getDefaultPlatform();
    }
}