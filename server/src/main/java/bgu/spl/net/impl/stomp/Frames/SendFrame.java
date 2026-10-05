package bgu.spl.net.impl.stomp.Frames;
 
import bgu.spl.net.srv.ConnectionsImpl;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;


public class SendFrame extends Frame {
  SendFrame(String body, ConcurrentHashMap<String, String> headers, ConnectionsImpl<String> connections, int connectionId, String stompCommand) {
    super(headers, body, connections, connectionId, stompCommand);
  }

  public void process() {
    boolean send = true;
    try {
      if (!headers.containsKey("destination")) {
        throw new IOException("Send frame does not contain a destination header");
      }
      String topic = (headers.get("destination")).substring(1);
      if (!(connections.getTopics().containsKey(topic) && connections.getHandler(connectionId).getUser().getTopics().contains(topic))){
        throw new IOException("You are not subscribed to the topic or it does not exist");
      }
    } catch (IOException err) {
      send = false;
      BuildFrame.sendErrorFrame(this, "Report failed", err.getMessage(), connections, connectionId, headers.get("receipt"));
    }
    if (send) {
      LinkedList<Integer> connectionIdsOfTopic = connections.getTopics().get((headers.get("destination")).substring(1));
      for (Integer connectionId : connectionIdsOfTopic){
        ConcurrentHashMap<String, String> msgHeaders = new ConcurrentHashMap<>(headers);
        msgHeaders.put("message-id", String.valueOf(connections.getMessageIdCounter().getAndIncrement()));
        ConcurrentHashMap<Integer, String> userTopics = connections.getHandler(connectionId).getUser().getTopics();
        for (ConcurrentHashMap.Entry<Integer, String> entry : userTopics.entrySet()) {
          if ((headers.get("destination")).equals(entry.getValue())) {
            msgHeaders.put("subscription", String.valueOf(entry.getKey()));
          }
        }
        String msg = (new MessageFrame(body, msgHeaders, connections, connectionId,"MESSAGE")).toString();
        connections.send(connectionId, msg);
      }
      if (headers.containsKey("receipt")) {
        BuildFrame.sendReceiptFrame(headers.get("receipt"), connections, connectionId);
      }
    }
  }
}