package bgu.spl.net.impl.stomp.Frames;

import bgu.spl.net.srv.ConnectionsImpl;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;


public class UnsubscribeFrame extends Frame {
  UnsubscribeFrame(String body, ConcurrentHashMap<String, String> headers, ConnectionsImpl<String> connections, int connectionId, String stompCommand) {
    super(headers, body, connections, connectionId, stompCommand);
  }

  public void process() {
    boolean unsubscribe = true;
    try {
        if (!headers.containsKey("id")) {
            throw new IOException("Unsubscribe frame does not contain an id header");
        }
        if (!connections.getHandler(connectionId).getUser().getTopics().containsKey(Integer.valueOf(Integer.parseInt(headers.get("id"))))) {
            throw new IOException("Topic is not part of your subscriptions");
        }
    } catch (IOException err) {
        unsubscribe = false;
        BuildFrame.sendErrorFrame(this, "Exit failed", err.getMessage(), connections, connectionId, headers.get("receipt"));
    }
   
    if (unsubscribe) {
        connections.unsubscribe(Integer.parseInt(headers.get("id")), connectionId);
        if (headers.containsKey("receipt")) {
            BuildFrame.sendReceiptFrame(headers.get("receipt"), connections, connectionId);
        }
    }
  }
}