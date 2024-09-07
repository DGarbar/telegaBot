package org.dharbar.telegabot.repository.util;

import lombok.Value;

import java.util.Map;
import java.util.Set;

@Value
public class ChangeResult<T> {

    Set<T> added;
    Map<T, T> present;
    Set<T> removed;

}
