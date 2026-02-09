package ru.mentor.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import ru.mentor.common.CreateModuleGrpcRequest;
import ru.mentor.common.DeleteModuleRequest;
import ru.mentor.common.GetModuleRequest;
import ru.mentor.common.ImportModuleFromFileRequest;
import ru.mentor.common.ModuleResponse;
import ru.mentor.dto.ModuleDto;
import ru.mentor.dto.front.CreateModuleRequest;
import ru.mentor.exception.GrpcRetryException;
import ru.mentor.factory.HeaderFactory;
import ru.mentor.grpc.CourseServiceModuleGrpcClient;
import ru.mentor.mapper.ModuleMapper;
import ru.mentor.services.impl.RedirectModuleServiceImpl;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestGrpcStubGenerator;

import java.io.IOException;
import ru.mentor.utils.TestDataGenerator;

@ExtendWith(MockitoExtension.class)
class RedirectModuleServiceImplTest {
    @Mock
    private UserService userService;
    @Mock
    private CourseServiceModuleGrpcClient moduleGrpcClient;
    @Mock
    private ModuleMapper moduleMapper;
    @Mock
    private HeaderFactory headerFactory;

    @InjectMocks
    private RedirectModuleServiceImpl redirectModuleService;

    @Test
    void createModule() {
        CreateModuleRequest createModuleRequest = TestDataGenerator.constructCreateModuleRequest();
        CreateModuleGrpcRequest grpcRequest = TestGrpcStubGenerator.constructCreateModuleGrpcRequest();
        ModuleResponse grpcResponse = TestGrpcStubGenerator.constructModuleResponse();
        ModuleDto dto = Mockito.mock(ModuleDto.class);

        Mockito.when(moduleMapper.toCreateModuleGrpcRequest(
                        ArgumentMatchers.any(),
                        ArgumentMatchers.eq(TestConstantHolder.userId),
                        ArgumentMatchers.eq(createModuleRequest)
                ))
                .thenReturn(grpcRequest);
        Mockito.when(moduleMapper.moduleResponseToModuleDto(grpcResponse))
                .thenReturn(dto);
        Mockito.when(moduleGrpcClient.createModule(ArgumentMatchers.eq(grpcRequest)))
                .thenReturn(grpcResponse);
        Mockito.when(userService.getCurrentUserId())
                .thenReturn(TestConstantHolder.userId);

        ModuleDto result = redirectModuleService.createModule(createModuleRequest);

        Assertions.assertThat(result).isEqualTo(dto);
        Mockito.verify(moduleMapper).toCreateModuleGrpcRequest(
                ArgumentMatchers.any(),
                ArgumentMatchers.eq(TestConstantHolder.userId),
                ArgumentMatchers.eq(createModuleRequest)
        );
        Mockito.verify(moduleGrpcClient).createModule(ArgumentMatchers.any(CreateModuleGrpcRequest.class));
        Mockito.verify(moduleMapper).moduleResponseToModuleDto(grpcResponse);
    }

    @Test
    void importModuleFromFile_success() throws IOException {
        CreateModuleRequest createModuleRequest = TestDataGenerator.constructCreateModuleRequest();
        MultipartFile file = Mockito.mock(MultipartFile.class);
        ImportModuleFromFileRequest grpcRequest = TestGrpcStubGenerator.constructImportModuleFromFileGrpcRequest();
        ModuleDto dto = Mockito.mock(ModuleDto.class);
        ModuleResponse grpcResponse = TestGrpcStubGenerator.constructModuleResponse();

        Mockito.when(moduleMapper.toImportModuleFromFileRequest(
                        ArgumentMatchers.any(),
                        ArgumentMatchers.eq(TestConstantHolder.userId),
                        ArgumentMatchers.eq(createModuleRequest),
                        ArgumentMatchers.eq(file)
                ))
                .thenReturn(grpcRequest);
        Mockito.when(moduleGrpcClient.importModuleFromMarkdown(ArgumentMatchers.eq(grpcRequest)))
                .thenReturn(grpcResponse);
        Mockito.when(moduleMapper.moduleResponseToModuleDto(grpcResponse))
                .thenReturn(dto);
        Mockito.when(userService.getCurrentUserId())
                .thenReturn(TestConstantHolder.userId);

        ModuleDto result = redirectModuleService.importModuleFromFile(createModuleRequest, file);

        Assertions.assertThat(result).isEqualTo(dto);
        Mockito.verify(moduleMapper).toImportModuleFromFileRequest(
                ArgumentMatchers.any(),
                ArgumentMatchers.eq(TestConstantHolder.userId),
                ArgumentMatchers.eq(createModuleRequest),
                ArgumentMatchers.eq(file)
        );
        Mockito.verify(moduleGrpcClient).importModuleFromMarkdown(ArgumentMatchers.eq(grpcRequest));
        Mockito.verify(moduleMapper).moduleResponseToModuleDto(grpcResponse);
    }

