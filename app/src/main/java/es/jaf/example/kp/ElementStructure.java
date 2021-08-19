package es.jaf.example.kp;

public class ElementStructure {
    private final long id;
    private final String group;
    private final String title;
    private final String userName;
    private final String password;
    private final String url;
    private final String notes;

    public ElementStructure(long id, String group, String title, String userName, String password, String url, String notes) {
        this.id = id;
        this.group = (group == null || group.length() == 0) ? "Group" : group;
        this.title = title;
        this.userName = userName;
        this.password = password;
        this.url = url;
        this.notes = notes;
    }

    public long getId() {
        return id;
    }
    public String getGroup() {
        return group;
    }
    public String getTitle() {
        return title;
    }
    public String getUserName() {
        return userName;
    }
    public String getPassword() {
        return password;
    }
    public String getUrl() {
        return url;
    }
    public String getNotes() {
        return notes;
    }
    @Override
    public String toString() {
        return "\"" + group.replace("\"", "\"\"") + "\"," +
                "\"" + title.replace("\"", "\"\"") + "\"," +
                "\"" + userName.replace("\"", "\"\"") + "\"," +
                "\"" + password.replace("\"", "\"\"") + "\"," +
                "\"" + url.replace("\"", "\"\"") + "\"," +
                "\"" + notes.replace("\"", "\"\"") + "\"";
    }
}
