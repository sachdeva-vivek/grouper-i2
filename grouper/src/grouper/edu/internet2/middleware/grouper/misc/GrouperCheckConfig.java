/*
 * @author mchyzer
 * $Id: GrouperCheckConfig.java,v 1.4 2008-10-21 18:12:45 mchyzer Exp $
 */
package edu.internet2.middleware.grouper.misc;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.security.CodeSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;

import com.p6spy.engine.spy.P6SpyDriver;

import edu.internet2.middleware.grouper.Group;
import edu.internet2.middleware.grouper.GroupFinder;
import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.SubjectFinder;
import edu.internet2.middleware.grouper.hooks.CompositeHooks;
import edu.internet2.middleware.grouper.hooks.FieldHooks;
import edu.internet2.middleware.grouper.hooks.GroupHooks;
import edu.internet2.middleware.grouper.hooks.GroupTypeHooks;
import edu.internet2.middleware.grouper.hooks.GroupTypeTupleHooks;
import edu.internet2.middleware.grouper.hooks.GrouperSessionHooks;
import edu.internet2.middleware.grouper.hooks.LifecycleHooks;
import edu.internet2.middleware.grouper.hooks.MemberHooks;
import edu.internet2.middleware.grouper.hooks.MembershipHooks;
import edu.internet2.middleware.grouper.hooks.StemHooks;
import edu.internet2.middleware.grouper.privs.AccessAdapter;
import edu.internet2.middleware.grouper.privs.NamingAdapter;
import edu.internet2.middleware.grouper.util.GrouperUtil;
import edu.internet2.middleware.morphString.Morph;
import edu.internet2.middleware.subject.SubjectCheckConfig;


/**
 * check the configuration of grouper to make sure things are configured right, and
 * to give descriptive errors of the problems
 */
public class GrouperCheckConfig {

  /**
   * 
   */
  public static final String GROUPER_PROPERTIES_NAME = "grouper.properties";

  /**
   * logger 
   */
  private static final Log LOG = GrouperUtil.getLog(GrouperCheckConfig.class);

  /**
   * verify that a group exists by name (dont throw exceptions)
   * @param groupName
   * @return true if group exists, and false if not
   */
  public static boolean checkGroup(String groupName) {

    if (configCheckDisabled()) {
      return true;
    }
    GrouperSession grouperSession = null;
    try {
      grouperSession = GrouperSession.start(SubjectFinder.findRootSubject());
      Group group = GroupFinder.findByName(grouperSession, groupName);
      if (group != null) {
        return true;
      }
    } catch (Exception e) {
      
    } finally {
      GrouperSession.stopQuietly(grouperSession);
    }
    return false;
  }
  
  /**
   * check a jar
   * @param name name of the jar from grouper
   * @param size that the jar should be
   * @param sampleClassName inside the jar
   * @param manifestVersion in the manifest file, which version we are expecting
   */
  public static void checkJar(String name, long size, String sampleClassName, String manifestVersion) {
    
    if (configCheckDisabled()) {
      return;
    }
    
    Class sampleClass = null;
    try {
      sampleClass = Class.forName(sampleClassName);
    } catch (ClassNotFoundException cnfe) {
      String error = "cannot find class " + sampleClassName + ", perhaps you are missing jar: " + name;
      System.err.println("Grouper warning: " + error);
      LOG.warn(error);
      return;
    }
    String jarFileFullName = null;
    String jarFileName = null;
    String jarVersion = null;
    long jarFileSize = -1;
    try {
      
      File jarFile = jarFile(sampleClass);
      jarFileFullName = jarFile.getCanonicalPath();
      jarFileName = jarFile.getName();
      jarFileSize = jarFile.length();
      jarVersion = jarVersion(sampleClass);
      
      if (size == jarFileSize && StringUtils.equals(manifestVersion, jarVersion)
          && StringUtils.equals(name, jarFile.getName())) {
        LOG.debug("Found jarfile: " + jarFileFullName + " with correct size " + size + " and version: " + manifestVersion);
        return;
      }
      
    } catch (Exception e) {
      //LOG.error("Error finding jar: " + name, e);
      //e.printStackTrace();
      //having problems
    }
    String error = "jarfile mismath, expecting name: '" + name + "' size: " + size 
      + " manifest version: " + manifestVersion + ".  However the jar detected is: " 
      + jarFileFullName + ", name: " + jarFileName + " size: " + jarFileSize
      + " manifest version: " + jarVersion;
    System.err.println("Grouper warning: " + error);
    LOG.warn(error);
  }
  
  /**
   * make sure a resource is on the resource path
   * @param resourcePath
   * @return false if problem or if not checking configs
   */
  public static boolean checkResource(String resourcePath) {
    if (configCheckDisabled()) {
      return false;
    }
    try {
      URL url = GrouperUtil.computeUrl(resourcePath, false);
      if (url != null) {
        LOG.debug("Found resource: " + url);
        return true;
      }
    } catch (Exception e) {
      //this means it cant be found
    }
    String error = "Cant find required resource on classpath: " + resourcePath;
    //this is serious, lets go out and error
    System.err.println("Grouper error: " + error);
    LOG.error(error);
    return false;
  }

