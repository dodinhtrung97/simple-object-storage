package muic.backend.project0.repository;

import muic.backend.project0.entity.Part;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartRepository extends CrudRepository<Part, Integer> {

    /**
     * Find all parts by object id
     * @param fileId
     * @return
     */
    List<Part> findByObjectId(Integer fileId);

    /**
     * Find part by object id and part number
     * @param objectId
     * @param number
     * @return
     */
    Part findByObjectIdAndNumber(Integer objectId, Integer number);
}
