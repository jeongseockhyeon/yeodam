package com.hifive.yeodam.global.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UploadAllowImageTypeDefine {
    JPG("jpg", "image/jpeg"),
    JPEG("jpeg", "image/jpeg"),
    PNG("png", "image/png"),
    ;


    private final String fileExtensionLowerCase;
    private final String fileMimeType;
}