  /** cache if we are disabling config check */
  private static Boolean disableConfigCheck = null;
  
  /**
   * if the config check is disabled
   * @return if the config check is disabled
   */
  public static boolean configCheckDisabled() {
    if (disableConfigCheck == null) {
      //see if we shouldnt do this (but dont use ApiConfig API)
      try {
        Properties properties = GrouperUtil.propertiesFromResourceName(GROUPER_PROPERTIES_NAME);
        String detectErrorsKey = "configuration.detect.errors";
        String detectErrors = GrouperUtil.propertiesValue(properties, detectErrorsKey);
        if (!GrouperUtil.booleanValue(detectErrors, true)) {
          String warning = "Not checking configuration integrity due to grouper.properties: " 
              + detectErrorsKey;
          System.err.println("Grouper warning: " + warning);
          LOG.warn(warning);
          disableConfigCheck = true;
        }
      } catch (Exception e) {
        //cant read grouper properties
      }
      if (disableConfigCheck == null) {
        disableConfigCheck = false;
      }
    }
    return disableConfigCheck;
  }

  /**
   * make sure grouper config files exist
   */
  private static void checkGrouperConfigs() {
    
    //make sure config files are there
    checkGrouperConfig();
    checkResource("ehcache.xml");
    checkResource("grouper.ehcache.xml");
    checkResource("grouper.hibernate.properties");
    checkResource("log4j.properties");
    checkResource("morphString.properties");
    checkResource("sources.xml");
    
  }
  
  /**
   * go through each property and check types of values
   */
  private static void checkGrouperConfig() {
    if (!checkResource(GROUPER_PROPERTIES_NAME)) {
      return;
    }

    propertyValueClass(GROUPER_PROPERTIES_NAME, "privileges.access.interface", 
        AccessAdapter.class, true);
    
    propertyValueClass(GROUPER_PROPERTIES_NAME, "privileges.naming.interface", 
        NamingAdapter.class, true);
    
    propertyValueBoolean(GROUPER_PROPERTIES_NAME, "groups.create.grant.all.admin", true);
    propertyValueBoolean(GROUPER_PROPERTIES_NAME, "groups.create.grant.all.optin", true);
    propertyValueBoolean(GROUPER_PROPERTIES_NAME, "groups.create.grant.all.optout", true);
    propertyValueBoolean(GROUPER_PROPERTIES_NAME, "groups.create.grant.all.read", true);
    propertyValueBoolean(GROUPER_PROPERTIES_NAME, "groups.create.grant.all.update", true);
    propertyValueBoolean(GROUPER_PROPERTIES_NAME, "groups.create.grant.all.view", true);

    propertyValueBoolean(GROUPER_PROPERTIES_NAME, "stems.create.grant.all.create", true);
    propertyValueBoolean(GROUPER_PROPERTIES_NAME, "stems.create.grant.all.stem", true);

    propertyValueBoolean(GROUPER_PROPERTIES_NAME, "memberships.log.group.effective.add", true);
    propertyValueBoolean(GROUPER_PROPERTIES_NAME, "memberships.log.group.effective.del", true);

    propertyValueBoolean(GROUPER_PROPERTIES_NAME, "memberships.log.stem.effective.add", true);
    propertyValueBoolean(GROUPER_PROPERTIES_NAME, "memberships.log.stem.effective.del", true);

    propertyValueBoolean(GROUPER_PROPERTIES_NAME, "groups.wheel.use", true);

    propertyValueBoolean(GROUPER_PROPERTIES_NAME, "registry.autoinit", true);
    propertyValueBoolean(GROUPER_PROPERTIES_NAME, "configuration.detect.errors", true);
    propertyValueBoolean(GROUPER_PROPERTIES_NAME, "configuration.display.startup.message", true);

    propertyValueClass(GROUPER_PROPERTIES_NAME, "dao.factory", 
        GrouperDAOFactory.class, true);

    propertyValueBoolean(GROUPER_PROPERTIES_NAME, "grouper.setters.dont.cause.queries", true);

    propertyValueClass(GROUPER_PROPERTIES_NAME, "hooks.group.class", GroupHooks.class, false);
    propertyValueClass(GROUPER_PROPERTIES_NAME, "hooks.lifecycle.class", LifecycleHooks.class, false);
    propertyValueClass(GROUPER_PROPERTIES_NAME, "hooks.membership.class", MembershipHooks.class, false);
    propertyValueClass(GROUPER_PROPERTIES_NAME, "hooks.member.class", MemberHooks.class, false);
    propertyValueClass(GROUPER_PROPERTIES_NAME, "hooks.stem.class", StemHooks.class, false);
    propertyValueClass(GROUPER_PROPERTIES_NAME, "hooks.composite.class", CompositeHooks.class, false);
    propertyValueClass(GROUPER_PROPERTIES_NAME, "hooks.field.class", FieldHooks.class, false);
    propertyValueClass(GROUPER_PROPERTIES_NAME, "hooks.grouperSession.class", GrouperSessionHooks.class, false);
    propertyValueClass(GROUPER_PROPERTIES_NAME, "hooks.groupType.class", GroupTypeHooks.class, false);
    propertyValueClass(GROUPER_PROPERTIES_NAME, "hooks.groupTypeTuple.class", GroupTypeTupleHooks.class, false);

    propertyValueBoolean(GROUPER_PROPERTIES_NAME, "ddlutils.exclude.subject.tables", true);
    propertyValueBoolean(GROUPER_PROPERTIES_NAME, "ddlutils.schemaexport.dropThenCreate", true);
    propertyValueBoolean(GROUPER_PROPERTIES_NAME, "ddlutils.schemaexport.writeAndRunScript", true);
    propertyValueBoolean(GROUPER_PROPERTIES_NAME, "ddlutils.schemaexport.installGrouperData", true);
    propertyValueBoolean(GROUPER_PROPERTIES_NAME, "ddlutils.failIfNotRightVersion", true);
    propertyValueBoolean(GROUPER_PROPERTIES_NAME, "ddlutils.dropBackupUuidCols", true);
    propertyValueBoolean(GROUPER_PROPERTIES_NAME, "ddlutils.dropBackupFieldNameTypeCols", true);
    propertyValueBoolean(GROUPER_PROPERTIES_NAME, "ddlutils.disableComments", true);
    propertyValueBoolean(GROUPER_PROPERTIES_NAME, "ddlutils.disableViews", true);
    
  }

