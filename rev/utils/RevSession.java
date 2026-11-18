package rev.utils;

import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RevSession {

    private final HttpSession session;

    public RevSession(HttpSession session) {
        this.session = session;
    }

    public void setAttribute(String key, Object value) {
        session.setAttribute(key, value);
    }

    public void removeAttribute(String key) {
        session.removeAttribute(key);
    }

    public Object getAttribute(String key) {
        return session.getAttribute(key);
    }

    public <T> T getAttribute(String key, Class<T> type) {
        Object value = session.getAttribute(key);
        if (value == null) return null;
        if (!type.isInstance(value)) {
            throw new ClassCastException("L'attribut '" + key + "' est de type "
                + value.getClass().getName() + ", pas " + type.getName());
        }
        return type.cast(value);
    }

    public Map<String, Object> getAttributes() {
        Map<String, Object> copie = new HashMap<>();
        for (String nom : Collections.list(session.getAttributeNames())) {
            copie.put(nom, session.getAttribute(nom));
        }
        return copie;
    }

    public void clear() {
        for (String nom : Collections.list(session.getAttributeNames())) {
            session.removeAttribute(nom);
        }
    }

    public String getId() {
        return session.getId();
    }
}