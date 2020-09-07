package org.haffson.adventofcode.days.day02;

import org.haffson.adventofcode.ProblemStatusEnum;
import org.haffson.adventofcode.days.Days;
import org.haffson.adventofcode.utils.FileReaders;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

public class Day02 implements Days {

    private final HashMap<String, ProblemStatusEnum> problemStatus;

    @Autowired
    Day02() {
        this.problemStatus = new HashMap<>();
        this.problemStatus.put("1", ProblemStatusEnum.UNDEFINED);
        this.problemStatus.put("2", ProblemStatusEnum.UNSOLVED);
    }

    @Override
    public String firstPart() {
        return null;
    }

    @Override
    public String secondPart() {
        return null;
    }

    @Override
    public int getDay() {
        return 2;
    }

    @Override
    public HashMap<String, ProblemStatusEnum> getProblemStatus() {
        return problemStatus;
    }
}
