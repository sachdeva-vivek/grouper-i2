package edu.internet2.middleware.grouper;

import  java.sql.*;
import  java.util.*;
import  net.sf.hibernate.*;

/** 
 * Class representing a {@link Grouper} group.
 *
 * @author  blair christensen.
 * @version $Id: GrouperGroup.java,v 1.26 2004-08-19 19:24:43 blair Exp $
 */
public class GrouperGroup {

  private Session         session;

  private String groupKey;
  private int groupType;
  private String createTime;
  private String createSubject;
  private String createSource;
  private String modifyTime;
  private String modifySubject;
  private String modifySource;
  private String comment;

  private GrouperSession  intSess;
  private String          name;
  private String          groupID   = null;
  private boolean         exists    = false;
  private Map             attributes;

  /**
   * TODO 
   *
   * Create a new object that represents a single {@link Grouper}
   * group. 
   * <p>
   * <ul>
   *  <li>Caches the group name.</li>
   *  <li>Checks and caches whether group exists.</li>
   *  <li>If group exists, the privileges of the current subject   
   *      on this group will be cached.</li>
   * </ul>
   * 
   * @param   s         Session context.
   * @param   name Name of group.
   */
  public GrouperGroup() {
    session       = null;

    // TODO Merge with 'attributes'?
    groupKey      = null;
    groupType     = 1;    // TODO Don't hardcode this
    createTime    = null;
    createSubject = null;
    createSource  = null;
    modifyTime    = null;
    modifySubject = null;
    modifySource  = null;
    comment       = null;

    attributes    = new HashMap();
    intSess       = null;
    name          = null;
  }

  public void session(GrouperSession s) {
    this.intSess = s;
    this.session = this.intSess.session();
  }

  public GrouperSession session() {
    // TODO Return an exception if !defined?
    return this.intSess;
  }

  public void attribute(String attribute, String value) {
    // TODO Attribute validation
    /* 
     * We save the transformation into a GrouperAttribute object until
     * later as we need to have a valid groupKey.  And yes, this 
     * can be improved upon.
     */
    attributes.put(attribute, value);
  }

  public String attribute(String attribute) {
    return (String) attributes.get(attribute);
  }

  public boolean exist() {
    // SELECT * FROM grouper_attributes WHERE name=$name
    try {
      // FIXME This query is incredibly wrong -- but that's ok as I a
      //        am fine with this returning false indefinitely for the
      //        time being.
      int cnt = ( (Integer) this.session.iterate(
                    "SELECT count(*) FROM grouper_attributes " +
                    "IN CLASS edu.internet2.middleware.grouper.GrouperAttribute " +
                    "WHERE groupField = '" + attributes.get("stem") + 
                    ":" + attributes.get("descriptor") + "'"
                    ).next() ).intValue();
      if (cnt > 0) {
        return true;
      } 
    } catch (Exception e) {
      System.err.println(e);
      System.exit(1); 
    }
    return false;
  }

  public void create() {
    // FIXME Damn this is ugly.

    // Set some of the operational attributes
    java.util.Date now = new java.util.Date();
    this.setCreateTime( Long.toString(now.getTime()) );
    this.setCreateSubject( this.intSess.whoAmI() );

    // And now attempt to add the group to the store
    try {
      Transaction t = session.beginTransaction();
      // The Group object
      session.save(this);

      // Its schema
      GrouperSchema schema = new GrouperSchema();
      schema.set(this.groupKey, this.groupType);
      session.save(schema);

      // And its attributes
      Iterator iter = attributes.keySet().iterator();
      while (iter.hasNext()) {
        GrouperAttribute attr = new GrouperAttribute();
        String key = (String) iter.next();
        attr.set(this.groupKey, key, (String) attributes.get(key));
        session.save(attr);
      }

      // And make the creator a member of the "admins" list
      GrouperMembership mship = new GrouperMembership(); 
      mship.set(this.groupKey, "admins", this.intSess.whoAmI(), true);
      session.save(mship);

      t.commit();
    } catch (Exception e) {
      System.err.println(e);
      System.exit(1);
    }
  }

