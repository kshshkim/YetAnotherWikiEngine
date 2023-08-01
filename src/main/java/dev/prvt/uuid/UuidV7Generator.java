package dev.prvt.uuid;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.UUIDGenerationStrategy;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.hibernate.type.descriptor.java.UUIDTypeDescriptor;

import java.io.Serializable;
import java.util.Properties;
import java.util.UUID;

public class UuidV7Generator implements IdentifierGenerator {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger( org.hibernate.id.UUIDGenerator.class );

    private UUIDGenerationStrategy strategy;
    private UUIDTypeDescriptor.ValueTransformer valueTransformer;

//    public static UuidV7Generator buildSessionFactoryUniqueIdentifierGenerator() {
//        final UuidV7Generator generator = new UuidV7Generator();
//        generator.strategy = StandardRandomStrategy.INSTANCE;
//        generator.valueTransformer = UUIDTypeDescriptor.ToStringTransformer.INSTANCE;
//        return generator;
//    }

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        strategy = UuidV7GenerationStrategy.INSTANCE;
        if ( UUID.class.isAssignableFrom( type.getReturnedClass() ) ) {
            valueTransformer = UUIDTypeDescriptor.PassThroughTransformer.INSTANCE;
        }
        else if ( String.class.isAssignableFrom( type.getReturnedClass() ) ) {
            valueTransformer = UUIDTypeDescriptor.ToStringTransformer.INSTANCE;
        }
        else if ( byte[].class.isAssignableFrom( type.getReturnedClass() ) ) {
            valueTransformer = UUIDTypeDescriptor.ToBytesTransformer.INSTANCE;
        }
        else {
            throw new HibernateException( "Unanticipated return type [" + type.getReturnedClass().getName() + "] for UUID conversion" );
        }
    }

    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return valueTransformer.transform( strategy.generateUUID( session ) );
    }


}
