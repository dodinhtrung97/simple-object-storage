package muic.backend.project0.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;
import java.lang.Object;

@Embeddable
public class ObjectMetadataComposite {

    @Column(name = "object_id")
    private Integer objectId;

    @Column(name = "metadata_name")
    private String metadataName;

    public ObjectMetadataComposite() {}

    public ObjectMetadataComposite(Integer objectId, String metadataName) {
        this.objectId = objectId;
        this.metadataName = metadataName;
    }

    public Integer getObjectId() {
        return objectId;
    }

    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }

    public String getMetadataName() {
        return metadataName;
    }

    public void setMetadataName(String metadataName) {
        this.metadataName = metadataName;
    }

    @Override
    public boolean equals(Object fileStorage) {
        if (this == fileStorage)
            return true;
        if (!(fileStorage.getClass().getName() == "ObjectMetadataComposite"))
            return false;
        ObjectMetadataComposite that = (ObjectMetadataComposite) fileStorage;
        return Objects.equals(getObjectId(), that.getObjectId()) &&
                Objects.equals(getMetadataName(), that.getMetadataName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getObjectId(), getMetadataName());
    }
}
