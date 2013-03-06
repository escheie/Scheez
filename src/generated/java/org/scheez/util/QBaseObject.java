package org.scheez.util;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QBaseObject is a Querydsl query type for BaseObject
 */
@Generated("com.mysema.query.codegen.EmbeddableSerializer")
public class QBaseObject extends BeanPath<BaseObject> {

    private static final long serialVersionUID = -234982374;

    public static final QBaseObject baseObject = new QBaseObject("baseObject");

    public QBaseObject(String variable) {
        super(BaseObject.class, forVariable(variable));
    }

    @SuppressWarnings("all")
    public QBaseObject(Path<? extends BaseObject> path) {
        super((Class)path.getType(), path.getMetadata());
    }

    public QBaseObject(PathMetadata<?> metadata) {
        super(BaseObject.class, metadata);
    }

}

