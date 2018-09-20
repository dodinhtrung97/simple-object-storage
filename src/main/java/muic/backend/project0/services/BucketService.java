package muic.backend.project0.services;

import muic.backend.project0.dto.BucketDto;
import muic.backend.project0.dto.ObjectDto;
import muic.backend.project0.entity.Bucket;
import muic.backend.project0.entity.Object;
import muic.backend.project0.repository.BucketRepository;
import muic.backend.project0.repository.ObjectRepository;
import muic.backend.project0.util.Variable;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BucketService {

    @Autowired
    BucketRepository bucketRepository;

    @Autowired
    ObjectRepository objectRepository;

    Misc misc = new Misc();

    /**
     * Create bucket
     * @param bucketname
     * @return
     */
    public Optional<Bucket> createBucket(String bucketname) {

        Bucket bucket;

        if (!isValidBucketName(bucketname)) {
            throw new RuntimeException("Invalid bucket name");
        }

        try {
            Path path = Paths.get(Variable.ROOT_FOLDER + bucketname);
            Files.createDirectories(path);
            long currentTime = new Date().getTime();
            bucket = new Bucket(currentTime, currentTime, bucketname);
            bucketRepository.save(bucket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Optional.of(bucket);
    }

    /**
     * delete bucket
     * @param bucketname
     */
    public void deleteBucket(String bucketname) {
        try {
            FileUtils.deleteDirectory(new File(Variable.ROOT_FOLDER + bucketname));
            bucketRepository.delete(bucketRepository.findByName(bucketname));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * List objects (not get)
     * @param bucketname
     * @return
     */
    public BucketDto listBucketObjects(String bucketname) {

        if (!misc.isBucketExist(bucketname)) {
            throw new RuntimeException("Bucket does not exist");
        }

        List<Object> objects = getAllObject(bucketname);
        List<ObjectDto> objectDtos = new ArrayList<>();
        objects.forEach((object) -> objectDtos.add(new ObjectDto(object.getName(),
                                                        object.geteTag(),
                                                        Long.toString(object.getCreated()),
                                                        Long.toString(object.getModified()))));
        Bucket bucket = bucketRepository.findByName(bucketname);
        BucketDto result = new BucketDto(Long.toString(bucket.getCreated()),
                                            Long.toString(bucket.getModified()),
                                            bucket.getName(), objectDtos);
        return result;
    }

    /**
     * Get all objects
     * @param bucketname
     * @return
     */
    private List<Object> getAllObject(String bucketname) {
        return objectRepository.findByBucketId(getBucketIdByName(bucketname));
    }

    /**
     * Get bucket id
     * @param bucketname
     * @return
     */
    private Integer getBucketIdByName(String bucketname) {
        return bucketRepository.findByName(bucketname).getId();
    }

    /**
     * Check bucket name is valid
     * @param bucketname
     * @return
     */
    private Boolean isValidBucketName(String bucketname) {
        String regex = "^[A-Za-z0-9-_]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(bucketname);
        return matcher.matches();
    }
}
