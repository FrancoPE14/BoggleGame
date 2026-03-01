package com.anteaters.boggle.repository;

import org.springframework.data.repository.CrudRepository;
import com.anteaters.boggle.entity.User;

/**
 * By extending the CrudRepository interface, sprint boot will automatically complete this class to include the all
 * methods, please reference this documentation:
 * https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html
 */
public interface UserRepository extends CrudRepository<User, String>{}