package com.example.awssqspilot.domain.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventMessageRepository extends JpaRepository<EventMessage, String> {

}
