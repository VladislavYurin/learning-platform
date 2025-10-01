package ru.mentor.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("mentor_time_slot__users")
public class MentorTimeSlotParticipantEntity {

    @Id
    private Long id;

    @Column("time_slot_id")
    private Long slotId;

    @Column("user_id")
    private Long userId;

}
