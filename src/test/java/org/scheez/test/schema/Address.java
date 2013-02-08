package org.scheez.test.schema;

import javax.persistence.Table;

import org.scheez.persistence.Index;
import org.scheez.test.def.State;

/**
 * @author Eric
 */
@Index (name="address_idx", fieldNames={"address1", "address2"})
@Table(name = "address_book")
public class Address
{
    private Long id;

    private String address1;

    private String address2;

    @Index
    private State state;

    private String city;

    private String zipCode;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getAddress1()
    {
        return address1;
    }

    public void setAddress1(String address1)
    {
        this.address1 = address1;
    }

    public String getAddress2()
    {
        return address2;
    }

    public void setAddress2(String address2)
    {
        this.address2 = address2;
    }

    public State getState()
    {
        return state;
    }

    public void setState(State state)
    {
        this.state = state;
    }

    public String getZipCode()
    {
        return zipCode;
    }

    public void setZipCode(String zipCode)
    {
        this.zipCode = zipCode;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

}
