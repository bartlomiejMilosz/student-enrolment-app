package io.bartmilo.student.enrolment.app.mapper;

public interface Mapper<Entity, Dto> {
    Entity mapTo(Dto dto);
    Dto mapFrom(Entity entity);
}
