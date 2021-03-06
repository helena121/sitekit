package org.vaadin.addons.sitekit.site;

import org.vaadin.addons.sitekit.model.User;
import org.vaadin.addons.sitekit.util.PropertiesUtil;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Processing context to be passed to processing layer. Processing context
 * is used for access control and audit logging.
 */
public class ProcessingContext {

    /** The object map. */
    private final Map<Object, Object> objectMap = new HashMap<Object, Object>();
    /** The entity manager. */
    private final EntityManager entityManager;
    /** The server name from HTTP request. */
    protected final String serverName;
    /** The local component IP address. */
    protected final String localIpAddress;
    /** The local component port. */
    protected final Integer componentPort;
    /** The local component type. */
    protected String componentType;
    /** The remote host. */
    protected final String remoteHost;
    /** The remote peer IP address. */
    protected final String remoteIpAddress;
    /** The remote peer port. */
    protected final Integer remotePort;
    /** The user ID. */
    protected String userId;
    /** The user name. */
    protected String userName;
    /** The user roles. */
    protected final List<String> roles;

    /**
     * Constructor for defining parameters for processing context.
     *
     * @param entityManager the entity manager
     * @param serverName the server name
     * @param localIpAddress the local IP address
     * @param componentPort the local component port
     * @param componentType the local component type
     * @param remoteHost the remote host
     * @param remoteIpAddress the remote peer IP address
     * @param remotePort the remote peer port
     * @param userId the user ID
     * @param userName the user name
     * @param roles the user roles
     */
    public ProcessingContext(final EntityManager entityManager,
                             final String serverName,
                             final String localIpAddress,
                             final Integer componentPort,
                             final String componentType,
                             final String remoteHost,
                             final String remoteIpAddress,
                             final Integer remotePort,
                             final String userId,
                             final String userName,
                             final List<String> roles) {
        this.entityManager = entityManager;
        this.serverName = serverName;
        this.localIpAddress = localIpAddress;
        this.componentPort = componentPort;
        this.componentType = componentType;
        this.remoteHost = remoteHost;
        this.remoteIpAddress = remoteIpAddress;
        this.remotePort = remotePort;
        this.userId = userId;
        this.userName = userName;
        this.roles = roles;
    }

    /**
     * Authenticated HTTP servlet request.
     *
     * @param entityManager the entity manager
     * @param request the request
     * @param user the user
     * @param roles the user roles
     */
    public ProcessingContext(final EntityManager entityManager,
                             final HttpServletRequest request,
                             final User user,
                             final List<String> roles) {
        this.entityManager = entityManager;
        this.componentPort = Integer.parseInt(PropertiesUtil.getProperty("site", "http-port"));
        this.componentType = PropertiesUtil.getProperty("site", "site-type");
        this.serverName = request.getServerName();
        this.localIpAddress = request.getLocalAddr();
        this.remoteHost = request.getRemoteHost();
        this.remoteIpAddress = request.getRemoteAddr();
        this.remotePort = request.getRemotePort();
        this.userId = user != null ? user.getUserId() : null;
        this.userName = user != null ? user.getEmailAddress() : null;
        this.roles = roles;
    }

    /**
     * Anonymous or system to system HTTP request.
     *
     * @param entityManager the entity manager
     * @param request the HTTP servlet request
     */
    public ProcessingContext(final EntityManager entityManager,
                             final HttpServletRequest request) {
        this.entityManager = entityManager;
        this.componentPort = Integer.parseInt(PropertiesUtil.getProperty("site", "http"));
        this.componentType = PropertiesUtil.getProperty("site", "site-type");
        this.serverName = request.getServerName();
        this.localIpAddress = request.getLocalAddr();
        this.remoteHost = request.getRemoteHost();
        this.remoteIpAddress = request.getRemoteAddr();
        this.remotePort = request.getRemotePort();
        this.userId = null;
        this.userName = null;
        this.roles = new ArrayList<String>();
    }

    /**
     * Gets extension object.
     *
     * @param <T> The type of the object.
     * @param objectKey the object key
     * @return the service class singleton instance.
     */
    @SuppressWarnings({ "unchecked" })
    public <T> T getObject(final Object objectKey) {
        return (T) objectMap.get(objectKey);
    }

    /**
     * Puts extension object.
     *
     * @param <T> The type of the object.
     * @param objectKey the object key
     * @param object the object
     */
    public <T> void putObject(final Object objectKey, final T object) {
        objectMap.put(objectKey, object);
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public String getLocalIpAddress() {
        return localIpAddress;
    }

    public Integer getComponentPort() {
        return componentPort;
    }

    public String getComponentType() {
        return componentType;
    }

    public String getRemoteIpAddress() {
        return remoteIpAddress;
    }

    public Integer getRemotePort() {
        return remotePort;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getServerName() {
        return serverName;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public List<String> getRoles() {
        return roles;
    }

}
