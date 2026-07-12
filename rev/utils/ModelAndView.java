package rev.utils;

import java.util.HashMap;
import java.util.Map;

public class ModelAndView {

    private String vue;
    private Map<String, Object> donnees;

    public ModelAndView() {
        this.donnees = new HashMap<>();
    }

    public ModelAndView(String vue) {
        this.vue = vue;
        this.donnees = new HashMap<>();
    }

    public void addObject(String cle, Object valeur) {
        this.donnees.put(cle, valeur);
    }

    public String getVue() {
        return vue;
    }

    public void setVue(String vue) {
        this.vue = vue;
    }

    public Map<String, Object> getDonnees() {
        return donnees;
    }
}