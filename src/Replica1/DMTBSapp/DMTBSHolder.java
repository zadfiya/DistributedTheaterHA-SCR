package Replica1.DMTBSapp;

/**
* Replica1.DMTBSapp/DMTBSHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from DMTBS.idl
* Monday, 10 April, 2023 9:18:49 PM EDT
*/

public final class DMTBSHolder implements org.omg.CORBA.portable.Streamable
{
  public Replica1.DMTBSapp.DMTBS value = null;

  public DMTBSHolder ()
  {
  }

  public DMTBSHolder (Replica1.DMTBSapp.DMTBS initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = Replica1.DMTBSapp.DMTBSHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    Replica1.DMTBSapp.DMTBSHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return Replica1.DMTBSapp.DMTBSHelper.type ();
  }

}