  /**
   * Retrieves all memberships of type "members".
   * <p>
   * <ul>
   *  <li>Verify that the current subject has sufficient
   *      privileges to view desired information.</li>
   *  <li>Fetch rows from the <i>grouper_membership</i> table
   *      that represent "members" and have the appropriate
   *      <i>groupID</i> value.</li>
   * </ul>
   *
   * @return  List of group memberships.
   */
  public List getMembership() {
    List membership = new ArrayList();

    // XXX Isn't this simplistic!
    // XXX And wrong!
    try { 
      Statement stmt  = intSess.connection().createStatement();
      String    query = "SELECT * FROM grouper_membership WHERE " +
                        "groupID=1"; // AND groupField='members'";
      ResultSet rs    = stmt.executeQuery(query);
      while (rs.next()) {
        String memberRef = rs.getString("memberID");
        GrouperMember m = new GrouperMember(this.intSess, rs.getString("memberID"), false);
        membership.add( (Object) m);
      }
    } catch (Exception e) {
      System.err.println(e);
      System.exit(1);
    }

    return membership;
  }

  /**
   * Retrieves all memberships of a specified type.
   * <p>
   * <ul>
   *  <li>Verify that the current subject has sufficient
   *      privileges to view desired information.</li>
   *  <li>Fetch rows from the <i>grouper_membership</i> table
   *      that have the appropriate <i>groupID</i> and 
   *      <i>groupField</i> values.</li>
   * </ul>
   *
   * @param   groupField  Type of group to return.
   * @return  List of group memberships.
   */
  public List getMembership(String groupField) {
    List membership = new ArrayList();
    return membership;
  }

  /**
   * Retrieves all memberships of a specified type and immediacy.
   * <p>
   * <ul>
   *  <li>Verify that the current subject has sufficient
   *      privileges to view desired information..</li>
   *  <li>Fetch rows from the <i>grouper_membership</i> table
   *      that have the appropriate <i>groupID</i>, 
   *      <i>groupField</i>, and <i>isImmediate</i> values.</li>
   * </ul>
   *
   * @param   groupField  Type of group to return.
   * @param   isImmediate Return only immediate or non-immediate
   *          memberships.
   * @return  List of group memberships..
   */
  public List getMembership(String groupField, boolean isImmediate) {
    List membership = new ArrayList();
    return membership;
  }

  /**
   * Retrieves all metadata.
   * <p>
   * <ul>
   *  <li>Verify that the current subject has sufficient privileges to
   *      view desired information.</li>
   *  <li>Fetch rows from the <i>grouper_metadata</i> table that have
   *      the appropriate <i>groupID</i> value.
   * </ul>
   *
   * @return  Map of all accessible group metadata.
  */
/* DEPRECATE */
/*
  public Map getMetadata() {
    Map metadata = new HashMap();
    return metadata;
  }
*/

  /**
   * Retrieves a single item of metadata.
   * <p>
   * <ul>
   *  <li>Verify that the current subject has sufficient privileges to
   *      view the desired informaton.</li>
   *  <li>Fetch row from the <i>grouper_metadata</i> table that has the 
   *      appropriate <i>groupID</i> and <i>groupField</i> values.</li>
   * </ul>
   *
   * @param   groupField Desired metadata for this {@link GrouperGroup}.
   * @return  Metadata value.
   */
/* DEPRECATE */
/*
  public String getMetadata(String groupField) {
    return null;
  }
*/

  /**
   * Create a new group of type "base".
   * <p>
   * <ul>
   *  <li>Verify that the current subject has sufficient privileges to
   *      create the desired group.</li>
   *  <li>If yes:</li>
   *  <ul>
   *   <li>Update the <i>grouper_group</i> table.</li>
   *   <li>Update the <i>grouper_schema</i> table.</li>
   *   <li>Update the <i>grouper_metadata</i> table.</li>
   *   <li>Grant the current subject "admin" privileges on the new
   *       group.</li>
   *   <li>Update the internal <i>exists</i> flag.</li>
   *  </ul>
   * </ul>
   */
  // XXX public void create() {
  // XXX  // Nothing -- Yet
  // XXX }

