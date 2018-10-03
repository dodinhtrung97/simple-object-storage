package muic.backend.project0.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "bucket")
public class Bucket {

    @Id
    @GeneratedValue
    private int id;
    private long created;
    private long modified;
    private String name;

    @OneToMany(mappedBy = "bucket", cascade = CascadeType.ALL)
    private Set<Object> objects;

    public Bucket() {}

    public Bucket(long created, long modified, String name) {
        this.created = created;
        this.modified = modified;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getModified() {
        return modified;
    }

    public void setModified(long modified) {
        this.modified = modified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Object> getFile() {
        return objects;
    }

    public void setObjects(Set<Object> objects) {
        this.objects = this.objects;
    }
}
