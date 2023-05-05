package edu.ncsu.csc.iTrust2.forms;

import java.util.Objects;

import edu.ncsu.csc.iTrust2.models.CPTCode;

/**
 * Intermediate form for adding or editing CPT Codes. Used to create and
 * serialize CPT Codes.
 *
 * @author Matthew Howard
 *
 */
public class CPTCodeForm {

    /** The ID number of the CPT Code */
    private Long   id;

    /* The CPT Code number */
    private String code;

    /** The time range of the CPT Code */
    private String description;

    /** The cost associated with the CPT Code */
    private int    cost;

    /**
     * Empty constructor
     */
    public CPTCodeForm () {

    }

    /**
     * Construct a form off an existing CPT Code object
     *
     * @param cptCode
     *            The object to fill this form with
     */
    public CPTCodeForm ( final CPTCode cptCode ) {
        setId( cptCode.getId() );
        setCode( cptCode.getCode() );
        setDescription( cptCode.getDescription() );
        setCost( cptCode.getCost() );
    }

    /**
     * Gets the CPT Code ID number
     *
     * @return id The ID number
     */
    public Long getId () {
        return id;
    }

    /**
     * Sets the ID number of the CPT Code (used by Hibernate)
     *
     * @param id
     *            The new ID number
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets the CPT Code number.
     *
     * @return code The CPT Code number
     */
    public String getCode () {
        return code;
    }

    /**
     * Sets the CPT Code number.
     *
     * @param code
     *            The new CPT Code number
     */
    public void setCode ( final String code ) {
        this.code = code;
    }

    /**
     * Gets the time range associated with the CPT Code
     *
     * @return description The time range of the CPT Code
     */
    public String getDescription () {
        return description;
    }

    /**
     * Sets the time range of the CPT Code
     *
     * @param description
     *            The new description
     */
    public void setDescription ( final String description ) {
        this.description = description;
    }

    /**
     * Gets the cost of the CPT Code
     *
     * @return cost The cost of the CPT Code
     */
    public int getCost () {
        return cost;
    }

    /**
     * Sets the cost of the CPT Code
     *
     * @param cost
     *            The new cost
     */
    public void setCost ( final int cost ) {
        this.cost = cost;
    }

    /**
     * Checks to see if a CPT Code is the same as another
     *
     * @param obj
     *            The second object being compared
     * @return True if the two objects are equal
     */
    @Override
    public boolean equals ( final Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final CPTCodeForm other = (CPTCodeForm) obj;
        return cost == other.cost && Objects.equals( description, other.description ) && Objects.equals( id, other.id );
    }
}
