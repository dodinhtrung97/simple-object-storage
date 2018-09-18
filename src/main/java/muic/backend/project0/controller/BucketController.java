package muic.backend.project0.controller;

import muic.backend.project0.model.Bucket;
import muic.backend.project0.services.BucketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

@RestController
public class BucketController {

    @Autowired
    private BucketService bucketService;

    /**
     * Create bucket
     * @param bucketName
     * @return
     * @throws JSONException
     */
    @PostMapping("/{bucketName}?create")
    public ResponseEntity createBucket(@RequestAttribute String bucketName) throws JSONException {
        Optional<Bucket> optionalBucket = bucketService.createBucket(bucketName);

        if (optionalBucket.isPresent()) {
            Bucket newBucket = optionalBucket.get();
            JSONObject response = new JSONObject();

            response.put("create", newBucket.getCreated());
            response.put("modified", newBucket.getModified());
            response.put("name", newBucket.getName());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Delete bucket
     * @param bucketName
     * @return
     * @throws JSONException
     * @throws IOException
     */
    @PostMapping("/{bucketName}?delete")
    public ResponseEntity deleteBucket(@RequestAttribute String bucketName) throws JSONException, IOException {
        boolean deleteSuccess = bucketService.deleteBucket(bucketName);

        if (deleteSuccess) {
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
}
