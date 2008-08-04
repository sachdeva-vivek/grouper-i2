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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.internet2.middleware.grouper.cache.GrouperCache;
import edu.internet2.middleware.grouper.exception.GrouperRuntimeException;
import edu.internet2.middleware.grouper.exception.SchemaException;
import edu.internet2.middleware.grouper.misc.E;
import edu.internet2.middleware.grouper.misc.GrouperDAOFactory;

/**
 * Find group types.
 * <p/>
 * @author  blair christensen.
 * @version $Id: GroupTypeFinder.java,v 1.31 2008-07-27 07:37:24 mchyzer Exp $
 */
public class GroupTypeFinder {
  
  /** 
   * every 10 minutes, get new elements
   */
  private static GrouperCache<String, GroupType> types = new GrouperCache<String, GroupType>(
      GroupTypeFinder.class.getName() + ".typeCache", 10000, false, 60*10, 60*10, false);

  /** logger */
  private static final Log LOG = LogFactory.getLog(GroupTypeFinder.class);


// 20080727: should do this in static block, too early, things arent initted yet...
//  // STATIC //
//  static {
//    LOG.info("finding group types");
//    
//    // We need to initialize the known types at this point to try and
//    // avoid running into Hibernate exceptions later on when attempting
//    // to save objects.
//    GroupType t;
//    Iterator  iter  = _findAll().iterator();
//    while (iter.hasNext()) {
//      t = (GroupType) iter.next();
//      types.put(t.getName(), t);
//      LOG.info("found group type: " + t);
//    }
//  } // static
//

  // PUBLIC CLASS METHODS //

  /** 
   * Find a {@link GroupType}.
   * <p/>
   * A {@link SchemaException} will be thrown if the type is not found.
   * <pre class="eg">
   * try {
   *   GroupType type = GroupTypeFinder.find(name);
   * }
   * catch (SchemaException eS) {
   *   // type does not exist
   * }
   * </pre>
   * @param   name  Find {@link GroupType} with this name.
   * @return  {@link GroupType}
   * @throws  SchemaException
   */
  public static GroupType find(String name) 
    throws  SchemaException
  {
    // First check to see if type is cached.
    if (types.containsKey(name)) {
      return (GroupType) types.get(name);
    }
    // If not, refresh known types as it may be new and try again. 
    internal_updateKnownTypes();
    if (types.containsKey(name)) {
      return (GroupType) types.get(name);
    }
    String msg = E.INVALID_GROUP_TYPE + name;
    LOG.error(msg);
    throw new SchemaException(msg);
  } // public static GroupType find(name)

  /**
   * Find all public group types.
   * <pre class="eg">
   * Set types = GroupTypeFinder.findAll();
   * </pre>
   * @return  A {@link Set} of {@link GroupType} objects.
   */
  public static Set findAll() {
    internal_updateKnownTypes();
    Set       values  = new LinkedHashSet();
    GroupType t;
    Iterator  iter    = types.values().iterator();
    while (iter.hasNext()) {
      t = (GroupType) iter.next();
      if ( !t .getIsInternal() ) {
        values.add(t); // We only want !internal group types
      }
    }
    return values;
  } // public static Set findAll()

  /**
   * Find all assignable group types.
   * <pre class="eg">
   * Set types = GroupTypeFinder.findAllAssignable();
   * </pre>
   * @return  A {@link Set} of {@link GroupType} objects.
   */
  public static Set findAllAssignable() {
    Set       types = new LinkedHashSet();
    GroupType t;
    Iterator  iter  = findAll().iterator();
    while (iter.hasNext()) {
      t = (GroupType) iter.next();
      if ( t.getIsAssignable() ) {
        types.add(t);
      }
    }
    return types;
  } // public static Set findAllAssignable()


  // PROTECTED CLASS METHODS //

  // @since   1.2.0
  public static void internal_updateKnownTypes() {
    // This method irks me still even if it is now more functionally correct
    Set typesInRegistry = _findAll();
    // Look for types to add
    GroupType tA;
    Iterator  addIter   = typesInRegistry.iterator();
    while (addIter.hasNext()) {
      tA = (GroupType) addIter.next();
      if (!types.containsKey(tA.getName())) {
        types.put(tA.getName(), tA); // New type.  Add it to the cached list.
      }
    }
    // Look for types to remove
    Set       toDel   = new LinkedHashSet();
    GroupType tD;
    Iterator  delIter = types.values().iterator();
    while (delIter.hasNext()) {
      tD = (GroupType) delIter.next();
      if (!typesInRegistry.contains(tD)) {
        toDel.add(tD.getName());  
      }
    }
    String    type;
    Iterator  toDelIter = toDel.iterator();
    while (toDelIter.hasNext()) {
      type = (String) toDelIter.next();
      types.remove(type);  
    }
  } // protected static void internal_updateKnownTypes()


  // PRIVATE CLASS METHODS //
  private static Set _findAll() 
    throws  GrouperRuntimeException
  {
    try {
      Set       types = new LinkedHashSet();
      Iterator  it    = GrouperDAOFactory.getFactory().getGroupType().findAll().iterator();
      while (it.hasNext()) {
        GroupType type = (GroupType)it.next() ;
        types.add(type);
      }
      LOG.info("found group types: " + types.size() );
      return types;
    }
    catch (GrouperRuntimeException eGRE) {
      String msg = E.GROUPTYPE_FINDALL + eGRE.getMessage();
      LOG.fatal(msg);
      throw new GrouperRuntimeException(msg, eGRE);
    }
  } // private Static Set _findAll()

  /**
   * clear cache (e.g. if schema export)
   */
  public static void clearCache() {
    types.clear();
  }

} // public class GroupTypeFinder
