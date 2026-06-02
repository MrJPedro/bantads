package com.bantads.saga.repository;

import com.bantads.saga.entity.SagaState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SagaStateRepository extends JpaRepository<SagaState, Long> {
    Optional<SagaState> findBySagaId(UUID sagaId);
}