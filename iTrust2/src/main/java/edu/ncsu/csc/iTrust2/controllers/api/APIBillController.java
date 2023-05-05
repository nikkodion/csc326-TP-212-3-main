package edu.ncsu.csc.iTrust2.controllers.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.iTrust2.forms.BillForm;
import edu.ncsu.csc.iTrust2.models.Bill;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.TransactionType;
import edu.ncsu.csc.iTrust2.services.BillService;
import edu.ncsu.csc.iTrust2.services.UserService;
import edu.ncsu.csc.iTrust2.utils.LoggerUtil;

/**
 * API controller for interacting with the Bill model. Provides standard CRUD
 * routes as appropriate for different user types
 *
 * @author Jacob Beasley
 *
 */
@RestController
@SuppressWarnings ( { "rawtypes", "unchecked" } )
public class APIBillController extends APIController {

    /** LoggerUtil */
    @Autowired
    private LoggerUtil        loggerUtil;

    /** Personnel Service */
    @Autowired
    private BillService       billService;

    /** User service */
    @Autowired
    private UserService<User> userService;

    /**
     * Retrieves a list of all bills in the database
     *
     * @return list of bills
     */
    @GetMapping ( BASE_PATH + "/bills" )
    @PreAuthorize ( "hasAnyRole('ROLE_BILL_SPEC')" )
    public List<Bill> getBillsList () {
        loggerUtil.log( TransactionType.BILL_SPEC_VIEW_BILLS_LIST, LoggerUtil.currentUser() );
        return billService.findAll();
    }

    /**
     * Retrieves a list of all bills in the database for the patient
     *
     * @return list of bills
     */
    @GetMapping ( BASE_PATH + "/bills/mybills" )
    @PreAuthorize ( "hasAnyRole('ROLE_PATIENT')" )
    public List<Bill> getMyBillsList () {
        final User self = userService.findByName( LoggerUtil.currentUser() );
        loggerUtil.log( TransactionType.PATIENT_VIEW_BILLS_LIST, self );
        return billService.findByPatient( self );
    }

    /**
     * Retrieves a specific bill as a billing specialist for a user
     *
     * @param id
     *            ID of the bill to get
     *
     * @return list of bills
     */
    @GetMapping ( BASE_PATH + "/bills/{id}" )
    @PreAuthorize ( "hasAnyRole('ROLE_BILL_SPEC')" )
    public ResponseEntity getBill ( @PathVariable final Long id ) {
        final User self = userService.findByName( LoggerUtil.currentUser() );
        loggerUtil.log( TransactionType.BILL_SPEC_VIEW_BILL, self );
        if ( !billService.existsById( id ) ) {
            return new ResponseEntity( HttpStatus.NOT_FOUND );
        }
        return new ResponseEntity( billService.findById( id ), HttpStatus.OK );
    }

    /**
     * Retrieves a specific bill as a patient
     *
     * @param id
     *            ID of the bill to get
     *
     * @return list of bills
     */
    @GetMapping ( BASE_PATH + "/bills/mybills/{id}" )
    @PreAuthorize ( "hasAnyRole('ROLE_PATIENT')" )
    public ResponseEntity getMyBillsList ( @PathVariable final Long id ) {
        final User self = userService.findByName( LoggerUtil.currentUser() );
        loggerUtil.log( TransactionType.PATIENT_VIEW_BILL, self );
        if ( !billService.existsById( id ) ) {
            return new ResponseEntity( HttpStatus.NOT_FOUND );
        }
        return new ResponseEntity( billService.findById( id ), HttpStatus.OK );
    }

    /**
     * Creates and saves a new Bill from the RequestBody provided.
     *
     * @param billForm
     *            The bill to be validated and saved
     * @return response
     */
    @PostMapping ( BASE_PATH + "/bills" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP')" )
    public ResponseEntity createBill ( @RequestBody final BillForm billForm ) {
        try {
            billForm.setAttendingHCP( LoggerUtil.currentUser() );
            final Bill bill = billService.build( billForm );

            if ( null != bill.getId() && billService.existsById( bill.getId() ) ) {
                return new ResponseEntity( errorResponse( "Bill with id " + bill.getId() + " already exists" ),
                        HttpStatus.CONFLICT );
            }
            billService.save( bill );
            loggerUtil.log( TransactionType.CREATE_BILL, LoggerUtil.currentUser(), bill.getPatient().getUsername() );
            return new ResponseEntity( bill, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
            return new ResponseEntity( errorResponse( "Could not validate or save the Bill due to " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );

        }
    }

    /**
     * Creates and saves a new Bill from the RequestBody provided.
     *
     * @param id
     *            ID of the bill to update
     * @param billForm
     *            The bill to be validated and saved
     * @return response
     */
    @PutMapping ( BASE_PATH + "/bills/{id}" )
    @PreAuthorize ( "hasAnyRole('ROLE_BILL_SPEC')" )
    public ResponseEntity updateBill ( @PathVariable final Long id, @RequestBody final BillForm billForm ) {
        try {
            billForm.setId( id );
            final Bill bill = billService.build( billForm );
            if ( null == bill.getId() || !billService.existsById( bill.getId() ) ) {
                return new ResponseEntity( errorResponse( "Bill with id " + bill.getId() + " does not exists" ),
                        HttpStatus.NOT_FOUND );
            }
            billService.save( bill );
            loggerUtil.log( TransactionType.EDIT_BILL, LoggerUtil.currentUser(), bill.getPatient().getUsername() );
            return new ResponseEntity( bill, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
            return new ResponseEntity( errorResponse( "Could not validate or save the Bill due to " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );

        }
    }

}
