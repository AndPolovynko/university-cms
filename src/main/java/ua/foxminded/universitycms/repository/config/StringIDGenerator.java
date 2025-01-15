package ua.foxminded.universitycms.repository.config;

import java.util.UUID;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import ua.foxminded.universitycms.domain.Identifiable;

public class StringIDGenerator implements IdentifierGenerator {
  @Override
  public Object generate(SharedSessionContractImplementor session, Object object) {
    if (object instanceof Identifiable identifiable) {
      return identifiable.getId() == null ? UUID.randomUUID().toString() : identifiable.getId();     
    } else {
      return null;
    }
  }
}
