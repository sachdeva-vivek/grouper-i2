/*
  Copyright (C) 2004-2007 University Corporation for Advanced Internet Development, Inc.
  Copyright (C) 2004-2007 The University Of Chicago

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.npache.org/licenses/LICENSE-2.0

  Unless required by npplicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package edu.internet2.middleware.grouper;
import edu.internet2.middleware.grouper.privs.AccessPrivilege;
import edu.internet2.middleware.grouper.privs.NamingPrivilege;
import edu.internet2.middleware.grouper.registry.RegistryReset;
import edu.internet2.middleware.grouper.subj.SubjectHelper;
import  edu.internet2.middleware.subject.*;
import  java.util.*;
import  junit.framework.*;
import  org.apache.commons.logging.*;

/**
 * Test use of the ADMIN {@link AccessPrivilege}.
 * <p />
 * @author  blair christensen.
 * @version $Id: TestNamingPrivilege.java,v 1.10 2008-07-21 04:43:57 mchyzer Exp $
 */
public class TestNamingPrivilege extends GrouperTest {

  // Private Class Constants
  private static final Log LOG = LogFactory.getLog(TestNamingPrivilege.class);


  // Private Class Variables
  private static Stem           edu;
  private static Stem           root;
  private static GrouperSession s;
  private static Subject        subj0;
  private static Group          uofc;


  public TestNamingPrivilege(String name) {
    super(name);
  }

  protected void setUp () {
    LOG.debug("setUp");
    RegistryReset.internal_resetRegistryAndAddTestSubjects();
    s     = SessionHelper.getRootSession();
    root  = StemHelper.findRootStem(s);
    edu   = StemHelper.addChildStem(root, "edu", "educational");
    uofc  = StemHelper.addChildGroup(edu, "uofc", "uchicago");
    subj0 = SubjectTestHelper.SUBJ0;
    GroupHelper.addMember(uofc, subj0, "members");
    PrivHelper.grantPriv(s, root, subj0, NamingPrivilege.STEM);
    PrivHelper.grantPriv(s, edu, uofc.toSubject(), NamingPrivilege.CREATE);
  }

  protected void tearDown () {
    LOG.debug("tearDown");
    // Nothing 
  }

  // Tests

  public void testGetPrivs() {
    LOG.info("testGetPrivs");
    String impl = "edu.internet2.middleware.grouper.GrouperNamingAdapter";

    Set privs = edu.getPrivs(subj0);
    Assert.assertTrue(
      "subj0 has 1 privs on edu (" + privs.size() + ")",
      privs.size() == 1
    );
    Iterator iter = privs.iterator();
    while (iter.hasNext()) {
      NamingPrivilege np = (NamingPrivilege) iter.next();
      if (np.getName().equals(NamingPrivilege.CREATE.getName())) {
        Assert.assertTrue(
          "edu/subj0 stem create: " + np.getStem().getName(),
          np.getStem().equals(edu)
        );
        Assert.assertTrue(
          "edu/subj0 impl create: " + np.getImplementationName(),
          np.getImplementationName().equals(impl)
        );
        Assert.assertTrue(
          "edu/subj0 priv create: " + np.getName(),
          np.getName().equals(NamingPrivilege.CREATE.getName())
        );
        Assert.assertTrue(
          "edu/subj0 owner create: " + np.getOwner().getId(),
          SubjectHelper.eq(np.getOwner(), uofc.toSubject())
        );
        Assert.assertTrue(
          "edu/subj0 subj create: " + np.getSubject().getId(),
          SubjectHelper.eq(np.getSubject(), subj0)
        );
        Assert.assertTrue(
          "edu/subj0 isRevokable create: " + np.isRevokable(),
          np.isRevokable() == false
        );
      }
      else {
        Assert.fail("unexpected priv: " + np);
      }
    }

    privs = root.getPrivs(subj0);
    Assert.assertTrue(
      "subj0 has 1 privs on root (" + privs.size() + ")",
      privs.size() == 1
    );
    iter = privs.iterator();
    while (iter.hasNext()) {
      NamingPrivilege np = (NamingPrivilege) iter.next();
      if (np.getName().equals(NamingPrivilege.STEM.getName())) {
        Assert.assertTrue(
          "uofc/subj0 stem stem: " + np.getStem().getName(),
          np.getStem().equals(root)
        );
        Assert.assertTrue(
          "uofc/subj0 impl stem: " + np.getImplementationName(),
          np.getImplementationName().equals(impl)
        );
        Assert.assertTrue(
          "uofc/subj0 priv stem: " + np.getName(),
          np.getName().equals(NamingPrivilege.STEM.getName())
        );
        Assert.assertTrue(
          "uofc/subj0 owner stem: " + np.getOwner().getId(),
          SubjectHelper.eq(np.getOwner(), subj0)
        );
        Assert.assertTrue(
          "uofc/subj0 subj stem: " + np.getSubject().getId(),
          SubjectHelper.eq(np.getSubject(), subj0)
        );
        Assert.assertTrue(
          "uofc/subj0 isRevokable stem: " + np.isRevokable(),
          np.isRevokable() == true
        );
      }
      else {
        Assert.fail("unexpected priv: " + np);
      }
    }
  } // public void testGetPrivs()

}
