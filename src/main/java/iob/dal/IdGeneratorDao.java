package iob.dal;

import org.springframework.data.mongodb.repository.MongoRepository;

import iob.data.IdGeneratorEntity;

public interface IdGeneratorDao  extends MongoRepository<IdGeneratorEntity, String>{

}
