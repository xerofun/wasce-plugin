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

package net.sourceforge.nbgeronimo.ui;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import org.netbeans.modules.j2ee.deployment.plugins.spi.J2eePlatformImpl;
import org.netbeans.spi.project.libraries.LibraryImplementation;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author John Platts
 */
class PathModel extends AbstractListModel {
    private J2eePlatformImpl platform ;
    private String type ;
    private List<URL> data ;
    
    public PathModel(J2eePlatformImpl platform, String type) {
        this.platform = platform ;
        this.type = type ;
    }

    public int getSize() {
        return getData().size() ;
    }

    public Object getElementAt(int index) {
        List<URL> list = getData() ;
        URL url = list.get(index) ;
        if("jar".equals(url.getProtocol())) {
            URL fileURL = FileUtil.getArchiveFile(url) ;
            if(FileUtil.getArchiveRoot(fileURL).equals(url)) {
                url = fileURL ;
            } else {
                return url.toExternalForm() ;
            }
        }
        if("file".equals(url.getProtocol())) {
            File f = new File(URI.create(url.toExternalForm())) ;
            return f.getAbsolutePath() ;
        }
        else
        {
            return url.toExternalForm() ;
        }
    }
    
    private synchronized List<URL> getData() {
        if(data == null) {
            data = new ArrayList<URL>() ;
            LibraryImplementation[] libImpl = platform.getLibraries() ;
            for(int i = 0; i < libImpl.length; i++) {
                data.addAll(libImpl[i].getContent(type)) ;
            }
        }
        return data ;
    }
}
