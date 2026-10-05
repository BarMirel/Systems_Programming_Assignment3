package bgu.spl.net.impl.stomp;
import bgu.spl.net.srv.Server;

public class StompServer {
  public static void main(String[] args) {
    if (args.length != 2) {
      System.out.println("Two arguments needed: port, tpc/reactor");
      System.exit(1);
    }
    if (args[1].equals("tpc")) {
      Server.threadPerClient(Integer.parseInt(args[0]), () -> new StompMessagingProtocolImpl(), StompEncoderDecoder::new).serve();
    }
    else if (args[1].equals("reactor")) {
      Server.reactor(
          Runtime.getRuntime().availableProcessors(), Integer.parseInt(args[0]), () -> new StompMessagingProtocolImpl(), StompEncoderDecoder::new).serve();
    } else {
      System.out.println("Two arguments needed: port, tpc/reactor");
      System.exit(1);
    }
  }
}
