package org.scheez.schema.manger;

import java.util.List;

import org.scheez.schema.diff.SchemaDifference;

public interface SchemaManager
{
    List<SchemaDifference> diff ();  
}