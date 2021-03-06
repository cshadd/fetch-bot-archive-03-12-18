/*
 * MIT License
 * 
 * Copyright (c) 2018 Christian Shadd
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * https://cshadd.github.io/fetch-bot/
 */
package io.github.cshadd.fetch_bot;

import io.github.cshadd.fetch_bot.controllers.ControllerException;
import io.github.cshadd.fetch_bot.controllers.HUDController;
import io.github.cshadd.fetch_bot.controllers.HUDControllerImpl;
import io.github.cshadd.fetch_bot.controllers.OpenCVController;
import io.github.cshadd.fetch_bot.controllers.OpenCVControllerImpl;
import io.github.cshadd.fetch_bot.controllers.PathfindController;
import io.github.cshadd.fetch_bot.controllers.PathfindControllerImpl;
import io.github.cshadd.fetch_bot.io.json.ArduinoCommunication;
import io.github.cshadd.fetch_bot.io.json.ArduinoCommunicationImpl;
import io.github.cshadd.fetch_bot.io.CommunicationException;
import io.github.cshadd.fetch_bot.io.json.WebInterfaceCommunication;
import io.github.cshadd.fetch_bot.io.json.WebInterfaceCommunicationImpl;
import io.github.cshadd.fetch_bot.io.socket.SocketImageStreamCommunication;
import io.github.cshadd.fetch_bot.io.socket.SocketImageStreamCommunicationImpl;
import io.github.cshadd.fetch_bot.util.Logger;
import io.github.cshadd.fetch_bot.util.VersionCheck;
import io.github.cshadd.fetch_bot.util.VersionCheckException;

// Main

/**
 * The Class Core. Core is Fetch Bot's main application.
 * 
 * @author Christian Shadd
 * @author Maria Verna Aquino
 * @author Thanh Vu
 * @author Joseph Damian
 * @author Giovanni Orozco
 * @since 1.0.0
 */
@Component("Core")
public class Core implements FetchBot {
    // Private Constant Instance/Property Fields
    
    /**
     * The Constant SENSOR_LIMIT.
     */
    private static final int SENSOR_LIMIT = 30;
    
    /**
     * The Constant VERSION.
     */
    private static final String VERSION = "v2.0.0-alpha.4";
    
    // Protected Static Instance/Property Fields
    
    /**
     * The Arduino Communications.
     */
    protected static ArduinoCommunication arduinoComm;
    
    /**
     * The current track status.
     */
    protected static String currentTrackStatus;
    
    /**
     * The HUD Controller.
     */
    protected static HUDController hudControl;
    
    /**
     * The HUD socket thread.
     */
    protected static Thread hudSocketThread;
    
    /**
     * The HUD socket runnable thread.
     */
    protected static HUDSocketThread hudSocketRunnable;
    
    /**
     * The OpenCV Controller.
     */
    protected static OpenCVController openCVControl;
    
    /**
     * The Pathfind Controller.
     */
    protected static PathfindController pathfindControl;
    
    /**
     * The Socket Image Stream Communications.
     */
    protected static SocketImageStreamCommunication socketImageStreamComm;
    
    /**
     * The Web Interface Communications.
     */
    protected static WebInterfaceCommunication webInterfaceComm;
    
    // Public Constructors
    
    /**
     * Instantiates a new Core.
     */
    public Core() {
    }
    
    // Private Static Final Nested Classes
    
    /**
     * The Class HUDSocketThread. A Runnable that controls the HUD Socket.
     * 
     * @author Christian Shadd
     * @author Maria Verna Aquino
     * @author Thanh Vu
     * @author Joseph Damian
     * @author Giovanni Orozco
     * @since 2.0.0-alpha.1
     */
    private static final class HUDSocketThread implements Runnable {
        // Private Instance/Property Fields
        
        /**
         * The running state.
         */
        private volatile boolean running;
        
        // Public Constructors
        
        /**
         * Instantiates a new HUD Socket Thread.
         */
        public HUDSocketThread() {
            super();
            this.running = false;
        }
        
        // Public Methods
        
        /**
         * Terminate the thread.
         */
        public void terminate() {
            this.running = false;
        }
        
        // Public Methods (Overrided)
        
