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
 * @version $Id: TestCompositeC23.java,v 1.5 2008-07-21 04:43:57 mchyzer Exp $
 */
public class TestCompositeC23 extends TestCase {

  // Private Static Class Constants
  private static final Log LOG = LogFactory.getLog(TestCompositeC23.class);

  public TestCompositeC23(String name) {
    super(name);
  }

  protected void setUp () {
    LOG.debug("setUp");
    RegistryReset.reset();
  }

  protected void tearDown () {
    LOG.debug("tearDown");
  }

  public void testAddUnionWithOneChildAndParent() {
    LOG.info("testAddUnionWithOneChildAndParent");
    try {
      R       r     = R.populateRegistry(1, 4, 1);
      // Feeder Groups
      Group   a     = r.getGroup("a", "a");
      Subject subjA = r.getSubject("a");
      a.addMember(subjA);
      Group   b     = r.getGroup("a", "b");
      // Composite Group
      Group   c     = r.getGroup("a", "c");
      c.addCompositeMember(CompositeType.COMPLEMENT, a, b); // subjA - 
      // Parent Group
      Group   d     = r.getGroup("a", "d");
      Subject cSubj = c.toSubject();
      d.addMember(cSubj);

      // And test
      Assert.assertFalse( "a !hasComposite" , a.hasComposite()  );
      Assert.assertFalse( "b !hasComposite" , b.hasComposite()  );
      Assert.assertTrue(  "c hasComposite"  , c.hasComposite()  );

      Assert.assertTrue(  "a isComposite"   , a.isComposite()   );
      Assert.assertTrue(  "b isComposite"   , b.isComposite()   );
      Assert.assertFalse( "c !isComposite"  , c.isComposite()   );

      T.amount( "a members", 1, a.getImmediateMembers().size() );
      Assert.assertTrue("a has subjA", a.hasMember(subjA));
      T.amount( "b members", 0, b.getMembers().size() );
      T.amount( "c members", 1, c.getMembers().size() );
      Assert.assertTrue("c has subjA", c.hasMember(subjA));
      T.amount( "d members", 2, d.getMembers().size() );
      Assert.assertTrue("d has cSubj", d.hasMember(cSubj));
      Assert.assertTrue("d has subjA", d.hasMember(subjA));

      r.rs.stop();
    }
    catch (Exception e) {
      T.e(e);
    }
  } // public void testAddUnionWithOneChildAndParent()

}
