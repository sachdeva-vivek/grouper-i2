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
import java.io.Serializable;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import edu.internet2.middleware.grouper.annotations.GrouperIgnoreClone;
import edu.internet2.middleware.grouper.annotations.GrouperIgnoreDbVersion;
import edu.internet2.middleware.grouper.annotations.GrouperIgnoreFieldConstant;
import edu.internet2.middleware.grouper.exception.SchemaException;
import edu.internet2.middleware.grouper.hibernate.HibernateSession;
import edu.internet2.middleware.grouper.hooks.FieldHooks;
import edu.internet2.middleware.grouper.hooks.beans.HooksFieldBean;
import edu.internet2.middleware.grouper.hooks.logic.GrouperHookType;
import edu.internet2.middleware.grouper.hooks.logic.GrouperHooksUtils;
import edu.internet2.middleware.grouper.hooks.logic.VetoTypeGrouper;
import edu.internet2.middleware.grouper.internal.dao.hib3.Hib3GrouperVersioned;
import edu.internet2.middleware.grouper.misc.GrouperDAOFactory;
import edu.internet2.middleware.grouper.privs.Privilege;
import edu.internet2.middleware.grouper.util.GrouperUtil;


/** 
 * Schema specification for a Group attribute or list.
 * <p/>
 * @author  blair christensen.
 * @version $Id: Field.java,v 1.31 2008-07-28 20:12:28 mchyzer Exp $    
 */
public class Field extends GrouperAPI implements Hib3GrouperVersioned {

  /** table name for fields */
  public static final String TABLE_GROUPER_FIELDS = "grouper_fields";
  
  /** uuid col in db */
  public static final String COLUMN_FIELD_UUID = "field_uuid";
  
  /** old id col for id conversion */
  public static final String COLUMN_OLD_ID = "old_id";
  
  /** old uuid id col for id conversion */
  public static final String COLUMN_OLD_FIELD_UUID = "old_field_uuid";
  
  
  //*****  START GENERATED WITH GenerateFieldConstants.java *****//

  /** constant for field name for: dbVersion */
  public static final String FIELD_DB_VERSION = "dbVersion";

  /** constant for field name for: groupTypeUUID */
  public static final String FIELD_GROUP_TYPE_UUID = "groupTypeUUID";

  /** constant for field name for: isNullable */
  public static final String FIELD_IS_NULLABLE = "isNullable";

  /** constant for field name for: name */
  public static final String FIELD_NAME = "name";

  /** constant for field name for: readPrivilege */
  public static final String FIELD_READ_PRIVILEGE = "readPrivilege";

  /** constant for field name for: type */
  public static final String FIELD_TYPE = "type";

  /** constant for field name for: uuid */
  public static final String FIELD_UUID = "uuid";

  /** constant for field name for: writePrivilege */
  public static final String FIELD_WRITE_PRIVILEGE = "writePrivilege";

  /**
   * fields which are included in db version
   */
  private static final Set<String> DB_VERSION_FIELDS = GrouperUtil.toSet(
      FIELD_GROUP_TYPE_UUID, FIELD_IS_NULLABLE, FIELD_NAME, FIELD_READ_PRIVILEGE, 
      FIELD_TYPE, FIELD_UUID, FIELD_WRITE_PRIVILEGE);

  /**
   * fields which are included in clone method
   */
  private static final Set<String> CLONE_FIELDS = GrouperUtil.toSet(
      FIELD_DB_VERSION, FIELD_GROUP_TYPE_UUID, FIELD_HIBERNATE_VERSION_NUMBER, FIELD_IS_NULLABLE, 
      FIELD_NAME, FIELD_READ_PRIVILEGE, FIELD_TYPE, FIELD_UUID, 
      FIELD_WRITE_PRIVILEGE);

  //*****  END GENERATED WITH GenerateFieldConstants.java *****//

  @GrouperIgnoreDbVersion
  @GrouperIgnoreFieldConstant
  @GrouperIgnoreClone
  private GroupType cachedGroupType   = null;
  // PRIVATE INSTANCE VARIABLES //
  private String    groupTypeUUID;
  private boolean   isNullable;
  private String    name;
  private String    readPrivilege;
  private String    type;
  private String    uuid;
  private String    writePrivilege;
  public  static final  long      serialVersionUID  = 2072790175332537149L;


  // PUBLIC INSTANCE METHODS //

  /**
   */
  public GroupType getGroupType() 
    throws  IllegalStateException
  {
    if ( this.cachedGroupType == null ) {
      try {
        GroupType type = GrouperDAOFactory.getFactory().getGroupType().findByUuid( this.getGroupTypeUuid() ) ;
        this.cachedGroupType = type;
      }
      catch (SchemaException eS) {
        throw new IllegalStateException( "unable to fetch GroupType: " + eS.getMessage() );
      }
    }
    return this.cachedGroupType;
  } 

