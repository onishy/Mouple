package jp.mouple.gui;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;


public class GlobalKeyObserver implements NativeKeyListener {
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

        if (e.getKeyCode() == NativeKeyEvent.VC_META_L) {
            MainWindow.showClickCapturer();
        }
    }

    public void nativeKeyReleased(NativeKeyEvent e) {
        System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
        if (e.getKeyCode() == NativeKeyEvent.VC_META_L) {
            MainWindow.hideClickCapturer();
        }
    }

    public void nativeKeyTyped(NativeKeyEvent e) {
        System.out.println("Key Typed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
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