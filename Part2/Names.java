public class Names
{
    private String name;
    private boolean used;

    public Names(String name)
    {
        this.name = name;
        this.used = false;
    }

    public String getName()
    {
        return name;
    }

    public boolean isUsed()
    {
        return used;
    }

    public void setUsed(boolean used)
    {
        this.used = used;
    }
}