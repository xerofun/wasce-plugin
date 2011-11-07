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

package net.sourceforge.nbgeronimo.optional;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.deploy.shared.ActionType;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.StateType;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.status.ProgressObject;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.deployment.plugins.api.ServerDebugInfo;
import org.netbeans.modules.j2ee.deployment.plugins.spi.StartServer;
import org.netbeans.modules.j2ee.wasce.GeronimoDeploymentManager;
import org.netbeans.modules.j2ee.wasce.GeronimoUtils;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author John Platts
 */
public class GeronimoStartServer extends StartServer {
    private GeronimoDeploymentManager dm ;
    private int state ;
    private boolean isDebug ;
    private Process geronimoServerProcess ;
    
    public GeronimoStartServer(GeronimoDeploymentManager dm)
    {
        this.dm = dm ;
        state = STATE_STOPPED ;
        isDebug = false ;
        geronimoServerProcess = null ;
        System.out.println("user :"+dm.getUsername());
        System.out.println("password :"+dm.getPassword());
    }
    
    @Override
    public boolean isAlsoTargetServer(Target target) {
        return true ;
    }
    
    @Override
    public boolean supportsStartDeploymentManager() {
        return true ;
    }

    private static final int START_TIMEOUT = 600000 ;
    private static final int START_DELAY = 5000 ;
    
    private static final int STOP_TIMEOUT = 600000 ;
    private static final int STOP_DELAY = 5000 ;
    
    private class GeronimoStartRunnable implements Runnable {
        //private GeronimoServerProgress serverProgress ;
        private GeronimoProgressObject serverProgress ;
        private String serverRoot ;
        
        public GeronimoStartRunnable(/*GeronimoServerProgress*/GeronimoProgressObject serverProgress) {
            this.serverProgress = serverProgress ;
            serverRoot = dm.getServerRoot() ;
        }
        public void run() {
            long start = System.currentTimeMillis() ;
            
            File commandFile =
                    GeronimoUtils.getGeronimoCommand(serverRoot) ;
            
            if((commandFile == null) || (!commandFile.exists()))
            {
                serverProgress.changeState(StateType.FAILED, "The geronimo command file was not found.");
//                serverProgress.notify(
//                        new GeronimoDeploymentStatus(ActionType.EXECUTE,
//                        CommandType.START, StateType.FAILED,
//                        "The geronimo command file was not found."));
                return ;
            }
            
            ProcessBuilder processBuilder =
                    new ProcessBuilder(commandFile.getAbsolutePath(),
                    "run") ;
            
            Map<String, String> geronimoEnvironment = processBuilder.environment() ;
            
            Process process ;
            try
            {
                process = processBuilder.start() ;
            }
            catch(IOException e)
            {
                  serverProgress.changeState(StateType.FAILED, "An error occurred while executing the geronimo command.");
//                serverProgress.notify(
//                        new GeronimoDeploymentStatus(ActionType.EXECUTE,
//                        CommandType.START, StateType.FAILED,
//                        "An error occurred while executing the geronimo command."));
                geronimoServerProcess = null ;
                return ;
            }
            
            GeronimoTailer inputTailer = new GeronimoTailer(process.getInputStream(),
                    dm.getServerTitleMessage()) ;
            inputTailer.start() ;
            
            GeronimoTailer logTailer = new GeronimoTailer(
                    GeronimoUtils.getGeronimoLog(serverRoot),
                    NbBundle.getMessage(GeronimoStartServer.class,
                            "TXT_logWindowTitle", dm.getServerTitleMessage())) ;
            logTailer.start() ;
            
            geronimoServerProcess = process ;
            
            while(System.currentTimeMillis() - start < START_TIMEOUT) {
                if(!isRunning()) {
                   // serverProgress.notifyStart(StateType.RUNNING,"") ;
                  //  dserverProgress.changeState(StateType.FAILED, "An error occurred while executing the geronimo command.");
                  fireStateChange(StateType.RUNNING, "MSG_StartServerTimeout");
                }
                else
                {
                  //  serverProgress.notifyStart(StateType.COMPLETED,                            "") ;
                    fireStateChange(StateType.COMPLETED, "MSG_ServerStarted");                    state = STATE_STARTED ;
                    System.out.println("terminou ok");
                    return ;
                }
                
                try {
                    Thread.sleep(START_DELAY) ;
                }
                catch(InterruptedException e) {}
            }
            
            // If the server did not start within the appropriate timeout
            // the startup is considered to be failed and the process
            // should be killed
            //serverProgress.notifyStart(StateType.FAILED, "") ;
            serverProgress.changeState(StateType.FAILED, "Erro desconhecido");
            //fireStateChange(StateType.FAILED, "Erro");
            process.destroy();
            geronimoServerProcess = null ;
            
            state = STATE_STOPPED ;
        }
        private void fireStateChange(StateType stateType, String msgKey, String... msgParams) {
            InstanceProperties ip = dm.getInstanceProperties();
            final String serverName = ip.getProperty(InstanceProperties.DISPLAY_NAME_ATTR);
            String msg = NbBundle.getMessage(GeronimoStartServer.class, msgKey, serverName, msgParams);
            serverProgress.changeState(stateType, msg);
        }
    }
    
