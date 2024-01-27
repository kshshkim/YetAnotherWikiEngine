package dev.prvt.yawiki.common.util.jpa.uuid;


import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.hibernate.annotations.IdGeneratorType;
import org.hibernate.annotations.ValueGenerationType;

@IdGeneratorType(UuidV7GeneratorImpl.class)
@ValueGenerationType(generatedBy = UuidV7GeneratorImpl.class)
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface UuidV7Generator {
}
