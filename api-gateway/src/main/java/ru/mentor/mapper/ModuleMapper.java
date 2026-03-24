package ru.mentor.mapper;

import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.mentor.common.CreateModuleGrpcRequest;
import ru.mentor.common.DeleteModuleRequest;
import ru.mentor.common.GetModuleRequest;
import ru.mentor.common.Header;
import ru.mentor.common.ImportModuleFromFileRequest;
import ru.mentor.common.ModuleResponse;
import ru.mentor.common.UpdateModuleGrpcRequest;
import ru.mentor.dto.ModuleDto;
import ru.mentor.dto.front.CreateModuleRequest;
import ru.mentor.dto.front.UpdateModuleRequest;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ModuleMapper {

    private final AdminModuleMapper adminModuleMapper;

    public GetModuleRequest constructGrpcGetRequest(
            Header header, Long senderId, Long courseId,
            Long moduleId) {
        return GetModuleRequest.newBuilder()
                               .setHeader(header)
                               .setSenderId(senderId)
                               .setCourseId(courseId)
                               .setModuleId(moduleId)
                               .build();
    }

    public GetModuleRequest constructGrpcGetRequest(
            Header header, Long senderId, Long courseId,
            Integer moduleOrderNum, Long moduleId) {
        return GetModuleRequest.newBuilder()
                               .setHeader(header)
                               .setSenderId(senderId)
                               .setCourseId(courseId)
                               .setModuleOrderNumber(moduleOrderNum)
                               .setModuleId(moduleId)
                               .build();
    }

    public ImportModuleFromFileRequest constructGrpcImportFromFileRequest(
            Header header,
            Long userId,
            CreateModuleRequest request,
            MultipartFile file
    ) throws IOException {
        return ImportModuleFromFileRequest.newBuilder()
                                          .setHeader(header)
                                          .setSenderId(userId)
                                          .setCourseId(request.getCourseId())
                                          .setTitle(request.getModuleTitle())
                                          .setOrderNumber(request.getModuleOrderNumber())
                                          .setContent(request.getModuleContentDescription())
                                          .setFilename(file.getName())
                                          .setFileContent(ByteString.copyFrom(file.getBytes()))
                                          .build();
    }

    public CreateModuleGrpcRequest constructGrpcCreateRequest(
            Header header,
            Long userId,
            CreateModuleRequest request) {
        return CreateModuleGrpcRequest.newBuilder()
                                      .setHeader(header)
                                      .setSenderId(userId)
                                      .setCourseId(request.getCourseId())
                                      .setTitle(request.getModuleTitle())
                                      .setOrderNumber(request.getModuleOrderNumber())
                                      .setContent(request.getModuleContentDescription())
                                      .build();
    }

    public DeleteModuleRequest constructGrpcDeleteRequest(
            Header header, Long userId, Long courseId,
            Long moduleId) {
        return DeleteModuleRequest.newBuilder()
                                  .setHeader(header)
                                  .setSenderId(userId)
                                  .setCourseId(courseId)
                                  .setModuleId(moduleId)
                                  .build();
    }

    public UpdateModuleGrpcRequest constructGrpcUpdateRequest(
            Header header,
            Long userId,
            UpdateModuleRequest request) {
        return UpdateModuleGrpcRequest.newBuilder()
                .setHeader(header)
                .setSenderId(userId)
                .setCourseId(request.getCourseId())
                .setModuleId(request.getModuleId())
                .setTitle(request.getModuleTitle())
                .setOrderNumber(request.getModuleOrderNumber())
                .setContent(request.getModuleContentDescription())
                .setIsActive(request.getIsActive())
                .build();
    }

    public DeleteModuleRequest constructGrpcDeleteRequest(
            Header header, Long userId, Long courseId,
            Integer moduleOrderNum, Long moduleId) {
        return DeleteModuleRequest.newBuilder()
                                  .setHeader(header)
                                  .setSenderId(userId)
                                  .setCourseId(courseId)
                                  .setModuleOrderNumber(moduleOrderNum)
                                  .setModuleId(moduleId)
                                  .build();
    }

    public ModuleDto mapGrpcModuleResponseToModuleDto(ModuleResponse moduleResponse) {
        return adminModuleMapper.mapGrpcModuleResponseToModuleDto(moduleResponse);
    }

}
