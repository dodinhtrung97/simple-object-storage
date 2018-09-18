package muic.backend.project0.services;

import muic.backend.project0.model.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Optional;

@Service
public class BucketService {

    private final Path bucketLocation;

    /**
     * Core service
     * @param bucket
     */
    @Autowired
    public BucketService(Bucket bucket) {
        this.bucketLocation = Paths.get(bucket.getName())
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.bucketLocation);
        } catch (Exception e) {
            throw new RuntimeException("Fail to create directory");
        }
    }

    /**
     * Create new bucket
     * @param bucketName
     * @return
     */
    public Optional<Bucket> createBucket(String bucketName) {
        File file = new File(".\\" + bucketName);

        Bucket bucket = null;

        if (!file.exists()) {
            if (file.mkdir()) {
                bucket = new Bucket();

                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date = new Date();
                String formattedDate = dateFormat.format(date);

                bucket.setCreated(formattedDate);
                bucket.setModified(formattedDate);
                bucket.setName(bucketName);
            }
        }

        Optional<Bucket> optionalBucket = Optional.of(bucket);
        return optionalBucket;
    }

    /**
     * Delete bucket
     * @param bucketName
     * @return
     */
    public boolean deleteBucket(String bucketName) throws IOException {
        File file = new File(".\\" + bucketName);

        if (file.isDirectory()) {
            Path path = file.toPath();

            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }

        return !file.isDirectory();
    }
}
