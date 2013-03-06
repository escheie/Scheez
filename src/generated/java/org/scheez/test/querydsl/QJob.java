package org.scheez.test.querydsl;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QJob is a Querydsl query type for Job
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QJob extends EntityPathBase<Job> {

    private static final long serialVersionUID = 818978718;

    public static final QJob job = new QJob("job");

    public final org.scheez.util.QBaseObject _super = new org.scheez.util.QBaseObject(this);

    public final NumberPath<Integer> grade = createNumber("grade", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<JobTrack> jobTrack = createEnum("jobTrack", JobTrack.class);

    public final NumberPath<java.math.BigDecimal> maxSalary = createNumber("maxSalary", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> minSalary = createNumber("minSalary", java.math.BigDecimal.class);

    public QJob(String variable) {
        super(Job.class, forVariable(variable));
    }

    @SuppressWarnings("all")
    public QJob(Path<? extends Job> path) {
        super((Class)path.getType(), path.getMetadata());
    }

    public QJob(PathMetadata<?> metadata) {
        super(Job.class, metadata);
    }

}

