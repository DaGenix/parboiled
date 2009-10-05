package org.parboiled;

public interface Rule {

    /**
     * Attaches a label to this Rule.
     *
     * @param label the label
     * @return this Rule
     */
    Rule label(String label);

    /**
     * Marks the created Matcher as "enforced", meaning that the Matcher creates a parsing error
     * in case an attempted match fails.
     *
     * @return this Rule
     */
    Rule enforce();

    /**
     * Create a Matcher for this rule.
     *
     * @return the Matcher for this rule
     */
    Matcher toMatcher();

}
