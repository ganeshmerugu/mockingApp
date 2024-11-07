package com.mock.application.Repository;

import com.mock.application.Model.RestApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestApplicationRepository extends JpaRepository<RestApplication, String> {
}
