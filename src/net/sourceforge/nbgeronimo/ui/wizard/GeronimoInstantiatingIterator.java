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

package net.sourceforge.nbgeronimo.ui.wizard;

import java.io.IOException;
import java.util.Set;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;

/**
 *
 * @author John Platts
 */
public class GeronimoInstantiatingIterator 
    implements WizardDescriptor.InstantiatingIterator {

    public Set instantiate() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void initialize(WizardDescriptor wizardDescriptor) {
        this.wizardDescriptor = wizardDescriptor;
    }

    public void uninitialize(WizardDescriptor wizardDescriptor) {
        // do nothing unless uninitialization is required
    }

    public Panel current() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String name() {
        return "";
    }

    public boolean hasNext() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean hasPrevious() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void nextPanel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void previousPanel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addChangeListener(ChangeListener changeListener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeChangeListener(ChangeListener changeListener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private WizardDescriptor wizardDescriptor;
}