  /**
   * make sure a value exists in properties
   * @param resourceName
   * @param key
   * @return true if ok, false if not
   */
  public static boolean propertyValueRequired(String resourceName, String key) {
    Properties properties = GrouperUtil.propertiesFromResourceName(resourceName);
    String value = GrouperUtil.propertiesValue(properties, key);
    if (!StringUtils.isBlank(value)) {
      return true;
    }
    String error = "Cant find property " + key + " in resource: " + resourceName + ", it is required";
    System.err.println("Grouper error: " + error);
    LOG.error(error);
    return false;
  }
  
  /**
   * make sure a value is boolean in properties
   * @param resourceName
   * @param key
   * @param required
   * @return true if ok, false if not
   */
  public static boolean propertyValueBoolean(String resourceName, String key, boolean required) {
    
    if (required && !propertyValueRequired(resourceName, key)) {
      return false;
    }

    Properties properties = GrouperUtil.propertiesFromResourceName(resourceName);
    String value = GrouperUtil.propertiesValue(properties, key);
    //maybe ok not there
    if (!required && StringUtils.isBlank(value)) {
      return true;
    }
    try {
      GrouperUtil.booleanValue(value);
      return true;
    } catch (Exception e) {
      
    }
    String error = "Expecting true or false property " + key + " in resource: " + resourceName + ", but is '" + value + "'";
    System.err.println("Grouper error: " + error);
    LOG.error(error);
    return false;
  }
  
  /**
   * make sure a property is a class of a certain type
   * @param resourceName
   * @param key
   * @param classType
   * @param required 
   * @return true if ok
   */
  public static boolean propertyValueClass(String resourceName, 
      String key, Class<?> classType, boolean required) {

    if (required && !propertyValueRequired(resourceName, key)) {
      return false;
    }
    Properties properties = GrouperUtil.propertiesFromResourceName(resourceName);
    String value = GrouperUtil.propertiesValue(properties, key);

    //maybe ok not there
    if (!required && StringUtils.isBlank(value)) {
      return true;
    }
    
    String extraError = "";
    try {
      
      
      Class<?> theClass = GrouperUtil.forName(value);
      if (classType.isAssignableFrom(theClass)) {
        return true;
      }
      extraError = " does not derive from class: " + classType.getSimpleName();
      
    } catch (Exception e) {
      extraError = ", " + ExceptionUtils.getFullStackTrace(e);
    }
    String error = "Cant process property " + key + " in resource: " + resourceName + ", the current" +
    		" value is '" + value + "', which should be of type: " 
    		+ classType.getName() + extraError;
    System.err.println("Grouper error: " + error);
    LOG.error(error);
    return false;
  }
  
  /**
   * check the grouper config safely, log errors
   */
  public static void checkConfig() {
    
    if (configCheckDisabled()) {
      return;
    }
    
    checkGrouperConfigs();
    
    checkGrouperJars();
    
    checkGrouperVersion();
    
    checkConfigProperties();
    
    checkGrouperDb();
    
    checkGroups();
    
    //delegate to subject API to check configs
    SubjectCheckConfig.checkConfig();
  }
  
  /**
   * make sure configured groups are there 
   */
  public static void checkGroups() {
    Properties properties = GrouperUtil.propertiesFromResourceName(GROUPER_PROPERTIES_NAME);
    boolean useWheel = GrouperUtil.booleanValue(properties.getProperty("groups.wheel.use", "false"));
    if (useWheel) {
      String wheelName = GrouperUtil.propertiesValue(properties,"groups.wheel.group");
      if (!StringUtils.isBlank(wheelName)) {
        if (!checkGroup(wheelName)) {
          String error = "cannot find wheel group configured in grouper.properties under groups.wheel.group: " + wheelName;
          System.err.println("Grouper warning: " + error);
          LOG.warn(error);
        }
      }
    }
    
  }
  
