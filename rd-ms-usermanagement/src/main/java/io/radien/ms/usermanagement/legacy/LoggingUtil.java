package io.radien.ms.usermanagement.legacy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Bruno Gama
 */

public @RequestScoped
class LoggingUtil {
    public static final String SYSTEM_NAME = "System";

    private static final Logger log = LoggerFactory.getLogger(LoggingUtil.class);

    @Inject
    private InjectionUtil injectionUtil;
    @Inject
    private UserSession userSession;

    public static <T> String buildLogCollection(Collection<T> list) {
        assert list != null;
        if (list.isEmpty()) {
             return "Collection contents: []";
        } else {
            return buildLog("Collection contents: {}", collectionToString(list));
        }
    }

    public static <T> String collectionToString(Collection<T> list){
        assert list != null;
        List<String> values = new ArrayList<>();
        for (T object : list) {
            if (object != null) {
                try {
                    String toString = object.toString();
                    if (toString != null && toString.isEmpty()) {
                        values.add(toString);
                    } else {
                        values.add(object.getClass().getName() + "@" + Integer.toHexString(object.hashCode()));
                    }
                } catch (NullPointerException e) {
                    values.add(object.getClass().getName() + "@" + Integer.toHexString(object.hashCode()));
                    LoggingUtil.log.warn("The object of the type {} as thrown as {} when invoking the method toString", object.getClass().getName(), e.getClass().getName());
                }
            } else {
                values.add("NULL");
            }
        }
        return "[ "+String.join(" , ", values)+" ]";
    }

    private static Integer getNumberOfResults(Object object) {
        if (object instanceof Collection) {
            return ((Collection) object).size();
        } else {
            if (object != null) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public static String buildLog(String format, Object ... arguments){
        return MessageFormatter.arrayFormat(format, arguments).getMessage();
    }

	private static String buildAuditLog(String format, SystemUser user, Object... arguments) {
		if (user == null)
            return buildLog("[{}] " + format, SYSTEM_NAME, arguments);
		else
            return buildLog("[{}] " + format, user.getUserEmail(), arguments);
    }

    private static String buildAuditLogNumberOfResults(Object results, SystemUser user) {
		if (user == null)
            return buildLog("[{}] Returned {} results(s)", SYSTEM_NAME, getNumberOfResults(results));
		else
            return buildLog("[{}] Returned {} results(s)", user.getUserEmail(), getNumberOfResults(results));
    }

    public String buildAuditLog(String format, Object... arguments) {
        if (injectionUtil.isSessionScopeActive()) {
            return buildAuditLog(format, userSession.getUser(true), arguments);
        } else {
            return buildAuditLog(format, null, arguments);
        }
    }

    public String buildAuditLogNumberOfResults(Object results) {
        if (injectionUtil.isSessionScopeActive()) {
            return buildAuditLogNumberOfResults(results, userSession.getUser(true));
        } else {
            return buildAuditLogNumberOfResults(results, null);
        }
    }

}
