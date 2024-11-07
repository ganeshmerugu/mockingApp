package com.mock.application.Repository;

import com.mock.application.Model.RestMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestMethodRepository extends JpaRepository<RestMethod, String> {
}