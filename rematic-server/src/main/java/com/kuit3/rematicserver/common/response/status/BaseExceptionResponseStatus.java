package com.kuit3.rematicserver.common.response.status;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum BaseExceptionResponseStatus implements ResponseStatus {

    /**
     * 1000: 요청 성공 (OK)
     */
    SUCCESS(1000, HttpStatus.OK.value(), "요청에 성공하였습니다."),

    /**
     * 2000: Request 오류 (BAD_REQUEST)
     */
    BAD_REQUEST(2000, HttpStatus.BAD_REQUEST.value(), "유효하지 않은 요청입니다."),
    URL_NOT_FOUND(2001, HttpStatus.BAD_REQUEST.value(), "유효하지 않은 URL 입니다."),
    METHOD_NOT_ALLOWED(2002, HttpStatus.METHOD_NOT_ALLOWED.value(), "해당 URL에서는 지원하지 않는 HTTP Method 입니다."),
    MISSING_PARAM(2003, HttpStatus.BAD_REQUEST.value(), "필요한 파라미터가 누락되었습니다"),
    EMPTY_IMAGE_FILE(2004, HttpStatus.BAD_REQUEST.value(), "이미지 파일이 비어있습니다."),
    UNSUPPORTED_FILE_EXTENSION(2005, HttpStatus.BAD_REQUEST.value(), "지원되지 않는 이미지 확장자입니다."),
    PARENT_COMMENT_NOT_EXISTS(2006, HttpStatus.NOT_FOUND.value(), "부모 댓글이 존재하지 않습니다."),
    IMAGE_ALREADY_EXISTS(2007, HttpStatus.BAD_REQUEST.value(), "댓글 첨부 이미지가 이미 존재합니다."),
    USER_COMMENT_MISMATCH(2008, HttpStatus.BAD_REQUEST.value(), "작성자가 아닌 사용자는 접근 불가합니다."),
    POST_NOT_FOUND(2009, HttpStatus.BAD_REQUEST.value(), "존재하지 않는 게시물입니다."),
    FILE_LIMIT_EXCEEDED(2010, HttpStatus.BAD_REQUEST.value(), "업로드할 수 있는 파일 수 제한을 초과했습니다."),
    WRONG_COMMENT_REGISTER(2011, HttpStatus.BAD_REQUEST.value(), "댓글 등록에 오류가 발생했습니다."),
    INVALID_PARAM(2012, HttpStatus.BAD_REQUEST.value(), "유효하지 않은 파라미터 값이 전달되었습니다."),

    /**
     * 3000: Server, Database 오류 (INTERNAL_SERVER_ERROR)
     */
    SERVER_ERROR(3000, HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에서 오류가 발생하였습니다."),
    DATABASE_ERROR(3001, HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스에서 오류가 발생하였습니다."),
    BAD_SQL_GRAMMAR(3002, HttpStatus.INTERNAL_SERVER_ERROR.value(), "SQL에 오류가 있습니다."),

    S3_UPLOADING_FAILED(3003, HttpStatus.INTERNAL_SERVER_ERROR.value(), "이미지 저장에 실패했습니다."),

    /**
     * 4000: Authorization 오류
     */
    JWT_ERROR(4000, HttpStatus.UNAUTHORIZED.value(), "JWT에서 오류가 발생하였습니다."),
    TOKEN_NOT_FOUND(4001, HttpStatus.BAD_REQUEST.value(), "토큰이 HTTP Header에 없습니다."),
    UNSUPPORTED_TOKEN_TYPE(4002, HttpStatus.BAD_REQUEST.value(), "지원되지 않는 토큰 형식입니다."),
    INVALID_TOKEN(4003, HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 토큰입니다."),
    MALFORMED_TOKEN(4004, HttpStatus.UNAUTHORIZED.value(), "토큰이 올바르게 구성되지 않았습니다."),
    EXPIRED_TOKEN(4005, HttpStatus.UNAUTHORIZED.value(), "만료된 토큰입니다."),
    TOKEN_MISMATCH(4006, HttpStatus.UNAUTHORIZED.value(), "로그인 정보가 토큰 정보와 일치하지 않습니다."),
    OAUTH_API_REQUEST_FAILED(4007, HttpStatus.INTERNAL_SERVER_ERROR.value(), "OAuth 서버에 요청하는 데 실패했습니다." ),

    /**
     * 5000: User 오류
     */
    INVALID_USER_VALUE(5000, HttpStatus.BAD_REQUEST.value(), "회원가입 요청에서 잘못된 값이 존재합니다."),
    DUPLICATE_EMAIL(5001, HttpStatus.BAD_REQUEST.value(), "이미 존재하는 이메일입니다."),
    DUPLICATE_NICKNAME(5002, HttpStatus.BAD_REQUEST.value(), "이미 존재하는 닉네임입니다."),
    USER_NOT_FOUND(5003, HttpStatus.BAD_REQUEST.value(), "존재하지 않는 계정입니다."),
    UNAUTHORIZED_USER_REQUEST(5004, HttpStatus.BAD_REQUEST.value(), "권한이 없는 사용자의 요청입니다."),
    USER_KEYWORD_NOT_FOUND(5005, HttpStatus.BAD_REQUEST.value(), "요청하신 사용자의 최근 검색어가 없습니다."),
    USER_DORMANT_STATUS(5006, HttpStatus.BAD_REQUEST.value(), "휴면상태 계정입니다."),
    COMMENT_NOT_FOUND(5007, HttpStatus.BAD_REQUEST.value(), "요청하신 댓글이 존재하지 않습니다."),

    /**
     * 6000: UserScrap 오류
     */
    DUPLICATE_USER_SCRAP(6000, HttpStatus.CONFLICT.value(), "이미 스크랩한 게시물입니다."),
    USER_SCRAP_NOT_FOUND(6001, HttpStatus.BAD_REQUEST.value(), "스크랩을 찾을 수 없습니다."),
    INVALID_SCRAP_USERID(6002, HttpStatus.BAD_REQUEST.value(), "자신이 작성한 게시물은 스크랩할 수 없습니다."),
    /**
     * 7000: 처벌 관련 오류
     */
    UNCATEGORIZED_PUNISHMENT(7000, HttpStatus.BAD_REQUEST.value(), "알 수 없는 처벌 및 신고 유형입니다.");

    private final int code;
    private final int status;
    private final String message;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
