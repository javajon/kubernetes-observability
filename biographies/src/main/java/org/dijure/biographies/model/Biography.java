package org.dijure.biographies.model;

/**
 * A biographies bean.
 */
public class Biography
{
    private String birth = "";

    private String occupation = "";

    private String nationality = "";

    public Biography()
    {
        // Required by Jackson parser
    }

    public String getBirth()
    {
        return birth;
    }

    public void setBirth(String birth)
    {
        this.birth = birth;
    }

    public String getOccupation()
    {
        return occupation;
    }

    public void setOccupation(String occupation)
    {
        this.occupation = occupation;
    }

    public String getNationality()
    {
        return nationality;
    }

    public void setNationality(String nationality)
    {
        this.nationality = nationality;
    }

    @Override
    public String toString()
    {
        return "Biography{" +
                "birth='" + birth + '\'' +
                ", occupation='" + occupation + '\'' +
                ", nationality='" + nationality + '\'' +
                '}';
    }
}