    @Override
    public ProgressObject startDeploymentManager() {
        //GeronimoServerProgress serverProgress = new GeronimoServerProgress(this) ;
        GeronimoProgressObject serverProgress = new GeronimoProgressObject(this) ;
        //serverProgress.notifyStart(StateType.RUNNING, "") ;
        
        String serverName = dm.getInstanceProperties().getProperty(dm.getServerName());
        String msg = NbBundle.getMessage(GeronimoStartServer.class, "MSG_StartServerInProgress", serverName);

        
        
        serverProgress.changeState(StateType.RUNNING, msg);
        if(state == STATE_STOPPING) {
            for(int i = 0; i < STOP_TIMEOUT; i += STOP_DELAY) {
                if(state == STATE_STOPPING) {
                    try {
                        Thread.sleep(STOP_DELAY) ;
                    } catch(InterruptedException e) {
                        // Ignore any InterruptedExceptions that are thrown
                    }
                }
                
                if(state == STATE_STOPPED) {
                    break ;
                }
                if(geronimoServerProcess == null)
                {
                    state = STATE_STOPPED ;
                    break ;
                }
            }
        }
        
        if(state == STATE_STARTING || state == STATE_STARTED) {
           // serverProgress.notifyStart(StateType.COMPLETED, "") ;
             String m = NbBundle.getMessage(GeronimoStartServer.class, "MSG_ServerStarted", dm.getServerName());
            serverProgress.changeState(StateType.COMPLETED,  m);
            return serverProgress ;
        }
        
        RequestProcessor.getDefault().post(new GeronimoStartRunnable(serverProgress),
                0, Thread.NORM_PRIORITY) ;
        
        state = STATE_STARTING ;
        
        return serverProgress ;
    }
    
    private class GeronimoStopRunnable implements Runnable {
        private GeronimoProgressObject serverProgress ;
        private String serverRoot ;
        
