package dev.prvt.yawiki.application.domain.wikipage;

import dev.prvt.yawiki.application.domain.wikipage.exception.EditValidationException;

/**
 * 문서 수정시 수정 토큰 체크, 리비전 체크, 수정자 체크
 * 문서 수정 검증 로직을 Document 클래스에 다 넣으면 책임이 비대해지는 문제가 생겨서 분리함.
 */
public interface DocumentEditValidator {
    void validate(Document document, Revision revision, String editToken) throws EditValidationException;
}
