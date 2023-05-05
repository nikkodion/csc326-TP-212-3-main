package edu.ncsu.csc.iTrust2.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.iTrust2.models.OfficeVisit;
import edu.ncsu.csc.iTrust2.models.User;

/**
 * Repository for interacting with OfficeVisit model. Method implementations
 * generated by Spring
 *
 * @author Kai Presler-Marshall
 *
 */
public interface OfficeVisitRepository extends JpaRepository<OfficeVisit, Long> {

    /**
     * Find office visits for a given patient
     *
     * @param hcp
     *            HCP to search by
     * @return Matching visits
     */
    public List<OfficeVisit> findByHcp ( User hcp );

    /**
     * Find office visits for a given HCP
     *
     * @param patient
     *            Patient to search by
     * @return Matching visits
     */
    public List<OfficeVisit> findByPatient ( User patient );

    /**
     * Find office visits for a given HCP and patient
     *
     * @param hcp
     *            HCP to search by
     * @param patient
     *            Patient to search by
     * @return Matching visits
     */
    public List<OfficeVisit> findByHcpAndPatient ( User hcp, User patient );

}
