/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. Portions Copyright 2007 John Platts. All Rights
 * Reserved.
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

/* This file was copied from the NetBeans 6.0 source code and adapted
 * for use in nbgeronimo by John Platts. */

package net.sourceforge.nbgeronimo.ui.nodes.editors;

import java.awt.Component;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyEditorSupport;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;

/**
 * An editor for the password field that appears on the properties sheet for
 * the instance node.
 *
 * @author Kirill Sorokin
 */
public class GeronimoPasswordEditor extends PropertyEditorSupport
        implements ExPropertyEditor {
    
    /**
     * The internal store for the edited object, for this editor we assume
     * that it's a string
     */
    private String value = "";
    
    /**
     * Returns the edited object as a string.
     *
     * @return the string representation of the edited object
     */
    @Override
    public String getAsText() {
        
        // return the object masquerading its real value with asterisks
        return value.replaceAll(".", "*");                             // NOI18N
    }
    
    /**
     * Sets the edited object value by supplying a string representation of
     * the new value
     *
     * @param string the string representation of the new value
     */
    @Override
    public void setAsText(String string) throws IllegalArgumentException {
       
        // if the supplied string is not null 0 update the value and notify the
        // listeners
        if (string != null) {
            value = string;
            firePropertyChange();
        }
    }
    
    /**
     * Implementation of ExPropertyEditor interface
     * Working is not checked
     */
    private PropertyEnv myPropertyEnv = null;
    
    public void attachEnv(PropertyEnv env) {
        myPropertyEnv = env;
    }
    
    /**
     * Sets the edited object value
     *
     * @param object the new value
     */
    @Override
    public void setValue(Object object) {
        
        // if the supplied object is not null - update the value
        if (object != null) {
            value = object.toString();
        }
    }
    
    /**
     * Returns the edited object's value
     *
     * @return the edited object's value
     */
    @Override
    public Object getValue() {
      
        // return
        return value;
    }
    
    /**
     * Returns the custom in-line editor for the object. In this case it's
     * a password field
     *
     * @return a swing component for editing the object
     */
    public Component getInPlaceCustomEditor() {
        
        // init the password field
        JPasswordField textfield = new JPasswordField(value);
        
        // set its looks
        textfield.setEchoChar('*');
        textfield.setBorder(new EmptyBorder(0, 0, 0, 0));
        textfield.setMargin(new Insets(0, 0, 0, 0));
        
        // select the component's text
        textfield.selectAll();
        
        // add a key listener
        textfield.addKeyListener(new PasswordListener());
        
        // return the component
        return textfield;
    }
    
    /**
     * Tells whether this editor support custom in-line editing
     *
     * @return true
     */
    public boolean hasInPlaceCustomEditor() {
       
        // return
        return true;
    }
    
    /**
     * This method is not clear - but apparently there are no tagged values in
     * a password, so we return false
     *
     * @return false
     */
    public boolean supportsEditingTaggedValues() {
       
        // return
        return false;
    }
    
    /**
     * This a listener that is attached to the editor field and watches
     * keystrokes appending the input characters to the value
     *
     * @author Kirill Sorokin
     */
    private class PasswordListener extends KeyAdapter {
        /**
         * Triggered when a keyboard button is released
         *
         * @param event the corresponding keyboard event
         */
        @Override
        public void keyReleased(KeyEvent event) {
            
            // get the event's source (in out case it would be a password field
            JPasswordField field = (JPasswordField) event.getSource();
            
            // update the edited object with the newly entered value
            value = new String(field.getPassword());
            
            // notify the listeners
            firePropertyChange();
            
            // if the enter key was pressed simulate the pressing of the
            // escape key so that the editor closes
            if(event.getKeyCode() == KeyEvent.VK_ENTER){
                KeyEvent escapeEvent = new KeyEvent(event.getComponent(),
                        KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_ESCAPE,
                        KeyEvent.CHAR_UNDEFINED);
                KeyboardFocusManager.getCurrentKeyboardFocusManager().
                        dispatchKeyEvent(escapeEvent);
            }
        }
    }
    
}