package bgu.spl.net.impl.stomp.Frames;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.ConnectionsImpl;

public class BuildFrame {
    public static Frame buildFrame(String command, ConcurrentHashMap<String, String> headers, String body, ConnectionsImpl<String> connections, int connectionId) {
        if (command.equals("SUBSCRIBE")) {
            return new SubscribeFrame(body, headers, connections, connectionId, command);
        } else if (command.equals("UNSUBSCRIBE")) {
            return new UnsubscribeFrame(body, headers, connections, connectionId, command);
        } else if (command.equals("CONNECT")) {
            return new ConnectFrame(body, headers, connections, connectionId, command);
        } else if (command.equals("CONNECTED")) {
            return new ConnectedFrame(body, headers, connections, connectionId, command);
        } else if (command.equals("DISCONNECT")) {
            return new DisconnectFrame(body, headers, connections, connectionId, command);
        } else if (command.equals("SEND")) {
            return new SendFrame(body, headers, connections, connectionId, command);
        }else if (command.equals("MESSAGE")) {
            return new MessageFrame(body, headers, connections, connectionId, command);
        } else if (command.equals("RECEIPT")) {
            return new ReceiptFrame(body, headers, connections, connectionId, command);
        } else if (command.equals("ERROR")) {
            return new ErrorFrame(body, headers, connections, connectionId, command);
        }
        return null;
    }
  
  public static void sendReceiptFrame(String receiptId, ConnectionsImpl<String> connections, int connectionId) {
    ConcurrentHashMap<String, String> headers = new ConcurrentHashMap<>();
    headers.put("receipt-id", receiptId);
    connections.send(connectionId, (new ReceiptFrame("", headers, connections, connectionId, "RECEIPT")).toString());
  }

  public static void sendErrorFrame(Frame frameError, String errorHeader, String errorDetails, ConnectionsImpl<String> connections, int connectionId, String receiptId) {
    ConcurrentHashMap<String, String> headers = new ConcurrentHashMap<>();
    if (receiptId != null) {
        headers.put("receipt-id", receiptId);
    }
    headers.put("message", errorHeader);
    String body = "";
    body += "The message: \n" + "----- \n" + frameError.toString() + "----- \n" + errorDetails;
    connections.send(connectionId, (new ErrorFrame(body, headers, connections, connectionId, "ERROR")).toString());
    ConnectionHandler<String> handler = connections.getHandler(connectionId);
    connections.disconnect(connectionId);
    try {
      handler.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    } 
  }
  
  public static void sendConnectedFrame(String stompVer, int connectionId, ConnectionsImpl<String> connections) {
    ConcurrentHashMap<String, String> headers = new ConcurrentHashMap<>();
    headers.put("version", stompVer);
    connections.send(connectionId, (new ConnectedFrame("", headers, connections, connectionId, "CONNECTED")).toString());
  }
}
