package model;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import static model.GameStatusEnum.NON_STARTED;
import static model.GameStatusEnum.INCOMPLETE;
import static model.GameStatusEnum.COMPLETE;

public class Board {
    
    private final List<List<Space>> spaces;

    public Board(final List<List<Space>> spaces) {
        this.spaces = spaces;
    }

    public List<List<Space>> getSpaces() {
        return spaces;
    }

    public GameStatusEnum getStatus() {
    Stream<Space> allSpaces = spaces.stream().flatMap(Collection::stream);

    List<Space> spaceList = allSpaces.toList();

    boolean nonStartedGame = spaceList.stream()
        .noneMatch(s -> !s.isFixed() && nonNull(s.getActual()));

    if (nonStartedGame) {
        return NON_STARTED;
    }

    boolean incompleteGame = spaceList.stream()
        .anyMatch(s -> isNull(s.getActual()));

    if (incompleteGame) {
        return INCOMPLETE;
    }

    return COMPLETE;
    }

    public boolean hasErrors() {
        if(getStatus() == NON_STARTED) {
            return false;
        }

        Stream<Space> allSpaces = spaces.stream().flatMap(Collection::stream);

        boolean errorSpaces = allSpaces.anyMatch(s -> 
        nonNull(s.getActual()) && !s.getActual().equals(s.getExpected()));

        return errorSpaces;
    }

    public boolean changeValue(final int column, final int row, final Integer value) {
        var space = spaces.get(column).get(row);

        if(space.isFixed()) {
            return false;
        }

        space.setActual(value);
        return true;
    }

    public boolean clearSpace(final int column, final int row) {
        var space = spaces.get(column).get(row);

        if(space.isFixed()) {
            return false;
        }

        space.clearSpace();
        return true;
    }

    public void reset() {
        spaces.forEach(column -> column.forEach(Space::clearSpace));
    }

    public boolean gameIsFinished() {
        boolean isFinished = !hasErrors() && getStatus().equals(COMPLETE);
        
        return isFinished;
    }
}
