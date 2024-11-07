package com.mock.application.Repository;

import com.mock.application.Model.RestResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestResourceRepository extends JpaRepository<RestResource, String> {
}