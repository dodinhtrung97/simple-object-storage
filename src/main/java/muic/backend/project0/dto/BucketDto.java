package muic.backend.project0.dto;

import java.util.List;

public class BucketDto {
    private String created;
    private String modified;
    private String name;
    private List<ObjectDto> files;

    public BucketDto() {}

    public BucketDto(String created, String modified, String name, List<ObjectDto> files) {
        this.created = created;
        this.modified = modified;
        this.name = name;
        this.files = files;
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

    public List<ObjectDto> getFiles() {
        return files;
    }

    public void setFiles(List<ObjectDto> files) {
        this.files = files;
    }
}
