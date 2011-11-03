/*
 * WasCEInstanceNode.java
 *
 * Created on June 11, 2005, 1:38 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.netbeans.modules.j2ee.wasce.nodes;

import java.awt.Component;
import java.awt.Label;
import javax.swing.JPanel;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Administrator
 */
public class WasCEInstanceNode extends AbstractNode implements Node.Cookie {
    
    private static String ICON_BASE = "org/netbeans/modules/j2ee/wasce/resources/server.gif"; // NOI18N
    
    public WasCEInstanceNode(Lookup lookup) {
        super(new Children.Array());
        getCookieSet().add(this);
        setIconBaseWithExtension(ICON_BASE);
    }
    
    public String getDisplayName() {
        return NbBundle.getMessage(WasCEInstanceNode.class, "TXT_MyInstanceNode");
    }
    
    public String getShortDescription() {
        return "http://localhost:8080"; // NOI18N
    }
    
    public javax.swing.Action[] getActions(boolean context) {
        return new javax.swing.Action[]{};
    }
    
    public boolean hasCustomizer() {
        return true;
    }
    
    public Component getCustomizer() {
        JPanel panel = new JPanel();
        panel.add(new Label("< Put your customizer implementation here! >")); // NOI18N
        return panel;
    }
}