    @Test
    void importModuleFromFile_failure() throws IOException {
        CreateModuleRequest createModuleRequest = TestDataGenerator.constructCreateModuleRequest();
        MultipartFile file = Mockito.mock(MultipartFile.class);
        ImportModuleFromFileRequest grpcRequest = TestGrpcStubGenerator.constructImportModuleFromFileGrpcRequest();
        ModuleDto dto = Mockito.mock(ModuleDto.class);
        ModuleResponse grpcResponse = TestGrpcStubGenerator.constructModuleResponse();

        Mockito.when(moduleMapper.toImportModuleFromFileRequest(
                        ArgumentMatchers.any(),
                        ArgumentMatchers.eq(TestConstantHolder.userId),
                        ArgumentMatchers.eq(createModuleRequest),
                        ArgumentMatchers.eq(file)
                ))
                .thenThrow(new RuntimeException("Ошибка чтения файла"));
        Mockito.when(userService.getCurrentUserId())
                .thenReturn(TestConstantHolder.userId);

        Assertions.assertThatThrownBy(() -> redirectModuleService.importModuleFromFile(createModuleRequest, file))
                .isInstanceOf(RuntimeException.class);

        Mockito.verify(moduleMapper).toImportModuleFromFileRequest(
                ArgumentMatchers.any(),
                ArgumentMatchers.eq(TestConstantHolder.userId),
                ArgumentMatchers.eq(createModuleRequest),
                ArgumentMatchers.eq(file)
        );
        Mockito.verify(moduleGrpcClient, Mockito.times(0))
                .importModuleFromMarkdown(ArgumentMatchers.eq(grpcRequest));
        Mockito.verify(moduleMapper, Mockito.times(0))
                .moduleResponseToModuleDto(grpcResponse);
    }

    @Test
    void getModuleByOrderNum_success() {
        ModuleResponse grpcResponse = TestGrpcStubGenerator.constructModuleResponse();
        ModuleDto dto = Mockito.mock(ModuleDto.class);
        GetModuleRequest getModuleRequest = TestGrpcStubGenerator.constructGetModuleRequest();

        Mockito.when(moduleMapper.toGetModuleRequest(
                        ArgumentMatchers.any(),
                        ArgumentMatchers.eq(TestConstantHolder.userId),
                        ArgumentMatchers.eq(TestConstantHolder.courseId),
                        ArgumentMatchers.eq(TestConstantHolder.moduleOrderNumber)
                ))
                .thenReturn(getModuleRequest);
        Mockito.when(moduleGrpcClient.getModule(ArgumentMatchers.eq(getModuleRequest)))
                .thenReturn(grpcResponse);
        Mockito.when(moduleMapper.moduleResponseToModuleDto(grpcResponse))
                .thenReturn(dto);

        Mockito.when(userService.getCurrentUserId())
                .thenReturn(TestConstantHolder.userId);

        ModuleDto result = redirectModuleService.getModuleByOrderNum(
                TestConstantHolder.courseId, TestConstantHolder.moduleOrderNumber);

        Assertions.assertThat(result).isEqualTo(dto);
        Mockito.verify(moduleMapper).toGetModuleRequest(
                ArgumentMatchers.any(),
                ArgumentMatchers.eq(TestConstantHolder.userId),
                ArgumentMatchers.eq(TestConstantHolder.courseId),
                ArgumentMatchers.eq(TestConstantHolder.moduleOrderNumber)
        );
        Mockito.verify(moduleGrpcClient).getModule(ArgumentMatchers.eq(getModuleRequest));
        Mockito.verify(moduleMapper).moduleResponseToModuleDto(grpcResponse);
    }

    @Test
    void getModuleByOrderNum_failure() {
        GetModuleRequest getModuleRequest = TestGrpcStubGenerator.constructGetModuleRequest();

        Mockito.when(moduleMapper.toGetModuleRequest(
                        ArgumentMatchers.any(),
                        ArgumentMatchers.eq(TestConstantHolder.userId),
                        ArgumentMatchers.eq(TestConstantHolder.courseId),
                        ArgumentMatchers.eq(TestConstantHolder.moduleOrderNumber)
                ))
                .thenReturn(getModuleRequest);
        Mockito.when(moduleGrpcClient.getModule(ArgumentMatchers.any(GetModuleRequest.class)))
                .thenAnswer(invocation -> {
                    GetModuleRequest request = invocation.getArgument(0, GetModuleRequest.class);
                    throw new GrpcRetryException(TestConstantHolder.grpcExceptionText, request.getHeader().getRequestId());
                });

        Mockito.when(userService.getCurrentUserId())
                .thenReturn(TestConstantHolder.userId);

        Assertions.assertThatThrownBy(() -> redirectModuleService.getModuleByOrderNum(TestConstantHolder.courseId,
                                                                                      TestConstantHolder.moduleOrderNumber))
                .isInstanceOf(GrpcRetryException.class)
                .hasMessageContaining(TestConstantHolder.grpcExceptionText);

        Mockito.verify(moduleGrpcClient).getModule(ArgumentMatchers.eq(getModuleRequest));
    }

    @Test
    void deleteModule() {
        DeleteModuleRequest deleteModuleRequest = TestGrpcStubGenerator.constructDeleteModuleRequest();

        Mockito.when(moduleMapper.toDeleteModuleRequest(
                        ArgumentMatchers.any(),
                        ArgumentMatchers.eq(TestConstantHolder.userId),
                        ArgumentMatchers.eq(TestConstantHolder.courseId),
                        ArgumentMatchers.eq(TestConstantHolder.moduleOrderNumber)
                )).thenReturn(deleteModuleRequest);

        Mockito.when(userService.getCurrentUserId())
                .thenReturn(TestConstantHolder.userId);

        redirectModuleService.deleteModule(TestConstantHolder.courseId, TestConstantHolder.moduleOrderNumber);

        Mockito.verify(moduleMapper).toDeleteModuleRequest(
                ArgumentMatchers.any(),
                ArgumentMatchers.eq(TestConstantHolder.userId),
                ArgumentMatchers.eq(TestConstantHolder.courseId),
                ArgumentMatchers.eq(TestConstantHolder.moduleOrderNumber)
        );

        Mockito.verify(moduleGrpcClient).deleteModule(ArgumentMatchers.eq(deleteModuleRequest));
    }
}