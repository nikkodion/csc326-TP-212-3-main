package edu.ncsu.csc.iTrust2.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.forms.CPTCodeForm;
import edu.ncsu.csc.iTrust2.models.CPTCode;
import edu.ncsu.csc.iTrust2.repositories.CPTCodeRepository;

/**
 * Service class for interacting with CPTCode model, performing CRUD tasks with
 * database.
 *
 * @author Matthew Howard
 *
 */
@Component
@Transactional
public class CPTCodeService extends Service<CPTCode, Long> {

    /** Repository for CRUD tasks */
    @Autowired
    private CPTCodeRepository cptCodeRepository;

    @Override
    protected JpaRepository<CPTCode, Long> getRepository () {
        return cptCodeRepository;
    }

    /**
     * Builds a CPT Code form the deserialized CPTCodeForm
     *
     * @param form
     *            Form to build a CPT Code from
     * @return Build CPT Code
     */
    public CPTCode build ( final CPTCodeForm form ) {
        final CPTCode cptCode = new CPTCode();

        cptCode.setCode( form.getCode() );
        cptCode.setCost( form.getCost() );
        cptCode.setDescription( form.getCode() );
        if ( form.getId() != null ) {
            cptCode.setId( form.getId() );
        }
        return cptCode;
    }

    /**
     * Find a CPT Code with the provided code number
     *
     * @param code
     *            CPT Code number to find
     * @return found CPT Code, null if none
     */
    public CPTCode findByCode ( final String code ) {
        return cptCodeRepository.findByCode( code );
    }

    public boolean existsByCode ( final String code ) {
        return cptCodeRepository.existsByCode( code );
    }
}
