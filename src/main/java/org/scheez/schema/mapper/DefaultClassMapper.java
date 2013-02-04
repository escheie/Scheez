package org.scheez.schema.mapper;

import org.atteo.evo.inflector.English;

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
        String name = nameMapper.mapJavaNameToDatabaseName(cls.getSimpleName());
        int index = name.lastIndexOf('_');
        if (index < 0)
        {
            name = English.plural(name);
        }
        else
        {
            name = name.substring(0, index + 1) + English.plural(name.substring(index + 1));
        }
        return name;
    }

}
