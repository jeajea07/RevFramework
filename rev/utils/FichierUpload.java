package rev.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FichierUpload {

    private String nom;        // nom sans extension : "5"
    private String extension;  // sans le point : "jpeg"
    private byte[] contenu;

    public FichierUpload(String nom, String extension, byte[] contenu) {
        this.nom = nom;
        this.extension = extension;
        this.contenu = contenu;
    }

    public String getNom() { return nom; }
    public String getExtension() { return extension; }
    public byte[] getContenu() { return contenu; }
    public long getTaille() { return contenu.length; }

    /** "5" + "jpeg" -> "5.jpeg" ; sans extension -> "5" */
    public String getNomComplet() {
        return extension.isEmpty() ? nom : nom + "." + extension;
    }

    /** Écrit le fichier dans le dossier avec son nom exact. Retourne le chemin écrit. */
    public Path sauvegarder(String dossier) throws IOException {
        Path dir = Paths.get(dossier).toAbsolutePath().normalize();
        Files.createDirectories(dir);

        Path cible = dir.resolve(getNomComplet()).normalize();
        if (!cible.startsWith(dir)) {
            throw new IOException("Nom de fichier invalide : " + getNomComplet());
        }

        Files.write(cible, contenu);   // crée ou remplace
        return cible;
    }
}