  /**
   * Create a new group of type <i>groupType</i>.
   * <p>
   * <ul>
   *  <li>Verify that the current subject has sufficient privileges to
   *      create the desired group.</li>
   *  <li>If yes:</li>
   *  <ul>
   *   <li>Update the <i>grouper_group</i> table.</li>
   *   <li>Update the <i>grouper_schema</i> table.</li>
   *   <li>Update the <i>grouper_metadata</i> table.</li>
   *   <li>Grant the current subject "admin" privileges on the new
   *       group.</li>
   *   <li>Update the internal <i>exists</i> flag.</li>
   *  </ul>
   * </ul>
   */
  public void create(String groupType) {
    // Nothing -- Yet
  }

  /**
   * Rename a group.
   * <p>
   * <ul>
   *  <li>Verify that the current subject has sufficient privileges to
   *      rename the group as the desired stem and descriptor.</li>
   *  <li>Update the <i>grouper_metadata</i> table.</li>
   * </ul>
   */
  public void rename(String newGroupName) {
    // Nothing -- Yet
  }

  /** 
   * Delete a group.
   * <p>
   * <ul>
   *  <li>Verify that the current subject has sufficient privileges to
   *      delete the group.</li>
   *  <li>Update the <i>grouper_group</i> table.</li>
   *  <li>Update the <i>grouper_schema</i> table.</li>
   *  <li>Update the <i>grouper_metadata</i> table.</li>
   *  <li>Update the <i>grouper_membership</i> table.</li>
   *  <li>Update the <i>grouper_via</i> table.</li>
   *  <li>Update the internal <i>exists</i> flag.</li>
   * </ul>
   */
  public void delete() {
    // Nothing -- Yet
  }

  /**
   * Add metadata to a group.
   * <p>
   * <ul>
   *  <li>Verify that the current subject has sufficient privileges to
   *      update the specified metadata.</li>
   *  <li>Update the <i>grouper_metadata</i> table.</li>
   * </ul>
   *
   * @param   groupField  The field type for this member.
   * @param   value       The new metadata value.
   */
  public void addValue(String groupField, String value) {
    // Nothing -- Yet
  }

  /**
   * Add new "member" membership.
   * <p>
   * <ul>
   *  <li>Verify that the current subject has sufficient privileges to
   *      add a new membership of this type.</li>
   *  <li>Update the <i>grouper_membership</i> table to with this new
   *      membership.</li>
   *  <li>Update the <i>grouper_membership</i> and <i>grouper_via</i>
   *      tables as appropriate to reflect any new effective
   *      memberships.</li>
   * </ul>
   *
   * @param   member      The member to add.
   */
  public void addValue(GrouperMember member) {
    // Nothing -- Yet
  }

  /**
   * Add new membership of type <i>groupField</i>.
   * <p>
   * <ul>
   *  <li>Verify that the current subject has sufficient privileges to
   *      add a new membership of this type.</li>
   *  <li>Update the <i>grouper_membership</i> table to with this new
   *      membership.</li>
   *  <li>Update the <i>grouper_membership</i> and <i>grouper_via</i>
   *      tables as appropriate to reflect any new effective
   *      memberships.</li>
   * </ul>
   *
   * @param   groupField  The field type for this member.
   * @param   member      The member to add.
   */
  public void addValue(String groupField, GrouperMember member) {
    // Nothing -- Yet
  }

  /**
   * Add new membership of type <i>groupField</i> with non-default TTL.
   * <p>
   * <ul>
   *  <li>Verify that the current subject has sufficient privileges to
   *      add a new membership of this type.</li>
   *  <li>Update the <i>grouper_membership</i> table to with this new
   *      membership.</li>
   *  <li>Update the <i>grouper_membership</i> and <i>grouper_via</i>
   *      tables as appropriate to reflect any new effective
   *      memberships.</li>
   * </ul>
   *
   * @param   groupField  The field type for this member.
   * @param   member      The member to add.
   * @param   ttl         When the membership expires.
   */
  public void addValue(String groupField, GrouperMember member, java.util.Date ttl) {
    // Nothing -- Yet
  }

