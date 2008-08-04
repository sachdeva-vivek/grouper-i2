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
import  org.apache.commons.logging.*;

import edu.internet2.middleware.grouper.misc.GrouperDAOFactory;

/**
 * @author  blair christensen.
 * @version $Id: Test_Integration_HibernateStemDAO_findAllByApproximateDisplayExtension.java,v 1.3 2008-07-21 04:43:57 mchyzer Exp $
 * @since   1.2.0
 */
public class Test_Integration_HibernateStemDAO_findAllByApproximateDisplayExtension extends GrouperTest {

  // PRIVATE CLASS CONSTANTS //
  private static final Log LOG = LogFactory.getLog(Test_Integration_HibernateStemDAO_findAllByApproximateDisplayExtension.class);


  // TESTS //  

  public void testFindAllByApproximateDisplayExtension_whenUpperCaseInRegistry() {
    try {
      LOG.info("testFindAllByApproximateDisplayExtension_whenUpperCaseInRegistry");
      R       r     = R.getContext("i2mi");
      Stem    i2mi  = r.getStem("i2mi");
      Stem    child = i2mi.addChildStem("lower", "UPPER");

      assertEquals(
        "stems found by displayExtension",
        1, 
        GrouperDAOFactory.getFactory().getStem().findAllByApproximateDisplayExtension( child.getDisplayExtension() ).size()
      );
    }
    catch (Exception e) {
      unexpectedException(e);
    }
  } // public void testFindAllByApproximateDisplayExtension_whenUpperCaseInRegistry

} // public class Test_Integration_HibernateStemDAO_findAllByApproximateDisplayExtension extends GrouperTest
