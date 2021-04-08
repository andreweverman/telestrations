package com.example.telestrations.utils;

import java.io.Serializable;

public interface ValueCallback<T> extends Serializable {
    void onReceiveValue(T o);
}
