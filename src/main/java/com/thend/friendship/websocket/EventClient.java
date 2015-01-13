//package com.thend.friendship.websocket;
//
//import java.net.URI;
//import java.util.concurrent.Future;
//
//import org.eclipse.jetty.websocket.api.Session;
//import org.eclipse.jetty.websocket.client.WebSocketClient;
//
//public class EventClient
//{
//    public static void main(String[] args)
//    {
//        URI uri = URI.create("ws://localhost:8080/events/");
//
//        WebSocketClient client = new WebSocketClient();
//        try
//        {
//            try
//            {
//                client.start();
//                // The socket that receives events
//                EventSocket socket = new EventSocket();
//                // Attempt Connect
//                Future<Session> fut = client.connect(socket,uri);
//                // Wait for Connect
//                Session session = fut.get();
//                // Send a message
//                for(int i=0;i<10;i++) {
//                	session.getRemote().sendString("Hello");
//                	Thread.sleep(1000);
//                }
//                // Close session
//                session.close();
//            }
//            finally
//            {
//                client.stop();
//            }
//        }
//        catch (Throwable t)
//        {
//            t.printStackTrace(System.err);
//        }
//    }
//}
