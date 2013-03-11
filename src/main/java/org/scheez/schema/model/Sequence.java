package org.scheez.schema.model;

import org.scheez.util.BaseObject;
import org.scheez.util.DbC;

public class Sequence extends BaseObject
{
    private SequenceName name;

    public Sequence(SequenceName name)
    {
        DbC.throwIfNullArg("name", name);
        this.name = name;
    }

    public SequenceName getName()
    {
        return name;
    }
}