  /**
   * make sure the grouper.hibernate.properties db settings are correct
   */
  private static void checkGrouperDb() {
    Properties grouperHibernateProperties = GrouperUtil.propertiesFromResourceName(
        "grouper.hibernate.properties");

    //#com.p6spy.engine.spy.P6SpyDriver, oracle.jdbc.driver.OracleDriver
    String driverClassName = GrouperUtil.propertiesValue(
        grouperHibernateProperties, "hibernate.connection.driver_class");
    String connectionUrl = GrouperUtil.propertiesValue(
        grouperHibernateProperties, "hibernate.connection.url");
    String dbUser = GrouperUtil.propertiesValue(
        grouperHibernateProperties, "hibernate.connection.username");
    String dbPassword = GrouperUtil.propertiesValue(
        grouperHibernateProperties, "hibernate.connection.password");
    
    checkDatabase(driverClassName, connectionUrl, dbUser, dbPassword, "grouper.hibernate.properties");
  }

  /**
   * test a database connection
   * @param driverClassName
   * @param connectionUrl
   * @param dbUser
   * @param dbPassword
   * @param databaseDescription friendly error description when there is a problem
   * @return true if it is ok, false if there is a problem
   */
  public static boolean checkDatabase(String driverClassName, String connectionUrl, String dbUser, String dbPassword,
      String databaseDescription) {
    try {
      
      dbPassword = Morph.decryptIfFile(dbPassword);
      Class driverClass = null;
      try {
        driverClass = GrouperUtil.forName(driverClassName);
      } catch (Exception e) {
        String error = "Error finding database driver class from " + databaseDescription + ": " 
          + driverClassName
          + ", perhaps you did not put the database driver jar in the lib/custom dir or lib dir, " +
              "or you have the wrong driver listed";
        System.err.println("Grouper error: " + error + ": " + ExceptionUtils.getFullStackTrace(e));
        LOG.error(error, e);
        return false;
      }
      
      //check out P6Spy
      String spyInsert = "";
      if (driverClass.equals(P6SpyDriver.class)) {
        spyInsert = " and spy.properties, ";
        checkResource("spy.properties");
        Properties spyProperties = GrouperUtil.propertiesFromResourceName("spy.properties");
        driverClassName = spyProperties.getProperty("realdriver");
        try {
          driverClass = GrouperUtil.forName(driverClassName);
        } catch (Exception e) {
          String error = "Error finding database driver class from spy.properties: '" 
            + driverClassName
            + "', perhaps you did not put the database driver jar in the lib/custom dir or lib dir, " +
                "or you have the wrong driver listed";
          System.err.println("Grouper error: " + error + ": " + ExceptionUtils.getFullStackTrace(e));
          LOG.error(error, e);
          return false;
        }
      }
      
      //lets make a db connection
      Connection dbConnection = null;
      try {
        dbConnection = DriverManager.getConnection(connectionUrl, dbUser, dbPassword);
        @SuppressWarnings("unused")
        String version = dbConnection.getMetaData().getDatabaseProductVersion();
        return true;
      } catch( SQLException sqlException) {
        String error = "Error connecting to the database with credentials from " + databaseDescription + ", "
          + spyInsert + "url: " + connectionUrl + ", driver: " + driverClassName + ", user: " + dbUser;
        System.out.println("Grouper error: " + error + ", " + ExceptionUtils.getFullStackTrace(sqlException));
        LOG.error(error, sqlException);
      } finally {
        GrouperUtil.closeQuietly(dbConnection);
      }
      
    } catch (Exception e) {
      String error = "Error verifying " + databaseDescription + " database configuration: ";
      System.err.println("Grouper error: " + error + ExceptionUtils.getFullStackTrace(e));
      LOG.error(error, e);
    }
    return false;
  }
  
    
  /**
   * make sure properties file properties match up
   */
  private static void checkConfigProperties() {

    checkConfigProperties(GROUPER_PROPERTIES_NAME, "grouper.example.properties");
    checkConfigProperties("grouper.hibernate.properties", "grouper.hibernate.example.properties");
    checkConfigProperties("morphString.properties", "morphString.example.properties");
    
    checkGrouperConfigDbChange();
    checkGrouperConfigGroupNameValidators();
  }

