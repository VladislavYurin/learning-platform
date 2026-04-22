package ru.mentor.mapper;

import com.google.protobuf.ByteString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import ru.mentor.common.CreateModuleGrpcRequest;
import ru.mentor.common.DeleteModuleRequest;
import ru.mentor.common.GetModuleRequest;
import ru.mentor.common.Header;
import ru.mentor.common.ImportModuleFromFileRequest;
import ru.mentor.common.ModuleResponse;
import ru.mentor.dto.ModuleDto;
import ru.mentor.dto.front.CreateModuleRequest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModuleMapperTest {

    @Mock
    private AdminModuleMapper adminModuleMapper;

    @InjectMocks
    private ModuleMapper moduleMapper;

    private static final Long SENDER_ID = 1L;
    private static final Long COURSE_ID = 10L;
    private static final Long MODULE_ID = 100L;
    private static final Integer MODULE_ORDER_NUM = 2;

    @Test
    void constructGrpcGetRequest_withoutModuleId_buildsRequestCorrectly() {
        Header header = Header.newBuilder().setRequestId("rq-1").build();

        GetModuleRequest result = moduleMapper.constructGrpcGetRequest(
                header,
                SENDER_ID,
                COURSE_ID,
                MODULE_ORDER_NUM,
                0L
        );

        assertNotNull(result);
        assertEquals(header, result.getHeader());
        assertEquals(SENDER_ID.longValue(), result.getSenderId());
        assertEquals(COURSE_ID.longValue(), result.getCourseId());
        assertEquals(MODULE_ORDER_NUM.intValue(), result.getModuleOrderNumber());
        assertEquals(0L, result.getModuleId());
    }

    @Test
    void constructGrpcGetRequest_withModuleId_buildsRequestCorrectly() {
        Header header = Header.newBuilder().setRequestId("rq-2").build();

        GetModuleRequest result = moduleMapper.constructGrpcGetRequest(
                header,
                SENDER_ID,
                COURSE_ID,
                MODULE_ORDER_NUM,
                MODULE_ID
        );

        assertNotNull(result);
        assertEquals(header, result.getHeader());
        assertEquals(SENDER_ID.longValue(), result.getSenderId());
        assertEquals(COURSE_ID.longValue(), result.getCourseId());
        assertEquals(MODULE_ORDER_NUM.intValue(), result.getModuleOrderNumber());
        assertEquals(MODULE_ID.longValue(), result.getModuleId());
    }

    @Test
    void constructGrpcImportFromFileRequest_buildsRequestCorrectly() throws IOException {
        Header header = Header.newBuilder().setRequestId("rq-3").build();

        CreateModuleRequest createRequest = CreateModuleRequest.builder()
                .courseId(COURSE_ID)
                .moduleTitle("Title")
                .moduleOrderNumber(MODULE_ORDER_NUM)
                .moduleContentDescription("Content")
                .build();

        MultipartFile file = mock(MultipartFile.class);
        byte[] fileBytes = "file-content".getBytes();
        when(file.getName()).thenReturn("module.md");
        when(file.getBytes()).thenReturn(fileBytes);

        ImportModuleFromFileRequest result = moduleMapper.constructGrpcImportFromFileRequest(
                header,
                SENDER_ID,
                createRequest,
                file
        );

        assertNotNull(result);
        assertEquals(header, result.getHeader());
        assertEquals(SENDER_ID.longValue(), result.getSenderId());
        assertEquals(COURSE_ID.longValue(), result.getCourseId());
        assertEquals("Title", result.getTitle());
        assertEquals(MODULE_ORDER_NUM.intValue(), result.getOrderNumber());
        assertEquals("Content", result.getContent());
        assertEquals("module.md", result.getFilename());
        assertEquals(ByteString.copyFrom(fileBytes), result.getFileContent());
    }

    @Test
    void constructGrpcCreateRequest_buildsRequestCorrectly() {
        Header header = Header.newBuilder().setRequestId("rq-4").build();

        CreateModuleRequest createRequest = CreateModuleRequest.builder()
                .courseId(COURSE_ID)
                .moduleTitle("Title")
                .moduleOrderNumber(MODULE_ORDER_NUM)
                .moduleContentDescription("Content")
                .build();

        CreateModuleGrpcRequest result = moduleMapper.constructGrpcCreateRequest(
                header,
                SENDER_ID,
                createRequest
        );

        assertNotNull(result);
        assertEquals(header, result.getHeader());
        assertEquals(SENDER_ID.longValue(), result.getSenderId());
        assertEquals(COURSE_ID.longValue(), result.getCourseId());
        assertEquals("Title", result.getTitle());
        assertEquals(MODULE_ORDER_NUM.intValue(), result.getOrderNumber());
        assertEquals("Content", result.getContent());
    }

    @Test
    void constructGrpcDeleteRequest_withoutModuleId_buildsRequestCorrectly() {
        Header header = Header.newBuilder().setRequestId("rq-5").build();

        DeleteModuleRequest result = moduleMapper.constructGrpcDeleteRequest(
                header,
                SENDER_ID,
                COURSE_ID,
                MODULE_ORDER_NUM,
                0L
        );

        assertNotNull(result);
        assertEquals(header, result.getHeader());
        assertEquals(SENDER_ID.longValue(), result.getSenderId());
        assertEquals(COURSE_ID.longValue(), result.getCourseId());
        assertEquals(MODULE_ORDER_NUM.intValue(), result.getModuleOrderNumber());
        assertEquals(0L, result.getModuleId());
    }

    @Test
    void constructGrpcDeleteRequest_withModuleId_buildsRequestCorrectly() {
        Header header = Header.newBuilder().setRequestId("rq-6").build();

        DeleteModuleRequest result = moduleMapper.constructGrpcDeleteRequest(
                header,
                SENDER_ID,
                COURSE_ID,
                MODULE_ORDER_NUM,
                MODULE_ID
        );

        assertNotNull(result);
        assertEquals(header, result.getHeader());
        assertEquals(SENDER_ID.longValue(), result.getSenderId());
        assertEquals(COURSE_ID.longValue(), result.getCourseId());
        assertEquals(MODULE_ORDER_NUM.intValue(), result.getModuleOrderNumber());
        assertEquals(MODULE_ID.longValue(), result.getModuleId());
    }

    @Test
    void mapGrpcModuleResponseToModuleDto_delegatesToAdminModuleMapper() {
        ModuleResponse moduleResponse = ModuleResponse.newBuilder()
                .setModuleId(MODULE_ID)
                .setTitle("Title")
                .build();

        ModuleDto expectedDto = ModuleDto.builder().build();
        when(adminModuleMapper.mapGrpcModuleResponseToModuleDto(moduleResponse)).thenReturn(expectedDto);

        ModuleDto result = moduleMapper.mapGrpcModuleResponseToModuleDto(moduleResponse);

        assertNotNull(result);
        assertSame(expectedDto, result);
        verify(adminModuleMapper).mapGrpcModuleResponseToModuleDto(moduleResponse);
    }
}

