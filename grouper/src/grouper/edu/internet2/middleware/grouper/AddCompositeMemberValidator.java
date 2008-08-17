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
 * @version $Id: AddCompositeMemberValidator.java,v 1.2 2007-04-05 14:28:28 blair Exp $
 * @since   1.2.0
 */
class AddCompositeMemberValidator extends GrouperValidator {

  // PROTECTED CLASS METHODS //

  // @since   1.2.0
  protected static AddCompositeMemberValidator validate(Group g) {
    AddCompositeMemberValidator v = new AddCompositeMemberValidator();
    if      ( g.hasComposite() )  {
      v.setErrorMessage(E.GROUP_ACTC);
    }
    else if ( 
      GrouperDAOFactory.getFactory().getMembership().findAllByOwnerAndField(
        g.getUuid(), Group.getDefaultList() 
      ).size() > 0 
    )
    {
      v.setErrorMessage(E.GROUP_ACTM);
    }
    else {
      v.setIsValid(true);
    }
    return v;
  } // protected static AddCompositeMemberValidator validate(parent, extn, dExtn)

} // class AddCompositeMemberValidator extends GrouperValidator
