// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.apache.doris.nereids.rules.expression;

import org.apache.doris.nereids.pattern.TypeMappings.TypeMapping;
import org.apache.doris.nereids.trees.expressions.Expression;

import java.util.List;
import java.util.function.Predicate;

/** ExpressionPatternMatcherRule */
public class ExpressionPatternMatchRule implements TypeMapping<Expression> {
    public final ExpressionRuleType expressionRuleType;
    public final Class<? extends Expression> typePattern;
    public final List<Predicate<ExpressionMatchingContext<Expression>>> predicates;
    public final ExpressionMatchingAction<Expression> matchingAction;

    public ExpressionPatternMatchRule(ExpressionPatternMatcher patternMatcher) {
        this.expressionRuleType = patternMatcher.expressionRuleType;
        this.typePattern = patternMatcher.typePattern;
        this.predicates = patternMatcher.predicates;
        this.matchingAction = patternMatcher.matchingAction;
    }

    /** matches */
    public boolean matchesTypeAndPredicates(ExpressionMatchingContext<Expression> context) {
        return typePattern.isInstance(context.expr) && matchesPredicates(context);
    }

    /** matchesPredicates */
    public boolean matchesPredicates(ExpressionMatchingContext<Expression> context) {
        if (!predicates.isEmpty()) {
            for (Predicate<ExpressionMatchingContext<Expression>> predicate : predicates) {
                if (!predicate.test(context)) {
                    return false;
                }
            }
        }
        return true;
    }

    public Expression apply(ExpressionMatchingContext<Expression> context) {
        Expression newResult = matchingAction.apply(context);
        return newResult == null ? context.expr : newResult;
    }

    @Override
    public Class<? extends Expression> getType() {
        return typePattern;
    }

    public ExpressionRuleType getExpressionRuleType() {
        return expressionRuleType;
    }
}
