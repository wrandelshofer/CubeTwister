/*
 * @(#)SymbolTest.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik.notation;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SymbolTest {
    @Test
    public void testSymbolIsTree() {
        // GIVEN Any symbol.
        Map<Symbol, List<Symbol>> parentMap = new HashMap<>();

        for (Symbol parent : Symbol.values()) {
            for (Symbol child : parent.getSubSymbols()) {
                parentMap.computeIfAbsent(child, k -> new ArrayList<>()).add(parent);
            }
        }

        // THEN Should have exactly one parent.
        List<Map.Entry<Symbol, List<Symbol>>> multiParents = parentMap.entrySet().stream().filter(e -> e.getValue().size() > 1).collect(Collectors.toList());
        System.out.println("symbols with multiple parents:");
        multiParents.forEach(System.out::println);
        assertTrue(multiParents.isEmpty());

        // THEN Should have SCRIPT as root
        for (Symbol symbol : Symbol.values()) {
            Symbol root = symbol;
            while (parentMap.get(root) != null) {
                root = parentMap.get(root).get(0);
            }
            assertEquals(Symbol.SEQUENCE, root, symbol + " has bad root: " + root);
        }
    }
}