  /**
   */
  public FieldType getType() {
    return FieldType.getInstance( this.getTypeString() );
  } // public FieldType getType()

  /**
   */
  public Privilege getReadPriv() {
    return Privilege.getInstance( this.getReadPrivilege() ); 
  } // public Privilege getReadPriv()

  /**
   */
  public boolean getRequired() {
    return !this.getIsNullable();
  } // public boolean isRequired()

  /**
   */
  public Privilege getWritePriv() {
    return Privilege.getInstance( this.getWritePrivilege() );
  } // public Privilege getWritePriv()

  /**
   * @since   1.2.0
   */
  public boolean equals(Object other) {
    if (this == other) { 
      return true;
    }
    if ( !(other instanceof Field) ) {
      return false;
    }
    Field that = (Field) other;
    return new EqualsBuilder()
      .append( this.getUuid(), that.getUuid() )
     .isEquals();
  } // public boolean equals(other)

  /**
   * @since   1.2.0
   */
  public String getGroupTypeUuid() {
    return this.groupTypeUUID;
  }

  /**
   * @since   1.2.0
   */
  public boolean getIsNullable() {
    return this.isNullable;
  }

  /**
   * @since   1.2.0
   */
  public String getName() {
    return this.name;
  }

  /**
   * @since   1.2.0
   */
  public String getReadPrivilege() {
    return this.readPrivilege;
  }

  /**
   * @since   1.2.0
   */
  public String getTypeString() {
    return this.type;
  }

  /**
   * @since   1.2.0
   */
  public String getUuid() {
    return this.uuid;
  }

  /**
   * @since   1.2.0
   */
  public String getWritePrivilege() {
    return this.writePrivilege;
  }

  /**
   * @since   1.2.0
   */
  public int hashCode() {
    return new HashCodeBuilder()
      .append( this.getUuid() )
      .toHashCode();
  } // public int hashCode()

  /**
   * @since   1.2.0
   */
  public void setGroupTypeUuid(String groupTypeUUID) {
    this.groupTypeUUID = groupTypeUUID;
  }

  /**
   * @since   1.2.0
   */
  public void setIsNullable(boolean isNullable) {
    this.isNullable = isNullable;
  }

  /**
   * @since   1.2.0
   */
  public void setName(String name) {
    this.name = name;
  
  }

  /**
   * @since   1.2.0
   */
  public void setReadPrivilege(Privilege readPrivilege) {
    this.setReadPrivilege( readPrivilege.getName() );
  
  }

  /**
   * @since   1.2.0
   */
  public void setReadPrivilege(String readPrivilege) {
    this.readPrivilege = readPrivilege;
  
  }

  /**
   * @since   1.2.0
   */
  public void setType(FieldType type) {
    this.setTypeString( type.toString() );
  
  }

  /**
   * @since   1.2.0
   */
  public void setTypeString(String type) {
    this.type = type;
  
  }

  /**
   * @since   1.2.0
   */
  public void setUuid(String uuid) {
    this.uuid = uuid;
  
  }

  /**
   * @since   1.2.0
   */
  public void setWritePrivilege(Privilege writePrivilege) {
    this.setWritePrivilege( writePrivilege.getName() );
  
  }

  /**
   * @since   1.2.0
   */
  public void setWritePrivilege(String writePrivilege) {
    this.writePrivilege = writePrivilege;
  
  }

  /**
   * @since   1.2.0
   */
  public String toString() {
    return new ToStringBuilder(this)
      .append( "groupTypeUuid",  this.getGroupTypeUuid()  )
      .append( "isNullable",     this.getIsNullable()     )
      .append( "name",           this.getName()           )
      .append( "readPrivilege",  this.getReadPrivilege()  )
      .append( "type",           this.getType()           )
      .append( "uuid",           this.getUuid()           )
      .append( "writePrivilege", this.getWritePrivilege() )
      .toString();
  } // public String toString()

  /**
   * @see edu.internet2.middleware.grouper.GrouperAPI#onPostDelete(edu.internet2.middleware.grouper.hibernate.HibernateSession)
   */
  @Override
  public void onPostDelete(HibernateSession hibernateSession) {
    super.onPostDelete(hibernateSession);
    
    GrouperHooksUtils.schedulePostCommitHooksIfRegistered(this, GrouperHookType.FIELD, 
        FieldHooks.METHOD_FIELD_POST_COMMIT_DELETE, HooksFieldBean.class, 
        this, Field.class);

    GrouperHooksUtils.callHooksIfRegistered(this, GrouperHookType.FIELD, 
        FieldHooks.METHOD_FIELD_POST_DELETE, HooksFieldBean.class, 
        this, Field.class, VetoTypeGrouper.FIELD_POST_DELETE, false, true);
  }

