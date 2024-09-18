package de.triology.universeadm.mapping;

import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Field;
import java.util.Map;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
final class ClassDescriptor<T>
{

  private final Class<T> type;
  private final Map<String,FieldDescriptor<T>> fields;
  
  ClassDescriptor(Class<T> type)
  {
    this.type = type;
    ImmutableMap.Builder<String,FieldDescriptor<T>> builder = ImmutableMap.builder();
    Class<?> c = type;
    while ( c != null )
    {
      for ( Field f : c.getDeclaredFields())
      {
        builder.put(f.getName(), new FieldDescriptor<>(type, f));
      }
      c = c.getSuperclass();
    }
    
    fields = builder.build();
  }
  
  public T newInstance(){
    try {
      return type.newInstance();
    } catch (IllegalAccessException | InstantiationException ex){
      throw new MappingException("could not create new instance", ex);
    }
  }
  
  public FieldDescriptor<T> getField(String name){
    return fields.get(name);
  }

  public Map<String, FieldDescriptor<T>> getFields()
  {
    return fields;
  }

  public Class<T> getType()
  {
    return type;
  }
  
}
