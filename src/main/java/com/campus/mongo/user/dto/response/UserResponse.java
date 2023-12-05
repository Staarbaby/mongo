package com.campus.mongo.user.dto.response;

import com.campus.mongo.user.entity.UserDoc;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
@Builder
public class UserResponse {
    private String id;
    private String firstName;
    private String lastName;
    public static UserResponse of(UserDoc userDoc){
        return UserResponse.builder()
                .id(userDoc.getId().toString())
                .firstName(userDoc.getFirstName())
                .lastName(userDoc.getLastName())
                .build();
    }
}
