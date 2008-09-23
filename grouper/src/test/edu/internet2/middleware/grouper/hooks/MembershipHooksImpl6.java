/*
 * @author mchyzer
 * $Id: MembershipHooksImpl6.java,v 1.4 2008-07-21 04:43:58 mchyzer Exp $
 */
package edu.internet2.middleware.grouper.hooks;

import org.apache.commons.lang.StringUtils;

import edu.internet2.middleware.grouper.SubjectTestHelper;
import edu.internet2.middleware.grouper.exception.MemberNotFoundException;
import edu.internet2.middleware.grouper.hooks.beans.HooksContext;
import edu.internet2.middleware.grouper.hooks.beans.HooksMembershipBean;
import edu.internet2.middleware.grouper.hooks.logic.HookVeto;


/**
 * test implementation of group hooks for test
 */
public class MembershipHooksImpl6 extends MembershipHooks {

  /** most recent subject id added to group */
  static String mostRecentDeleteMemberSubjectId;

  /** most recent subject id added to group */
  static String mostRecentDeleteCommitMemberSubjectId;

  /**
   * 
   * @see edu.internet2.middleware.grouper.hooks.MembershipHooks#membershipPostDelete(edu.internet2.middleware.grouper.hooks.beans.HooksContext, edu.internet2.middleware.grouper.hooks.beans.HooksMembershipBean)
   */
  @Override
  public void membershipPostDelete(HooksContext hooksContext,
      HooksMembershipBean postDeleteBean) {
    try {
      String subjectId = postDeleteBean.getMembership().getMember().getSubjectId();
      mostRecentDeleteMemberSubjectId = subjectId;
      if (StringUtils.equals(SubjectTestHelper.SUBJ1.getId(), subjectId)) {
        throw new HookVeto("hook.veto.subjectId.not.subj1", "subject cannot be subj1");
      }
    } catch (MemberNotFoundException mnfe) {
      throw new RuntimeException(mnfe);
    }
  }

  /**
   * 
   * @see edu.internet2.middleware.grouper.hooks.MembershipHooks#membershipPostCommitDelete(edu.internet2.middleware.grouper.hooks.beans.HooksContext, edu.internet2.middleware.grouper.hooks.beans.HooksMembershipBean)
   */
  @Override
  public void membershipPostCommitDelete(HooksContext hooksContext,
      HooksMembershipBean postDeleteBean) {
    try {
      String subjectId = postDeleteBean.getMembership().getMember().getSubjectId();
      mostRecentDeleteCommitMemberSubjectId = subjectId;
    } catch (MemberNotFoundException mnfe) {
      throw new RuntimeException(mnfe);
    }
  }
}