package bgu.spl.net.impl.stomp.Frames;


import bgu.spl.net.srv.ConnectionsImpl;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class DisconnectFrame extends Frame {
  DisconnectFrame(String body, ConcurrentHashMap<String, String> headers, ConnectionsImpl<String> connections, int connectionId, String stompCommand) {
    super(headers, body, connections, connectionId, stompCommand);
  }

  public void process() {
    boolean disconnect = true;
    try {
        if (!headers.containsKey("receipt")) {
            throw new IOException("Disconnect frame does not contain a receipt header");
          }
    } catch (IOException err) {
        disconnect = false;
        BuildFrame.sendErrorFrame(this, "Logout failed", err.getMessage(), connections, connectionId, headers.get("receipt"));
    }
    if (disconnect) {
      BuildFrame.sendReceiptFrame(headers.get("receipt"), connections, connectionId);
      connections.disconnect(connectionId);
    }
  }
}