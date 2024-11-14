package com.mock.application.rest.Repository;

import com.mock.application.rest.Model.RestResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestResourceRepository extends JpaRepository<RestResource, String> {
}