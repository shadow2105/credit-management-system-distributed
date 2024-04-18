package com.example.cmscreditstatementservice.domain.repository;

import com.example.cmscreditstatementservice.domain.CreditStatementSchedule;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;
import java.util.UUID;

public interface CreditStatementScheduleRepository extends CrudRepository<CreditStatementSchedule, UUID> {

    Set<CreditStatementSchedule> findAllByStatementDayOfMonth(int day);
}
