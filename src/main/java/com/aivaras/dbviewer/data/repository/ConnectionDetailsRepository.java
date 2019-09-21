package com.aivaras.dbviewer.data.repository;

import com.aivaras.dbviewer.data.entity.ConnectionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConnectionDetailsRepository extends JpaRepository<ConnectionDetails, Long> {

}
