package ru.mentor.mapper;

import com.google.protobuf.ByteString;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
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

@Mapper(componentModel = "spring",
        uses = AdminModuleMapper.class,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        imports = {ByteString.class,
                   MultipartFile.class,
                   IOException.class})
public abstract class ModuleMapper {

    @Autowired
    protected AdminModuleMapper adminModuleMapper;

    @Mapping(target = "header", source = "header")
    @Mapping(target = "senderId", source = "senderId")
    @Mapping(target = "moduleId", source = "moduleId")
    @Mapping(target = "courseId", source = "courseId")
    @Mapping(target = "moduleOrderNumber", ignore = true)
    public abstract GetModuleRequest constructGrpcGetRequest(
            Header header, Long senderId, Long courseId,
            Long moduleId);

    @Mapping(target = "header", source = "header")
    @Mapping(target = "senderId", source = "senderId")
    @Mapping(target = "moduleId", source = "moduleId")
    @Mapping(target = "courseId", source = "courseId")
    @Mapping(target = "moduleOrderNumber", source = "moduleOrderNum")
    public abstract GetModuleRequest constructGrpcGetRequest(
            Header header, Long senderId, Long courseId,
            Integer moduleOrderNum, Long moduleId);

    @Mapping(target = "header", source = "header")
    @Mapping(target = "senderId", source = "userId")
    @Mapping(target = "courseId", source = "request.courseId")
    @Mapping(target = "title", source = "request.moduleTitle")
    @Mapping(target = "content", source = "request.moduleContentDescription")
    @Mapping(target = "orderNumber", source = "request.moduleOrderNumber")
    @Mapping(target = "filename", source = "file.name")
    @Mapping(target = "fileContent", expression = "java(ByteString.copyFrom(file.getBytes()))")
    public abstract ImportModuleFromFileRequest constructGrpcImportFromFileRequest(
            Header header,
            Long userId,
            CreateModuleRequest request,
            MultipartFile file
    ) throws IOException;

    @Mapping(target = "header", source = "header")
    @Mapping(target = "senderId", source = "userId")
    @Mapping(target = "courseId", source = "request.courseId")
    @Mapping(target = "title", source = "request.moduleTitle")
    @Mapping(target = "content", source = "request.moduleContentDescription")
    @Mapping(target = "orderNumber", source = "request.moduleOrderNumber")
    public abstract CreateModuleGrpcRequest constructGrpcCreateRequest(
            Header header,
            Long userId,
            CreateModuleRequest request);

    @Mapping(target = "header", source = "header")
    @Mapping(target = "senderId", source = "userId")
    @Mapping(target = "courseId", source = "courseId")
    @Mapping(target = "moduleId", source = "moduleId")
    @Mapping(target = "moduleOrderNumber", ignore = true)
    public abstract DeleteModuleRequest constructGrpcDeleteRequest(
            Header header, Long userId, Long courseId,
            Long moduleId);

    @Mapping(target = "header", source = "header")
    @Mapping(target = "senderId", source = "userId")
    @Mapping(target = "courseId", source = "courseId")
    @Mapping(target = "moduleId", source = "moduleId")
    @Mapping(target = "moduleOrderNumber", source = "moduleOrderNum")
    public abstract DeleteModuleRequest constructGrpcDeleteRequest(
            Header header, Long userId, Long courseId,
            Integer moduleOrderNum, Long moduleId);

    public ModuleDto mapGrpcModuleResponseToModuleDto(ModuleResponse moduleResponse) {
        return adminModuleMapper.mapGrpcModuleResponseToModuleDto(moduleResponse);
    }

}
