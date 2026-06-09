package com.team7.hboict.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Request body voor het aanmaken van een forum comment.
 * De author wordt server-side ingevuld op basis van het JWT.
 */
@Schema(description = "Request body voor het aanmaken van een forum comment")
public class CreateCommentRequest {

    @Schema(
            description = "Optionele author voor backwards compatibility. Wordt normaal genegeerd omdat JWT wordt gebruikt.",
            example = "student@example.com"
    )
    private String author;

    @NotBlank
    @Schema(
            description = "Tekst van de comment",
            example = "Ik vind dit een interessante post."
    )
    private String description;

    public CreateCommentRequest() {
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}