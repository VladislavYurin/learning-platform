package ru.mentor.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import ru.mentor.common.AllTimeSlotsResponse;
import ru.mentor.common.MentorSlotInfo;
import ru.mentor.common.PageDetails;
import ru.mentor.common.TimeSlotResponse;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;

class TimeSlotMapperTest {

    private final TimeSlotMapper mapper = new TimeSlotMapperImpl();

    @Test
    void entityToGrpcResponse_returnsExpectedResponse() {
        MentorTimeSlotEntity entity = TestEntityStubGenerator.constructMentorTimeSlotEntity();

        TimeSlotResponse response = mapper.entityToGrpcResponse(
                entity,
                TestConstantHolder.REQUEST_ID
        );

        Assertions.assertEquals(TestConstantHolder.REQUEST_ID, response.getRequestId());
        Assertions.assertEquals(TestConstantHolder.SLOT_ID, response.getSlotId());
        Assertions.assertEquals(TestConstantHolder.MENTOR_ID, response.getMentorId());
        Assertions.assertEquals(
                TestConstantHolder.SLOT_START_EPOCH_SECONDS,
                response.getStartTime().getSeconds()
        );
        Assertions.assertEquals(
                TestConstantHolder.SLOT_END_EPOCH_SECONDS,
                response.getEndTime().getSeconds()
        );
        Assertions.assertEquals(TestConstantHolder.GRPC_SLOT_TYPE, response.getSlotType());
        Assertions.assertEquals(
                TestConstantHolder.GRPC_SLOT_MEETING_TYPE,
                response.getSlotMeetingType()
        );
        Assertions.assertEquals(TestConstantHolder.MAX_PARTICIPANTS, response.getMaxParticipants());
        Assertions.assertEquals(TestConstantHolder.MEETING_LINK, response.getMeetingLink());
        Assertions.assertEquals(TestConstantHolder.SLOT_DESCRIPTION, response.getDescription());
        Assertions.assertEquals(
                TestConstantHolder.CREATED_AT_EPOCH_SECONDS,
                response.getCreatedAt().getSeconds()
        );
    }

    @Test
    void mentorTimeSlotEntityToMentorSlotInfo_returnsExpectedResponse() {
        MentorSlotInfo expected = TestGrpcStubGenerator.constructMentorSlotInfo();

        MentorSlotInfo actual = mapper.mentorTimeSlotEntityToMentorSlotInfo(
                expected.getSlotInfo(),
                expected.getParticipantsList()
        );

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void mapMentorTimeSlotEntityPageToAllTimeSlotsResponse_returnsExpectedAggregation() {
        Page<MentorSlotInfo> page = TestGrpcStubGenerator.constructMentorSlotInfoPage();

        AllTimeSlotsResponse aggregated = mapper.mapMentorTimeSlotEntityPageToAllTimeSlotsResponse(
                page);
        MentorSlotInfo expectedSlot = page.getContent().get(TestConstantHolder.PAGE_NUMBER);
        PageDetails pageDetails = aggregated.getPageDetails();

        Assertions.assertEquals(page.getContent().size(), aggregated.getTimeSlotsCount());
        Assertions.assertEquals(
                expectedSlot,
                aggregated.getTimeSlots(TestConstantHolder.PAGE_NUMBER)
        );
        Assertions.assertEquals(page.getNumber(), pageDetails.getPage());
        Assertions.assertEquals(page.getSize(), pageDetails.getSize());
        Assertions.assertEquals(page.getTotalElements(), pageDetails.getTotalElements());
        Assertions.assertEquals(page.getTotalPages(), pageDetails.getTotalPages());
    }

}
