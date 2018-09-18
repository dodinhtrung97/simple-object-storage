package muic.backend.project0.services;

import com.mongodb.*;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;
import muic.backend.project0.model.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    private final Path bucketLocation;

    private Mongo mongo = new Mongo("localhost", 27017);
    private DB db = mongo.getDB("backend_upload");
    private DBCollection collection = db.getCollection("files");

    /**
     * Core storage service
     * @param bucket
     */
    @Autowired
    public FileStorageService(Bucket bucket) {
        this.bucketLocation = Paths.get(bucket.getName())
                .toAbsolutePath().normalize();
    }

    /**
     * Convert multipart type to file type
     * @param file
     * @return
     * @throws IOException
     */
    public File multipartToType(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    /**
     * Store file
     * @param multipartFile
     * @return
     */
    public String storeFile(MultipartFile multipartFile) throws IOException {
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        File file = this.multipartToType(multipartFile);

        GridFS gridfs = new GridFS(db, "fs");
        GridFSInputFile gfsFile = gridfs.createFile(file);
        gfsFile.setFilename(fileName);
        gfsFile.save();

        try {
            if (fileName.contains("..")) {
                throw new RuntimeException("Filename contains invalid path");
            }

            // Save filename to database
            BasicDBObject metaData = new BasicDBObject();
            metaData.put("fileName", fileName);
            collection.insert(metaData, WriteConcern.SAFE);

            Path location = this.bucketLocation.resolve(fileName);
            Files.copy(multipartFile.getInputStream(), location, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Fail to store file " + fileName);
        }
    }

    /**
     * Load existing file by fileName
     * @param fileName
     * @return
     */
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.bucketLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + fileName);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found " + fileName, e);
        }
    }
}
