package com.yuhtin.quotes.sbswitcher.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author <a href="https://github.com/Yuhtin">Yuhtin</a>
 */
@AllArgsConstructor
@Getter
public class Board implements Comparable<Board> {

    private final int priority;
    private final String boardName;
    private final String permission;
    private final String regionName;

    @Override
    public int compareTo(Board o) {
        return Integer.compare(o.getPriority(), priority);
    }
}
