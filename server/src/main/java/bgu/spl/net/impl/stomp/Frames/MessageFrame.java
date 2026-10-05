package bgu.spl.net.impl.stomp.Frames;

import bgu.spl.net.srv.ConnectionsImpl;
import java.util.concurrent.ConcurrentHashMap;

public class MessageFrame extends Frame {
  MessageFrame(String body, ConcurrentHashMap<String, String> headers, ConnectionsImpl<String> connections, int connectionId, String stompCommand) {
    super(headers, body, connections, connectionId, stompCommand);
  }

 public void process() {}
}
