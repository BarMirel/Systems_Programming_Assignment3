package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;

public abstract class BaseServer<T> implements Server<T> {
  private final int port;
  private final Supplier<MessagingProtocol<T>> protocolFactory;
  private final Supplier<MessageEncoderDecoder<T>> encdecFactory;
  private ServerSocket sock = null;
  private ConnectionsImpl<T> connections;
  private int connectionIdCounter = 0;

  public BaseServer(int port, Supplier<MessagingProtocol<T>> protocolFactory, Supplier<MessageEncoderDecoder<T>> encdecFactory) {
    this.port = port;
    this.protocolFactory = protocolFactory;
    this.encdecFactory = encdecFactory;
  }

  public void serve() {
    try (ServerSocket serverSock = new ServerSocket(port)) {
      System.out.println("Server started");
      connections = new ConnectionsImpl<>();
      sock = serverSock;
      while (!Thread.currentThread().isInterrupted()) {
        Socket clientSock = serverSock.accept();
        BlockingConnectionHandler<T> handler = new BlockingConnectionHandler<>(clientSock, this.encdecFactory.get(), this.protocolFactory.get());
        handler.getProtocol().startImpl(connectionIdCounter, connections);
        connections.addConnectionHandler(handler, connectionIdCounter);
        connectionIdCounter++;
        execute(handler);
      }
    } catch (IOException iOException) {}
    System.out.println("server closed!!!");
  }

  public void close() throws IOException {
    if (this.sock != null)
      this.sock.close();
    }

  protected abstract void execute(BlockingConnectionHandler<T> paramBlockingConnectionHandler);
}