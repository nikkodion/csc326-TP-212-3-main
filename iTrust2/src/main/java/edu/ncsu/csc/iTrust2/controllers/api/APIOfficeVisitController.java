package edu.ncsu.csc.iTrust2.controllers.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.iTrust2.forms.OfficeVisitForm;
import edu.ncsu.csc.iTrust2.models.CPTCode;
import edu.ncsu.csc.iTrust2.models.OfficeVisit;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.TransactionType;
import edu.ncsu.csc.iTrust2.services.CPTCodeService;
import edu.ncsu.csc.iTrust2.services.OfficeVisitService;
import edu.ncsu.csc.iTrust2.services.UserService;
import edu.ncsu.csc.iTrust2.utils.LoggerUtil;

/**
 * API controller for interacting with the OfficeVisit model. Provides standard
 * CRUD routes as appropriate for different user types
 *
 * @author Kai Presler-Marshall
 * @author Matthew Howard
 *
 */
@RestController
@SuppressWarnings ( { "unchecked", "rawtypes" } )
public class APIOfficeVisitController extends APIController {

    /** OfficeVisit service */
    @Autowired
    private OfficeVisitService officeVisitService;

    /** CPT Code service */
    @Autowired
    private CPTCodeService     cptCodeService;

    /** User service */
    @Autowired
    private UserService<User>  userService;

    /** LoggerUtil */
    @Autowired
    private LoggerUtil         loggerUtil;

