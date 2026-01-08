package gr.hua.fitTrack.web.rest.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @see gr.hua.fitTrack.web.rest.AuthResource
 **/

public record TokenRequest(
        @NotNull @NotBlank String clientId,
        @NotNull @NotBlank String clientSecret
) {

}
