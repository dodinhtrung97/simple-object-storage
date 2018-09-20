package muic.backend.project0.controller;

import com.google.gson.JsonObject;
import muic.backend.project0.dto.BucketDto;
import muic.backend.project0.entity.Bucket;
import muic.backend.project0.services.BucketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class BucketController {

    @Autowired
    BucketService bucketService;

    @RequestMapping(value="/{bucketName}", method=RequestMethod.POST)
    public ResponseEntity createBucket(@PathVariable("bucketName") String bucketName,
                                        @RequestParam("create") String create) {

        Optional<Bucket> optionalBucket = bucketService.createBucket(bucketName);

        if (optionalBucket.isPresent()) {
            Bucket result = optionalBucket.get();
            JsonObject response = new JsonObject();

            response.addProperty("create", result.getCreated());
            response.addProperty("modified", result.getModified());
            response.addProperty("name", result.getName());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value="/{bucketName}", method=RequestMethod.DELETE)
    public ResponseEntity deleteBucket(@PathVariable("bucketName") String bucketName,
                                       @RequestParam("delete") String delete) {
        try {
            bucketService.deleteBucket(bucketName);
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value="/{bucketName}", method=RequestMethod.GET)
    public ResponseEntity getBucketObjects(@PathVariable("bucketName") String bucketName,
                                           @RequestParam("list") String list) {
        try {
            BucketDto bucketDto = bucketService.listBucketObjects(bucketName);
            return new ResponseEntity(bucketDto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
}
