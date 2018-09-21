package muic.backend.project0.controller;

import com.google.gson.JsonObject;
import muic.backend.project0.services.FileService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.HashMap;
import java.util.Optional;

@RestController
public class ObjectController {

    @Autowired
    FileService fileService;

    @RequestMapping(value = "/{bucketName}/{objectName}", method = RequestMethod.POST)
    public ResponseEntity createTicket(@PathVariable("bucketName") String bucketName,
                                       @PathVariable("objectName") String objectName,
                                       @RequestParam("create") String create) {
        try {
            fileService.createTicket(bucketName, objectName);
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/{bucketName}/{objectName}", params = "delete", method = RequestMethod.DELETE)
    public ResponseEntity deleteObject(@PathVariable("bucketName") String bucketName,
                                       @PathVariable("objectName") String objectName,
                                       @RequestParam("delete") String delete) {
        try {
            fileService.deleteObject(bucketName, objectName);
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/{bucketname}/{objectname}", method = RequestMethod.PUT)
    public ResponseEntity addUpdateMetadataByKey(
            @PathVariable("bucketname") String bucketname,
            @PathVariable("objectname") String objectname,
            @RequestParam("metadata") String metadataAction,
            @RequestParam("key") String key,
            @RequestBody String value
    ) {
        try {
            fileService.addUpdateMetadataByKey(bucketname, objectname, key, value);
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/{bucketname}/{objectname}", params = "metadata", method = RequestMethod.DELETE)
    public ResponseEntity deleteMetadataByKey(
            @PathVariable("bucketname") String bucketname,
            @PathVariable("objectname") String objectname,
            @RequestParam("metadata") String metadataAction,
            @RequestParam("key") String key
    ) {
        try {
            fileService.deleteMetadataByKey(bucketname, objectname, key);
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/{bucketname}/{objectname}", params = "key", method = RequestMethod.GET)
    public ResponseEntity getMetadataByKey(
            @PathVariable("bucketname") String bucketname,
            @PathVariable("objectname") String objectname,
            @RequestParam("metadata") String metadata,
            @RequestParam("key") String key
    ) {
        try {
            HashMap<String, String> ret = fileService.getMetadataByKey(bucketname, objectname, key);
            return ResponseEntity.ok(ret);
        } catch (Exception e) {
            if (e.getMessage() == "No value present") {
                    return new ResponseEntity(HttpStatus.OK);
            } else {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
        }
    }

    @RequestMapping(value = "/{bucketname}/{objectname}", params = "metadata", method = RequestMethod.GET)
    public ResponseEntity getAllMetadata(
            @PathVariable("bucketname") String bucketname,
            @PathVariable("objectname") String objectname,
            @RequestParam("metadata") String metadata
    ) {
        try {
            HashMap<String, String> response = fileService.getAllMetadata(bucketname, objectname);
            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/{bucketname}/{objectname}", params = "partNumber", method = RequestMethod.PUT)
    public ResponseEntity handleUploadPart(
            @PathVariable("bucketname") String bucketname,
            @PathVariable("objectname") String objectname,
            @RequestParam("partNumber") Integer partNumber,
            @RequestHeader("Content-Length") Integer partSize,
            @RequestHeader("Content-MD5") String partMd5,
            HttpServletRequest requestServlet
    ) {
        try {
            String md5 = fileService.saveObject(requestServlet, bucketname, objectname, partNumber, partSize, partMd5);

            JsonObject response = new JsonObject();
            response.addProperty("md5", md5);
            response.addProperty("partSize", partSize);
            response.addProperty("partNumber", partNumber);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            JsonObject response = new JsonObject();
            response.addProperty("partMd5", partMd5);
            response.addProperty("partSize", partSize);
            response.addProperty("partNumber", partNumber);
            response.addProperty("error", e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/{bucketname}/{objectname}", method = RequestMethod.DELETE)
    public ResponseEntity deletePart(
            @PathVariable("bucketname") String bucketname,
            @PathVariable("objectname") String objectname,
            @RequestParam("partNumber") Integer partNumber
    ) {
        try {
            fileService.deletePart(bucketname, objectname, partNumber);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/{bucketName}/{objectName}", params = "complete", method = RequestMethod.POST)
    public ResponseEntity completeUpload(
            @PathVariable("bucketName") String bucketName,
            @PathVariable("objectName") String objectName,
            @RequestParam("complete") String complete
    ) {
        try {
            HashMap<String, String> result = fileService.completeUpload(bucketName, objectName);

            JsonObject response = new JsonObject();
            response.addProperty("eTag", result.get("eTag"));
            response.addProperty("name", result.get("name"));
            response.addProperty("length", result.get("length"));

            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception e) {
            String eTag = fileService.getETagByBucketAndObjectName(bucketName, objectName);
            Long length = fileService.getObjectLength(bucketName, objectName);

            JsonObject response = new JsonObject();
            response.addProperty("eTag", eTag);
            response.addProperty("length", length);
            response.addProperty("name", objectName);
            response.addProperty("error", e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/{bucketname}/{objectname}", method = RequestMethod.GET)
    public ResponseEntity downloadObject(
            @PathVariable("bucketname") String bucketname,
            @PathVariable("objectname") String objectname,
            @RequestHeader("range") Optional<String> range,
            HttpServletResponse response
    ) {
        FileInputStream input = null;
        try {
            SequenceInputStream sequenceInputStream;
            if (range.isPresent()) {
                sequenceInputStream = fileService.getObjectWithRange(bucketname, objectname, range.get(), input);
            } else {
                sequenceInputStream = fileService.getObject(bucketname, objectname);
            }
            response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", objectname));
            response.setHeader("ETag", fileService.getETagByObjectName(objectname));

            IOUtils.copyLarge(sequenceInputStream, response.getOutputStream());
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        } finally {
            System.out.println("Finally");
            try {
                if (input != null){
                    System.out.println("close");
                    input.close();
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }

    }
}
