package ru.mentor.utils;

import ru.mentor.dto.front.CreateCourseRequest;
import ru.mentor.dto.front.CreateModuleRequest;
import ru.mentor.testUtil.TestConstantHolder;

public class TestDataGenerator {
    public static CreateModuleRequest constructCreateModuleRequest(){
        return CreateModuleRequest.builder()
                                  .moduleTitle(TestConstantHolder.moduleTitle)
                                  .moduleContentDescription(TestConstantHolder.moduleContent)
                                  .moduleOrderNumber(TestConstantHolder.moduleOrderNumber)
                                  .build();
    }

    public static CreateCourseRequest constructCreateCourseRequest(){
        return CreateCourseRequest.builder()
                                  .courseName(TestConstantHolder.courseTitle)
                                  .courseDescription(TestConstantHolder.courseDescription)
                                  .build();
    }
}
