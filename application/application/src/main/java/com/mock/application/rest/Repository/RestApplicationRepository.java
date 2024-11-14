package com.mock.application.rest.Repository;

import com.mock.application.rest.Model.RestApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestApplicationRepository extends JpaRepository<RestApplication, String> {
}
