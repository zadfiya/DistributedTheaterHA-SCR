package DMTBSApp.DMTBSPackage;


/**
* DMTBSApp/DMTBSPackage/listHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from DMTBSApp.idl
* Monday, April 10, 2023 9:29:09 o'clock PM EDT
*/

public final class listHolder implements org.omg.CORBA.portable.Streamable
{
  public String value[] = null;

  public listHolder ()
  {
  }

  public listHolder (String[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = DMTBSApp.DMTBSPackage.listHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    DMTBSApp.DMTBSPackage.listHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return DMTBSApp.DMTBSPackage.listHelper.type ();
  }

}
