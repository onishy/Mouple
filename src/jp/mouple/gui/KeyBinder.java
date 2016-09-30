package jp.mouple.gui;

import jp.mouple.net.User;

public abstract interface KeyBinder {
	abstract public void setUser(User u);
	abstract public boolean isActive();
	abstract public void notifyStatus();
	abstract public String getText();
}
