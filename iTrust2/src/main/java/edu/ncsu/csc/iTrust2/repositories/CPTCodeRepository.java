package edu.ncsu.csc.iTrust2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.iTrust2.models.CPTCode;

/**
 * CPTCodeRepository is used to provide CRUD operations for the CPTCode model.
 * Spring will generate appropriate code with JPA.
 *
 * @author Matthew Howard
 *
 */
public interface CPTCodeRepository extends JpaRepository<CPTCode, Long> {

    /**
     * Finds a CPT Code object with the provided code number. Spring will
     * generate code to make this happen.
     *
     * @param code
     *            Code number of the CPT Code
     * @return Found CPT Code, null if none.
     */
    CPTCode findByCode ( String code );

    boolean existsByCode ( String code );
}
