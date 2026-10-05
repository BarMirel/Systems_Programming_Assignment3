package bgu.spl.net.impl.stomp.Frames;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import bgu.spl.net.srv.ConnectionsImpl;

public abstract class Frame
{
  protected final ConcurrentHashMap<String, String> headers;
  protected final String body;
  protected final ConnectionsImpl<String> connections;
  protected final int connectionId;
  protected final String stompCommand;
  
  Frame(ConcurrentHashMap<String, String> headers, String body, ConnectionsImpl<String> connections, int connectionId, String stompCommand) {
    this.body = body;
    this.headers = headers;
    this.connections = connections;
    this.connectionId = connectionId;
    this.stompCommand = stompCommand;
  }
  
  public String toString() {
    String msg = "";
    msg += stompCommand + "\n";
    for (Map.Entry<String, String> entry : headers.entrySet()) {
      msg = msg + entry.getKey() + ":" + entry.getValue() + "\n";
    }
    msg += "\n" + body;
    return msg;
  }
  
  public abstract void process();
}
