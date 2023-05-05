package edu.ncsu.csc.iTrust2.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.iTrust2.models.Bill;
import edu.ncsu.csc.iTrust2.models.User;

public interface BillRepository extends JpaRepository<Bill, Long> {

    /**
     * Find bills for a given patient
     *
     * @param patient
     *            patient to search by
     * @return Matching bills
     */
    public List<Bill> findByPatient ( User patient );
}
