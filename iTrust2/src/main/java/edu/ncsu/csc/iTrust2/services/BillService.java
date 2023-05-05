package edu.ncsu.csc.iTrust2.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.forms.BillForm;
import edu.ncsu.csc.iTrust2.models.Bill;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.repositories.BillRepository;

@Component
@Transactional
public class BillService extends Service<Bill, Long> {

    /**
     * Repository for CRUD operations
     */
    @Autowired
    private BillRepository    repository;

    /**
     * User service
     */
    @Autowired
    private UserService<User> userService;

    @Override
    protected JpaRepository<Bill, Long> getRepository () {
        return repository;
    }

    /**
     * Builds and validates a Bill from the provided BillForm
     *
     * @param form
     *            Form for building persistence object
     * @return Generated Bil
     */
    public Bill build ( final BillForm form ) {
        final Bill bill = new Bill( form );
        bill.setId( (Long) form.getId() );
        bill.setPatient( userService.findByName( form.getPatient() ) );
        // bill.setAmountOwed( form.getAmountOwed() );
        // bill.setCost( form.getCost() );

        bill.setAttendingHCP( userService.findByName( form.getAttendingHCP() ) );
        // bill.setCptCodes( form.getCptCodes() );
        // final ZonedDateTime visitDate = ZonedDateTime.parse( form.getDate()
        // );
        // bill.setDate( visitDate );
        // bill.setStatus( form.getStatus() );
        return bill;
    }

    /**
     * Finds all Bills for a specified patient
     *
     * @param patient
     *            Patient to search for
     * @return Bills matched
     */
    public List<Bill> findByPatient ( final User patient ) {
        return repository.findByPatient( patient );
    }
}
