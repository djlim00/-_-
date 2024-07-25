package com.kuit3.rematicserver.dto.bulletin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BulletinDto {
    private long bulletinId;
    private String name;
    private String genre;
    private String originCategory;
    private String category;
    private String thumnailImageUrl;
    private String status;
    private String nameGroup;
}