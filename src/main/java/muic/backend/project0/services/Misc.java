package muic.backend.project0.services;

import muic.backend.project0.util.Constant;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Misc {

    /**
     * Check if bucket exists
     * @param bucketname
     * @return
     */
    public Boolean isBucketExist(String bucketname) {
        Path bucketPath = Paths.get(Constant.ROOT_FOLDER + bucketname);
        return Files.exists(bucketPath);
    }

    /**
     * Check if object exists
     * @param bucketname
     * @param objectname
     * @return
     */
    public Boolean isObjectExist(String bucketname, String objectname) {
        Path objectPath = Paths.get(Constant.ROOT_FOLDER + bucketname + "/" + objectname);
        return Files.exists(objectPath);
    }

    /**
     * Calculate etag
     * @param md5List
     * @return
     */
    public String computeETag(List<String> md5List) {
        StringBuilder eTag = new StringBuilder();

        for (String s : md5List)
            eTag.append(s);

        return this.stringToMd5(eTag.toString()) + "-" + md5List.size();
    }

    /**
     * Convert string to md5 for object upload
     * @param input
     * @return
     */
    public String stringToMd5(String input) {
        try {
            return DigestUtils.md5Hex(input);
        } catch (Exception e) {
            throw new RuntimeException("Unable to calculate md5");
        }
    }

    /**
     * Convert file to md5
     * @param file
     * @return
     */
    public String fileToMd5(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            return DigestUtils.md5Hex(fis);
        } catch (Exception e) {
            throw new RuntimeException("Unable to calculate md5");
        }
    }

    /**
     * Check requested name is valid
     * @param objectname
     * @return
     */
    public Boolean isValidObjectName(String objectname) {
        String regex = "^[A-Za-z0-9-_]+[A-Za-z0-9.-_]*[A-Za-z0-9_-]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(objectname);
        return matcher.matches();
    }

    /**
     * Check valid range
     * @param partNumber
     * @return
     */
    public Boolean isValidPartNumberRange(Integer partNumber) {
        return partNumber > 0 && partNumber <= 10000;
    }

    /**
     * Parse start-end range
     * @param range
     * @return
     */
    public HashMap<String, Long> parseRange(String range) {
        try {
            String[] ranges = range.split("-");
            return new HashMap<String, Long>(){{
                put("start", Long.valueOf(ranges[0]));
                put("end", Long.valueOf(ranges[1]));
            }};
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
