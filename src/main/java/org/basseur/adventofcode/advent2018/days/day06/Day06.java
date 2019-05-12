package org.basseur.adventofcode.advent2018.days.day06;

import org.basseur.adventofcode.advent2018.ProblemStatusEnum;
import org.basseur.adventofcode.advent2018.days.Days;
import org.basseur.adventofcode.advent2018.utils.FileReaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation for <i>Day 6: Chronal Coordinates</i>.
 *
 * @author Jan Philipp G&ouml;lz
 */
@Component
public class Day06 implements Days {

    /** The location of the puzzle input file */
    private static final String FILE_LOCATION = "src/main/java/org/basseur/adventofcode/advent2018/days/day06/Input.txt";
    /** The puzzle status {@code HashMap} */
    private final HashMap<String, ProblemStatusEnum> problemStatus;
    /** The list of coordinate Strings */
    private List<String> coordinateStringList;
    /** The list of VoronoiCells */
    private List<VoronoiCell> voronoiCellList = new ArrayList<>();

    /** Minimum of all X values */
    private int minX = Integer.MAX_VALUE;
    /** Maximum of all X values */
    private int maxX = Integer.MIN_VALUE;
    /** Minimum of all Y values */
    private int minY = Integer.MAX_VALUE;
    /** Maximum of all Y values */
    private int maxY = Integer.MIN_VALUE;
    /** Maximum total distance for the region containing all locations which have a total distance of less than maxTotalDistance */
    private int maxTotalDistance = 10000;

    /**
     * Constructor of Day06.
     * Causes the input file to be stored in coordinateStringList.
     *
     * @param fileReaders {@code @Autowired} fileReader
     */
    @Autowired
    Day06(FileReaders fileReaders) {
        this.problemStatus = new HashMap<>();
        this.problemStatus.put("1", ProblemStatusEnum.SOLVED);
        this.problemStatus.put("2", ProblemStatusEnum.SOLVED);

        this.coordinateStringList = fileReaders.readFileIntoStringList(FILE_LOCATION);

        generateVoronoiCells();
        setPlaneBoundries();
    }


    @Override
    public int getDay() {
        return 6;
    }

    @Override
    public HashMap<String, ProblemStatusEnum> getProblemStatus() {
        return problemStatus;
    }

    @Override
    public String firstPart() {
        return "Size of the largest area that isn't infinite: " + findSizeOfLargestVoronoiCell();
    }

    @Override
    public String secondPart() {
        return "Size of the region containing all locations which have a total distance to all given coordinates of less than " + maxTotalDistance + ": " + calculateSizeOfRegionOfLocations();
    }

    /**
     * Primary method for Day 6, Part 1.
     * <p>
     * Finds and returns the size of the Voronoi cell with the largest area. For this purpose the
     * plane is swept from the minimum to maximum X and Y values. The distance to the corresponding
     * coordinate is determined. If there is only one cell with a minimum distance, the coordinate
     * is added to that cell. If the coordinate is on the edge, that Voronoi cell is set to infinite
     * so it will later be excluded.
     *
     * @return size of the largest finite Voronoi cell
     */
    private int findSizeOfLargestVoronoiCell() {
        Coordinate sweepingCoordinate = new Coordinate(0, 0);

        for (int x = minX; x <= maxX; x++) {
            sweepingCoordinate.x = x;

            for (int y = minY; y <= maxY; y++) {
                sweepingCoordinate.y = y;
                int minimalDistance = Integer.MAX_VALUE;
                VoronoiCell closestVoronoiCell = null;

                for (VoronoiCell voronoiCell : voronoiCellList) {
                    int currentDistance = voronoiCell.distanceTo(sweepingCoordinate);

                    if (x == minX || x == maxX || y == minY || y == maxY) {
                        voronoiCell.isInfinite = true;
                    }

                    if (currentDistance == minimalDistance) {
                        closestVoronoiCell = null;
                    }

                    if (currentDistance < minimalDistance) {
                        minimalDistance = currentDistance;
                        closestVoronoiCell = voronoiCell;
                    }

                }

                if (closestVoronoiCell != null) {
                    closestVoronoiCell.addCoordinate(x, y);
                }
            }
        }

        int[] areaSizes = new int[voronoiCellList.size()];
        for (int i = 0; i < voronoiCellList.size(); i++) {
            areaSizes[i] = voronoiCellList.get(i).voronoiArea();
        }

        Arrays.sort(areaSizes);

        return areaSizes[areaSizes.length - 1];


    }

    /**
     * Primary method for Day 6, Part 2.
     * <p>
     * Calculates the size of the region containing all locations that have a total distance to all
     * given coordinates of less than {@link Day06#maxTotalDistance}. For this purpose the plane is
     * swept from the minimum to the maximum X and Y values. For each coordinate the sum of the distances
     * to all Voronoi seeds calculated. If the sum is smaller than {@link Day06#maxTotalDistance}, the
     * sizeOfRegionOfLocations is incremented and at the end returned.
     *
     * @return size of the region of coordinates with a maximum total distance to all Voronoi seeds smaller than {@link Day06#maxTotalDistance}
     */
    private int calculateSizeOfRegionOfLocations() {
        Coordinate sweepingCoordinate = new Coordinate(0, 0);
        int sizeOfRegionOfLocations = 0;

        for (int x = minX; x <= maxX; x++) {
            sweepingCoordinate.x = x;

            for (int y = minY; y <= maxY; y++) {
                sweepingCoordinate.y = y;
                int totalDistance = 0;

                for (VoronoiCell voronoiCell : voronoiCellList) {
                    totalDistance += voronoiCell.distanceTo(sweepingCoordinate);
                }
                if (totalDistance < maxTotalDistance) {
                    sizeOfRegionOfLocations++;
                }
            }

        }
        return sizeOfRegionOfLocations;
    }

    /**
     * Generates the Voronoi cells by parsing each coordinateString from {@link Day06#coordinateStringList}.
     */
    private void generateVoronoiCells() {
        for (String coordinateSting : coordinateStringList) {
            Matcher matcher = Pattern.compile("(\\d+), (\\d+)").matcher(coordinateSting);
            if (matcher.find()) {
                voronoiCellList.add(new VoronoiCell(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2))));
            }
        }
    }

    /**
     * Defines the minimum and maximum X and Y values for the plane containing the Voronoi cells.
     */
    private void setPlaneBoundries() {
        for (VoronoiCell voronoiCell : voronoiCellList) {
            minX = Math.min(voronoiCell.x, minX);
            maxX = Math.max(voronoiCell.x, maxX);
            minY = Math.min(voronoiCell.y, minY);
            maxY = Math.max(voronoiCell.y, maxY);
        }
    }
}
