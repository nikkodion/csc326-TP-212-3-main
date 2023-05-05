package edu.ncsu.csc.iTrust2.models.enums;

/**
 * Payment method enum that has cash, check, credit, or insurance.
 * 
 * @author Dion Ybanez
 *
 */
public enum PaymentMethod {
    /* Positive statuses */

    /**
     * Paid with cash
     */
    CASH ( 1 ),

    /**
     * Paid with check
     */
    CHECK ( 2 ),

    /**
     * Paid with credit card
     */
    CREDIT_CARD ( 3 ),

    /**
     * Paid with insurance
     */
    INSURANCE ( 4 );

    /**
     * Code of the status
     */
    private int code;

    /**
     * Create a Status from the numerical code.
     *
     * @param code
     *            Code of the Status
     */
    private PaymentMethod ( final int code ) {
        this.code = code;
    }

    /**
     * Gets the numerical Code of the Status
     *
     * @return Code of the Status
     */
    public int getCode () {
        return code;
    }

}
