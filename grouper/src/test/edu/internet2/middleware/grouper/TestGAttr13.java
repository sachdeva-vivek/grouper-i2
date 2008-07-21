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

import edu.internet2.middleware.grouper.cfg.GrouperConfig;
import edu.internet2.middleware.grouper.registry.RegistryReset;

/**
 * @author  blair christensen.
 * @version $Id: TestGAttr13.java,v 1.5 2008-07-21 04:43:57 mchyzer Exp $
 * @since   1.1.0
 */
public class TestGAttr13 extends TestCase {

  private static final Log LOG = LogFactory.getLog(TestGAttr13.class);

  public TestGAttr13(String name) {
    super(name);
  }

  protected void setUp () {
    LOG.debug("setUp");
    RegistryReset.reset();
  }

  protected void tearDown () {
    LOG.debug("tearDown");
  }

  public void testDeleteAttribute() {
    LOG.info("testDeleteAttribute");
    try {
      R     r   = R.populateRegistry(1, 1, 0);
      Group gA  = r.getGroup("a", "a");

      String k = "description";
      gA.setAttribute(k, k);
      gA.store();
      gA.deleteAttribute(k);
      T.ok("deleted attribute");
      T.string(
        "fetch deleted attribute",
        GrouperConfig.EMPTY_STRING,
        gA.getAttribute(k)
      );

      r.rs.stop();
    }
    catch (Exception e) {
      T.e(e);
    }
  } // public void testDeleteAttribute()

} // public class TestGAttr13

