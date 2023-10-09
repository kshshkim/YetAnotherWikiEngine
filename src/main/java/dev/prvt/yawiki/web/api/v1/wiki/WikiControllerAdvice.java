package dev.prvt.yawiki.web.api.v1.wiki;

import dev.prvt.yawiki.core.permission.domain.PermissionEvaluationException;
import dev.prvt.yawiki.core.wikipage.domain.exception.NoSuchWikiPageException;
import dev.prvt.yawiki.core.wikipage.domain.exception.VersionCollisionException;
import dev.prvt.yawiki.web.api.v1.ErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice("dev.prvt.yawiki.web.api.v1.wiki")
public class WikiControllerAdvice {
//    private final SQLErrorCodeSQLExceptionTranslator sqlErrorCodeSQLExceptionTranslator;

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchWikiPageException.class)
    public ErrorMessage noSuchWikiPage(NoSuchWikiPageException e, HttpServletRequest request) {
        return new ErrorMessage(HttpStatus.NOT_FOUND, e.getMessage(), request.getRequestURI());
    }

//    @ExceptionHandler(WikiCreationException.class)
//    public ErrorMessage wikiCreationException(WikiCreationException e, HttpServletRequest request, HttpServletResponse response) {
//        DataIntegrityViolationException cause = (DataIntegrityViolationException) e.getCause();
//        Throwable rootCause = cause.getRootCause();
//        DataAccessException translate = sqlErrorCodeSQLExceptionTranslator.translate(request.getContextPath(), null, (SQLException) rootCause);
//        if (translate instanceof DuplicateKeyException) {
//            response.setStatus(HttpStatus.BAD_REQUEST.value());
//            return new ErrorMessage(HttpStatus.BAD_REQUEST, "duplicate title", request.getRequestURI());
//        } else {
//            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
//            return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, null, request.getRequestURI());
//        }
//    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(VersionCollisionException.class)
    public ErrorMessage versionCollisionException(VersionCollisionException e, HttpServletRequest request) {
        return new ErrorMessage(HttpStatus.CONFLICT, "versionToken mismatch: " + e.getMessage(), request.getRequestURI());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(PermissionEvaluationException.class)
    public ErrorMessage permissionEvaluationException(PermissionEvaluationException e, HttpServletRequest request) {
        return new ErrorMessage(HttpStatus.FORBIDDEN, e.getMessage(), request.getRequestURI());
    }
}
