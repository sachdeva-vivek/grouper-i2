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
import edu.internet2.middleware.grouper.cfg.GrouperConfig;
import edu.internet2.middleware.grouper.registry.RegistryReset;
import edu.internet2.middleware.grouper.subj.InternalSourceAdapter;
import  edu.internet2.middleware.subject.*;
import  junit.framework.*;
import  org.apache.commons.logging.*;

/**
 * @author  blair christensen.
 * @version $Id: TestSubject14.java,v 1.3 2008-07-21 04:43:57 mchyzer Exp $
 * @since   1.1.0
 */
public class TestSubject14 extends TestCase {

  private static final Log LOG = LogFactory.getLog(TestSubject14.class);

  public TestSubject14(String name) {
    super(name);
  }

  protected void setUp () {
    LOG.debug("setUp");
    RegistryReset.reset();
  }

  protected void tearDown () {
    LOG.debug("tearDown");
  }

  public void testFindGrouperAllByIdentifierAndTypeAndSource() {
    LOG.info("testFindGrouperAllByIdentifierAndTypeAndSource");
    try {
      Subject subj = SubjectFinder.findByIdentifier(
        GrouperConfig.ALL, GrouperConfig.IST, InternalSourceAdapter.ID
      );
      Assert.assertNotNull("subj !null", subj);
      Assert.assertTrue("subj instanceof Subject", subj instanceof Subject);
      T.string("subj id"      , GrouperConfig.ALL       , subj.getId()              );
      T.string("subj type"    , GrouperConfig.IST       , subj.getType().getName()  );
      T.string("subj source"  , InternalSourceAdapter.ID, subj.getSource().getId()  );
    }
    catch (Exception e) {
      T.e(e);
    }
  } // public void testFindGrouperAllByIdentifierAndTypeAndSource()

} // public class TestSubject14
