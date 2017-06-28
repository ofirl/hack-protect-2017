package DataClasses;

public class HeadlineSites {
    private int id;
    private String url;
    private int reportersCount;


    public HeadlineSites(int id, String url, int reportersCount) {
        this.id = id;
        this.url = url;
        this.reportersCount = reportersCount;
    }
    public int getReportersCount() {
        return this.reportersCount;
    }

}
