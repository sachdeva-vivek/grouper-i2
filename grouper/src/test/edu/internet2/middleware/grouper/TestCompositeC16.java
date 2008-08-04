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
import edu.internet2.middleware.grouper.misc.CompositeType;
import edu.internet2.middleware.grouper.registry.RegistryReset;
import  edu.internet2.middleware.subject.*;
import  junit.framework.*;
import  org.apache.commons.logging.*;

/**
 * @author  blair christensen.
 * @version $Id: TestCompositeC16.java,v 1.5 2008-07-21 04:43:57 mchyzer Exp $
 */
public class TestCompositeC16 extends TestCase {

  // Private Static Class Constants
  private static final Log LOG = LogFactory.getLog(TestCompositeC16.class);

  public TestCompositeC16(String name) {
    super(name);
  }

  protected void setUp () {
    LOG.debug("setUp");
    RegistryReset.reset();
  }

  protected void tearDown () {
    LOG.debug("tearDown");
  }

  public void testDelUnionWithTwoChildrenAndNoParents() {
    LOG.info("testDelUnionWithTwoChildrenAndNoParents");
    try {
      R       r     = R.populateRegistry(1, 3, 2);
      Group   a     = r.getGroup("a", "a");
      Group   b     = r.getGroup("a", "b");
      Group   c     = r.getGroup("a", "c");
      Subject subjA = r.getSubject("a");
      Subject subjB = r.getSubject("b");
      b.addMember(subjA);
      c.addMember(subjB);
      a.addCompositeMember(CompositeType.COMPLEMENT, b, c);
      a.deleteCompositeMember();
      T.amount("a members", 0, a.getMembers().size());
      Assert.assertFalse("a !hasMember subjA", a.hasMember(subjA));
      Assert.assertFalse("a !hasMember subjB", a.hasMember(subjB));
      r.rs.stop();
    }
    catch (Exception e) {
      T.e(e);
    }
  } // public void testDelUnionWithTwoChildrenAndNoParents()

}
