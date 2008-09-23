/*--
$Id: JDBCSourceAdapter.java,v 1.10 2008-09-16 05:12:09 mchyzer Exp $
$Date: 2008-09-16 05:12:09 $
 
Copyright 2005 Internet2 and Stanford University.  All Rights Reserved.
See doc/license.txt in this distribution.
 */
package edu.internet2.middleware.subject.provider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.internet2.middleware.morphString.Morph;
import edu.internet2.middleware.subject.InvalidQueryRuntimeException;
import edu.internet2.middleware.subject.SourceUnavailableException;
import edu.internet2.middleware.subject.Subject;
import edu.internet2.middleware.subject.SubjectNotFoundException;
import edu.internet2.middleware.subject.SubjectNotUniqueException;
import edu.internet2.middleware.subject.SubjectUtils;


/**
 * JDBC Source
 */
public class JDBCSourceAdapter
        extends BaseSourceAdapter {
    
  /** logger */
	private static Log log = LogFactory.getLog(JDBCSourceAdapter.class);
	
  /** name attribute name */
	protected String nameAttributeName;
  
  /** subject id attribute name */
	protected String subjectIDAttributeName;

  /** decsription attribute name */
	protected String descriptionAttributeName;
  
  /** subject type string */
	protected String subjectTypeString;

	/** keep a reference to the object which gets our connections for us */
	private JdbcConnectionProvider jdbcConnectionProvider = null;
	
    /**
     * Allocates new JDBCSourceAdapter;
     */
    public JDBCSourceAdapter() {
        super();
    }
    
    /**
     * Allocates new JDBCSourceAdapter;
     * 
     * @param id
     * @param name
     */
    public JDBCSourceAdapter(String id, String name) {
        super(id, name);
    }
    
    /**
     * {@inheritDoc}
     */
    public Subject getSubject(String id)
    throws SubjectNotFoundException,SubjectNotUniqueException {
        return uniqueSearch(id, "searchSubject");
    }
    
    /**
     * {@inheritDoc}
     */
    public Subject getSubjectByIdentifier(String id)
    throws SubjectNotFoundException, SubjectNotUniqueException  {
        return uniqueSearch(id, "searchSubjectByIdentifier");
    }
    
    /**
     * Perform a search for a unique subject.
     * 
     * @param id
     * @param searchType
     * @return subject
     * @throws SubjectNotFoundException
     * @throws SubjectNotUniqueException
     * @throws InvalidQueryRuntimeException 
     */
    private Subject uniqueSearch(String id, String searchType)
    throws SubjectNotFoundException, SubjectNotUniqueException, InvalidQueryRuntimeException  {
        Subject subject = null;
        Search search = getSearch(searchType);
        if (search == null) {
            log.error("searchType: \"" + searchType + "\" not defined.");
            return subject;
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        JdbcConnectionBean jdbcConnectionBean = null;
        try {
            jdbcConnectionBean = this.jdbcConnectionProvider.connectionBean();
            conn = jdbcConnectionBean.connection();
            stmt = prepareStatement(search, conn);
            ResultSet rs = getSqlResults(id, stmt, search);
            subject = createUniqueSubject(rs, search,  id, search.getParam("sql"));
            jdbcConnectionBean.doneWithConnection();
        } catch (SQLException ex) {
          try {
            jdbcConnectionBean.doneWithConnectionError(ex);
          } catch (RuntimeException e) {
            log.error(e);
          }
          log.error(ex.getMessage() + ", source: " + this.getId() + ", sql: " + search.getParam("sql"), ex);
        } finally {
            closeStatement(stmt);
            if (jdbcConnectionBean != null) {
              jdbcConnectionBean.doneWithConnectionFinally();
            }
        }
        if (subject == null) {
            throw new SubjectNotFoundException("Subject " + id + " not found.");
        }
        return subject;
    }

    /**
     * {@inheritDoc}
     */
    public Set<Subject> search(String searchValue) {
        Set result = new HashSet();
        Search search = getSearch("search");
        if (search == null) {
            log.error("searchType: \"search\" not defined.");
            return result;
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        JdbcConnectionBean jdbcConnectionBean = null;
        try {
            jdbcConnectionBean = this.jdbcConnectionProvider.connectionBean();
            conn = jdbcConnectionBean.connection();
            stmt = prepareStatement(search, conn);
            ResultSet rs = getSqlResults(searchValue, stmt, search);
            if (rs==null) {
                return result;
            }
            while (rs.next()) {
                Subject subject = createSubject(rs, search.getParam("sql"));
                result.add(subject);
            }
            jdbcConnectionBean.doneWithConnection();
        } catch (InvalidQueryRuntimeException nqe) {
          try {
            jdbcConnectionBean.doneWithConnectionError(nqe);
          } catch (RuntimeException re) {
            log.error(re);
          }
          //shouldnt this throw???
          log.error(nqe.getMessage() + ", source: " + this.getId() + ", sql: " + search.getParam("sql"), nqe);
        } catch (SQLException ex) {
          try {
            jdbcConnectionBean.doneWithConnectionError(ex);
          } catch (RuntimeException re) {
            log.error(re);
          }
          log.error(ex.getMessage() + ", source: " + this.getId() + ", sql: " + search.getParam("sql"), ex);
        } finally {
            closeStatement(stmt);
            if (jdbcConnectionBean != null) {
              jdbcConnectionBean.doneWithConnectionFinally();
            }
        }
        return result;
    }
    
    /**
     * Create a subject from the current row in the resultSet
     * 
     * @param rs
     * @param sql 
     * @return subject
     * @throws SQLException 
     */
    private Subject createSubject(ResultSet rs, String sql) throws SQLException {
        String name = "";
        String subjectID = "";
        String description = "";
        JDBCSubject subject = null;
        try {
          subjectID = retrieveString(rs, subjectIDAttributeName, "SubjectID_AttributeType", sql);
          name = retrieveString(rs, nameAttributeName, "Name_AttributeType", sql);
          if (!descriptionAttributeName.equals("")) {
            description = retrieveString(rs, descriptionAttributeName, "Description_AttributeType", sql);
          }
          Map attributes = loadAttributes(rs);
          subject = new JDBCSubject(subjectID,name, description, this.getSubjectType(), this, attributes);
        } catch (SQLException ex) {
            log.error("SQLException occurred: " + ex.getMessage() + ": " + sql, ex);
        }    
        return subject;
    }
    
    /**
     * retrieve a param from resultset
     * @param rs
     * @param name
     * @param varName 
     * @param sql
     * @return the value
     * @throws SQLException 
     */
    private String retrieveString(ResultSet rs, String name, String varName, String sql) throws SQLException {
      try {
        String result = rs.getString(name);
        return result;
      } catch (SQLException se) {
      SubjectUtils.injectInException(se, "Error retrieving column name: '" + name + "' in source: " + this.getId() 
            + ", in query: " + sql + ", " + se.getMessage() 
            + ", maybe the column configured in " + varName + " does not exist as a query column");
        throw se;

      }
    }
    
    /**
     * Create a unique subject from the resultSet.
     * 
     * @param rs
     * @param search
     * @param searchValue
     * @param sql 
     * @return subject
     * @throws SubjectNotFoundException
     * @throws SubjectNotUniqueException
     * @throws SQLException 
     */
    private Subject createUniqueSubject(ResultSet rs, Search search, String searchValue, String sql)
    throws SubjectNotFoundException,SubjectNotUniqueException, SQLException {
        Subject subject =null;
        try {
          if (rs == null || !rs.next()) {
            String errMsg = "No results: " + search.getSearchType() +
                    " searchValue: " + searchValue;
            throw new SubjectNotFoundException( errMsg);
          }
          subject = createSubject(rs, sql);
          if (rs.next()) {
            String errMsg ="Search is not unique:" + rs.getString(subjectIDAttributeName) + "\n";
            throw new SubjectNotUniqueException( errMsg );
          }
        } catch (SQLException ex) {
            log.error("SQLException occurred: " + ex.getMessage() + ": " + sql, ex);
        } 
        return subject;
        
    }
    
    /**
     * Prepare a statement handle from the search object.
     * 
     * @param search
     * @param conn
     * @return the prepared statement
     * @throws InvalidQueryRuntimeException
     * @throws SQLException
     */
    protected PreparedStatement prepareStatement(Search search, Connection conn)
    throws InvalidQueryRuntimeException, SQLException {
        String sql = search.getParam("sql");
        if (sql == null) {
          throw new InvalidQueryRuntimeException("No sql parameter for search type " + search.getSearchType() + ", source: " + this.getId());
        }
        if (sql.contains("%TERM%")) {
          throw new InvalidQueryRuntimeException("%TERM%. Possibly old style SQL query, source: " + this.getId() + ", sql: " + sql);
        }
        if (search.getParam("numParameters") == null) {
            throw new InvalidQueryRuntimeException("No numParameters parameter specified, source: " + this.getId() + ", sql: " + sql);
        }
        try {
            Integer.parseInt(search.getParam("numParameters"));
        } catch (NumberFormatException e) {
            throw new InvalidQueryRuntimeException("Non-numeric numParameters parameter specified, source: " + this.getId() + ", sql: " + sql);
        }
        PreparedStatement stmt = conn.prepareStatement(sql);
        return stmt;
    }
    
    /**
     * Set the parameters in the prepared statement and execute the query.
     * 
     * @param searchValue
     * @param stmt
     * @param search
     * @return resultSet
     * @throws SQLException 
     */
    protected ResultSet getSqlResults(String searchValue, PreparedStatement stmt, Search search) 
           throws SQLException {
        ResultSet rs = null;
        for (int i = 1; i <= Integer.parseInt(search.getParam("numParameters")); i++) {
          try {
            stmt.setString(i, searchValue);
          } catch (SQLException e) {
            SubjectUtils.injectInException(e, "Error setting param: " + i + " in source: " + this.getId() + ", in query: " + search.getParam("sql") + ", " + e.getMessage() 
                + ", maybe not enough question marks (bind variables) are in query, or the param 'numParameters' in sources.xml for that query is incorrect");
            throw e;
          }
        }
        rs = stmt.executeQuery();
        return rs;
    }

    /**
     * Loads attributes for the argument subject.
     * @param rs 
     * @return attributes
     * @throws SQLException 
     */
    protected Map loadAttributes(ResultSet rs) throws SQLException {
        Map attributes = new HashMap();
        try {
        ResultSetMetaData rsmd = rs.getMetaData();
        int colCount = rsmd.getColumnCount();
        String[] colNames = new String[colCount];
        for (int i=0; i < colCount; i++) {
            colNames[i] = rsmd.getColumnName(i+1);
        }
        for (int i=0; i < colCount; i++ ) {
            String name = colNames[i];
            if (name.toLowerCase().equals(subjectIDAttributeName.toLowerCase())) {
                continue;
            }
            if (name.toLowerCase().equals(nameAttributeName.toLowerCase())) {
                continue;
            }
            if (name.toLowerCase().equals(descriptionAttributeName.toLowerCase())){
                continue;
            }
            String value = rs.getString(i+1);
            Set values = (Set)attributes.get(name);
            if (values == null) {
                values = new HashSet();
                attributes.put(name, values);
            }
            values.add(value);
          }
        } catch (SQLException ex) {
            log.error("SQLException occurred: " + ex.getMessage(), ex);
        }

        return attributes;
    }
    
    /**
     * {@inheritDoc}
     */
    public void init()
    throws SourceUnavailableException {
        try {
            Properties props = getInitParams();
            //this might not exist if it is Grouper source and no driver...
            setupDataSource(props);
        } catch (Exception ex) {
            throw new SourceUnavailableException(
                    "Unable to init JDBC source, source: " + this.getId(), ex);
        }
    }
    
    /**
     * Loads the the JDBC driver.
     * @param sourceId 
     * @param driver 
     * @throws SourceUnavailableException 
     */
    public static void loadDriver(String sourceId, String driver)
    throws SourceUnavailableException {
        try {
            Class.forName(driver).newInstance();
            log.debug("Loading JDBC driver: " + driver);
        } catch (Exception ex) {
            throw new SourceUnavailableException(
                    "Error loading JDBC driver: " + driver + ", source: " + sourceId, ex);
        }
        log.info("JDBC driver loaded.");
    }
    
    /**
     * DataSource connection pool setup.
     * @param props 
     * @throws SourceUnavailableException
     */
    @SuppressWarnings("unchecked")
    protected void setupDataSource(Properties props)
        throws SourceUnavailableException {
        
      String driver = props.getProperty("dbDriver");

      //default is 2
      String maxActiveString = props.getProperty("maxActive");
      Integer maxActive = StringUtils.isBlank(maxActiveString) ? null : Integer.parseInt(maxActiveString);

      //default is 2
      String maxIdleString = props.getProperty("maxIdle");
      Integer maxIdle = StringUtils.isBlank(maxIdleString) ? null : Integer.parseInt(maxIdleString);

      //default is 5
      String maxWaitString = props.getProperty("maxWait");
      Integer maxWaitSeconds = StringUtils.isBlank(maxWaitString) ? null : Integer.parseInt(maxWaitString);
      
      String dbUrl = null;
      log.debug("Initializing connection factory.");
      dbUrl = props.getProperty("dbUrl");
      String dbUser = props.getProperty("dbUser");
      String dbPwd = props.getProperty("dbPwd");
      dbPwd = Morph.decryptIfFile(dbPwd);
      
      //defaults to true
      Boolean readOnly = SubjectUtils.booleanObjectValue(props.getProperty("readOnly"));
      
      String jdbcConnectionProviderString = SubjectUtils.defaultIfBlank(
          props.getProperty("jdbcConnectionProvider"), C3p0JdbcConnectionProvider.class.getName());
      Class<JdbcConnectionProvider> jdbcConnectionProviderClass = null;
      try {
        jdbcConnectionProviderClass = SubjectUtils.forName(jdbcConnectionProviderString);
      } catch (RuntimeException re) {
        SubjectUtils.injectInException(re, "Valid built-in options are: " 
            + C3p0JdbcConnectionProvider.class.getName() + " (default) [note: its a zero, not a capital O], " 
            + DbcpJdbcConnectionProvider.class.getName() 
            + ", edu.internet2.middleware.grouper.subj.GrouperJdbcConnectionProvider (if using Grouper).  " 
            + "Note, these are the built-ins for the Subject API or Grouper, there might be other valid choices." );
        throw re;
      }
      
      this.jdbcConnectionProvider = SubjectUtils.newInstance(jdbcConnectionProviderClass);
      this.jdbcConnectionProvider.init(this.getId(), driver, maxActive, 2, maxIdle, 2, maxWaitSeconds, 5, 
          dbUrl, dbUser, dbPwd, readOnly, true);

      log.info("Data Source initialized.");
      nameAttributeName = props.getProperty("Name_AttributeType");
      if (nameAttributeName==null) {
          throw new SourceUnavailableException("Name_AttributeType not defined, source: " + this.getId());
      }
      subjectIDAttributeName = props.getProperty("SubjectID_AttributeType");
      if (subjectIDAttributeName==null) {
        throw new SourceUnavailableException("SubjectID_AttributeType not defined, source: " + this.getId());
      }
      descriptionAttributeName = props.getProperty("Description_AttributeType");
      if (descriptionAttributeName==null) {
        throw new SourceUnavailableException("Description_AttributeType not defined, source: " + this.getId());
      }
    }
    
    /**
     * 
     * @param stmt
     */
    protected void closeStatement(PreparedStatement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.info("Error while closing JDBC Statement.");
            }
        }
    }

	/**
	 * @return the descriptionAttributeName
	 */
	public String getDescriptionAttributeName() {
		return descriptionAttributeName;
	}

	/**
	 * @return the nameAttributeName
	 */
	public String getNameAttributeName() {
		return nameAttributeName;
	}

	/**
	 * @return the subjectIDAttributeName
	 */
	public String getSubjectIDAttributeName() {
		return subjectIDAttributeName;
	}

	/**
	 * @return the subjectTypeString
	 */
	public String getSubjectTypeString() {
		return subjectTypeString;
	}
}