package org.scheez.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class BaseObject
{
    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean equals = false;
        if (obj != null)
        {
            equals = EqualsBuilder.reflectionEquals(this, obj);
        }
        return equals;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.MULTI_LINE_STYLE);
    }

    public boolean hasNulls()
    {
        boolean nulls = false;
        try
        {
            BeanInfo beanInfo = Introspector.getBeanInfo(getClass());
            for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors())
            {
                if (descriptor.getReadMethod().invoke(this) == null)
                {
                    nulls = true;
                    break;
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return nulls;
    }
    
    public boolean allNulls()
    {
        boolean nulls = true;
        try
        {
            BeanInfo beanInfo = Introspector.getBeanInfo(getClass());
            for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors())
            {
                if (descriptor.getReadMethod().invoke(this) != null)
                {
                    nulls = false;
                    break;
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return nulls;
    }
}
