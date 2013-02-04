package org.scheez.schema.mapper;

import org.scheez.schema.objects.Table;
import org.scheez.schema.objects.TableName;

public interface ClassMapper
{
    String mapClass (Class<?> cls);

    Table mapClass (Class<?> cls, TableName tableName);
}
