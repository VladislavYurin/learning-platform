package ru.mentor.exception;

import feign.Response;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.mentor.config.CommonFeignConfig.CustomErrorDecoder.FeignClientExceptionWithResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FeignClientExceptionWithResponse.class)
    public ResponseEntity<String> handleFeignClientExceptionWithResponse(
            FeignClientExceptionWithResponse ex) throws IOException {

        Response response = ex.getResponse();
        String body = response.body() == null ? "" :
                StreamUtils.copyToString(
                        response.body().asInputStream(),
                        StandardCharsets.UTF_8
                );

        return ResponseEntity.status(response.status())
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(body);
    }

}
