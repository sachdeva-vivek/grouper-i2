/*
 * @author mchyzer
 * $Id: MembershipHooksImpl.java,v 1.8 2008-07-11 05:11:28 mchyzer Exp $
 */
package edu.internet2.middleware.grouper.hooks;

import org.apache.commons.lang.StringUtils;

import edu.internet2.middleware.grouper.SubjectTestHelper;
import edu.internet2.middleware.grouper.hooks.beans.HooksContext;
import edu.internet2.middleware.grouper.hooks.beans.HooksMembershipChangeBean;
import edu.internet2.middleware.grouper.hooks.logic.HookVeto;


/**
 * test implementation of group hooks for test
 */
public class MembershipHooksImpl extends MembershipHooks {

  /** most recent subject id added to group */
  static String mostRecentAddMemberSubjectId;

  /** most recent subject id added to group */
  static String mostRecentAddCommitMemberSubjectId;

  /** keep track of hook count so we know how often it fires */
  static int preAddMemberHookCount = 0;

  /**
   * 
   * @see edu.internet2.middleware.grouper.hooks.MembershipHooks#membershipPreAddMember(edu.internet2.middleware.grouper.hooks.beans.HooksContext, edu.internet2.middleware.grouper.hooks.beans.HooksMembershipChangeBean)
   */
  @Override
  public void membershipPreAddMember(HooksContext hooksContext, 
      HooksMembershipChangeBean preAddMemberBean) {
    String subjectId = preAddMemberBean.getMember().getSubjectId();
    mostRecentAddMemberSubjectId = subjectId;
    preAddMemberHookCount++;
    if (StringUtils.equals(SubjectTestHelper.SUBJ1.getId(), subjectId)) {
      throw new HookVeto("hook.veto.subjectId.not.subj1", "subject cannot be subj1");
    }
  }

  /**
   * 
   * @see edu.internet2.middleware.grouper.hooks.MembershipHooks#membershipPostCommitAddMember(edu.internet2.middleware.grouper.hooks.beans.HooksContext, edu.internet2.middleware.grouper.hooks.beans.HooksMembershipChangeBean)
   */
  @Override
  public void membershipPostCommitAddMember(HooksContext hooksContext, 
      HooksMembershipChangeBean postAddCommitMemberBean) {
    String subjectId = postAddCommitMemberBean.getMember().getSubjectId();
    mostRecentAddCommitMemberSubjectId = subjectId;
  }
}