  /**
   * check the grouper loader db configs
   */
  public static void checkGrouperLoaderConfigDbs() {

    Properties properties = GrouperUtil.propertiesFromResourceName("grouper-loader.properties");
    
    //db.warehouse.user = mylogin
    //db.warehouse.pass = secret
    //db.warehouse.url = jdbc:mysql://localhost:3306/grouper
    //db.warehouse.driver = com.mysql.jdbc.Driver
    //make sure sequences are ok
    Set<String> dbKeys = retrievePropertiesKeys("grouper-loader.properties", 
        grouperLoaderDbPattern);
    while (dbKeys.size() > 0) {
      //get one
      String dbKey = dbKeys.iterator().next();
      //get the database name
      Matcher matcher = grouperLoaderDbPattern.matcher(dbKey);
      matcher.matches();
      String dbName = matcher.group(1);
      boolean missingOne = false;
      //now find all 4 required keys
      String userKey = "db." + dbName + ".user";
      if (!dbKeys.contains(userKey)) {
        String error = "cannot find grouper-loader.properties key: " + userKey; 
        System.out.println("Grouper error: " + error);
        LOG.error(error);
        missingOne = true;
      }
      dbKeys.remove(userKey);
      String passKey = "db." + dbName + ".pass";
      if (!dbKeys.contains(passKey)) {
        String error = "cannot find grouper-loader.properties key: " + passKey; 
        System.out.println("Grouper error: " + error);
        LOG.error(error);
        missingOne = true;
      }
      dbKeys.remove(passKey);
      String urlKey = "db." + dbName + ".url";
      if (!dbKeys.contains(urlKey)) {
        String error = "cannot find grouper-loader.properties key: " + urlKey; 
        System.out.println("Grouper error: " + error);
        LOG.error(error);
        missingOne = true;
      }
      dbKeys.remove(urlKey);
      String driverKey = "db." + dbName + ".driver";
      if (!dbKeys.contains(driverKey)) {
        String error = "cannot find grouper-loader.properties key: " + driverKey; 
        System.out.println("Grouper error: " + error);
        LOG.error(error);
        missingOne = true;
      }
      dbKeys.remove(driverKey);
      if (missingOne) {
        return;
      }
      String user = GrouperUtil.propertiesValue(properties, userKey);
      String password = GrouperUtil.propertiesValue(properties, passKey);
      String url = GrouperUtil.propertiesValue(properties, urlKey);
      String driver = GrouperUtil.propertiesValue(properties, driverKey);

      //try to connect to database
      checkDatabase(driver, url, user, password, "grouper-loader.properties database name '" + dbName + "'");
    }
    
  }
  
  /**
   * check the grouper config group name validators
   */
  private static void checkGrouperConfigGroupNameValidators() {
    //#group.attribute.validator.attributeName.0=extension
    //#group.attribute.validator.regex.0=^[a-zA-Z0-9]+$
    //#group.attribute.validator.vetoMessage.0=Group ID '$attributeValue$' is invalid since it must contain only alpha-numerics
    
    //make sure sequences are ok
    Set<String> validatorKeys = retrievePropertiesKeys(GROUPER_PROPERTIES_NAME, groupValidatorPattern);
    int i=0;
    while (true) {
      boolean foundOne = false;
      String attributeNameKey = "group.attribute.validator.attributeName." + i;
      String regexKey = "group.attribute.validator.regex." + i;
      String vetoMessageKey = "group.attribute.validator.vetoMessage." + i;

      foundOne = assertAndRemove(GROUPER_PROPERTIES_NAME, validatorKeys, 
          new String[]{attributeNameKey, regexKey, vetoMessageKey});
      if (!foundOne) {
        break;
      }
      i++;
    }
    if (validatorKeys.size() > 0) {
      String error = "in property file: grouper.properties, these properties " +
          "are misspelled or non-sequential: " + GrouperUtil.setToString(validatorKeys);
      System.err.println("Grouper error: " + error);
      LOG.error(error);
    }

  }

  /**
   * check db change properties in the grouper config
   */
  private static void checkGrouperConfigDbChange() {
    //make sure sequences are ok
    Set<String> dbChangeKeys = retrievePropertiesKeys(GROUPER_PROPERTIES_NAME, dbChangePattern);
    int i=0;
    //db.change.allow.user.0=grouper3
    //db.change.allow.url.0=jdbc:mysql://localhost:3306/grouper3
    while (true) {
      boolean foundOne = false;
      String allowUserKey = "db.change.allow.user." + i;
      String allowUrlKey = "db.change.allow.url." + i;
      String denyUserKey = "db.change.deny.user." + i;
      String denyUrlKey = "db.change.deny.url." + i;
      //note, not short circuit OR since needs to evaluate both
      foundOne = assertAndRemove(GROUPER_PROPERTIES_NAME, dbChangeKeys, new String[]{allowUserKey, allowUrlKey})
        | assertAndRemove(GROUPER_PROPERTIES_NAME, dbChangeKeys, new String[]{denyUserKey, denyUrlKey});
      if (!foundOne) {
        break;
      }
      i++;
    }
    if (dbChangeKeys.size() > 0) {
      String error = "in property file: grouper.properties, these properties " +
          "are misspelled or non-sequential: " + GrouperUtil.setToString(dbChangeKeys);
      System.err.println("Grouper error: " + error);
      LOG.error(error);
    }
  }

  /**
   * if one there, then they all must be there, and remove, return true if found one
   * @param resourceName
   * @param set of properties that match this pattern
   * @param propertiesNames
   * @return true if found one
   */
  public static boolean assertAndRemove(String resourceName, 
      Set<String> set, String[] propertiesNames) {
    boolean foundOne = false;
    for (String propertyName : propertiesNames) {
      if (set.contains(propertyName)) {
        foundOne = true;
        break;
      }
    }
    if (foundOne) {
      for (String propertyName : propertiesNames) {
        if (set.contains(propertyName)) {
          set.remove(propertyName);
        } else {
          String error = "expecting property " + propertyName 
            + " in config file: " + resourceName + " since related properties exist";
          System.err.println("Grouper error: " + error);
          LOG.error(error);
        }
      }
    }
    return foundOne;
  }
  
