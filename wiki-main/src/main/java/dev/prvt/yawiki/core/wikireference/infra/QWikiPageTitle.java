package dev.prvt.yawiki.core.wikireference.infra;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Expression;
import dev.prvt.yawiki.common.model.Namespace;
import dev.prvt.yawiki.common.model.WikiPageTitle;

public class QWikiPageTitle extends ConstructorExpression<WikiPageTitle> {

    public QWikiPageTitle(Expression<String> title, Expression<Namespace> namespace) {
        super(WikiPageTitle.class, new Class[]{String.class, Namespace.class}, new Expression[]{title, namespace});
    }

}
