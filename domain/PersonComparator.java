package domain;
import java.util.*;

public class PersonComparator implements java.util.Comparator<Person> {
    @Override
    public int compare(Person a, Person b) {
        return b.getMatches() - a.getMatches();
    }
}