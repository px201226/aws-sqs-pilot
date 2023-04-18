package com.example.awssqspilot.domain.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationEventRepository extends JpaRepository<ApplicationEvent, String> {

}
