package org.scheez.test.querydsl;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QDepartment is a Querydsl query type for Department
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QDepartment extends EntityPathBase<Department> {

    private static final long serialVersionUID = 994988113;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QDepartment department = new QDepartment("department");

    public final org.scheez.util.QBaseObject _super = new org.scheez.util.QBaseObject(this);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QEmployee manager;

    public final StringPath name = createString("name");

    public QDepartment(String variable) {
        this(Department.class, forVariable(variable), INITS);
    }

    @SuppressWarnings("all")
    public QDepartment(Path<? extends Department> path) {
        this((Class)path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QDepartment(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QDepartment(PathMetadata<?> metadata, PathInits inits) {
        this(Department.class, metadata, inits);
    }

    public QDepartment(Class<? extends Department> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.manager = inits.isInitialized("manager") ? new QEmployee(forProperty("manager"), inits.get("manager")) : null;
    }

}

