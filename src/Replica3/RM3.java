package Replica3;

import Replica3.Message;
import Replica3.DMTBSApp.DMTBS;
import Replica3.DMTBSApp.DMTBSHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.io.IOException;
import java.net.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;



public class RM3 {
    public static int lastSequenceID = 1;
    public static ConcurrentHashMap<Integer, Message> message_list = new ConcurrentHashMap<>();
    public static Queue<Message> message_q = new ConcurrentLinkedQueue<Message>();
    private static boolean serversFlag = true;

    public static void main(String[] args) throws Exception {
        NamingContextExt ncRef = null;
        try {
            ORB orb = ORB.init(args, null);
            // -ORBInitialPort 1050 -ORBInitialHost localhost
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            ncRef = NamingContextExtHelper.narrow(objRef);
            Run(ncRef);
            //init(ncRef);
        } catch (Exception e) {
            System.out.println("Client ORB init exception: " + e);
            e.printStackTrace();
        }

    }

    private static void Run(NamingContextExt ncRef) throws Exception {
        Runnable task = () -> {
            try {
                receive(ncRef);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    private static void receive(NamingContextExt ncRef) throws Exception {
        MulticastSocket socket = null;
        try {

            socket = new MulticastSocket(1234);

            socket.joinGroup(InetAddress.getByName("230.1.1.10"));

            byte[] buffer = new byte[1000];
            System.out.println("RM3 UDP Server Started(port=1234)............");

            //Run thread for executing all messages in queue
            Runnable task = () -> {
                try {
                    executeAllRequests(ncRef);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            Thread thread = new Thread(task);
            thread.start();

            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);

                String data = new String(request.getData(), 0, request.getLength());
                String[] parts = data.split(";");

                /*
                Message Types:
                    00- Simple message
                    01- Sync request between the RMs
                    02- Initialing RM
                    11-Rm1 has bug
                    12-Rm2 has bug
                    13-Rm3 has bug
                    21-Rm1 is down
                    22-Rm2 is down
                    23-Rm3 is down
                */
                System.out.println("RM3 recieved message. Detail:" + data);
                if (parts[2].equalsIgnoreCase("00")) {
                    Message message = message_obj_create(data);
                    Message message_To_RMs = message_obj_create(data);
                    message_To_RMs.MessageType = "01";
                    send_multicast_toRM(message_To_RMs);
                    if (message.sequenceId - lastSequenceID > 1) {
                        Message initial_message = new Message(0, "Null", "02", Integer.toString(lastSequenceID), Integer.toString(message.sequenceId), "RM3", "Null", "Null", 0, 0);
                        System.out.println("RM3 send request to update its message list. from:" + lastSequenceID + "To:" + message.sequenceId);
                        // Request all RMs to send back list of messages
                        send_multicast_toRM(initial_message);
                    }
                    System.out.println("is adding queue:" + message);
                    message_q.add(message);
                    message_list.put(message.sequenceId, message);
                } else if (parts[2].equalsIgnoreCase("01")) {
                    Message message = message_obj_create(data);
                    if (!message_list.contains(message.sequenceId))
                        message_list.put(message.sequenceId, message);
                } else if (parts[2].equalsIgnoreCase("02")) {
                    initial_send_list(Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), parts[5]);
                } else if (parts[2].equalsIgnoreCase("03") && parts[5].equalsIgnoreCase("RM3")) {
                    update_message_list(parts[1]);
                } else if (parts[2].equalsIgnoreCase("11")) {
                    Message message = message_obj_create(data);
                    System.out.println("RM3 has bug:" + message.toString());
                } else if (parts[2].equalsIgnoreCase("12")) {
                    Message message = message_obj_create(data);
                    System.out.println("RM3 has bug:" + message.toString());
                } else if (parts[2].equalsIgnoreCase("13")) {
                    Message message = message_obj_create(data);
                    System.out.println("RM3 has bug:" + message.toString());
                } else if (parts[2].equalsIgnoreCase("22")) {
                    Runnable crash_task = () -> {
                        try {
                            
                            serversFlag = false;
                            DMTBS atwater_obj = DMTBSHelper.narrow(ncRef.resolve_str("atwater"));
                            atwater_obj.shutdown();
                            System.out.println("RM3 shutdown Atwater Server");

                         
                            DMTBS verdun_obj = DMTBSHelper.narrow(ncRef.resolve_str("verdun"));
                            verdun_obj.shutdown();
                            System.out.println("RM3 shutdown Verdun Server");

                            DMTBS outremont_obj = DMTBSHelper.narrow(ncRef.resolve_str("outremont"));
                            outremont_obj.shutdown();

                            System.out.println("RM3 shutdown Outremont Server");

                         
                            AtwaterServer.main(new String[0]);
                            Thread.sleep(500);
                            VerdunServer.main(new String[0]);
                            Thread.sleep(500);
                            OutremontServer.main(new String[0]);
                          
                            Thread.sleep(5000);

                            System.out.println("RM3 is reloading servers hashmap");
                            reloadServers(ncRef);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    };
                    Thread handleThread = new Thread(crash_task);
                    handleThread.start();
                    System.out.println("RM3 handled the crash!");
                    serversFlag = true;
                }
            }

        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (socket != null)
                socket.close();
        }
    }

    private static Message message_obj_create(String data) {
        String[] parts = data.split(";");
        int sequenceId = Integer.parseInt(parts[0]);
        String FrontIpAddress = parts[1];
        String MessageType = parts[2];
        String Function = parts[3];
        String userID = parts[4];
        String newMovieID = parts[5];
        String newMovieName = parts[6];
        String oldMovieID = parts[7];
         int numberOfTickets = Integer.parseInt(parts[8]);
        int bookingCapacity = Integer.parseInt(parts[9]);
        Message message = new Message(sequenceId, FrontIpAddress, MessageType, Function, userID, newMovieID, newMovieName, oldMovieID, numberOfTickets, bookingCapacity);
        return message;
    }

    // Create a list of messsages, seperating them with @ and send it back to RM
    private static void initial_send_list(Integer begin, Integer end, String RmNumber) {
        String list = "";
        for (ConcurrentHashMap.Entry<Integer, Message> entry : message_list.entrySet()) {
            if (entry.getValue().sequenceId > begin && entry.getValue().sequenceId < end) {
                list += entry.getValue().toString() + "@";
            }
        }
        // Remove the last @ character
        if (list.length() > 2)
            list.substring(list.length() - 1);
        Message message = new Message(0, list, "03", begin.toString(), end.toString(), RmNumber, "Null", "Null", 0, 0);
        System.out.println("RM3 sending its list of messages for initialization. list of messages:" + list);
        send_multicast_toRM(message);
    }

    //update the hasmap and and new data to queue to be execited
    private static void update_message_list(String data) {
        String[] parts = data.split("@");
        for (int i = 0; i < parts.length; ++i) {
            Message message = message_obj_create(parts[i]);
            //we get the list from 2 other RMs and will ensure that there will be no duplication
            if (!message_list.containsKey(message.sequenceId)) {
                System.out.println("RM3 update its message list" + message);
                message_q.add(message);
                message_list.put(message.sequenceId, message);
            }
        }
    }

    private static void send_multicast_toRM(Message message) {
        int port = 1234;
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            byte[] data = message.toString().getBytes();
            InetAddress aHost = InetAddress.getByName("230.1.1.10");

            DatagramPacket request = new DatagramPacket(data, data.length, aHost, port);
            socket.send(request);
            System.out.println("Message multicasted from RM3 to other RMs. Detail:" + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Execute all request from the lastSequenceID, send the response back to Front and update the counter(lastSequenceID)
    private static void executeAllRequests(NamingContextExt ncRef) throws Exception {
        System.out.println("before while true");
        while (true) {
            synchronized (RM3.class) {
                Iterator<Message> itr = message_q.iterator();
                while (itr.hasNext()) {
                    Message data = itr.next();
                    System.out.println("RM3 is executing message request. Detail:" + data);
                    //when the servers are down serversFlag is False therefore, no execution untill all servers are up.
                    if (data.sequenceId == lastSequenceID && serversFlag) {
                        System.out.println("RM3 is executing message request. Detail:" + data);
                        String response = requestToServers(data,ncRef);
                        Message message = new Message(data.sequenceId, response, "RM3",
                                data.Function, data.userID, data.newMovieID,
                                data.newMovieName, data.oldMovieID,
                                data.numberOfTickets, data.bookingCapacity);
                        lastSequenceID += 1;
                        messsageToFront(message.toString(), data.FrontIpAddress);
                        message_q.poll();
                    }
                }
//                message_q.clear();
            }
        }
    }

    //Send Corba request to server
    private static String requestToServers(Message input,NamingContextExt ncRef) throws Exception {
        String serverID = getServerID(input.userID.substring(0, 3));
        DMTBS obj = DMTBSHelper.narrow(ncRef.resolve_str(serverID));

        if (input.userID.substring(3, 4).equalsIgnoreCase("A")) {
            if (input.Function.equalsIgnoreCase("addMovieSlot")) {
                String[] capstringlist = new String[1];
                capstringlist[0]=String.valueOf(input.bookingCapacity);
                String response = obj.addMovieSlots(input.newMovieID, input.newMovieName,capstringlist);
                System.out.println(response);
                return response;
            } else if (input.Function.equalsIgnoreCase("removeMovieSlot")) {
                String response = obj.removeMovieSlots(input.newMovieID, input.newMovieName);
                System.out.println(response);
                return response;
            } else if (input.Function.equalsIgnoreCase("listMovieShowAvailability")) {
                String[] response = obj.listMovieShowsAvailability(input.newMovieName);
                System.out.println(response);
                return (response).toString();
            }
        } else if (input.userID.substring(3, 4).equalsIgnoreCase("C")) {
            if (input.Function.equalsIgnoreCase("bookMovieTickets")) {
                String response = obj.bookMovieTickets(input.userID, input.newMovieID, input.newMovieName,input.numberOfTickets);
                System.out.println(response);
                return response;
            } else if (input.Function.equalsIgnoreCase("getBookingSchedule")) {
                String[] response = obj.getBookingSchedule(input.userID);
                System.out.println(response);
                return (response).toString();
            } else if (input.Function.equalsIgnoreCase("cancelMovieTickets")) {
                String response = obj.cancelMovieTickets(input.userID, input.newMovieID, input.newMovieName, input.numberOfTickets);
                System.out.println(response);
                return response;
            } else if (input.Function.equalsIgnoreCase("exchangeTickets")) {
                String response = obj.exchangeTickets(input.userID, input.newMovieID, input.newMovieName, input.oldMovieID, input.numberOfTickets);
                System.out.println(response);
                return response;
            }
        }
        return "Null response from server" + input.userID.substring(0, 3);
    }

    public static String getServerID(String userID) {
        String branchAcronym = userID.substring(0, 3).toUpperCase();
        if (branchAcronym.equalsIgnoreCase("ATW")) {
            return "atwater";
        } else if (branchAcronym.equalsIgnoreCase("VER")) {
            return "verdun";
        } else if (branchAcronym.equalsIgnoreCase("OUT")) {
            return "outremont";
        }
        return "1";
    }

    public static void messsageToFront(String message, String FrontIpAddress) {
        System.out.println("Message to front:" + message);
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(4323);
            byte[] bytes = message.getBytes();
            InetAddress aHost = InetAddress.getByName(FrontIpAddress);

            System.out.println(aHost);
            DatagramPacket request = new DatagramPacket(bytes, bytes.length, aHost, 1999);
            socket.send(request);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }

    }

    public static void reloadServers(NamingContextExt ncRef) throws Exception {
        for (ConcurrentHashMap.Entry<Integer, Message> entry : message_list.entrySet()) {
            if (entry.getValue().sequenceId < lastSequenceID)
                requestToServers(entry.getValue(),ncRef);
        }
    }
}
