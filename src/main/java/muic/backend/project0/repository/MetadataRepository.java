package muic.backend.project0.repository;

import muic.backend.project0.entity.ObjectMetadataComposite;
import muic.backend.project0.entity.Metadata;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetadataRepository extends CrudRepository<Metadata, ObjectMetadataComposite> {

    /**
     * Find object metadata by object id
     * @param objectId
     * @return
     */
    List<Metadata> findByObjectId(Integer objectId);
}
