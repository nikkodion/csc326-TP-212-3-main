package edu.ncsu.csc.iTrust2.controllers.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.iTrust2.models.Bill;
import edu.ncsu.csc.iTrust2.models.CPTCode;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.enums.BillStatus;
import edu.ncsu.csc.iTrust2.services.BillService;
import edu.ncsu.csc.iTrust2.services.CPTCodeService;
import edu.ncsu.csc.iTrust2.services.PatientService;
import edu.ncsu.csc.iTrust2.utils.LoggerUtil;

/**
 * Class that provides REST API endpoints for the Bill Certification model.
 *
 * @author dybanez
 */
@RestController
@SuppressWarnings ( { "rawtypes" } )
public class APIBillCertificateController extends APIController {

    /** Patient Service */
    @Autowired
    private PatientService userService;

    /** Bill Service */
    @Autowired
    private BillService    billService;

    /** CPT Code Service */
    @Autowired
    private CPTCodeService cptCodeService;

    /**
     * Gets the vaccination certificate for a patient
     *
     * @return the certificate
     */
    @GetMapping ( BASE_PATH + "/billcertificate" )
    @PreAuthorize ( "hasAnyRole('ROLE_PATIENT')" )
    public Certificate getBillCertificate () {
        final Patient self = (Patient) userService.findByName( LoggerUtil.currentUser() );
        final List<Bill> bills = billService.findByPatient( self );

        final List<CertificateElement> celist = new ArrayList<CertificateElement>();
        // CertificateElement ce = new CertificateElement( null, null, null,
        // null, null );
        for ( int i = 0; i < bills.size(); i++ ) {
            final Patient patient = (Patient) bills.get( i ).getPatient();
            final Long id = bills.get( i ).getId();
            final String dateTime = bills.get( i ).getDate().toString();
            final BillStatus status = bills.get( i ).getStatus();
            final int totalCost = bills.get( i ).getCost();
            final List<CPTCode> cptCodes = bills.get( i ).getCptCodes();
            final CertificateElement ce = new CertificateElement( patient, id, dateTime, status, totalCost, cptCodes );

            celist.add( ce );
        }
        final Certificate certificate = new Certificate( celist, self );
        return certificate;
    }

    /**
     * Helper class to hold parts of a certificate
     *
     * @author dybanez
     *
     */
    public class CertificateElement {

        /**
         * patient
         */
        private Patient             patient;

        /**
         * id
         */
        private Long                id;

        /**
         * date bill issued
         */
        private String              dateTime;

        /**
         * status of bill
         */
        private BillStatus          status;

        /**
         * total cost of bill
         */
        private int                 totalCost;

        /**
         * list of cpt codes for bill
         */
        private final List<CPTCode> cptCodes;

        /**
         * Constructor for CertificateElement
         *
         * @param patient
         *            patient to set
         * @param dateTime
         *            dateTime to set
         * @param status
         *            status to set
         * @param totalCost
         *            total cost to set
         */
        public CertificateElement ( final Patient patient, final Long id, final String dateTime,
                final BillStatus status, final int totalCost, final List<CPTCode> cptCodes ) {
            super();
            this.patient = patient;
            this.dateTime = dateTime;
            this.id = id;
            this.status = status;
            this.totalCost = totalCost;
            this.cptCodes = cptCodes;
        }

        /**
         * Gets the patient
         *
         * @return the patient
         */
        public Patient getPatient () {
            return patient;
        }

        /**
         * Sets the patient
         *
         * @param patient
         *            the patient to set
         */
        public void setPatient ( final Patient patient ) {
            this.patient = patient;
        }

        /**
         * Gets the date/time of the visit
         *
         * @return the dateTime
         */
        public String getDateTime () {
            return dateTime;
        }

        /**
         * Sets the date/time of the visit
         *
         * @param dateTime
         *            the dateTime to set
         */
        public void setDateTime ( final String dateTime ) {
            this.dateTime = dateTime;
        }

        /**
         * @return the id
         */
        public Long getId () {
            return id;
        }

        /**
         * @param id
         *            the id to set
         */
        public void setId ( final Long id ) {
            this.id = id;
        }

        /**
         * @return the status
         */
        public BillStatus getStatus () {
            return status;
        }

        /**
         * @param status
         *            the status to set
         */
        public void setStatus ( final BillStatus status ) {
            this.status = status;
        }

        /**
         * @return the totalCost
         */
        public float getTotalCost () {
            return totalCost;
        }

        /**
         * @param totalCost
         *            the totalCost to set
         */
        public void setTotalCost ( final int totalCost ) {
            this.totalCost = totalCost;
        }

        /**
         * @return the cptCodes
         */
        public List<CPTCode> getCptCodes () {
            return cptCodes;
        }
    }

    /**
     * Helper method for when there are 2 bills
     *
     * @author dybanez
     *
     */
    public class Certificate {
        /**
         * The list of certificate elements
         */
        private List<CertificateElement> celist;
        /**
         * The patient who's certificate we're building
         */
        private Patient                  patient;

        /**
         * Constructor for the certificate
         *
         * @param celist
         *            list of certificate elements
         * @param patient
         *            the patient who's certificate we're building
         */
        public Certificate ( final List<CertificateElement> celist, final Patient patient ) {
            super();
            this.celist = celist;
            this.patient = patient;
        }

        /**
         * Gets the list holding the certificate elements
         *
         * @return the celist
         */
        public List<CertificateElement> getCelist () {
            return celist;
        }

        /**
         * Sets the certificate element list
         *
         * @param celist
         *            the celist to set
         */
        public void setCelist ( final List<CertificateElement> celist ) {
            this.celist = celist;
        }

        /**
         * Gets the patient
         *
         * @return the patient
         */
        public Patient getPatient () {
            return patient;
        }

        /**
         * Sets the patient
         *
         * @param patient
         *            the patient to set
         */
        public void setPatient ( final Patient patient ) {
            this.patient = patient;
        }

    }
}
