package Replica2.ServerObjectInterfaceApp2;

/**
* ServerObjectInterfaceApp2/ServerObjectInterface2Holder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ./ServerObjectInterface.idl
* Saturday, April 8, 2023 11:14:24 o'clock PM EDT
*/

public final class ServerObjectInterface2Holder implements org.omg.CORBA.portable.Streamable
{
  public Replica2.ServerObjectInterfaceApp2.ServerObjectInterface2 value = null;

  public ServerObjectInterface2Holder ()
  {
  }

  public ServerObjectInterface2Holder (Replica2.ServerObjectInterfaceApp2.ServerObjectInterface2 initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = Replica2.ServerObjectInterfaceApp2.ServerObjectInterface2Helper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    Replica2.ServerObjectInterfaceApp2.ServerObjectInterface2Helper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return Replica2.ServerObjectInterfaceApp2.ServerObjectInterface2Helper.type ();
  }

}
