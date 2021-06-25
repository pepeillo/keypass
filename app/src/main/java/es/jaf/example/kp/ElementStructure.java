package es.jaf.example.kp;

public class ElementStructure {
    private long id;
    private String title;
    private String userName;
    private String password;
    private String url;
    private String notes;

    public ElementStructure(long id, String title, String userName, String password, String url, String notes) {
        this.id = id;
        this.title = title;
        this.userName = userName;
        this.password = password;
        this.url = url;
        this.notes = notes;
    }

    public long getId() {
        return id;
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
        return "\"Raíz\"," +
                "\"" + title.replace("\"", "\"\"") + "\"," +
                "\"" + userName.replace("\"", "\"\"") + "\"," +
                "\"" + password.replace("\"", "\"\"") + "\"," +
                "\"" + url.replace("\"", "\"\"") + "\"," +
                "\"" + notes.replace("\"", "\"\"") + "\"";
    }
}
