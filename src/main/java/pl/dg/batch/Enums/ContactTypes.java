package pl.dg.batch.Enums;

public enum ContactTypes {

    unknown("0"),
    email("1"),
    phone("2"),
    jabber("3");

    private String idx;

    private ContactTypes(String idx)
    {
        this.idx = idx;
    }

    public String getIdx()
    {
        return this.idx;
    }
}
