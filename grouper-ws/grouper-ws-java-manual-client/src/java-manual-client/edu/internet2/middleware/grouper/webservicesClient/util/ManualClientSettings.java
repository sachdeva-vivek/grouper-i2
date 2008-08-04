/*
 * @author mchyzer $Id: ManualClientSettings.java,v 1.2 2008-03-30 09:00:33 mchyzer Exp $
 */
package edu.internet2.middleware.grouper.webservicesClient.util;


/**
 * generated client settings
 */
public class ManualClientSettings {
    /** client version.  keep this updated as the version changes */
    public static final String VERSION = "v1_3_000";

    /** user to login as */
    public static final String USER = "GrouperSystem";

    /** user to login as */
    public static final String PASS = "pass";

    /** port for auth settings */
    public static final int PORT = 8093;
    
    /** host for auth settings */
    public static final String HOST = "localhost";
    
    /** url prefix */
    public static final String URL = "http://localhost:8093/grouper-ws/services/GrouperService";

    /**
     * make sure a array is non null.  If null, then return an empty array.
     * Note: this will probably not work for primitive arrays (e.g. int[])
     * @param <T>
     * @param array
     * @return the list or empty list if null
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] nonNull(T[] array) {
      return array == null ? ((T[])new Object[0]) : array;
    }
}