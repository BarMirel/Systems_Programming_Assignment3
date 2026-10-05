package bgu.spl.net.impl.stomp.Frames;

import bgu.spl.net.srv.ConnectionsImpl;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;


public class SubscribeFrame extends Frame {
  SubscribeFrame(String body, ConcurrentHashMap<String, String> headers, ConnectionsImpl<String> connections, int connectionId, String stompCommand) {
    super(headers, body, connections, connectionId, stompCommand);
  }

  public void process() {
    boolean subscribe = true;
    try {
        if (!headers.containsKey("destination")) {
            throw new IOException("Subscribe frame does not contain a destination header");
          }
          if (!headers.containsKey("id")) {
            throw new IOException("Subscribe frame does not contain an id header");
          }
          if (connections.getHandler(connectionId).getUser().getTopics().containsKey(Integer.valueOf(Integer.parseInt(headers.get("id"))))) {
            throw new IOException("You are already subscribed to this topic");
          }
    } catch (IOException err) {
        subscribe = false;
        BuildFrame.sendErrorFrame(this, "Join failed", err.getMessage(), connections, connectionId, headers.get("receipt"));
    }
   
    if (subscribe) {
        connections.subscribe(headers.get("destination").substring(1), Integer.parseInt(headers.get("id")), connectionId);
        if (headers.containsKey("receipt")) {
            BuildFrame.sendReceiptFrame(headers.get("receipt"), connections, connectionId);
      }
    }
  }
}