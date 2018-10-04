package muic.backend.project0.dto;

import java.util.List;

public class BucketDto {
    private String created;
    private String modified;
    private String name;
    private List<ObjectDto> objects;

    public BucketDto() {}

    public BucketDto(String created, String modified, String name, List<ObjectDto> objects) {
        this.created = created;
        this.modified = modified;
        this.name = name;
        this.objects = objects;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ObjectDto> getObjects() {
        return objects;
    }

    public void setObjects(List<ObjectDto> objects) {
        this.objects = objects;
    }
}
