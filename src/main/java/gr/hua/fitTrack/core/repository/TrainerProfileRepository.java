package gr.hua.fitTrack.core.repository;

import gr.hua.fitTrack.core.model.TrainerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrainerProfileRepository extends JpaRepository <TrainerProfile, Long> {
    boolean existsByPersonId(Long personId);

    Optional<TrainerProfile> findByPersonId(Long personId);

    @Query("""
        select distinct tp
        from TrainerProfile tp
        join fetch tp.person p
        left join fetch tp.weeklyAvailability wa
        where p.id = :personId
    """)
    Optional<TrainerProfile> findByPersonIdWithWeeklyAvailability(@Param("personId") Long personId);

    @Query("""
        select distinct tp.location
        from TrainerProfile tp
        where tp.location is not null
        order by tp.location
    """)
    List<String> findDistinctLocations();

    @Query("""
        select distinct tp.specialization
        from TrainerProfile tp
        where tp.specialization is not null
        order by tp.specialization
    """)
    List<String> findDistinctSpecializations();

    @Query("""
    select distinct p.lastName
    from TrainerProfile tp
    join tp.person p
    where p.lastName is not null
    order by p.lastName
""")
    List<String> findDistinctLastNames();





}
