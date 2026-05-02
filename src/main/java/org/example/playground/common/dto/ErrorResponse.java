package org.example.playground.common.dto;

import lombok.Builder;

/**
 *
 * @param message
 *
 * -record 기본적으로 필드가 final
 */
@Builder
public record ErrorResponse(String message) {

    public static ErrorResponse from(String message) {
        return new ErrorResponse(message);
    }
}
