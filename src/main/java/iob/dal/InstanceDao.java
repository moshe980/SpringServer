package iob.dal;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Streamable;

import iob.data.InstanceEntity;

public interface InstanceDao extends MongoRepository<InstanceEntity, String>	{
	
    public List<InstanceEntity> findAllByName(@Param("name") String name,Pageable pageable);

    public List<InstanceEntity> findAllByNameAndActive(@Param("name") String name,@Param("active") boolean active,Pageable pageable);

    public List<InstanceEntity> findAllByActive(@Param("active") boolean active,Pageable pageable);

	public List<InstanceEntity> findAllByType(@Param("type") String type, Pageable pageable);

	public List<InstanceEntity> findAllByTypeAndActive(@Param("type") String type,@Param("active") boolean active, Pageable pageable);

	public List<InstanceEntity> findAllByLocation_LatBetweenAndLocation_LngBetween(
			@Param("minLat") double minLat,
			@Param("maxLat") double maxLat,
			@Param("minLng") double minLng, 
			@Param("maxLng") double maxLng,
			Pageable pageable);
	
	public List<InstanceEntity> findAllByLocation_LatBetweenAndLocation_LngBetweenAndActive(
			@Param("minLat") double minLat,
			@Param("maxLat") double maxLat,
			@Param("minLng") double minLng, 
			@Param("maxLng") double maxLng,
			@Param("active") boolean active,
			Pageable pageable);

	public List<InstanceEntity> findAllByCreatedTimestampAfter(@Param("date") Date date, Pageable pageable);

	public List<InstanceEntity> findAllByCreatedTimestampAfterAndActive(@Param("date") Date date,@Param("active") boolean active, Pageable pageable);

	public List<InstanceEntity> findAllByParents_instanceId(@Param("instanceId") String instanceId, Pageable pageable);
	
	public List<InstanceEntity> findAllByParents_instanceIdAndActive(@Param("instanceId") String instanceId,@Param("active") boolean active, Pageable pageable);

	public List<InstanceEntity> findAllByChildren_instanceId(@Param("instanceId") String instanceId, Pageable pageable);

	public List<InstanceEntity> findAllByChildren_instanceIdAndActive(@Param("instanceId") String instanceId,@Param("active") boolean active, Pageable pageable);


}
