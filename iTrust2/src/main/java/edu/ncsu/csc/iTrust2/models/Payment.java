package edu.ncsu.csc.iTrust2.models;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import edu.ncsu.csc.iTrust2.forms.PaymentForm;
import edu.ncsu.csc.iTrust2.models.enums.PaymentMethod;

/**
 * Class of a payment which stores a payment method and an amount paid for a
 * bill.
 *
 * @author Dion Ybanez
 *
 */
@Entity
public class Payment extends DomainObject {

    /** ID of this payment */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long          id;

    /** amount paid with this payment */
    private int           amount;

    /** method used to pay */
    @Enumerated ( EnumType.STRING )
    private PaymentMethod method;

    /** empty constructor for Hibernate */
    public Payment () {
        //
    }

    /**
     * Makes a Payment object from a Payment form
     *
     * @param form
     *            PaymentForm to make a Payment from
     */
    public Payment ( final PaymentForm form ) {
        setId( form.getId() );
        setAmount( form.getAmount() );
        setMethod( form.getMethod() );

        if ( amount <= 0 ) {
            throw new IllegalArgumentException( "Amount paid can't be 0 or negative" );
        }
    }

    /**
     * Returns the id associated with this payment.
     *
     * @return the id
     */
    @Override
    public Long getId () {
        return this.id;
    }

    /**
     * Sets the ID of the payment.
     *
     * @param id
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * @return the amount
     */
    public int getAmount () {
        return amount;
    }

    /**
     * @param amount
     *            the amount to set
     */
    public void setAmount ( final int amount ) {
        this.amount = amount;
    }

    /**
     * @return the method
     */
    public PaymentMethod getMethod () {
        return method;
    }

    /**
     * @param method
     *            the method to set
     */
    public void setMethod ( final PaymentMethod method ) {
        this.method = method;
    }
}
