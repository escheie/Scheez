package org.scheez.schema.mapper;

public class DefaultClassMapper implements ClassMapper
{
    private NameMapper nameMapper;

    public DefaultClassMapper()
    {
        this(new DefaultNameMapper());
    }

    public DefaultClassMapper(NameMapper nameMapper)
    {
        this.nameMapper = nameMapper;
    }

    @Override
    public String mapClass (Class<?> cls)
    {
        return nameMapper.mapJavaNameToDatabaseName(cls.getSimpleName());
    }

}
