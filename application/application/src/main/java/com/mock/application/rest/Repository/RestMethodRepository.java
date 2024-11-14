package com.mock.application.rest.Repository;

import com.mock.application.rest.Model.RestMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestMethodRepository extends JpaRepository<RestMethod, String> {
}