package ru.ulstu.event.service;


import org.springframework.stereotype.Service;
import ru.ulstu.event.model.ForecastResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MarkovChainPredictor {
    private Map<Integer, Map<Integer, Double>> transitionMatrix;
    private Map<Integer, String> reverseVocabulary;

    public void train(List<Integer> eventSequence, int vocabularySize) {
        transitionMatrix = new HashMap<>();

        // Инициализация матрицы переходов
        for (int i = 0; i < vocabularySize; i++) {
            Map<Integer, Double> transitions = new HashMap<>();
            for (int j = 0; j < vocabularySize; j++) {
                transitions.put(j, 0.0);
            }
            transitionMatrix.put(i, transitions);
        }

        // Подсчет переходов
        for (int i = 0; i < eventSequence.size() - 1; i++) {
            int current = eventSequence.get(i);
            int next = eventSequence.get(i + 1);

            Map<Integer, Double> transitions = transitionMatrix.get(current);
            transitions.put(next, transitions.get(next) + 1.0);
        }

        // Нормализация вероятностей
        for (Map<Integer, Double> transitions : transitionMatrix.values()) {
            double total = transitions.values().stream().mapToDouble(Double::doubleValue).sum();
            if (total > 0) {
                for (Integer key : transitions.keySet()) {
                    transitions.put(key, transitions.get(key) / total);
                }
            }
        }
    }

    public void setReverseVocabulary(Map<Integer, String> reverseVocabulary) {
        this.reverseVocabulary = reverseVocabulary;
    }

    public ForecastResult predictNextEvent(int lastEvent) {
        if (!transitionMatrix.containsKey(lastEvent)) {
            return null;
        }

        Map<Integer, Double> probabilities = transitionMatrix.get(lastEvent);

        // Находим событие с максимальной вероятностью
        Map.Entry<Integer, Double> maxEntry = null;
        for (Map.Entry<Integer, Double> entry : probabilities.entrySet()) {
            if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
                maxEntry = entry;
            }
        }

        if (maxEntry != null && maxEntry.getValue() > 0) {
            String predictedEvent = reverseVocabulary.get(maxEntry.getKey());
            return new ForecastResult(predictedEvent, maxEntry.getValue());
        }

        return null;
    }
}