package edu.ncsu.csc.iTrust2.models;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.gson.annotations.JsonAdapter;

import edu.ncsu.csc.iTrust2.adapters.ZonedDateTimeAdapter;
import edu.ncsu.csc.iTrust2.adapters.ZonedDateTimeAttributeConverter;
import edu.ncsu.csc.iTrust2.forms.BillForm;
import edu.ncsu.csc.iTrust2.models.enums.BillStatus;

@Entity
public class Bill extends DomainObject {

    /** ID of this bill */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long          id;

    /** name of the bill */
    @NotNull
    @ManyToOne
    @JoinColumn ( name = "patient_id", columnDefinition = "varchar(100)" )
    private User          patient;

    /** total cost of the bill */
    private int           cost;

    /** total amount currently owed */
    private int           amountOwed;

    /** date the bill was issued */
    @NotNull
    @Basic
    // Allows the field to show up nicely in the database
    @Convert ( converter = ZonedDateTimeAttributeConverter.class )
    @JsonAdapter ( ZonedDateTimeAdapter.class )
    private ZonedDateTime date;

    @Enumerated ( EnumType.STRING )
    /** status of paid, unpaid, or delinquent */
    private BillStatus    status;

    /** HCP attending to the bill */
    @NotNull
    @ManyToOne
    @JoinColumn ( name = "hcp_id", columnDefinition = "varchar(100)" )
    private User          attendingHCP;

    /** cpt codes provided for the bill */
    @ManyToMany ( cascade = CascadeType.MERGE )
    @LazyCollection ( LazyCollectionOption.FALSE )
    @JsonManagedReference
    private List<CPTCode> cptCodes;

    /** payments currently associated with the bill */
    @ManyToMany ( cascade = CascadeType.MERGE )
    @LazyCollection ( LazyCollectionOption.FALSE )
    @JsonManagedReference
    private List<Payment> payments;

    /**
     * Empty constructor for Hibernate
     */
    public Bill () {

    }

    /**
     * Construct from a form
     *
     * @param form
     *            The form that validates and sanitizes input
     */
    public Bill ( final BillForm form ) {
        setCost( form.getCost() );
        setAmountOwed( form.getAmountOwed() );
        setStatus( form.getStatus() );
        setDate( ZonedDateTime.parse( form.getDate() ) );
        setCptCodes( form.getCptCodes() );
        setPayments( form.getPayments() );

    }

    /**
     * Returns the id associated with this bill.
     *
     * @return the id
     */
    @Override
    public Long getId () {
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
    public User getPatient () {
        return patient;
    }

    /**
     * Sets the bill patient.
     *
     * @param patient
     */
    public void setPatient ( final User patient ) {
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
        if ( cost < 0 ) {
            throw new IllegalArgumentException( "Cost cannot be negative" );
        }
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
        setStatus( BillStatus.PARTIALLY_PAID );
        if ( this.amountOwed == getCost() ) {
            setStatus( BillStatus.UNPAID );
        }
        if ( getDate() != null && ZonedDateTime.now().minusDays( 60 ).isAfter( getDate() ) ) {
            setStatus( BillStatus.DELINQUENT );
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
    public ZonedDateTime getDate () {
        return date;
    }

    /**
     * Sets date bill was issued.
     *
     * @param date
     */
    public void setDate ( final ZonedDateTime date ) {
        if ( date != null && ZonedDateTime.now().minusDays( 60 ).isAfter( date ) ) {
            setStatus( BillStatus.DELINQUENT );
        }
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
        if ( amountOwed < 0 ) {
            throw new IllegalArgumentException( "Cannot owe a negative amount" );
        }
        if ( amountOwed != 0 && status.equals( BillStatus.PAID ) ) {
            throw new IllegalArgumentException( "Bill cannot be set to paid if bill is not fully paid" );
        }
        if ( amountOwed == 0 && ( status.equals( BillStatus.UNPAID ) || status.equals( BillStatus.DELINQUENT ) ) ) {
            throw new IllegalArgumentException( "Bill cannot be set to unpaid or delinquent if bill is fully paid" );
        }
        this.status = status;
    }

    /**
     * Returns attending HCP personnel.
     *
     * @return personnel
     */
    public User getAttendingHCP () {
        return attendingHCP;
    }

    /**
     * Sets the attending HCP for the bill.
     *
     * @param attendingHCP
     */
    public void setAttendingHCP ( final User attendingHCP ) {
        this.attendingHCP = attendingHCP;
    }

    /**
     * Returns the list of CPT codes.
     *
     * @return cpt code list
     */
    public List<CPTCode> getCptCodes () {
        return cptCodes;
    }

    /**
     * Sets the CPT code list.
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

    /**
     * Adds a payment to the payment list and then updates amount owed.
     *
     * @param paymentToAdd
     *            payment to add
     * @return true if successful, false if not
     */
    public boolean addPayment ( final Payment paymentToAdd ) {

        if ( paymentToAdd.getAmount() > getAmountOwed() ) {
            return false;
        }
        setAmountOwed( getAmountOwed() - paymentToAdd.getAmount() );
        try {
            if ( this.payments == null ) {
                this.payments = new ArrayList<Payment>();
            }
            payments.add( paymentToAdd );
        }
        catch ( final IllegalArgumentException e ) {
            return false;
        }
        return true;
    }

    /**
     * Removes a payment, mainly used for if a user accidentally pays an amount
     * they didn't intend to.
     *
     * @param paymentToRemove
     *            the payment to remove
     * @return true if successful, false if not
     */
    public boolean removePayment ( final Payment paymentToRemove ) {
        setAmountOwed( getAmountOwed() - paymentToRemove.getAmount() );
        return payments.remove( paymentToRemove );
    }
}
