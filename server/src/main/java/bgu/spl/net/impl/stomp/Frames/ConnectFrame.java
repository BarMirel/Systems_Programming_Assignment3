package bgu.spl.net.impl.stomp.Frames;

import bgu.spl.net.srv.ConnectionsImpl;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectFrame extends Frame {
  ConnectFrame(String body, ConcurrentHashMap<String, String> headers, ConnectionsImpl<String> connections, int connectionId, String stompCommand) {
    super(headers, body, connections, connectionId, stompCommand);
  }
 
  public void process() {
    boolean login = true;
    try {
      if (!headers.containsKey("accept-version")) {
        throw new IOException("Connect frame does not contain a accept-version header");
      }
      if (!(headers.get("accept-version")).equals("1.2")) {
        throw new IOException("accept-version must be 1.2, your version is " + headers.get("accept-version"));
      }
      if (!this.headers.containsKey("host")) {
        throw new IOException("Connect frame does not contain a host header");
      }
      if (!(headers.get("host")).equals("stomp.cs.bgu.ac.il")) {
        throw new IOException("host must be ‘stomp.cs.bgu.ac.il’, your host is " + headers.get("host"));
      }
      if (!headers.containsKey("login")){
        throw new IOException("Connect frame does not contain a login header");
      }
      if (!headers.containsKey("passcode")) {
        throw new IOException("Connect frame does not contain a passcode header");
      }
      String username = headers.get("login");
      String password = headers.get("passcode");
      if ((connections.getUsers().containsKey(username))){
        if(!connections.getUsers().get(username).getPassword().equals(password)){
          throw new IOException("password does not match user " + username + "'s password");
        }
      }  
      if (connections.getUsers().containsKey(username) && connections.getUsers().get(username).getIsConnected()) {
        throw new IOException("user " + username + " is already logged in to the server");
      }
    } catch (IOException err) { 
        login = false;
        BuildFrame.sendErrorFrame(this, "Login failed", err.getMessage(), connections, connectionId, headers.get("receipt"));
    }
    if (login) {
      connections.login(connectionId, headers.get("login"), headers.get("passcode"));
      BuildFrame.sendConnectedFrame(headers.get("accept-version"), connectionId, connections);
      if (headers.containsKey("receipt")) {
        BuildFrame.sendReceiptFrame(headers.get("receipt"), connections, connectionId);
      }
    }
  }
}