  /**
   * find all keys with a certain pattern in a properties file.
   * return the keys.  if none, will return the empty set, not null set
   * @param resourceName
   * @param pattern
   * @return the keys.  if none, will return the empty set, not null set
   */
  public static Set<String> retrievePropertiesKeys(String resourceName, Pattern pattern) {
    Properties properties = GrouperUtil.propertiesFromResourceName(resourceName);
    Set<String> result = new LinkedHashSet<String>();
    for (String key: (Set<String>)(Object)properties.keySet()) {
      if (pattern.matcher(key).matches()) {
        result.add(key);
      }
    }
    return result;
  }
  
  /**
   * make sure grouper versions match up
   */
  private static void checkGrouperVersion() {
    //grouper version must match in grouper.version.properties,
    //the manifest, and GrouperVersion class
    String grouperVersionFromClass = GrouperVersion.GROUPER_VERSION;
    String grouperVersionFromProperties = null;
    String grouperManifestVersion = null;
    try {
      Properties properties = GrouperUtil.propertiesFromResourceName("grouper.version.properties");
      grouperVersionFromProperties = GrouperUtil.propertiesValue(properties, "version");
      grouperManifestVersion = jarVersion(GrouperCheckConfig.class);
      
    } catch (Exception e) {
      
    }
    if (!StringUtils.equals(grouperVersionFromClass, grouperVersionFromProperties)
        || !StringUtils.equals(grouperVersionFromClass, grouperManifestVersion)) {
      if (grouperVersionFromProperties == null || grouperManifestVersion == null) {
        File jarFile = jarFile(GrouperCheckConfig.class);
        if (jarFile == null || !jarFile.exists() || jarFile.isDirectory()) {
          return;
        }
      }
      String error = "grouper versions do not match, GrouperVersion.class: " + grouperVersionFromClass
        + ", grouper.version.properties: " + grouperVersionFromProperties
        + ", manifest: " + grouperManifestVersion;
      System.out.println("Grouper error: " + error);
      LOG.error(error);
    }
  }
  
  /**
   * make sure grouper jars are ok
   */
  private static void checkGrouperJars() {
    //NOTE, START THIS IS GENERATED BY GrouperCheckconfig.main()
    checkJar("activation.jar", 54665, "javax.activation.ActivationDataFlavor", "1.0.2");
    checkJar("ant.jar", 3323026, "org.apache.tools.ant.AntClassLoader", "1.7.1");
    checkJar("antlr.jar", 443330, "antlr.actions.cpp.ActionLexer", "2.7.6");
    checkJar("asm-attrs.jar", 16777, "org.objectweb.asm.attrs.Annotation", "1.5.3");
    checkJar("asm-util.jar", 32684, "org.objectweb.asm.util.ASMifierClassVisitor", "1.5.3");
    checkJar("asm.jar", 26360, "org.objectweb.asm.Attribute", "1.5.3");
    checkJar("backport-util-concurrent.jar", 328268, "edu.emory.mathcs.backport.java.util.AbstractCollection", "3.0");
    checkJar("bsh.jar", 281694, "bsh.BSHFormalComment", "2.0b4 2005-05-23 11:49:20");
    checkJar("c3p0.jar", 1064264, "com.mchange.lang.ByteUtils", "0.9.1.2");
    checkJar("cglib.jar", 454154, "net.sf.cglib.beans.BeanCopier", "2.1.3");
    checkJar("commons-beanutils.jar", 173783, "org.apache.commons.beanutils.BasicDynaBean", "0.1.0");
    checkJar("commons-betwixt.jar", 242227, "org.apache.commons.betwixt.expression.MethodExpression", "0.8");
    checkJar("commons-cli.jar", 36174, "org.apache.commons.cli.AlreadySelectedException", "1.1");
    checkJar("commons-collections.jar", 570463, "org.apache.commons.collections.ArrayStack", "0.1.0");
    checkJar("commons-digester.jar", 136649, "org.apache.commons.digester.AbstractObjectCreationFactory", "0.1.0");
    checkJar("commons-discovery.jar", 76685, "org.apache.commons.discovery.ant.ServiceDiscoveryTask", "0.4");
    checkJar("commons-lang.jar", 468109, "org.apache.commons.lang.ArrayUtils", "2.1");
    checkJar("commons-logging.jar", 131078, "org.apache.commons.logging.impl.AvalonLogger", "1.1.1");
    checkJar("commons-math.jar", 174535, "org.apache.commons.math.distribution.ExponentialDistributionImpl", "1.1");
    checkJar("DdlUtils.jar", 713153, "org.apache.ddlutils.alteration.AddColumnChange", "1.0");
    checkJar("dom4j.jar", 312668, "org.dom4j.Attribute", "0.1.0");
    checkJar("ehcache.jar", 527332, "net.sf.ehcache.bootstrap.BootstrapCacheLoader", "1.4.0");
    checkJar("hibernate.jar", 3770617, "org.hibernate.action.BulkOperationCleanupAction", "3.2.6.ga");
    checkJar("invoker.jar", 27767, "com.dawidweiss.invoker.Invoker", "1.0");
    checkJar("jakarta-oro.jar", 65261, "org.apache.oro.io.AwkFilenameFilter", "2.0.8 2003-12-28 11:00:13");
    checkJar("jamon.jar", 280580, "com.jamonapi.aop.JAMonEJBInterceptor", "JAMon 2.7");
    checkJar("jsr107cache.jar", 8302, "net.sf.jsr107cache.Cache", "1.0");
    checkJar("jta.jar", 8374, "javax.transaction.HeuristicCommitException", "1.0.1B");
    checkJar("jug.jar", 19091, "com.ccg.net.ethernet.BadAddressException", "1.1.1");
    checkJar("log4j.jar", 352668, "org.apache.log4j.Appender", "1.2.8");
    checkJar("mailapi.jar", 178533, "javax.mail.Address", "1.3.2");
    checkJar("morphString.jar", 74805, "edu.internet2.middleware.morphString.Encrypt", "1.0");
    checkJar("odmg.jar", 42111, "org.odmg.ClassNotPersistenceCapableException", "0.1.0");
    checkJar("p6spy.jar", 388726, "com.p6spy.engine.common.FastExternalUtils", "1.0");
    checkJar("quartz.jar", 792769, "org.quartz.Calendar", "1.6.0");
    checkJar("smtp.jar", 23567, "com.sun.mail.smtp.DigestMD5", "1.3.2");
    checkJar("subject.jar", 91768, "edu.internet2.middleware.subject.InvalidQueryException", "0.4.2");
    //NOTE, END THIS IS GENERATED BY GrouperCheckconfig.main()

  }
  
