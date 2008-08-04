/*
  Copyright (C) 2004-2007 University Corporation for Advanced Internet Development, Inc.
  Copyright (C) 2004-2007 The University Of Chicago

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package edu.internet2.middleware.grouper;
import edu.internet2.middleware.grouper.registry.RegistryReset;
import  edu.internet2.middleware.subject.*;
import  junit.framework.*;
import  org.apache.commons.logging.*;

/**
 * @author  blair christensen.
 * @version $Id: TestAddMember0.java,v 1.7 2008-07-21 04:43:57 mchyzer Exp $
 */
public class TestAddMember0 extends TestCase {

  // Private Static Class Constants
  private static final Log LOG = LogFactory.getLog(TestAddMember0.class);

  public TestAddMember0(String name) {
    super(name);
  }

  protected void setUp () {
    LOG.debug("setUp");
    RegistryReset.internal_resetRegistryAndAddTestSubjects();
  }

  protected void tearDown () {
    LOG.debug("tearDown");
  }

  public void testAddMember() {
    LOG.info("testAddMember");
    GrouperSession  s     = SessionHelper.getRootSession();
    Stem            root  = StemHelper.findRootStem(s);
    Stem            edu   = StemHelper.addChildStem(root, "edu", "education");
    Group           i2    = StemHelper.addChildGroup(edu, "i2", "internet2");
    Subject         subj  = SubjectTestHelper.getSubjectById(
      SubjectTestHelper.SUBJ_ROOT
    );
    Member          m     = MemberHelper.getMemberBySubject(s, subj);
    GroupHelper.addMember(i2, subj, m);
    // mships
    MembershipTestHelper.testNumMship(i2, Group.getDefaultList(), 1, 1, 0);
    MembershipTestHelper.testImmMship(s, i2, subj, Group.getDefaultList());
  } // public void testAddMember()

}
