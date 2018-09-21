package muic.backend.project0.services;

import muic.backend.project0.entity.*;
import muic.backend.project0.entity.Object;
import muic.backend.project0.repository.BucketRepository;
import muic.backend.project0.repository.MetadataRepository;
import muic.backend.project0.repository.ObjectRepository;
import muic.backend.project0.repository.PartRepository;
import muic.backend.project0.util.Variable;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.BoundedInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class FileService {

    @Autowired
    BucketRepository bucketRepository;

    @Autowired
    ObjectRepository objectRepository;

    @Autowired
    MetadataRepository metadataRepository;

    @Autowired
    PartRepository partRepository;

    Misc misc = new Misc();

    /**
     * Create ticket for object
     * @param bucketName
     * @param objectName
     */
    public void createTicket(String bucketName, String objectName) {
        if (!misc.isBucketExist(bucketName)) {
            throw new RuntimeException("Bucket not exist");
        }

        if (misc.isObjectExist(bucketName, objectName)) {
            throw new RuntimeException("ObjectDto already exist");
        }

        if (objectRepository.findByName(objectName) != null &&
                            objectRepository.findByName(objectName)
                                            .getName()
                                            .equals(objectName)) {
            throw new RuntimeException("ObjectDto already exist");
        }

        long currentTime = new Date().getTime();
        Object object = new Object();
        object.setName(objectName);
        object.setCreated(currentTime);
        object.setModified(currentTime);
        object.setBucket(bucketRepository.findByName(bucketName));
        object.setComplete(false);

        objectRepository.save(object);
    }

    /**
     * Delete object
     * @param bucketName
     * @param objectName
     */
    public void deleteObject(String bucketName, String objectName) {

        if (!misc.isBucketExist(bucketName)) {
            throw new RuntimeException("Invalid bucket name");
        }

        if (!misc.isObjectExist(bucketName, objectName)) {
            throw new RuntimeException("ObjectDto not exist");
        }

        try {
            Path objectPath = Paths.get(Variable.ROOT_FOLDER + bucketName + "/" + objectName);
            if (Files.deleteIfExists(objectPath)) {
                objectRepository.delete(objectRepository.findByName(objectName));
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not delete object");
        }
    }

    /**
     * Add update metadata by key
     * @param bucketName
     * @param objectName
     * @param key
     * @param value
     */
    public void addUpdateMetadataByKey(String bucketName, String objectName, String key, String value) {

        if (!misc.isBucketExist(bucketName)) {
            throw new RuntimeException("Invalid bucket name");
        }

        if (!misc.isObjectExist(bucketName, objectName)) {
            throw new RuntimeException("ObjectDto does not exist");
        }
        Object object = objectRepository.findByName(objectName);
        metadataRepository.save(new Metadata(new ObjectMetadataComposite(object.getId(), key), value));
    }

    /**
     * Delete metadata given key
     * @param bucketName
     * @param objectName
     * @param key
     */
    public void deleteMetadataByKey(String bucketName, String objectName, String key) {

        if (!misc.isBucketExist(bucketName)) {
            throw new RuntimeException("Invalid bucket name");
        }

        if (!misc.isObjectExist(bucketName, objectName)) {
            throw new RuntimeException("ObjectDto does not exist");
        }

        Object object = objectRepository.findByName(objectName);
        Metadata metadata = metadataRepository.findById(new ObjectMetadataComposite(object.getId(), key)).get();
        metadataRepository.deleteById(metadata.getId());
    }

    /**
     * Get metadata
     * @param bucketName
     * @param objectName
     * @param key
     * @return
     */
    public HashMap<String, String> getMetadataByKey(String bucketName, String objectName, String key) {

        if (!misc.isBucketExist(bucketName)) {
            throw new RuntimeException("Invalid bucket name");
        }

        if (!misc.isObjectExist(bucketName, objectName)) {
            throw new RuntimeException("Object does not exist");
        }

        Object object = objectRepository.findByName(objectName);
        Metadata metadata = metadataRepository.findById(new ObjectMetadataComposite(object.getId(), key)).get();
        return new HashMap<String, String>(){{
            put(key, metadata.getValue());
        }};
    }
    /**
     * Get all metadata
     * @param bucketName
     * @param objectName
     * @return
     */
    public HashMap<String, String> getAllMetadata(String bucketName, String objectName) {

        if (!misc.isBucketExist(bucketName)) {
            throw new RuntimeException("Invalid bucket name");
        }

        if (!misc.isObjectExist(bucketName, objectName)) {
            throw new RuntimeException("Object does not exist");
        }

        Object object = objectRepository.findByName(objectName);
        List<Metadata> metadatas = metadataRepository.findByObjectId(object.getId());
        HashMap<String, String> ret = new HashMap<>();
        metadatas.forEach((metadata) -> ret.put(metadata.getId().getMetadataName(), metadata.getValue()));
        return ret;
    }

    /**
     * Delete part
     * @param bucketName
     * @param objectName
     * @param partNumber
     */
    public void deletePart(String bucketName, String objectName, Integer partNumber) {
        try {

            String fileName = StringUtils.cleanPath(String.format("%05d", partNumber) + "_" + objectName);

            if (!misc.isBucketExist(bucketName)) {
                throw new RuntimeException("Invalid bucket name");
            }

            if (!isPartExist(bucketName, fileName)) {
                throw new RuntimeException("Invalid part");
            }

            if (!misc.isValidPartNumberRange(partNumber)) {
                throw new RuntimeException("Invalid part number");
            }

            if (!isObjectComplete(objectName)) {
                throw new RuntimeException("ObjectDto is already complete");
            }

            Path partPath = Paths.get(Variable.ROOT_FOLDER + bucketName + "/" + fileName);
            Object object = objectRepository.findByName(objectName);
            if (Files.deleteIfExists(partPath)) {
                partRepository.delete(partRepository.findByObjectIdAndNumber(object.getId(), partNumber));
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Check if object is done
     * @param objectName
     * @return
     */
    private Boolean isObjectComplete(String objectName) {
        Object object = objectRepository.findByName(objectName);
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
     * @param bucketName
     * @param objectName
     * @return
     */
    public HashMap<String, String> completeUpload(String bucketName, String objectName) {
        if (!misc.isBucketExist(bucketName)) {
            throw new RuntimeException("Invalid bucket name");
        }

        if (!misc.isValidObjectName(objectName)) {
            throw new RuntimeException("Invalid object name");
        }

        Object object = objectRepository.findByName(objectName);
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

        HashMap<String, String> response = new HashMap<>();
        response.put("eTag", eTag);
        response.put("length", length.toString());
        response.put("name", objectName);
        return response;
    }

    /**
     * Store object
     * @param request
     * @param bucketName
     * @param objectName
     * @param partNumber
     * @param partSize
     * @param partMd5
     * @return
     */
    public String saveObject(HttpServletRequest request, String bucketName, String objectName, Integer partNumber, Integer partSize, String partMd5) {
        String md5 = "";
        try {
            ServletInputStream file = request.getInputStream();
            String fileName = getObjectName(objectName, partNumber);
            if (!Files.exists(Paths.get(Variable.ROOT_FOLDER + bucketName))) {
                throw new RuntimeException("Invalid Bucket");
            }

            if ((objectRepository.findByName(objectName).getComplete() == null ||
                    !objectRepository.findByName(objectName).getComplete()) &&
                    !Files.exists(Paths.get(Variable.ROOT_FOLDER + bucketName + "/" + objectName))) {

                if (!misc.isValidPartNumberRange(partNumber)) {
                    throw new RuntimeException("Invalid Part Number");
                }

                if (!misc.isValidObjectName(objectName)) {
                    throw new RuntimeException("Invalid Object Name");
                }

                if (partSize != request.getContentLength()) {
                    throw new RuntimeException("Length Mismatched");
                }

                File targetFile = new File(Variable.ROOT_FOLDER + bucketName + "/" + fileName);
                FileUtils.copyInputStreamToFile(file, targetFile);
                md5 = misc.stringToMd5(Variable.ROOT_FOLDER + bucketName + "/" + fileName);
                if (!partMd5.equals(md5)) {
                    throw new RuntimeException("MD5Mismatched");
                }
                savePart(objectName, partNumber, request.getContentLength(), md5);
            }
            return md5;
        } catch (IOException ex) {
            throw new RuntimeException("Unable to save file");
        }
    }

    /**
     * Store parts
     * @param objectName
     * @param partNumber
     * @param partSize
     * @param partMd5
     */
    private void savePart(String objectName, Integer partNumber, Integer partSize, String partMd5) {
        try {
            Object object = objectRepository.findByName(objectName);
            partRepository.save(new Part(partNumber, partSize, partMd5, object));
            updateObjectETag(objectName);
        } catch (Exception e) {
            throw new RuntimeException("Fail to save part");
        }

    }

    /**
     * Get object name
     * @param objectName
     * @param partNumber
     * @return
     */
    private String getObjectName(String objectName, Integer partNumber) {
        return StringUtils.cleanPath(String.format("%05d", partNumber) + "_" + objectName);
    }

    /**
     * Get object w range
     * @param bucketName
     * @param objectName
     * @param range
     * @param input
     * @return
     */
    public SequenceInputStream getObjectWithRange(String bucketName, String objectName, String range, FileInputStream input) {

        HashMap<String, Long> ranges = misc.parseRange(range);
        long start = ranges.get("start");
        long end = ranges.get("end");

        if (end < start) {
            throw new RuntimeException("START > END");
        }

        Object object = objectRepository.findByName(objectName);
        List<Part> parts = partRepository.findByObjectId(object.getId());

        long objectLength = 0;
        for (Part part : parts) {
            objectLength += part.getLength();
        }

        if (end > objectLength) {
            throw new RuntimeException("Invalid range");
        }

        long currentPos = 0;
        List<InputStream> filesStream = new ArrayList<>();
        boolean started = false;
        try {
            for (Part part : parts) {
                input = new FileInputStream(Variable.ROOT_FOLDER + bucketName + "/" + getObjectName(objectName, part.getNumber()));
                long partLength = part.getLength() + currentPos;

                if (end > currentPos) {
                    if (start < partLength && end < partLength && !started) {
                        input.skip(start);
                        filesStream.add(new BoundedInputStream(input, end - start));
                        break;
                    } else if (start < partLength && end >= partLength) {
                        input.skip(start - currentPos);
                        filesStream.add(new BoundedInputStream(input, part.getLength() - start));
                    } else if (end < partLength) {
                        filesStream.add(new BoundedInputStream(input, end - currentPos));
                    } else if (start < currentPos || end > partLength && started) {
                        filesStream.add(new BoundedInputStream(input));
                    }
                }
                started = true;
                currentPos += part.getLength();
            }
            return new SequenceInputStream(Collections.enumeration(filesStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get object
     * @param bucketName
     * @param objectName
     * @return
     */
    public SequenceInputStream getObject(String bucketName, String objectName) {

        FileInputStream input;
        Object object = objectRepository.findByName(objectName);
        List<Part> parts = partRepository.findByObjectId(object.getId());

        long length = 0;
        for (Part part : parts) {
            length += part.getLength();
        }

        List<InputStream> filesStream = new ArrayList<>();

        try {
            for (Part part : parts) {
                input = new FileInputStream(Variable.ROOT_FOLDER +
                                            bucketName + "/" +
                                            getObjectName(objectName, part.getNumber()));
                filesStream.add(input);
            }
            return new SequenceInputStream(Collections.enumeration(filesStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get object's etag by name
     * @param objectName
     * @return
     */
    public String getETagByObjectName(String objectName) {
        return objectRepository.findByName(objectName).geteTag();
    }

    /**
     * Get object's etag
     * @param objectname
     * @return
     */
    public String getETagByBucketAndObjectName(String bucketname, String objectname) {
        Bucket bucket = bucketRepository.findByName(bucketname);
        if (bucket == null) {
            return "";
        }
        Object object = objectRepository.findByNameAndBucketId(objectname, bucket.getId());
        if (object == null) {
            return "";
        }
        return object.geteTag();
    }

    /**
     * Update object's etag
     * @param objectName
     */
    private void updateObjectETag(String objectName) {
        Object object = objectRepository.findByName(objectName);
        List<Part> parts = partRepository.findByObjectId(object.getId());
        long currentTime = new Date().getTime();
        List<String> md5List = new ArrayList<>();

        for (Part part : parts) {
            md5List.add(part.getMd5());
        }

        String eTag = misc.computeETag(md5List);

        object.setModified(currentTime);
        object.seteTag(eTag);
        objectRepository.save(object);
    }

    /**
     * Get object length
     * @param bucketName
     * @param objectName
     * @return
     */
    public Long getObjectLength(String bucketName, String objectName) {
        Bucket bucket = bucketRepository.findByName(bucketName);
        if (bucket == null) return 0L;

        Object object = objectRepository.findByNameAndBucketId(objectName, bucket.getId());
        if (object == null)  return 0L;

        List<Part> parts = partRepository.findByObjectId(object.getId());
        if (parts.isEmpty()) return 0L;

        long length = 0;
        for (Part part : parts) length += part.getLength();

        return length;
    }
}
