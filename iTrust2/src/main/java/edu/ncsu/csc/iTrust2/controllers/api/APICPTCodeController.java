package edu.ncsu.csc.iTrust2.controllers.api;

import java.util.ArrayList;
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

import edu.ncsu.csc.iTrust2.forms.CPTCodeForm;
import edu.ncsu.csc.iTrust2.models.CPTCode;
import edu.ncsu.csc.iTrust2.models.enums.TransactionType;
import edu.ncsu.csc.iTrust2.services.CPTCodeService;
import edu.ncsu.csc.iTrust2.utils.LoggerUtil;

/**
 * This is the controller that holds the REST endpoints that handle CRUD
 * operations for CPT Codes
 *
 * Spring will automatically convert all of the ResponseEntity and List results
 * to JSON
 *
 * @author Matthew Howard
 *
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APICPTCodeController extends APIController {

    /**
     * APIService object, to be autowired in by Spring to allow for manipulating
     * the CPTCode model
     */
    @Autowired
    private CPTCodeService      service;

    /** LoggerUtil */
    @Autowired
    private LoggerUtil          loggerUtil;

    /** Previous versions of CPT Codes */
    private final List<CPTCode> old = new ArrayList<CPTCode>();

    // /**
    // * Gets the times from the CPT Code description
    // *
    // * @param range
    // * The CPT Code description
    // * @return The list of times for the range
    // */
    // private List<Integer> extractTime ( final String range ) {
    // final Matcher matcher = Pattern.compile( "\\d+" ).matcher( range );
    // final List<Integer> list = new ArrayList<Integer>();
    // while ( matcher.find() ) {
    // list.add( Integer.parseInt( matcher.group() ) );
    // }
    // return list;
    // }

    /**
     * Gets a list of all the CPT Codes in the system.
     *
     * @return a list of CPT Codes
     */
    @GetMapping ( BASE_PATH + "/cpt_codes" )
    public List<CPTCode> getCPTCodes () {
        loggerUtil.log( TransactionType.CPT_CODE_VIEW, LoggerUtil.currentUser(), "Got list of CPT Codes" );
        // addDefaultCodes();
        return service.findAll();
    }

    /**
     * Gets a CPT Code from the system. Returns an error message if something
     * goes wrong.
     *
     * @param id
     *            The ID of the desired CPT Code
     * @return The desired CPT Code
     */
    @GetMapping ( BASE_PATH + "/cpt_codes/{id}" )
    public ResponseEntity getCPTCode ( @PathVariable ( "id" ) final Long id ) {
        final CPTCode cptCode = service.findById( id );
        return null == cptCode
                ? new ResponseEntity( errorResponse( "No CPT Code found with the ID " + id ), HttpStatus.NOT_FOUND )
                : new ResponseEntity( cptCode, HttpStatus.OK );
    }

    /**
     * Adds a new CPT Code to the system. Returns an error message if something
     * goes wrong.
     *
     * @param form
     *            The CPT Code form
     * @return The created CPT Code
     */
    @PreAuthorize ( "hasRole('ROLE_BILL_SPEC')" )
    @PostMapping ( BASE_PATH + "/cpt_codes" )
    public ResponseEntity addCPTCode ( @RequestBody final CPTCodeForm form ) {
        try {
            final CPTCode cptCode = new CPTCode( form );

            if ( service.existsById( cptCode.getId() ) || service.existsByCode( cptCode.getCode() ) ) {
                loggerUtil.log( TransactionType.CPT_CODE_CREATE, LoggerUtil.currentUser(),
                        "Conflict: CPT Code with ID " + cptCode.getId() + " already exists" );
                return new ResponseEntity( errorResponse( "CPT Code with id " + cptCode.getId() + " already exists" ),
                        HttpStatus.CONFLICT );
            }

            for ( int i = 0; i < old.size(); i++ ) {
                if ( old.get( i ).getCode().equals( cptCode.getCode() ) ) {
                    loggerUtil.log( TransactionType.CPT_CODE_CREATE, LoggerUtil.currentUser(),
                            "Conflict: CPT Code with ID " + cptCode.getId() + " already existed previously" );
                    return new ResponseEntity(
                            errorResponse( "CPT Code with id " + cptCode.getId() + " already existed previously" ),
                            HttpStatus.CONFLICT );
                }
            }

            // final List<Integer> times = extractTime( cptCode.getDescription()
            // );
            // if ( times.get( 0 ) >= times.get( 1 ) ) {
            // loggerUtil.log( TransactionType.CPT_CODE_CREATE,
            // LoggerUtil.currentUser(), "Invalid time range" );
            // return new ResponseEntity( errorResponse( "Invalid time range" ),
            // HttpStatus.BAD_REQUEST );
            // }
            //
            // final List<CPTCode> codes = getCPTCodes();
            // for ( int i = 0; i < codes.size(); i++ ) {
            // final List<Integer> range = extractTime( codes.get( i
            // ).getDescription() );
            // if ( times.get( 0 ) >= range.get( 0 ) && times.get( 0 ) <=
            // range.get( 1 ) ) {
            // loggerUtil.log( TransactionType.CPT_CODE_CREATE,
            // LoggerUtil.currentUser(),
            // "Cannot have overlapping times" );
            // return new ResponseEntity( errorResponse( "Cannot have
            // overlapping times" ),
            // HttpStatus.BAD_REQUEST );
            // }
            // else if ( times.get( 1 ) >= range.get( 0 ) && times.get( 1 ) <=
            // range.get( 1 ) ) {
            // loggerUtil.log( TransactionType.CPT_CODE_CREATE,
            // LoggerUtil.currentUser(),
            // "Cannot have overlapping times" );
            // return new ResponseEntity( errorResponse( "Cannot have
            // overlapping times" ),
            // HttpStatus.BAD_REQUEST );
            // }
            // }

            service.save( cptCode );
            loggerUtil.log( TransactionType.CPT_CODE_CREATE, LoggerUtil.currentUser(),
                    "CPT Code " + cptCode.getId() + " created" );
            return new ResponseEntity( cptCode, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            loggerUtil.log( TransactionType.CPT_CODE_CREATE, LoggerUtil.currentUser(), "Failed to create CPT Code" );
            return new ResponseEntity( errorResponse( "Could not add CPT Code: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Edits a drug in the system. The id stored in the form must match an
     * existing CPT Code.
     *
     * @param form
     *            The edited CPT Code form
     * @return The edited CPT Code or an error message
     */
    @PreAuthorize ( "hasRole('ROLE_BILL_SPEC')" )
    @PutMapping ( BASE_PATH + "/cpt_codes" )
    public ResponseEntity editCPTCode ( @RequestBody final CPTCodeForm form ) {
        try {
            final CPTCode savedcptCode = service.findById( form.getId() );
            if ( savedcptCode == null ) {
                return new ResponseEntity( errorResponse( "No CPT Code found with the ID " + form.getId() ),
                        HttpStatus.NOT_FOUND );
            }

            if ( !savedcptCode.getCode().equals( form.getCode() ) ) {
                return new ResponseEntity( errorResponse( "The CPT Code number must be the same." ),
                        HttpStatus.BAD_REQUEST );
            }

            final CPTCode cptCode = new CPTCode( form );

            // final List<Integer> times = extractTime( cptCode.getDescription()
            // );
            // if ( times.get( 0 ) >= times.get( 1 ) ) {
            // loggerUtil.log( TransactionType.CPT_CODE_CREATE,
            // LoggerUtil.currentUser(), "Invalid time range" );
            // return new ResponseEntity( errorResponse( "Invalid time range" ),
            // HttpStatus.BAD_REQUEST );
            // }

            // final List<CPTCode> codes = getCPTCodes();
            // old.add( savedcptCode );
            // codes.remove( savedcptCode );
            // for ( int i = 0; i < codes.size(); i++ ) {
            // final List<Integer> range = extractTime( codes.get( i
            // ).getDescription() );
            // if ( times.get( 0 ) >= range.get( 0 ) && times.get( 0 ) <=
            // range.get( 1 ) ) {
            // loggerUtil.log( TransactionType.CPT_CODE_CREATE,
            // LoggerUtil.currentUser(),
            // "Cannot have overlapping times" );
            // return new ResponseEntity( errorResponse( "Cannot have
            // overlapping times" ),
            // HttpStatus.BAD_REQUEST );
            // }
            // else if ( times.get( 1 ) >= range.get( 0 ) && times.get( 1 ) <=
            // range.get( 1 ) ) {
            // loggerUtil.log( TransactionType.CPT_CODE_CREATE,
            // LoggerUtil.currentUser(),
            // "Cannot have overlapping times" );
            // return new ResponseEntity( errorResponse( "Cannot have
            // overlapping times" ),
            // HttpStatus.BAD_REQUEST );
            // }
            // }

            service.save( cptCode );

            loggerUtil.log( TransactionType.CPT_CODE_EDIT, LoggerUtil.currentUser(),
                    "CPT Code with id " + cptCode.getId() + " edited" );
            return new ResponseEntity( cptCode, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            loggerUtil.log( TransactionType.CPT_CODE_EDIT, LoggerUtil.currentUser(), "Failed to edit CPT Code" );
            return new ResponseEntity( errorResponse( "Could not update CPT Code: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Deletes the CPT Code with the id matching the given id.
     *
     * @param id
     *            The id of the CPT Code to delete
     * @return The id of the deleted CPT Code
     */
    @PreAuthorize ( "hasRole('ROLE_BILL_SPEC')" )
    @DeleteMapping ( BASE_PATH + "/cpt_codes/{id}" )
    public ResponseEntity deleteCPTCode ( @PathVariable ( "id" ) final Long id ) {
        try {
            final CPTCode cptCode = service.findById( id );
            if ( cptCode == null ) {
                loggerUtil.log( TransactionType.CPT_CODE_DELETE, LoggerUtil.currentUser(),
                        "Could not find CPT Code with id " + id );
                return new ResponseEntity( errorResponse( "No CPT Code with found with id " + id ),
                        HttpStatus.NOT_FOUND );
            }
            old.add( cptCode );
            service.delete( cptCode );
            loggerUtil.log( TransactionType.CPT_CODE_DELETE, LoggerUtil.currentUser(),
                    "Deleted CPT Code with id " + cptCode.getId() );
            return new ResponseEntity( id, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            loggerUtil.log( TransactionType.CPT_CODE_DELETE, LoggerUtil.currentUser(), "Failed to delete CPT Code" );
            return new ResponseEntity( errorResponse( "Could not delete CPT Code: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    // public void addDefaultCodes () {
    // if ( service.findByCode( "99202" ) == null ) {
    // final CPTCode cpt = new CPTCode( "99202", "15-29 minutes", 75 );
    // service.save( cpt );
    // }
    // if ( service.findByCode( "99203" ) == null ) {
    // final CPTCode cpt = new CPTCode( "99203", "30-44 minutes", 150 );
    // service.save( cpt );
    // }
    // if ( service.findByCode( "99204" ) == null ) {
    // final CPTCode cpt = new CPTCode( "99204", "45-59 minutes", 200 );
    // service.save( cpt );
    // }
    // if ( service.findByCode( "99205" ) == null ) {
    // final CPTCode cpt = new CPTCode( "99205", "60-74 minutes", 250 );
    // service.save( cpt );
    // }
    // if ( service.findByCode( "99212" ) == null ) {
    // final CPTCode cpt = new CPTCode( "99212", "10-19 minutes", 50 );
    // service.save( cpt );
    // }
    // if ( service.findByCode( "99213" ) == null ) {
    // final CPTCode cpt = new CPTCode( "99213", "20-29 minutes", 100 );
    // service.save( cpt );
    // }
    // if ( service.findByCode( "99214" ) == null ) {
    // final CPTCode cpt = new CPTCode( "99214", "30-39 minutes", 125 );
    // service.save( cpt );
    // }
    // if ( service.findByCode( "99215" ) == null ) {
    // final CPTCode cpt = new CPTCode( "99215", "40-54 minutes", 175 );
    // service.save( cpt );
    // }
    // }
}
