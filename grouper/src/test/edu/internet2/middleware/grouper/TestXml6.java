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
import  java.io.*;
import  java.util.*;
import  org.apache.commons.logging.*;

import edu.internet2.middleware.grouper.privs.AccessPrivilege;
import edu.internet2.middleware.grouper.registry.RegistryReset;
import edu.internet2.middleware.grouper.xml.XmlExporter;
import edu.internet2.middleware.grouper.xml.XmlImporter;
import edu.internet2.middleware.grouper.xml.XmlReader;

/**
 * @author  blair christensen.
 * @version $Id: TestXml6.java,v 1.6 2008-07-21 04:43:57 mchyzer Exp $
 * @since   1.1.0
 */
public class TestXml6 extends GrouperTest {

  private static final Log LOG = LogFactory.getLog(TestXml6.class);

  public TestXml6(String name) {
    super(name);
  }

  protected void setUp () {
    LOG.debug("setUp");
    RegistryReset.reset();
  }

  protected void tearDown () {
    LOG.debug("tearDown");
  }

  public void testFullExportFullImportFullAccessPrivs() {
    LOG.info("testFullExportFullImportFullAccessPrivs");
    try {
      // Populate Registry And Verify
      R     r   = R.populateRegistry(1, 1, 0);
      Group gA  = assertFindGroupByName( r.rs, "i2:a:a" );
      gA.grantPriv( SubjectFinder.findAllSubject(), AccessPrivilege.ADMIN );
      gA.grantPriv( SubjectFinder.findAllSubject(), AccessPrivilege.OPTIN );
      gA.grantPriv( SubjectFinder.findAllSubject(), AccessPrivilege.OPTOUT );
      gA.revokePriv( SubjectFinder.findAllSubject(), AccessPrivilege.READ );
      gA.grantPriv( SubjectFinder.findAllSubject(), AccessPrivilege.UPDATE );
      gA.revokePriv( SubjectFinder.findAllSubject(), AccessPrivilege.VIEW );
      boolean has_a   = gA.hasAdmin( SubjectFinder.findAllSubject() );
      boolean has_oi  = gA.hasOptin( SubjectFinder.findAllSubject() );
      boolean has_oo  = gA.hasOptout( SubjectFinder.findAllSubject() );
      boolean has_u   = gA.hasUpdate( SubjectFinder.findAllSubject() );
      r.rs.stop();

      // Export
      GrouperSession  s         = GrouperSession.start( SubjectFinder.findRootSubject() );
      Writer          w         = new StringWriter();
      XmlExporter     exporter  = new XmlExporter(s, new Properties());
      exporter.export(w);
      String          xml       = w.toString();
      s.stop();

      // Reset And Verify
      RegistryReset.reset();
      s = GrouperSession.start( SubjectFinder.findRootSubject() );
      assertDoNotFindGroupByName( s, "i2:a:a" );
      s.stop();

      // Import 
      s = GrouperSession.start( SubjectFinder.findRootSubject() );
      XmlImporter importer = new XmlImporter(s, new Properties());
      importer.load( XmlReader.getDocumentFromString(xml) );
      s.stop();

      // Verify
      s   = GrouperSession.start( SubjectFinder.findRootSubject() );
      gA  = assertFindGroupByName( s, "i2:a:a" );
      assertGroupHasAdmin( gA, SubjectFinder.findAllSubject(), has_a );
      assertGroupHasOptin( gA, SubjectFinder.findAllSubject(), has_oi );
      assertGroupHasOptout( gA, SubjectFinder.findAllSubject(), has_oo );
      assertGroupHasRead( gA, SubjectFinder.findAllSubject(), true ); // added by default
      assertGroupHasUpdate( gA, SubjectFinder.findAllSubject(), has_u );
      assertGroupHasView( gA, SubjectFinder.findAllSubject(), true ); // added by default
      s.stop();
    }
    catch (Exception e) {
      T.e(e);
    }
  } // public void testFullExportFullImportFullAccessPrivs()

} // public class TestXml6

