/*
 * Copyright (C) 2006-2007 blair christensen.
 * All Rights Reserved.
 *
 * You may use and distribute under the same terms as Grouper itself.
 */

package edu.internet2.middleware.grouper.app.gsh;
import  bsh.*;
import  edu.internet2.middleware.grouper.*;
import edu.internet2.middleware.grouper.exception.GroupNotFoundException;
import edu.internet2.middleware.grouper.exception.InsufficientPrivilegeException;
import edu.internet2.middleware.grouper.exception.MemberAddException;
import  edu.internet2.middleware.subject.*;

/**
 * Add a member.
 * <p/>
 * @author  blair christensen.
 * @version $Id: addMember.java,v 1.1 2008-07-21 21:01:59 mchyzer Exp $
 * @since   0.0.1
 */
public class addMember {

  // PUBLIC CLASS METHODS //

  /**
   * Add a member.
   * <p/>
   * @param   i           BeanShell interpreter.
   * @param   stack       BeanShell call stack.
   * @param   group       Add {@link Member} to {@link Group} with this name.
   * @param   subjId      Add {@link Subject} with this <i>subject id</i> as a member.
   * @return  True if succeeds.
   * @throws  GrouperShellException
   * @since   0.0.1
   */
  public static boolean invoke(
    Interpreter i, CallStack stack, String group, String subjId
  ) 
    throws  GrouperShellException
  {
    GrouperShell.setOurCommand(i, true);
    try {
      GrouperSession  s     = GrouperShell.getSession(i);
      Group           g     = GroupFinder.findByName(s, group);
      Subject         subj  = SubjectFinder.findById(subjId);
      g.addMember(subj);
      return true;
    }
    catch (GroupNotFoundException eGNF)         {
      GrouperShell.error(i, eGNF);
    }
    catch (InsufficientPrivilegeException eIP)  {
      GrouperShell.error(i, eIP);
    }
    catch (MemberAddException eMA)              {
      GrouperShell.error(i, eMA);
    }
    catch (SubjectNotFoundException eSNF)       {
      GrouperShell.error(i, eSNF); 
    }
    catch (SubjectNotUniqueException eSNU)      {
      GrouperShell.error(i, eSNU); 
    }
    return false;
  } // public static boolean invoke(i, stack, group, subjId)

} // public class addMember
