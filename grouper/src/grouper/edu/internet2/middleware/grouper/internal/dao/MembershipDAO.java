/*
  Copyright (C) 2007 University Corporation for Advanced Internet Development, Inc.
  Copyright (C) 2007 The University Of Chicago

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

package edu.internet2.middleware.grouper.internal.dao;
import java.util.Date;
import java.util.Set;

import edu.internet2.middleware.grouper.Field;
import edu.internet2.middleware.grouper.Member;
import edu.internet2.middleware.grouper.Membership;
import edu.internet2.middleware.grouper.exception.MembershipNotFoundException;
import edu.internet2.middleware.grouper.misc.DefaultMemberOf;

/** 
 * Basic <code>Membership</code> DAO interface.
 * @author  blair christensen.
 * @version $Id: MembershipDAO.java,v 1.12 2008-07-21 04:43:59 mchyzer Exp $
 * @since   1.2.0
 */
public interface MembershipDAO extends GrouperDAO {

  /**
   * @since   1.2.0 
   */
  boolean exists(String ownerUUID, String memberUUID, String listName, String msType)
    throws  GrouperDAOException;

  /**
   * @since   1.2.0
   */
  Set<Membership> findAllByCreatedAfter(Date d, Field f) 
    throws  GrouperDAOException;

  /**
   * @since   1.2.0
   */
  Set<Membership> findAllByCreatedBefore(Date d, Field f) 
    throws  GrouperDAOException;

  /**
   * @since   1.2.0
   */
  Set<Membership> findAllByMember(String memberUUID) 
    throws  GrouperDAOException;

  /**
   * @since   1.2.0
   */
  Set<Membership> findAllByMemberAndVia(String memberUUID, String viaUUID) 
    throws  GrouperDAOException;

  /**
   * @since   1.2.0
   */
  Set<Membership> findAllByOwnerAndField(String ownerUUID, Field f) 
    throws  GrouperDAOException;
  
  /**
   * @since   1.2.1
   */
  Set<Membership> findAllByOwnerAndMember(String ownerUUID, String memberUUID) 
    throws  GrouperDAOException;

  /**
   * @since   1.2.0
   */
  Set<Membership> findAllByOwnerAndFieldAndType(String ownerUUID, Field f, String type) 
    throws  GrouperDAOException;

  /**
   * @since   1.2.0
   */
  Set<Membership> findAllByOwnerAndMemberAndField(String ownerUUID, String memberUUID, Field f) 
    throws  GrouperDAOException;

  /**
   * @return  Members from memberships.
   * @throws  GrouperDAOException if any DAO errors occur.
   * @see     MembershipDAO#findAllMembersByOwnerAndField(String, Field)
   * @since   1.2.1
   */
  Set<Member> findAllMembersByOwnerAndField(String ownerUUID, Field f)
    throws  GrouperDAOException;
    
  /**
   * @since   1.2.0
   */
  Membership findByOwnerAndMemberAndFieldAndType(String ownerUUID, String memberUUID, Field f, String type)
    throws  GrouperDAOException,
            MembershipNotFoundException
            ;            

  /**
   * @since   1.2.0
   */
  Set<Membership> findAllChildMemberships(Membership _ms) 
    throws  GrouperDAOException;

  /**
   * @since   1.2.0
   */
  Set<Membership> findAllEffective(String ownerUUID, String memberUUID, Field f, String viaUUID, int depth) 
    throws  GrouperDAOException;

  /**
   * @since   1.2.0
   */
  Set<Membership> findAllEffectiveByMemberAndField(String memberUUID, Field f) 
    throws  GrouperDAOException;

  /**
   * @since   1.2.0
   */
  Set<Membership> findAllEffectiveByOwnerAndMemberAndField(String ownerUUID, String memberUUID, Field f)
    throws  GrouperDAOException;

  /**
   * @since   1.2.0
   */
  Set<Membership> findAllImmediateByMemberAndField(String memberUUID, Field f) 
    throws  GrouperDAOException;

  /**
   * @since   1.2.0
   */
  Membership findByUuid(String uuid) 
    throws  GrouperDAOException,
            MembershipNotFoundException 
            ;

  /**
   * @since   1.2.0
   */
  Set findMembershipsByMemberAndField(String memberUUID, Field f)
    throws  GrouperDAOException;

  /**
   * @since   1.2.0
   */
  void update(DefaultMemberOf mof) 
    throws  GrouperDAOException;

} 
