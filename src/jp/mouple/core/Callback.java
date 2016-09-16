package jp.mouple.core;

import jp.mouple.net.Message;

public interface Callback {
    public abstract void func(Message data);
};