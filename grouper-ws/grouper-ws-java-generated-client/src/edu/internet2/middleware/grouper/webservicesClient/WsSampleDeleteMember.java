/**
 * 
 */
package edu.internet2.middleware.grouper.webservicesClient;

import edu.internet2.middleware.grouper.webservicesClient.GrouperServiceStub.DeleteMember;
import edu.internet2.middleware.grouper.webservicesClient.GrouperServiceStub.WsDeleteMemberResults;
import edu.internet2.middleware.grouper.webservicesClient.GrouperServiceStub.WsGroupLookup;
import edu.internet2.middleware.grouper.webservicesClient.GrouperServiceStub.WsSubjectLookup;
import edu.internet2.middleware.grouper.webservicesClient.util.GeneratedClientSettings;
import edu.internet2.middleware.grouper.ws.samples.types.WsSampleGenerated;
import edu.internet2.middleware.grouper.ws.samples.types.WsSampleGeneratedType;

import org.apache.axis2.client.Options;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.lang.reflect.Array;

/**
 * @author mchyzer
 *
 */
public class WsSampleDeleteMember implements WsSampleGenerated {

  /**
   * @param args
   */
  public static void main(String[] args) {
    deleteMember(WsSampleGeneratedType.soap);
  }

  /**
   * @see edu.internet2.middleware.grouper.ws.samples.types.WsSampleGenerated#executeSample(edu.internet2.middleware.grouper.ws.samples.types.WsSampleGeneratedType)
   */
  public void executeSample(WsSampleGeneratedType wsSampleGeneratedType) {
    deleteMember(wsSampleGeneratedType);
  }

  /**
   * @param wsSampleGeneratedType can run as soap or xml/http
   */
  public static void deleteMember(WsSampleGeneratedType wsSampleGeneratedType) {
    try {
      //URL, e.g. http://localhost:8091/grouper-ws/services/GrouperService
      GrouperServiceStub stub = new GrouperServiceStub(GeneratedClientSettings.URL);
      Options options = stub._getServiceClient().getOptions();
      HttpTransportProperties.Authenticator auth = new HttpTransportProperties.Authenticator();
      auth.setUsername(GeneratedClientSettings.USER);
      auth.setPassword(GeneratedClientSettings.PASS);
      auth.setPreemptiveAuthentication(true);

      options.setProperty(HTTPConstants.AUTHENTICATE, auth);
      options.setProperty(HTTPConstants.SO_TIMEOUT, new Integer(3600000));
      options.setProperty(HTTPConstants.CONNECTION_TIMEOUT, new Integer(3600000));

      DeleteMember deleteMember = DeleteMember.class.newInstance();

      //version, e.g. v1_3_000
      deleteMember.setClientVersion(GeneratedClientSettings.VERSION);

      // set the act as id
      WsSubjectLookup actAsSubject = WsSubjectLookup.class.newInstance();
      actAsSubject.setSubjectId("GrouperSystem");
      deleteMember.setActAsSubjectLookup(actAsSubject);

      WsGroupLookup wsGroupLookup = WsGroupLookup.class.newInstance();
      wsGroupLookup.setGroupName("aStem:aGroup");
      deleteMember.setWsGroupLookup(wsGroupLookup);

      // delete two subjects from the group
      WsSubjectLookup[] subjectLookups = (WsSubjectLookup[]) Array.newInstance(
          WsSubjectLookup.class, 2);
      subjectLookups[0] = WsSubjectLookup.class.newInstance();
      subjectLookups[0].setSubjectId("10021368");

      subjectLookups[1] = WsSubjectLookup.class.newInstance();
      subjectLookups[1].setSubjectId("10039438");

      deleteMember.setSubjectLookups(subjectLookups);

      WsDeleteMemberResults wsDeleteMemberResults = stub.deleteMember(deleteMember)
          .get_return();

      System.out.println(ToStringBuilder.reflectionToString(wsDeleteMemberResults));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}