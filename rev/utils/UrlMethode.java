package rev.utils;

public class UrlMethode {

    private String url;
    private String methode;

    public UrlMethode(String url, String methode) {
        this.url = url;
        this.methode = methode;
    }

    public String getUrl() {
        return url;
    }

    public String getMethode() {
        return methode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof UrlMethode)) return false;
        UrlMethode other = (UrlMethode) obj;
        return url.equals(other.url) && methode.equalsIgnoreCase(other.methode);
    }

    @Override
    public int hashCode() {
        return url.hashCode() * 31 + methode.toUpperCase().hashCode();
    }

    @Override
    public String toString() {
        return methode + " " + url;
    }
}