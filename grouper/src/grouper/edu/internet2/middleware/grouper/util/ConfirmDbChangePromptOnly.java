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

package edu.internet2.middleware.grouper.util;

import org.apache.commons.lang.StringUtils;



/**
 * Make sure the user is ok with db changes, only display message
 * @author Chris Hyzer
 * @version $Id: ConfirmDbChangePromptOnly.java,v 1.1 2008-05-06 21:30:50 mchyzer Exp $
 */
public class ConfirmDbChangePromptOnly {

  /**
   * 
   * @param args
   */
  public static void main(String[] args) {
//    if (StringUtils.isBlank(args[0])) {
//      System.out.println("Need to pass arg to confirm class");
//      System.exit(1);
//    }
    GrouperUtil.promptUserAboutDbChanges("run ant", false);
  }
} 
