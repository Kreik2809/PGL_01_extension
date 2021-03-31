package tool;

import org.apache.commons.lang3.RandomStringUtils;
import tool.sql.UserDB;
import user.UserType;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class SignIn {
    private static ArrayList<String> info;

    /**
     * fonction qui permet de connecter un utilisateur à la BD
     *
     * @param mail L'adresse mail de l'utilisateur essayant de se connecter
     * @return true si le nom d'utilisateur et le mot de passe sont correctes, false sinon
     */
    public static boolean log(String mail, String password) {
        info = new ArrayList<>();
        try {
            info = HttpRequest.Get(URLDB.URL + "User/MAIL=" + mail, new ParseJson(UserDB.COLUMNS, "NO USER", "PLEASE USE THIS SYNTAX : /RestAPI/User/MAIL=exemple@adresse.x") {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (info.size() != 0)
            return info.get(1).equals(encryption(password));
        return false;
    }

    /**
     * fonction qui permet de s'enregistrer
     * @param username Le nom d'utilisateur
     * @param password Le mot de passe
     * @return true si l'utilisateur a bien été enregistré, false sinon
     */
    public static boolean signUp(String mail, String username, String password, UserType type) {
        try {
            HttpRequest.Post(URLDB.URL + "User/?MAIL=" + mail + "&PWD=" + encryption(password) + "&TYPE=" + type.name() + "&USERNAME=" + username);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (log(mail, password))
            return true;
        else
            return false;
    }

    /**
     * fonction qui permet de chiffrer un mot de passe (http://www.codeurjava.com/2016/12/hashage-md5-et-sha-256-en-java.html)
     *
     * @param password le mot de passe
     * @return le mot de passe chiffré
     */
    public static String encryption(String password) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(password.getBytes());

        byte byteData[] = md.digest();

        //convertir le tableau de bits en une format hexadécimal
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    public static String generatePassword()
    {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'(#[]$%,.;:/\\=+-*_-|{})";
        int nbr = (int) (7 + (Math.random() * (20 - 7)));
        return RandomStringUtils.random( nbr, characters );
    }

    public static ArrayList<String> getInfo() {
        return info;
    }
}
