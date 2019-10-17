package com.srigopal.home.projects.repository;


import com.srigopal.home.projects.imageprocessor.model.DBFile;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface DBFileRepository extends JpaRepository<DBFile, String> {

}
