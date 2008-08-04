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

package edu.internet2.middleware.grouper.internal.dao.hib3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.io.DatabaseIO;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Table;
import org.apache.ddlutils.platform.SqlBuilder;
import org.hibernate.HibernateException;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import edu.internet2.middleware.grouper.cfg.GrouperConfig;
import edu.internet2.middleware.grouper.hibernate.GrouperTransactionType;
import edu.internet2.middleware.grouper.hibernate.HibernateHandler;
import edu.internet2.middleware.grouper.hibernate.HibernateSession;
import edu.internet2.middleware.grouper.internal.dao.GrouperDAOException;
import edu.internet2.middleware.grouper.internal.dao.RegistryDAO;

/**
 * Basic Hibernate <code>Registry</code> DAO interface.
 * @author  blair christensen.
 * @version $Id: Hib3RegistryDAO.java,v 1.6 2008-07-21 04:43:58 mchyzer Exp $
 * @since   @HEAD@
 */
class Hib3RegistryDAO implements RegistryDAO {

  // PRIVATE CLASS CONSTANTS //
  private static final boolean PRINT_DDL_TO_CONSOLE = false;
  private static final boolean EXPORT_DDL_TO_DB     = true;
  
  // PUBLIC INSTANCE METHODS //

  /**
   * @since   @HEAD@
   */
  public void initializeSchema() 
    throws  GrouperDAOException
  {
    String outputFile = GrouperConfig.getBuildProperty("schemaexport.out");

    // drop foreign keys and save the drop statements in a StringWriter.
    StringWriter sw = new StringWriter();
    dropForeignKeys(sw);

    // run schema export and save the ddl to the outputFile.
    try {
      new SchemaExport( Hib3DAO.getConfiguration() )
        .setDelimiter(";")
        .setOutputFile( outputFile )
        .create(PRINT_DDL_TO_CONSOLE, EXPORT_DDL_TO_DB);
    }
    catch (HibernateException eH) {
      throw new GrouperDAOException( eH.getMessage(), eH );
    }

    FileWriter fw = null;
    try {
      // read the ddl generated by the schema export and append to the StringWriter.
      BufferedReader reader = new BufferedReader(new FileReader(outputFile));
      String line = null;
      String newLine = System.getProperty("line.separator", "\n");
      while ((line = reader.readLine()) != null) {
        sw.write(line + newLine);
      }
      reader.close();

      // write the foreign key drop statements and the ddl from the schema export to the outputFile.
      fw = new FileWriter(outputFile, false);
      fw.write(sw.toString());

      // add foreign keys and write the ddl to the outputFile.
      addForeignKeys(fw);
    }
    catch (Exception e) {
      throw new GrouperDAOException( e.getMessage(), e );
    }
    finally {
      if (fw != null) {
        try { 
          fw.close();
        }
        catch (IOException e) { }
      }
    }
  }

  /**
   * @since   @HEAD@
   */
  public void reset() 
    throws  GrouperDAOException {
    HibernateSession.callbackHibernateSession(GrouperTransactionType.READ_WRITE_OR_USE_EXISTING,
        new HibernateHandler() {

          public Object callback(HibernateSession hibernateSession) {

            Hib3MembershipDAO.reset(hibernateSession);
            Hib3GrouperSessionDAO.reset(hibernateSession);
            Hib3CompositeDAO.reset(hibernateSession);
            Hib3GroupDAO.reset(hibernateSession);
            Hib3StemDAO.reset(hibernateSession);
            Hib3MemberDAO.reset(hibernateSession);
            Hib3GroupTypeDAO.reset(hibernateSession);
            Hib3RegistrySubjectDAO.reset(hibernateSession);

            return null;
          }
      
    });
    
  } 

  public void addForeignKeys(Writer writer)
    throws GrouperDAOException {

    try {
      Hib3DaoConfig config = new Hib3DaoConfig();
      BasicDataSource ds = new BasicDataSource();
      ds.setUrl(config.getProperty("hibernate.connection.url"));
      ds.setDriverClassName(config.getProperty("hibernate.connection.driver_class"));
      ds.setUsername(config.getProperty("hibernate.connection.username"));
      ds.setPassword(config.getProperty("hibernate.connection.password"));

      Platform platform = PlatformFactory.createNewPlatformInstance(ds);

      if (Hib3DAO.class.getResource("Hib3ForeignKeys.xml") == null) {
        throw new RuntimeException("Cannot find resource Hib3ForeignKeys.xml.");
      }
      Database model = new DatabaseIO().read(Hib3DAO.class.getResource("Hib3ForeignKeys.xml").toString());

      StringWriter stringWriter = new StringWriter();
      SqlBuilder builder = platform.getSqlBuilder();
      builder.setWriter(stringWriter);
      builder.createExternalForeignKeys(model);

      // write the DDL to the writer before applying it to the database.
      if (writer != null) {
        writer.write(stringWriter.toString());
      }

      platform.evaluateBatch(stringWriter.toString(), false);
    }
    catch (IOException e) {
      throw new GrouperDAOException( e.getMessage(), e );
    }
  }

  public void dropForeignKeys(Writer writer)
    throws GrouperDAOException {

    try {
      Hib3DaoConfig config = new Hib3DaoConfig();
      BasicDataSource ds = new BasicDataSource();
      ds.setUrl(config.getProperty("hibernate.connection.url"));
      ds.setDriverClassName(config.getProperty("hibernate.connection.driver_class"));
      ds.setUsername(config.getProperty("hibernate.connection.username"));
      ds.setPassword(config.getProperty("hibernate.connection.password"));

      Platform platform = PlatformFactory.createNewPlatformInstance(ds);

      if (Hib3DAO.class.getResource("Hib3ForeignKeys.xml") == null) {
        throw new RuntimeException("Cannot find resource Hib3ForeignKeys.xml.");
      }
      Database model = new DatabaseIO().read(Hib3DAO.class.getResource("Hib3ForeignKeys.xml").toString());
      Table[] tables = model.getTables();

      StringWriter stringWriter = new StringWriter();
      SqlBuilder builder = platform.getSqlBuilder();
      builder.setWriter(stringWriter);

      for (int i = 0; i < tables.length; i++) {
        builder.dropExternalForeignKeys(tables[i]);
      }

      // write the DDL to the writer before applying it to the database.
      if (writer != null) {
        writer.write(stringWriter.toString());
      }

      int ret = platform.evaluateBatch(stringWriter.toString(), true);
    }
    catch (IOException e) {
      throw new GrouperDAOException( e.getMessage(), e );
    }
  }
} 