  /**
   * generate the jars to find
   * @param args 
   * @throws Exception 
   */
  public static void main(String[] args) throws Exception {
    generateCheckJars();
  }

  /**
   * @throws IOException
   * @throws ClassNotFoundException
   * @throws Exception
   */
  private static void generateCheckJars() throws IOException, ClassNotFoundException,
      Exception {
    //find resources dir
    File log4jFile = GrouperUtil.fileFromResourceName("log4j.properties");
    File confDir = log4jFile.getParentFile();
    File grouperDir = confDir.getParentFile();
    File libDir = new File(GrouperUtil.fileCanonicalPath(grouperDir) + File.separator + "lib");
    if (!libDir.exists()) {
      throw new RuntimeException("Cant find lib dir: " + libDir.getCanonicalPath());
    }
    File grouperLibDir = new File(libDir.getCanonicalPath() + File.separator + "grouper");
    if (!grouperLibDir.exists()) {
      throw new RuntimeException("Cant find grouper lib dir: " + grouperLibDir.getCanonicalPath());
    }
    for (File file : grouperLibDir.listFiles()) {
      if (file.getCanonicalPath().endsWith(".jar")) {
        
        JarFile jarFile = new JarFile(file);
        Class sampleClass = null;
        Enumeration<JarEntry> enumeration = jarFile.entries();
        while (enumeration.hasMoreElements()) {
          JarEntry jarEntry = enumeration.nextElement();
          String jarEntryName = jarEntry.getName();
          if (jarEntryName.endsWith(".class") && !jarEntryName.contains("$")) {
            String className = jarEntryName.substring(0, jarEntryName.length()-6);
            className = className.replace('/', '.');
            className = className.replace('\\', '.');
            Class tempClass = Class.forName(className);
            if (Modifier.isPublic(tempClass.getModifiers())) {
              sampleClass = tempClass;
              break;
            }
          }
        }
        String version = jarVersion(sampleClass);
        System.out.println("    checkJar(\"" + file.getName() + "\", " + file.length() 
            + ", \"" + sampleClass.getName() + "\", \"" + version + "\");");
      }
    }
  }
  
  /** properties in manifest for version */
  private static final String[] versionProperties = new String[]{
    "Implementation-Version","Version"};
  
  /**
   * get the version from the manifest of a jar
   * @param sampleClass
   * @return the version
   * @throws Exception
   */
  public static String jarVersion(Class sampleClass) throws Exception {
    return manifestProperty(sampleClass, versionProperties);
  }

  /**
   * get the version from the manifest of a jar
   * @param sampleClass
   * @param propertyNames that we are looking for (usually just one)
   * @return the version
   * @throws Exception
   */
  public static String manifestProperty(Class sampleClass, String[] propertyNames) throws Exception {
    File jarFile = jarFile(sampleClass);
    URL manifestUrl = new URL("jar:file:" + jarFile.getCanonicalPath() + "!/META-INF/MANIFEST.MF");
    Manifest manifest = new Manifest(manifestUrl.openStream());
    Map<String, Attributes> attributeMap = manifest.getEntries();
    String value = null;
    for (String propertyName : propertyNames) {
      value = manifest.getMainAttributes().getValue(propertyName);
      if (!StringUtils.isBlank(value)) {
        break;
      }
    }
    if (value == null) {
      OUTER:
      for (Attributes attributes: attributeMap.values()) {
        for (String propertyName : propertyNames) {
          value = attributes.getValue(propertyName);
          if (!StringUtils.isBlank(value)) {
            break OUTER;
          }
        }
      }
    }
    if (value == null) {
      
      for (Attributes attributes: attributeMap.values()) {
        for (Object key : attributes.keySet()) {
          LOG.info(jarFile.getName() + ", " + key + ": " + attributes.getValue((Name)key));
        }
      }
      Attributes attributes = manifest.getMainAttributes();
      for (Object key : attributes.keySet()) {
        LOG.info(jarFile.getName() + ", " + key + ": " + attributes.getValue((Name)key));
      }
    }
    return value;
  }

