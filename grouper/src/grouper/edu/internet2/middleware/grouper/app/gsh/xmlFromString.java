/*
 * Copyright (C) 2006-2007 blair christensen.
 * All Rights Reserved.
 *
 * You may use and distribute under the same terms as Grouper itself.
 */

package edu.internet2.middleware.grouper.app.gsh;
import  bsh.*;
import  edu.internet2.middleware.grouper.*;
import edu.internet2.middleware.grouper.exception.GrouperException;
import edu.internet2.middleware.grouper.xml.XmlImporter;
import edu.internet2.middleware.grouper.xml.XmlReader;

import  java.util.Properties;

/**
 * Import Groups Registry from XML string.
 * <p/>
 * @author  blair christensen.
 * @version $Id: xmlFromString.java,v 1.1 2008-07-21 21:01:59 mchyzer Exp $
 * @since   0.1.0
 */
public class xmlFromString {

  // PUBLIC CLASS METHODS //

  /**
   * Import Groups Registry from XML string.
   * <p/>
   * @param   i           BeanShell interpreter.
   * @param   stack       BeanShell call stack.
   * @param   xml         Import XML from this <tt>String</tt>.
   * @return  True if successful.
   * @throws  GrouperShellException
   * @since   0.1.0
   */
  public static boolean invoke(Interpreter i, CallStack stack, String xml) 
    throws  GrouperShellException
  {
    GrouperShell.setOurCommand(i, true);
    try {
      GrouperSession  s         = GrouperShell.getSession(i);
      XmlImporter     importer  = new XmlImporter(s, new Properties());
      importer.load( XmlReader.getDocumentFromString(xml) );
      return true;
    }
    catch (GrouperException eG) {
      GrouperShell.error(i, eG);
    }
    return false;
  } // public static boolean invoke(i, stack, xml)

} // public class xmlFromString
