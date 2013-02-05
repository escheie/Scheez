package org.scheez.schema.mapper;

import org.scheez.schema.parts.Table;
import org.scheez.schema.parts.TableName;

public interface ClassMapper
{
    String mapClass (Class<?> cls);

    Table mapClass (Class<?> cls, TableName tableName);
}
