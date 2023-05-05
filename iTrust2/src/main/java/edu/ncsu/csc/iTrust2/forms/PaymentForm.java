package edu.ncsu.csc.iTrust2.forms;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import edu.ncsu.csc.iTrust2.models.Payment;
import edu.ncsu.csc.iTrust2.models.enums.PaymentMethod;

public class PaymentForm {

    /**
     * Serial Version of the Form. For the Serializable
     */
    private static final long serialVersionUID = 1L;

    /** ID of this bill */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long              id;

    /** amount paid with this payment */
    private int               amount;

    /** method used to pay */
    @Enumerated ( EnumType.STRING )
    private PaymentMethod     method;

    /**
     * Empty constructor so that we can create an Payment form for the user to
     * fill out
     */
    public PaymentForm () {
    }

    /**
     * Creates a PaymentForm from the Payment provided
     *
     * @param pa
     *            Payment to turn into an PaymentForm
     */
    public PaymentForm ( final Payment pa ) {
        setId( pa.getId() );
        setAmount( pa.getAmount() );
        setMethod( pa.getMethod() );
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