        /**
         * Runs the HUD Socket.
         * 
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            this.running = true;
            while (this.running) {
                hudControl.updateBack(openCVControl.takeCameraImageIcon());
                final int[] trackBounds = openCVControl.takeTrackBounds();
                hudControl.updateTrackBounds(trackBounds[0], trackBounds[1],
                                trackBounds[2], trackBounds[3]);
                hudControl.updateTrackCaptureLabel(openCVControl
                                .takeTrackCaptureLabel());
                currentTrackStatus = "Fetch Bot " + VERSION
                                + "<br />&#187; Status: Processing<br />"
                                + openCVControl.takeStatus() + "<br />";
                try {
                    if ((int) arduinoComm.getRobotFloatValue(
                                    "s") <= SENSOR_LIMIT) {
                        currentTrackStatus += "Imminent Collision!";
                    }
                } catch (@SuppressWarnings("unused") Exception e) {
                    /* */
                } // Suppressed
                finally {
                    /* */ }
                hudControl.updateStatus(currentTrackStatus);
                Logger.debug("HUD updated.");
                try {
                    socketImageStreamComm.listen();
                    socketImageStreamComm.write(hudControl.takeHUD());
                    Logger.debug("Stream sent.");
                } catch (@SuppressWarnings("unused") Exception e) {
                    try {
                        // Reconnect socket on error.
                        socketImageStreamComm.close();
                        socketImageStreamComm.open();
                    } catch (@SuppressWarnings("unused") Exception e2) {
                        /* */ } // Suppressed
                    /* */
                } // Suppressed
                finally {
                    /* */ }
            }
        }
    }
    
    // Private Nested Classes
    
    /**
     * The Enum CommandLineArgument. Provides the command line argument flags.
     * 
     * @author Christian Shadd
     * @author Maria Verna Aquino
     * @author Thanh Vu
     * @author Joseph Damian
     * @author Giovanni Orozco
     * @since 2.0.0-alpha
     */
    private enum CommandLineArgument {
        // Public Constant Instance/Property Fields
        
        /**
         * The normal flag.
         */
        NORMAL("Normal execution mode."),
        /**
         * The debug flag.
         */
        DEBUG("Debugging mode."),
        /**
         * The help flag.
         */
        HELP("View help."),
        /**
         * The version flag.
         */
        V("View version.");
        
        /**
         * The Constant DEFAULT_FLAG.
         */
        public static final CommandLineArgument DEFAULT_FLAG = NORMAL;
        
        // Private Instance/Property Fields
        
        /**
         * The description.
         */
        private final String description;
        
        // Private Constructors
        
        /**
         * Instantiates a new Command Line Argument.
         */
        private CommandLineArgument() {
            this(null);
        }
        
        /**
         * Instantiates a new Command Line Argument with description.
         *
         * @param newDescription
         *            the new description
         */
        private CommandLineArgument(String newDescription) {
            this.description = newDescription;
        }
        
        // Public Methods
        
        /**
         * Gets the description.
         *
         * @return the description
         */
        public String getDescription() {
            return this.description;
        }
    }
    
    // Private Static Methods
    
    /**
     * Runs the main application actions.
     */
    private static void run() {
        String currentMode = "Idle";
        String currentMove = "Stop";
        String currentTrackClass = "None";
        boolean tracked = false;
        int currentUltrasonicSensor = -1;
        
        currentTrackStatus = "";
        
        while (true) {
            try {
                // Pull data from communications
                Logger.debug("Pulling.");
                webInterfaceComm.pullSource();
                webInterfaceComm.pullRobot();
                arduinoComm.pullRobot();
                Logger.debug("Finished pulling.");
                
                // Sensors
                final float rawUltrasonicSensor = arduinoComm
                                .getRobotFloatValue("s");
                final int ultrasonicSensor = (int) rawUltrasonicSensor;
                if (ultrasonicSensor != -1) {
                    if (ultrasonicSensor != currentUltrasonicSensor) {
                        currentUltrasonicSensor = ultrasonicSensor;
                        Logger.debug("Arduino - [s: " + currentUltrasonicSensor
                                        + "] received.");
                        webInterfaceComm.setSourceValue("ultrasonic", ""
                                        + currentUltrasonicSensor);
                    }
                }
                
                // Tracking Class
                final String trackClass = webInterfaceComm.getRobotValue(
                                "trackclass");
                if (trackClass != null) {
                    if (!trackClass.equals(currentTrackClass)) {
                        currentTrackClass = trackClass;
                        Logger.debug("WebInterface - [trackclass: "
                                        + currentTrackClass + "] received.");
                        openCVControl.assignTrackClass(currentTrackClass);
                        webInterfaceComm.setSourceValue("trackclass", ""
                                        + currentTrackClass);
                        Logger.info("Core - Please wait.");
                        pathfindControl.reset();
                        Logger.info("Core - Ready.");
                        tracked = false;
                    }
                }
                
                // Mode
                final String mode = webInterfaceComm.getRobotValue("mode");
                if (mode != null) {
                    if (!mode.equals(currentMode)) {
                        currentMode = mode;
                        Logger.debug("WebInterface - [mode: " + currentMode
                                        + "] received.");
                        webInterfaceComm.setSourceValue("mode", currentMode);
                        arduinoComm.setSourceValue("a", "Stop");
                    }
                    
                    if (currentMode.equals("Auto")) {
                        if (currentTrackClass != null) {
                            if (!currentTrackClass.equals("None")) {
                                if (!tracked) {
                                    if (currentUltrasonicSensor <= SENSOR_LIMIT) {
                                        Logger.warn("Arduino - Safety cut due to imminent collision.");
                                        pathfindControl.blockNext();
                                    } else {
                                        pathfindControl.unblockNext();
                                    }
                                    Logger.debug("PathfindController - ["
                                                    + pathfindControl + "].");
                                    
                                    if (openCVControl.isTrackClassFound()) {
                                        Logger.info("OpenCVController - Target found!");
                                        webInterfaceComm.setSourceValue(
                                                        "emotion", "Happy");
                                        arduinoComm.setSourceValue("a", "Stop");
                                        tracked = true;
                                    } else { // Pathfinding
                                        Logger.info("OpenCVController - Target not found!");
                                        final boolean states[] = pathfindControl
                                                        .calculate();
                                        
                                        final boolean backBlocked = states[0];
                                        final boolean backVisited = states[1];
                                        final boolean frontBlocked = states[2];
                                        final boolean frontVisited = states[3];
                                        final boolean leftBlocked = states[4];
                                        final boolean leftVisited = states[5];
                                        final boolean rightBlocked = states[6];
                                        final boolean rightVisited = states[7];
                                        final String rawGraph = pathfindControl
                                                        .rawGraphToString();
                                        Logger.debug("PathfindController - [\n\n"
                                                        + rawGraph + "\n\n].");
                                        webInterfaceComm.setSourceValue(
                                                        "rawgraph", rawGraph);
                                        
                                        Logger.debug("PathfindController - [Back Blocked: "
                                                        + backBlocked
                                                        + "; Back Visited: "
                                                        + backVisited + "].");
                                        Logger.debug("PathfindController - [Front Blocked: "
                                                        + frontBlocked
                                                        + "; Front Visited: "
                                                        + frontVisited + "].");
                                        Logger.debug("PathfindController - [Left Blocked: "
                                                        + leftBlocked
                                                        + "; Left Visited: "
                                                        + leftVisited + "].");
                                        Logger.debug("PathfindController - [Right Blocked: "
                                                        + rightBlocked
                                                        + "; Right Visited: "
                                                        + rightVisited + "].");
                                        
                                        if (frontBlocked) { // B
                                            pathfindControl.rotateRight();
                                            if (rightBlocked) { // B-B
                                                pathfindControl.rotateRight();
                                                if (backBlocked) { // B-B-B
                                                    pathfindControl.rotateRight();
                                                    if (leftBlocked) { // B-B-B-B
                                                        pathfindControl.rotateRight();
                                                        Logger.debug("PathfindController - I can't seem to find a way out.");
                                                        webInterfaceComm.setSourceValue(
                                                                        "emotion",
                                                                        "Sad");
                                                        arduinoComm.setSourceValue(
                                                                        "a",
                                                                        "Stop");
                                                        tracked = true;
                                                    } else if (leftVisited) { // B-B-B-V
                                                        Logger.debug("PathfindController - Get out of my way!");
                                                        Logger.debug("PathfindController - Wait I visited that area before.");
                                                        Logger.debug("PathfindController - Left!");
                                                        webInterfaceComm.setSourceValue(
                                                                        "emotion",
                                                                        "Sad");
                                                        arduinoComm.setSourceValue(
                                                                        "a",
                                                                        "Left");
                                                    } else { // B-B-B-O
                                                        Logger.debug("PathfindController - Get out of my way!");
                                                        Logger.debug("PathfindController - Left!");
                                                        webInterfaceComm.setSourceValue(
                                                                        "emotion",
                                                                        "Angry");
                                                        arduinoComm.setSourceValue(
                                                                        "a",
                                                                        "Left");
                                                    }
                                                } else if (backVisited) { // B-B-V
                                                    pathfindControl.rotateRight();
                                                    if (leftBlocked) { // B-B-V-B
                                                        pathfindControl.rotateLeft();
                                                        Logger.debug("PathfindController - Get out of my way!");
                                                        Logger.debug("PathfindController - Back!");
                                                        webInterfaceComm.setSourceValue(
                                                                        "emotion",
                                                                        "Angry");
                                                        arduinoComm.setSourceValue(
                                                                        "a",
                                                                        "DRight");
                                                    } else if (leftVisited) { // B-B-V-V
                                                        pathfindControl.rotateLeft();
                                                        Logger.debug("PathfindController - Get out of my way!");
                                                        Logger.debug("PathfindController - Wait I visited that area before.");
                                                        Logger.debug("PathfindController - Back!");
                                                        webInterfaceComm.setSourceValue(
                                                                        "emotion",
                                                                        "Sad");
                                                        arduinoComm.setSourceValue(
                                                                        "a",
                                                                        "DLeft");
                                                    } else { // B-B-V-O
                                                        Logger.debug("PathfindController - Get out of my way!");
                                                        Logger.debug("PathfindController - Left!");
                                                        webInterfaceComm.setSourceValue(
                                                                        "emotion",
                                                                        "Angry");
                                                        arduinoComm.setSourceValue(
                                                                        "a",
                                                                        "Left");
                                                    }
                                                } else { // B-B-O
                                                    Logger.debug("PathfindController - Get out of my way!");
                                                    Logger.debug("PathfindController - Back!");
                                                    webInterfaceComm.setSourceValue(
                                                                    "emotion",
                                                                    "Angry");
                                                    arduinoComm.setSourceValue(
                                                                    "a",
                                                                    "DLeft");
                                                }
                                            } else if (rightVisited) { // B-V
                                                pathfindControl.rotateRight();
                                                if (backBlocked) { // B-V-B
                                                    pathfindControl.rotateRight();
                                                    if (leftBlocked) { // B-V-B-B
                                                        pathfindControl.rotateLeft();
                                                        pathfindControl.rotateLeft();
                                                        Logger.debug("PathfindController - Get out of my way!");
                                                        Logger.debug("PathfindController - Wait I visited that area before.");
                                                        Logger.debug("PathfindController - Right!");
                                                        webInterfaceComm.setSourceValue(
                                                                        "emotion",
                                                                        "Sad");
                                                        arduinoComm.setSourceValue(
                                                                        "a",
                                                                        "Right");
                                                    } else if (leftVisited) { // B-V-B-V
                                                        pathfindControl.rotateLeft();
                                                        pathfindControl.rotateLeft();
                                                        Logger.debug("PathfindController - Get out of my way!");
                                                        Logger.debug("PathfindController - Wait I visited that area before.");
                                                        Logger.debug("PathfindController - Right!");
                                                        webInterfaceComm.setSourceValue(
                                                                        "emotion",
                                                                        "Sad");
                                                        arduinoComm.setSourceValue(
                                                                        "a",
                                                                        "Right");
                                                    } else { // B-V-B-O
                                                        Logger.debug("PathfindController - Get out of my way!");
                                                        Logger.debug("PathfindController - Left!");
                                                        webInterfaceComm.setSourceValue(
                                                                        "emotion",
                                                                        "Angry");
                                                        arduinoComm.setSourceValue(
                                                                        "a",
                                                                        "Left");
                                                    }
                                                } else if (backVisited) { // B-V-V
                                                    pathfindControl.rotateRight();
                                                    if (leftBlocked) { // B-V-V-B
                                                        pathfindControl.rotateLeft();
                                                        Logger.debug("PathfindController - Get out of my way!");
                                                        Logger.debug("PathfindController - Wait I visited that area before.");
                                                        Logger.debug("PathfindController - Back!");
                                                        webInterfaceComm.setSourceValue(
                                                                        "emotion",
                                                                        "Sad");
                                                        arduinoComm.setSourceValue(
                                                                        "a",
                                                                        "DRight");
                                                    } else if (leftVisited) { // B-V-V-V
                                                        pathfindControl.rotateLeft();
                                                        pathfindControl.rotateLeft();
                                                        Logger.debug("PathfindController - Get out of my way!");
                                                        Logger.debug("PathfindController - Wait I visited that area before.");
                                                        Logger.debug("PathfindController - Right!");
                                                        webInterfaceComm.setSourceValue(
                                                                        "emotion",
                                                                        "Sad");
                                                        arduinoComm.setSourceValue(
                                                                        "a",
                                                                        "Right");
                                                    } else { // B-V-V-O
                                                        Logger.debug("PathfindController - Get out of my way!");
                                                        Logger.debug("PathfindController - Left!");
                                                        webInterfaceComm.setSourceValue(
                                                                        "emotion",
                                                                        "Angry");
                                                        arduinoComm.setSourceValue(
                                                                        "a",
                                                                        "Left");
                                                    }
                                                } else { // B-V-O
                                                    Logger.debug("PathfindController - Get out of my way!");
                                                    Logger.debug("PathfindController - Back!");
                                                    webInterfaceComm.setSourceValue(
                                                                    "emotion",
                                                                    "Angry");
                                                    arduinoComm.setSourceValue(
                                                                    "a",
                                                                    "DLeft");
                                                }
                                            } else { // B-O
                                                Logger.debug("PathfindController - Get out of my way!");
                                                Logger.debug("PathfindController - Right!");
                                                webInterfaceComm.setSourceValue(
                                                                "emotion",
                                                                "Angry");
                                                arduinoComm.setSourceValue("a",
                                                                "Right");
                                            }
                                        } else if (frontVisited) { // V
                                            pathfindControl.rotateRight();
                                            if (rightBlocked) { // V-B
                                                pathfindControl.rotateRight();
                                                if (backBlocked) { // V-B-B
                                                    pathfindControl.rotateRight();
                                                    if (leftBlocked) { // V-B-B-B
                                                        pathfindControl.rotateRight();
                                                        Logger.debug("PathfindController - Wait I visited that area before.");
                                                        Logger.debug("PathfindController - Forward!");
                                                        webInterfaceComm.setSourceValue(
                                                                        "emotion",
                                                                        "Sad");
                                                        pathfindControl.goNext();
                                                        arduinoComm.setSourceValue(
                                                                        "a",
                                                                        "Forward");
                                                    } else if (leftVisited) { // V-B-B-V
                                                        pathfindControl.rotateRight();
                                                        Logger.debug("PathfindController - Wait I visited that area before.");
                                                        Logger.debug("PathfindController - Forward!");
                                                        webInterfaceComm.setSourceValue(
                                                                        "emotion",
                                                                        "Sad");
                                                        pathfindControl.goNext();
                                                        arduinoComm.setSourceValue(
                                                                        "a",
                                                                        "Forward");
                                                    } else { // V-B-B-O
                                                        Logger.info("Nothing yet.");
                                                        Logger.debug("PathfindController - Left!");
                                                        webInterfaceComm.setSourceValue(
                                                                        "emotion",
                                                                        "Neutral");
                                                        arduinoComm.setSourceValue(
                                                                        "a",
                                                                        "Left");
                                                    }
                                                } else if (backVisited) { // V-B-V
                                                    pathfindControl.rotateRight();
                                                    if (leftBlocked) { // V-B-V-B
                                                        pathfindControl.rotateRight();
                                                        Logger.debug("PathfindController - Wait I visited that area before.");
                                                        Logger.debug("PathfindController - Forward!");
                                                        webInterfaceComm.setSourceValue(
                                                                        "emotion",
                                                                        "Sad");
                                                        pathfindControl.goNext();
                                                        arduinoComm.setSourceValue(
                                                                        "a",
                                                                        "Forward");
                                                    } else if (leftVisited) { // V-B-V-V
                                                        pathfindControl.rotateRight();
                                                        Logger.debug("PathfindController - Wait I visited that area before.");
                                                        Logger.debug("PathfindController - Forward!");
                                                        webInterfaceComm.setSourceValue(
                                                                        "emotion",
                                                                        "Sad");
                                                        pathfindControl.goNext();
                                                        arduinoComm.setSourceValue(
                                                                        "a",
                                                                        "Forward");
                                                    } else { // V-B-V-O
                                                        Logger.info("Nothing yet.");
                                                        Logger.debug("PathfindController - Left!");
                                                        webInterfaceComm.setSourceValue(
                                                                        "emotion",
                                                                        "Angry");
                                                        arduinoComm.setSourceValue(
                                                                        "a",
                                                                        "Left");
                                                    }
                                                } else { // V-B-O
                                                    Logger.info("Nothing yet.");
                                                    Logger.debug("PathfindController - Back!");
                                                    webInterfaceComm.setSourceValue(
                                                                    "emotion",
                                                                    "Angry");
                                                    arduinoComm.setSourceValue(
                                                                    "a",
                                                                    "DLeft");
                                                }
                                            } else if (rightVisited) { // V-V
                                                pathfindControl.rotateRight();
                                                if (backBlocked) { // V-V-B
                                                    pathfindControl.rotateRight();
                                                    if (leftBlocked) { // V-V-B-B
                                                        pathfindControl.rotateRight();
                                                        Logger.debug("PathfindController - Wait I visited that area before.");
                                                        Logger.debug("PathfindController - Forward!");
                                                        webInterfaceComm.setSourceValue(
                                                                        "emotion",
                                                                        "Sad");
                                                        pathfindControl.goNext();
                                                        arduinoComm.setSourceValue(
                                                                        "a",
                                                                        "Forward");
                                                    } else if (leftVisited) { // V-V-B-V
                                                        pathfindControl.rotateRight();
                                                        Logger.debug("PathfindController - Wait I visited that area before.");
                                                        Logger.debug("PathfindController - Forward!");
                                                        webInterfaceComm.setSourceValue(
                                                                        "emotion",
                                                                        "Sad");
                                                        pathfindControl.goNext();
                                                        arduinoComm.setSourceValue(
                                                                        "a",
                                                                        "Forward");
                                                    } else { // V-V-B-O
                                                        Logger.info("Nothing yet.");
                                                        Logger.debug("PathfindController - Left!");
                                                        webInterfaceComm.setSourceValue(
                                                                        "emotion",
                                                                        "Neutral");
                                                        arduinoComm.setSourceValue(
                                                                        "a",
                                                                        "Left");
                                                    }
                                                } else if (backVisited) { // V-V-V
                                                    pathfindControl.rotateRight();
                                                    if (leftBlocked) { // V-V-V-B
                                                        pathfindControl.rotateRight();
                                                        Logger.debug("PathfindController - Wait I visited that area before.");
                                                        Logger.debug("PathfindController - Forward!");
                                                        webInterfaceComm.setSourceValue(
                                                                        "emotion",
                                                                        "Sad");
                                                        pathfindControl.goNext();
                                                        arduinoComm.setSourceValue(
                                                                        "a",
                                                                        "Forward");
                                                    } else if (leftVisited) { // V-V-V-V
                                                        pathfindControl.rotateRight();
                                                        Logger.debug("PathfindController - Wait I visited that area before.");
                                                        Logger.debug("PathfindController - Forward!");
                                                        webInterfaceComm.setSourceValue(
                                                                        "emotion",
                                                                        "Sad");
                                                        pathfindControl.goNext();
                                                        arduinoComm.setSourceValue(
                                                                        "a",
                                                                        "Forward");
                                                    } else { // V-V-V-O
                                                        Logger.info("Nothing yet.");
                                                        Logger.debug("PathfindController - Left!");
                                                        webInterfaceComm.setSourceValue(
                                                                        "emotion",
                                                                        "Neutral");
                                                        arduinoComm.setSourceValue(
                                                                        "a",
                                                                        "Left");
                                                    }
                                                } else { // V-V-O
                                                    Logger.info("Nothing yet.");
                                                    Logger.debug("PathfindController - Back!");
                                                    webInterfaceComm.setSourceValue(
                                                                    "emotion",
                                                                    "Neutral");
                                                    arduinoComm.setSourceValue(
                                                                    "a",
                                                                    "DLeft");
                                                }
                                            } else { // V-O
                                                Logger.info("Nothing yet.");
                                                Logger.debug("PathfindController - Right!");
                                                webInterfaceComm.setSourceValue(
                                                                "emotion",
                                                                "Neutral");
                                                arduinoComm.setSourceValue("a",
                                                                "Right");
                                            }
                                        } else { // O
                                            Logger.info("Nothing yet.");
                                            Logger.debug("PathfindController - Forward!");
                                            webInterfaceComm.setSourceValue(
                                                            "emotion",
                                                            "Neutral");
                                            pathfindControl.goNext();
                                            arduinoComm.setSourceValue("a",
                                                            "Forward");
                                        }
                                    }
                                } else {
                                    arduinoComm.setSourceValue("a", "Stop");
                                }
                            } else {
                                webInterfaceComm.setSourceValue("emotion",
                                                "Neutral");
                                arduinoComm.setSourceValue("a", "Stop");
                            }
                        } else {
                            Logger.warn("WebInterface - [trackclass: "
                                            + currentTrackClass
                                            + "] is invalid, setting to [trackclass: None].");
                            webInterfaceComm.setRobotValue("trackclass",
                                            "None");
                        }
                    } else if (currentMode.equals("Idle")) {
                        webInterfaceComm.setRobotValue("trackclass", "None");
                        webInterfaceComm.setSourceValue("emotion", "Idle");
                    } else if (currentMode.equals("Off")) {
                        break;
                    } else if (currentMode.equals("Manual")) {
                        webInterfaceComm.setRobotValue("trackclass", "None");
                        webInterfaceComm.setSourceValue("emotion", "Neutral");
                        
                        // Manual move
                        final String move = webInterfaceComm.getRobotValue(
                                        "move");
                        if (move != null) {
                            if (!move.equals(currentMove)) {
                                currentMove = move;
                                Logger.debug("WebInterface - [move: "
                                                + currentMove + "] received.");
                                
                                if (currentUltrasonicSensor <= SENSOR_LIMIT) {
                                    if (currentUltrasonicSensor <= SENSOR_LIMIT
                                                    / 2) {
                                        webInterfaceComm.setSourceValue(
                                                        "emotion", "Sad");
                                    } else {
                                        webInterfaceComm.setSourceValue(
                                                        "emotion", "Angry");
                                    }
                                    webInterfaceComm.setRobotValue("move",
                                                    "Stop");
                                    if (!move.equals("Stop")) {
                                        if (move.equals("Forward")) {
                                            Logger.warn("Arduino - Safety cut due to imminent collision.");
                                            arduinoComm.setSourceValue("a",
                                                            "Stop");
                                        } else {
                                            arduinoComm.setSourceValue("a",
                                                            currentMove);
                                        }
                                    }
                                } else {
                                    webInterfaceComm.setSourceValue("emotion",
                                                    "Happy");
                                    webInterfaceComm.setRobotValue("move",
                                                    "Stop");
                                    arduinoComm.setSourceValue("a",
                                                    currentMove);
                                }
                            }
                        } else {
                            Logger.warn("WebInterface - [move: " + currentMove
                                            + "] is invalid, setting to [move: Stop].");
                            webInterfaceComm.setRobotValue("move", "Stop");
                        }
                    } else {
                        Logger.warn("WebInterface - [mode: " + currentMode
                                        + "] is invalid, setting to [mode: Idle].");
                        webInterfaceComm.setRobotValue("mode", "Idle");
                    }
                } else {
                    Logger.warn("WebInterface - [mode: " + currentMode
                                    + "] is invalid, setting to [mode: Idle].");
                    webInterfaceComm.setRobotValue("mode", "Idle");
                }
                Logger.debug("Pushing.");
                webInterfaceComm.pushSource();
                webInterfaceComm.pushRobot();
                arduinoComm.pushSource();
                Logger.debug("Finished pushing.");
            } catch (CommunicationException e) {
                Logger.error(e, "There was an issue with Communication!");
            } catch (Exception e) {
                Logger.error(e, "There was an unknown issue!");
            } finally {
                delayThread(1000);
            }
        }
    }
    
    /**
     * Sets the application up.
     *
     * @param args
     *            the arguments
     * @throws UnsatisfiedLinkError
     *             if a native library failed to load or if the system
     *             architecture is not valid for Fetch Bot.
     */
    private static void setup(String[] args) {
        CommandLineArgument cLArg = CommandLineArgument.DEFAULT_FLAG;
        
        if (args.length > 0) {
            try {
                cLArg = CommandLineArgument.valueOf(args[0]);
            } catch (IllegalArgumentException e) {
                Logger.close();
                Logger.assign();
                Logger.fatalError(e, "Bad command line, try using HElP.");
                Logger.info("Core - Fetch Bot terminating prematurely! Log file: "
                                + Logger.LOG_FILE + ".");
                Logger.close();
                System.exit(1);
            } catch (Exception e) {
                throw e;
            } finally {
                /* */ }
        }
        
        if (cLArg == CommandLineArgument.DEBUG) {
            Logger.setToDebugMode();
        } else if (cLArg == CommandLineArgument.HELP) {
            String help = "\n\nFetch Bot " + VERSION + "\n" + Logger.WEBSITE
                            + "\nUsage: java -jar fetch-bot-" + VERSION
                            + ".jar [commands]\n where commands include:\n";
            for (CommandLineArgument arg : CommandLineArgument.values()) {
                help += "\t" + arg + "\t\t" + arg.getDescription() + "\n";
            }
            Logger.close();
            Logger.assign();
            Logger.info(help + "\n\n");
            Logger.close();
            System.exit(0);
        } else if (cLArg == CommandLineArgument.V) {
            Logger.close();
            Logger.assign();
            Logger.info("\n\n" + VERSION + "\n\n");
            Logger.close();
            System.exit(0);
        }
        
        // Initiate communications
        arduinoComm = new ArduinoCommunicationImpl(
                        References.ARDUINO_SERIAL_PORT);
        socketImageStreamComm = new SocketImageStreamCommunicationImpl(
                        References.HUD_STREAM_HOST, References.HUD_STREAM_PORT);
        webInterfaceComm = new WebInterfaceCommunicationImpl(
                        References.WEB_INTERFACE_PATH);
        Logger.close();
        Logger.assign();
        Logger.clear();
        Logger.setWebInterfaceCommunications(webInterfaceComm);
        
        // Reset communications
        try {
            arduinoComm.open();
            arduinoComm.reset();
            arduinoComm.pushSource();
            Logger.debug("Connected to Arduino serial on "
                            + References.ARDUINO_SERIAL_PORT + ".");
            socketImageStreamComm.open();
            Logger.debug("Connected to HUD socket on "
                            + References.HUD_STREAM_HOST + ":"
                            + References.HUD_STREAM_PORT + ".");
            webInterfaceComm.reset();
            webInterfaceComm.pushSource();
            webInterfaceComm.pushRobot();
            Logger.debug("Connected to Web Interface on "
                            + References.WEB_INTERFACE_PATH + ".");
            webInterfaceComm.pushSource();
        } catch (CommunicationException e) {
            Logger.error(e, "There was an issue with Communication!");
        } catch (Exception e) {
            Logger.error(e, "There was an unknown issue!");
        } finally {
            /* */ }
            
        Logger.info("Core - Fetch Bot " + VERSION + " started as profile "
                        + cLArg.toString() + "!");
        try {
            webInterfaceComm.pushSource();
        } catch (CommunicationException e) {
            Logger.error(e, "There was an issue with Communication!");
        } catch (Exception e) {
            Logger.error(e, "There was an unknown issue!");
        } finally {
            /* */ }
            
        // Version check
        boolean versionOk = false;
        try {
            versionOk = VersionCheck.verify(VERSION);
            if (!versionOk && !References.CUSTOM_BUILD) {
                Logger.warn("VersionCheck - Version mismatch [this: " + VERSION
                                + "; current: " + VersionCheck
                                                .getCurrentVersion()
                                + "], this version might be outdated!");
            }
        } catch (VersionCheckException e) {
            Logger.error(e, "There was an issue with VersionCheck!");
        } catch (Exception e) {
            Logger.error(e, "There was an unknown issue!");
        } finally {
            /* */ }
            
        // Initiate controllers
        hudControl = new HUDControllerImpl();
        hudControl.openHud();
        Logger.debug("Opened HUD.");
        try {
            openCVControl = new OpenCVControllerImpl();
            openCVControl.startCamera();
            Logger.debug("Started Camera.");
        } catch (ControllerException e) {
            Logger.error(e, "There was an issue with Controller!");
        } catch (Exception e) {
            Logger.error(e, "There was an unknown issue!");
        } finally {
            /* */ }
            
        pathfindControl = new PathfindControllerImpl();
        Logger.debug("Pathfinding online.");
        hudSocketRunnable = new HUDSocketThread();
        hudSocketThread = new Thread(hudSocketRunnable);
        hudSocketThread.start();
        Logger.debug("Started HUD Socket Thread.");
    }
    
    /**
     * Terminates the application.
     */
    private static void terminate() {
        try {
            hudSocketRunnable.terminate();
            hudSocketThread.join();
            Logger.debug("Stopped HUD Socket Thread.");
            hudControl.closeHud();
            Logger.debug("Closed HUD.");
            openCVControl.stopCamera();
            Logger.debug("Stopped Camera.");
            arduinoComm.setSourceValue("a", "Stop");
            arduinoComm.pushSource();
            arduinoComm.clear();
            arduinoComm.close();
            Logger.debug("Disconnected Arduino Serial Communication.");
            socketImageStreamComm.close();
            Logger.debug("Closed Socket Image Stream.");
            webInterfaceComm.clear();
            webInterfaceComm.pushSource();
            webInterfaceComm.pushRobot();
            Logger.debug("Disconnected Web Interface Communication.");
            Logger.info("Core - Fetch Bot terminating! Log file: "
                            + Logger.LOG_FILE + ".");
        } catch (Exception e) {
            Logger.error(e, "There was an unknown issue!");
        } finally {
            Logger.closePrompt();
        }
        System.exit(0);
    }
    
    // Public Static Final Methods
    
    /**
     * Delays a thread.
     *
     * @param millis
     *            the milliseconds
     */
    public static final void delayThread(long millis) {
        try {
            Thread.sleep(millis);
        } catch (@SuppressWarnings("unused") InterruptedException e) {
            /* */ } // Suppressed
        catch (Exception e) {
            Logger.error(e, "There was an unknown issue!");
        } finally {
            /* */ }
    }
    
    // Public Static Methods
    // Entry Point
    
    /**
     * The main method.
     *
     * @param args
     *            the arguments
     * @throws UnsatisfiedLinkError
     *             if a native library failed to load or if the system
     *             architecture is not valid for Fetch Bot.
     */
    public static void main(String[] args) {
        try {
            // Setup
            setup(args);
        } catch (UnsatisfiedLinkError e) {
            Logger.close();
            Logger.assign();
            Logger.fatalError(e, "Native library failed to load!");
            Logger.info("Core - Fetch Bot terminating prematurely! Log file: "
                            + Logger.LOG_FILE + ".");
            Logger.close();
            System.exit(1);
        } catch (Exception e) {
            Logger.close();
            Logger.assign();
            Logger.fatalError(e, "Unknown issue.");
            Logger.info("Core - Fetch Bot terminating prematurely! Log file: "
                            + Logger.LOG_FILE + ".");
            Logger.close();
            System.exit(1);
        } finally {
            /* */ }
            
        // Run
        run();
        
        // Termination
        terminate();
    }
}
