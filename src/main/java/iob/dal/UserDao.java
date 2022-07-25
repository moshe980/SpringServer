package iob.dal;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import iob.data.InstanceEntity;
import iob.data.UserEntity;

public interface UserDao extends MongoRepository<UserEntity, String>  {
	

}
