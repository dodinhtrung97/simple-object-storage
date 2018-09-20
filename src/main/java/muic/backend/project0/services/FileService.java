package muic.backend.project0.services;

import muic.backend.project0.entity.Metadata;
import muic.backend.project0.entity.Object;
import muic.backend.project0.entity.ObjectMetadataComposite;
import muic.backend.project0.entity.Part;
import muic.backend.project0.repository.BucketRepository;
import muic.backend.project0.repository.FileRepository;
import muic.backend.project0.repository.MetadataRepository;
import muic.backend.project0.repository.PartRepository;
import muic.backend.project0.util.Variable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class FileService {

    @Autowired
    BucketRepository bucketRepository;

    @Autowired
    FileRepository objectRepository;

    @Autowired
    MetadataRepository metadataRepository;

    @Autowired
    PartRepository partRepository;

    Misc misc = new Misc();

    /**
     * Create ticket for object
     * @param bucketname
     * @param objectname
     */
    public void createTicket(String bucketname, String objectname) {
        if (!misc.isBucketExist(bucketname)) {
            throw new RuntimeException("Bucket not exist");
        }

        if (misc.isObjectExist(bucketname, objectname)) {
            throw new RuntimeException("ObjectDto already exist");
        }

        if (objectRepository.findByName(objectname) != null &&
                            objectRepository.findByName(objectname)
                                            .getName()
                                            .equals(objectname)) {
            throw new RuntimeException("ObjectDto already exist");
        }

        long currentTime = new Date().getTime();
        Object object = new Object();
        object.setName(objectname);
        object.setCreated(currentTime);
        object.setModified(currentTime);
        object.setBucket(bucketRepository.findByName(bucketname));
        object.setComplete(false);

        objectRepository.save(object);
    }

    /**
     * Delete object
     * @param bucketname
     * @param objectname
     */
    public void deleteObject(String bucketname, String objectname) {

        if (!misc.isBucketExist(bucketname)) {
            throw new RuntimeException("Invalid bucket name");
        }

        if (!misc.isObjectExist(bucketname, objectname)) {
            throw new RuntimeException("ObjectDto not exist");
        }

        try {
            Path objectPath = Paths.get(Variable.ROOT_FOLDER + bucketname + "/" + objectname);
            if (Files.deleteIfExists(objectPath)) {
                objectRepository.delete(objectRepository.findByName(objectname));
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not delete object");
        }

    }

    /**
     * Add update metadata by key
     * @param bucketname
     * @param objectname
     * @param key
     * @param value
     */
    public void addUpdateMetadataByKey(String bucketname, String objectname, String key, String value) {

        if (!misc.isBucketExist(bucketname)) {
            throw new RuntimeException("Invalid bucket name");
        }

        if (!misc.isObjectExist(bucketname, objectname)) {
            throw new RuntimeException("ObjectDto does not exist");
        }
        Object object = objectRepository.findByName(objectname);
        metadataRepository.save(new Metadata(new ObjectMetadataComposite(object.getId(), key), value));
    }

    /**
     * Delete metadata given key
     * @param bucketname
     * @param objectname
     * @param key
     */
    public void deleteMetadataByKey(String bucketname, String objectname, String key) {

        if (!misc.isBucketExist(bucketname)) {
            throw new RuntimeException("Invalid bucket name");
        }

        if (!misc.isObjectExist(bucketname, objectname)) {
            throw new RuntimeException("ObjectDto does not exist");
        }

        Object object = objectRepository.findByName(objectname);
        Metadata metadata = metadataRepository.findById(new ObjectMetadataComposite(object.getId(), key)).get();
        metadataRepository.deleteById(metadata.getId());
    }

    /**
     * Get metadata
     * @param bucketname
     * @param objectname
     * @param key
     * @return
     */
    public HashMap<String, String> getMetadataByKey(String bucketname, String objectname, String key) {

        if (!misc.isBucketExist(bucketname)) {
            throw new RuntimeException("Invalid bucket name");
        }

        if (!misc.isObjectExist(bucketname, objectname)) {
            throw new RuntimeException("ObjectDto does not exist");
        }

        Object object = objectRepository.findByName(objectname);
        Metadata metadata = metadataRepository.findById(new ObjectMetadataComposite(object.getId(), key)).get();
        return new HashMap<String, String>(){{
            put(key, metadata.getValue());
        }};
    }
    /**
     * Get all metadata
     * @param bucketname
     * @param objectname
     * @return
     */
    public HashMap<String, String> getAllMetadata(String bucketname, String objectname) {

        if (!misc.isBucketExist(bucketname)) {
            throw new RuntimeException("Invalid bucket name");
        }

        if (!misc.isObjectExist(bucketname, objectname)) {
            throw new RuntimeException("ObjectDto does not exist");
        }

        Object object = objectRepository.findByName(objectname);
        List<Metadata> metadatas = metadataRepository.findByObjectId(object.getId());
        HashMap<String, String> ret = new HashMap<>();
        metadatas.forEach((metadata) -> ret.put(metadata.getId().getMetadataName(), metadata.getValue()));
        return ret;
    }

    /**
     * Delete part
     * @param bucketname
     * @param objectname
     * @param partNumber
     */
    public void deletePart(String bucketname, String objectname, Integer partNumber) {
        try {

            String fileName = StringUtils.cleanPath(String.format("%05d", partNumber) + "_" + objectname);

            if (!misc.isBucketExist(bucketname)) {
                throw new RuntimeException("Invalid bucket name");
            }

            if (!isPartExist(bucketname, fileName)) {
                throw new RuntimeException("Invalid part");
            }

            if (!misc.isValidPartNumberRange(partNumber)) {
                throw new RuntimeException("Invalid part number");
            }

            if (!isObjectComplete(objectname)) {
                throw new RuntimeException("ObjectDto is already complete");
            }

            Path partPath = Paths.get(Variable.ROOT_FOLDER + bucketname + "/" + fileName);
            Object object = objectRepository.findByName(objectname);
            if (Files.deleteIfExists(partPath)) {
                partRepository.delete(partRepository.findByObjectIdAndNumber(object.getId(), partNumber));
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Check if object is done
     * @param objectname
     * @return
     */
    private Boolean isObjectComplete(String objectname) {
        Object object = objectRepository.findByName(objectname);
        System.out.println(object.getComplete());
        return object.getComplete() != null && object.getComplete() == false;
    }

    /**
     * Check if part exists
     * @param bucketname
     * @param partName
     * @return
     */
    private Boolean isPartExist(String bucketname, String partName) {
        Path partPath = Paths.get(Variable.ROOT_FOLDER + bucketname + "/" + partName);
        return Files.exists(partPath);
    }

    /**
     * Complete upload notice
     * @param bucketname
     * @param objectname
     * @return
     */
    public HashMap<String, String> completeUpload(String bucketname, String objectname) {
        if (!misc.isBucketExist(bucketname)) {
            throw new RuntimeException("Invalid bucket name");
        }

        if (!misc.isValidObjectName(objectname)) {
            throw new RuntimeException("Invalid object name");
        }

        Object object = objectRepository.findByName(objectname);
        List<Part> parts = partRepository.findByObjectId(object.getId());
        long currentTime = new Date().getTime();
        Integer length = 0;
        List<String> md5List = new ArrayList<>();

        for (Part part : parts) {
            md5List.add(part.getMd5());
            length += part.getLength();
        }

        String eTag = misc.computeETag(md5List);

        object.setModified(currentTime);
        object.seteTag(eTag);
        object.setComplete(true);
        objectRepository.save(object);

        HashMap<String, String> ret = new HashMap<>();
        ret.put("eTag", eTag);
        ret.put("length", length.toString());
        ret.put("name", objectname);
        return ret;
    }
}
