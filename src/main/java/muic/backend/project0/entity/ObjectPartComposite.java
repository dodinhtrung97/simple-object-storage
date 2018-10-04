package muic.backend.project0.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.lang.Object;
import java.util.Objects;

@Embeddable
public class ObjectPartComposite implements Serializable {
    @Column(name = "object_id")
    private Integer objectId;

    @Column(name = "part_number")
    private Integer partNumber;

    public ObjectPartComposite() {}

    public ObjectPartComposite(Integer objectId, Integer partNumber) {
        this.objectId = objectId;
        this.partNumber = partNumber;
    }

    public Integer getObjectId() {
        return objectId;
    }

    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }

    public Integer getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(Integer partNumber) {
        this.partNumber = partNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ObjectPartComposite)) return false;
        ObjectPartComposite that = (ObjectPartComposite) obj;
        return Objects.equals(getObjectId(), that.getObjectId()) &&
                Objects.equals(getPartNumber(), that.getPartNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getObjectId(), getPartNumber());
    }
}