  /** match something like this: db.change.allow.url.1 */
  private static Pattern dbChangePattern = Pattern.compile(
      "^db\\.change\\.(deny|allow)\\.(user|url).\\d+$");
  
  /** match something like this: group.attribute.validator.attributeName.0 */
  private static Pattern groupValidatorPattern = Pattern.compile(
      "^group\\.attribute\\.validator\\.(attributeName|regex|vetoMessage)\\.\\d+$");
  
  /**
   * match something like this: db.warehouse.pass
   */
  private static Pattern grouperLoaderDbPattern = Pattern.compile(
      "^db\\.(\\w+)\\.(pass|url|driver|user)$");
  
  /**
   * return true if this is an exception case, dont worry about it
   * @param resourceName
   * @param propertyName
   * @param missingPropertyInFile true if missing property in file, false if
   * extra property in file
   * @return true if exception case
   */
  public static boolean nonStandardProperty(String resourceName, String propertyName,
      boolean missingPropertyInFile) {
    if (StringUtils.equals(resourceName, GROUPER_PROPERTIES_NAME)) {
      if (dbChangePattern.matcher(propertyName).matches()) {
        return true;
      }
      if (groupValidatorPattern.matcher(propertyName).matches()) {
        return true;
      }
    }
    if (StringUtils.equals(resourceName, "grouper.hibernate.properties")
      || !missingPropertyInFile) {
      return true;
    }
    if (StringUtils.equals(resourceName, "grouper-loader.properties")) {
      if (grouperLoaderDbPattern.matcher(propertyName).matches()) {
        return true;
      }
    }
    
    return false;
  }
  
  /**
   * compare a properties file with an example file, compare all the properties
   * @param resourceName
   * @param resourceExampleName
   */
  public static void checkConfigProperties(String resourceName, 
      String resourceExampleName) {
    
    Properties propertiesFromFile = GrouperUtil.propertiesFromResourceName(resourceName);
    Properties propertiesFromExample = GrouperUtil.propertiesFromResourceName(resourceExampleName);
    String exampleFileContents = GrouperUtil.readResourceIntoString(resourceExampleName, false);
    
    //find properties missing from file:
    Set<String> missingProps = new HashSet<String>();
    for (String key: (Set<String>)(Object)propertiesFromExample.keySet()) {
      if (!propertiesFromFile.containsKey(key)) {
        if (!nonStandardProperty(resourceName, key, true)) {
          missingProps.add(key);
        }
      }
    }
    if (missingProps.size() > 0) {
      String error = "missing from file: " + resourceName + ", the following " +
      		"properties (which are in the example file: " + resourceExampleName
      		+ "): " + GrouperUtil.setToString(missingProps);
      System.err.println("Grouper warning: " + error);
      LOG.warn(error);
    }
    
    //find extra properties in file:
    missingProps.clear();
    for (String key: (Set<String>)(Object)propertiesFromFile.keySet()) {
      //dont look in properties, look in file, since could be commented out
      if (!exampleFileContents.contains(key)) {
        if (!nonStandardProperty(resourceName, key, false)) {
          missingProps.add(key);
        }
      }
    }
    if (missingProps.size() > 0) {
      String error = "properties are in file: " + resourceName + " (but not in " +
      		"the example file: " + resourceExampleName
          + "): " + GrouperUtil.setToString(missingProps);
      System.err.println("Grouper warning: " + error);
      LOG.warn(error);
    }
  }
  
  /**
   * get a jar file from a sample class
   * @param sampleClass
   * @return the jar file
   */
  public static File jarFile(Class sampleClass) {
    try {
      CodeSource codeSource = sampleClass.getProtectionDomain().getCodeSource();
      if (codeSource != null && codeSource.getLocation() != null) {
        return new File(codeSource.getLocation().getFile());
      }
      String resourcePath = sampleClass.getName();
      resourcePath = resourcePath.replace('.', '/') + ".class";
      URL url = GrouperUtil.computeUrl(resourcePath, true);
      String urlPath = url.toString();
      
      if (urlPath.startsWith("jar:")) {
        urlPath = urlPath.substring(4);
      }
      if (urlPath.startsWith("file:")) {
        urlPath = urlPath.substring(5);
      }
      urlPath = GrouperUtil.prefixOrSuffix(urlPath, "!", true); 

      File file = new File(urlPath);
      if (urlPath.endsWith(".jar") && file.exists() && file.isFile()) {
        return file;
      }
    } catch (Exception e) {
      e.printStackTrace();
      LOG.debug("Cant find jar for class: " + sampleClass + ", " + e.getMessage());
    }
    return null;
  }
  
}
