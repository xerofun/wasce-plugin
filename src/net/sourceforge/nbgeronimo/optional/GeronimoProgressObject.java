/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package net.sourceforge.nbgeronimo.optional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;
import javax.enterprise.deploy.shared.ActionType;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.StateType;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.exceptions.OperationUnsupportedException;
import javax.enterprise.deploy.spi.status.ClientConfiguration;
import javax.enterprise.deploy.spi.status.DeploymentStatus;
import javax.enterprise.deploy.spi.status.ProgressEvent;
import javax.enterprise.deploy.spi.status.ProgressListener;
import javax.enterprise.deploy.spi.status.ProgressObject;
import org.netbeans.modules.j2ee.deployment.plugins.spi.StartServer;
import org.openide.util.NbBundle;

/**
 * The ProgressObject interface tracks and reports the progress of the deployment activities, distribute, start, stop, undeploy.
 *
 * @version $Rev: 679911 $ $Date: 2008-07-25 19:18:43 -0300 (sex, 25 jul 2008) $
 */
public class GeronimoProgressObject implements ProgressObject {

    private StartServer startServer;

    private DeploymentStatus deploymentStatus;

    private List<ProgressListener> listeners = new Vector<ProgressListener>();

    private static final Logger logger = Logger.getLogger(GeronimoProgressObject.class.getName());

    public GeronimoProgressObject(StartServer startServer) {
        this.startServer = startServer;
    }

    public void changeState(StateType state, String message) {
        fireProgressEvent(new GeronimoDeploymentStatus(ActionType.EXECUTE, CommandType.START, state, message));
    }

    private void fireProgressEvent(DeploymentStatus deploymentStatus) {
        ProgressEvent evt = new ProgressEvent(startServer, null, deploymentStatus);

        this.deploymentStatus = deploymentStatus;

        Iterator<ProgressListener> iterator = null;
        synchronized (this) {
            if (listeners != null) {
                iterator = new ArrayList<ProgressListener>(listeners).iterator();
            }
        }
        if (iterator != null) {
            while (iterator.hasNext()) {
                iterator.next().handleProgressEvent(evt);
            }
        }
    }

    public DeploymentStatus getDeploymentStatus() {
        return deploymentStatus;
    }

    public TargetModuleID[] getResultTargetModuleIDs() {
        return new TargetModuleID[0];
    }

    public ClientConfiguration getClientConfiguration(TargetModuleID targetModuleId) {
        return null;
    }

    public boolean isCancelSupported() {
        return false;
    }

    public void cancel() throws OperationUnsupportedException {
        String msg = NbBundle.getMessage(GeronimoProgressObject.class, "MSG_OperationNotSupported");
        throw new UnsupportedOperationException(msg);
    }

    public boolean isStopSupported() {
        return false;
    }

    public void stop() throws OperationUnsupportedException {
        String msg = NbBundle.getMessage(GeronimoProgressObject.class, "MSG_OperationNotSupported");
        throw new UnsupportedOperationException(msg);
    }

    public void addProgressListener(ProgressListener progressListener) {
        listeners.add(progressListener);
    }

    public void removeProgressListener(ProgressListener progressListener) {
        listeners.remove(progressListener);
    }
}
