package ru.ulstu.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.ulstu.datamodel.exception.ModelingException;
import ru.ulstu.datamodel.ts.TimeSeries;
import ru.ulstu.db.model.TimeSeriesMeta;
import ru.ulstu.db.model.TimeSeriesSet;
import ru.ulstu.service.UtilService;
import ru.ulstu.service.ValidationUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DbFileService implements DbService {
    @Value("${time-series.db-path}")
    private String timeSeriesDbPath;

    @Override
    public List<TimeSeriesSet> getSets() throws IOException {
        createDbIfNotExists();
        return Arrays.stream(Objects.requireNonNull(new File(timeSeriesDbPath).listFiles(File::isDirectory)))
                .map(TimeSeriesSet::new)
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public List<TimeSeriesMeta> getTimeSeriesMeta(TimeSeriesSet timeSeriesSet) throws IOException {
        validateTimeSeriesSet(timeSeriesSet);
        List<TimeSeriesMeta> list = new ArrayList<>();
        for (File file : getTimeSeriesMetaFiles(timeSeriesSet)) {
            list.add(new ObjectMapper()
                    .readValue(Paths.get(getSetPath(timeSeriesSet).getAbsolutePath(), file.getName())
                            .toFile(), TimeSeriesMeta.class));
        }
        return list.stream().sorted().collect(Collectors.toList());
    }

    @Override
    public TimeSeries getTimeSeries(TimeSeriesSet timeSeriesSet, String timeSeriesKey) throws IOException {
        validateDb(timeSeriesSet, timeSeriesKey);
        File timeSeriesFile = getTimeSeriesFile(timeSeriesSet, timeSeriesKey);
        BufferedReader csvReader = new BufferedReader(new FileReader(timeSeriesFile));
        String row;
        TimeSeries result = new TimeSeries(timeSeriesKey);
        while ((row = csvReader.readLine()) != null) {
            TimeSeries partOfTimeSeries = new UtilService().getTimeSeriesFromDateValueString(row);
            if (partOfTimeSeries.getLength() > 0) {
                result.getValues().addAll(partOfTimeSeries.getValues());
            }
        }
        csvReader.close();
        return result;
    }

    @Override
    public boolean addSet(String key) {
        TimeSeriesSet timeSeriesSet = new TimeSeriesSet(key);
        if (isTimeSeriesSetExists(timeSeriesSet)) {
            throw new RuntimeException(String.format("Time series set %s already exists", timeSeriesSet.getKey()));
        } else {
            return Paths.get(timeSeriesDbPath, timeSeriesSet.getKey()).toFile().mkdirs();
        }
    }

    @Override
    public void addTimeSeries(TimeSeriesSet timeSeriesSet, TimeSeries timeSeries) throws IOException, ModelingException {
        ValidationUtils.validateTimeSeries(timeSeries);
        if (!isTimeSeriesSetExists(timeSeriesSet)) {
            addSet(timeSeriesSet.getKey());
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(Paths.get(getSetPath(timeSeriesSet).getAbsolutePath(), timeSeries.getKey() + ".csv").toFile()));
        writer.write(new UtilService().getTimeSeriesToDateValueString(timeSeries));
        writer.close();
        createMetaFile(timeSeriesSet, timeSeries);
    }

    private void createMetaFile(TimeSeriesSet timeSeriesSet, TimeSeries timeSeries) throws IOException {
        TimeSeriesMeta timeSeriesMeta = new TimeSeriesMeta(timeSeries);
        new ObjectMapper()
                .writeValue(Paths.get(getSetPath(timeSeriesSet).getAbsolutePath(), timeSeries.getKey() + ".csv.meta")
                        .toFile(), timeSeriesMeta);
    }

    @Override
    public boolean deleteTimeSeries(TimeSeriesSet set, String timeSeriesKey) throws IOException {
        validateDb(set, timeSeriesKey);
        Files.delete(getTimeSeriesFile(set, timeSeriesKey).toPath());
        Files.delete(getTimeSeriesMetaFile(set, timeSeriesKey).toPath());
        return true;
    }

    @Override
    public boolean deleteTimeSeriesSet(TimeSeriesSet timeSeriesSet) {
        validateDb(timeSeriesSet);
        return deleteDirectory(getSetPath(timeSeriesSet));
    }

    private void validateDb(TimeSeriesSet timeSeriesSet, String timeSeriesKey) {
        validateDb(timeSeriesSet);
        validateTimeSeries(timeSeriesSet, timeSeriesKey);
    }

    private void validateDb(TimeSeriesSet timeSeriesSet) {
        validateTimeSeriesSet(timeSeriesSet);
    }

    private void createDbIfNotExists() throws IOException {
        File dbPath = new File(timeSeriesDbPath);
        if (!dbPath.exists()) {
            Files.createDirectory(dbPath.toPath());
        }
    }

    private void validateTimeSeriesSet(TimeSeriesSet timeSeriesSet) {
        if (!isTimeSeriesSetExists(timeSeriesSet)) {
            throw new RuntimeException(String.format("Time series set %s not exists", timeSeriesSet.getKey()));
        }
    }

    private void validateTimeSeries(TimeSeriesSet timeSeriesSet, String timeSeriesKey) {
        if (Arrays.stream(getSetPath(timeSeriesSet).listFiles(getFileFilter(timeSeriesKey, ".csv"))).findAny().isEmpty()) {
            throw new RuntimeException(String.format("Time series %s not exists", timeSeriesKey));
        }
    }

    private boolean isTimeSeriesSetExists(TimeSeriesSet timeSeriesSet) {
        return Arrays.stream(Objects.requireNonNull(new File(timeSeriesDbPath).listFiles(File::isDirectory)))
                .anyMatch(d -> d.getName().equals(timeSeriesSet.getKey()));
    }

    private List<File> getTimeSeriesMetaFiles(TimeSeriesSet timeSeriesSet) {
        return Arrays.asList(getSetPath(timeSeriesSet).listFiles(f -> f.getName().endsWith(".csv.meta")));
    }

    private File getTimeSeriesFile(TimeSeriesSet timeSeriesSet, String timeSeriesKey) {
        return Arrays.stream(getSetPath(timeSeriesSet).listFiles(getFileFilter(timeSeriesKey, ".csv")))
                .findAny()
                .orElseThrow(() -> new RuntimeException(String.format("Time series file not found: %s", timeSeriesKey)));
    }

    private File getTimeSeriesMetaFile(TimeSeriesSet timeSeriesSet, String timeSeriesKey) {
        return Arrays.stream(getSetPath(timeSeriesSet).listFiles(getFileFilter(timeSeriesKey, ".csv.meta")))
                .findAny()
                .orElseThrow(() -> new RuntimeException(String.format("Time series meta file not found: %s", timeSeriesKey)));
    }

    private File getSetPath(TimeSeriesSet timeSeriesSet) {
        return Paths.get(timeSeriesDbPath, timeSeriesSet.getKey()).toFile();
    }

    private FileFilter getFileFilter(String timeSeriesKey, String extension) {
        return f -> f.getName().equals(timeSeriesKey + extension);
    }

    private boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
}
