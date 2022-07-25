package iob.dal;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import iob.data.ActivityEntity;

public interface ActivityDao extends MongoRepository<ActivityEntity, String> {

}
