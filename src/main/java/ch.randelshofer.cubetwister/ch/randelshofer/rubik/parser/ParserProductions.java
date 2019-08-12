package ch.randelshofer.rubik.parser;

public enum ParserProductions {
    /* The symbols are ordered by precedence. */
    BEGIN,
    PERMUTATION_BEGIN,
    END,
    PERMUTATION_END,
    SINGLE,
    PREFIX_UNARY_OPERATOR,
    SUFFIX_UNARY_OPERATOR,
    PREFIX_BINARY_OPERATOR,
    SUFFIX_BINARY_OPERATOR,
    PREINFIX_BINARY_OPERATOR,
    POSTINFIX_BINARY_OPERATOR,
    FACE,
    DELIMITER,
    COMMENT_BEGIN,
    COMMENT_END,
    ;
}