    /**
     * Retrieves a list of all OfficeVisits in the database
     *
     * @return list of office visits
     */
    @GetMapping ( BASE_PATH + "/officevisits" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP')" )
    public List<OfficeVisit> getOfficeVisits () {
        loggerUtil.log( TransactionType.VIEW_ALL_OFFICE_VISITS, LoggerUtil.currentUser() );
        return officeVisitService.findAll();
    }

    /**
     * Retrieves all of the office visits for the current HCP.
     *
     * @return all of the office visits for the current HCP.
     */
    @GetMapping ( BASE_PATH + "/officevisits/HCP" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP')" )
    public List<OfficeVisit> getOfficeVisitsForHCP () {
        final User self = userService.findByName( LoggerUtil.currentUser() );
        loggerUtil.log( TransactionType.VIEW_ALL_OFFICE_VISITS, self );
        final List<OfficeVisit> visits = officeVisitService.findByHcp( self );
        return visits;
    }

    /**
     * Retrieves a list of all OfficeVisits in the database for the current
     * patient
     *
     * @return list of office visits
     */
    @GetMapping ( BASE_PATH + "/officevisits/myofficevisits" )
    @PreAuthorize ( "hasAnyRole('ROLE_PATIENT')" )
    public List<OfficeVisit> getMyOfficeVisits () {
        final User self = userService.findByName( LoggerUtil.currentUser() );
        loggerUtil.log( TransactionType.VIEW_ALL_OFFICE_VISITS, self );
        return officeVisitService.findByPatient( self );
    }

    /**
     * Retrieves a specific OfficeVisit in the database, with the given ID
     *
     * @param id
     *            ID of the office visit to retrieve
     * @return list of office visits
     */
    @GetMapping ( BASE_PATH + "/officevisits/{id}" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP')" )
    public ResponseEntity getOfficeVisit ( @PathVariable final Long id ) {
        final User self = userService.findByName( LoggerUtil.currentUser() );
        loggerUtil.log( TransactionType.GENERAL_CHECKUP_HCP_VIEW, self );
        if ( !officeVisitService.existsById( id ) ) {
            return new ResponseEntity( HttpStatus.NOT_FOUND );
        }

        return new ResponseEntity( officeVisitService.findById( id ), HttpStatus.OK );
    }

    /**
     * Creates and saves a new OfficeVisit from the RequestBody provided.
     *
     * @param visitForm
     *            The office visit to be validated and saved
     * @return response
     */
    @PostMapping ( BASE_PATH + "/officevisits" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP')" )
    public ResponseEntity createOfficeVisit ( @RequestBody final OfficeVisitForm visitForm ) {
        try {
            visitForm.setHcp( LoggerUtil.currentUser() );
            final OfficeVisit visit = officeVisitService.build( visitForm );

            if ( null != visit.getId() && officeVisitService.existsById( visit.getId() ) ) {
                return new ResponseEntity(
                        errorResponse( "Office visit with the id " + visit.getId() + " already exists" ),
                        HttpStatus.CONFLICT );
            }
            officeVisitService.save( visit );
            loggerUtil.log( TransactionType.GENERAL_CHECKUP_CREATE, LoggerUtil.currentUser(),
                    visit.getPatient().getUsername() );
            return new ResponseEntity( visit, HttpStatus.OK );

        }
        catch ( final Exception e ) {
            e.printStackTrace();
            return new ResponseEntity(
                    errorResponse( "Could not validate or save the OfficeVisit provided due to " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Creates and saves a new Office Visit from the RequestBody provided.
     *
     * @param id
     *            ID of the office visit to update
     * @param visitForm
     *            The office visit to be validated and saved
     * @return response
     */
    @PutMapping ( BASE_PATH + "/officevisits/{id}" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP')" )
    public ResponseEntity updateOfficeVisit ( @PathVariable final Long id,
            @RequestBody final OfficeVisitForm visitForm ) {
        try {
            final OfficeVisit visit = officeVisitService.build( visitForm );

            if ( null == visit.getId() || !officeVisitService.existsById( visit.getId() ) ) {
                return new ResponseEntity(
                        errorResponse( "Office visit with the id " + visit.getId() + " doesn't exist" ),
                        HttpStatus.NOT_FOUND );
            }
            officeVisitService.save( visit );
            loggerUtil.log( TransactionType.GENERAL_CHECKUP_EDIT, LoggerUtil.currentUser(),
                    visit.getPatient().getUsername() );
            return new ResponseEntity( visit, HttpStatus.OK );

        }
        catch ( final Exception e ) {
            e.printStackTrace();
            return new ResponseEntity(
                    errorResponse( "Could not validate or save the OfficeVisit provided due to " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Gets the entire list of CPT Codes in an Office Visit
     *
     * @param id
     *            The ID number of the Office Visit
     * @return The list of CPT Codes within the Office Visit
     */
    @GetMapping ( BASE_PATH + "/officevisits/cpt/{id}" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP')" )
    public List<CPTCode> getCPTCodes ( @PathVariable ( "id" ) final Long id ) {
        loggerUtil.log( TransactionType.OFFICE_VISIT_CPT_CODE_VIEW, LoggerUtil.currentUser() );
        return officeVisitService.findById( id ).getCPTCodes();
    }

    /**
     * Gets a specific CPT Code given its ID number.
     *
     * @param ovID
     *            The Office Visit ID number
     * @param cptID
     *            The ID number of the CPT Code being searched
     * @return The CPT Code with the given ID number
     */
    @GetMapping ( BASE_PATH + "/officevists/cptid/{ovID}" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP')" )
    public ResponseEntity getCPTCode ( @PathVariable ( "ovID" ) final Long ovID, @RequestBody final Long cptID ) {
        CPTCode cptCode = null;
        final List<CPTCode> cptList = officeVisitService.findById( ovID ).getCPTCodes();
        for ( int i = 0; i < cptList.size(); i++ ) {
            if ( cptList.get( i ).getId().equals( cptID ) ) {
                cptCode = cptList.get( i );
            }
        }

        return null == cptCode
                ? new ResponseEntity( errorResponse( "No CPT Code found with the ID " + cptID ), HttpStatus.NOT_FOUND )
                : new ResponseEntity( cptCode, HttpStatus.OK );
    }

    /**
     * Gets a specific CPT Code given its code value.
     *
     * @param ovID
     *            The Office Visit ID number
     * @param cptID
     *            The code value of the CPT Code being searched
     * @return The CPT Code with the given code value
     */
    @GetMapping ( BASE_PATH + "/officevists/cptcode/{ovID}" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP')" )
    public ResponseEntity getCPTCode ( @PathVariable ( "ovID" ) final Long ovID, @RequestBody final String code ) {
        CPTCode cptCode = null;
        final List<CPTCode> cptList = officeVisitService.findById( ovID ).getCPTCodes();
        for ( int i = 0; i < cptList.size(); i++ ) {
            if ( cptList.get( i ).getCode().equals( code ) ) {
                cptCode = cptList.get( i );
            }
        }
        return null == cptCode
                ? new ResponseEntity( errorResponse( "No CPT Code found with the code " + code ), HttpStatus.NOT_FOUND )
                : new ResponseEntity( cptCode, HttpStatus.OK );
    }

    /**
     * Adds a CPT Code to the Office Visit
     *
     * @param id
     *            The ID number of the Office Visit
     * @param cptCode
     *            The CPT Code being added
     * @return The newly updated Office Visit
     */
    @PostMapping ( BASE_PATH + "/officevisits/cpt/{id}" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP')" )
    public ResponseEntity addCPTCode ( @PathVariable ( "id" ) final Long id, @RequestBody final CPTCode cptCode ) {
        try {
            final OfficeVisit ov = officeVisitService.findById( id );
            List<CPTCode> cptList = cptCodeService.findAll();
            boolean found = false;
            for ( int i = 0; i < cptList.size(); i++ ) {
                if ( cptList.get( i ).equals( cptCode ) ) {
                    found = true;
                    break;
                }
            }
            if ( !found ) {
                loggerUtil.log( TransactionType.OFFICE_VISIT_CPT_CODE_ADD, LoggerUtil.currentUser(),
                        "Could not find CPT Code in database." );
                return new ResponseEntity(
                        errorResponse( "CPT Code with code " + cptCode.getCode() + " is not in database." ),
                        HttpStatus.NOT_FOUND );
            }

            cptList = ov.getCPTCodes();
            for ( int i = 0; i < cptList.size(); i++ ) {
                if ( cptList.get( i ).getId().equals( cptCode.getId() )
                        || cptList.get( i ).getCode().equals( cptCode.getCode() ) ) {
                    loggerUtil.log( TransactionType.OFFICE_VISIT_CPT_CODE_ADD, LoggerUtil.currentUser(),
                            "CPT Code already in OfficeVisit" );
                    return new ResponseEntity(
                            errorResponse( "CPT Code with code " + cptCode.getCode() + " is already in Office Visit" ),
                            HttpStatus.CONFLICT );
                }
            }

            ov.getCPTCodes().add( cptCode );
            officeVisitService.save( ov );
            loggerUtil.log( TransactionType.OFFICE_VISIT_CPT_CODE_ADD, LoggerUtil.currentUser(),
                    "CPT Code " + cptCode.getCode() + " was added to the Office Visit." );
            return new ResponseEntity( ov, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            loggerUtil.log( TransactionType.OFFICE_VISIT_CPT_CODE_ADD, LoggerUtil.currentUser(),
                    "Failed to add CPT Code to Office Visit" );
            return new ResponseEntity( errorResponse( "Could not add CPT Code: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Removes a CPT Code from an Office Visit given its ID number
     *
     * @param ovID
     *            The ID of the Office Visit
     * @param cptID
     *            The ID of the CPT Code being searched for
     * @return The newly updated Office Visit
     */
    @DeleteMapping ( BASE_PATH + "/officevisits/cptid/{ovID}" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP')" )
    public ResponseEntity deleteCPTCode ( @PathVariable ( "ovID" ) final Long ovID, @RequestBody final Long cptID ) {
        try {
            final OfficeVisit ov = officeVisitService.findById( ovID );

            if ( !cptCodeService.existsById( cptID ) ) {
                loggerUtil.log( TransactionType.OFFICE_VISIT_CPT_CODE_DELETE, LoggerUtil.currentUser(),
                        "CPT Code with id " + cptID + " not found in database" );
                return new ResponseEntity( errorResponse( "Could not find CPT Code in database" ),
                        HttpStatus.NOT_FOUND );
            }
            final CPTCode cptCode = cptCodeService.findById( cptID );

            boolean found = false;
            for ( int i = 0; i < ov.getCPTCodes().size(); i++ ) {
                if ( ov.getCPTCodes().get( i ).equals( cptCode ) ) {
                    found = true;
                    break;
                }
            }

            if ( !found ) {
                loggerUtil.log( TransactionType.OFFICE_VISIT_CPT_CODE_DELETE, LoggerUtil.currentUser(),
                        "CPT Code with id " + cptID + " not found in office Visit" );
                return new ResponseEntity( errorResponse( "Could not find CPT Code in Office Visit" ),
                        HttpStatus.NOT_FOUND );
            }

            ov.getCPTCodes().remove( cptCode );
            officeVisitService.save( ov );
            loggerUtil.log( TransactionType.OFFICE_VISIT_CPT_CODE_DELETE, LoggerUtil.currentUser(),
                    "CPT Code " + cptCode.getCode() + " was removed from the Office Visit." );
            return new ResponseEntity( ov, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            loggerUtil.log( TransactionType.OFFICE_VISIT_CPT_CODE_DELETE, LoggerUtil.currentUser(),
                    "Failed to remove CPT Code remove from Office Visit" );
            return new ResponseEntity( errorResponse( "Could not remove CPT Code: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Removes a CPT Code from an Office Visit given its code value
     *
     * @param id
     *            The ID of the Office Visit
     * @param code
     *            The code value of the CPT Code being searched for
     * @return The newly updated Office Visit
     */
    @DeleteMapping ( BASE_PATH + "/officevisits/cptcode/{id}" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP')" )
    public ResponseEntity deleteCPTCode ( @PathVariable ( "id" ) final Long id, @RequestBody final String code ) {
        try {
            final OfficeVisit ov = officeVisitService.findById( id );

            if ( !cptCodeService.existsByCode( code ) ) {
                loggerUtil.log( TransactionType.OFFICE_VISIT_CPT_CODE_DELETE, LoggerUtil.currentUser(),
                        "CPT Code with code " + code + " not found in database" );
                return new ResponseEntity( errorResponse( "Could not find CPT Code in database" ),
                        HttpStatus.NOT_FOUND );
            }
            final CPTCode cptCode = cptCodeService.findByCode( code );

            boolean found = false;
            for ( int i = 0; i < ov.getCPTCodes().size(); i++ ) {
                if ( ov.getCPTCodes().get( i ).equals( cptCode ) ) {
                    found = true;
                    break;
                }
            }

            if ( !found ) {
                loggerUtil.log( TransactionType.OFFICE_VISIT_CPT_CODE_DELETE, LoggerUtil.currentUser(),
                        "CPT Code with code " + code + " not found in office Visit" );
                return new ResponseEntity( errorResponse( "Could not find CPT Code in Office Visit" ),
                        HttpStatus.NOT_FOUND );
            }

            ov.getCPTCodes().remove( cptCode );
            officeVisitService.save( ov );
            loggerUtil.log( TransactionType.OFFICE_VISIT_CPT_CODE_DELETE, LoggerUtil.currentUser(),
                    "CPT Code " + cptCode.getCode() + " was removed from the Office Visit." );
            return new ResponseEntity( ov, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            loggerUtil.log( TransactionType.OFFICE_VISIT_CPT_CODE_DELETE, LoggerUtil.currentUser(),
                    "Failed to remove CPT Code remove from Office Visit" );
            return new ResponseEntity( errorResponse( "Could not remove CPT Code: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }
}
