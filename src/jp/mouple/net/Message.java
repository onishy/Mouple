package jp.mouple.net;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	private Type m_type;
    public String[] data;
    
    public enum Type {
    	i, // initial data
        p, // CursorPos
        c, // Click
        r, // Release
        w, // Wheel
        m  // Message
    }
    
    public Message(Type type) {
        m_type = type;
    }
    
    public String toString() {
        String res = m_type.name() + ":";
        if (data.length >= 0) {
            res += data[0];
            for (int i = 1; i < data.length; i++) {
                res += "," + data[i];                
            }
        }
        return res;
    }
    
    public Type getType() {
        return m_type;
    }
}
