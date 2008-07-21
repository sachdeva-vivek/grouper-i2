/*
 * Copyright (C) 2006-2007 blair christensen.
 * All Rights Reserved.
 *
 * You may use and distribute under the same terms as Grouper itself.
 */

package com.devclue.grouper.shell;
import  bsh.*;
import  edu.internet2.middleware.grouper.*;
import edu.internet2.middleware.grouper.exception.InsufficientPrivilegeException;
import edu.internet2.middleware.grouper.exception.SchemaException;
import edu.internet2.middleware.grouper.privs.Privilege;

/**
 * Add a list to to a {@link GroupType}.
 * <p/>
 * @author  blair christensen.
 * @version $Id: typeAddList.java,v 1.2 2008-07-21 04:44:17 mchyzer Exp $
 * @since   0.1.0
 */
public class typeAddList {

  // PUBLIC CLASS METHODS //

  /**
   * Add a list to a {@link GroupType}.
   * <p/>
   * @param   i           BeanShell interpreter.
   * @param   stack       BeanShell call stack.
   * @param   type        Add to this {@link GroupType}.
   * @param   name        Name of list.
   * @param   read        {@link Privilege} required for reading.
   * @param   write       {@link Privilege} required for writing.
   * @return  {@link Field}
   * @throws  GrouperShellException
   * @since   0.1.0
   */
  public static Field invoke(
    Interpreter i, CallStack stack, String type, String name, Privilege read, Privilege write
  ) 
    throws  GrouperShellException
  {
    GrouperShell.setOurCommand(i, true);
    try {
      GrouperSession  s = GrouperShell.getSession(i);
      GroupType       t = GroupTypeFinder.find(type);
      return t.addList(s, name, read, write);
    }
    catch (InsufficientPrivilegeException eIP)  {
      GrouperShell.error(i, eIP);
    }
    catch (SchemaException eS)                  {
      GrouperShell.error(i, eS);
    }
    throw new GrouperShellException(E.TYPE_ADDLIST + name);
  } // public static Field invoke(i, stack, type, name, read, write)

} // public class typeAddList

