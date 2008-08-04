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

package edu.internet2.middleware.grouper.filter;
import java.util.Date;
import java.util.Set;

import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.Stem;
import edu.internet2.middleware.grouper.exception.QueryException;
import edu.internet2.middleware.grouper.misc.GrouperDAOFactory;
import edu.internet2.middleware.grouper.privs.PrivilegeHelper;

/** 
 * Query by groups modified after the specified date.
 * <p/>
 * @author  blair christensen.
 * @version $Id: GroupModifiedAfterFilter.java,v 1.2 2008-07-21 05:32:20 mchyzer Exp $
 * @since   1.1.0
 */
public class GroupModifiedAfterFilter extends BaseQueryFilter {

  // PRIVATE INSTANCE VARIABLES //
  private Date  d;      
  private Stem  ns;


  // CONSTRUCTORS //

  /**
   * {@link QueryFilter} that returns groups modified after the
   * specified date. 
   * <p/>
   * @param   d   Find groups modified after this date.
   * @param   ns  Restrict results to within this stem.
   * @since   1.1.0
   */
  public GroupModifiedAfterFilter(Date d, Stem ns) {
    this.d  = (Date) d.clone();
    this.ns = ns;
  } // public GroupModifiedAfterFilter(d, ns)


  // PUBLIC INSTANCE METHODS //

  public Set getResults(GrouperSession s) 
    throws QueryException
  {
    //note, no need for GrouperSession inverse of control
    GrouperSession.validate(s);
    Set candidates  = PrivilegeHelper.canViewGroups(
      s, GrouperDAOFactory.getFactory().getGroup().findAllByModifiedAfter(this.d)
    );
    Set results     = this.filterByScope(this.ns, candidates);
    return results;
  } // public Set getResults(s)

} // public class GroupModifiedAfterFilter
