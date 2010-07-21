/*
 * Copyright (C) 2009-2010 Mathias Doenitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.parboiled.matchers;

import org.jetbrains.annotations.NotNull;
import org.parboiled.MatcherContext;
import org.parboiled.Rule;

/**
 * A special {@link Matcher} not actually matching any input but rather trying its sub matcher against the current input
 * position. Succeeds if the sub matcher would fail.
 */
public class TestNotMatcher extends AbstractMatcher {

    public final Matcher subMatcher;

    public TestNotMatcher(@NotNull Rule subRule) {
        super(subRule);
        this.subMatcher = getChildren().get(0);
    }

    public boolean match(@NotNull MatcherContext context) {
        int lastIndex = context.getCurrentIndex();
        if (subMatcher.getSubContext(context).runMatcher()) {
            context.setCurrentIndex(lastIndex); // reset location, Test matchers never advance
            return false;
        }
        context.setCurrentIndex(lastIndex); // reset location, Test matchers never advance
        return true;
    }

    public <R> R accept(@NotNull MatcherVisitor<R> visitor) {
        return visitor.visit(this);
    }

}