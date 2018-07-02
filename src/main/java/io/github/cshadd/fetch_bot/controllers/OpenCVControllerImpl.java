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
package io.github.cshadd.fetch_bot.controllers;

import io.github.cshadd.fetch_bot.Component;
import javax.swing.ImageIcon;

// Main

/**
 * The Class OpenCVControllerImpl. An OpenCV Controller with basic
 * implementation.
 * 
 * @author Christian Shadd
 * @author Maria Verna Aquino
 * @author Thanh Vu
 * @author Joseph Damian
 * @author Giovanni Orozco
 * @since 1.0.0
 */
@Component("OpenCV")
public class OpenCVControllerImpl extends AbstractOpenCVController {
    // Public Constructors
    
    /**
     * Instantiates a new Open CV Controller Impl.
     *
     * @throws ControllerException
     *             if OpenCV could not load
     */
    public OpenCVControllerImpl() throws OpenCVControllerException {
        super();
    }
    
    public OpenCVControllerImpl(int newCameraPort)
                    throws OpenCVControllerException {
        super(newCameraPort);
    }
    
    // Public Methods (Overrided)
    
    /**
     * @see io.github.cshadd.fetch_bot.controllers.OpenCVController#assignTrackClass(java.lang.String)
     */
    @Override
    public void assignTrackClass(String newTrackClass) {
        this.trackClass = newTrackClass;
    }
    
    /**
     * @see io.github.cshadd.fetch_bot.controllers.OpenCVController#isTrackClassFound()
     */
    @Override
    public boolean isTrackClassFound() {
        return this.trackClassFound;
    }
    
    /**
     * @see io.github.cshadd.fetch_bot.controllers.OpenCVController#startCamera()
     */
    @Override
    public void startCamera() {
        this.cameraThread.start();
    }
    
    @Override
    public ImageIcon takeCameraImageIcon() {
        return this.cameraImageIconBuffer;
    }
    
    @Override
    public String takeStatus() {
        return this.cameraStatusBuffer;
    }
    
    @Override
    public int[] takeTrackBounds() {
        return this.cameraTrackBoundBuffer;
    }
    
    @Override
    public String takeTrackCaptureLabel() {
        return cameraTrackCaptureLabelBuffer;
    }
    
    /**
     * @see io.github.cshadd.fetch_bot.controllers.OpenCVController#stopCamera()
     */
    @Override
    public void stopCamera() throws OpenCVControllerException {
        try {
            this.cameraRunnable.terminate();
            this.cameraThread.join();
        } catch (InterruptedException e) {
            /* */ } // Suppressed
        catch (Exception e) {
            throw new OpenCVControllerException("Unknown issue.", e);
        } finally {
            /* */ }
    }
}