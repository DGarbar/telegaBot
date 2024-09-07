package org.dharbar.telegabot.repository.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ChangeComparator {

    public static <T> ChangeResult<T> compare(Set<T> source, Set<T> target) {
        Set<T> added = new HashSet<>(target);  // Initially, everything in target
        Set<T> removed = new HashSet<>(source);  // Initially, everything in source
        Map<T, T> existingEntities = new HashMap<>();  // Map to store existing entities (source -> target)

        for (T sourceEntity : source) {
            if (target.contains(sourceEntity)) {
                target.stream()
                        .filter(e -> e.equals(sourceEntity))
                        .findFirst()
                        .ifPresent(matchedTarget -> existingEntities.put(sourceEntity, matchedTarget));
            }
        }

        added.removeAll(source);  // Remove all existing entities from added set
        removed.removeAll(target);  // Remove all existing entities from removed set
        return new ChangeResult<>(added, existingEntities, removed);
    }
}
