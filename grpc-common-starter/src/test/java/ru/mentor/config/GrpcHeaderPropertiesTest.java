package ru.mentor.config;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.mentor.testUtil.TestConstantHolder;

class GrpcHeaderPropertiesTest {

    private static final String NODE_ID_FIELD = "nodeId";
    private static final String API_KEY_FIELD = "apiKey";
    private static Validator validator;
    private static final String NO_VIOLATIONS_MSG = "Expected no constraint violations";
    private static final String BLANK_NODE_ID_VIOLATIONS_MSG = "Expected violations for blank nodeId";
    private static final String NODE_ID_VIOLATION_MSG = "Expected violation on nodeId";
    private static final String BLANK_API_KEY_VIOLATIONS_MSG = "Expected violations for blank apiKey";
    private static final String API_KEY_VIOLATION_MSG = "Expected violation on apiKey";

    private GrpcHeaderProperties validProps() {
        GrpcHeaderProperties props = new GrpcHeaderProperties();
        props.setNodeId(TestConstantHolder.NODE_ID);
        props.setApiKey(TestConstantHolder.API_KEY);
        return props;
    }

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_allFieldsValid_noViolations() {
        GrpcHeaderProperties props = validProps();
        Set<ConstraintViolation<GrpcHeaderProperties>> violations = validator.validate(props);
        Assertions.assertTrue(violations.isEmpty(), NO_VIOLATIONS_MSG);
    }

    @Test
    void validate_blankNodeId_violationOnNodeId() {
        GrpcHeaderProperties props = validProps();
        props.setNodeId(TestConstantHolder.BLANK);
        Set<ConstraintViolation<GrpcHeaderProperties>> violations = validator.validate(props);
        Assertions.assertFalse(violations.isEmpty(), BLANK_NODE_ID_VIOLATIONS_MSG);
        Assertions.assertTrue(
                violations.stream()
                          .anyMatch(v -> NODE_ID_FIELD.equals(v.getPropertyPath().toString())),
                NODE_ID_VIOLATION_MSG
        );
    }

    @Test
    void validate_blankApiKey_violationOnApiKey() {
        GrpcHeaderProperties props = validProps();
        props.setApiKey(TestConstantHolder.BLANK);
        Set<ConstraintViolation<GrpcHeaderProperties>> violations = validator.validate(props);
        Assertions.assertFalse(violations.isEmpty(), BLANK_API_KEY_VIOLATIONS_MSG);
        Assertions.assertTrue(
                violations.stream()
                          .anyMatch(v -> API_KEY_FIELD.equals(v.getPropertyPath().toString())),
                API_KEY_VIOLATION_MSG
        );
    }

}
