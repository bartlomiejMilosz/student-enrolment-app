package io.bartmilo.student.enrolment.app.mapper;

public interface Mapper<Entity, Dto> {
    /**
     * Maps specified dto to entity.
     * @param dto   The dto object type to map.
     * @return      The mapped object as entity.
     */
    Entity mapTo(Dto dto);

    /**
     * Maps specified entity to dto.
     * @param entity    The entity object type to map.
     * @return          The mapped object as dto.
     */
    Dto mapFrom(Entity entity);
}
