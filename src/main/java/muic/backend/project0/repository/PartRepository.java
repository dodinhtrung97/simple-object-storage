package muic.backend.project0.repository;

import muic.backend.project0.entity.ObjectPartComposite;
import muic.backend.project0.entity.Part;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartRepository extends CrudRepository<Part, ObjectPartComposite> {

    /**
     * Find all parts by object id
     * @param objectId
     * @return
     */
    List<Part> findByObjectId(Integer objectId);
}
