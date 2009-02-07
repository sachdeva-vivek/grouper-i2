/*
 * @author mchyzer
 * $Id: AuditTypeBuiltin.java,v 1.1 2009-02-06 16:33:18 mchyzer Exp $
 */
package edu.internet2.middleware.grouper.audit;


/**
 *
 */
public enum AuditTypeBuiltin implements AuditTypeIdentifier {

  /**
   * add group type
   */
  GROUP_TYPE_ADD(new AuditType("groupType", "addGroupType", null, "id", "name")),
  
  /**
   * update group type
   */
  GROUP_TYPE_UPDATE(new AuditType("groupType", "updateGroupType", null, "id", "name")),
  
  /**
   * delete group type
   */
  GROUP_TYPE_DELETE(new AuditType("groupType", "deleteGroupType", null, "id", "name")),
  
  /**
   * add group field
   */
  GROUP_FIELD_ADD(new AuditType("groupField", "addGroupField", null, "id", "name", 
      "groupTypeId", "groupTypeName", "type")),
  
  /**
   * update group field
   */
  GROUP_FIELD_UPDATE(new AuditType("groupField", "updateGroupField", null, "id", "name", "groupTypeId", "groupTypeName", "type")),
  
  /**
   * delete group field
   */
  GROUP_FIELD_DELETE(new AuditType("groupField", "deleteGroupField", null, "id", "name", "groupTypeId", "groupTypeName", "type")),
  
  /**
   * add group attribute
   */
  GROUP_ATTRIBUTE_ADD(new AuditType("groupAttribute", "addGroupAttribute", null, "id", "name", "groupId", "groupName", "fieldId", "fieldName", "value")),
  
  /**
   * update group attribute
   */
  GROUP_ATTRIBUTE_UPDATE(new AuditType("groupAttribute", "updateGroupAttribute", null, "id", "name", "groupId", "groupName", "fieldId", "fieldName", "value", "oldValue")),

  /**
   * delete group attribute
   */
  GROUP_ATTRIBUTE_DELETE(new AuditType("groupAttribute", "deleteGroupAttribute", null, "id", "name", "groupId", "groupName", "fieldId", "fieldName", "value")),

  /**
   * add group composite
   */
  GROUP_COMPOSITE_ADD(new AuditType("groupComposite", "addGroupComposite", null, "id", "ownerId", "ownerName", "leftFactorId", "leftFactorName", "rightFactorId", "rightFactorName", "type")),
  
  /**
   * update group composite
   */
  GROUP_COMPOSITE_UPDATE(new AuditType("groupComposite", "updateGroupComposite", null, "id", "ownerId", "ownerName", "leftFactorId", "leftFactorName", "rightFactorId", "rightFactorName", "type")),
  
  /**
   * delete group composite
   */
  GROUP_COMPOSITE_DELETE(new AuditType("groupComposite", "deleteGroupComposite", null, "id", "ownerId", "ownerName", "leftFactorId", "leftFactorName", "rightFactorId", "rightFactorName", "type")),
  
  /**
   * assign group type
   */
  GROUP_TYPE_ASSIGN(new AuditType("groupTypeAssignment", "assignGroupType", null, "id", "groupId", "groupName", "typeId", "typeName")),
  
  /**
   * unassign group type
   */
  GROUP_TYPE_UNASSIGN(new AuditType("groupTypeAssignment", "unassignGroupType", null, "id", "groupId", "groupName", "typeId", "typeName")),

  /**
   * add membership
   */
  MEMBERSHIP_ADD(new AuditType("membership", "addMembership", null, "id", "fieldId", "fieldName", "memberId", "membershipType", "ownerType", "ownerId", "ownerName")),
  
  /**
   * update membership
   */
  MEMBERSHIP_UPDATE(new AuditType("membership", "updateMembership", null, "id", "fieldId", "fieldName", "memberId", "membershipType", "ownerType", "ownerId", "ownerName")),

  /**
   * delete membership
   */
  MEMBERSHIP_DELETE(new AuditType("membership", "deleteMembership", null, "id", "fieldId", "fieldName", "memberId", "membershipType", "ownerType", "ownerId", "ownerName")),

  /**
   * add group
   */
  GROUP_ADD(new AuditType("group", "addGroup", null, "id", "name", "parentStemId", "displayName", "description")),
  
  /**
   * update group
   */
  GROUP_UPDATE(new AuditType("group", "updateGroup", null, "id", "name", "parentStemId", "displayName", "description")),
  
  /**
   * delete group
   */
  GROUP_DELETE(new AuditType("group", "deleteGroup", null, "id", "name", "parentStemId", "displayName", "description")),
  
  /**
   * stem add
   */
  STEM_ADD(new AuditType("stem", "addStem", null, "id", "name", "parentStemId", "displayName", "description")),

  /**
   * stem update
   */
  STEM_UPDATE(new AuditType("stem", "updateStem", null, "id", "name", "parentStemId", "displayName", "description")),
  
  /**
   * stem delete
   */
  STEM_DELETE(new AuditType("stem", "deleteStem", null, "id", "name", "parentStemId", "displayName", "description")),
  
  /**
   * member change subject
   */
  MEMBER_CHANGE_SUBJECT(new AuditType("member", "changeSubject", null, "oldMemberId", "oldSubjectId", "oldSourceId", "newMemberId", "newSubjectId", "newSourceId", "deleteOldMember", "memberIdChanged"));
  
  /**
   * defaults for audit type, though doesnt hold the id
   */
  private AuditType internalAuditTypeDefault;
  
  /**
   * construct
   * @param theInternalAuditTypeDefault 
   */
  private AuditTypeBuiltin(AuditType theInternalAuditTypeDefault) {
    this.internalAuditTypeDefault = theInternalAuditTypeDefault;
  }
  
  /**
   * get the audit type from the enum
   * @return the audit type
   */
  public AuditType getAuditType() {
    return AuditTypeFinder.find(this.internalAuditTypeDefault.getAuditCategory(), 
        this.internalAuditTypeDefault.getActionName(), true);
  }

  /**
   * get the defaults, but not the id
   * @return the defaults
   */
  public AuditType internal_auditTypeDefault() {
    return this.internalAuditTypeDefault;
  }
  
  /**
   * @see edu.internet2.middleware.grouper.audit.AuditTypeIdentifier#getId()
   */
  public String getId() {
    return this.getAuditType().getId();
  }
  
}