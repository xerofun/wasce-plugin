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

import javax.swing.JTabbedPane;
import org.netbeans.modules.j2ee.deployment.plugins.spi.J2eePlatformImpl;
import org.openide.util.NbBundle;
import net.sourceforge.nbgeronimo.j2ee.DeploymentManagerProperties;

/**
 *
 * @author John Platts
 */
public class Customizer extends JTabbedPane {
    private J2eePlatformImpl platform;
    private DeploymentManagerProperties dmp;
    
    public Customizer(J2eePlatformImpl platform,
            DeploymentManagerProperties dmp)
    {
        this.platform = platform ;
        this.dmp = dmp ;
        initComponents();
    }
    
    private void initComponents() {
        getAccessibleContext().setAccessibleName (NbBundle.getMessage(Customizer.class,"TXT_CustomizerTitle"));
        getAccessibleContext().setAccessibleDescription (NbBundle.getMessage(Customizer.class,"TXT_CustomizerTitle"));
        
        addTab(NbBundle.getMessage(Customizer.class,"TXT_Classes"), new PathView(platform, PathView.CLASSPATH));
        addTab(NbBundle.getMessage(Customizer.class,"TXT_Sources"), new PathView(platform, PathView.SOURCES));
        addTab(NbBundle.getMessage(Customizer.class,"TXT_Javadoc"), new PathView(platform, PathView.JAVADOC));
    }
}
