/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Daniel Gomes. All rights reserved.
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
 * Software is Daniel Gomes. Portions Copyright 2011 Daniel Gomes.
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

import java.awt.Component;
import java.awt.Label;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceCreationException;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;


/**
 *
 * @author Administrator
 */
public class WasCEInstantiatingIterator implements WizardDescriptor.InstantiatingIterator {
    
    private final static String PROP_DISPLAY_NAME = "ServInstWizard_displayName"; // NOI18N

        
    private WasCEWizardPanel panel;
    private WizardDescriptor wizard;
    
    public void removeChangeListener(ChangeListener l) {
    }

    public void addChangeListener(ChangeListener l) {
    }

    public void uninitialize(WizardDescriptor wizard) {
    }

    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
    }

    public void previousPanel() {
    }

    public void nextPanel() {
    }

    public String name() {
        return NbBundle.getMessage(WasCEInstantiatingIterator.class, "MSG_InstallerName");
    }

    public Set instantiate() throws InstanceCreationException  {
           Set result = new HashSet();
           Map<String, String> s= new HashMap<String, String>();
           s.put("serverRoot", current().getComponent().getPath());
           s.put("serverName", getDisplayName());
           InstanceProperties ip =GeronimoURIManager.createInstanceProperties(current().getComponent().getHost(), current().getComponent().getPort(), current().getComponent().getUser(), current().getComponent().getPwd(), getDisplayName(),s);
           result.add(ip);
           return result;
         
    }

    public boolean hasPrevious() {
        return false;
    }

    public boolean hasNext() {
        return false;
    }

    public WasCEWizardPanel current() {
        if (panel == null) {
                panel = new WasCEWizardPanel();                
            }
            return panel;

    }
    
    private String getDisplayName() {
        return (String)wizard.getProperty(PROP_DISPLAY_NAME);
    }
    
    

}