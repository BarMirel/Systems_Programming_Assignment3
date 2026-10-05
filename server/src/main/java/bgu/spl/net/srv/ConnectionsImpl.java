package bgu.spl.net.srv;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionsImpl<T> implements Connections<T> {
  private ConcurrentHashMap<String, User> users;
  private ConcurrentHashMap<String, LinkedList<Integer>> topics;
  private ConcurrentHashMap<Integer, ConnectionHandler<T>> connectionHandlers;
  private AtomicInteger msgIdCounter = new AtomicInteger();
  
  public ConnectionsImpl() {
    connectionHandlers = new ConcurrentHashMap<>();
    users = new ConcurrentHashMap<>();
    topics = new ConcurrentHashMap<>();
  }
  
  public boolean send(int connectionId, T msg) {
    connectionHandlers.get(Integer.valueOf(connectionId)).send(msg);
    return true;
  }
  
  public void subscribe(String topic, int subId, int connectionId) {
    ConcurrentHashMap<Integer, String> userTopics = connectionHandlers.get(Integer.valueOf(connectionId)).getUser().getTopics();
    userTopics.put(Integer.valueOf(subId), topic);
    if (!topics.containsKey(topic))
      topics.put(topic, new LinkedList<>());
    topics.get(topic).push(Integer.valueOf(connectionId));
  }
  
  public void unsubscribe(int subId, int connectionId) {
    ConcurrentHashMap<Integer, String> userTopics = connectionHandlers.get(Integer.valueOf(connectionId)).getUser().getTopics();
    String topic = userTopics.get(Integer.valueOf(subId));
    if (topics.size() != 0){
      topics.get(topic).remove(Integer.valueOf(connectionId));
      userTopics.remove(Integer.valueOf(subId));
    }
  }
  
  public void disconnect(int connectionId) {
    logout(connectionId);
    connectionHandlers.remove(Integer.valueOf(connectionId));
  }
  
  public void login(int connectionId, String userName, String password) {
    User user;
    ConnectionHandler<T> newHandler = connectionHandlers.get(Integer.valueOf(connectionId));
    if (!users.containsKey(userName)) {
      user = new User(connectionId, userName, password);
      users.put(userName, user);
    } else {
      user = users.get(userName);
      user.setIsConnected(true);
      user.setConnectionId(connectionId);
    }
    newHandler.setUser(user);
  }
  
  public void logout(int connectionId) {
    User user = connectionHandlers.get(Integer.valueOf(connectionId)).getUser();
    if (user != null) {
        for (String topic : user.getTopics().values())
          topics.get(topic).remove(user.getConnectionId()); 
      user.getTopics().clear();
      user.setIsConnected(false);
      user.setConnectionId(-1);
    } 
  }
  
  public void addConnectionHandler(ConnectionHandler<T> handler, int connectionId) {
    connectionHandlers.put(Integer.valueOf(connectionId), handler);
  }
  
  public ConnectionHandler<T> getHandler(int connectionId) {
    return connectionHandlers.get(Integer.valueOf(connectionId));
  }

  public ConcurrentHashMap<String, LinkedList<Integer>> getTopics(){
    return topics;
  }
  
  public AtomicInteger getMessageIdCounter() {
    return msgIdCounter;
  }

  public ConcurrentHashMap<String, User> getUsers(){
    return users;
  }
}