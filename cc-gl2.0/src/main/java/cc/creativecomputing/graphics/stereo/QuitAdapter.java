package cc.creativecomputing.graphics.stereo;

import com.jogamp.newt.event.*;

public class QuitAdapter extends WindowAdapter implements WindowListener, KeyListener {
    boolean shouldQuit = false;
    boolean enabled = true;

    public void enable(final boolean v) { enabled = v; }

    public void clear() { shouldQuit = false; }

    public boolean shouldQuit() { return shouldQuit; }
    public void doQuit() { shouldQuit=true; }

    public void windowDestroyNotify(final WindowEvent e) {
        if( enabled ) {
            System.err.println("QUIT Window "+Thread.currentThread());
            shouldQuit = true;
        }
    }

    public void keyReleased(final KeyEvent e) {
        if( !e.isPrintableKey() || e.isAutoRepeat() ) {
            return;
        }
        if( enabled ) {
            if(e.getKeyChar()=='q') {
                System.err.println("QUIT Key "+Thread.currentThread());
                shouldQuit = true;
            }
        }
    }
    public void keyPressed(final KeyEvent e) {}
}
