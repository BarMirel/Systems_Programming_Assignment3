package bgu.spl.net.impl.stomp;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.impl.stomp.Frames.BuildFrame;
import bgu.spl.net.impl.stomp.Frames.Frame;
import bgu.spl.net.srv.Connections;
import bgu.spl.net.srv.ConnectionsImpl;

public class StompMessagingProtocolImpl implements StompMessagingProtocol<String>
{
  private boolean shouldTerminate = false;
  private int connectionId;
  private ConnectionsImpl<String> connections;

  public String process(String msg) {
    LinkedList<String> msgLinesList = new LinkedList<>(Arrays.asList(msg.split("\\n")));
    String command = msgLinesList.remove();
    ConcurrentHashMap<String, String> headers = new ConcurrentHashMap<>();
    while (!msgLinesList.isEmpty() && !msgLinesList.peek().equals("")) {
      String[] headerAndVal = msgLinesList.remove().split(":");
      headers.put(headerAndVal[0], headerAndVal[1]);
    }
    if (!msgLinesList.isEmpty() && msgLinesList.peek().equals("")) {
      msgLinesList.remove();
    }
    String body = ""; 
    while (!msgLinesList.isEmpty()) {
      String line = msgLinesList.remove();
      if (line != "\0") {
        body += line + "\n";
      }
      else break;
    }
    Frame frame = BuildFrame.buildFrame(command, headers, body, connections, connectionId);
    frame.process();
    return null;
  }

  public boolean shouldTerminate() {
    return shouldTerminate;
  }

  public void terminate(){
    shouldTerminate = true;
  }

  public void startImpl(int connectionId, ConnectionsImpl<String> connections) {
    this.connectionId = connectionId;
    this.connections = connections;
  }

  @Override
  public void start(int connectionId, Connections<String> connections) {
    
  }
}