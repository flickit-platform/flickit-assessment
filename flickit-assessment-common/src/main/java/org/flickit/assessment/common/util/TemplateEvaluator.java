package org.flickit.assessment.common.util;

import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TemplateEvaluator {

    private final ExpressionParser parser = new SpelExpressionParser();
    private final TemplateParserContext templateContext = new TemplateParserContext("{{", "}}");

    public String evaluate(String template, Map<String, Object> variables) {
        EvaluationContext context = new StandardEvaluationContext(variables);
        context.getPropertyAccessors().add(new MapAccessor());
        Expression exp = parser.parseExpression(template, templateContext);
        return exp.getValue(context, String.class);
    }
}
