package io.github.kirillf.hashviewer.events;

import android.os.Handler;
import android.os.Message;

import io.github.kirillf.hashviewer.Constants;

/**
 *  Event dispatcher a singleton object.
 *  Dispatches background events to a handler.
 *  Main purpose is to dispatch messaged to UI thread.
 */
public class EventDispatcher {
    private Handler handler;

    private static EventDispatcher instance;

    private EventDispatcher() {
    }

    public static synchronized EventDispatcher getInstance() {
        if (instance == null) {
            instance = new EventDispatcher();
        }
        return instance;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void removeHandler(Handler handler) {
        if (this.handler.equals(handler)) {
            this.handler = null;
        }
    }

    /**
     * Send event to specified handler
     * @param event notification event
     */
    public void notify(Event event) {
        if (handler != null) {
            Message message = handler.obtainMessage(Constants.WHAT, event);
            handler.sendMessage(message);
        }
    }
}
