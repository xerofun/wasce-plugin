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
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author John Platts
 * changed by Daniel Gomes, included new File(serverRoot + "/jsr88/") .
 */
public class GeronimoClassLoader extends URLClassLoader
{
    private static Map<String, GeronimoClassLoader> instances =
            Collections.synchronizedMap(new HashMap<String, GeronimoClassLoader>()) ;
    
    private String serverRoot ;
    
    public static GeronimoClassLoader getInstance(String serverRoot)
    {
        GeronimoClassLoader instance =
                instances.get(serverRoot) ;
        
        if(instance == null)
        {
            instance = new GeronimoClassLoader(serverRoot) ;
            instances.put(serverRoot, instance) ;
        }
        
        return instance ;
    }
    
    private GeronimoClassLoader(String serverRoot)
    {
        super(new URL[0], Thread.currentThread().getContextClassLoader()) ;
        
        this.serverRoot = serverRoot ;
        
        File[] directories = new File[]
        {
            new File(serverRoot + "/lib/"),
            new File(serverRoot + "/jsr88/")
        };
        
        for(int i = 0; i < directories.length; i++)
        {
            File dir = directories[i] ;
            if(dir.exists() && dir.isDirectory())
            {
                File[] children = dir.listFiles(new FileFilter()
                {
                    public boolean accept(File file) {
                        return file.getName().endsWith(".jar") ;
                    }
                }) ;
                for(int j = 0; j < children.length; j++)
                {
                    try
                    {
                        addURL(children[j].toURI().toURL()) ;
                        System.out.println("Loading :"+children[j].toURI().toURL());
                    }
                    catch(MalformedURLException e)
                    {
                        // Ignore the MalformedURLException and continue
                    }
                }
            }
            try
            {
                addURL(dir.toURI().toURL());
            }
            catch(MalformedURLException e)
            {
                // Ignore the MalformedURLException and continue
            }
        }
    }
    
    private ClassLoader oldLoader = null ;
    
    public synchronized void updateLoader()
    {
        
        Thread currentThread = Thread.currentThread() ;
        
        if (!currentThread.getContextClassLoader().equals(this))
        {
            oldLoader = currentThread.getContextClassLoader();
            currentThread.setContextClassLoader(this);
        }
    }
    public synchronized void restoreLoader()
    {
        Thread currentThread = Thread.currentThread() ;
        
        if(oldLoader != null)
        {
            currentThread.setContextClassLoader(oldLoader) ;
            oldLoader = null ;
        }
    }
}
