package org.scheez.schema.mapper;

import org.hibernate.cfg.DefaultNamingStrategy;

public class HibernateNamingStrategy extends DefaultNamingStrategy
{
    private static final long serialVersionUID = 4633704464460147458L;

    private NameMapper nameMapper;

    public HibernateNamingStrategy()
    {
        this(new DefaultNameMapper());
    }

    public HibernateNamingStrategy(NameMapper nameMapper)
    {
        this.nameMapper = nameMapper;
    }

    @Override
    public String classToTableName(String className)
    {
        return nameMapper.mapClassNameToTableName(className);
    }

    @Override
    public String propertyToColumnName(String propertyName)
    {
        return nameMapper.mapFieldNameToColumnName(propertyName);
    }

    @Override
    public String foreignKeyColumnName(String propertyName, String propertyEntityName, String propertyTableName,
            String referencedColumnName)
    {
        return propertyToColumnName(propertyName) + "_" + referencedColumnName;
    }
}
