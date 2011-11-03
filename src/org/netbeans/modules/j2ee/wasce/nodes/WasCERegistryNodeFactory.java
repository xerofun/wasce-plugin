/*
 * WasCERegistryNodeFactory.java
 *
 * Created on June 11, 2005, 2:09 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.netbeans.modules.j2ee.wasce.nodes;

import org.netbeans.modules.j2ee.deployment.plugins.spi.RegistryNodeFactory;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author Administrator
 */
public class WasCERegistryNodeFactory implements RegistryNodeFactory {
    
    public Node getTargetNode(Lookup lookup) {
        return null;
    }
    
    public Node getManagerNode(Lookup lookup) {
        return new WasCEInstanceNode(lookup);
    }    
}
