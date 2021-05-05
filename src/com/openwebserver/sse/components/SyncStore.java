package com.openwebserver.sse.components;

public class SyncStore<T> {

    boolean valueSet;
    volatile T value;

    public synchronized T get(){
        try{
            return value;
        }finally {
            valueSet = false;
            notify();
        }
    }

    public synchronized void set(T value){
        this.value = value;
        valueSet = true;
        notify();
    }

}
