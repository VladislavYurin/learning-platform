package ru.mentor.grpc.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GrpcErrorText {

    public static final String EMPTY_REQUEST = "Empty request";

}
