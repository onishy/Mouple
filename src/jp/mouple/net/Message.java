package jp.mouple.net;

public class Message {
	private Type m_type;
	public String[] data;
	
	public enum Type {
		p, // CursorPos
		c, // Click
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
