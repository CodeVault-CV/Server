package com.example.cv.oauth.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Embeddable
public class OauthId implements Serializable {
    private String id;
    private SocialOauth type;

    private OauthId(String id, SocialOauth type) {
        this.id = id;
        this.type = type;
    }

    public static OauthId of(String id, SocialOauth type) {
        return new OauthId(id, type);
    }
}
