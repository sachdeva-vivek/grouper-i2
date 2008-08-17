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
import  edu.internet2.middleware.grouper.internal.dto.GroupDTO;
import  edu.internet2.middleware.grouper.internal.dto.GroupTypeDTO;
import  java.util.Iterator;
import  java.util.LinkedHashSet;
import  java.util.Set;

/** 
 * Query by {@link GroupType}.
 * <p/>
 * @author  blair christensen.
 * @version $Id: GroupTypeFilter.java,v 1.11 2007-04-18 14:03:11 blair Exp $
 * @since   1.2.0
 */
public class GroupTypeFilter extends BaseQueryFilter {

  // PRIVATE INSTANCE VARIABLES //
  private GroupType type  = null;
  private Stem      ns    = null;


  // CONSTRUCTORS //

  /**
   * {@link QueryFilter} that returns groups that have the specified 
   * {@link GroupType}.
   * @param   type  Find groups of this type.
   * @param   ns    Restrict results to within this stem.
   * @since   1.2.0
   */
  public GroupTypeFilter(GroupType type, Stem ns) {
    this.type = type;
    this.ns   = ns;
  } // public GroupTypeFilter(type, ns)


  // PUBLIC INSTANCE METHODS //

  public Set getResults(GrouperSession s) 
    throws QueryException
  {
    GrouperSession.validate(s);
    Set       groups  = new LinkedHashSet();
    Group     g;  
    Iterator  it      = GrouperDAOFactory.getFactory().getGroup().findAllByType( (GroupTypeDTO) this.type.getDTO() ).iterator();
    while (it.hasNext()) {
      g = (Group) new Group().setDTO( (GroupDTO) it.next() );
      g.setSession(s);
      groups.add(g);
    }
    return this.filterByScope(this.ns, groups);
  } 

}
