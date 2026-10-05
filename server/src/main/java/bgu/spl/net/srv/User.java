package bgu.spl.net.srv;

import java.util.concurrent.ConcurrentHashMap;

public class User {
  private int connectionId;
  private final String username;
  private final String password;  
  private boolean isConnected;
  private ConcurrentHashMap<Integer, String> topics;
  
  public User(int connectionId, String username, String password) {
    this.connectionId = connectionId;
    this.username = username;
    this.password = password;
    this.isConnected = true;
    this.topics = new ConcurrentHashMap<>();
  }
  
  public int getConnectionId() {
    return connectionId;
  }
  
  public String getUsername() {
    return username;
  }
  
  public String getPassword() {
    return password;
  }
  
  public boolean getIsConnected() {
    return isConnected;
  }
  
  public ConcurrentHashMap<Integer, String> getTopics() {
    return topics;
  }
  
  public void setIsConnected(boolean isConnected) {
    this.isConnected = isConnected;
  }
  
  public void setConnectionId(int connectionId) {
    this.connectionId = connectionId;
  }
}
