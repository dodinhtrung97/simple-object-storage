package muic.backend.project0.repository;

import muic.backend.project0.entity.Bucket;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BucketRepository extends CrudRepository<Bucket, Integer> {

    /**
     * Find bucket by name
     * @param name
     * @return
     */
    Bucket findByName(String name);
}
