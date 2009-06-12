
package edu.internet2.middleware.grouper.ui.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import edu.internet2.middleware.grouper.Group;
import edu.internet2.middleware.grouper.GroupFinder;
import edu.internet2.middleware.grouper.GrouperHelper;
import edu.internet2.middleware.grouper.GrouperSession;

/**
 * @author shilen
 * @version $Id: PopulateCopyGroupAction.java,v 1.2 2009-05-08 12:03:37 shilen Exp $
 */
public class PopulateCopyGroupAction extends GrouperCapableAction {

  static final private String FORWARD_CopyGroup = "CopyGroup";

  public ActionForward grouperExecute(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response,
      HttpSession session, GrouperSession grouperSession)
      throws Exception {
    DynaActionForm groupForm = (DynaActionForm) form;

    // Identify and instantiate group to copy
    String curNode = (String)groupForm.get("groupId");
    Group group = GroupFinder.findByUuid(grouperSession, curNode, true);
    
    // this is needed to display the current path in the JSP page.
    request.setAttribute("browseParent", GrouperHelper.group2Map(
        grouperSession, group));
    
    // this is needed so that the JSP page can show the saved stems.
    makeSavedStemsAvailable(request);

    // this is needed to show the subtitle on the page.
    session.setAttribute("subtitle", "groups.action.copy");

    
    return mapping.findForward(FORWARD_CopyGroup);

  }

}