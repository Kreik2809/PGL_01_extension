package user;

/**
 * La classe Address représente une adresse.
 */
public class Address
{
    private String street;
    private String number;
    private String postCode;
    private String city;
    private String extra;

    public Address(String street, String number, String postCode, String city, String extra)
    {
        this.street = street;
        this.number = number;
        this.postCode = postCode;
        this.city = city;
        this.extra = extra;
    }

    /**
     * méthode qui permet de récuperer la rue
     * @return la rue
     */
    public String getStreet()
    {
        return street;
    }

    /**
     * méthode qui permet de changer la rue
     * @param street la rue
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * méthode qui permet de récuperer le numéro de maison/batiment
     * @return le numéro de maison/batiment
     */
    public String getNumber() {
        return number;
    }

    /**
     * méthode qui permet de chnager le numéro de maison/batiment
     * @param number le numéro de maison/batiment
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * méthode qui permet de récuperer le code postal
     * @return le code postal
     */
    public String getPostCode() {
        return postCode;
    }

    /**
     * méthode qui permet de changer le code postal
     * @param postCode le code postal
     */
    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    /**
     * méthode qui permet de récuperer la ville
     * @return la ville
     */
    public String getCity() {
        return city;
    }

    /**
     * méthode qui permet de changer la ville
     * @param city la ville
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * méthode qui permet de récuperer les informations complémentaires concernant l'adresse (infos extras)
     * @return les informations complémentaires
     */
    public String getExtra() {
        return extra;
    }

    /**
     * méthode qui permet de changer les informations complémentaires concernant l'adresse (infos extras)
     * @param extra les informations complémentaires
     */
    public void setExtra(String extra) {
        this.extra = extra;
    }
}
