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
    private Oauth type;

    private OauthId(String id, Oauth type) {
        this.id = id;
        this.type = type;
    }

    public static OauthId of(String id, Oauth type) {
        return new OauthId(id, type);
    }
}
