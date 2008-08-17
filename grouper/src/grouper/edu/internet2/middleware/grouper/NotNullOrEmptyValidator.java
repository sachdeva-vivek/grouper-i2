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

package edu.internet2.middleware.grouper;

/** 
 * @author  blair christensen.
 * @version $Id: NotNullOrEmptyValidator.java,v 1.4 2007-03-06 15:58:47 blair Exp $
 * @since   1.2.0
 */
class NotNullOrEmptyValidator extends GrouperValidator {

  // PROTECTED CLASS METHODS //

  // @since   1.2.0
  protected static NotNullOrEmptyValidator validate(String value) {
    NotNullOrEmptyValidator v   = new NotNullOrEmptyValidator();
    NotNullValidator        nnv = NotNullValidator.validate(value);
    if (nnv.isInvalid()) {
      v.setErrorMessage( nnv.getErrorMessage() );
      return v;
    }
    if ( value.equals(GrouperConfig.EMPTY_STRING) )  {
      v.setErrorMessage("empty value");
    }
    else {
      v.setIsValid(true);
    }
    return v;
  } // protected static NotNullOrEmptyValidator validate(value)

} // class NotNullOrEmptyValidator extends GrouperValidator
