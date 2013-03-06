package org.scheez.test.querydsl;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QEmployee is a Querydsl query type for Employee
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QEmployee extends EntityPathBase<Employee> {

    private static final long serialVersionUID = -567269523;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QEmployee employee = new QEmployee("employee");

    public final org.scheez.util.QBaseObject _super = new org.scheez.util.QBaseObject(this);

    public final QDepartment department;

    public final StringPath email = createString("email");

    public final StringPath firstName = createString("firstName");

    public final DateTimePath<java.sql.Timestamp> hireDate = createDateTime("hireDate", java.sql.Timestamp.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QJob job;

    public final StringPath lastName = createString("lastName");

    public final QEmployee manager;

    public final ComparablePath<Character> middleInitial = createComparable("middleInitial", Character.class);

    public final StringPath phoneNumber = createString("phoneNumber");

    public final NumberPath<java.math.BigDecimal> salary = createNumber("salary", java.math.BigDecimal.class);

    public final StringPath title = createString("title");

    public QEmployee(String variable) {
        this(Employee.class, forVariable(variable), INITS);
    }

    @SuppressWarnings("all")
    public QEmployee(Path<? extends Employee> path) {
        this((Class)path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QEmployee(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QEmployee(PathMetadata<?> metadata, PathInits inits) {
        this(Employee.class, metadata, inits);
    }

    public QEmployee(Class<? extends Employee> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.department = inits.isInitialized("department") ? new QDepartment(forProperty("department"), inits.get("department")) : null;
        this.job = inits.isInitialized("job") ? new QJob(forProperty("job")) : null;
        this.manager = inits.isInitialized("manager") ? new QEmployee(forProperty("manager"), inits.get("manager")) : null;
    }

}

