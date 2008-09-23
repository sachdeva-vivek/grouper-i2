/*
 * @author mchyzer
 * $Id: WsRestFindStemsRequest.java,v 1.1 2008-03-29 10:50:43 mchyzer Exp $
 */
package edu.internet2.middleware.grouper.ws.rest.stem;

import edu.internet2.middleware.grouper.ws.GrouperServiceLogic;
import edu.internet2.middleware.grouper.ws.rest.WsRequestBean;
import edu.internet2.middleware.grouper.ws.rest.method.GrouperRestHttpMethod;
import edu.internet2.middleware.grouper.ws.soap.WsParam;
import edu.internet2.middleware.grouper.ws.soap.WsStemQueryFilter;
import edu.internet2.middleware.grouper.ws.soap.WsSubjectLookup;

/**
 * bean that will be the data from rest request
 * @see GrouperServiceLogic#getGroups(edu.internet2.middleware.grouper.ws.GrouperWsVersion, WsSubjectLookup[], edu.internet2.middleware.grouper.ws.member.WsMemberFilter, WsSubjectLookup, boolean, boolean, String[], WsParam[])
 * for method
 */
public class WsRestFindStemsRequest implements WsRequestBean {
  
  /** query filter for request */
  private WsStemQueryFilter wsStemQueryFilter;

  
  /** field */
  private String clientVersion;
  
  /** field */
  private WsSubjectLookup actAsSubjectLookup;
  
  /** field */
  private WsParam[] params;
  
  /**
   * @return the clientVersion
   */
  public String getClientVersion() {
    return this.clientVersion;
  }

  
  /**
   * @param clientVersion1 the clientVersion to set
   */
  public void setClientVersion(String clientVersion1) {
    this.clientVersion = clientVersion1;
  }

  
  /**
   * @return the actAsSubjectLookup
   */
  public WsSubjectLookup getActAsSubjectLookup() {
    return this.actAsSubjectLookup;
  }

  
  /**
   * @param actAsSubjectLookup1 the actAsSubjectLookup to set
   */
  public void setActAsSubjectLookup(WsSubjectLookup actAsSubjectLookup1) {
    this.actAsSubjectLookup = actAsSubjectLookup1;
  }

  
  /**
   * @return the params
   */
  public WsParam[] getParams() {
    return this.params;
  }


  
  /**
   * @param params1 the params to set
   */
  public void setParams(WsParam[] params1) {
    this.params = params1;
  }

  /**
   * @see edu.internet2.middleware.grouper.ws.rest.WsRequestBean#retrieveRestHttpMethod()
   */
  public GrouperRestHttpMethod retrieveRestHttpMethod() {
    return GrouperRestHttpMethod.GET;
  }


  
  /**
   * query filter for request
   * @return the wsQueryFilter
   */
  public WsStemQueryFilter getWsStemQueryFilter() {
    return this.wsStemQueryFilter;
  }


  
  /**
   * query filter for request
   * @param wsQueryFilter1 the wsQueryFilter to set
   */
  public void setWsStemQueryFilter(WsStemQueryFilter wsQueryFilter1) {
    this.wsStemQueryFilter = wsQueryFilter1;
  }

}