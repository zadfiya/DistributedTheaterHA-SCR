package Frontend.FEInterfaceApp;


/**
* FEInterfaceApp/FEObjectInterfaceHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ./FEInterface.idl
* Sunday, April 9, 2023 7:31:49 o'clock PM EDT
*/

abstract public class FEObjectInterfaceHelper
{
  private static String  _id = "IDL:FEInterfaceApp/FEObjectInterface:1.0";

  public static void insert (org.omg.CORBA.Any a, Frontend.FEInterfaceApp.FEObjectInterface that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static Frontend.FEInterfaceApp.FEObjectInterface extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (Frontend.FEInterfaceApp.FEObjectInterfaceHelper.id (), "FEObjectInterface");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static Frontend.FEInterfaceApp.FEObjectInterface read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_FEObjectInterfaceStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, Frontend.FEInterfaceApp.FEObjectInterface value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static Frontend.FEInterfaceApp.FEObjectInterface narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof Frontend.FEInterfaceApp.FEObjectInterface)
      return (Frontend.FEInterfaceApp.FEObjectInterface)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      Frontend.FEInterfaceApp._FEObjectInterfaceStub stub = new Frontend.FEInterfaceApp._FEObjectInterfaceStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static Frontend.FEInterfaceApp.FEObjectInterface unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof Frontend.FEInterfaceApp.FEObjectInterface)
      return (Frontend.FEInterfaceApp.FEObjectInterface)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      Frontend.FEInterfaceApp._FEObjectInterfaceStub stub = new Frontend.FEInterfaceApp._FEObjectInterfaceStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}
