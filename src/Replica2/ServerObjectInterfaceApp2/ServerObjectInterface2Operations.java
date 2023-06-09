package Replica2.ServerObjectInterfaceApp2;


/**
* ServerObjectInterfaceApp2/ServerObjectInterface2Operations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ./ServerObjectInterface.idl
* Saturday, April 8, 2023 11:14:24 o'clock PM EDT
*/

public interface ServerObjectInterface2Operations 
{

  /**
          * Only for admin
          */
  String addMovieSlot (String movieID, String movieName, int bookingCapacity);
  String removeMovieSlots (String movieID, String movieName);
  String listMovieShowAvailability (String movieName);

  /**
               * Both admin and Customer
               */
  String bookMovieTickets (String customerID, String movieID, String movieName, int numberOfTickets);
  String getBookingSchedule (String customerID);
  String cancelMovieTickets (String customerID, String movieID, String movieName, int numberOfTickets);
  String exchangeTickets (String customerID, String newMovieID, String newMovieName, String oldMovieID, int numberOfTickets);
  void shutdown ();
} // interface ServerObjectInterface2Operations
