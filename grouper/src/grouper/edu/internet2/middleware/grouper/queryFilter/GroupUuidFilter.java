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

package edu.internet2.middleware.grouper.queryFilter;

import java.util.HashSet;
import java.util.Set;

import edu.internet2.middleware.grouper.BaseQueryFilter;
import edu.internet2.middleware.grouper.Group;
import edu.internet2.middleware.grouper.GroupFinder;
import edu.internet2.middleware.grouper.GroupNotFoundException;
import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.QueryException;
import edu.internet2.middleware.grouper.QueryFilter;


/** 
 * Query by group uuid.
 * <p/>
 * @author  mchyzer.
 * @version $Id: GroupUuidFilter.java,v 1.1 2008-03-19 20:43:24 mchyzer Exp $
 */
public class GroupUuidFilter extends BaseQueryFilter {

  /** uuid of group to find */
  private String uuid;

  // Constructors

  /**
   * {@link QueryFilter} that returns group matching the specified
   * uuid.
   * <p>
   * @param   theUuid  Find groups matching this uuid.
   */
  public GroupUuidFilter(String theUuid) {
    this.uuid = theUuid;
  }

  /**
   * get the results
   * @param s is the grouper session
   * @return the set of groups (which is just going to be one or not groups)
   * @throws QueryException
   */
  public Set<Group> getResults(GrouperSession s) 
    throws QueryException {
    GrouperSession.validate(s);
    Set<Group> groups  = new HashSet<Group>();
    Group group = null;
    try {
      group = GroupFinder.findByUuid(s, this.uuid);
      groups.add(group);
    } catch (GroupNotFoundException gnfe) {
      //ignore
    }
    return groups;
  }
}
