package edu.ncsu.csc.iTrust2.forms;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import edu.ncsu.csc.iTrust2.models.Bill;
import edu.ncsu.csc.iTrust2.models.CPTCode;
import edu.ncsu.csc.iTrust2.models.Payment;
import edu.ncsu.csc.iTrust2.models.enums.BillStatus;

public class BillForm {

    /**
     * Serial Version of the Form. For the Serializable
     */
    private static final long serialVersionUID = 1L;

    /**
     * Empty constructor so that we can create an Bill form for the user to fill
     * out
     */
    public BillForm () {
    }

    /** ID of this bill */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long          id;

    /** name of the bill */
    private String        patient;

    /** total cost of the bill */
    private int           cost;

    /** total amount currently owed */
    private int           amountOwed;

    /** date of bill */
    private String        date;

    @Enumerated ( EnumType.STRING )
    /** status of paid, unpaid, or delinquent */
    private BillStatus    status;

    /** hcp attending to the bill */
    private String        attendingHCP;

    /** cpt codes provided for the bill */
    private List<CPTCode> cptCodes;

    /** payments currently associated with the bill */
    private List<Payment> payments;

    /**
     * Creates a BillForm from the Bill provided
     *
     * @param bi
     *            Bill to turn into an BillForm
     */
    public BillForm ( final Bill bi ) {
        setId( bi.getId() );
        setPatient( bi.getPatient().getUsername() );
        setCost( bi.getCost() );
        setDate( bi.getDate().toString() );
        setStatus( bi.getStatus() );
        setAmountOwed( bi.getAmountOwed() );
        setAttendingHCP( bi.getAttendingHCP().getUsername() );
        setCptCodes( bi.getCptCodes() );
    }

    /**
     * Returns the id associated with this bill.
     *
     * @return the id
     */
    public Serializable getId () {
        return this.id;
    }

    /**
     * Sets the ID of the bill.
     *
     * @param id
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns the patient associated with this bill.
     *
     * @return the patient
     */
    public String getPatient () {
        return patient;
    }

    /**
     * Sets the bill patient.
     *
     * @param patient
     */
    public void setPatient ( final String patient ) {
        this.patient = patient;
    }

    /**
     * Returns total cost of bill (regardless of amount already paid)
     *
     * @return cost
     */
    public int getCost () {
        return cost;
    }

    /**
     * Sets total bill cost.
     *
     * @param cost
     */
    public void setCost ( final int cost ) {
        this.cost = cost;
    }

    /**
     * Returns amount left to pay.
     *
     * @return amount
     */
    public int getAmountOwed () {
        return amountOwed;
    }

    /**
     * Sets the amount left to pay.
     *
     * @param amountOwed
     */
    public void setAmountOwed ( final int amountOwed ) {
        this.amountOwed = amountOwed;
        if ( getStatus() == null || !getStatus().equals( BillStatus.DELINQUENT ) ) {
            setStatus( BillStatus.PARTIALLY_PAID );
            if ( this.amountOwed == getCost() ) {
                setStatus( BillStatus.UNPAID );
            }
        }
        if ( this.amountOwed == 0 ) {
            setStatus( BillStatus.PAID );
        }
    }

    /**
     * Returns the date the bill was issued.
     *
     * @return date bill was issued
     */
    public String getDate () {
        return date;
    }

    /**
     * Sets date bill was issued.
     *
     * @param date
     */
    public void setDate ( final String date ) {
        this.date = date;
    }

    /**
     * Returns bill status as an enum (paid, unpaid, deliquent).
     *
     * @return bill status
     */
    public BillStatus getStatus () {
        return status;
    }

    /**
     * Sets bill status enum.
     *
     * @param status
     */
    public void setStatus ( final BillStatus status ) {
        this.status = status;
    }

    /**
     * Returns attending HCP personnel.
     *
     * @return personnel
     */
    public String getAttendingHCP () {
        return attendingHCP;
    }

    /**
     * Sets the attending HCP for the bill.
     *
     * @param attendingHCP
     */
    public void setAttendingHCP ( final String attendingHCP ) {
        this.attendingHCP = attendingHCP;
    }

    /**
     * Returns the array of CPT codes.
     *
     * @return cpt code array
     */
    public List<CPTCode> getCptCodes () {
        return cptCodes;
    }

    /**
     * Sets the CPT code array.
     *
     * @param cptCodes
     */
    public void setCptCodes ( final List<CPTCode> cptCodes ) {
        this.cptCodes = cptCodes;
    }

    /**
     * @return the payments
     */
    public List<Payment> getPayments () {
        return payments;
    }

    /**
     * @param payments
     *            the payments to set
     */
    public void setPayments ( final List<Payment> payments ) {
        this.payments = payments;
    }
}
