package bgu.spl.net.impl.stomp;


import bgu.spl.net.api.MessageEncoderDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


public class StompEncoderDecoder implements MessageEncoderDecoder<String>{
 private byte[] bytes = new byte[1024];
 private int length = 0;

 public String decodeNextByte(byte nextByte) {
   if (nextByte == 0){
    String result = new String(bytes, 0, length, StandardCharsets.UTF_8);
    length = 0;
    return result;
   }
   if (length >= bytes.length) {
    bytes = Arrays.copyOf(bytes, length * 2);
   }
   bytes[length] = nextByte;
   length++;
   return null;
 }

 public byte[] encode(String message) {
    return (message + "\0").getBytes();
 }
}