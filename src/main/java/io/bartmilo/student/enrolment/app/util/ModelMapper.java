package io.bartmilo.student.enrolment.app.util;

public interface ModelMapper<T, K> {
  /**
   * Maps specified dto to entity.
   *
   * @param dto The dto object type to map.
   * @return The mapped object as entity.
   */
  T mapTo(K dto);

  /**
   * Maps specified entity to dto.
   *
   * @param entity The entity object type to map.
   * @return The mapped object as dto.
   */
  K mapFrom(T entity);
}
