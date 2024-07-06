package jp.aoyama.h15822097.watch_heartrateapp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataHolder {
    private static DataHolder instance;
    private String personalid;
    private Date date;
    private SimpleDateFormat sdf;

    private DataHolder() {
        sdf = new SimpleDateFormat("yyyy-MM-dd");
    }

    // インスタンスを取得するためのメソッド
    public static synchronized DataHolder getInstance() {
        if (instance == null) {
            instance = new DataHolder();
        }
        return instance;
    }

    // Dateを取得
    public String getDate() {
        if (date != null) {
            return sdf.format(date);
        }
        return null;
    }

    // Dateをセット
    public void setDate(Date date) {
        this.date = date;
    }

    // idを取得
    public String getPersonalid() {
        return personalid;
    }

    // idをセット
    public void setPersonalid(String personalid) {
        this.personalid = personalid;
    }
}