  /**
   * Remove group metadata.
   * <p>
   * <ul>
   *  <li>Verify that the current subject has sufficient privileges to
   *      remove the specified metadata.</li>
   *  <li>Update <i>grouper_metadata</i> table.</li>
   * </ul>
   * 
   * @param   groupField  The metadata field.
   */
  public void  removeValue(String groupField) {
    // Nothing -- Yet
  }

  /**
   * Remove specific group metadata.
   * <p>
   * <ul>
   *  <li>Verify that the current subject has sufficient privileges to
   *      remove the specified metadata.</li>
   *  <li>Update <i>grouper_metadata</i> table.</li>
   * </ul>
   * 
   * @param   groupField  The metadata field.
   * @param   groupValue  The value to remove.
   */
  public void  removeValue(String groupField, String groupValue) {
    // Nothing -- Yet
  }

  /**
   * Remove existing "member" membership.
   * <p>
   * <ul>
   *  <li>Verify that the current subject has sufficient privileges to 
   *      remove the specified membership.</li>
   *  <li>Update <i>grouper_membership</i> table to reflect the loss of
   *      this membership.</li>
   *  <li>Update the <i>grouper_membership</i> and <i>grouper_via</i>
   *      tables as appropriate to reflect any loss of effective
   *      memberships.</li>
   * </ul>
   *
   * @param   member    The membership to remove.
   */
  public void removeValue(GrouperMember member) {
    // Nothing -- Yet
  }

  /**
   * Remove existing membership of type <i>groupField</i>.
   * <p>
   * <ul>
   *  <li>Verify that the current subject has sufficient privileges to 
   *      remove the specified membership.</li>
   *  <li>Update <i>grouper_membership</i> table to reflect the loss of
   *      this membership.</li>
   *  <li>Update the <i>grouper_membership</i> and <i>grouper_via</i>
   *      tables as appropriate to reflect any loss of effective
   *      memberships.</li>
   * </ul>
   *
   * @param   member      The membership to remove.
   * @param   groupField  Type of membership to remove.
   */
  public void removeValue(GrouperMember member, String groupField) {
    // Nothing -- Yet
  }

  /**
   * Identify this group object.
   *
   * @return  Group name.
   */
  public String whoAmI() {
    if (this.name == null) {
      // XXX Query <i>grouper_metadata</i> table, fetch <i>name</i>,
      //     and cache it in <i>this.name</i>.
    }
    return this.name;
  }

  /*
   * Below for Hibernate
   */

  private String getGroupKey() {
    return this.groupKey;
  }

  private void setGroupKey(String groupKey) {
    this.groupKey = groupKey;
  }

  private String getCreateTime() {
    return this.createTime;
  }
 
  private void setCreateTime(String createTime) {
    this.createTime = createTime;
  }
 
  private String getCreateSubject() {
    return this.createSubject;
  }
 
  private void setCreateSubject(String createSubject) {
    this.createSubject = createSubject;
  }
 
  private String getCreateSource() {
    return this.createSource;
  }
 
  private void setCreateSource(String createSource) {
    this.createSource = createSource;
  }
 
  private String getModifyTime() {
    return this.modifyTime;
  }
 
  private void setModifyTime(String modifyTime) {
    this.modifyTime = modifyTime;
  }
 
  private String getModifySubject() {
    return this.modifySubject;
  }
 
  private void setModifySubject(String modifySubject) {
    this.modifySubject = modifySubject;
  }
 
  private String getModifySource() {
    return this.modifySource;
  }
 
  private void setModifySource(String modifySource) {
    this.modifySource = modifySource;
  }

  private String getComment() {
    return this.comment;
  }

  private void setComment(String comment) {
    this.comment = comment;
  } 

}

