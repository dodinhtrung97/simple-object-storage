package muic.backend.project0.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.lang.Object;

@Embeddable
public class ObjectPartComposite implements Serializable {

    @Column(name = "object_id")
    private Integer objectId;

    @Column(name = "part_number")
    private Integer partNumber;

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
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (!(object.getClass().getName() == "ObjectPartComposite"))
            return false;

        ObjectPartComposite that = (ObjectPartComposite) object;
        return Objects.equals(getObjectId(), that.getObjectId()) &&
                Objects.equals(getPartNumber(), that.getPartNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getObjectId(), getPartNumber());
    }
}
