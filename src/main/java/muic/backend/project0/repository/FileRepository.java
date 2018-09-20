package muic.backend.project0.repository;

import muic.backend.project0.entity.Object;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends CrudRepository<Object, Integer> {

    /**
     * Find object by name
     * @param name
     * @return
     */
    Object findByName(String name);

    /**
     * Find all objects by bucket id
     * @param bucketId
     * @return
     */
    List<Object> findByBucketId(Integer bucketId);
}
