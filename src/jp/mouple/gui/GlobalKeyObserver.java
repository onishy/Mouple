package jp.mouple.gui;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import jp.mouple.net.User;


public class GlobalKeyObserver implements NativeKeyListener {
	
	private static HashMap<Integer, Boolean> m_key_status = new HashMap<Integer, Boolean>();

	public GlobalKeyObserver() {
        try {
        	if (!GlobalScreen.isNativeHookRegistered()) {
        		GlobalScreen.registerNativeHook();
        	}
        }
        catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());
        }
        suppressLogger();
	}
	
    public void nativeKeyPressed(NativeKeyEvent e) {
        System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
        m_key_status.put(e.getKeyCode(), true);
        
        User.scanKeyBinds();
        
//        if (e.getKeyCode() == NativeKeyEvent.VC_META_L) {
//            MainWindow.showClickCapturer();
//        }
    }

    public void nativeKeyReleased(NativeKeyEvent e) {
        System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
        m_key_status.put(e.getKeyCode(), false);

        User.scanKeyBinds();

//        if (e.getKeyCode() == NativeKeyEvent.VC_META_L) {
//            MainWindow.hideClickCapturer();
//        }
    }

    public void nativeKeyTyped(NativeKeyEvent e) {
        System.out.println("Key Typed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
    }
    
    public static boolean getKeyStatus(int key) {
    	Boolean status = m_key_status.get(key);
    	if (status != null) {
    		return status;
    	} else {
    		return false;
    	}
    }

	private static void suppressLogger() {
	    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1, r -> {
	        Thread thread = new Thread(r);
	        thread.setDaemon(true);
	        return thread;
	    });
	
	    executorService.schedule(() -> {
	        final Logger jNativeHookLogger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
	        if (jNativeHookLogger.getLevel() != Level.WARNING) {
	            synchronized (jNativeHookLogger) {
	                jNativeHookLogger.setLevel(Level.WARNING);
	            }
	        }
	    }, 2, TimeUnit.SECONDS);
	}
}