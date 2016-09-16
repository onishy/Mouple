package jp.mouple.core;

public class Config {
    private static Config m_instance = new Config();
    
    private Display m_disp;
    
    private Config() {
        m_disp = new Display();
    }
    
    public static Config getInstance() {
        return m_instance;
    }
    
    public Display getDisplay() {
        return m_disp;
    }

}
