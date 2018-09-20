package muic.backend.project0.entity;

import javax.persistence.*;

@Entity
public class Metadata {

    @EmbeddedId
    private ObjectMetadataComposite id;
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "object_id")
    private Object object;

    public Metadata() {}

    public Metadata(ObjectMetadataComposite id, String value) {
        this.id = id;
        this.value = value;
    }

    public ObjectMetadataComposite getId() {
        return id;
    }

    public void setId(ObjectMetadataComposite id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
