package ru.ulstu.event.service;


import org.springframework.stereotype.Service;
import ru.ulstu.event.model.Event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DataPreprocessor {

    public Map<String, Integer> createEventVocabulary(List<String> eventTypes) {
        Map<String, Integer> vocabulary = new HashMap<>();
        int index = 0;
        for (String eventType : eventTypes) {
            vocabulary.put(eventType, index++);
        }
        return vocabulary;
    }

    public List<Integer> convertEventsToSequence(List<Event> events,
                                                 Map<String, Integer> vocabulary) {
        return events.stream()
                .map(event -> vocabulary.get(event.getEventType()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public double[][] createTrainingData(List<Integer> eventSequence, int sequenceLength) {
        int totalSequences = eventSequence.size() - sequenceLength;
        double[][] trainingData = new double[totalSequences][sequenceLength + 1];

        for (int i = 0; i < totalSequences; i++) {
            for (int j = 0; j < sequenceLength; j++) {
                trainingData[i][j] = eventSequence.get(i + j);
            }
            // Target - следующее событие
            trainingData[i][sequenceLength] = eventSequence.get(i + sequenceLength);
        }

        return trainingData;
    }

    public Map<Integer, String> createReverseVocabulary(Map<String, Integer> vocabulary) {
        return vocabulary.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }
}