package rev.utils;

import java.util.HashMap;
import java.util.Map;

public class RevSession {

    private Map<String, Object> donnees = new HashMap<>();

    public Object get(String cle) {
        return donnees.get(cle);
    }

    public void add(String cle, Object valeur) {
        donnees.put(cle, valeur);
    }

    public void remove(String cle) {
        donnees.remove(cle);
    }

    public Map<String, Object> getDonnees() {
        return donnees;
    }
}