package muic.backend.project0.entity;

import javax.persistence.*;

@Entity
public class Part {

    @Id
    private Integer number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "object_id")
    private Object object;

    private Integer length;
    private String md5;

    public Part() {}

    public Part(Integer number, Integer length, String md5, Object object) {
        this.number = number;
        this.length = length;
        this.md5 = md5;
        this.object = object;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
