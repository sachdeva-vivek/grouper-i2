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
import  junit.framework.*;
import  org.apache.commons.logging.*;

import edu.internet2.middleware.grouper.privs.AccessPrivilege;
import edu.internet2.middleware.grouper.registry.RegistryReset;

/**
 * @author  blair christensen.
 * @version $Id: TestMember22.java,v 1.4 2008-07-21 04:43:57 mchyzer Exp $
 */
public class TestMember22 extends TestCase {

  private static final Log LOG = LogFactory.getLog(TestMember22.class);

  public TestMember22(String name) {
    super(name);
  }

  protected void setUp () {
    LOG.debug("setUp");
    RegistryReset.reset();
  }

  protected void tearDown () {
    LOG.debug("tearDown");
  }

  public void testFailCanViewWhenNoPriv() {
    LOG.info("testFailCanViewWhenNoPriv");
    try {
      R       r   = R.populateRegistry(1, 1, 1);
      Group   a   = r.getGroup("a", "a");
      a.revokePriv(AccessPrivilege.VIEW); // Revoke VIEW from all subjects
      a.revokePriv(AccessPrivilege.READ); // Revoke READ from all subjects
      Member  m   = MemberFinder.findBySubject(r.rs, r.getSubject("a"));
      Assert.assertFalse("OK: cannot view", m.canView(a));
      r.rs.stop();
    }
    catch (Exception e) {
      T.e(e);
    }
  } // public void testFailCanViewWhenNoPriv()

}
