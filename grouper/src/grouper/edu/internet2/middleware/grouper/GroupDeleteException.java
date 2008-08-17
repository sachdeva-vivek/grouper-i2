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

/**
 * Exception thrown when a group cannot be deleted from the Groups Registry.
 * <p/>
 * @author  blair christensen.
 * @version $Id: GroupDeleteException.java,v 1.6 2007-04-12 18:06:51 blair Exp $
 */
public class GroupDeleteException extends Exception {
  private static final long serialVersionUID = 2529375797709195916L;
  public GroupDeleteException() { 
    super(); 
  }
  public GroupDeleteException(String msg) { 
    super(msg); 
  }
  public GroupDeleteException(String msg, Throwable cause) { 
    super(msg, cause); 
  }
  public GroupDeleteException(Throwable cause) { 
    super(cause); 
  }
}