  /**
   * @see edu.internet2.middleware.grouper.GrouperAPI#onPostSave(edu.internet2.middleware.grouper.hibernate.HibernateSession)
   */
  @Override
  public void onPostSave(HibernateSession hibernateSession) {
    super.onPostSave(hibernateSession);
    
    GrouperHooksUtils.schedulePostCommitHooksIfRegistered(this, GrouperHookType.FIELD, 
        FieldHooks.METHOD_FIELD_POST_COMMIT_INSERT, HooksFieldBean.class, 
        this, Field.class);

    GrouperHooksUtils.callHooksIfRegistered(this, GrouperHookType.FIELD, 
        FieldHooks.METHOD_FIELD_POST_INSERT, HooksFieldBean.class, 
        this, Field.class, VetoTypeGrouper.FIELD_POST_INSERT, true, false);
  }

  /**
   * @see edu.internet2.middleware.grouper.GrouperAPI#onPostUpdate(edu.internet2.middleware.grouper.hibernate.HibernateSession)
   */
  @Override
  public void onPostUpdate(HibernateSession hibernateSession) {
    super.onPostUpdate(hibernateSession);

    GrouperHooksUtils.schedulePostCommitHooksIfRegistered(this, GrouperHookType.FIELD, 
        FieldHooks.METHOD_FIELD_POST_COMMIT_UPDATE, HooksFieldBean.class, 
        this, Field.class);
    
    GrouperHooksUtils.callHooksIfRegistered(this, GrouperHookType.FIELD, 
        FieldHooks.METHOD_FIELD_POST_UPDATE, HooksFieldBean.class, 
        this, Field.class, VetoTypeGrouper.FIELD_POST_UPDATE, true, false);
  }

  /**
   * @see edu.internet2.middleware.grouper.GrouperAPI#onPreDelete(edu.internet2.middleware.grouper.hibernate.HibernateSession)
   */
  @Override
  public void onPreDelete(HibernateSession hibernateSession) {
    super.onPreDelete(hibernateSession);
    
    GrouperHooksUtils.callHooksIfRegistered(this, GrouperHookType.FIELD, 
        FieldHooks.METHOD_FIELD_PRE_DELETE, HooksFieldBean.class, 
        this, Field.class, VetoTypeGrouper.FIELD_PRE_DELETE, false, false);
  
  }

  /**
   * 
   * @see edu.internet2.middleware.grouper.GrouperAPI#onPreSave(edu.internet2.middleware.grouper.hibernate.HibernateSession)
   */
  @Override
  public void onPreSave(HibernateSession hibernateSession) {
    super.onPreSave(hibernateSession);
    
    
    GrouperHooksUtils.callHooksIfRegistered(this, GrouperHookType.FIELD, 
        FieldHooks.METHOD_FIELD_PRE_INSERT, HooksFieldBean.class, 
        this, Field.class, VetoTypeGrouper.FIELD_PRE_INSERT, false, false);
  
  }

  /**
   * @see edu.internet2.middleware.grouper.GrouperAPI#onPreUpdate(edu.internet2.middleware.grouper.hibernate.HibernateSession)
   */
  @Override
  public void onPreUpdate(HibernateSession hibernateSession) {
    super.onPreUpdate(hibernateSession);
    
    GrouperHooksUtils.callHooksIfRegistered(this, GrouperHookType.FIELD, 
        FieldHooks.METHOD_FIELD_PRE_UPDATE, HooksFieldBean.class, 
        this, Field.class, VetoTypeGrouper.FIELD_PRE_UPDATE, false, false);
  }

  /**
   * save the state when retrieving from DB
   * @return the dbVersion
   */
  @Override
  public Field dbVersion() {
    return (Field)this.dbVersion;
  }

  /**
   * note, these are massaged so that name, extension, etc look like normal fields.
   * access with fieldValue()
   * @see edu.internet2.middleware.grouper.GrouperAPI#dbVersionDifferentFields()
   */
  @Override
  public Set<String> dbVersionDifferentFields() {
    if (this.dbVersion == null) {
      throw new RuntimeException("State was never stored from db");
    }
    //easier to unit test if everything is ordered
    Set<String> result = GrouperUtil.compareObjectFields(this, this.dbVersion,
        DB_VERSION_FIELDS, null);
    return result;
  }

  /**
   * take a snapshot of the data since this is what is in the db
   */
  @Override
  public void dbVersionReset() {
    //lets get the state from the db so we know what has changed
    this.dbVersion = GrouperUtil.clone(this, DB_VERSION_FIELDS);
  }

  /**
   * deep clone the fields in this object
   */
  @Override
  public Field clone() {
    return GrouperUtil.clone(this, CLONE_FIELDS);
  }


} // public class Field extends GrouperAPI implements Serializable