        public GeronimoStopRunnable(/*GeronimoServerProgress*/GeronimoProgressObject serverProgress) {
            this.serverProgress = serverProgress ;
            serverRoot = dm.getServerRoot() ;
        }
        public void run() {
            long start = System.currentTimeMillis() ;
            
            File commandFile =
                    GeronimoUtils.getGeronimoCommand(serverRoot) ;
            
            if((commandFile == null) || (!commandFile.exists()))
            {
               serverProgress.changeState(StateType.FAILED, "The geronimo command file was not found.");
               //serverProgress.notify( new GeronimoDeploymentStatus(ActionType.EXECUTE, CommandType.STOP, StateType.FAILED, "The geronimo command file was not found."));
                return ;
            }
            
            ProcessBuilder processBuilder =
                    new ProcessBuilder(commandFile.getAbsolutePath(),
                    "stop") ;
            
            Map<String, String> geronimoEnvironment = processBuilder.environment() ;
            
            Process process ;
            try
            {
                process = processBuilder.start() ;
            }
            catch(IOException e)
            {
                serverProgress.changeState(StateType.FAILED, "An error occurred while executing the geronimo command.");
//                serverProgress.notify( new GeronimoDeploymentStatus(ActionType.EXECUTE,CommandType.STOP, StateType.FAILED,"An error occurred while executing the geronimo command."));
                return ;
            }
            
             GeronimoTailer inputTailer = new GeronimoTailer(process.getInputStream(),dm.getServerTitleMessage()) ;
            inputTailer.start() ;
            GeronimoTailer logTailer = new GeronimoTailer(
                    GeronimoUtils.getGeronimoLog(serverRoot),
                    NbBundle.getMessage(GeronimoStartServer.class,
                            "TXT_logWindowTitle", dm.getServerTitleMessage())) ;
            logTailer.start() ;
            geronimoServerProcess = process ;
            
            while(System.currentTimeMillis() - start < STOP_TIMEOUT) {
                if(isRunning()) {
                    fireStateChange(StateType.RUNNING, "MSG_StopServerInProgress");

                   // serverProgress.notifyStop(StateType.RUNNING, "") ;
                }
                else
                {
                   fireStateChange(StateType.COMPLETED, "MSG_ServerStopped");
                    
                    state = STATE_STOPPED;
                    
                    return ;
                }
                
                try {
                    Thread.sleep(STOP_DELAY);
                } catch(InterruptedException e) {}
            }
            
            //serverProgress.notifyStop(StateType.FAILED, "");
            fireStateChange(StateType.FAILED, "MSG_StoptServerTimeout");
            process.destroy();
            
            state = STATE_STARTED;
        }
        private void fireStateChange(StateType stateType, String msgKey, String... msgParams) {
            InstanceProperties ip = dm.getInstanceProperties();
            final String serverName = ip.getProperty(InstanceProperties.DISPLAY_NAME_ATTR);
            String msg = NbBundle.getMessage(GeronimoStartServer.class, msgKey, serverName, msgParams);
            serverProgress.changeState(stateType, msg);
        }
        
    }

    @Override
    public ProgressObject stopDeploymentManager() {
        //GeronimoServerProgress serverProgress = new GeronimoServerProgress(this) ;
        GeronimoProgressObject serverProgress = new GeronimoProgressObject(this) ;
        //serverProgress.notifyStop(StateType.RUNNING, "") ;
        String serverName = dm.getInstanceProperties().getProperty(dm.getServerName());
        String msg = NbBundle.getMessage(GeronimoStartServer.class, "MSG_StopServerInProgress", serverName);
        serverProgress.changeState(StateType.RUNNING, msg);
        
                
        if(state == STATE_STARTING) {
            for(int i = 0; i < START_TIMEOUT; i += START_DELAY) {
                if(state == STATE_STARTING) {
                    try {
                        Thread.sleep(START_DELAY);
                    }
                    catch (InterruptedException e) {
                    }
                }
                
                if(state == STATE_STARTED) {
                    break;
                }
            }
        }
        
        if(state == STATE_STOPPING || state == STATE_STOPPED) {
            
        }
        
        return serverProgress;
    }

    @Override
    public boolean needsStartForConfigure() {
        return true ;
    }

    @Override
    public boolean needsStartForTargetList() {
        return true ;
    }

    @Override
    public boolean needsStartForAdminConfig() {
        return true ;
    }

    @Override
    public boolean isRunning() {
        Socket socket = null ;
        boolean serverIsRunning = false ;
        try {
            socket = new Socket(dm.getHost(), Integer.parseInt(dm.getPort())) ;
            
            // If a socket is created successfully, then the server
            // has started
            serverIsRunning = true ;
        }
        catch(UnknownHostException e) {
            Logger.getLogger("global").log(Level.SEVERE, null, e) ;
        }
        catch(IOException e) {
            // Ignore IOExceptions because the server has not yet started
        }
        finally
        {
            if(socket != null) {
                try {
                    socket.close() ;
                }
                catch(IOException e) {
                    Logger.getLogger("global").log(Level.WARNING, null, e);
                }
            }
        }
        
        // The server is not started if a socket cannot be created to the
        // server
        return serverIsRunning ;
    }

    @Override
    public boolean isDebuggable(Target target) {
        return false ;
    }

    @Override
    public ProgressObject startDebugging(Target target) {
        return null ;
    }

    @Override
    public ServerDebugInfo getDebugInfo(Target target) {
        return null ;
    }
    
    
    private static final int STATE_STOPPED  = 0;
    private static final int STATE_STARTING = 1;
    private static final int STATE_STARTED  = 2;
    private static final int STATE_STOPPING = 3;
}
