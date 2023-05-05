package edu.ncsu.csc.iTrust2.models.enums;

/**
 * Enum with bill statuses of paid, unpaid, and delinquent.
 *
 * @author Dion Ybanez
 *
 */
public enum BillStatus {

    /* Positive statuses */

    /**
     * Paid bill
     */
    PAID ( 1 ),

    /**
     * Unpaid bill
     */
    UNPAID ( 2 ),

    /**
     * overdue bill
     */
    DELINQUENT ( 3 ),

    /*
     * partially paid bill
     */
    PARTIALLY_PAID ( 4 );

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
    private BillStatus ( final int code ) {
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
