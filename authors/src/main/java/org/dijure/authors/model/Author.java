package org.dijure.authors.model;

/**
 * An Author bean.
 */
public class Author
{
    private String first = "";

    private String last = "";

    public Author()
    {
        // Required by Jackson parser
    }

    public String getFirst()
    {
        return first;
    }

    public String getLast()
    {
        return last;
    }
}
