package jp.mouple.gui;

import java.util.ArrayList;
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


public class GlobalKeyObserver implements NativeKeyListener {
	
	public enum Key {
		CMD,
		CTRL,
		SHIFT
	}

	private static HashMap<Key, String> m_key_names = new HashMap<Key, String>();
	private HashMap<Key, Boolean> m_key_status = new HashMap<Key, Boolean>();
	private HashMap<Key, ArrayList<Integer>> m_key_map = new HashMap<Key, ArrayList<Integer>>();

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
        initializeKeyMap();
	}
	
	private void initializeKeyMap() {
        for (Key k : Key.values()) {
        	m_key_status.put(k, false);
        }
        
        m_key_names.put(Key.CMD, NativeKeyEvent.getKeyText(NativeKeyEvent.VC_META_L));
        m_key_names.put(Key.CTRL, NativeKeyEvent.getKeyText(NativeKeyEvent.VC_CONTROL_L));
        m_key_names.put(Key.SHIFT, NativeKeyEvent.getKeyText(NativeKeyEvent.VC_SHIFT_L));        

		ArrayList<Integer> l;
		l = new ArrayList<Integer>();
		l.add(NativeKeyEvent.VC_META_L);
		l.add(NativeKeyEvent.VC_META_R);
		m_key_map.put(Key.CMD, l);

		l = new ArrayList<Integer>();
		l.add(NativeKeyEvent.VC_CONTROL_L);
		l.add(NativeKeyEvent.VC_CONTROL_R);
		m_key_map.put(Key.CTRL, l);

		l = new ArrayList<Integer>();
		l.add(NativeKeyEvent.VC_SHIFT_L);
		l.add(NativeKeyEvent.VC_SHIFT_R);
		m_key_map.put(Key.SHIFT, l);
	}
	
	public static String getText(Key key) {
		return m_key_names.get(key);
	}
	
    public void nativeKeyPressed(NativeKeyEvent e) {
        System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
        
        for (Key k : Key.values()) {
        	ArrayList<Integer> l = m_key_map.get(k);
        	for (Integer i : l) {
        		if (e.getKeyCode() == i) {
        			m_key_status.put(k, true);
        			break;
        		}
        	}
        }

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