package org.cdm.core.observer;

/**
 * 事件基础接口
 */
public interface Event {
    String getEventType();
    long getTimestamp();
    Object getSource();
}
